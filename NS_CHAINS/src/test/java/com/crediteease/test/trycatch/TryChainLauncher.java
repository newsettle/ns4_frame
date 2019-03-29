package com.crediteease.test.trycatch;

import java.util.HashMap;
import java.util.Map;

import com.creditease.ns.chains.chain.Chain;
import com.creditease.ns.chains.chain.ChainBuilder;
import com.creditease.ns.chains.context.GlobalScope;
import com.creditease.ns.chains.def.CatalogDef;
import com.creditease.ns.chains.exchange.DefaultExchanger;
import com.creditease.ns.chains.exchange.Exchanger;
import com.creditease.ns.chains.start.ChainLauncher;

public class TryChainLauncher {
	public static void main(String[] args) throws Exception 
	{
		
		String chainConfigPath = Thread.currentThread().getContextClassLoader().getResource("demotry.xml").getPath();
		ChainLauncher chainLauncher = ChainLauncher.getInstance();
		chainLauncher.setResourcePath(chainConfigPath);
		chainLauncher.startUp();
		
		
		GlobalScope globalScope = GlobalScope.getInstance();
		
		globalScope.hasSpring = false;
		
		CatalogDef catalogDef = globalScope.getCatalogDef("testtry");
		if (catalogDef != null) 
		{
			Exchanger exchanger = null;
			Map<String,Object> requestScope = new HashMap<String, Object>();
			exchanger = new DefaultExchanger(requestScope);
			Chain chain = ChainBuilder.build(catalogDef);
			chain.doChain(exchanger);
		}
		
		
	}
}
