package com.creditease.framework.scope;


public enum  SystemOutKey implements OutKey  {
    RETURN_CODE,
    HTML_REDIRECT_URL,
    HTML_WINDOW_ONLOAD,
    HTML_SELF_CONTENT,
    XML_OUT_CONTENT,
    PLAIN_TEXT_CONTENT,
    SIGN_INFO,
    SINGLE_OUT;

    @Override
    public String getDescription() {
        return null;
    }
}
