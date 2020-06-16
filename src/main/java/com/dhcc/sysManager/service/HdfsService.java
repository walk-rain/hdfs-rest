package com.dhcc.sysManager.service;

import com.dhcc.sysManager.entity.msg.GetHBaseTableSizeRequest;
import com.dhcc.sysManager.entity.msg.GetPathTotalSizeResponse;

public interface HdfsService {
    public GetPathTotalSizeResponse getHtmlSize();
    public GetPathTotalSizeResponse getHBaseTableSize(GetHBaseTableSizeRequest request);
    public GetPathTotalSizeResponse getHBaseTotalSize();
}
