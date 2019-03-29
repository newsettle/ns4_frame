package com.creditease.ns.transporter.result;


import com.creditease.framework.scope.RetInfo;
import com.creditease.ns.transporter.chain.adapter.Abandonable;

public enum TransporterReturnInfo implements RetInfo{
    UNKNOWN_ERROR("1000","未知错误")
    ;
    private String code;
    private String msg;

    TransporterReturnInfo(String code, String msg){
        this.code = code;
        this.msg = msg;
    };

    @Override
    public String toString() {
        return "retCode\":\"" + code +  "\", \"retInfo\":\"" + msg ;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }
}
