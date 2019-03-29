package com.creditease.framework.exception;


public class NotStopException extends NSException {
    public NotStopException() {
    }

    public NotStopException(String message) {
        super(message);
    }

    public NotStopException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotStopException(Throwable cause) {
        super(cause);
    }

    public NotStopException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
