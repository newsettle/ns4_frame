package com.creditease.ns.mq.exception;

/**
 * Message format exception
 */
public class MQMessageFormatException extends MQException{
    public MQMessageFormatException() {
        super();
    }

    public MQMessageFormatException(String message) {
        super(message);
    }

    public MQMessageFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public MQMessageFormatException(Throwable cause) {
        super(cause);
    }
}
