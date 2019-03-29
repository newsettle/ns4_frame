package com.creditease.ns.transporter.fetch;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import com.creditease.framework.exception.ThreadUncaughtExceptionHandler;
import com.creditease.ns.framework.startup.LifeCycle;
import com.creditease.ns.log.NsLog;
import com.creditease.ns.log.spi.TransporterLog;
import com.creditease.ns.transporter.buffer.BufferManager;
import com.creditease.ns.transporter.config.InQueueInfo;
import com.creditease.ns.transporter.context.XmlAppTransporterContext;

/**
 * 需要测试下没有数据会怎么样
 *
 * @author liuyang
 * 2015年8月12日下午7:40:34
 */
public class DefaultFetcherManager implements FetcherManager, LifeCycle {

    private static NsLog frameLog = NsLog.getFramLog("Transport", "DefaultFetcherManager");

    private boolean isStarted = false;
    private boolean isStartedFetch = false;
    private static int DEFAULT_FETCHER_NUM_PER_QUEUE = 1;

    private Map<String, InQueueInfo> queueNameToQueueInfos;
    private BufferManager bufferManager;
    private static FetcherManager self = new DefaultFetcherManager();
    private Map<String, ThreadPoolExecutor> queueNameToFetcherExecutors = new LinkedHashMap<>();
    private Map<String, List<Fetcher>> queueNameToFetcher = new HashMap<>();


    public void init() {
        self = this;
    }

    @Override
    public synchronized void startUp() {
        if (!isStarted) {
            startFetch();
            isStarted = true;
        }
    }

    @Override
    public void destroy() {
        long startTime = System.currentTimeMillis();
        frameLog.info("DefaultFetcherManager stoping ");
        stopFetch();
        frameLog.info("DefaultFetcherManager stoped , 耗时{}ms", System.currentTimeMillis() - startTime);
    }

    @Override
    public synchronized void startFetch() {
        //异步启动线程获取消息 默认只用一个fetch线程

        if (!isStartedFetch) {
            Iterator<String> it = queueNameToQueueInfos.keySet().iterator();
            while (it.hasNext()) {
                String queueName = it.next();
                InQueueInfo inQueueInfo = queueNameToQueueInfos.get(queueName);
                ThreadPoolExecutor scheduler = (ThreadPoolExecutor) Executors.newCachedThreadPool(new CustomThreadNameThreadFactory("f", queueName));
                List<Fetcher> fetcherList = new ArrayList<>(inQueueInfo.getFetcherNum());
                for (int i = 0; i < inQueueInfo.getFetcherNum(); i++) {
                    DefaultFetcher fetcher = new DefaultFetcher();
                    fetcher.setQueueName(queueName);
                    fetcher.setBufferManager(bufferManager);
                    scheduler.execute(fetcher);
                    fetcherList.add(fetcher);
                }
                queueNameToFetcherExecutors.put(queueName, scheduler);
                queueNameToFetcher.put(queueName, fetcherList);
            }

            isStartedFetch = true;
        }
    }

    @Override
    public void stopFetch() {
        Iterator<String> it = queueNameToQueueInfos.keySet().iterator();
        while (it.hasNext()) {
            String queueName = it.next();
            ThreadPoolExecutor threadPoolExecutor = queueNameToFetcherExecutors.get(queueName);
            List<Fetcher> fetcherList = queueNameToFetcher.get(queueName);
            for (Fetcher fetcher : fetcherList) {
                try {
                    fetcher.stop();
                } catch (Exception e) {
                    frameLog.error("{} fetcher stoping error", queueName, e);
                }
            }
            threadPoolExecutor.shutdownNow();
            while (threadPoolExecutor.getActiveCount() > 0) {
                try {
                    threadPoolExecutor.awaitTermination(1, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    frameLog.error("{} fetcher executors stoping error", queueName, e);
                }
            }

            queueNameToFetcherExecutors.remove(queueName);
        }
    }

    public Map<String, InQueueInfo> getQueueNameToQueueInfos() {
        return queueNameToQueueInfos;
    }

    public void setQueueNameToQueueInfos(
            Map<String, InQueueInfo> queueNameToQueueInfos) {
        this.queueNameToQueueInfos = Collections.unmodifiableMap(queueNameToQueueInfos);
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


    public static synchronized FetcherManager getInstance() {
        if (self == null) {
            self = new DefaultFetcherManager();
        }
        return self;
    }

    public BufferManager getBufferManager() {
        return bufferManager;
    }

    public void setBufferManager(BufferManager bufferManager) {
        this.bufferManager = bufferManager;
    }


}
