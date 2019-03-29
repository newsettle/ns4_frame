package com.creditease.ns.dispatcher.community.tcp;

import com.creditease.ns.dispatcher.core.ConfigCenter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;

public class TcpServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        //获取管道
        ChannelPipeline pipeline = ch.pipeline();
        //读取消息超时检测
        pipeline.addLast(new ReadTimeoutHandler(ConfigCenter.getConfig.getTimeout()));
        //JinChengESB解码
        pipeline.addLast(new JinChengESBDecoder());
        //JinChengESB编码
        pipeline.addLast(new JinChengESBEncoder());
        //调用远端服务
        pipeline.addLast(new TcpRPCHandler());
    }
}
