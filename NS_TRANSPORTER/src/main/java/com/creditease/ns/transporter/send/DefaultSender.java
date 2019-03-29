package com.creditease.ns.transporter.send;

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
import com.creditease.ns.transporter.stop.PoisonMessage;

public class DefaultSender implements Sender, Runnable {
    private String queueName;
    private BufferManager bufferManager;
    //发送给底层
    private static MQTemplate template = MQTemplates.defaultTemplate();
    private boolean isStop;
    private static NsLog frameLog = NsLog.getFramLog("Transport", "DefaultSender");
    public static NsLog flowLog = NsLog.getFlowLog("TransporterFlow", "DefaultSenderFlow");

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


    @Override
    public void send() {
        long startTime = System.currentTimeMillis();

        //转成底层对象
        ServiceMessage mqMessage = null;
        Header header = null;
        try {
            mqMessage = bufferManager.getFromSendBuffer(queueName, true);
            if (mqMessage instanceof PoisonMessage) {
                stop();
                //放回毒药，毒死其他handler
                bufferManager.putInSendBuffer(queueName, mqMessage);
                flowLog.info(" {} sender is stoped", Thread.currentThread().getName());
                return;
            }

            Message message = MessageConvertUtil.convertToMessage(mqMessage);
            header = message.getHeader();

            try {
                NotifyMonitorHelper.notifyMonitors(MonitorEvent.createEvent(MonitorEvent.Type.MESSAGE_SENDING,
                    message, queueName, System.currentTimeMillis()));
            } catch (Exception e) {
                frameLog.error("放入监控事件出错 {}", message, e);
            }

            startTime = System.currentTimeMillis();
            //需要reply 1是同步
            template.reply(message);
            flowLog.info("# 消息响应 成功 queuename:{} messageHeader:{} cost:{}ms #", queueName, header, System.currentTimeMillis() - startTime);

        } catch (InterruptedException e) {
            flowLog.error("# 准备发送消息时 线程中断 应该是线程被中断了 queuename:{} messageHeader:{} cost:{}ms #", queueName, header, System.currentTimeMillis() - startTime, e);
            /**当我们在关闭应用的时候，shutdownnow的时候线程interrupt
             此时buffermanager首先终止抛出interrupted异常，但是此时是在循环内部
             在抛出interrupted异常后 thread的interrupt的状态会被清理，导致应用不能顺畅停止
             所以在这里需要再次设置interrupted的状态使得循环可以停止
             */
            Thread.currentThread().interrupt();
        } catch (MQException e) {
            if (e instanceof MQArgumentException) {
                flowLog.error("# 准备发送消息时 失败 底层告知传入参数错误 queueName:{} cost:{}ms #", queueName, System.currentTimeMillis() - startTime, e);
                return;
            } else if (e instanceof MQConnectionException) {
                flowLog.error("# 准备发送消息时 失败 底层连接MQ错误 queueName:{} messageHeader:{} cost:{}ms #", queueName, header, System.currentTimeMillis() - startTime, e);
                //TODO 尝试记下来消息供以后处理或者通知异常处理模块
                return;
            } else if (e instanceof MQMessageFormatException) {
                flowLog.error("# 准备发送消息时 失败 底层告知消息格式出现问题 queueName:{} messageHeader:{} cost:{}ms #", queueName, header, System.currentTimeMillis() - startTime, e);
                //TODO 尝试记下来消息供以后处理或者通知异常处理模块
                return;
            } else {
                flowLog.error("# 准备发送消息时 失败 底层告知出现异常 queueName:{} messageHeader:{} cost:{}ms #", queueName, header, System.currentTimeMillis() - startTime, e);
                //TODO 尝试记下来消息供以后处理或者通知异常处理模块
                return;
            }
        } catch (Exception e) {
            flowLog.error("# 准备发送消息时 失败 queuename:{} servicemessage:{} messageHeader:{} cost:{}ms #", queueName, header, System.currentTimeMillis() - startTime, e);
            return;
        }

        NsLog.removeMsgId();
    }

    @Override
    public void run() {
        frameLog.info("# {} {} 开始运行", Thread.currentThread().getName(), this.queueName);

        while (!isStop && !Thread.interrupted()) {
            send();
        }
        stop();
        frameLog.info("{} is stoped ", Thread.currentThread().getName());
    }

    public void stop() {
        isStop = true;
    }


}
