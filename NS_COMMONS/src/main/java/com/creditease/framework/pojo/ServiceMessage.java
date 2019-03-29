package com.creditease.framework.pojo;

import com.creditease.framework.exception.NSException;
import com.creditease.framework.scope.ExchangeKey;
import com.creditease.framework.scope.OutKey;
import com.creditease.ns.mq.model.Header;

import java.util.Map;

public interface ServiceMessage {
    Header getHeader();

    void setHeader(Header header) throws NSException;

    <T> T getParameterByType(String paramName, Class<T> clazz);

    String getParameter(String paramName);


    void setExchange(ExchangeKey key, Object value) throws NSException;

    String getExchange(ExchangeKey key) throws NSException;

    <T> T getExchangeByType(ExchangeKey key, Class<T> clazz) throws NSException;

    void setOut(OutKey outKey, Object value) throws NSException;

    String getOut(OutKey outKey) throws NSException;

    <T> T getOutByType(OutKey outKey, Class<T> clazz) throws NSException;

    void clearAllOut() throws NSException;

    void removeOut(OutKey outKey) throws NSException;

    String getJsonOut() throws NSException;

    void setOutHtmlAsRedirect(String url) throws NSException;

    void setOutHtmlAsWinOnload(Map<String, String> form, String url) throws NSException;

    void setOutHtmlConent(String htmlContent) throws NSException;
    
    void setOutPlainText(String plainText) throws NSException;

}
