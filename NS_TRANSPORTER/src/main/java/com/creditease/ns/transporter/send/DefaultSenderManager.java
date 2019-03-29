package com.creditease.ns.transporter.send;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import com.creditease.framework.exception.ThreadUncaughtExceptionHandler;
import com.creditease.ns.framework.startup.LifeCycle;
import com.creditease.ns.log.NsLog;
import com.creditease.ns.transporter.buffer.BufferManager;
import com.creditease.ns.transporter.config.InQueueInfo;
import com.creditease.ns.transporter.context.XmlAppTransporterContext;
import com.creditease.ns.transporter.stop.PoisonMessage;

/**
 * 需要测试下没有数据会怎么样
 *
 * @author liuyang
 * 2015年8月12日下午7:40:34
 */
public class DefaultSenderManager implements SenderManager, LifeCycle {
    private static NsLog frameLog = NsLog.getFramLog("Transport", "DefaultSenderManager");

    private boolean isStarted = false;
    private boolean isStartedSend = false;
    private static int DEFAULT_SENDER_NUM_PER_QUEUE = 1;

    private Map<String, InQueueInfo> queueNameToQueueInfos;
    private BufferManager bufferManager;
    private static SenderManager self = new DefaultSenderManager();
    private Map<String, ThreadPoolExecutor> queueNameToSenderExecutors = new HashMap<>();

    public void init() {
        self = this;
    }

    @Override
    public synchronized void startUp() {
        if (!isStarted) {
            startSend();
            isStarted = true;
        }
    }

    @Override
    public void destroy() {
        long startTime = System.currentTimeMillis();
        frameLog.info("DefaultSenderManager stoping");
        stopFetch();
        frameLog.info("DefaultSenderManager stoped cost:{}ms", System.currentTimeMillis() - startTime);
    }

    @Override
    public synchronized void startSend() {
        //异步启动线程获取消息 默认只用一个send线程

        if (!isStartedSend) {
            Iterator<String> it = queueNameToQueueInfos.keySet().iterator();
            while (it.hasNext()) {
                String queueName = it.next();
                InQueueInfo inQueueInfo = queueNameToQueueInfos.get(queueName);
                ThreadPoolExecutor scheduler = (ThreadPoolExecutor) Executors.newCachedThreadPool(new CustomThreadNameThreadFactory("s", queueName));
                for (int i = 0; i < inQueueInfo.getSenderNum(); i++) {
                    DefaultSender sender = new DefaultSender();
                    sender.setQueueName(queueName);
                    sender.setBufferManager(bufferManager);
                    scheduler.execute(sender);
                }
                queueNameToSenderExecutors.put(queueName, scheduler);
            }

            isStartedSend = true;
        }
    }

    @Override
    public void stopFetch() {
        Iterator<String> it = queueNameToQueueInfos.keySet().iterator();
        while (it.hasNext()) {
            long stopTime = System.currentTimeMillis();
            String queueName = it.next();
            //等待停止工作线程运行完毕
            //1.发放毒药
            try {
                bufferManager.putInSendBuffer(queueName, new PoisonMessage());
            } catch (Exception e) {
                frameLog.error("{} handler put poison message error", queueName, e);
            }
            //2.等待传染
            frameLog.info(" {} sender stoping", queueName);
            ThreadPoolExecutor threadPoolExecutor = queueNameToSenderExecutors.get(queueName);
            while (threadPoolExecutor.getActiveCount() > 0) {
                try {
                    threadPoolExecutor.awaitTermination(1, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            queueNameToSenderExecutors.remove(queueName);
            frameLog.info(" {} sender stoped cost:{}ms", queueName, System.currentTimeMillis() - stopTime);
        }
        isStartedSend = false;
        isStarted = false;
    }

    public Map<String, InQueueInfo> getQueueNameToQueueInfos() {
        return queueNameToQueueInfos;
    }

    public void setQueueNameToQueueInfos(
            Map<String, InQueueInfo> queueNameToQueueInfos) {
        this.queueNameToQueueInfos = Collections.unmodifiableMap(queueNameToQueueInfos);
    }


    public BufferManager getBufferManager() {
        return bufferManager;
    }

    public void setBufferManager(BufferManager bufferManager) {
        this.bufferManager = bufferManager;
    }

    public Map<String, ThreadPoolExecutor> getQueueNameToSenderExecutors() {
        return queueNameToSenderExecutors;
    }

    public void setQueueNameToSenderExecutors(
            Map<String, ThreadPoolExecutor> queueNameToSenderExecutors) {
        this.queueNameToSenderExecutors = queueNameToSenderExecutors;
    }


    static class CustomThreadNameThreadFactory implements ThreadFactory {
        static final AtomicInteger poolNumber = new AtomicInteger(1);
        final ThreadGroup group;
        final AtomicInteger threadNumber = new AtomicInteger(1);
        final String namePrefix;

        CustomThreadNameThreadFactory(String threadNamePrefix, String queuename) {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            namePrefix = threadNamePrefix + "-" + queuename + "-" +
                    poolNumber.getAndIncrement() +
                    "-t-";
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0);
            t.setUncaughtExceptionHandler(new ThreadUncaughtExceptionHandler());
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }

    public static synchronized SenderManager getInstance() {
        if (self == null) {
            self = new DefaultSenderManager();
        }
        return self;
    }

}
