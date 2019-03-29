package com.creditease.ns.mq;

/**
 * define the core of oprations like receive ,send and so on .
 */
public abstract class MQTemplate implements AsyncOperation, SyncOperation, SystemOperation {
	
	String queuePrefix = null;
	
	int tempQueueExpired = -1;
	
    public String getActiveQueueName(String requestQueueName) {
    	
    	String realQueueName = requestQueueName;
    	
    	if (queuePrefix != null) 
		{
    		realQueueName = queuePrefix + requestQueueName;
		}
    	else
    	{
    		realQueueName = MQConfig.getConfig.getQueuePrefix() + requestQueueName;
    	}
    	
    	
        return  realQueueName;
    }

	public String getQueuePrefix() {
		return queuePrefix;
	}

	public void setQueuePrefix(String queuePrefix) {
		this.queuePrefix = queuePrefix;
	}

	public int getTempQueueExpired() {
		return tempQueueExpired;
	}

	public void setTempQueueExpired(int tempQueueExpired) {
		this.tempQueueExpired = tempQueueExpired;
	}
    
}
