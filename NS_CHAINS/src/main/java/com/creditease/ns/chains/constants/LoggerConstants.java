package com.creditease.ns.chains.constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.creditease.ns.log.spi.LoggerWrapper;

public class LoggerConstants {
	//def日志 各个元素内部的日志
	public static LoggerWrapper DEF_LOGGER = LoggerWrapper.getLoggerWrapper(LoggerFactory.getLogger("chains.def"));
	//chain日志 各个chain执行的日志
	public static LoggerWrapper CHAIN_LOGGER = LoggerWrapper.getLoggerWrapper(LoggerFactory.getLogger("chains.chain"));
	//exchange日志 传递消息内部的日志
	public static LoggerWrapper EXCHANGE_LOGGER = LoggerWrapper.getLoggerWrapper(LoggerFactory.getLogger("chains.exchange"));
	//chain日志 系统自己的日志
	public static LoggerWrapper CHAINS_LOGGER = LoggerWrapper.getLoggerWrapper(LoggerFactory.getLogger("chains.system"));
	
	public static LoggerWrapper COMMAND_LOGGER = LoggerWrapper.getLoggerWrapper(LoggerFactory.getLogger("chains.command"));
}
