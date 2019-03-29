package com.crediteease.test;

import com.creditease.ns.chains.chain.Command;
import com.creditease.ns.chains.exchange.Exchanger;

public class DemoNaiveCommand1 implements Command{

	@Override
	public void doCommand(Exchanger exchanger) throws Exception {
		exchanger.setExchange("step1", "2");
		System.out.println("执行了第一步:"+exchanger.hashCode());
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
