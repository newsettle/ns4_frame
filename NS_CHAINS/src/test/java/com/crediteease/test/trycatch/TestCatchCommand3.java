package com.crediteease.test.trycatch;

import com.creditease.ns.chains.chain.Command;
import com.creditease.ns.chains.exchange.Exchanger;

public class TestCatchCommand3 implements Command{

	@Override
	public void doCommand(Exchanger exchanger) throws Exception {
		System.out.println("TestCatchCommand3");
		throw new Exception("执行了exception3");
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
