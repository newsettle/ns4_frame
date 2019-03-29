package com.creditease.ns.mq.redis;

import com.creditease.ns.log.NsLog;
import com.creditease.ns.mq.exception.MQRedisException;
import redis.clients.util.SafeEncoder;

import java.util.List;


public class ClusterRedis implements Redis {
    private JedisClusterExtend jc;
    private static final NsLog commandLog = NsLog.getMqLog("NS_MQ", "redis集群命令");

    @Override
    public void lpush(String queueName, byte[] data) throws MQRedisException {
        try {
            this.jc.lpush(SafeEncoder.encode(queueName), data);
        } catch (Exception e) {
            commandLog.error("mq lpush error", e);
            throw new MQRedisException(e);
        }
    }

    @Override
    public byte[] brpop(String queueName) throws MQRedisException {
        List<byte[]> result = null;
        try {
            result = this.jc.brpop(0, SafeEncoder.encode(queueName));
        } catch (Exception e) {
            commandLog.error("mq lpush error", e);
            throw new MQRedisException(e);
        }
        return result.get(1);
    }

    @Override
    public byte[] brpop(String queueName, int timeout) throws MQRedisException {
        List<byte[]> result = this.jc.brpop(timeout, SafeEncoder.encode(queueName));
        return result.get(1);
    }

    @Override
    public void expired(String key, int expired) throws MQRedisException {
        this.jc.expire(SafeEncoder.encode(key), expired);
    }

    @Override
    public byte[] rpop(String queueName) throws MQRedisException {
        return this.jc.rpop(SafeEncoder.encode(queueName));
    }

    @Override
    /*
        Redis Cluster 不支持不在同一台机器上的多key操作，在我们这种情况里，lua脚本在一台机器，key在别的机器，此时rediscluster就会报错
        为了解决这个问题，这里将不会采用多key事务性的语句，而是采用两条单独命令叠加
        这样可能会造成第二条报错，但是他并不会造成什么不好的结果，所以这里将无关的报错给吞掉
     */
    public void lpushWithExpired(String queueName, byte[] data, int second) throws MQRedisException {
//        String script = "local retCode = redis.call('LPUSH', KEYS[1], KEYS[2]);\n redis.call('EXPIRE', KEYS[1], KEYS[3]);\n return retCode";
//        this.jc.eval(SafeEncoder.encode(script), SafeEncoder.encode(queueName), 3, SafeEncoder.encode(queueName), data, SafeEncoder.encode(String.valueOf(second)));
        long result = this.jc.pushAndExpirte(second,SafeEncoder.encode(queueName),data);

        if (result == -1)
        {
            commandLog.debug("mq pushAndExpired push error");
        }
        else if (result == -2)
        {
            commandLog.debug("mq pushAndExpired expired error");
        }
    }

    @Override
    public String ping() throws MQRedisException {
        String time = String.valueOf(System.currentTimeMillis());
        this.jc.lpush(time, time);
        String checkTime = this.jc.rpop(time);
        if (time.equals(checkTime)) {
            return Constants.PONG;
        } else {
            return Constants.PONG_ERROR;
        }
    }

    public ClusterRedis(JedisClusterExtend jc) {
        this.jc = jc;
    }
}
