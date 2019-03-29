package com.creditease.ns.transporter.fetch;

/**
 * FetcherManager控制从MQ中获取消息的Fetcher
 * @author liuyang
 *2015年8月11日下午9:24:43
 */
public interface FetcherManager {
	public void startFetch();
	public void stopFetch();
}
