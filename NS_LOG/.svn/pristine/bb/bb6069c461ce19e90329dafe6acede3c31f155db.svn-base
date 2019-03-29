package com.creditease.ns.log;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import ch.qos.logback.core.rolling.TriggeringPolicy;
import ch.qos.logback.core.spi.AppenderAttachable;
import ch.qos.logback.core.util.Loader;
import com.creditease.ns.log.appender.NSLogRollingFileAppender;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Iterator;

/**
 * Date: 15-5-29
 * Time: 下午7:22
 * spring
 * <bean class="com.creditease.ns.log.LogSetting" factory-method="init">
 *   <property name="logPrefix" value="xxx" />
 *   <property name="logPath" value="xxx" />
 * </bean>
 */
public class LogSetting {
    /**
     * Log文件的前缀，当为null时，表示无前缀
     */
    private String logPrefix;
    /**
     * Log文件的输出的路径，为null时，会采用项目启动时的工作空间为路径
     */
    private String logPath;


    public LogSetting setAll(String logPrefix, String logPath) {
        this.logPrefix = logPrefix;
        this.logPath = logPath;
        return this;

    }

    public LogSetting setLogPrefix(String logPrefix) {
        this.logPrefix = logPrefix;
        return this;
    }

    public LogSetting setLogPath(String logPath) {
        this.logPath = logPath;
        return this;
    }

    public void init() {
        renameLoggingFile(logPrefix, logPath);
    }

    private void renameLoggingFile(String logPrefix, String logPath) {
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
            configurator.doConfigure(url.getFile());
        } catch (JoranException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        if (logPath == null) {
            logPath = System.getProperty("user.dir") + LogConstants.DEFAULT_LOG_DIR;
        }
        System.out.println("【NS_LOG】 logPath:" + logPath);
        if (logPrefix == null) {
            logPrefix = "";
        } else {
            logPrefix = logPrefix + "_";
        }
        System.out.println("【NS_LOG】 logPrefix:" + logPrefix);

        for (ch.qos.logback.classic.Logger logger : context.getLoggerList()) {
            Iterator<Appender<ILoggingEvent>> index = logger.iteratorForAppenders();
            while (index.hasNext()) {
                Appender<ILoggingEvent> appender = index.next();
                if (appender instanceof NSLogRollingFileAppender) {
                    NSLogRollingFileAppender fileAppender = ((NSLogRollingFileAppender) appender);
                    fileAppender.stop();
                    String fileName = fileAppender.getFile();
                    fileAppender.setFile(logPath + logPrefix + fileName);
                    System.out.println("【NS_LOG】 logFile:" + logPath + logPrefix + fileName);


                    TriggeringPolicy triggeringPolicy = fileAppender.getTriggeringPolicy();
                    if (triggeringPolicy instanceof TimeBasedRollingPolicy) {
                        TimeBasedRollingPolicy timeBasedRollingPolicy = ((TimeBasedRollingPolicy) triggeringPolicy);
                        String fileNamePattern = timeBasedRollingPolicy.getFileNamePattern();
                        timeBasedRollingPolicy.setFileNamePattern( logPath + logPrefix + fileNamePattern);
                        System.out.println("【NS_LOG】 logFile:" + logPath + logPrefix + fileNamePattern);
                        timeBasedRollingPolicy.stop();
                        timeBasedRollingPolicy.start();
                    }
                    fileAppender.realStart();
                } else if (appender.getName().equals("CoalescingStatistics") && appender instanceof AppenderAttachable) {
                    Iterator<Appender> appenderIterator = ((AppenderAttachable) appender).iteratorForAppenders();
                    while (appenderIterator.hasNext()) {
                        Appender attachAppender = appenderIterator.next();
                        if (attachAppender instanceof NSLogRollingFileAppender) {
                            NSLogRollingFileAppender fileAppender = ((NSLogRollingFileAppender) attachAppender);
                            fileAppender.stop();
                            String fileName = fileAppender.getFile();

                            fileAppender.setFile(logPath + logPrefix + fileName);
                            System.out.println("【NS_LOG】 logFile:" + logPath + logPrefix + fileName);


                            TriggeringPolicy triggeringPolicy = fileAppender.getTriggeringPolicy();
                            if (triggeringPolicy instanceof TimeBasedRollingPolicy) {
                                TimeBasedRollingPolicy timeBasedRollingPolicy = ((TimeBasedRollingPolicy) triggeringPolicy);
                                String fileNamePattern = timeBasedRollingPolicy.getFileNamePattern();
                                timeBasedRollingPolicy.setFileNamePattern( logPath + logPrefix + fileNamePattern);
                                System.out.println("【NS_LOG】 logFile:" + logPath + logPrefix + fileNamePattern);
                                timeBasedRollingPolicy.stop();
                                timeBasedRollingPolicy.start();
                            }
                            fileAppender.realStart();
                        }
                    }
                }
            }
        }
        context.start();
    }

    private URL getResource(String filename, ClassLoader myClassLoader) {
        URL url = Loader.getResource(filename, myClassLoader);
        return url;
    }
}
