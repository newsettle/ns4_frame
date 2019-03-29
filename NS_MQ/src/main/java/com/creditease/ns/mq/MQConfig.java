package com.creditease.ns.mq;


import com.creditease.ns.log.NsLog;
import com.creditease.ns.mq.utils.PropertiesHelper;

import java.util.Properties;

public class MQConfig {
    private static final NsLog initLog = NsLog.getMqLog("NS_MQ", "初始化");
    public static MQConfig getConfig = new MQConfig();
    private final String DEFAULT_MAXTOTAL = "50";
    private final String DEFAULT_MINIDLE = "1";
    private final String DEFAULT_MAXIDLE = "10";
    private final String DEFAULT_CONNECTIONTIMEOUT = "5000";
    private final String DEFAULT_SOCKETTIMEOUT = "5000";
    private final String DEFAULT_EXPIRED = "5000";
    private final String DEFAULT_QUEUE_PREFIX = "";
    private final String DEFAULT_MAXREDIRECTIONS = "5";
    //单点：1, 集群：2
    private final String REDIS_DEFUALT_TYPE = "1";
    private final String DEFAULT_PASSWORD = null;

    private int redisDefaultType;
    private String redisClusterHost;
    private int redisClusterMaxTotal;
    private int redisClusterMiniIdle;
    private int redisClusterMaxIdle;
    private int redisClusterConnectionTimeout;
    private int redisClusterMaxRedirections;
    private String redisClusterPassword;


    private String redisSingleHost;
    private int redisSingleMaxTotal;
    private int redisSingleMinIdle;
    private int redisSingleMaxIdle;
    private int redisSingleConnectionTimeout;
    private int redisSingleSocketTimeout;
    private int tempQueueExpired;
    private String redisSinglePassword;

    private String queuePrefix;

    public MQConfig() {
        Properties properties = PropertiesHelper.getMQProperteis("ns_mq.properties");
        
        if (properties == null) 
		{
        	properties = new Properties();
			properties.setProperty("redis.single.host", "127.0.0.1:6379");
		}
        
        redisDefaultType = Integer.parseInt(properties.getProperty("redis.type", REDIS_DEFUALT_TYPE));
        redisSingleHost = properties.getProperty("redis.single.host");
        redisSingleMaxTotal = Integer.parseInt(properties.getProperty("redis.single.maxTotal", DEFAULT_MAXTOTAL));
        redisSingleMinIdle = Integer.parseInt(properties.getProperty("redis.single.miniIdle", DEFAULT_MINIDLE));
        redisSingleMaxIdle = Integer.parseInt(properties.getProperty("redis.single.maxIdle", DEFAULT_MAXIDLE));
        redisSingleConnectionTimeout = Integer.parseInt(properties.getProperty("redis.single.connectionTimeout", DEFAULT_CONNECTIONTIMEOUT));
        redisSingleSocketTimeout = Integer.parseInt(properties.getProperty("redis.single.socketTimeout", DEFAULT_SOCKETTIMEOUT));
        redisSinglePassword = properties.getProperty("redis.single.password", DEFAULT_PASSWORD);

        redisClusterHost = properties.getProperty("redis.cluster.host");
        redisClusterMaxTotal = Integer.parseInt(properties.getProperty("redis.cluster.maxTotal", DEFAULT_MAXTOTAL));
        redisClusterMiniIdle = Integer.parseInt(properties.getProperty("redis.cluster.miniIdle", DEFAULT_MINIDLE));
        redisClusterMaxIdle = Integer.parseInt(properties.getProperty("redis.cluster.maxIdle", DEFAULT_MAXIDLE));
        redisClusterConnectionTimeout = Integer.parseInt(properties.getProperty("redis.cluster.connectionTimeout", DEFAULT_CONNECTIONTIMEOUT));
        redisClusterMaxRedirections = Integer.parseInt(properties.getProperty("redis.cluster.maxRedirections", DEFAULT_MAXREDIRECTIONS));
        redisClusterPassword = properties.getProperty("redis.cluster.password", DEFAULT_PASSWORD);

        tempQueueExpired = Integer.parseInt(properties.getProperty("redis.temp.queue.expired", DEFAULT_EXPIRED));
        queuePrefix = properties.getProperty("redis.queue.prefix", DEFAULT_QUEUE_PREFIX);

        //log
        if (redisDefaultType == 1) {
            initLog.info("redis服务器类型为:{}", "单机");
            initLog.info("redis单机服务器地址:{}", redisSingleHost);
            initLog.info("redis单机密码为:{}", redisSinglePassword);
            initLog.info("redis单机服务器最大连接数:{}", redisSingleMaxTotal);
            initLog.info("redis单机服务器最小空闲数:{}", redisSingleMinIdle);
            initLog.info("redis单机服务器最大空闲数:{}", redisSingleMaxIdle);
            initLog.info("redis单机连接超时时间:{}ms", redisSingleConnectionTimeout);
            initLog.info("redis单机socket超时时间:{}ms", redisSingleSocketTimeout);
        } else {
            initLog.info("redis服务器类型为:{}", "集群");
            initLog.info("redis集群服务器地址:{}", redisClusterHost);
            initLog.info("redis集群密码为:{}", redisClusterPassword);
            initLog.info("redis集群最大连接数:{}", redisClusterMaxTotal);
            initLog.info("redis集群最小空闲数:{}", redisClusterMiniIdle);
            initLog.info("redis集群最大空闲数:{}", redisClusterMaxIdle);
            initLog.info("redis集群连接时间和socket超时时间:{}", redisClusterConnectionTimeout);
            initLog.info("redis集群最大跳转次数:{}", redisClusterMaxRedirections);
        }


        initLog.info("redis临时队列过期时间:{}ms", tempQueueExpired);
        initLog.info("redis队列名前缀:{}", queuePrefix);
    }

    public String getRedisClusterHost() {
        return redisClusterHost;
    }

    public int getRedisClusterMaxTotal() {
        return redisClusterMaxTotal;
    }

    public int getRedisClusterMiniIdle() {
        return redisClusterMiniIdle;
    }

    public int getRedisClusterMaxIdle() {
        return redisClusterMaxIdle;
    }

    public int getRedisClusterMaxRedirections() {
        return redisClusterMaxRedirections;
    }

    public int getRedisClusterConnectionTimeout() {
        return redisClusterConnectionTimeout;
    }

    public String getRedisSingleHost() {
        return redisSingleHost;
    }

    public int getRedisSingleMaxTotal() {
        return redisSingleMaxTotal;
    }

    public int getRedisSingleConnectionTimeout() {
        return redisSingleConnectionTimeout;
    }

    public int getRedisSingleSocketTimeout() {
        return redisSingleSocketTimeout;
    }

    public int getRedisSingleMinIdle() {
        return redisSingleMinIdle;
    }

    public int getRedisSingleMaxIdle() {
        return redisSingleMaxIdle;
    }

    public int getTempQueueExpired() {
        return tempQueueExpired;
    }

    public String getQueuePrefix() {
        return queuePrefix;
    }

    public void setQueuePrefix(String queuePrefix) {
        this.queuePrefix = queuePrefix;
    }

    public int getRedisDefaultType() {
        return redisDefaultType;
    }

    public String getRedisClusterPassword() {
        return redisClusterPassword;
    }

    public String getRedisSinglePassword() {
        return redisSinglePassword;
    }
}
