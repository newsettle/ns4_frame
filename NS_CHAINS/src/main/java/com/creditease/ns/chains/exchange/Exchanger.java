package com.creditease.ns.chains.exchange;

import java.util.Map;

import com.creditease.ns.chains.constants.LoggerConstants;
import com.creditease.ns.log.spi.LoggerWrapper;

public interface Exchanger {
	static LoggerWrapper loggerWrapper = LoggerConstants.EXCHANGE_LOGGER;
	static String logPrefix = "[Exchanger] ";
	public void setExchange(Object key,Object value);
	public Object getExchange(Object key);
	public void setOut(Object key,Object value);
	public Object getOut(Object key);
	public Object getParameter(String key);
	public Map getExchangeScope();
	public Map getOutScope();
}
