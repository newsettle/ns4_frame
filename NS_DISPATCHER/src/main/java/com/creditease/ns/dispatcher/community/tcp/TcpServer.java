package com.creditease.ns.dispatcher.community.tcp;

import com.creditease.ns.dispatcher.community.log.NSLogginHandler;
import com.creditease.ns.dispatcher.core.ConfigCenter;
import com.creditease.ns.framework.startup.LifeCycle;
import com.creditease.ns.log.NsLog;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;

public class TcpServer implements LifeCycle {
    private static NsLog initLog = NsLog.getFramLog("Dispatcher", "分发器");
    private ServerBootstrap tcpBootstrap = new ServerBootstrap();
    private EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private EventLoopGroup workerGroup = new NioEventLoopGroup(10);

    @Override
    public void startUp() throws Exception {
        initLog.info("启动Tcp服务");
        tcpBootstrap.option(ChannelOption.SO_BACKLOG, 1024);
        tcpBootstrap.childOption(ChannelOption.TCP_NODELAY, true);
        tcpBootstrap.group(bossGroup, workerGroup);
        tcpBootstrap.channel(NioServerSocketChannel.class);
        tcpBootstrap.handler(new NSLogginHandler(LogLevel.INFO));
        tcpBootstrap.childHandler(new TcpServerInitializer());

        try {
            Channel ch = tcpBootstrap.bind(ConfigCenter.getConfig.getTcpPort()).sync().channel();
            ch.closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
            destroy();
        }
    }

    @Override
    public void destroy() throws Exception {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }
}
