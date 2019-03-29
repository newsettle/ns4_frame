package com.creditease.ns.dispatcher.netty.ext;

import java.net.SocketAddress;
import java.util.List;

import com.creditease.ns.log.NsLog;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelPromise;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObjectDecoder;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;

public class SslController extends ChannelInboundHandlerAdapter {
    private static NsLog httpsLog = NsLog.getFramLog("Dispatcher", "SslController");
    private SslContext sslContext;
    private DeterMineHttpsHandler deterMineHttpsHandler;
    private SocketChannel socketChannel;
    private boolean isAddedSslHandler;
    private ChannelConfig channelConfig;
    private boolean isHttps;

    public SslController(SslContext sslContext, DeterMineHttpsHandler deterMineHttpsHandler, SocketChannel socketChannel, ChannelConfig channelConfig) {
        this.sslContext = sslContext;
        this.deterMineHttpsHandler = deterMineHttpsHandler;
        this.socketChannel = socketChannel;
        this.channelConfig = channelConfig;
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        if (deterMineHttpsHandler != null) {
            if (deterMineHttpsHandler.isHttps()) {
                if (sslContext != null && socketChannel != null && !isAddedSslHandler) {
                    socketChannel.pipeline().addAfter("sslcontroller", "sslhandler", sslContext.newHandler(socketChannel.alloc()));
                    isAddedSslHandler = true;
                    isHttps = true;
                }
            }
        }

        channelConfig.setPosibleHttps(isHttps);
        super.channelRead(ctx, msg);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        httpsLog.error("出现异常", cause);
        super.exceptionCaught(ctx, cause);
    }

}
