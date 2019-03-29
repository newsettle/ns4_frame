package com.creditease.ns.dispatcher.community.http;

import com.creditease.ns.dispatcher.community.common.ContentType;
import com.creditease.ns.dispatcher.community.common.error.ErrorHandler;
import com.creditease.ns.dispatcher.community.common.error.ErrorHandlerException;
import com.creditease.ns.dispatcher.community.common.http.HttpContext;
import com.creditease.ns.dispatcher.community.common.http.HttpResponse;
import com.creditease.ns.dispatcher.community.rpc.RPCExecutor;
import com.creditease.ns.dispatcher.core.ErrorMessageCenter;
import com.creditease.ns.dispatcher.community.common.error.ErrorType;
import com.creditease.ns.log.NsLog;
import io.netty.channel.*;
import io.netty.handler.codec.http.HttpResponseStatus;

import static io.netty.buffer.Unpooled.copiedBuffer;

public class HttpRPCHandler extends SimpleChannelInboundHandler<HttpContext> {
	private static NsLog flowLog = NsLog.getFlowLog("Dispatcher", "分发器");

	private static RPCExecutor rpcExecutor = new RPCExecutor();

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, HttpContext msg) throws Exception {
		rpcExecutor.asyncExecute(ctx, msg);
	}




	@Override
	public void channelReadComplete(ChannelHandlerContext ctx){
		ctx.flush();
	}


	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		flowLog.error(cause, "处理消息异常");
		ErrorHandler.handle(new ErrorHandlerException(cause, ErrorType.UNKOWN_ERROR), ctx.channel(), new HttpResponse());

	}

	public static int getQueueSize(){
		return rpcExecutor.getQueueSize();
	}

	public static int getActiveThreadCount(){
		return rpcExecutor.getActiveThreadCount();
	}
}
