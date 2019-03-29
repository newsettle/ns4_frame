package com.creditease.ns.dispatcher.core;

import com.creditease.framework.util.PropertiesUtil;
import com.creditease.ns.dispatcher.community.common.error.ErrorType;
import com.creditease.ns.framework.startup.LifeCycle;
import com.creditease.ns.log.NsLog;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ErrorMessageCenter implements LifeCycle {
    private static NsLog initLog = NsLog.getFramLog("Dispatcher", "错误信息匹配器");
    private static Map<ErrorType, String> container;
    private static String defaultMessage = "{\"retCode\":\"500\",\"retInfo\":\"请求的服务超限\"}";

    public String getErrorMessage(ErrorType errorType) {
        String errorMessage = container.get(errorType);
        if (errorMessage == null) {
            return defaultMessage;
        }
        return errorMessage;
    }

    @Override
    public void startUp() throws Exception {
        try {
            container = new ConcurrentHashMap<>();
            PropertiesUtil pu = new PropertiesUtil("ns_dispatcher_error.properties");
            container.put(ErrorType.MESSAGE_FORMAT, pu.getString(ErrorType.MESSAGE_FORMAT.toString(), defaultMessage));
            container.put(ErrorType.REQUEST_TIMEOUT, pu.getString(ErrorType.REQUEST_TIMEOUT.toString(), defaultMessage));
            container.put(ErrorType.REQUEST_LIMIT, pu.getString(ErrorType.REQUEST_LIMIT.toString(), defaultMessage));
            container.put(ErrorType.SERVER_NOT_FOUND, pu.getString(ErrorType.SERVER_NOT_FOUND.toString(), defaultMessage));
            container.put(ErrorType.UNKOWN_ERROR, pu.getString(ErrorType.UNKOWN_ERROR.toString(), defaultMessage));
            for (Map.Entry<ErrorType, String> entries : container.entrySet()) {
                initLog.info("异常消息配置：{},{}", entries.getKey(), entries.getValue());
            }
        } catch (Exception e) {
            initLog.info("loading ns_dispatcher_error.properties error", e);
        }
    }

    @Override
    public void destroy() throws Exception {

    }
}
