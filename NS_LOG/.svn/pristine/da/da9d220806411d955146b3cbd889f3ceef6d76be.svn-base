package com.creditease.ns.log;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import ch.qos.logback.core.rolling.TriggeringPolicy;
import ch.qos.logback.core.util.Loader;
import com.creditease.ns.log.appender.NSLogRollingFileAppender;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 
* @ClassName: NsLog 
* @Description: log实现 
* @author dingzhiwei
* @date 2015年11月5日 上午11:24:28 
*
 */
public class NsLog extends NsLogFace {
	
	static{
		renameLoggingFile();
	}
	
	private static final Map<String, NsLog> _pool = new HashMap<String, NsLog>();
    //----------
	public static synchronized Set<String> getLoggers() {
		return _pool.keySet();
	}
	public static synchronized void clearLoggers() {
	   _pool.clear();
	}
	//----------
	public static synchronized NsLog getLog(String logKey) {
    	NsLog log = _pool.get(logKey);
    	if (log==null) {
    		log = new NsLog();
    		log.setName(logKey);
    		_pool.put(logKey, log);
    	}
    	return log;
    }
    //----------
	
	public static NsLog getLog(String category, String moduleName, String mouleDesc) {
		if((null == moduleName || "".equals(moduleName.trim())) ||
				(null == mouleDesc || "".equals(mouleDesc.trim()))) return getLog(category);
		String logKey = category + LogConstants.SPLIT_CATEGORY + moduleName + LogConstants.SPLIT_MODULE + mouleDesc;
		return getLog(logKey);
	}	
	
    public static NsLog getFramLog() {
    	return getLog(LogConstants.CATEGORY_NS_FRAM);
    }
    
    public static NsLog getFlowLog() {
    	return getLog(LogConstants.CATEGORY_NS_FLOW);
    }
    
    public static NsLog getMqLog() {
    	return getLog(LogConstants.CATEGORY_NS_MQ);
    }
    
    public static NsLog getTaskLog() {
    	return getLog(LogConstants.CATEGORY_NS_TASK);
    }
    
    public static NsLog getBizLog() {
    	return getLog(LogConstants.CATEGORY_NS_BIZ);
    }	
    
    public static NsLog getFramLog(String moduleName, String moduleDesc) {
    	return getLog(LogConstants.CATEGORY_NS_FRAM, moduleName, moduleDesc);
    }
    
    public static NsLog getFlowLog(String moduleName, String moduleDesc) {
    	return getLog(LogConstants.CATEGORY_NS_FLOW, moduleName, moduleDesc);
    }
    
    public static NsLog getMqLog(String moduleName, String moduleDesc) {
    	return getLog(LogConstants.CATEGORY_NS_MQ, moduleName, moduleDesc);
    }
    
    public static NsLog getTaskLog(String moduleName, String moduleDesc) {
    	return getLog(LogConstants.CATEGORY_NS_TASK, moduleName, moduleDesc);
    }
    
    public static NsLog getBizLog(String moduleName, String moduleDesc) {
    	return getLog(LogConstants.CATEGORY_NS_BIZ, moduleName, moduleDesc);
    }  
   
    // ----------
	static void log(String category, String message, Object...args) {
		NsLog.getLog(category).info(message, args);
	}
	
    /**
     * 写入日志的主key
     *
     * @param primayKey 主key
     */
    /*public static void setPrimayKey(String primayKey) {
        MDC.put(LogConstants.LOG_PRIMARY_KEY, primayKey);
    }*/

    /**
     * 写入日志的副key
     *
     * @param subPrimaryKey 副key
     */
    public static void setSubPrimary(String subPrimaryKey) {
        MDC.put(LogConstants.LOG_SUB_PRIMARY_KEY, subPrimaryKey);
    }

    /**
     * 写入初始化的UUID
     */
    /*public static void setUniqKey() {
        MDC.put(LogConstants.UUID_KEY, LogUtils.getShortUUID());
    }*/
    
    public static void setMsgId(String msgId) {
    	 MDC.put(LogConstants.UUID_KEY, msgId);
    }
    
    public static void removeMsgId() {
    	MDC.remove(LogConstants.UUID_KEY);
    }
    
    private static void renameLoggingFile() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        context.stop();
        context.reset();
        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(context);
        try {
            URL url = getResource(LogConstants.AUTOCONFIG_FILE, Thread.currentThread().getContextClassLoader());
            if (url == null) {
                throw new RuntimeException("init Log error,can't find logback.xml");
            }
            
            
            System.out.println("初始化File:"+url.getFile());
            
            
            configurator.doConfigure(url.getFile());
        } catch (JoranException e) {
        	
        	//读取文件读不到 读取流
        	try {
				configurator.doConfigure(getResourceAsStream(LogConstants.AUTOCONFIG_FILE, Thread.currentThread().getContextClassLoader()));
			} catch (JoranException e1) {
				throw new RuntimeException(e.getMessage(), e1);
			}
        	
        }

        String configFile = System.getProperty("configfile");
        if(configFile == null || "".equals(configFile)) {
        	configFile = "";
        	//System.out.println("ns_log.configfile is null");
        }else {
        	configFile = configFile.indexOf(".") != -1 ? configFile.substring(0, configFile.lastIndexOf(".")) : configFile;
        	configFile += "_";
        	//System.out.println("ns_log.configfile is configFile");
        }
        
        for (ch.qos.logback.classic.Logger logger : context.getLoggerList()) {
            Iterator<Appender<ILoggingEvent>> index = logger.iteratorForAppenders();
            while (index.hasNext()) {
                Appender<ILoggingEvent> appender = index.next();
                if (appender instanceof NSLogRollingFileAppender) {
                    NSLogRollingFileAppender fileAppender = ((NSLogRollingFileAppender) appender);
                    fileAppender.stop();
                    String fileName = fileAppender.getFile().replace("configfile_", configFile);
                    fileAppender.setFile(fileName);
                    //System.out.println("ns_log.filenNme:" + fileName);

                    TriggeringPolicy triggeringPolicy = fileAppender.getTriggeringPolicy();
                    if (triggeringPolicy instanceof TimeBasedRollingPolicy) {
                        TimeBasedRollingPolicy timeBasedRollingPolicy = ((TimeBasedRollingPolicy) triggeringPolicy);
                        String fileNamePattern = timeBasedRollingPolicy.getFileNamePattern().replace("configfile_", configFile);
                        timeBasedRollingPolicy.setFileNamePattern(fileNamePattern);
                        //System.out.println("ns_log.fileNamePattern:" + fileNamePattern);
                        timeBasedRollingPolicy.stop();
                        timeBasedRollingPolicy.start();
                    }
                    fileAppender.realStart();
                }
            }
        }
        context.start();
    }
    
    private static URL getResource(String filename, ClassLoader myClassLoader) {
        URL url = Loader.getResource(filename, myClassLoader);
        return url;
    }
    
    private static InputStream getResourceAsStream(String filename,ClassLoader myClassLoader)
    {
    	return myClassLoader.getResourceAsStream(filename);
    }

}