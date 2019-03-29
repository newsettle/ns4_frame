package com.creditease.ns.transporter.config;

import com.creditease.ns.framework.spring.SpringPlugin;

public class InQueueInfo {
	private String queueName;
	private int bufferSize;
	private int handlerNum;
	private String serviceClassName;
	private String exceptionListenerClassName;
	private SpringPlugin SpringPlugin;
	private String refCatalogId;
	private int fetcherNum;
	private int senderNum;
	
	public String getQueueName() {
		return queueName;
	}
	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}
	public int getBufferSize() {
		return bufferSize;
	}
	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}
	public int getHandlerNum() {
		return handlerNum;
	}
	public void setHandlerNum(int handlerNum) {
		this.handlerNum = handlerNum;
	}
	public String getServiceClassName() {
		return serviceClassName;
	}
	public void setServiceClassName(String serviceClassName) {
		this.serviceClassName = serviceClassName;
	}
	public String getExceptionListenerClassName() {
		return exceptionListenerClassName;
	}
	public void setExceptionListenerClassName(String exceptionListenerClassName) {
		this.exceptionListenerClassName = exceptionListenerClassName;
	}
	public SpringPlugin getSpringPlugin() {
		return SpringPlugin;
	}
	public void setSpringPlugin(SpringPlugin springPlugin) {
		SpringPlugin = springPlugin;
	}
	public String getRefCatalogId() {
		return refCatalogId;
	}
	public void setRefCatalogId(String refCatalogId) {
		this.refCatalogId = refCatalogId;
	}
	public int getFetcherNum() {
		return fetcherNum;
	}
	public void setFetcherNum(int fetcherNum) {
		this.fetcherNum = fetcherNum;
	}
	public int getSenderNum() {
		return senderNum;
	}
	public void setSenderNum(int senderNum) {
		this.senderNum = senderNum;
	}
	
}	
