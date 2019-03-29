package com.creditease.framework.exception;


public class NSException extends Exception {
    public NSException() {
    }

    public NSException(String message) {
        super(message);
    }

    public NSException(String message, Throwable cause) {
        super(message, cause);
    }

    public NSException(Throwable cause) {
        super(cause);
    }

    public NSException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
