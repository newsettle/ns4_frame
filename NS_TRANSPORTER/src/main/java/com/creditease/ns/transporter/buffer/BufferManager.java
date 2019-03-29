package com.creditease.ns.transporter.buffer;

import com.creditease.framework.pojo.ServiceMessage;

/**
 * 主要用来管理Buffer
 * @author liuyang
 *2015年8月11日下午9:35:53
 */
public interface BufferManager {
	public ServiceMessage getFromReceiveBuffer(String queueName,boolean isSync) throws Exception;
	public  void putInReceiveBuffer(String queueName,ServiceMessage message) throws Exception;
	
	public ServiceMessage getFromSendBuffer(String queueName,boolean isSync) throws Exception;
	public  void putInSendBuffer(String queueName,ServiceMessage message) throws Exception;
	
	public long sizeOfReceiveBufferOf(String queueName);
	
	public long sizeOfSendBufferOf(String queueName);
	
}
