package com.creditease.ns.framework.startup;
/**
 * 我们认为LifeCycleManager是一个全程总的入口开关
 * 通过它我们开始启动初始化所有的项目组件
 * 也会通过它我们会释放所有的资源
 * @author liuyang
 *2015年8月11日下午8:31:13
 */
public interface LifeCycleManager {
	public void startUp();
	public void destroy();
}
