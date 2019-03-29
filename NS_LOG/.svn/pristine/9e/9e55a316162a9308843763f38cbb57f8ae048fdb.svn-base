package com.creditease.ns.log.spi;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.creditease.ns.log.LogCode;
import com.creditease.ns.log.LogConstants;
import com.creditease.ns.log.LogUtils;

/**
 * 专门给ns_transportor使用的日志
 * @author liuyang
 *2015年8月11日下午8:15:11
 */
public class TransporterLog {
    private static Logger systemLog = LoggerFactory.getLogger("systemLog");
    private static Logger recordLog = LoggerFactory.getLogger("recordLog");

    public static void logSystemTrace(String msg,Object... arguments)
    {
    	systemLog.trace(msg,arguments);
    }
    
    public static void logSystemDebug(String msg, Object... arguments) {
        systemLog.debug(msg,arguments);
    }
    
    public static void logSystemInfo(String msg, Object... arguments) {
        systemLog.info(msg,arguments);
    }
    
    public static void logSystemWarn(String msg, Object... arguments) {
        systemLog.warn(msg,arguments);
    }

    public static void logSystemError(String msg, Object... arguments) {
    	systemLog.error(msg, arguments);
    }
    
    public static void logRecordTrace(String msg,Object... arguments)
    {
    	recordLog.trace(msg,arguments);
    }

    public static void logRecordDebug(String msg, Object... arguments) {
        recordLog.debug(msg,arguments);
    }
    
    public static void logRecordInfo(String msg, Object... arguments) {
        recordLog.info(msg,arguments);
    }
    
    public static void logRecordWarn(String msg, Object... arguments) {
        recordLog.warn(msg,arguments);
    }

    public static void logRecordError(String msg, Object... arguments) {
    	recordLog.error(msg, arguments);
    }





}
