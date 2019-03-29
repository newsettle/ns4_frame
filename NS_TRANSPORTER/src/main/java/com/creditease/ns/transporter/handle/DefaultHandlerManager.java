package com.creditease.ns.transporter.handle;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import com.creditease.framework.exception.ThreadUncaughtExceptionHandler;
import com.creditease.framework.util.StringUtil;
import com.creditease.ns.framework.spring.SpringPlugin;
import com.creditease.ns.framework.startup.LifeCycle;
import com.creditease.ns.log.NsLog;
import com.creditease.ns.log.spi.TransporterLog;
import com.creditease.ns.transporter.buffer.BufferManager;
import com.creditease.ns.transporter.chain.service.AbstractServiceChainBridge;
import com.creditease.ns.transporter.config.ConfigManager;
import com.creditease.ns.transporter.config.InQueueInfo;
import com.creditease.ns.transporter.config.XmlConfigManager;
import com.creditease.ns.transporter.constants.TransporterConstants;
import com.creditease.ns.transporter.context.XmlAppTransporterContext;
import com.creditease.ns.transporter.stop.PoisonMessage;

/**
 * 需要测试下没有数据会怎么样
 *
 * @author liuyang
 * 2015年8月12日下午7:40:34
 */
public class DefaultHandlerManager implements HandlerManager, LifeCycle {

    private static NsLog frameLog = NsLog.getFramLog("Transport", "DefaultHandlerManager");

    private boolean isStarted = false;
    private boolean isStartedHandle = false;
    private Map<String, InQueueInfo> queueNameToQueueInfos;

    private BufferManager bufferManager;
    private static HandlerManager self = new DefaultHandlerManager();
    private Map<String, ThreadPoolExecutor> queueNameToExecutors = new HashMap<>();
    private ConfigManager configManager;
    private XmlAppTransporterContext context;


    public void init() {
        self = this;
    }

    @Override
    public synchronized void startUp() {
        if (!isStarted) {
            startHandle();
            isStarted = true;
        }
    }

    /**
     * 只能用最简单的方式 获取缓存中的数量当不为0时一直循环
     * 为0 循环结束
     * 然后判断发送缓存中数量是否为0 不为0 一直循环
     * 然后开始中断handler线程
     * 以上 receivebuffer和sendebuffer已经全部是空的
     */
    @Override
    public void destroy() {
        long startTime = System.currentTimeMillis();
        frameLog.info("DefaultHandlerManager stoping");
        Iterator<String> it = queueNameToQueueInfos.keySet().iterator();
        while (it.hasNext()) {
            long stopTime = System.currentTimeMillis();
            String queueName = it.next();
            frameLog.info(" {} handler stoping", queueName);
            //等待接收buffer为空
            while (bufferManager.sizeOfReceiveBufferOf(queueName) > 0) ;

            //等待停止工作线程运行完毕
            //1.发放毒药
            try {
                bufferManager.putInReceiveBuffer(queueName, new PoisonMessage());
            } catch (Exception e) {
                frameLog.error("{} handler put poison message error", queueName, e);
            }
            //2.等待传染
            ThreadPoolExecutor threadPoolExecutor = queueNameToExecutors.get(queueName);
            while (threadPoolExecutor.getActiveCount() > 0) {
                try {
                    threadPoolExecutor.awaitTermination(1, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    frameLog.error("{} handler stoping error", queueName, e);
                }
            }
            //等待发送buffer为空
            while (bufferManager.sizeOfSendBufferOf(queueName) > 0) ;

            //移除线程池
            queueNameToExecutors.remove(queueName);
            frameLog.info(" {} Handler stoped cost:{}ms", queueName, System.currentTimeMillis() - stopTime);
        }
        frameLog.info("DefaultHandlerManager stoped cost:" + (System.currentTimeMillis() - startTime) + "ms");
    }

    @Override
    public synchronized void startHandle() {
        //异步启动线程获取消息 默认只用一个fetch线程
        //TODO 消息处理前 处理后
        if (!isStartedHandle) {
            Iterator<String> it = queueNameToQueueInfos.keySet().iterator();
            while (it.hasNext()) {
                String queueName = it.next();
                InQueueInfo inQueueInfo = queueNameToQueueInfos.get(queueName);
                int handlerMaxNum = inQueueInfo.getHandlerNum();
                ThreadPoolExecutor scheduler = (ThreadPoolExecutor) Executors.newFixedThreadPool(handlerMaxNum, new CustomThreadNameThreadFactory("h", queueName));
                queueNameToExecutors.put(queueName, scheduler);
                Object handlerTarget = null;
                if (configManager instanceof XmlConfigManager) {
                    XmlConfigManager xmlConfigManager = (XmlConfigManager) configManager;
                    String serviceClassName = inQueueInfo.getServiceClassName();

                    try {
                        Class<?> cl = Class.forName(serviceClassName);
                        try {
                            Object o = null;
                            if (xmlConfigManager.isSpring()) {
                                //如果有代理 spring可能根据当前类型获取不到对象
                                //动态注册bean bean的名字只允许取beanName的小写
                                SpringPlugin springPlugin = (SpringPlugin) inQueueInfo.getSpringPlugin();
                                o = springPlugin.getBeanByClassName(cl);
                            } else {
                                o = cl.newInstance();
                            }

                            handlerTarget = o;

                            if (inQueueInfo.getRefCatalogId() != null) {
                                ((AbstractServiceChainBridge) o).setCatalogId(inQueueInfo.getRefCatalogId());
                            }

                        } catch (InstantiationException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            throw new RuntimeException("实例化错误" + serviceClassName);
                        } catch (IllegalAccessException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            throw new RuntimeException("实例化错误,不允许访问构造方法" + serviceClassName);
                        }


                    } catch (ClassNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        throw new RuntimeException("没有找到" + serviceClassName);
                    } catch (Exception e) {
                        throw new RuntimeException(StringUtil.getStackTrace(e));
                    }
                }

                for (int i = 0; i < handlerMaxNum; i++) {
                    Handler handler = new DefaultHandler();
                    DefaultHandler defaultHandler = (DefaultHandler) handler;
                    defaultHandler.setQueueName(queueName);
                    defaultHandler.setBufferManager(bufferManager);
                    defaultHandler.setContext(context);
                    defaultHandler.setServiceInstance(handlerTarget);
                    scheduler.execute(defaultHandler);
                }
            }

            isStartedHandle = true;
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

        CustomThreadNameThreadFactory(String threadNamePrefix, String queueName) {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            namePrefix = threadNamePrefix + "-" + queueName + "-" +
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


    public BufferManager getBufferManager() {
        return bufferManager;
    }

    public void setBufferManager(BufferManager bufferManager) {
        this.bufferManager = bufferManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public void setConfigManager(ConfigManager configManager) {
        this.configManager = configManager;
    }

    public static synchronized HandlerManager getInstance() {
        if (self == null) {
            self = new DefaultHandlerManager();
        }
        return self;
    }

    public XmlAppTransporterContext getContext() {
        return context;
    }

    public void setContext(XmlAppTransporterContext context) {
        this.context = context;
    }


}
