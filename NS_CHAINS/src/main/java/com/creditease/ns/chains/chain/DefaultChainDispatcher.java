package com.creditease.ns.chains.chain;

import com.creditease.ns.chains.constants.ChainConstants;
import com.creditease.ns.chains.context.GlobalScope;
import com.creditease.ns.chains.def.CatalogDef;
import com.creditease.ns.chains.def.ChainDef;
import com.creditease.ns.chains.exchange.Exchanger;

public class DefaultChainDispatcher extends AbstractChain{
	
	public static DefaultChainDispatcher self;
	protected GlobalScope globalScope;
	
	public void doChain(Exchanger exchanger) throws Exception {
		//根据默认消息头获取到chain名字 然后执行不同的chain
		long startTime = System.currentTimeMillis();
		String catalogId = (String)exchanger.getParameter(ChainConstants.DEFAULT_DISPATCHER_KEY);
		CatalogDef catalogDef = globalScope.getCatalogDef(catalogId);
		if (catalogDef != null) 
		{
			Chain chain = ChainBuilder.build(catalogDef);
			chain.doChain(exchanger);
		}
		else
		{
			flowLog.error("没有获取到对应的Catalog catalogId:{}", catalogId);
			throw new Exception("["+catalogId+"]没有找到对应的Catalog服务");
		}
	}
	
	public synchronized static DefaultChainDispatcher getInstance()
	{
		if (self == null) 
		{
			self = new DefaultChainDispatcher();
			self.init();
		}
		
		return self;
	}
	
	protected DefaultChainDispatcher()
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
		return "[执行链分发器]";
	}
}
