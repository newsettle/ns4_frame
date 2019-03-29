package com.creditease.framework.util;

import com.creditease.framework.pojo.DefaultServiceMessage;
import com.creditease.framework.pojo.ServiceMessage;
import com.creditease.framework.scope.ExchangeScope;
import com.creditease.framework.scope.OutScope;
import com.creditease.ns.mq.MQTemplate;
import com.creditease.ns.mq.MQTemplates;
import com.creditease.ns.mq.exception.MQException;
import com.creditease.ns.mq.model.DeliveryMode;
import com.creditease.ns.mq.model.Header;
import com.creditease.ns.mq.model.Message;

import java.io.IOException;


public class MessageOperationUtils {
    private static MQTemplate mqTemplate = MQTemplates.defaultTemplate();

    public static void sendMessage(String queueName, ServiceMessage serviceMessage) throws IOException, MQException {
        byte[] bs = ProtoStuffSerializeUtil.serializeForCommon(serviceMessage);
        Message message = buildMessage(serviceMessage.getHeader(), bs, DeliveryMode.ASYNC);
        message.setBody(bs);
        mqTemplate.send(queueName, message);
    }

    /**
     * @param queueName
     * @param serviceMessage
     * @param timeout        单位秒
     * @return
     * @throws IOException
     */
    public static ServiceMessage publish(String queueName, ServiceMessage serviceMessage, int timeout) throws
            Exception {
        byte[] bs = ProtoStuffSerializeUtil.serializeForCommon(serviceMessage);
        Message message = buildMessage(serviceMessage.getHeader(), bs, DeliveryMode.SYNC);

        if (timeout > 0) {
            message = mqTemplate.publish(queueName, message, timeout);
        } else {
            message = mqTemplate.publish(queueName, message);
        }

        DefaultServiceMessage newserviceMessage = (DefaultServiceMessage) MessageConvertUtil.convertToServiceMessage
                (message);
        //反射获取servicemessage中的域
        String outScopeName = "outScope";
        String exchangeScopeName = "exchangeScope";

        OutScope outScope = (OutScope) ReflectionUtils.getFieldValue(outScopeName, newserviceMessage);
        ExchangeScope exchangeScope = (ExchangeScope) ReflectionUtils.getFieldValue(exchangeScopeName,
                newserviceMessage);

        ReflectionUtils.setFieldValue(exchangeScopeName, serviceMessage, exchangeScope);
        ReflectionUtils.setFieldValue(outScopeName, serviceMessage, outScope);

        return serviceMessage;
    }

    public static ServiceMessage receive(String queueName) throws Exception {
        Message message = mqTemplate.receive(queueName);
        return MessageConvertUtil.convertToServiceMessage(message);
    }

    public static void reply(ServiceMessage serviceMessage) throws Exception {
        Message message = MessageConvertUtil.convertToMessage(serviceMessage);
        //需要reply 1是同步
        mqTemplate.reply(message);
    }


    private static Message buildMessage(Header header, byte[] bs, DeliveryMode deliveryMode) {
        Message message = new Message(header.getMessageID(), deliveryMode);
        message.setBody(bs);
        return message;
    }

}
