package com.creditease.ns.dispatcher.community.tcp;

import com.creditease.ns.dispatcher.community.common.ContentType;
import com.creditease.ns.dispatcher.community.common.error.ErrorHandler;
import com.creditease.ns.dispatcher.community.common.error.ErrorHandlerException;
import com.creditease.ns.dispatcher.community.common.http.HttpResponse;
import com.creditease.ns.dispatcher.community.common.tcp.TcpContext;
import com.creditease.ns.dispatcher.community.common.tcp.TcpResponse;
import com.creditease.ns.dispatcher.community.http.ResponseWriter;
import com.creditease.ns.dispatcher.community.rpc.RPCExecutor;
import com.creditease.ns.dispatcher.core.ErrorMessageCenter;
import com.creditease.ns.dispatcher.community.common.error.ErrorType;
import com.creditease.ns.log.NsLog;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.util.logging.Handler;

public class TcpRPCHandler extends SimpleChannelInboundHandler<TcpContext> {
    private static NsLog flowLog = NsLog.getFlowLog("Dispatcher", "分发器");

    private static RPCExecutor rpcExecutor = new RPCExecutor();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TcpContext msg) throws Exception {
        rpcExecutor.asyncExecute(ctx, msg);
    }


    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        flowLog.error(cause, "处理消息异常");
        ErrorHandler.handle(new ErrorHandlerException(cause, ErrorType.UNKOWN_ERROR), ctx.channel(), new TcpResponse());

    }

    public static int getQueueSize() {
        return rpcExecutor.getQueueSize();
    }

    public static int getActiveThreadCount() {
        return rpcExecutor.getActiveThreadCount();
    }
}
