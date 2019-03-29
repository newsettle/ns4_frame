package com.creditease.ns.mq.redis;


import com.creditease.ns.mq.MQConfig;
import com.creditease.ns.mq.exception.MQException;
import com.creditease.ns.mq.exception.MQInitError;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.HostAndPort;

import java.util.HashSet;
import java.util.Set;

public class ClusterRedisBuilder {
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
	// 集群跳转次数
	private int maxRedirections = -1;
	// 主机名和端口号
	private String hostAndPorts;
	// 密码
	private String password;

    private ClusterRedisBuilder() {
    }

    public static ClusterRedisBuilder create() {
        return new ClusterRedisBuilder();
    }

    public ClusterRedisBuilder setMaxTotal(int maxTotal) {
        this.maxTotal = maxTotal;
        return this;
    }

    public ClusterRedisBuilder setMiniIdle(int miniIdle) {
        this.miniIdle = miniIdle;
        return this;
    }

    public ClusterRedisBuilder setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
        return this;
    }

    public ClusterRedisBuilder setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
        return this;
    }

    public ClusterRedisBuilder setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
        return this;
    }

    public ClusterRedisBuilder setHostAndPorts(String hostAndPorts) {
        this.hostAndPorts = hostAndPorts;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public ClusterRedisBuilder setPassword(String password) {
        this.password = password;
        return this;
    }

    public RedisMQTemplate build() {
        if (this.maxTotal == -1) {
            this.maxTotal = MQConfig.getConfig.getRedisClusterMaxTotal();
            if (this.maxTotal == -1) {
                throw new IllegalArgumentException("ClusterRedisBuilder was not set maxTotal value");
            }
        }
        if (this.miniIdle == -1) {
            this.miniIdle = MQConfig.getConfig.getRedisClusterMiniIdle();
        }
        if (this.maxIdle == -1) {
            this.maxIdle = MQConfig.getConfig.getRedisClusterMaxIdle();
        }
        if (this.connectionTimeout == -1) {
            this.connectionTimeout = MQConfig.getConfig.getRedisClusterConnectionTimeout();
        }
        if (this.maxRedirections == -1) {
            this.maxRedirections = MQConfig.getConfig.getRedisClusterMaxRedirections();
        }

        if (this.hostAndPorts == null) {
            this.hostAndPorts = MQConfig.getConfig.getRedisClusterHost();
            if (this.hostAndPorts == null) {
                throw new IllegalArgumentException("ClusterRedisBuilder was not set hostAndPorts value");
            }
        }

		if (this.password == null) {
			this.password = MQConfig.getConfig.getRedisClusterPassword();
			if ("".equals(this.password)) {
				password = null;
			}
		}

		GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
		poolConfig.setMaxIdle(maxTotal);
		poolConfig.setMinIdle(miniIdle);
		poolConfig.setMinIdle(maxIdle);
		String[] hostArray = hostAndPorts.split(";");

        Set<HostAndPort> jedisClusterNodes = new HashSet<HostAndPort>();
        for (String hostItem : hostArray) {
            String[] hostPort = hostItem.split(":");
            if (hostPort.length == 2) {
                jedisClusterNodes.add(new HostAndPort(hostPort[0], Integer.parseInt(hostPort[1])));
            }
        }

		JedisClusterExtend jc = new JedisClusterExtend(jedisClusterNodes, connectionTimeout, maxRedirections,
				poolConfig, password);
		ClusterRedis clusterRedis = new ClusterRedis(jc);
		RedisMQTemplate client = new RedisMQTemplate();
		client.setRedis(clusterRedis);
		try {
			boolean result = client.ping();
			if (!result) {
				throw new MQInitError("redis服务不可用 hostAndPorts:" + hostAndPorts);
			}
		} catch (MQException e) {
			e.printStackTrace();
			throw new MQInitError("redis服务不可用 hostAndPorts:" + hostAndPorts);
		}
		return client;
	}
}
