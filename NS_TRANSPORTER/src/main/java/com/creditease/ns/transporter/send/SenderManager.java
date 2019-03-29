package com.creditease.ns.transporter.send;

/**
 * 主要用来对将消息发送到不同的渠道上 比如缓存，或者底层mq等
 * 的sender进行管理
 * @author liuyang
 *2015年8月11日下午9:33:00
 */
public interface SenderManager {
	void startSend();
	void stopFetch();
}
