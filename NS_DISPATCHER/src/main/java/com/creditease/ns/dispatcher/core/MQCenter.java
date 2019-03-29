package com.creditease.ns.dispatcher.core;

import com.creditease.ns.framework.startup.LifeCycle;
import com.creditease.ns.mq.MQTemplate;
import com.creditease.ns.mq.MQTemplates;

public class MQCenter implements LifeCycle {
    private MQTemplate mqTemplate;

    @Override
    public void startUp() throws Exception {
        if (!ConfigCenter.getConfig.isLocalOnly()) {
            mqTemplate = MQTemplates.defaultTemplate();
        }
    }

    @Override
    public void destroy() throws Exception {
    }

    public MQTemplate getMqTemplate() {
        return mqTemplate;
    }
}
