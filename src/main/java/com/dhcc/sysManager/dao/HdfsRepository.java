package com.dhcc.sysManager.dao;

import java.io.File;
import java.io.IOException;

import com.dhcc.sysManager.common.HdfsException;
import com.dhcc.sysManager.common.LoginUtil;
import com.dhcc.sysManager.common.Util;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class HdfsRepository {
    public static long lastQueryTime = 0;

    //public static long totalSize = 0;
    public static HashMap<String, Long> sizeMap = new HashMap<>();

    private static final Logger LOG = Logger.getLogger(HdfsRepository.class);

    private static final String STORAGE_POLICY_HOT = "HOT";

    private static String userdir = System.getProperty("catalina.home") + File.separator + "conf" + File.separator;

    /*
    private static String PATH_TO_HDFS_SITE_XML = HdfsOperation.class.getClassLoader().getResource("hdfs-site.xml")
            .getPath();
    private static String PATH_TO_CORE_SITE_XML = HdfsOperation.class.getClassLoader().getResource("core-site.xml")
            .getPath();
     */
    private static String PATH_TO_HDFS_SITE_XML = userdir + "hdfs-site.xml";
    private static String PATH_TO_CORE_SITE_XML = userdir + "core-site.xml";

    private static Configuration conf = null;

    private static String PRNCIPAL_NAME = "dev";
    /*
    private static String PATH_TO_KEYTAB = HdfsOperation.class.getClassLoader().getResource("user.keytab").getPath();
    private static String PATH_TO_KRB5_CONF = HdfsOperation.class.getClassLoader().getResource("krb5.conf").getPath();
     */
    private static String PATH_TO_KEYTAB = userdir + "user.keytab";
    private static String PATH_TO_KRB5_CONF = userdir + "krb5.conf";

    //private static String PATH_TO_SMALL_SITE_XML = HdfsExample.class.getClassLoader().getResource("smallfs-site.xml")
    //.getPath();

    private FileSystem fSystem; /* HDFS file system */
    //private String DEST_PATH;
    //private String FILE_NAME;

    private static long lastLoginTime = 0L;

    public HdfsRepository() throws IOException {
        confLoad();
        authentication();
        instanceBuild();
        lastLoginTime = System.currentTimeMillis();
    }

    /**
     * build HDFS instance
     */
    private void instanceBuild() throws IOException {
        // get filesystem
        // 一般情况下，FileSystem对象JVM里唯一，是线程安全的，这个实例可以一直用，不需要立马close。
        // 注意：
        // 若需要长期占用一个FileSystem对象的场景，可以给这个线程专门new一个FileSystem对象，但要注意资源管理，别导致泄露。
        // 在此之前，需要先给conf加上：
        // conf.setBoolean("fs.hdfs.impl.disable.cache",
        // true);//表示重新new一个连接实例，不用缓存中的对象。
        fSystem = FileSystem.get(conf);
    }

    /**
     *
     * Add configuration file if the application run on the linux ,then need
     * make the path of the core-site.xml and hdfs-site.xml to in the linux
     * client file
     *
     */
    private static void confLoad() throws IOException {
        System.setProperty("java.security.krb5.conf", PATH_TO_KRB5_CONF);
        conf = new Configuration();
        // conf file
        conf.addResource(new Path(PATH_TO_HDFS_SITE_XML));
        conf.addResource(new Path(PATH_TO_CORE_SITE_XML));
        // conf.addResource(new Path(PATH_TO_SMALL_SITE_XML));
    }

    /**
     * kerberos security authentication if the application running on Linux,need
     * the path of the krb5.conf and keytab to edit to absolute path in Linux.
     * make the keytab and principal in example to current user's keytab and
     * username
     *
     */
    private static void authentication() throws IOException {
        // security mode
        if ("kerberos".equalsIgnoreCase(conf.get("hadoop.security.authentication"))) {
            System.setProperty("java.security.krb5.conf", PATH_TO_KRB5_CONF);
            LoginUtil.login(PRNCIPAL_NAME, PATH_TO_KEYTAB, PATH_TO_KRB5_CONF, conf);
        }
    }

    private void getTotalHTMLSize(String destPath) throws HdfsException {
        Path path = new Path(destPath);
        try {
            //fSystem.isDirectory(path);
            /*
            FileStatus[] fileStatuses = fSystem.listStatus(path);
            for (FileStatus fileStatus : fileStatuses) {
                boolean isDir = fileStatus.isDirectory();
                String fullPath = fileStatus.getPath().toString();
                System.out.println("isDir:" + isDir + ",Path:" + fullPath);
            }*/
            RemoteIterator<LocatedFileStatus> iterator = fSystem.listFiles(path, true);
            long total = 0;
            while (iterator.hasNext()) {
                LocatedFileStatus fileStatus = iterator.next();
                //long tmp = fileStatus.getLen();
                total = total + fileStatus.getLen();
                //System.out.println(fileStatus.getPath().getName() + "\t" + tmp);
            }
            //totalSize = total;
            sizeMap.put(destPath, total);
            LOG.info("Total size: " + total);
        } catch (IOException e) {
            throw new HdfsException("Get HDFS path size failed.");
        }
    }

    public String getHTMLSize(String path) throws HdfsException, IOException {
        long nowTime = System.currentTimeMillis();

        if ((nowTime - lastLoginTime) > (1000*60*60*2)) {
            confLoad();
            authentication();
            instanceBuild();
            lastLoginTime = nowTime;
        }

        if ((sizeMap.get(path) == null) || ((nowTime - lastQueryTime) > (1000*60*5))) {
            // 完成初始化和认证
            getTotalHTMLSize(path);
            lastQueryTime = nowTime;
            //System.out.println("Get size from HDFS.");
            LOG.info("Get size from HDFS.");
        } else {
            //System.out.println("Get size from memory.");
            LOG.info("Get size from memory.");
        }
        return Util.sizePretty(sizeMap.get(path));
    }
}