package com.creditease.ns.dispatcher.community.http;


import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslContext;

import com.creditease.ns.dispatcher.netty.ext.ChannelConfig;
import com.creditease.ns.log.NsLog;

public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {
    private static NsLog initLog =  NsLog.getFramLog("Dispatcher", "分发器");
    private SslContext sslContext;
    
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline p = socketChannel.pipeline();
        ChannelConfig channelConfig = new ChannelConfig();
        //判断是否是https
        //是https获取对应的sslengine 添加sslhandler
        if (sslContext != null) 
		{
//        	DeterMineHttpsHandler deterMineHttpsHandler = new DeterMineHttpsHandler();
//	        p.addLast("determine",deterMineHttpsHandler);
//	        p.addLast("sslcontroller",new SslController(sslContext, deterMineHttpsHandler, socketChannel,channelConfig));
        	channelConfig.setPosibleHttps(true);
        	p.addLast("sslHandler",sslContext.newHandler(socketChannel.alloc()));
        	
		}
	        
        p.addLast("httpservercode",new HttpServerCodec(4096, 8192, 8192,false));
        p.addLast(new HttpObjectAggregator(1048576));
        p.addLast(new HttpDispatcherServerHandler(channelConfig));
        p.addLast(new HttpRPCHandler());
    }
    
    public HttpServerInitializer(SslContext sslCtx) {
    	this.sslContext = sslCtx;
    }
    
    public HttpServerInitializer() {
	}
}
