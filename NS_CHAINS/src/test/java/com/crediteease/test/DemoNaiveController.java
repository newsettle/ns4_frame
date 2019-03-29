package com.crediteease.test;

import java.util.Map;

import com.creditease.framework.scope.RequestScope;
import com.creditease.ns.chains.chain.DefaultChain;
import com.creditease.ns.chains.exchange.DefaultExchanger;
import com.creditease.ns.chains.exchange.Exchanger;

public class DemoNaiveController {
	public static void main(String[] args) throws Exception 
	{
		DefaultChain defaultChain = new DefaultChain();
		defaultChain.init();
		defaultChain.add(new DemoNaiveCommand1());
		defaultChain.add(new DemoNaiveCommand2());
		
		defaultChain.doChain(new DefaultExchanger(new RequestScope()));
	}
}
