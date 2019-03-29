package com.creditease.ns.mq.redis;


import com.creditease.ns.mq.exception.MQRedisException;

public interface Redis {

    void lpush(String queueName, byte[] data) throws MQRedisException;

    void lpushWithExpired(String queueName,byte[] data,int second) throws MQRedisException;

    byte[] brpop(String queueName) throws MQRedisException;

    byte[] brpop(String queueName, int timeout) throws MQRedisException;

    byte[] rpop(String queueName) throws MQRedisException;

    void expired(String key, int expired) throws MQRedisException;

    String ping() throws MQRedisException;

}
