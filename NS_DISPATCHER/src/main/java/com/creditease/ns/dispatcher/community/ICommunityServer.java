package com.creditease.ns.dispatcher.community;

/**
 * 通信服务端的基础类，主要用于定义服务端的通用接口
 */
public interface ICommunityServer {
    /**
     * 开启通信服务
     */
    public void start();

    /**
     * 停止通信服务
     */
    public void stop();


}
