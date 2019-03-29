package com.creditease.ns.transporter.handle;


import com.creditease.framework.exception.NSException;
import com.creditease.framework.exception.StopException;
import com.creditease.framework.ext.plugin.MonitorEvent;
import com.creditease.framework.pojo.ServiceMessage;
import com.creditease.framework.scope.SystemOutKey;
import com.creditease.framework.util.ExceptionUtil;
import com.creditease.framework.work.Worker;
import com.creditease.ns.log.NsLog;
import com.creditease.ns.mq.model.Header;
import com.creditease.ns.transporter.buffer.BufferManager;
import com.creditease.ns.transporter.context.XmlAppTransporterContext;
import com.creditease.ns.transporter.plugin.NotifyMonitorHelper;
import com.creditease.ns.transporter.result.TransporterReturnInfo;
import com.creditease.ns.transporter.stop.PoisonMessage;

import java.lang.reflect.Method;

public class DefaultHandler implements Handler, Runnable {

    private String queueName;
    private BufferManager bufferManager;
    private Object serviceInstance;
    private Method beforeMessageHandleMethod = null;
    private Method messageHandleMethod = null;
    private Method afterMessageHandleMethod = null;
    private boolean isStop;
    private XmlAppTransporterContext context;
    private static NsLog frameLog = NsLog.getFramLog("Transport", "DefaultHandler");
    public static NsLog flowLog = NsLog.getFlowLog("TransporterFlow", "DefaultHandlerFlow");


    @Override
    public void run() {
        frameLog.debug("{} {} 开始运行", Thread.currentThread().getName(), queueName);
        while (!isStop && !Thread.interrupted()) {
            handle();
            NsLog.removeMsgId();
        }
    }

    //TODO  需要检查下配置的serviceclass是否具有messageHandle注解
    @Override
    public void handle() {
        long startTime = System.currentTimeMillis();
        ServiceMessage serviceMessage = null;
        Header header = null;
        //从缓存中拿到消息 交给指定的service进行处理
        //如果线程处于中断状态，那么需要作如下几件事情
        //如果接受缓存中还有消息，那么全部拿出来然后调用处理 不采用中断的take了
        //清理状态 设置isStop


        try {
            serviceMessage = bufferManager.getFromReceiveBuffer(queueName, true);
            header = serviceMessage.getHeader();
            if (serviceMessage instanceof PoisonMessage) {
                stop();
                //放回毒药，毒死其他handler
                bufferManager.putInReceiveBuffer(queueName, serviceMessage);
                flowLog.info(" {} handle is stoped", Thread.currentThread().getName());
                return;
            }
            try {
                NotifyMonitorHelper.notifyMonitors(MonitorEvent.createEvent(MonitorEvent.Type.MESSAGE_HANDLING,
                    serviceMessage, queueName, System.currentTimeMillis()));
            } catch (Exception e) {
                frameLog.error("放入监控事件出错 {}", serviceMessage, e);
            }
            startTime = System.currentTimeMillis();
            flowLog.debug("# 业务层方法调用 queuename:{} {} cost:{}ms #", queueName, serviceMessage, System.currentTimeMillis() - startTime);
            ((Worker) serviceInstance).doWork(serviceMessage);
            flowLog.info("# 业务层方法调用 OK queuename:{} messageHeader:{} cost:{}ms #", queueName, header, System.currentTimeMillis() - startTime);
            try {
                NotifyMonitorHelper.notifyMonitors(MonitorEvent.createEvent(MonitorEvent.Type.MESSAGE_HANDLED,
                    serviceMessage, queueName, System.currentTimeMillis()));
            } catch (Exception e) {
                frameLog.error("放入监控事件出错 {}", serviceMessage, e);
            }
        } catch (InterruptedException e) {
            flowLog.error("# 业务层方法调用 线程中断 queuename:{} messageHeader:{} cost:{}ms #", queueName, header, System.currentTimeMillis() - startTime, e);
            /**当我们在关闭应用的时候，shutdownnow的时候线程interrupt
             此时buffermanager首先终止抛出interrupted异常，但是此时是在循环内部
             在抛出interrupted异常后 thread的interrupt的状态会被清理，导致应用不能顺畅停止
             所以在这里需要再次设置interrupted的状态使得循环可以停止
             */
            Thread.currentThread().interrupt();
        } catch (IllegalArgumentException e) {
            flowLog.error("# 业务层方法调用 失败 queuename:{} messageHeader:{} cost:{}ms #", queueName, header, System.currentTimeMillis() - startTime, e);
            sendException(header, e);
        } catch (StopException e) {
            flowLog.error("# 业务层方法调用 失败 queuename:{} messageHeader:{} cost:{}ms #", queueName, header, System.currentTimeMillis() - startTime, e);
            sendException(header, e);
            setStopFlag(header);
        } catch (NSException e) {
            flowLog.error("# 业务层方法调用 失败 queuename:{} messageHeader:{} cost:{}ms #", queueName, header, System.currentTimeMillis() - startTime, e);
            sendException(header, e);
            if (serviceMessage != null) {
                try {
                    String outMessage = serviceMessage.getOut(SystemOutKey.RETURN_CODE);
                    if (outMessage == null || outMessage.trim().length() < 1) {
                        serviceMessage.setOut(SystemOutKey.RETURN_CODE, TransporterReturnInfo.UNKNOWN_ERROR);
                    }
                } catch (NSException e1) {
                    e1.printStackTrace();
                }
            }

        } catch (Exception e) {
            flowLog.error("# 业务层方法调用 失败 queuename:{} messageHeader:{} cost:{}ms #", queueName, header, System.currentTimeMillis() - startTime, e);
            sendException(header, e);
            setStopFlag(header);
            if (serviceMessage != null) {
                try {
                    serviceMessage.setOut(SystemOutKey.RETURN_CODE, TransporterReturnInfo.UNKNOWN_ERROR);
                } catch (NSException e1) {
                    e1.printStackTrace();
                }
            }
        } catch (Throwable e2) {
            flowLog.error("# 业务层方法调用 失败 queuename:{} messageHeader:{} cost:{}ms #", queueName, header, System.currentTimeMillis() - startTime, e2);
            setStopFlag(header);
            sendException(header, e2);
            if (serviceMessage != null) {
                try {
                    serviceMessage.setOut(SystemOutKey.RETURN_CODE, TransporterReturnInfo.UNKNOWN_ERROR.toString());
                } catch (NSException e1) {
                    e1.printStackTrace();
                }
            }
        } finally {
            if (serviceMessage != null) {
                try {
                    //如果是异步消息 不放入缓存
                    if (header.getDeliveryMode() == 1) {
                        bufferManager.putInSendBuffer(queueName, serviceMessage);
                    }
                } catch (Exception e) {
                    flowLog.error("# 将serviceMessage放入发送缓存 失败 最终消息没有放入发送缓存  queuename:{} messageHeader:{} cost:{}ms #", queueName, header, System.currentTimeMillis() - startTime, e);
                }
            } else {
                flowLog.error("# 将serviceMessage放入发送缓存 失败 最终消息没有放入发送缓存 因为没有拿到serviceMessage queuename:{} messageHeader:{} cost:{}ms #", queueName, header, System.currentTimeMillis() - startTime);
            }
        }
    }

