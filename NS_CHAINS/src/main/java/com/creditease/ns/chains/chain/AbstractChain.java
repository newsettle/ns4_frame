package com.creditease.ns.chains.chain;

import com.creditease.ns.chains.exchange.Exchanger;

public abstract class AbstractChain implements Chain{

	public void doCommand(Exchanger exchanger) throws Exception {
		checkExchanger(exchanger);
		doChain(exchanger);
	}

	private void checkExchanger(Exchanger exchanger) throws Exception 
	{
		if (exchanger == null) 
		{
			throw new Exception("exchanger不能为null");
		}
	}
	
	@Override
	public boolean isNotBreak() {
		return false;
	}
}
