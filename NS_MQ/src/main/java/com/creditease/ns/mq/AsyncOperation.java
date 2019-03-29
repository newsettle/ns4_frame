package com.creditease.ns.mq;

import com.creditease.ns.mq.exception.MQException;
import com.creditease.ns.mq.model.Message;

/**
 * asynchronous opertions for mq
 */
public interface AsyncOperation {
    void send(String queueName, byte[] body) throws MQException;
    void send(String queueName, String msgId,byte[] body) throws MQException;
    void send(String queueName, Message message) throws MQException;

    Message receive(String queueName) throws MQException;

    Message receive(String queueName, int timeout) throws MQException;

    Message receiveNoneBlock(String queueName) throws MQException;
}
