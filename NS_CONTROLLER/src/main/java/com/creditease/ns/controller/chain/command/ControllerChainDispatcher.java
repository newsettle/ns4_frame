package com.creditease.ns.controller.chain.command;

import com.creditease.ns.chains.chain.Chain;
import com.creditease.ns.chains.chain.ChainBuilder;
import com.creditease.ns.chains.chain.Command;
import com.creditease.ns.chains.chain.DefaultChainDispatcher;
import com.creditease.ns.chains.constants.ChainConstants;
import com.creditease.ns.chains.context.GlobalScope;
import com.creditease.ns.chains.exchange.Exchanger;
import com.creditease.ns.controller.chain.def.ControllerCatalogDef;
import com.creditease.ns.controller.chain.error.CatalogNotFoundException;
import com.creditease.ns.controller.constants.ControllerConstants;
import com.creditease.ns.log.NsLog;

public class ControllerChainDispatcher extends DefaultChainDispatcher{
	public static ControllerChainDispatcher self;
	
	private static NsLog flowLog = ControllerConstants.FLOW_LOG;
	public void doChain(Exchanger exchanger) throws CatalogNotFoundException,Exception {
		//根据默认消息头获取到chain名字 然后执行不同的chain
		String catalogId = (String)exchanger.getParameter(ChainConstants.DEFAULT_DISPATCHER_KEY);
		flowLog.info("[{}]业务链开始执行", catalogId);
		ControllerCatalogDef catalogDef = (ControllerCatalogDef)globalScope.getCatalogDef(catalogId);
		if (catalogDef != null) 
		{
			exchanger.setExchange(ControllerConstants.CONTROLLER_CONTENTTYPE, catalogDef.getContentType());
			Chain chain = ChainBuilder.build(catalogDef);
			chain.doChain(exchanger);
		}
		else
		{
			flowLog.error("# 消息转发给业务链处理 失败 没有获取到对应的Catalog catalogId:{}", catalogId);
			throw new CatalogNotFoundException("["+catalogId+"]没有找到对应的Catalog服务");
		}
		
		flowLog.info("[{}]业务链执行完毕", catalogId);
	}
	
	public synchronized static ControllerChainDispatcher getInstance()
	{
		if (self == null) 
		{
			self = new ControllerChainDispatcher();
			self.init();
		}
		
		return self;
	}
	
	private ControllerChainDispatcher()
	{
		
	}
	
	private void init()
	{
		globalScope = GlobalScope.getInstance();
	}

	public void add(Command command) {
		throw new UnsupportedOperationException("不支持此方法");
	}

	@Override
	public String getLogStr() {
		return "[执行ControllerChainDispatcher链分发器]";
	}
}
