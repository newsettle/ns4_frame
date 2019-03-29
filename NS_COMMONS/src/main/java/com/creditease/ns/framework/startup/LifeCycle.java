package com.creditease.ns.framework.startup;

public interface LifeCycle {
	public void startUp() throws Exception;
	public void destroy() throws Exception;
}
