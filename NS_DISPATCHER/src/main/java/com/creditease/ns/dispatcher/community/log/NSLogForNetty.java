package com.creditease.ns.dispatcher.community.log;

import com.creditease.ns.log.NsLog;
import io.netty.util.internal.logging.AbstractInternalLogger;


class NSLogForNetty extends AbstractInternalLogger {

    private static final long serialVersionUID = 108038972685130825L;

    private final transient NsLog logger;

    NSLogForNetty() {
        super("Netty框架");
        logger = NsLog.getFramLog("Dispatcher", "Netty框架");
    }

    @Override
    public boolean isTraceEnabled() {
        return logger.isDebugEnabled();
    }

    @Override
    public void trace(String msg) {
        logger.debug(msg);
    }

    @Override
    public void trace(String format, Object arg) {
        logger.debug(format, arg);
    }

    @Override
    public void trace(String format, Object argA, Object argB) {
        logger.debug(format, argA, argB);
    }

    @Override
    public void trace(String format, Object... argArray) {
        logger.debug(format, argArray);
    }

    @Override
    public void trace(String msg, Throwable t) {
        logger.debug(msg, t);
    }

    @Override
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    @Override
    public void debug(String msg) {
        logger.debug(msg);
    }

    @Override
    public void debug(String format, Object arg) {
        logger.debug(format, arg);
    }

    @Override
    public void debug(String format, Object argA, Object argB) {
        logger.debug(format, argA, argB);
    }

    @Override
    public void debug(String format, Object... argArray) {
        logger.debug(format, argArray);
    }

    @Override
    public void debug(String msg, Throwable t) {
        logger.debug(msg, t);
    }

    @Override
    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    @Override
    public void info(String msg) {
        logger.info(msg);
    }

    @Override
    public void info(String format, Object arg) {
        logger.info(format, arg);
    }

    @Override
    public void info(String format, Object argA, Object argB) {
        logger.info(format, argA, argB);
    }

    @Override
    public void info(String format, Object... argArray) {
        logger.info(format, argArray);
    }

    @Override
    public void info(String msg, Throwable t) {
        logger.info(msg, t);
    }

    @Override
    public boolean isWarnEnabled() {
        return logger.isWarnEnabled();
    }

    @Override
    public void warn(String msg) {
        logger.warn(msg);
    }

    @Override
    public void warn(String format, Object arg) {
        logger.warn(format, arg);
    }

    @Override
    public void warn(String format, Object... argArray) {
        logger.warn(format, argArray);
    }

    @Override
    public void warn(String format, Object argA, Object argB) {
        logger.warn(format, argA, argB);
    }

    @Override
    public void warn(String msg, Throwable t) {
        logger.warn(msg, t);
    }

    @Override
    public boolean isErrorEnabled() {
        return logger.isErrorEnabled();
    }

    @Override
    public void error(String msg) {
        logger.error(msg);
    }

    @Override
    public void error(String format, Object arg) {
        logger.error(format, arg);
    }

    @Override
    public void error(String format, Object argA, Object argB) {
        logger.error(format, argA, argB);
    }

    @Override
    public void error(String format, Object... argArray) {
        logger.error(format, argArray);
    }

    @Override
    public void error(String msg, Throwable t) {
        logger.error(msg, t);
    }
}
