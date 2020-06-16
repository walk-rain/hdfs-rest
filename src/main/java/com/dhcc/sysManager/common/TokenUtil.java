package com.dhcc.sysManager.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dhcc.sysManager.common.config.SSLSocketFactoryConfig;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TokenUtil {
    public static Object  doTokenValidation(String url, Map<String, Object> params) throws Exception{
        HttpClient client = null;
        //原http方式
//		CloseableHttpClient client = null;
        BasicHeader AcceptEncoding = new BasicHeader("Accept-Encoding", "gzip, deflate");
        BasicHeader Accept = new BasicHeader("Accept", "application/json, text/javascript, */*; q=0.01");
        BasicHeader XRequestedWit = new BasicHeader("X-Requested-With", "XMLHttpRequest");
        //配置需要从配置文件取（不需要自己生成，统一提供）
        BasicHeader Authorization = new BasicHeader("Authorization", "Basic eFVGM2NrSUxVNFdjeHNqb0Vo"
                + "dlJEN1hia1A2b0JabzZuRjlvMERRRmwzczpHRkdnY1hBb0xfWmRMZ1NfMUtjVlNEVmpDNG5YYzI1SjNTO"
                + "HBXT2U1QXZmc2U3MU5pa3YtaWFlcjlIRmJiSlFYOHRBS0lmMjVSeE5vbDlPM0tjX1E2dwo=");
//		RequestConfig reqConfig = RequestConfig.custom().setSocketTimeout(60000).setConnectTimeout(60000).build();
        HttpPost post = new HttpPost(url);
//		post.setConfig(reqConfig);
        post.setHeaders(new Header[] { AcceptEncoding, Accept, XRequestedWit,Authorization});
        String result="";
        JSONObject jsonObject=null;
        try {
            //这块我用的是DefaultHttpClient来配置的ssl,CloseableHttpClient的方式配置ssl我没试过
            client = new SSLSocketFactoryConfig();
            //原http方式
//			client = HttpClients.createDefault();
            StringEntity s = new UrlEncodedFormEntity(createParam(params), Consts.UTF_8);
            post.setEntity(s);
            HttpResponse res = client.execute(post);
            StatusLine status =  res.getStatusLine();
            int state = status.getStatusCode();
            if (state == 200) {
                HttpEntity responseEntity = res.getEntity();
                result = EntityUtils.toString(responseEntity,"UTF-8");
                jsonObject = JSONObject.parseObject(result);
            }
        } catch (Exception e) {
            System.out.println(result);
            e.printStackTrace();
        }finally {
            if(client!=null){
                client.getConnectionManager().shutdown();
            }
        }
        return jsonObject;
    }

    private static List<NameValuePair> createParam(Map<String, Object> param) {
        //建立一个NameValuePair数组，用于存储欲传送的参数
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        if(param != null) {
            for(String k : param.keySet()) {
                nvps.add(new BasicNameValuePair(k, param.get(k).toString()));
            }
        }
        return nvps;
    }

    public static String TaskResponse(String ResultCode,String ResultContent){
        Map<String, Map<String, String>> responseData=new HashMap<>();
        Map<String, String> responseMap=new HashMap<>();
        responseMap.put("ResultCode", ResultCode);
        responseMap.put("ResultContent", ResultContent);
        responseData.put("data", responseMap);
        return JSON.toJSONString(responseData);
    }
}
