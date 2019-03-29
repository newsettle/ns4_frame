package com.crediteease.test.trycatch;

import com.creditease.ns.chains.chain.Command;
import com.creditease.ns.chains.exchange.Exchanger;

public class TestTryCommand1 implements Command{

	@Override
	public void doCommand(Exchanger exchanger) throws Exception {
		
		System.out.println("执行了trycommand1");
//		throw new Exception("执行了trycommand1异常");
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
