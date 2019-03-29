package com.creditease.framework.scope;

/**
 * 用于保存一些已经确定的Key
 */
public class SystemRequestKey {
    private SystemRequestKey() {
    }

    public static final String SERVER_NAME = "SYSTEM_SERVER_NAME";
    public static final String QUERY_STRING = "SYSTEM_QUERY_STRING";
    public static final String ALL_PARAMS = "SYSTEM_ALL_PARAMS";
    public static final String FROM_IP = "SYSTEM_FROM_IP";
    public static final String TO_IP = "SYSTEM_TO_IP";
    public static final String REMOTE_CLIENT_IP = "SYSTEM_REMOTE_CLIENT_IP";
    public static final String USE_SIGN_TYPE = "SYSTEM_USE_SIGN_TYPE";
    public static final String ESB_HEADER = "SYSTEM_ESB_HEADER";
    public static final String ESB_XML = "SYSTEM_ESB_XML";
}
