package com.creditease.ns.transporter.executor;

import com.creditease.ns.framework.startup.LifeCycle;
import com.creditease.ns.log.NsLog;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadExecutorManager implements LifeCycle {
    private static final NsLog frameLog = NsLog.getFramLog("commons", "ThreadExecutorManager");
    private Map<String, ThreadPoolExecutor> container = new ConcurrentHashMap<>();

    @Override
    public void startUp() throws Exception {

    }

    @Override
    public void destroy() throws Exception {
        for (Map.Entry<String, ThreadPoolExecutor> entry : container.entrySet()) {
            ThreadPoolExecutor executor = entry.getValue();
            frameLog.info("{} is shutting down now", entry.getKey());
            try {
//                executor.shutdown();
                while (executor.getActiveCount() != 0 && executor.getQueue().size() != 0) {
                    executor.awaitTermination(1, TimeUnit.SECONDS);
                    frameLog.info("{} 's activeCount:{}, queueLength:{}",entry.getKey(),executor.getActiveCount(),executor.getQueue().size());
                }
            } catch (InterruptedException e) {
                frameLog.error("shutting ThreadPoolExecutor error :{}", entry.getKey(), e);
                throw e;
            }
            frameLog.info("{} is shutted down", entry.getKey());

        }
    }

    public void register(ThreadPoolExecutor nsThreadExecutor) {
        container.put(String.valueOf(nsThreadExecutor.hashCode()), nsThreadExecutor);
    }

    public void register(String name, ThreadPoolExecutor nsThreadExecutor) {
        container.put(name, nsThreadExecutor);
    }

}
