package com.creditease.ns.mq.model;

/**
 * 用于标识消息的发送的类型，
 * SYNC为同步发送或RPC发送，有响应结果
 * ASYNC为异步发送，无响应结果
 */
public enum  DeliveryMode {
    SYNC,ASYNC
}
