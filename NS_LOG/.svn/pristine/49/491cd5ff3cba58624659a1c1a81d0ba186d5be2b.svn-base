package com.creditease.ns.log.spi;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;

public class LoggerWrapper {
	
	private static Map<String,LoggerWrapper> loggerPool = new ConcurrentHashMap<String, LoggerWrapper>();
	private Logger logger;
	public static LoggerWrapper getLoggerWrapper(Logger logger)
	{
		LoggerWrapper loggerWrapper = loggerPool.get(logger.getName());
		if(loggerWrapper == null)
		{
			loggerWrapper = new LoggerWrapper(logger);
			loggerPool.put(logger.getName(),loggerWrapper);
		}
		return loggerWrapper;
	}
	
	private LoggerWrapper(Logger logger)
	{
		this.logger = logger;
	}
	
	public Logger getLogger() {
		return logger;
	}
	
	
    public void logTrace(String msg,Object... arguments)
    {
    	this.logger.trace(msg,arguments);
    }
    
    public void logDebug(String msg, Object... arguments) {
        this.logger.debug(msg,arguments);
    }
    
    public void logInfo(String msg, Object... arguments) {
        this.logger.info(msg,arguments);
    }
    
    public void logWarn(String msg, Object... arguments) {
        this.logger.warn(msg,arguments);
    }

    public void logError(String msg, Object... arguments) {
    	this.logger.error(msg, arguments);
    }
}
