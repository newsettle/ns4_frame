package com.creditease.framework.listener;

public class ExceptionEvent implements Event{
	private Throwable exception;

	public Throwable getException() {
		return exception;
	}

	public void setException(Throwable exception) {
		this.exception = exception;
	}
	
	
}
