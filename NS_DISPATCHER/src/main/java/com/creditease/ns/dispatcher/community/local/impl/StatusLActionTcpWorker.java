package com.creditease.ns.dispatcher.community.local.impl;

import com.creditease.framework.exception.NSException;
import com.creditease.framework.pojo.ServiceMessage;
import com.creditease.framework.scope.SystemOutKey;
import com.creditease.framework.scope.SystemRetInfo;
import com.creditease.framework.work.ActionWorker;
import com.creditease.ns.dispatcher.community.http.HttpRPCHandler;
import com.creditease.ns.dispatcher.community.local.LocalActionMapping;
import com.creditease.ns.dispatcher.community.tcp.TcpRPCHandler;

@LocalActionMapping(server = "NSSTATUS")
public class StatusLActionTcpWorker extends ActionWorker {

    @Override
    public void doWork(ServiceMessage serviceMessage) throws NSException {
        StringBuilder sb = new StringBuilder();
        sb.append(LActionOutKey.queueSize).append(":").append(TcpRPCHandler.getQueueSize()).append(",")
                .append(LActionOutKey.activeThreadCount).append(":").append(TcpRPCHandler.getActiveThreadCount());

        serviceMessage.setOut(SystemOutKey.XML_OUT_CONTENT, sb.toString());
    }
}
