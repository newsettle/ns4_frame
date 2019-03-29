package com.creditease.ns.mq;

import com.creditease.ns.mq.exception.MQException;

/**
 * some System Operation for mq
 */
public interface SystemOperation {
    boolean ping() throws MQException;
}
