package TestThreadLogger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import ch.qos.logback.classic.LoggerContext;

public class ThreadLogger {

	private static final String LOGGERNAMEKEY = "thread-logger-key-name";
	private static  Logger logger = LoggerFactory.getLogger(ThreadLogger.class); 

	private String fileName;


	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
		MDC.put(LOGGERNAMEKEY,fileName);
	}

	public ThreadLogger(String fileName)
	{
		this.fileName = fileName;
		MDC.put(LOGGERNAMEKEY,fileName);
	}
	
	public ThreadLogger()
	{
		
	}

	public void trace(String msg,Object... arguments)
	{
		logger.trace(msg,arguments);
	}

	public void debug(String msg, Object... arguments) {
		logger.debug(msg,arguments);
	}

	public void info(String msg, Object... arguments) {
		logger.info(msg,arguments);
		
		System.out.println("打印出了数据");
	}

	public void warn(String msg, Object... arguments) {
		logger.warn(msg,arguments);
	}

	public void error(String msg, Object... arguments) {
		logger.error(msg, arguments);
	}

}
