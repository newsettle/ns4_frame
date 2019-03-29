package com.creditease.ns.dispatcher.community.http;


import com.creditease.framework.scope.RetInfo;
import com.creditease.ns.dispatcher.convertor.json.JSONConvertor;
import com.creditease.framework.scope.SystemRetInfo;

import java.util.Map;

/**
 * 用于封装返回的对象
 */
public class ResponseContent {
    public ResponseContent(RetInfo retCode) {
        this.retCode = retCode.getCode();
        this.retInfo = retCode.getMsg();
    }

    public ResponseContent(RetInfo retCode,Map data) {
        this.retCode = retCode.getCode();
        this.retInfo = retCode.getMsg();
        this.data = data;
    }

    public ResponseContent(String retCode, String retInfo) {
        if (retCode == null) {
            retCode = SystemRetInfo.NO_RETURN_CODE.getCode();
        }
        if (retInfo == null) {
            retInfo = SystemRetInfo.NO_RETURN_CODE.getMsg();
        }
        this.retCode = retCode;
        this.retInfo = retInfo;
    }

    public String retCode;
    public String retInfo;
    public Map data;

    public String getRetCode() {
        return retCode;
    }

    public void setRetCode(String retCode) {
        this.retCode = retCode;
    }

    public String getRetInfo() {
        return retInfo;
    }

    public void setRetInfo(String retInfo) {
        this.retInfo = retInfo;
    }

    public Map getData() {
        return data;
    }

    public void setData(Map data) {
        this.data = data;
    }


    public String toJSON() {
        return JSONConvertor.toJSON(this);
    }
}
