package com.creditease.ns.exception;

import com.creditease.framework.listener.ExceptionEvent;
import com.creditease.framework.listener.ExceptionListener;

public class ExceptionHandler implements ExceptionListener {

	
	@Override
	public void exceptionListen(ExceptionEvent event) {
		Throwable throwable = event.getException();
		
		if (throwable instanceof Exception) 
		{
			
		}
		else 
		{
			
		}
	}

}
