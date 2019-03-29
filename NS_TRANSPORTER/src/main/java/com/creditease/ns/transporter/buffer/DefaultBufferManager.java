package com.creditease.ns.transporter.buffer;

import com.creditease.framework.pojo.ServiceMessage;
import com.creditease.ns.framework.startup.LifeCycle;
import com.creditease.ns.log.NsLog;
import com.creditease.ns.transporter.config.InQueueInfo;
import com.creditease.ns.transporter.fetch.DefaultFetcher;
import com.creditease.ns.transporter.handle.DefaultHandler;
import com.creditease.ns.transporter.send.DefaultSender;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class DefaultBufferManager implements LifeCycle, BufferManager {

    private Map<String, InQueueInfo> queueNameToInQueueInfo;
    private Map<String, BlockingQueue<ServiceMessage>> queueNameToReceiveBuffer;
    private Map<String, BlockingQueue<ServiceMessage>> queueNameToSendBuffer;

    private boolean isStarted = false;
    private static NsLog frameLog = NsLog.getFramLog("Transport", "DefaultBufferManager");
    private static NsLog handlerLog = DefaultHandler.flowLog;
    private static NsLog senderLog = DefaultSender.flowLog;
    private static NsLog fetchLog = DefaultFetcher.flowLog;

    private static BufferManager self;


    public void init() {
        self = this;
    }

    @Override
    public ServiceMessage getFromReceiveBuffer(String queueName, boolean isSync) throws InterruptedException, Exception {
        long startTime = System.currentTimeMillis();
        ServiceMessage mqMessage = null;
        handlerLog.debug("# 获取接收缓存中的消息对象 queueName:{} receiverbuffersize:{} cost:{}ms #", queueName, queueNameToReceiveBuffer.get(queueName).size(), System.currentTimeMillis() - startTime);
        try {
            if (isSync) {
                mqMessage = queueNameToReceiveBuffer.get(queueName).take();
            } else {
                mqMessage = queueNameToReceiveBuffer.get(queueName).poll();
            }
        } catch (InterruptedException e) {
            handlerLog.debug("# 获取接收缓存中对象 失败 线程中断 queueName:{} receiverbuffersize:{} cost:{}ms #", queueName, queueNameToReceiveBuffer.get(queueName).size(), System.currentTimeMillis() - startTime);
            throw e;
        }

        NsLog.setMsgId(mqMessage.getHeader().getMessageID());
        handlerLog.trace("# messageHeader:{} #", mqMessage.getHeader());
        handlerLog.info("# 获取接收缓存中的消息对象 OK queueName:{} receiverbuffersize:{} cost:{}ms #", queueName, queueNameToReceiveBuffer.get(queueName).size(), System.currentTimeMillis() - startTime);
        return mqMessage;
    }

    /**
     * 这里不需要考虑put的线程安全性，原因如下:
     * 1.线程不安全的情况下，最危险的情况可能会是放入缓存中消息的顺序，但是在我们这种场景下，消息顺序并不重要
     * 2.当前只有一个线程会进行进入队列的缓存存放
     * 3.底层是线程安全的
     */
    @Override
    public void putInReceiveBuffer(String queueName, ServiceMessage message) throws Exception {
        long startTime = System.currentTimeMillis();
        fetchLog.debug("# 将消息对象放入接收缓存中 queueName:{} receiverbuffersize:{} cost:{}ms #", queueName, queueNameToReceiveBuffer.get(queueName).size(), System.currentTimeMillis() - startTime);
        queueNameToReceiveBuffer.get(queueName).put(message);
        fetchLog.trace("# messageHeader:{} #", message.getHeader());
        fetchLog.info("# 将消息对象放入接收缓存中 OK queueName:{} receiverbuffersize:{} cost:{}ms #", queueName, queueNameToReceiveBuffer.get(queueName).size(), System.currentTimeMillis() - startTime);
        NsLog.removeMsgId();
    }

    @Override
    public synchronized void startUp() {
        if (!isStarted) {
            queueNameToReceiveBuffer = new ConcurrentHashMap<String, BlockingQueue<ServiceMessage>>();
            queueNameToSendBuffer = new ConcurrentHashMap<String, BlockingQueue<ServiceMessage>>();
            Iterator<String> it = queueNameToInQueueInfo.keySet().iterator();
            while (it.hasNext()) {
                String queueName = it.next();
                InQueueInfo inQueueInfo = queueNameToInQueueInfo.get(queueName);

                //构造queue对应的buffer
                BlockingQueue<ServiceMessage> inBuffer = new LinkedBlockingQueue<ServiceMessage>(inQueueInfo.getBufferSize());
                frameLog.debug("# 消息接受缓存初始化 queuename:{} size:{} #", queueName, inQueueInfo.getBufferSize());
                queueNameToReceiveBuffer.put(queueName, inBuffer);

                //TODO 需要sendBuffer也要可以设置buffersize
                BlockingQueue<ServiceMessage> sendBuffer = new LinkedBlockingQueue<ServiceMessage>(inQueueInfo.getBufferSize());
                frameLog.debug("# 消息发送缓存初始化 queuename:{} size:{} #", queueName, inQueueInfo.getBufferSize());
                queueNameToSendBuffer.put(queueName, sendBuffer);
            }
            isStarted = true;
        }

    }


    @Override
    public void destroy() {

    }

    public Map<String, InQueueInfo> getQueueNameToInQueueInfo() {
        return queueNameToInQueueInfo;
    }

    public void setQueueNameToInQueueInfo(
            Map<String, InQueueInfo> queueNameToInQueueInfo) {
        this.queueNameToInQueueInfo = Collections.unmodifiableMap(queueNameToInQueueInfo);
    }

    public Map<String, BlockingQueue<ServiceMessage>> getQueueNameToReceiveBuffer() {
        return queueNameToReceiveBuffer;
    }

    public void setQueueNameToReceiveBuffer(Map<String, BlockingQueue<ServiceMessage>> queueNameToBuffer) {
        this.queueNameToReceiveBuffer = queueNameToBuffer;
    }


    public Map<String, BlockingQueue<ServiceMessage>> getQueueNameToSendBuffer() {
        return queueNameToSendBuffer;
    }

    public void setQueueNameToSendBuffer(
            Map<String, BlockingQueue<ServiceMessage>> queueNameToSendBuffer) {
        this.queueNameToSendBuffer = queueNameToSendBuffer;
    }

    public static synchronized BufferManager getInstance() {
        if (self == null) {
            self = new DefaultBufferManager();
        }
        return self;
    }

    @Override
    public ServiceMessage getFromSendBuffer(String queueName, boolean isSync) throws Exception {
        long startTime = System.currentTimeMillis();
        ServiceMessage serviceMessage = null;
        try {
            if (isSync) {
                serviceMessage = ((BlockingQueue<ServiceMessage>) queueNameToSendBuffer.get(queueName)).take();
            } else {
                serviceMessage = ((BlockingQueue<ServiceMessage>) queueNameToSendBuffer.get(queueName)).poll();
            }
        } catch (InterruptedException e) {
            senderLog.debug("将消息对象从发送缓存中取出 失败 线程中断 queueName:{} sendbuffersize:{} cost:{}ms", queueName, queueNameToSendBuffer.get(queueName).size(), System.currentTimeMillis() - startTime);
            throw e;
        } catch (Exception e) {
            senderLog.debug("将消息对象从发送缓存中取出 失败 queueName:{} t:{} sendbuffersize:{} cost:{}ms", queueName, serviceMessage, queueNameToSendBuffer.get(queueName).size(), System.currentTimeMillis() - startTime, e);
            throw e;
        }
        if (serviceMessage.getHeader() == null) {
            throw new NullPointerException("ServiceMessage");
        }
        NsLog.setMsgId(serviceMessage.getHeader().getMessageID());
        senderLog.trace("# messageHeader:{} #", serviceMessage.getHeader());
        senderLog.info("将消息对象从发送缓存中取出 OK queueName:{} sendbuffersize:{} cost:{}ms", queueName, queueNameToSendBuffer.get(queueName).size(), System.currentTimeMillis() - startTime);
        return serviceMessage;
    }

    @Override
    public void putInSendBuffer(String queueName, ServiceMessage message)
            throws Exception {
        long startTime = System.currentTimeMillis();
        handlerLog.debug("# 将消息对象放入发送缓存 queueName:{} sendbuffersize:{} cost:{}ms #", queueName, queueNameToSendBuffer.get(queueName).size(), System.currentTimeMillis() - startTime);
        ((BlockingQueue<ServiceMessage>) queueNameToSendBuffer.get(queueName)).put(message);
        handlerLog.trace("# messageHeader:{} #", message.getHeader());
        handlerLog.info("# 将消息对象放入发送缓存 OK queueName:{} sendbuffersize:{} cost:{}ms #", queueName, queueNameToSendBuffer.get(queueName).size(), System.currentTimeMillis() - startTime);
        NsLog.removeMsgId();
    }

    @Override
    public long sizeOfReceiveBufferOf(String queueName) {
        return queueNameToReceiveBuffer.get(queueName).size();
    }

    @Override
    public long sizeOfSendBufferOf(String queueName) {
        return queueNameToSendBuffer.get(queueName).size();
    }

}
