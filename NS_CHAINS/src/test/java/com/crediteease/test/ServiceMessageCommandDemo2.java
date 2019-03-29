package com.crediteease.test;

import com.creditease.framework.exception.NSException;
import com.creditease.framework.pojo.ServiceMessage;
import com.creditease.ns.chains.chain.AbstractServiceMessageCommand;

public class ServiceMessageCommandDemo2 extends AbstractServiceMessageCommand{

	@Override
	public void doService(ServiceMessage serviceMessage) throws NSException {
		System.out.println("执行了demo2");
	}

	@Override
	public String getLogStr() {
		// TODO Auto-generated method stub
		return "执行demo2";
	}

}
