package com.creditease.ns.mq.redis;


import com.creditease.ns.mq.MQConfig;
import com.creditease.ns.mq.MQTemplate;
import com.creditease.ns.mq.exception.MQException;
import com.creditease.ns.mq.exception.MQInitError;
import com.creditease.ns.mq.utils.PropertiesHelper;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.JedisPool;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

public class SingleRedisBuilder {
	// 最大连接数
	private int maxTotal = -1;
	// 最小空闲数
	private int miniIdle = -1;
	// 最大空闲数
	private int maxIdle = -1;
	// 连接超时时间
	private int connectionTimeout = -1;
	// 读取超时时间
	private int socketTimeout = -1;
	// 主机名和端口号
	private String hostAndPort;
	// 密码
	private String password;

    protected SingleRedisBuilder() {
    }

    public static SingleRedisBuilder create() {
        return new SingleRedisBuilder();
    }

    public int getMaxTotal() {
        return maxTotal;
    }

    public SingleRedisBuilder setMaxTotal(int maxTotal) {
        this.maxTotal = maxTotal;
        return this;
    }

    public int getMiniIdle() {
        return miniIdle;
    }

    public SingleRedisBuilder setMiniIdle(int miniIdle) {
        this.miniIdle = miniIdle;
        return this;
    }

    public int getMaxIdle() {
        return maxIdle;
    }

    public SingleRedisBuilder setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
        return this;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public SingleRedisBuilder setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
        return this;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public SingleRedisBuilder setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
        return this;
    }

    public String getHostAndPort() {
        return hostAndPort;
    }

    public SingleRedisBuilder setHostAndPort(String hostAndPort) {
        this.hostAndPort = hostAndPort;
        return this;
    }

	public String getPassword() {
		return password;
	}

	public SingleRedisBuilder setPassword(String password) {
		this.password = password;
		return this;
	}

	public RedisMQTemplate build() {
		if (this.maxIdle == -1) {
			this.maxTotal = MQConfig.getConfig.getRedisSingleMaxTotal();
			if (this.maxTotal == -1) {
				throw new IllegalArgumentException("SingleRedisBuilder was not set maxTotal value");
			}
		}

        if (this.connectionTimeout == -1) {
            this.connectionTimeout = MQConfig.getConfig.getRedisSingleConnectionTimeout();
        }

        if (this.socketTimeout == -1) {
            this.socketTimeout = MQConfig.getConfig.getRedisSingleSocketTimeout();
        }

        if (this.miniIdle == -1) {
            this.miniIdle = MQConfig.getConfig.getRedisSingleMinIdle();
        }

        if (this.maxIdle == -1) {
            this.maxIdle = MQConfig.getConfig.getRedisSingleMaxIdle();
        }

        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMaxTotal(maxTotal);
        poolConfig.setMinIdle(miniIdle);
        poolConfig.setMaxIdle(maxIdle);


        if (this.hostAndPort == null) {
            this.hostAndPort = MQConfig.getConfig.getRedisSingleHost();
            if (this.hostAndPort == null) {
                throw new IllegalArgumentException("SingleRedisBuilder was not set host value");
            }
        }

		if (this.password == null) {
			this.password = MQConfig.getConfig.getRedisSinglePassword();
			if ("".equals(this.password)) {
				password = null;
			}
		}

		String[] hostPort = this.hostAndPort.split(":");
		JedisPool jp = null;
		if (hostPort.length == 2) {

			jp = new JedisPool(poolConfig, hostPort[0], Integer.parseInt(hostPort[1]), connectionTimeout,
					socketTimeout, password, 0, null, false, null, null, null);
		}
		RedisMQTemplate client = new RedisMQTemplate();
		client.setRedis(new SingleRedis(jp));
		try {
			boolean result = client.ping();
			if (result) {
				throw new MQInitError("redis服务不可用 hostAndPort:" + hostAndPort);
			}
		} catch (MQException e) {
			e.printStackTrace();
			throw new MQInitError("redis服务不可用 hostAndPort:" + hostAndPort);
		}
		return client;
	}
}
