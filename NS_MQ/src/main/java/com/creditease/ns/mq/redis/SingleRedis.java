package com.creditease.ns.mq.redis;


import com.creditease.ns.log.NsLog;
import com.creditease.ns.mq.exception.MQRedisException;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.util.SafeEncoder;

import java.util.List;
import java.util.concurrent.*;

public class SingleRedis implements Redis {
    private static final NsLog commandLog = NsLog.getMqLog("NS_MQ", "redis单机命令");

    private JedisPool jedisPool;
    private ExecutorService executor = Executors.newCachedThreadPool();

    public SingleRedis(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }


    @Override
    public void lpush(String queueName, byte[] data) throws MQRedisException {
        Jedis jedis = this.jedisPool.getResource();
        try {
            jedis.lpush(SafeEncoder.encode(queueName), data);
        } catch (Exception e) {
            commandLog.error("mq lpush error", e);
            throw new MQRedisException(e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    @Override
    public byte[] brpop(String queueName) throws MQRedisException {
        Jedis jedis = this.jedisPool.getResource();
        List<byte[]> result = null;
        try {
            result = jedis.brpop(0, SafeEncoder.encode(queueName));
        } catch (Exception e) {
            commandLog.error("mq brpop error", e);
            throw new MQRedisException(e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        if (result != null) {
            return result.get(1);
        } else {
            return null;
        }
    }

    @Override
    public byte[] brpop(final String queueName, final int timeout) throws MQRedisException {
        boolean runable = true;
        final Jedis jedis = this.jedisPool.getResource();
        List<byte[]> result = null;
        try {
            FutureTask<List<byte[]>> brpopTask = new FutureTask<>(new Callable<List<byte[]>>() {
                @Override
                public List<byte[]> call() throws Exception {
                    return jedis.brpop(timeout, SafeEncoder.encode(queueName));
                }
            });
            executor.execute(brpopTask);
            result = brpopTask.get(timeout + 2, TimeUnit.SECONDS);
        } catch (Exception e) {
            if (e instanceof TimeoutException || e instanceof InterruptedException) {
                if (jedis != null) {
                    jedis.disconnect();
                    runable = false;
                    jedisPool.returnBrokenResource(jedis);
                }
            }
            commandLog.error("mq brpop error", e);
            throw new MQRedisException(e);
        } finally {
            if (jedis != null && runable) {
                jedis.close();
            }
        }
        if (result != null) {
            return result.get(1);
        } else {
            return null;
        }
    }

    @Override
    public void expired(String key, int expired) throws MQRedisException {
        Jedis jedis = this.jedisPool.getResource();
        try {
            jedis.expire(key, expired);
        } catch (Exception e) {
            commandLog.error("mq expired error", e);
            throw new MQRedisException(e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    @Override
    public byte[] rpop(String queueName) throws MQRedisException {
        Jedis jedis = this.jedisPool.getResource();
        byte[] result = null;
        try {
            result = jedis.rpop(SafeEncoder.encode(queueName));
        } catch (Exception e) {
            commandLog.error("mq rpop error", e);
            throw new MQRedisException(e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return result;
    }

    @Override
    public void lpushWithExpired(String queueName, byte[] data, int second) throws MQRedisException {
        Jedis jedis = this.jedisPool.getResource();

        try {
            String sha1 = jedis.scriptLoad("local retCode = redis.call('LPUSH', KEYS[1], KEYS[2]);\n redis.call('EXPIRE', KEYS[1], KEYS[3]);\n return retCode");
            jedis.evalsha(SafeEncoder.encode(sha1), 3, SafeEncoder.encode(queueName), data, SafeEncoder.encode(String.valueOf(second)));
        } catch (Exception e) {
            commandLog.error("mq lpushWithExpired error", e);
            throw new MQRedisException(e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    @Override
    public String ping() throws MQRedisException {
        Jedis jedis = this.jedisPool.getResource();

        try {
            return jedis.ping();
        } catch (Exception e) {
            commandLog.error("mq ping error", e);
            throw new MQRedisException(e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
}
