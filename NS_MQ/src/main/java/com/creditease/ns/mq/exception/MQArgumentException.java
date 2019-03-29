package com.creditease.ns.mq.exception;

/**
 * argument Exception
 */
public class MQArgumentException extends MQException{

    public MQArgumentException() {
        super();
    }

    public MQArgumentException(String message) {
        super(message);
    }

    public MQArgumentException(String message, Throwable cause) {
        super(message, cause);
    }

    public MQArgumentException(Throwable cause) {
        super(cause);
    }
}
