package com.creditease.ns.dispatcher.community.common.error;

import com.creditease.ns.dispatcher.community.common.ContentType;
import com.creditease.ns.dispatcher.community.common.Context;
import com.creditease.ns.dispatcher.community.common.ProtocolType;
import com.creditease.ns.dispatcher.community.common.Response;
import com.creditease.ns.dispatcher.community.common.http.HttpResponse;
import com.creditease.ns.dispatcher.community.common.tcp.TcpResponse;
import com.creditease.ns.dispatcher.community.http.ResponseWriter;
import com.creditease.ns.dispatcher.core.ConfigCenter;
import com.creditease.ns.dispatcher.core.ErrorMessageCenter;
import com.creditease.ns.dispatcher.core.GolbalCenter;
import com.google.common.base.Charsets;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpResponseStatus;

public class ErrorHandler {
    public static void handle(ErrorHandlerException exception, Channel channel, Context context) {
        ErrorType errorType = exception.getErrorType();

        if (context != null) {

            if (context.getProtocolType() == ProtocolType.HTTP) {
                HttpResponse httpResponse = (HttpResponse) context.getResponse();
                httpResponse.setStatus(HttpResponseStatus.OK);
                httpResponse.setResponseContent(GolbalCenter.get(ErrorMessageCenter.class).getErrorMessage(errorType));
                httpResponse.setContentType(ContentType.JSON);
                ResponseWriter.write(channel, httpResponse);
            } else if (context.getProtocolType() == ProtocolType.TCP) {
                TcpResponse tcpResponse = (TcpResponse) context.getResponse();
                tcpResponse.setResponseContent(GolbalCenter.get(ErrorMessageCenter.class).getErrorMessage(errorType).getBytes(Charsets.UTF_8));
                ResponseWriter.write(channel, tcpResponse);
            }
        }
    }

    public static void handle(ErrorHandlerException exception, Channel channel) {
        ErrorType errorType = exception.getErrorType();
        if ("http".equalsIgnoreCase(ConfigCenter.getConfig.getProtocolType())) {
            HttpResponse httpResponse = new HttpResponse();
            httpResponse.setStatus(HttpResponseStatus.OK);
            httpResponse.setResponseContent(GolbalCenter.get(ErrorMessageCenter.class).getErrorMessage(errorType));
            httpResponse.setContentType(ContentType.JSON);
            ResponseWriter.write(channel, httpResponse);
        } else if ("tcp".equalsIgnoreCase(ConfigCenter.getConfig.getProtocolType())) {
            TcpResponse tcpResponse = new TcpResponse();
            tcpResponse.setResponseContent(GolbalCenter.get(ErrorMessageCenter.class).getErrorMessage(errorType).getBytes(Charsets.UTF_8));
            ResponseWriter.write(channel, tcpResponse);
        }
    }

    public static void handle(ErrorHandlerException exception, Channel channel, Response response) {
        ErrorType errorType = exception.getErrorType();
        if (response instanceof HttpResponse) {
            HttpResponse httpResponse = (HttpResponse) response;
            httpResponse.setStatus(HttpResponseStatus.OK);
            httpResponse.setResponseContent(GolbalCenter.get(ErrorMessageCenter.class).getErrorMessage(errorType));
            httpResponse.setContentType(ContentType.JSON);
            ResponseWriter.write(channel, httpResponse);
        } else if (response instanceof TcpResponse) {
            TcpResponse tcpResponse = (TcpResponse) response;
            tcpResponse.setResponseContent(GolbalCenter.get(ErrorMessageCenter.class).getErrorMessage(errorType).getBytes(Charsets.UTF_8));
            ResponseWriter.write(channel, tcpResponse);
        }
    }
}
