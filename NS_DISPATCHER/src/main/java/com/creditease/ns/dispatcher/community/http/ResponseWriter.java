package com.creditease.ns.dispatcher.community.http;

import com.creditease.ns.dispatcher.community.common.http.HttpResponse;
import com.creditease.ns.dispatcher.community.common.tcp.TcpResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

import static io.netty.buffer.Unpooled.copiedBuffer;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;

public class ResponseWriter {
    private ResponseWriter() {
    }

    public static void write(Channel channel, HttpResponse httpResponse) {
        ByteBuf buf = copiedBuffer(httpResponse.getResponseContent(), CharsetUtil.UTF_8);
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, httpResponse.getStatus(), buf);
        response.headers().set(CONTENT_TYPE, httpResponse.getContentType().toValue());
        response.headers().set(CONTENT_LENGTH, buf.readableBytes());
        ChannelFuture future = channel.writeAndFlush(response);
        // Close the connection after the write operation is done if necessary.
        future.addListener(ChannelFutureListener.CLOSE);
    }

    public static void write(Channel channel, TcpResponse tcpResponse) {
        ChannelFuture future = channel.writeAndFlush(tcpResponse);
        future.addListener(ChannelFutureListener.CLOSE);
    }
}
