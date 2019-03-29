package com.creditease.ns.transporter.chain.service;

import java.util.HashMap;
import java.util.Map;

import com.creditease.framework.exception.NSException;
import com.creditease.framework.pojo.ServiceMessage;
import com.creditease.ns.chains.chain.Chain;
import com.creditease.ns.chains.chain.ChainFactory;
import com.creditease.ns.chains.constants.ChainConstants;
import com.creditease.ns.chains.exchange.DefaultExchanger;
import com.creditease.ns.chains.exchange.Exchanger;
import com.creditease.ns.log.spi.TransporterLog;
import com.creditease.ns.mq.model.Header;
import com.creditease.ns.transporter.chain.adapter.ServiceMessageExchangerAdapter;

public class DefaultServiceChainBridge extends AbstractServiceChainBridge{

	@Override
	public void doWork(ServiceMessage serviceMessage) throws NSException {
		//获取当前配置的catalogId
		long startTime = System.currentTimeMillis();
		//如果没有catalogId 报错
		if (this.catalogId == null || this.catalogId.trim().length() < 1) 
		{
			throw new NSException("没有指定本地任务链");
		}
		//得到catalogId后调用chainDispatcher
		Header header = serviceMessage.getHeader();
		Exchanger exchanger = null;
		try
		{
			Map<String,Object> requestScope = new HashMap<String, Object>();
			requestScope.put(ChainConstants.DEFAULT_DISPATCHER_KEY, this.catalogId);
			exchanger = new DefaultExchanger(requestScope);
			ServiceMessageExchangerAdapter serviceMessageAdapter = new ServiceMessageExchangerAdapter();
			serviceMessageAdapter.setServiceMessage(serviceMessage);
			serviceMessageAdapter.setExchanger(exchanger);
			exchanger.setExchange(ChainConstants.DEFAULT_SERIVCEMESSAGE_KEY, serviceMessageAdapter);
			Chain chain	= ChainFactory.getDefaultChain();
			chain.doChain(exchanger);
			TransporterLog.logSystemDebug("[中转消息] [成功] [{}] [{}] [{}] [cost:{}ms]",header,exchanger,catalogId,System.currentTimeMillis()-startTime);
		}
		catch(NSException exception)
		{
			TransporterLog.logSystemError("[中转消息] [失败] [内部执行链停止] [出现异常] [{}] [{}] [{}] [cost:{}ms]",header,exchanger,catalogId,System.currentTimeMillis()-startTime,exception);
			throw exception;
		}
		catch(Exception e)
		{
			TransporterLog.logSystemError("[中转消息] [失败] [出现异常] [{}] [{}] [{}] [cost:{}ms]",header,exchanger,catalogId,System.currentTimeMillis()-startTime,e);
			throw new RuntimeException("出现未知异常");
		}
	}
	
}
