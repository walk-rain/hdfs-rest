package com.dhcc.sysManager.entity.msg;

import javax.validation.constraints.NotEmpty;

public class GetHBaseTableSizeRequest {
    @NotEmpty(message = "nameSpace cannot be empty.")
    private String nameSpace;
    private String tableName;

    public String getNameSpace() {
        return nameSpace;
    }

    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public String toString() {
        return "GetHBaseTableSizeRequest{" +
                "nameSpace='" + nameSpace + '\'' +
                ", tableName='" + tableName + '\'' +
                '}';
    }
}
