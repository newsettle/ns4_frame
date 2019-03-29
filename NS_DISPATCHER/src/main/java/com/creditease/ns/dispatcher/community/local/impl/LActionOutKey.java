package com.creditease.ns.dispatcher.community.local.impl;

import com.creditease.framework.scope.OutKey;

public enum LActionOutKey implements OutKey {
    queueSize, activeThreadCount;

    @Override
    public String getDescription() {
        return null;
    }
}
