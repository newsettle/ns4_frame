package com.creditease.ns.framework;

import com.creditease.framework.scope.OutKey;

public enum  TestOutKey implements OutKey {
    TEST_MAP,
    TEST_LIST,
    TEST_STRING;

    @Override
    public String getDescription() {
        return null;
    }
}
