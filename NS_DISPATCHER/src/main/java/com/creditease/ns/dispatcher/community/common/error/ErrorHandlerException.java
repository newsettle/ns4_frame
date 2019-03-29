package com.creditease.ns.dispatcher.community.common.error;

public class ErrorHandlerException extends RuntimeException {
    public ErrorType errorType;

    public ErrorHandlerException(ErrorType errorType) {
        this.errorType = errorType;
    }

    public ErrorHandlerException(Throwable cause, ErrorType errorType) {
        super(cause);
        this.errorType = errorType;
    }

    public ErrorType getErrorType() {
        return errorType;
    }
}
