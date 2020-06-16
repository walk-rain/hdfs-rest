package com.dhcc.sysManager.controller;

import com.dhcc.sysManager.entity.msg.GetHBaseTableSizeRequest;
import com.dhcc.sysManager.entity.msg.GetPathTotalSizeResponse;
import com.dhcc.sysManager.service.HdfsService;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/api/hdfs-rest/v1.0/")
public class HdfsController {

    private HdfsService service;

    public HdfsController(HdfsService service) {
        this.service = service;
    }

    @RequestMapping(value = "/getHtmlSize")
    public GetPathTotalSizeResponse getHtmlSize() {
        return service.getHtmlSize();
    }

    @RequestMapping(value = "/getHBaseTableSize")
    public GetPathTotalSizeResponse getHBaseTableSize(@RequestBody @Valid GetHBaseTableSizeRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new GetPathTotalSizeResponse("-1", bindingResult.getFieldError().getDefaultMessage());
        } else {
            return service.getHBaseTableSize(request);
        }
    }

    @RequestMapping(value = "/getHBaseTotalSize")
    public GetPathTotalSizeResponse getHBaseTotalSize() {
        return service.getHBaseTotalSize();
    }

    @RequestMapping(value = "test")
    public String test() {
        return "test!!!";
    }
}
