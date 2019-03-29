package com.creditease.framework.listener;

/**
 * 对于service层 异常的处理
 * 现在想到的处理方案有如下几种
 * 1.transporter捕获异常通知controller
 * 	这涉及到多线程的问题，如何根据不同的异常做出不同的行为？
 * 	1.自定义不同级别的异常
 * 	2.
 * 2.controller解析servicemessage判断是否发生异常
 * 3.在下个环节 解析判断异常
 * @author liuyang
 *2015年9月7日下午2:30:38
 */

public interface ExceptionListener {
	public void exceptionListen(ExceptionEvent event);
}
