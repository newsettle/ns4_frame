package com.creditease.ns.mq.redis;

import com.creditease.ns.log.NsLog;
import com.creditease.ns.mq.MQConfig;
import com.creditease.ns.mq.MQTemplate;
import com.creditease.ns.mq.exception.MQArgumentException;
import com.creditease.ns.mq.exception.MQException;
import com.creditease.ns.mq.exception.MQRedisException;
import com.creditease.ns.mq.exception.MQTimeOutException;
import com.creditease.ns.mq.model.DeliveryMode;
import com.creditease.ns.mq.model.Message;

/**
 * redis的实现
 */
public class RedisMQTemplate extends MQTemplate {
    private static final NsLog commandLog = NsLog
            .getMqLog("NS_MQ", "redis命令");

    Redis redis;

    @Override
    public void send(String queueName, byte[] body) throws MQException {
        long start = System.currentTimeMillis();
        if (queueName == null || queueName.length() == 0) {
            throw new MQArgumentException("queueName is illegal");
        }
        if (body == null || body.length == 0) {
            throw new MQArgumentException("The byte[] body is illegal");
        }
        Message message = new Message(DeliveryMode.ASYNC);
        message.setBody(body);
        try {
            queueName = getActiveQueueName(queueName);
            redis.lpush(queueName, message.toBytes());
            commandLog.debug("send message msgId:{} to queueName:{}，use:[{}ms",
                    message.getHeader().getMessageID(), queueName,
                    System.currentTimeMillis() - start);
        } catch (MQRedisException e) {
            throw e;
        } catch (Exception e1) {
            throw new MQException(e1);
        }
    }

    @Override
    public void send(String queueName, String msgId, byte[] body)
            throws MQException {
        long start = System.currentTimeMillis();
        if (queueName == null || queueName.length() == 0) {
            throw new MQArgumentException("queueName is illegal");
        }
        if (body == null || body.length == 0) {
            throw new MQArgumentException("The byte[] body is illegal");
        }
        Message message = new Message(msgId, DeliveryMode.ASYNC);
        message.setBody(body);
        try {
            queueName = getActiveQueueName(queueName);
            redis.lpush(queueName, message.toBytes());
            commandLog.debug("send message msgId:{} to queueName:{}，use:[{}ms",
                    message.getHeader().getMessageID(), queueName,
                    System.currentTimeMillis() - start);
        } catch (MQRedisException e) {
            throw e;
        } catch (Exception e1) {
            throw new MQException(e1);
        }
    }

    @Override
    public void send(String queueName, Message message) throws MQException {
        long start = System.currentTimeMillis();
        if (queueName == null || queueName.length() == 0) {
            throw new MQArgumentException("queueName is illegal");
        }
        try {
            queueName = getActiveQueueName(queueName);
            redis.lpush(queueName, message.toBytes());
            commandLog.debug("send message msgId:{} to queueName:{}，use:[{}ms",
                    message.getHeader().getMessageID(), queueName,
                    System.currentTimeMillis() - start);
        } catch (MQRedisException e) {
            throw e;
        } catch (Exception e1) {
            throw new MQException(e1);
        }
    }

    @Override
    public Message receive(String queueName) throws MQException {
        long start = System.currentTimeMillis();
        if (queueName == null || queueName.length() == 0) {
            throw new MQArgumentException("queueName is illegal");
        }
        byte[] result;
        try {
            queueName = getActiveQueueName(queueName);
            result = redis.brpop(queueName);

        } catch (MQRedisException e) {
            throw e;
        } catch (Exception e1) {
            throw new MQException(e1);
        }
        if (result == null) {
            return null;
        } else {
            Message message = new Message(result);
            commandLog.debug(
                    "receive message msgId:{} from queueName:{}，use:[{}ms]",
                    message.getHeader().getMessageID(), queueName,
                    System.currentTimeMillis() - start);
            return message;
        }
    }

    @Override
    public Message receive(String queueName, int timeout) throws MQException {
        long start = System.currentTimeMillis();
        if (queueName == null || queueName.length() == 0) {
            throw new MQArgumentException("queueName is illegal");
        }
        byte[] result;
        try {
            queueName = getActiveQueueName(queueName);
            result = redis.brpop(queueName, timeout);
        } catch (MQRedisException e) {
            throw e;
        } catch (Exception e1) {
            throw new MQException(e1);
        }
        if (result == null) {
            return null;
        } else {
            Message message = new Message(result);
            commandLog.debug(
                    "receive message msgId:{} from queueName:{}，use:[{}ms]",
                    message.getHeader().getMessageID(), queueName,
                    System.currentTimeMillis() - start);
            return message;
        }
    }

