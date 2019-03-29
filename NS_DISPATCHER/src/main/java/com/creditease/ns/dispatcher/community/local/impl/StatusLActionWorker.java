package com.creditease.ns.dispatcher.community.local.impl;

import com.creditease.framework.exception.NSException;
import com.creditease.framework.pojo.ServiceMessage;
import com.creditease.framework.scope.SystemOutKey;
import com.creditease.framework.scope.SystemRetInfo;
import com.creditease.framework.work.ActionWorker;
import com.creditease.ns.dispatcher.community.http.HttpRPCHandler;
import com.creditease.ns.dispatcher.community.local.LocalActionMapping;

@LocalActionMapping(server="dispatcher_status")
public class StatusLActionWorker extends ActionWorker {

    @Override
    public void doWork(ServiceMessage serviceMessage) throws NSException {
        serviceMessage.setOut(LActionOutKey.queueSize, HttpRPCHandler.getQueueSize());
        serviceMessage.setOut(LActionOutKey.activeThreadCount, HttpRPCHandler.getActiveThreadCount());
        serviceMessage.setOut(SystemOutKey.RETURN_CODE, SystemRetInfo.NORMAL);
    }
}
