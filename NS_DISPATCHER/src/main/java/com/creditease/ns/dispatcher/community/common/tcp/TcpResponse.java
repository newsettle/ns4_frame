package com.creditease.ns.dispatcher.community.common.tcp;

import com.creditease.framework.pojo.DefaultServiceMessage;
import com.creditease.framework.scope.*;
import com.creditease.framework.util.ProtoStuffSerializeUtil;
import com.creditease.ns.dispatcher.community.common.ContentType;
import com.creditease.ns.dispatcher.community.common.Response;
import com.creditease.ns.dispatcher.community.common.error.ErrorHandler;
import com.creditease.ns.dispatcher.community.common.error.ErrorHandlerException;
import com.creditease.ns.dispatcher.community.common.error.ErrorType;
import com.creditease.ns.dispatcher.community.common.http.HttpResponse;
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

public class TcpResponse extends Response {


    public TcpResponse() {
        super.setContentType(ContentType.BYTES);
    }

    private byte[] responseContent;

    public byte[] getResponseContent() {
        return responseContent;
    }

    public void setResponseContent(byte[] responseContent) {
        this.responseContent = responseContent;
    }


    public void writeOut(Channel channel, Message serviceMessage, TcpResponse tcpResponse) throws Exception {
        DefaultServiceMessage defaultServiceMessage = (DefaultServiceMessage) ProtoStuffSerializeUtil.unSerializeForCommon(serviceMessage.getBody());
        SystemRetInfo systemRetInfo = defaultServiceMessage.getOutByType(SystemOutKey.RETURN_CODE, SystemRetInfo.class);
        if (systemRetInfo == SystemRetInfo.CTRL_NOT_FOUND_SEVICE_ERROR) {
            ErrorHandler.handle(new ErrorHandlerException(ErrorType.SERVER_NOT_FOUND), channel);
            return;
        }
        String xmlContent = defaultServiceMessage.getOut(SystemOutKey.XML_OUT_CONTENT);
        tcpResponse.setResponseContent(xmlContent.getBytes(Charsets.UTF_8));
        ResponseWriter.write(channel, tcpResponse);
    }

}
