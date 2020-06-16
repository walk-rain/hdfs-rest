package com.dhcc.sysManager.service.impl;

import com.dhcc.sysManager.common.HdfsException;
import com.dhcc.sysManager.entity.msg.GetHBaseTableSizeRequest;
import com.dhcc.sysManager.entity.msg.GetPathTotalSizeResponse;
import com.dhcc.sysManager.dao.HdfsRepository;
import com.dhcc.sysManager.service.HdfsService;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service("HdfsService")
public class HdfsServiceImpl implements HdfsService {

    private HdfsRepository repository;

    public HdfsServiceImpl(HdfsRepository repository) {
        this.repository = repository;
    }

    @Override
    public GetPathTotalSizeResponse getHtmlSize() {
        GetPathTotalSizeResponse response = new GetPathTotalSizeResponse();
        try {
            String size = repository.getHTMLSize("/hbase/data/source_db");
            response.setRetCode("0");
            response.setRetMsg("Get size success.");
            response.setTotalSize(size);
        } catch (HdfsException e) {
            response.setRetCode("-1");
            response.setRetMsg(e.getMessage());
            response.setTotalSize("56.0GB");
        } catch (IOException e) {
            response.setRetCode("-1");
            response.setRetMsg("Build instance failed.");
            response.setTotalSize("56.0GB");
        }
        return response;
    }

    @Override
    public GetPathTotalSizeResponse getHBaseTableSize(GetHBaseTableSizeRequest request) {
        GetPathTotalSizeResponse response = new GetPathTotalSizeResponse();
        String path = "";
        if ((request.getTableName()==null)||(request.getTableName().isEmpty())) {
            path = "/hbase/data/" + request.getNameSpace();
        }
        else {
            path = "/hbase/data/" + request.getNameSpace() + "/" + request.getTableName();
        }
        try {
            String size = repository.getHTMLSize(path);
            response.setRetCode("0");
            response.setRetMsg("Get table size success.");
            response.setTotalSize(size);
        } catch (HdfsException e) {
            response.setRetCode("-1");
            response.setRetMsg(e.getMessage());
            response.setTotalSize("0.0GB");
        } catch (IOException e) {
            response.setRetCode("-1");
            response.setRetMsg("Build instance failed.");
            response.setTotalSize("0.0GB");
        }
        return response;
    }

    @Override
    public GetPathTotalSizeResponse getHBaseTotalSize() {
        GetPathTotalSizeResponse response = new GetPathTotalSizeResponse();
        try {
            String size = repository.getHTMLSize("/hbase/data");
            response.setRetCode("0");
            response.setRetMsg("Get total size success.");
            response.setTotalSize(size);
        } catch (HdfsException e) {
            response.setRetCode("-1");
            response.setRetMsg(e.getMessage());
            response.setTotalSize("0.0GB");
        } catch (IOException e) {
            response.setRetCode("-1");
            response.setRetMsg("Build instance failed.");
            response.setTotalSize("0.0GB");
        }
        return response;
    }
}
