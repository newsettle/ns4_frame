package com.creditease.ns.log;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.spi.LoggerContextListener;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.spi.LifeCycle;
import org.slf4j.LoggerFactory;

import java.net.URL;

public class MyLoggerListenerTest extends ContextAwareBase implements LoggerContextListener,LifeCycle{
	static org.slf4j.Logger logger = LoggerFactory.getLogger("test");
	public static void main(String[] args) 
	{
		URL url = Thread.currentThread().getContextClassLoader().getResource("joran_test.xml");
		
		//加载配置文件 然后获取logger 打印日志
		LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

		try {
			JoranConfigurator configurator = new JoranConfigurator();
			configurator.setContext(context);
			// Call context.reset() to clear any previous configuration, e.g. default 
			// configuration. For multi-step configuration, omit calling context.reset().
			context.reset(); 
			configurator.doConfigure(url.getPath());
			
//			logger.info("哈哈哈哈");
			
		} catch (JoranException je) {
			// StatusPrinter will handle this
			je.printStackTrace();
			return;
		}
		
		logger.info("哈哈哈哈哈哈||{}",1111);
		
		
	}

	@Override
	public boolean isResetResistant() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onStart(LoggerContext context) {
	
	}

	@Override
	public void onReset(LoggerContext context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStop(LoggerContext context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLevelChange(Logger logger, Level level) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isStarted() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void start() {
		System.out.println("加载配置");
		this.context.putProperty("ISUSEREDIS", "true和false");
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}


}
