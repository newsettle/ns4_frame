package com.creditease.ns.chains.chain;

import java.util.HashMap;
import java.util.Map;

import com.creditease.framework.exception.NSException;
import com.creditease.framework.exception.StopException;
import com.creditease.framework.pojo.ServiceMessage;
import com.creditease.framework.scope.RetInfo;
import com.creditease.framework.work.ActionWorker;
import com.creditease.ns.chains.constants.ChainConstants;
import com.creditease.ns.chains.exchange.Exchanger;
import com.creditease.ns.log.NsLog;

public abstract class AbstractServiceMessageCommand implements Command{
	static NsLog  flowLog = NsLog.getFlowLog("NsChainFlow", "NsChainFlow");
	private ThreadLocal<Object> notBreakChain = new ThreadLocal<Object>();
	/**
	 * 这个方法需要注意
	 * 当在使用spring注解的时候，使用人员往往喜欢在doService方法上加事务注解
	 * transactional 此时就会出现问题，发现事务没有起作用
	 * 原因就是doService是被doCommand调用的 doCommand上面是没有加transactional注解的
	 * 原理是 在spring中当外部对象调用时 直接调用的是代理对象 而在本地对象自我调用内部方法
	 * 利用的是this对象 这是原生对象 绕过了代理 所以事务没有执行
	 * 目前的解决办法是继承类自己覆盖doCommand方法然后在doCommand方法上加事务
	 * @param exchanger
	 * @throws Exception
	 * 2015年11月5日下午2:02:22
	 */
	@Override
	public void doCommand(Exchanger exchanger) throws Exception {
		ServiceMessage serviceMessage = (ServiceMessage)exchanger.getExchange(ChainConstants.DEFAULT_SERIVCEMESSAGE_KEY);
		doService(serviceMessage);
	}

	public abstract void doService(ServiceMessage serviceMessage) throws NSException;

	@Override
	public abstract String getLogStr();	

	public void stop(ServiceMessage serviceMessage,RetInfo retInfo) throws StopException
	{
		ActionWorker.stop(serviceMessage, retInfo);
	}

	@SuppressWarnings("unchecked")
	public void continueCurrentChain()
	{
		Object isNotBreakChain = notBreakChain.get();
		Boolean isNotBreak = true;
		Map<String,Boolean> isNotBreakChainMap = null;
		if (isNotBreakChain == null) 
		{
			isNotBreakChainMap = new HashMap<String, Boolean>();
	
			notBreakChain.set(isNotBreakChainMap);
		}
		else
		{
			isNotBreakChainMap = (Map<String,Boolean>)isNotBreakChain;
		}
		
		isNotBreakChainMap.put(this.getClass().getName(), isNotBreak);
		flowLog.debug("# 设置不中断标记 OK classname:{} isnotbreak:{} #", this.getClass().getName(),isNotBreak);
	}


	@SuppressWarnings("unchecked")
	@Override
	public boolean isNotBreak() {
		Object isNotBreakChain = notBreakChain.get();
		Boolean isNotBreak = false;
		if (isNotBreakChain == null) 
		{
			isNotBreak = false;
		}
		else
		{
			Map<String,Boolean> isNotBreakChainMap = (Map<String,Boolean>) isNotBreakChain;

			isNotBreak = isNotBreakChainMap.get(this.getClass().getName());

			if(isNotBreak == null)
			{
				isNotBreak = false;
			}
		}
		flowLog.debug("# 获取不中断标记 OK classname:{} isnotbreak:{} #", this.getClass().getName(),isNotBreak);
		return isNotBreak;

	}
}
