package com.creditease.framework.ext.plugin;

import com.creditease.framework.pojo.ServiceMessage;
import com.creditease.framework.util.ProtoStuffSerializeUtil;
import com.creditease.ns.mq.exception.MQConnectionException;
import com.creditease.ns.mq.model.Message;

import java.io.IOException;

/**
 * Created by liuyang on 2019-02-23.
 *
 * @author liuyang
 */
public class MonitorEvent {
    private final Type eventType;
    private final Message message;
    private final ServiceMessage serviceMessage;
    private final String queueName;
    private final long timeHappend;

    public MonitorEvent(Type eventType, Message message, String queueName, long timeHappend) throws IOException, MQConnectionException, InstantiationException, IllegalAccessException {
        this(eventType, message, null, queueName, timeHappend);
    }

    public MonitorEvent(Type eventType, Message message) throws IOException, MQConnectionException,
        InstantiationException, IllegalAccessException {
        this(eventType, message, null, null, System.currentTimeMillis());
    }

    public MonitorEvent(Type eventType, ServiceMessage serviceMessage, String queueName, long timeHappend) throws IOException, MQConnectionException, InstantiationException, IllegalAccessException {
        this(eventType, null, serviceMessage, queueName, timeHappend);
    }

    public MonitorEvent(Type eventType, Message message, ServiceMessage serviceMessage, String queueName,
        long timeHappend) throws IOException, MQConnectionException, IllegalAccessException, InstantiationException {
        this.eventType = eventType;

        Message copyOfMessage = null;
        if (message != null) {
            copyOfMessage = new Message(message.toBytes());
        }
        this.message = copyOfMessage;
        ServiceMessage copyOfServiceMessage = null;
        if (serviceMessage != null) {
            byte[] smBytes = ProtoStuffSerializeUtil.serializeForCommon(serviceMessage);
            copyOfServiceMessage = (ServiceMessage) ProtoStuffSerializeUtil.unSerializeForCommon(smBytes);
        }

        this.serviceMessage = copyOfServiceMessage;
        this.queueName = queueName;
        this.timeHappend = timeHappend;
    }

    public static MonitorEvent createEvent(Type eventType, Message message) throws IOException, MQConnectionException, IllegalAccessException, InstantiationException {
        MonitorEvent monitorEvent = new MonitorEvent(eventType, message);
        return monitorEvent;
    }

    public static MonitorEvent createEvent(Type eventType, Message message, String queueName, long timeHappend) throws IOException, MQConnectionException, IllegalAccessException, InstantiationException {
        MonitorEvent monitorEvent = new MonitorEvent(eventType, message, queueName, timeHappend);
        return monitorEvent;
    }

    public static MonitorEvent createEvent(Type eventType, ServiceMessage serviceMessage, String queueName,
        long timeHappend) throws IOException, MQConnectionException, IllegalAccessException, InstantiationException {
        MonitorEvent monitorEvent = new MonitorEvent(eventType, serviceMessage, queueName, timeHappend);
        return monitorEvent;
    }

    public static MonitorEvent createEvent(Type eventType, Message message, ServiceMessage serviceMessage,
        String queueName, long timeHappend) throws IOException, MQConnectionException, InstantiationException,
        IllegalAccessException {
        MonitorEvent monitorEvent = new MonitorEvent(eventType, message, serviceMessage, queueName, timeHappend);
        return monitorEvent;
    }

    public enum Type {
        MESSAGE_RECEIVED(1),
        MESSAGE_HANDLING(2),
        MESSAGE_HANDLED(3),
        MESSAGE_SENDING(4);

        private int code;

        Type(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }

    public Type getEventType() {
        return eventType;
    }

    public Message getMessage() {
        return message;
    }

    public long getTimeHappend() {
        return timeHappend;
    }

    public String getQueueName() {
        return queueName;
    }

    public ServiceMessage getServiceMessage() {
        return serviceMessage;
    }


}
