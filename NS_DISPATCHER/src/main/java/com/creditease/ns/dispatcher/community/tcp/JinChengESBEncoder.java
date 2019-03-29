package com.creditease.ns.dispatcher.community.tcp;

import com.creditease.ns.dispatcher.community.common.tcp.TcpResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class JinChengESBEncoder extends MessageToByteEncoder<TcpResponse> {
    @Override
    protected void encode(ChannelHandlerContext ctx, TcpResponse msg, ByteBuf out) throws Exception {
        byte[] responseContent = msg.getResponseContent();
        out.writeBytes(responseContent);

    }
}
