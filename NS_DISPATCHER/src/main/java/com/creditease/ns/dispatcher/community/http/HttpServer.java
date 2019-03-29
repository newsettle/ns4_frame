package com.creditease.ns.dispatcher.community.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.ssl.SslContext;

import java.io.File;

import org.apache.commons.lang.StringUtils;

import com.creditease.ns.dispatcher.community.log.NSLogginHandler;
import com.creditease.ns.dispatcher.core.ConfigCenter;
import com.creditease.ns.framework.startup.LifeCycle;
import com.creditease.ns.log.NsLog;
import com.creditease.ns.mq.MQTemplate;


public class HttpServer  implements LifeCycle {
	private static NsLog initLog =  NsLog.getFramLog("Dispatcher", "分发器");
	private ServerBootstrap httpBootstrap = new ServerBootstrap();
	private ServerBootstrap httpsBootstrap = new ServerBootstrap();
	EventLoopGroup bossGroup = new NioEventLoopGroup(1);
	EventLoopGroup workerGroup = new NioEventLoopGroup(10);

	@Override
	public void startUp() throws Exception {
		initLog.info("启动Http服务");
		int port = ConfigCenter.getConfig.getHttpPort();
		final int httpsPort = ConfigCenter.getConfig.getHttpsPort();
		httpBootstrap.option(ChannelOption.SO_BACKLOG, 1024);
		
		
		if(!StringUtils.isEmpty(ConfigCenter.getConfig.getCaPath()) && !StringUtils.isEmpty(ConfigCenter.getConfig.getKeyPath())) {
			final File caFile = new File(ConfigCenter.getConfig.getCaPath());
			final File keyFile = new File(ConfigCenter.getConfig.getKeyPath());
			final SslContext sslCtx = SslContext.newServerContext(caFile, keyFile);
			if (!caFile.exists()) {
				throw new Exception("指定的公钥不存在");
			}

			if (!keyFile.exists()) {
				throw new Exception("指定的私钥不存在");
			}
			
			
			Thread httpsServerThread = new Thread(new Runnable() {

				@Override
				public void run() {
					
					httpsBootstrap.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.handler(new NSLogginHandler(LogLevel.INFO))
					.childHandler(new HttpServerInitializer(sslCtx));
					try {
						Channel httpsCh = httpsBootstrap.bind(httpsPort).sync().channel();
						httpsCh.closeFuture().sync();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
			
			httpsServerThread.start();
		}
		httpBootstrap.group(bossGroup, workerGroup)
		.channel(NioServerSocketChannel.class)
		.handler(new NSLogginHandler(LogLevel.INFO))
		.childHandler(new HttpServerInitializer());
		try {
			Channel ch = httpBootstrap.bind(port).sync().channel();
			ch.closeFuture().sync();
		}
		catch(Exception e) {
			e.printStackTrace();
			destroy();
		}
		

	}

	@Override
	public void destroy() {
		bossGroup.shutdownGracefully();
		workerGroup.shutdownGracefully();
	}
}
