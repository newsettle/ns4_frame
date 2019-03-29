package com.creditease.ns.mq.exception;


public class MQConnectionException extends MQException{
    public MQConnectionException() {
        super();
    }

    public MQConnectionException(String message) {
        super(message);
    }

    public MQConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public MQConnectionException(Throwable cause) {
        super(cause);
    }
}
