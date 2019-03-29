package com.creditease.ns.dispatcher.community.rpc;

import com.creditease.ns.dispatcher.community.common.ContentType;
import com.creditease.ns.dispatcher.community.common.Response;
import com.creditease.ns.dispatcher.community.common.http.HttpResponse;
import com.creditease.ns.dispatcher.community.common.tcp.TcpResponse;
import com.creditease.ns.dispatcher.community.http.ResponseWriter;
import com.creditease.ns.dispatcher.core.ErrorMessageCenter;
import com.creditease.ns.dispatcher.community.common.error.ErrorType;
import com.creditease.ns.dispatcher.core.GolbalCenter;
import com.google.common.base.Charsets;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

public class RPCRejectedExecutionHandler implements RejectedExecutionHandler {
    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        if (r instanceof RPCTask) {
            RPCTask task = (RPCTask) r;
            Response response = ((RPCTask) r).getContext().getResponse();
            if (response instanceof HttpResponse) {
                HttpResponse httpResponse = (HttpResponse) response;
                httpResponse.setContentType(ContentType.JSON);
                httpResponse.setResponseContent(GolbalCenter.get(ErrorMessageCenter.class).getErrorMessage(ErrorType.REQUEST_LIMIT));
                httpResponse.setStatus(HttpResponseStatus.OK);
                ResponseWriter.write(task.getCtx().channel(), httpResponse);
            } else if (response instanceof TcpResponse) {
                TcpResponse tcpResponse = (TcpResponse) response;
                tcpResponse.setResponseContent(GolbalCenter.get(ErrorMessageCenter.class).getErrorMessage(ErrorType.REQUEST_LIMIT).getBytes(Charsets.UTF_8));
                ResponseWriter.write(task.getCtx().channel(), tcpResponse);
            }
        }
    }
}
