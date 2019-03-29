package com.creditease.ns.transporter.executor;


import java.util.concurrent.*;

public class Executors {

    private static Executors instance;
    private ThreadExecutorManager manager;

    private Executors(ThreadExecutorManager manager) {
        this.manager = manager;
    }

    public static void init(ThreadExecutorManager manager) {
        instance = new Executors(manager);
    }

    public static Executors getInstance() {
        return instance;
    }


    public ThreadPoolExecutor makeFixedThreadPoolExecutor(final int nThreads) {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(nThreads, nThreads,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());

        manager.register(threadPoolExecutor);
        return threadPoolExecutor;
    }

    public ThreadPoolExecutor makeFixedThreadPoolExecutor(final String name, final int nThreads) {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(nThreads, nThreads,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());

        manager.register(name, threadPoolExecutor);
        return threadPoolExecutor;
    }

    public ThreadPoolExecutor makeFixedThreadPoolExecutor(final int nThreads, RejectedExecutionHandler handler) {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(nThreads, nThreads,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(), handler);

        manager.register(threadPoolExecutor);
        return threadPoolExecutor;
    }

    public ThreadPoolExecutor makeFixedThreadPoolExecutor(final String name, final int nThreads, RejectedExecutionHandler handler) {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(nThreads, nThreads,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(), handler);

        manager.register(name, threadPoolExecutor);
        return threadPoolExecutor;
    }

    public ThreadPoolExecutor makeSingleThreadPoolExecutor() {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
        manager.register(threadPoolExecutor);
        return threadPoolExecutor;
    }

    public ThreadPoolExecutor makeSingleThreadPoolExecutor(final String name) {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
        manager.register(name, threadPoolExecutor);
        return threadPoolExecutor;
    }

    public ThreadPoolExecutor makeThreadPoolExecutor(final int corePoolSize, final int maximumPoolSize, final long keepAliveTime, final TimeUnit unit, final BlockingQueue<Runnable> workQueue) {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize,
                keepAliveTime, unit,
                workQueue);
        manager.register(threadPoolExecutor);
        return threadPoolExecutor;
    }

    public ThreadPoolExecutor makeThreadPoolExecutor(final String name, final int corePoolSize, final int maximumPoolSize, final long keepAliveTime, final TimeUnit unit, final BlockingQueue<Runnable> workQueue) {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize,
                keepAliveTime, unit,
                workQueue);
        manager.register(name, threadPoolExecutor);
        return threadPoolExecutor;
    }

    public ThreadPoolExecutor makeThreadPoolExecutor(final int corePoolSize, final int maximumPoolSize, final long keepAliveTime, final TimeUnit unit, final BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize,
                keepAliveTime, unit,
                workQueue, handler);
        manager.register(threadPoolExecutor);
        return threadPoolExecutor;
    }

    public ThreadPoolExecutor makeThreadPoolExecutor(final String name, final int corePoolSize, final int maximumPoolSize, final long keepAliveTime, final TimeUnit unit, final BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize,
                keepAliveTime, unit,
                workQueue, handler);
        manager.register(name, threadPoolExecutor);
        return threadPoolExecutor;
    }
}
