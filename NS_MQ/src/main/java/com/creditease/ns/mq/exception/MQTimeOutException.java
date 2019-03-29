package com.creditease.ns.mq.exception;

/**
 *
 */
public class MQTimeOutException extends MQException {
    public MQTimeOutException() {
    }

    public MQTimeOutException(String message) {
        super(message);
    }

    public MQTimeOutException(String message, Throwable cause) {
        super(message, cause);
    }

    public MQTimeOutException(Throwable cause) {
        super(cause);
    }
}
