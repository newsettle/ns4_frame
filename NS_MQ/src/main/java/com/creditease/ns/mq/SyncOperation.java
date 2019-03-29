package com.creditease.ns.mq;

import com.creditease.ns.mq.exception.MQException;
import com.creditease.ns.mq.model.Message;

/**
 * synchronous opersions for mq
 */
public interface SyncOperation {
    Message publish(String queueName, byte[] body) throws MQException;

    Message publish(String queueName, byte[] body, int timeout) throws MQException;

    Message publish(String queueName, byte[] body, String msgId, int timeout) throws MQException;

    Message publish(String queueName, Message message) throws MQException;

    Message publish(String queueName, Message message, int timeout) throws MQException;

    void reply(Message message) throws MQException;
}
