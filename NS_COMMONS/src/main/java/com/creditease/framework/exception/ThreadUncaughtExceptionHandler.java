package com.creditease.framework.exception;

import java.lang.Thread.UncaughtExceptionHandler;

public class ThreadUncaughtExceptionHandler implements UncaughtExceptionHandler {

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		System.out.println("[线程可能出错] [当前运行的线程:"+Thread.currentThread().getName()+"] [出错线程:"+t.getName()+"]");
		e.printStackTrace();
	}

}
