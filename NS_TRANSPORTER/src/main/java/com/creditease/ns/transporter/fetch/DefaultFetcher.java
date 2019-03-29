package com.creditease.ns.transporter.fetch;

import com.creditease.framework.ext.plugin.MonitorEvent;
import com.creditease.framework.pojo.ServiceMessage;
import com.creditease.framework.util.MessageConvertUtil;
import com.creditease.ns.log.NsLog;
import com.creditease.ns.mq.MQTemplate;
import com.creditease.ns.mq.MQTemplates;
import com.creditease.ns.mq.exception.MQArgumentException;
import com.creditease.ns.mq.exception.MQConnectionException;
import com.creditease.ns.mq.exception.MQException;
import com.creditease.ns.mq.exception.MQMessageFormatException;
import com.creditease.ns.mq.model.Header;
import com.creditease.ns.mq.model.Message;
import com.creditease.ns.transporter.buffer.BufferManager;
import com.creditease.ns.transporter.plugin.NotifyMonitorHelper;

import java.io.UnsupportedEncodingException;

public class DefaultFetcher implements Fetcher, Runnable {

    private String queueName;
    private BufferManager bufferManager;
    private static MQTemplate template = MQTemplates.defaultTemplate();
    private boolean isStop;
    private static final int RETRY_NUM = 3;
    private static NsLog frameLog = NsLog.getFramLog("Transport", "DefaultFetcher");
    public static NsLog flowLog = NsLog.getFlowLog("TransporterFlow", "DefaultFetcherFlow");

    @Override
    public void run() {
        frameLog.info("{} {} 开始运行", Thread.currentThread().getName(), queueName);
        while (!isStop && !Thread.interrupted()) {
            fetch();
        }
        stop();
        frameLog.info("{} is stoped ", Thread.currentThread().getName());
    }

    @Override
    public void fetch() {
        //调用底层获取消息
        long startTime = System.currentTimeMillis();
        Message message = null;
        try {
            message = template.receive(queueName, 1);
            if (message == null) {
                return;
            }
            NsLog.setMsgId(message.getHeader().getMessageID());
        } catch (MQException e1) {
            if (e1 instanceof MQArgumentException) {
                flowLog.error("# 从MQ中接收消息 失败 传给MQ的参数错误 queueName:{} cost:{}ms #", queueName,
                    System.currentTimeMillis() - startTime, e1);
                return;
            } else if (e1 instanceof MQConnectionException) {
                flowLog.error("# 从MQ中接收消息 失败 连接MQ失败 queueName:{} cost:{}ms #", queueName,
                    System.currentTimeMillis() - startTime, e1);
                //TODO 尝试记下来消息供以后处理或者通知异常处理模块
                return;
            } else if (e1 instanceof MQMessageFormatException) {
                flowLog.error("# 从MQ中接收消息 失败 传给MQ的消息格式不符合规范 queueName:{} cost:{}ms #", queueName,
                    System.currentTimeMillis() - startTime, e1);
                //TODO 尝试记下来消息供以后处理或者通知异常处理模块
                return;
            }
        } catch (Throwable e2) {
            flowLog.error("# 从MQ中接收消息 失败 出现未知异常 queueName:{} cost:{}ms #", queueName,
                System.currentTimeMillis() - startTime, e2);
            return;
        }
        if (message == null) {
            flowLog.error("# 从MQ中接收消息 失败 没有返回消息 queueName:{} cost:{}ms #", queueName,
                System.currentTimeMillis() - startTime);
            return;
        }
        Header header = message.getHeader();
        flowLog.debug("header:{}", header);
        flowLog.info("# 从MQ中接收消息 OK queueName:{} messageHeader:{} cost:{}ms #", queueName, message.getHeader(),
            System.currentTimeMillis() - startTime);
        //将消息转化成业务消息对象
        ServiceMessage serviceMessage;
        try {
            serviceMessage = MessageConvertUtil.convertToServiceMessage(message);
            //放入消息到缓存中
            startTime = System.currentTimeMillis();
            try {
                NotifyMonitorHelper.notifyMonitors(MonitorEvent.createEvent(MonitorEvent.Type.MESSAGE_RECEIVED,
                    message, queueName, System.currentTimeMillis()));
            } catch (Exception e) {
                frameLog.error("放入监控事件出错 {}", message, e);
            }
            bufferManager.putInReceiveBuffer(queueName, serviceMessage);
        } catch (UnsupportedEncodingException e) {
            //TODO 出异常后考虑处理 比如放入一个队列中再次插入等
            flowLog.error("# 将消息放入接收缓存 失败 编码不支持 queueName:{} header:{} cost:{}ms #", queueName, header,
                System.currentTimeMillis() - startTime, e);

        } catch (Exception e) {
            flowLog.error("# 将消息放入接收缓存 失败 出现未知异常 queueName:{} header:{} cost:{}ms #", queueName, header,
                System.currentTimeMillis() - startTime, e);
        }
    }


    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public BufferManager getBufferManager() {
        return bufferManager;
    }

    public void setBufferManager(BufferManager bufferManager) {
        this.bufferManager = bufferManager;
    }

    public void stop() {
        this.isStop = true;
    }

}
