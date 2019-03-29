package com.creditease.ns.dispatcher.community.common.tcp;

import com.creditease.ns.dispatcher.community.common.Context;
import com.creditease.ns.dispatcher.community.common.ProtocolType;

public class TcpContext extends Context {
    public TcpContext() {
        super.setProtocolType(ProtocolType.TCP);
        super.setRequest(new TcpRequest());
        super.setResponse(new TcpResponse());
    }
}
