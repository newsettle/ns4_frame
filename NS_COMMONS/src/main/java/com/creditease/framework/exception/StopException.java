package com.creditease.framework.exception;


public class StopException extends NSException {
    public StopException() {
    }

    public StopException(String message) {
        super(message);
    }

    public StopException(String message, Throwable cause) {
        super(message, cause);
    }

    public StopException(Throwable cause) {
        super(cause);
    }

    public StopException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