    @Override
    public Message receiveNoneBlock(String queueName) throws MQException {
        long start = System.currentTimeMillis();
        if (queueName == null || queueName.length() == 0) {
            throw new MQArgumentException("queueName is illegal");
        }
        byte[] result;
        try {
            queueName = getActiveQueueName(queueName);
            result = redis.rpop(queueName);
        } catch (MQRedisException e) {
            throw e;
        } catch (Exception e1) {
            throw new MQException(e1);
        }
        if (result == null) {
            return null;
        } else {
            Message message = new Message(result);
            commandLog.debug(
                    "receive message msgId:{} from queueName:{}，use:[{}ms]",
                    message.getHeader().getMessageID(), queueName,
                    System.currentTimeMillis() - start);
            return message;
        }
    }

    @Override
    public Message publish(String queueName, byte[] body) throws MQException {
        long start = System.currentTimeMillis();
        if (queueName == null || queueName.length() == 0) {
            throw new MQArgumentException("queueName is illegal");
        }
        if (body == null || body.length == 0) {
            throw new MQArgumentException("The byte[] body is illegal");
        }
        Message message = new Message(DeliveryMode.SYNC);
        message.setBody(body);
        try {
            queueName = getActiveQueueName(queueName);
            redis.lpush(queueName, message.toBytes());
            commandLog.debug(
                    "publish message msgId:{} to queueName:{}，use:[{}ms]",
                    message.getHeader().getMessageID(), queueName,
                    System.currentTimeMillis() - start);
        } catch (MQRedisException e) {
            throw e;
        } catch (Exception e1) {
            throw new MQException(e1);
        }
        long replyStart = System.currentTimeMillis();
        String replyQueue = message.getHeader().getReplyTo();
        byte[] response;

        try {
            response = redis.brpop(replyQueue);
        } catch (MQRedisException e) {
            throw e;
        } catch (Exception e1) {
            throw new MQException(e1);
        }
        Message resultMessage = new Message(response);
        commandLog.debug("get reply message msgId:{},use:[{}ms]", resultMessage
                .getHeader().getMessageID(), System.currentTimeMillis()
                - replyStart);
        return resultMessage;
    }

    @Override
    public Message publish(String queueName, byte[] body, int timeout)
            throws MQException {
        long start = System.currentTimeMillis();
        if (queueName == null || queueName.length() == 0) {
            throw new MQArgumentException("queueName is illegal");
        }
        if (body == null || body.length == 0) {
            throw new MQArgumentException("The byte[] body is illegal");
        }
        if (timeout <= 0) {
            throw new MQArgumentException("The int timeout is illegal");
        }

        Message message = new Message(DeliveryMode.SYNC);
        message.setBody(body);

        try {
            queueName = getActiveQueueName(queueName);
            redis.lpush(queueName, message.toBytes());
            commandLog.debug(
                    "publish message msgId:{} to queueName:{}，,use:[{}ms]",
                    message.getHeader().getMessageID(), queueName,
                    System.currentTimeMillis() - start);
        } catch (MQRedisException e) {
            throw e;
        } catch (Exception e1) {
            throw new MQException(e1);
        }
        long replyStart = System.currentTimeMillis();
        String replyQueue = message.getHeader().getReplyTo();

        byte[] response;
        try {
            response = redis.brpop(replyQueue, timeout);
        } catch (MQRedisException e) {
            throw e;
        } catch (Exception e1) {
            throw new MQException(e1);
        }
        if (response == null) {
            throw new MQTimeOutException();
        }
        Message retMessage = new Message(response);
        commandLog.debug("get reply message msgId:{},use:[{}ms]", retMessage
                .getHeader().getMessageID(), System.currentTimeMillis()
                - replyStart);
        return retMessage;
    }

    @Override
    public Message publish(String queueName, byte[] body, String msgId,
                           int timeout) throws MQException {
        long start = System.currentTimeMillis();
        if (queueName == null || queueName.length() == 0) {
            throw new MQArgumentException("queueName is illegal");
        }
        if (body == null || body.length == 0) {
            throw new MQArgumentException("The byte[] body is illegal");
        }
        if (timeout <= 0) {
            throw new MQArgumentException("The int timeout is illegal");
        }

        Message message = new Message(msgId, DeliveryMode.SYNC);
        message.setBody(body);

        try {
            queueName = getActiveQueueName(queueName);
            redis.lpush(queueName, message.toBytes());
            commandLog.debug(
                    "publish message msgId:{} to queueName:{}，,use:[{}ms]",
                    message.getHeader().getMessageID(), queueName,
                    System.currentTimeMillis() - start);
        } catch (MQRedisException e) {
            throw e;
        } catch (Exception e1) {
            throw new MQException(e1);
        }
        long replyStart = System.currentTimeMillis();
        String replyQueue = message.getHeader().getReplyTo();

        byte[] response;
        try {
            response = redis.brpop(replyQueue, timeout);
        } catch (MQRedisException e) {
            throw e;
        } catch (Exception e1) {
            throw new MQException(e1);
        }
        if (response == null) {
            throw new MQTimeOutException();
        }
        Message retMessage = new Message(response);
        commandLog.debug("get reply message msgId:{},use:[{}ms]", retMessage
                .getHeader().getMessageID(), System.currentTimeMillis()
                - replyStart);
        return retMessage;
    }

