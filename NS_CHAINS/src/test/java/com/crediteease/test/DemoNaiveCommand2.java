package com.crediteease.test;

import com.creditease.ns.chains.chain.Command;
import com.creditease.ns.chains.exchange.Exchanger;

public class DemoNaiveCommand2 implements Command{

	@Override
	public void doCommand(Exchanger exchanger) throws Exception {
		System.out.println("执行了第二步:"+exchanger.hashCode());
		System.out.println(exchanger.getExchange("step1"));
	}

	@Override
	public boolean isNotBreak() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getLogStr() {
		// TODO Auto-generated method stub
		return null;
	}

}