    private void sendException(Header header, Throwable e) {
//		ExceptionListener exceptionListener = context.getExceptionListener(queueName);
//		if (exceptionListener != null) 
//		{
//			ExceptionEvent event = new ExceptionEvent();
//			event.setException(e);
//			exceptionListener.exceptionListen(event);
//		}
        if (header != null) {
            header.setExceptionContent(ExceptionUtil.getStackTrace(e));
        } else {
            flowLog.error("# 将serviceMessage放入发送缓存 记录异常信息 失败 没有得到header queuename:{} messageHeader:{} #", queueName, header, e);
        }

    }

    private void setStopFlag(Header header) {
        if (header != null)
            header.setStop();
        else {
            flowLog.error("# 将serviceMessage放入发送缓存 设置记录标志 失败 没有得到header queuename:{} messageHeader:{} #", queueName, header);
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


    public Object getServiceInstance() {
        return serviceInstance;
    }

    public void setServiceInstance(Object serviceInstance) {
        this.serviceInstance = serviceInstance;
    }

    public Method getBeforeMessageHandleMethod() {
        return beforeMessageHandleMethod;
    }

    public void setBeforeMessageHandleMethod(Method beforeMessageHandleMethod) {
        this.beforeMessageHandleMethod = beforeMessageHandleMethod;
    }

    public Method getMessageHandleMethod() {
        return messageHandleMethod;
    }

    public void setMessageHandleMethod(Method messageHandleMethod) {
        this.messageHandleMethod = messageHandleMethod;
    }

    public Method getAfterMessageHandleMethod() {
        return afterMessageHandleMethod;
    }

    public void setAfterMessageHandleMethod(Method afterMessageHandleMethod) {
        this.afterMessageHandleMethod = afterMessageHandleMethod;
    }


    public void stop() {
        isStop = true;
    }

    public XmlAppTransporterContext getContext() {
        return context;
    }

    public void setContext(XmlAppTransporterContext context) {
        this.context = context;
    }

    public void readyShutdown() {
    }
}
