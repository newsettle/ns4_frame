package com.creditease.ns.dispatcher.community.common.http;

import com.creditease.framework.pojo.DefaultServiceMessage;
import com.creditease.framework.scope.*;
import com.creditease.framework.util.ProtoStuffSerializeUtil;
import com.creditease.ns.dispatcher.community.common.ContentType;
import com.creditease.ns.dispatcher.community.common.Response;
import com.creditease.ns.dispatcher.community.common.error.ErrorHandler;
import com.creditease.ns.dispatcher.community.common.error.ErrorHandlerException;
import com.creditease.ns.dispatcher.community.common.error.ErrorType;
import com.creditease.ns.dispatcher.community.common.tcp.TcpResponse;
import com.creditease.ns.dispatcher.community.http.ResponseContent;
import com.creditease.ns.dispatcher.community.http.ResponseWriter;
import com.creditease.ns.log.NsLog;
import com.creditease.ns.mq.model.Message;
import com.google.common.base.Charsets;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaders.Names.LOCATION;

public class HttpResponse extends Response {
    private final static NsLog flowLog = NsLog.getFlowLog("HttpResponse", "http响应");
    private String responseContent;
    private HttpResponseStatus status;


    public String getResponseContent() {
        return responseContent;
    }

    public void setResponseContent(String responseContent) {
        this.responseContent = responseContent;
    }

    public HttpResponseStatus getStatus() {
        return status;
    }

    public void setStatus(HttpResponseStatus status) {
        this.status = status;
    }


    public void writeOut(Channel channel, Message serviceMessage, HttpResponse httpResponse) throws Exception {
        long responseStart = System.currentTimeMillis();
        if (serviceMessage == null) {
            flowLog.info("返回内容为空");

            httpResponse.setStatus(HttpResponseStatus.OK);
            httpResponse.setResponseContent(new ResponseContent(SystemRetInfo.DISP_NO_RESPONSE).toJSON());
            httpResponse.setContentType(ContentType.JSON);
        } else {
            //单纯的初始化一个serviceMessage 可以直接声明一个serviceMessage，这里需要修改
            RequestScope requestScope = new RequestScope();
            ExchangeScope exchangeScope = new ExchangeScope();
            OutScope outScope = new OutScope();
            DefaultServiceMessage defaultServiceMessage = new DefaultServiceMessage(requestScope, exchangeScope, outScope);

            defaultServiceMessage = (DefaultServiceMessage) ProtoStuffSerializeUtil.unSerializeForCommon(serviceMessage.getBody());
            if (serviceMessage.getHeader() != null && serviceMessage.getHeader().getContentType() == 1) {
                //使用返回302的方式进行返回HTML页面
                String redirectUrl = defaultServiceMessage.getOut(SystemOutKey.HTML_REDIRECT_URL);
                String windLoadPage = defaultServiceMessage.getOut(SystemOutKey.HTML_WINDOW_ONLOAD);
                String selfHtmlContent = defaultServiceMessage.getOut(SystemOutKey.HTML_SELF_CONTENT);
                if (redirectUrl != null) {
                    writeResponseLocation(channel, redirectUrl);
                    flowLog.info("HTML 302跳转方式返回");
                    return;
                } else if (windLoadPage != null) {
                    //返回HTML页面
                    httpResponse.setStatus(HttpResponseStatus.OK);
                    httpResponse.setResponseContent(windLoadPage);
                    httpResponse.setContentType(ContentType.HTML);
                    flowLog.info("HTML js reload跳转方式返回");
                } else if (selfHtmlContent != null) {

                    httpResponse.setStatus(HttpResponseStatus.OK);
                    httpResponse.setResponseContent(selfHtmlContent);
                    httpResponse.setContentType(ContentType.HTML);
                    flowLog.info("HTML 自定义页面返回");
                } else {
                    httpResponse.setStatus(HttpResponseStatus.INTERNAL_SERVER_ERROR);
                    httpResponse.setResponseContent("Internal Server Error:跳转地址为空");
                    httpResponse.setContentType(ContentType.HTML);
                    flowLog.error("HTML模式无返回内容");
                }
            }
            //添加一个新的类型 plainText，3.0框架需要这种格式
            else if (serviceMessage.getHeader() != null && serviceMessage.getHeader().getContentType() == 3) {
                String textContent = defaultServiceMessage.getOut(SystemOutKey.PLAIN_TEXT_CONTENT);
                if (textContent != null) {
                    httpResponse.setStatus(HttpResponseStatus.OK);
                    httpResponse.setResponseContent(textContent);
                    httpResponse.setContentType(ContentType.TEXT);
                    flowLog.info("PlainText 返回");
                } else {

                    httpResponse.setStatus(HttpResponseStatus.INTERNAL_SERVER_ERROR);
                    httpResponse.setResponseContent("Internal Server Error:无对应的纯文本内容");
                    httpResponse.setContentType(ContentType.TEXT);
                    flowLog.error("PlainText模式无返回内容");
                }

            } else {
                //默认json

                httpResponse.setStatus(HttpResponseStatus.OK);
                httpResponse.setResponseContent(defaultServiceMessage.getJsonOut());
                httpResponse.setContentType(ContentType.JSON);
                flowLog.info("json方式返回:{}", defaultServiceMessage.getJsonOut());
            }
            ResponseWriter.write(channel, httpResponse);
        }
        flowLog.debug("服务返回到解析数据结束耗时:{}ms", System.currentTimeMillis() - responseStart);
    }

    private void writeResponseLocation(Channel channel, String URL) {
        // Decide whether to close the connection or not.
        boolean close = false;
        // Build the response object.
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, HttpResponseStatus.FOUND);
        response.headers().set(CONTENT_TYPE, ContentType.HTML);
        response.headers().set(LOCATION, URL);
        response.headers().set(CONTENT_LENGTH, 0);
        // Write the response.
        ChannelFuture future = channel.writeAndFlush(response);
        future.addListener(ChannelFutureListener.CLOSE);
    }
}
