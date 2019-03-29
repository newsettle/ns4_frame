package com.creditease.ns.dispatcher.community.common.http;

import com.creditease.ns.dispatcher.community.common.Context;
import com.creditease.ns.dispatcher.community.common.ProtocolType;

public class HttpContext extends Context {

    public HttpContext() {
        super.setRequest(new HttpRequest());
        super.setResponse(new HttpResponse());
        super.setProtocolType(ProtocolType.HTTP);
    }

}
