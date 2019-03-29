package com.creditease.ns.framework.spring;

public interface SpringPlugin {

	public Object getBean(String beanId);
	
	public Object getBeanByClassName(Class<?> className);
	
	public void init() throws Exception;
}