    @Override
    public Message publish(String queueName, Message message)
            throws MQException {
        long start = System.currentTimeMillis();
        if (queueName == null || queueName.length() == 0) {
            throw new MQArgumentException("queueName is illegal");
        }

        try {
            queueName = getActiveQueueName(queueName);
            redis.lpush(queueName, message.toBytes());
            commandLog.debug(
                    "publish message msgId:{} to  queueName:{}，use:[{}ms]",
                    message.getHeader().getMessageID(), queueName,
                    System.currentTimeMillis() - start);
        } catch (MQRedisException e) {
            throw e;
        } catch (Exception e1) {
            throw new MQException(e1);
        }
        long replyStart = System.currentTimeMillis();
        String replyQueue = message.getHeader().getReplyTo();
        byte[] response;

        try {
            response = redis.brpop(replyQueue);
        } catch (MQRedisException e) {
            throw e;
        } catch (Exception e1) {
            throw new MQException(e1);
        }
        Message retMessage = new Message(response);
        commandLog.debug("get reply message msgId:{},use:[{}ms]", retMessage
                .getHeader().getMessageID(), System.currentTimeMillis()
                - replyStart);
        return retMessage;
    }

    @Override
    public Message publish(String queueName, Message message, int timeout)
            throws MQException {
        long start = System.currentTimeMillis();
        if (queueName == null || queueName.length() == 0) {
            throw new MQArgumentException("queueName is illegal");
        }
        if (message == null) {
            throw new MQArgumentException("The byte[] body is illegal");
        }
        if (timeout <= 0) {
            throw new MQArgumentException("The int timeout is illegal");
        }

        try {
            queueName = getActiveQueueName(queueName);
            redis.lpush(queueName, message.toBytes());
            commandLog.debug(
                    "publish message msgId:{} to queueName:{}，use:[{}ms]",
                    message.getHeader().getMessageID(), queueName,
                    System.currentTimeMillis() - start);
        } catch (MQRedisException e) {
            throw e;
        } catch (Exception e1) {
            throw new MQException(e1);
        }
        long replyStart = System.currentTimeMillis();
        String replyQueue = message.getHeader().getReplyTo();

        byte[] response = null;
        try {
            if (timeout <= 0) {
                response = redis.brpop(replyQueue, 0);
            } else {
                while (timeout-- > 0) {
                    response = redis.brpop(replyQueue, 1);
                    if (response != null) {
                        break;
                    }
                }
            }
        } catch (MQRedisException e) {
            throw e;
        } catch (Exception e1) {
            throw new MQException(e1);
        }
        if (response == null) {
            throw new MQTimeOutException();
        }
        Message retMessage = new Message(response);
        commandLog.debug("get reply message msgId:{},use:[{}ms]", retMessage
                .getHeader().getMessageID(), System.currentTimeMillis()
                - replyStart);
        return retMessage;
    }

    @Override
    public void reply(Message message) throws MQException {
        long start = System.currentTimeMillis();
        String replyQueue = message.getHeader().getReplyTo();
        try {

            if (this.getTempQueueExpired() == -1) {
                this.setTempQueueExpired(MQConfig.getConfig
                        .getTempQueueExpired());
            }
            commandLog
                    .debug("ready to reply message msgId:{} to queueName:{}], tempQueueExpired:{}",
                            message.getHeader().getMessageID(), replyQueue,
                            this.getTempQueueExpired());
            redis.lpushWithExpired(replyQueue, message.toBytes(),
                    this.getTempQueueExpired());
            commandLog
                    .debug("reply message msgId:{} to queueName:{}，use:[{}ms], tempQueueExpired:{}",
                            message.getHeader().getMessageID(), replyQueue,
                            System.currentTimeMillis() - start);
        } catch (MQRedisException e) {
            throw e;
        } catch (Exception e1) {
            throw new MQException(e1);
        }
    }

    @Override
    public boolean ping() throws MQException {
        try {
            String check = redis.ping();
            if (Constants.PONG.equals(check)) {
                return true;
            }
        } catch (Exception e) {
            throw new MQRedisException(e);
        }
        return false;
    }

    public void setRedis(Redis redis) {
        this.redis = redis;
    }

}
