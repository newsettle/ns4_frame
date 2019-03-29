package com.creditease.ns.log.converter;

import com.creditease.ns.log.LogConstants;

import ch.qos.logback.classic.pattern.ClassOfCallerConverter;
import ch.qos.logback.classic.spi.CallerData;
import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * @ClassName: ClassNameAndLineNumberConverter
 * @Description: 得到实际输出日志的类和行号
 * @author dingzhiwei
 * @date 2015年11月6日 下午1:43:01
 */
public class ClassNameAndLineNumberConverter extends ClassOfCallerConverter {

	protected String getFullyQualifiedName(ILoggingEvent event) {
		StackTraceElement[] cda = event.getCallerData();
		if (cda != null && cda.length > 0) {
			String loggerName = event.getLoggerName();
			int index = loggerName.indexOf(LogConstants.SPLIT_CATEGORY);
			loggerName = index != -1 ? loggerName.substring(0, index) : loggerName;
			/*String className = cda[cda.length - 1].getClassName();*/
			return loggerName /*+ LogConstants.CONNECT_CATEGORY
					+ className.substring(className.lastIndexOf(".") + 1) + "."
					+ Integer.toString(cda[cda.length - 1].getLineNumber())*/;
		}
		return CallerData.NA;
	}

}
