package com.creditease.ns.controller.chain.command;

import com.creditease.framework.exception.StopException;
import com.creditease.framework.pojo.DefaultServiceMessage;
import com.creditease.framework.scope.ExchangeScope;
import com.creditease.framework.scope.OutScope;
import com.creditease.framework.util.MessageConvertUtil;
import com.creditease.framework.util.ProtoStuffSerializeUtil;
import com.creditease.framework.util.ReflectionUtils;
import com.creditease.framework.util.StringUtil;
import com.creditease.ns.chains.chain.Command;
import com.creditease.ns.chains.exchange.Exchanger;
import com.creditease.ns.controller.constants.ControllerConstants;
import com.creditease.ns.log.NsLog;
import com.creditease.ns.mq.MQTemplate;
import com.creditease.ns.mq.MQTemplates;
import com.creditease.ns.mq.model.DeliveryMode;
import com.creditease.ns.mq.model.Header;
import com.creditease.ns.mq.model.Message;

public class DefaultPublishCommand implements Command {

	protected String queueName;
	protected boolean isSync = true;
	protected String desc;
	protected int timeout = 2 * 60; //单位秒
	protected String serviceName;
	protected boolean isBreak;

	private static MQTemplate template = MQTemplates.defaultTemplate();
	private static NsLog flowLog = ControllerConstants.FLOW_LOG;
	public void doCommand(Exchanger exchanger) throws Exception {
		flowLog.trace("# DefaultPublishCommand 开始 {} #", queueName);
		doPublish(exchanger);
	}

	public void doPublish(Exchanger exchanger) throws Exception {
		long startTime = System.currentTimeMillis();
		DefaultServiceMessage serviceMessage = (DefaultServiceMessage) exchanger.getExchange(ControllerConstants.DEFAULT_SERVICEMESSAGE_KEY);
		Header header = serviceMessage.getHeader();
		String contentType = (String)exchanger.getExchange(ControllerConstants.CONTROLLER_CONTENTTYPE);
		if("html".equals(contentType))
		{
			header.setContentType(1);
		}
		else if("plaintext".equals(contentType))
		{
			header.setContentType(3);
		}
		else
		{
			header.setContentType(2);
		}

		//       
		byte[] bs = ProtoStuffSerializeUtil.serializeForCommon(serviceMessage);

		if (isSync) {

			Message message = buildMessage(header, bs,DeliveryMode.SYNC);
			Message messsage = template.publish(queueName,message,timeout);

			boolean isStop = messsage.getHeader().isStop();

			DefaultServiceMessage newserviceMessage = (DefaultServiceMessage) MessageConvertUtil.convertToServiceMessage(messsage);
			//反射获取servicemessage中的域
			String outScopeName = "outScope";
			String exchangeScopeName = "exchangeScope";
			String propertyMapName = "propertiesMap";
			
			OutScope outScope = (OutScope) ReflectionUtils.getFieldValue(outScopeName, newserviceMessage);
			ExchangeScope exchangeScope = (ExchangeScope) ReflectionUtils.getFieldValue(exchangeScopeName, newserviceMessage);
			
			ReflectionUtils.setFieldValue(exchangeScopeName, serviceMessage, exchangeScope);
			ReflectionUtils.setFieldValue(outScopeName, serviceMessage, outScope);

			exchanger.setExchange(ControllerConstants.DEFAULT_SERVICEMESSAGE_KEY, serviceMessage);

			if (isStop) {
				String exception = messsage.getHeader().getExceptionContent();
				flowLog.error("# 执行消息发送 失败 出现中断标记 {} {} {} cost:{}ms #", isStop, queueName, exception, System.currentTimeMillis() - startTime);
				throw new StopException("出现中断标记,抛出异常,中断执行链[" + queueName + "] [" + this.getDesc() + "]");
			}
		} else {
			
			Message message = buildMessage(header, bs, DeliveryMode.ASYNC);
			message.setBody(bs);
			if(!StringUtil.isEmpty(serviceName))
			{
				message.getHeader().setServerName(serviceName);
			}
			template.send(queueName,message);
		}

		flowLog.info("# 执行消息发送 结束 是否是同步发送:{} queueName:{} cost:{}ms #", isSync, queueName,System.currentTimeMillis() - startTime);
	}

	private Message buildMessage(Header header, byte[] bs, DeliveryMode deliveryMode) {
		Message message = new Message(header.getMessageID(), deliveryMode);
		message.setBody(bs);
		if(!StringUtil.isEmpty(serviceName))
		{
			message.getHeader().setServerName(serviceName);
		}
		return message;
	}

	public String getQueueName() {
		return queueName;
	}

	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}

	public boolean isSync() {
		return isSync;
	}

	public void setSync(boolean isSync) {
		this.isSync = isSync;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	@Override
	public String getLogStr() {
		return "[执行publish命令] [" + queueName + "] [" + timeout + "] [" + isSync + "] [" + desc + "] ["+ isBreak +"]";
	}

	@Override
	public boolean isNotBreak() {
		return !isBreak;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}


	public void setBreak(boolean aBreak) {
		isBreak = aBreak;
	}
}
