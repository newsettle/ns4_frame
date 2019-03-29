package com.creditease.ns.chains.exchange;

import java.util.HashMap;
import java.util.Map;

import com.creditease.ns.log.NsLog;

public class DefaultExchanger implements Exchanger{
	private static NsLog flowLog = NsLog.getFlowLog("链内部传递对象问题查看", "主要用作调试");
	private Map<String,Object> requestScope;
	private Map<Object,Object> exchangeScope;
	private Map<Object,Object> outScope;
	
	
	public void setExchange(Object key, Object value) {
		exchangeScope.put(key, value);
		flowLog.trace(logPrefix+"[放入交换域] [成功] [key:{}] [value:{}] [{}] [{}]", key,value,exchangeScope.size(),exchangeScope);
	}

	public Object getExchange(Object key) {
		Object value = exchangeScope.get(key);
		flowLog.trace(logPrefix+"[得到交换域值] [成功] [key:{}] [value:{}] [{}]", key,value,exchangeScope.size(),exchangeScope);
		return value;
		
	}

	public void setOut(Object key, Object value) {
		outScope.put(key, value);
		flowLog.trace(logPrefix+"[放入输出域] [成功] [key:{}] [value:{}] [{}] [{}]", key,value,outScope.size(),outScope);
	}

	public Object getOut(Object key) {
		Object value = outScope.get(key);
		flowLog.trace(logPrefix+"[得到输出域值] [成功] [key:{}] [value:{}] [{}] [{}]", key,value,outScope.size(),outScope);
		return value;
				
	}
	
	public DefaultExchanger(Map<String,Object> requestScope)
	{
		this.requestScope = requestScope;
		this.exchangeScope = new HashMap<Object,Object>();
		this.outScope = new HashMap<Object,Object>();
	}

	public Object getParameter(String key) {
		Object value = requestScope.get(key);
		flowLog.trace(logPrefix+"[得到输出域值] [成功] [key:{}] [value:{}] [{}] [{}]", key,value,requestScope.size(),requestScope);
		return value;
	}

	public Map getExchangeScope() {
		return exchangeScope;
	}

	public Map getOutScope() {
		return outScope;
	}
	
	public String toString()
	{
		return "["+requestScope+"] ["+exchangeScope+"] ["+outScope+"]";
	}
}
