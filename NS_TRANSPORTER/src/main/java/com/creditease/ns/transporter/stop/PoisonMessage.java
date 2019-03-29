package com.creditease.ns.transporter.stop;

import com.creditease.framework.exception.NSException;
import com.creditease.framework.pojo.ServiceMessage;
import com.creditease.framework.scope.ExchangeKey;
import com.creditease.framework.scope.OutKey;
import com.creditease.ns.mq.model.Header;
import com.creditease.ns.mq.model.Message;

import java.util.Map;

public class PoisonMessage implements ServiceMessage {
    @Override
    public Header getHeader() {
        Header header = new Header();
        header.setMessageID(Thread.currentThread().getName());
        //设置发送类型为毒药，只是在本层传递，不向下游传递
        header.setDeliveryMode(3);
        return header;
    }

    @Override
    public void setHeader(Header header) throws NSException {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T getParameterByType(String paramName, Class<T> clazz) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getParameter(String paramName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setExchange(ExchangeKey key, Object value) throws NSException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getExchange(ExchangeKey key) throws NSException {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T getExchangeByType(ExchangeKey key, Class<T> clazz) throws NSException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setOut(OutKey outKey, Object value) throws NSException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getOut(OutKey outKey) throws NSException {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T getOutByType(OutKey outKey, Class<T> clazz) throws NSException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clearAllOut() throws NSException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeOut(OutKey outKey) throws NSException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getJsonOut() throws NSException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setOutHtmlAsRedirect(String url) throws NSException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setOutHtmlAsWinOnload(Map<String, String> form, String url) throws NSException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setOutHtmlConent(String htmlContent) throws NSException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setOutPlainText(String plainText) throws NSException {
        throw new UnsupportedOperationException();
    }
}
