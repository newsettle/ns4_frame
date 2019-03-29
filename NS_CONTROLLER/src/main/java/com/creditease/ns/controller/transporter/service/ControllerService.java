package com.creditease.ns.controller.transporter.service;

import com.creditease.framework.exception.NSException;
import com.creditease.framework.pojo.ServiceMessage;
import com.creditease.framework.scope.SystemOutKey;
import com.creditease.framework.scope.SystemRetInfo;
import com.creditease.framework.work.Worker;
import com.creditease.ns.chains.chain.Chain;
import com.creditease.ns.chains.constants.ChainConstants;
import com.creditease.ns.chains.exchange.DefaultExchanger;
import com.creditease.ns.chains.exchange.Exchanger;
import com.creditease.ns.controller.chain.command.ControllerChainDispatcher;
import com.creditease.ns.controller.chain.error.CatalogNotFoundException;
import com.creditease.ns.controller.constants.ControllerConstants;
import com.creditease.ns.log.NsLog;
import com.creditease.ns.mq.MQTemplate;
import com.creditease.ns.mq.MQTemplates;
import com.creditease.ns.mq.exception.MQTimeOutException;
import com.creditease.ns.mq.model.Header;

import java.util.HashMap;
import java.util.Map;

//需要判断message的header来调用publish还是send
//需要判断message的header 来进行扭转

public class ControllerService implements Worker {
	private MQTemplate mqTemplate = MQTemplates.defaultTemplate();
	private static NsLog flowLog = ControllerConstants.FLOW_LOG;
	
	/*
	 * 1.这里service的执行方法被异步调用
	 * 2.根据传来的servicemessage，调用不同的chain
	 * 3.每个chain的
	 * 
	 * 
	 */
	public void doWork(ServiceMessage serviceMessage) throws NSException
	{
		long startTime = System.currentTimeMillis();
		Header header = serviceMessage.getHeader();
		Exchanger exchanger = null;
		try
		{
			String projectName = header.getServerName();
			Map<String,Object> requestScope = new HashMap<String, Object>();
			requestScope.put(ChainConstants.DEFAULT_DISPATCHER_KEY, projectName);
			exchanger = new DefaultExchanger(requestScope);
			exchanger.setExchange(ControllerConstants.DEFAULT_SERVICEMESSAGE_KEY, serviceMessage);
			Chain chain	= ControllerChainDispatcher.getInstance();
			chain.doChain(exchanger);
			flowLog.info("# 消息处理 OK {} messageHeader:{} cost:{}ms #",projectName,header,System.currentTimeMillis()-startTime);
		}
		catch(CatalogNotFoundException e)
		{
			try {
				String outMessage = serviceMessage.getOut(SystemOutKey.RETURN_CODE);
				if( outMessage == null || outMessage.trim().length() <  1)
				{
					serviceMessage.setOut(SystemOutKey.RETURN_CODE, SystemRetInfo.CTRL_NOT_FOUND_SEVICE_ERROR);
				}
			} catch (NSException e1) {
				e1.printStackTrace();
			}
			flowLog.error("# 消息处理 失败 没有找到对应的Catalog messageHeader:{} cost:{}ms #",header,System.currentTimeMillis()-startTime,e);
			throw e;
		}
		
		catch(Exception e)
		{
			if(e instanceof MQTimeOutException)
			{
				try {
					String outMessage = serviceMessage.getOut(SystemOutKey.RETURN_CODE);
					if( outMessage == null || outMessage.trim().length() <  1)
					{
						serviceMessage.setOut(SystemOutKey.RETURN_CODE,SystemRetInfo.CTRL_SERVICE_TIMEOUT_ERROR);
					}
				} catch (NSException e1) {
					e1.printStackTrace();
				}
			}
			
			flowLog.error("# 消息处理 失败 出现未知异常 messageHeader:{} cost:{}ms #",header,System.currentTimeMillis()-startTime,e);
			throw new NSException(e);
		}
		
		
	}
	
}
