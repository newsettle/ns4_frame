package com.creditease.framework.util;

import com.creditease.framework.pojo.DefaultServiceMessage;
import com.creditease.framework.pojo.ServiceMessage;
import com.creditease.framework.scope.ExchangeScope;
import com.creditease.framework.scope.OutScope;
import com.creditease.framework.scope.RequestScope;
import com.creditease.ns.log.spi.TransporterLog;
import com.creditease.ns.mq.model.Message;

public class MessageConvertUtil {
	public static ServiceMessage convertToServiceMessage(Message message) throws Exception
	{
		byte[] body = message.getBody();
		if (body != null) 
		{
			//先认为是json直接转成的字节，现在转回来 字符集是utf-8
			ServiceMessage mqMessage = (ServiceMessage)ProtoStuffSerializeUtil.unSerializeForCommon(body);
			mqMessage.setHeader(message.getHeader());
			return mqMessage;
		}
		else
		{
			throw new IllegalArgumentException("格式错误，没有消息体");
		}
	}
	
	public static Message convertToMessage(ServiceMessage serviceMessage) throws Exception
	{
		Message message = new Message();
		message.setHeader(serviceMessage.getHeader());
		message.setBody(ProtoStuffSerializeUtil.serializeForCommon(serviceMessage));
		return message;
	}
}
