package com.crediteease.test.trycatch;

import com.creditease.ns.chains.chain.Command;
import com.creditease.ns.chains.exchange.Exchanger;

public class TestTryCommand3 implements Command{

	@Override
	public void doCommand(Exchanger exchanger) throws Exception {
		
		System.out.println("执行了trycommand3");
//		throw new Exception("抛出异常");
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
