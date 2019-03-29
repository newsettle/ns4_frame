package com.creditease.ns.dispatcher.community.common;


public enum ContentType {
    TEXT("text/plain;charset=UTF-8"),
    HTML("text/html;charset=UTF-8"),
    XML("text/xml"),
    JSON("application/json;charset=UTF-8"),
    BYTES("bytes");
    private final String value;

    private ContentType(String value) {
        this.value = value;
    }

    public String toValue() {
        return value;
    }

}
