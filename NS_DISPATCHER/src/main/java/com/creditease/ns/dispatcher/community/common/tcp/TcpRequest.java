package com.creditease.ns.dispatcher.community.common.tcp;

import com.creditease.ns.dispatcher.community.common.Request;

public class TcpRequest extends Request {
    private String headMessage;
    private String XMLContent;

    public String getXMLContent() {
        return XMLContent;
    }

    public void setXMLContent(String XMLContent) {
        this.XMLContent = XMLContent;
    }

    public String getHeadMessage() {
        return headMessage;
    }

    public void setHeadMessage(String headMessage) {
        this.headMessage = headMessage;
    }
}
