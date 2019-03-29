package com.creditease.ns.dispatcher.community.rpc;

import com.creditease.ns.dispatcher.community.common.Context;
import com.creditease.ns.dispatcher.core.ConfigCenter;
import com.creditease.ns.log.NsLog;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.*;

public class RPCExecutor {
    private final static NsLog flowLog = NsLog.getFlowLog("RPCTask", "rcp执行线程");

    private final ThreadPoolExecutor threadPoolExecutor;

    private final LinkedBlockingQueue queue;

    public RPCExecutor(){
        queue = new LinkedBlockingQueue(ConfigCenter.getConfig.getRpcQueueLength());
        ThreadFactory threadFactory = new RPCThreadFactory();
        RejectedExecutionHandler rejectHandler = new RPCRejectedExecutionHandler();
        threadPoolExecutor = new ThreadPoolExecutor(ConfigCenter.getConfig.getRpcExecutorSize(), ConfigCenter.getConfig.getRpcExecutorSize(), 0, TimeUnit.MILLISECONDS,queue, threadFactory,
                rejectHandler);
    }

    public void asyncExecute(ChannelHandlerContext ctx, Context context) throws TimeoutException{
        flowLog.info("controller rpc queue size {}", queue.size());
        long elapsedSecond = TimeUnit.SECONDS.convert(System.currentTimeMillis() - context.getRequest().getAccessTimeStamp(), TimeUnit.MILLISECONDS);
        if( context.getRequest().getAccessTimeStamp() != 0  && elapsedSecond >= ConfigCenter.getConfig.getTimeout()){
            throw new TimeoutException(context.getId()+" message is timeout");
        }
        RPCTask task = new RPCTask(ctx, context);
        threadPoolExecutor.execute(task);
    }

    public int getQueueSize(){
        return queue.size();
    }

    public int getActiveThreadCount(){
        return threadPoolExecutor.getActiveCount();
    }


}