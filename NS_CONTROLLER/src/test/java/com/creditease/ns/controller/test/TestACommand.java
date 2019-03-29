package com.creditease.ns.controller.test;

import com.creditease.framework.exception.NSException;
import com.creditease.framework.pojo.ServiceMessage;
import com.creditease.ns.chains.chain.AbstractServiceMessageCommand;

public class TestACommand extends AbstractServiceMessageCommand {
    @Override
    public void doService(ServiceMessage serviceMessage) throws NSException {
        serviceMessage.setOut(MyKey.TESTCONTROLLER,"OK");
    }

    @Override
    public String getLogStr() {
        return null;
    }

}
