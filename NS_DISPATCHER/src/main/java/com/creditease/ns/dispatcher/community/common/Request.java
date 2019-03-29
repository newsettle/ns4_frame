package com.creditease.ns.dispatcher.community.common;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class Request {
    private long accessTimeStamp;
    private String serverName;

    public long getAccessTimeStamp() {
        return accessTimeStamp;
    }

    public void setAccessTimeStamp(long accessTimeStamp) {
        this.accessTimeStamp = accessTimeStamp;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this,
                ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
