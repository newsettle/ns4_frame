package com.creditease.ns.log.thread;

import java.net.URL;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import TestThreadLogger.ThreadLogger;

public class TestThreadLogger extends Thread 
{
	private ThreadLogger threadLogger;
	
	public static void main(String[] args) throws InterruptedException 
	{
		URL url = Thread.currentThread().getContextClassLoader().getResource("logback_thread.xml");
		
		//加载配置文件 然后获取logger 打印日志
		LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

		try {
			JoranConfigurator configurator = new JoranConfigurator();
			configurator.setContext(context);
			// Call context.reset() to clear any previous configuration, e.g. default 
			// configuration. For multi-step configuration, omit calling context.reset().
			context.reset(); 
			configurator.doConfigure(url.getPath());
		} catch (JoranException je) {
			// StatusPrinter will handle this
			je.printStackTrace();
			return;
		}
		
		
		//文件名
		TestThreadLogger testThreadLogger =  new TestThreadLogger();
		testThreadLogger.start();
		
		
		TestThreadLogger testThreadLogger1 =  new TestThreadLogger();
		testThreadLogger1.start();
		
		
		TestThreadLogger testThreadLogger2 =  new TestThreadLogger();
		testThreadLogger2.start();
		
		testThreadLogger.join();
		testThreadLogger1.join();
		testThreadLogger2.join();
		
	}

	public void setLoggerFileName(String fileName)
	{
		if (threadLogger == null) 
		{
			threadLogger = new ThreadLogger(fileName);
			return;
		}
		
		threadLogger.setFileName(fileName);
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		setLoggerFileName(getName());
		threadLogger.info("aaaaaa"+Thread.currentThread().getName());
		Random random = new Random();
		
		try {
			sleep(random.nextInt(5000));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("完成"+Thread.currentThread().getName());
	}
	
}
