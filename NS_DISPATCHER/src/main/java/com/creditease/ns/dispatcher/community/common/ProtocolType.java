package com.creditease.ns.dispatcher.community.common;

public enum ProtocolType {
    HTTP("http"),
    HTTPS("https"),
    TCP("tcp");
    private final String value;

    private ProtocolType(String value) {
        this.value = value;
    }

    public String toValue() {
        return value;
    }
}
