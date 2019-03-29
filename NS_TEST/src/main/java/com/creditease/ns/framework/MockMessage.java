package com.creditease.ns.framework;

import com.creditease.framework.exception.NSException;
import com.creditease.framework.pojo.ServiceMessage;
import com.creditease.framework.scope.*;
import com.creditease.ns.mq.model.DeliveryMode;
import com.creditease.ns.mq.model.Header;

import java.util.HashMap;
import java.util.Map;

public class MockMessage implements ServiceMessage {
    private Header header = new Header(DeliveryMode.SYNC);
    private Map<String, Object> requestScope = new HashMap<>();
    private Map<ExchangeKey, Object> exchangeScope = new HashMap<>();
    private Map<OutKey, Object> outScope = new HashMap<>();

    public void setRequestScope(Map<String, Object> requestScope) {
        this.requestScope = requestScope;
    }

    public void setExchangeScope(Map<ExchangeKey, Object> exchangeScope) {
        this.exchangeScope = exchangeScope;
    }

    public void setOutScope(Map<OutKey, Object> outScope) {
        this.outScope = outScope;
    }

    @Override
    public Header getHeader() {
        return header;
    }

    @Override
    public void setHeader(Header header) throws NSException {
        this.header = header;
    }

    @Override
    public <T> T getParameterByType(String paramName, Class<T> clazz) {
        return clazz.cast(requestScope.get(paramName));
    }

    public void setParameter(String paramName, Object value) {
        requestScope.put(paramName, value);
    }

    @Override
    public String getParameter(String paramName) {
        return (String) requestScope.get(paramName);
    }

    @Override
    public void setExchange(ExchangeKey key, Object value) throws NSException {
        exchangeScope.put(key, value);
    }

    @Override
    public String getExchange(ExchangeKey key) throws NSException {
        return (String) exchangeScope.get(key);
    }

    @Override
    public <T> T getExchangeByType(ExchangeKey key, Class<T> clazz) throws NSException {
        return clazz.cast(exchangeScope.get(key));
    }

    @Override
    public void setOut(OutKey outKey, Object value) throws NSException {
        outScope.put(outKey, value);
    }

    @Override
    public String getOut(OutKey outKey) throws NSException {
        return (String) outScope.get(outKey);
    }

    public Object getOutAsObject(OutKey outKey) throws NSException {
        return outScope.get(outKey);
    }

    @Override
    public <T> T getOutByType(OutKey outKey, Class<T> clazz) throws NSException {
        return clazz.cast(outScope.get(outKey));
    }

    @Override
    public void clearAllOut() throws NSException {
        outScope.clear();
    }

    @Override
    public void removeOut(OutKey outKey) throws NSException {
        outScope.remove(outKey);
        outScope.remove(outKey);
    }

    @Override
    public String getJsonOut() throws NSException {

        StringBuilder jsonBuilder = new StringBuilder();
        if (outScope != null && !outScope.isEmpty()) {
            jsonBuilder.append("{");
            Object resultInfo = this.getOutAsObject(SystemOutKey.RETURN_CODE);
            Boolean useSign = (Boolean) requestScope.get(SystemRequestKey.USE_SIGN_TYPE);
            if (useSign == null || !useSign) {
                if (resultInfo != null) {
                    jsonBuilder.append(resultInfo);
                } else {
                    jsonBuilder.append("\"retCode\":\"" + SystemRetInfo.NO_RETURN_CODE.getCode() + "\",\"retInfo\":\"" + SystemRetInfo.NO_RETURN_CODE.getMsg() + "\"");
                }
            } else {
                String signInfo = this.getOut(SystemOutKey.SIGN_INFO);
                if (resultInfo != null) {
                    jsonBuilder.append("\"signInfo\":\"" + (signInfo == null ? "" : signInfo) + "\",");
                    jsonBuilder.append(resultInfo);
                } else {
                    jsonBuilder.append("\"signInfo\":\"" + (signInfo == null ? "" : signInfo) + "\",");
                    jsonBuilder.append("\"retCode\":\"" + SystemRetInfo.NO_RETURN_CODE.getCode() + "\",\"retInfo\":\"" + SystemRetInfo.NO_RETURN_CODE.getMsg() + "\"");
                }
            }



            Object singeOut = outScope.get(SystemOutKey.SINGLE_OUT.toString()) == null ? outScope.get(SystemOutKey.SINGLE_OUT) : outScope.get(SystemOutKey.SINGLE_OUT.toString());
            if (singeOut != null) {
                jsonBuilder.append(",\"data\":");
                jsonBuilder.append(singeOut);
                jsonBuilder.append("}");
            } else {
                jsonBuilder.append(",\"data\":{");
                for (Map.Entry<OutKey, Object> entry : outScope.entrySet()) {
                    if (!SystemOutKey.RETURN_CODE.toString().equals(entry.getKey())) {
                        jsonBuilder.append("\"").append(entry.getKey()).append("\"").append(":").append(entry.getValue()).append(",");
                    }
                }
                if (jsonBuilder.charAt(jsonBuilder.length() - 1) == ',') {
                    jsonBuilder.deleteCharAt(jsonBuilder.length() - 1);
                }
                jsonBuilder.append("}}");
            }



        }

        return jsonBuilder.toString();
    }

    @Override
    public void setOutHtmlAsRedirect(String url) throws NSException {
        setOut(SystemOutKey.HTML_REDIRECT_URL, url);
    }

    @Override
    public void setOutHtmlAsWinOnload(Map<String, String> form, String url) throws NSException {
        StringBuilder sb = new StringBuilder("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">");
        sb.append("<html><body>").append("<form id=\"payBillForm\" action=\"").append(url).append("\" method=\"post\">");
        for (Map.Entry<String, String> param : form.entrySet()) {
            sb.append("<input type=\"hidden\" name=\"").append(param.getKey()).append("\" value=\"").append(param.getValue()).append("\"/>");
        }
        sb.append("</form></body>");
        sb.append("<script language=\"javascript\" type=\"text/javascript\"> window.onload=function(){ document.getElementById(\"payBillForm\").submit();}</script></html>");
        setOut(SystemOutKey.HTML_WINDOW_ONLOAD, sb.toString());
    }

    @Override
    public void setOutHtmlConent(String htmlContent) throws NSException {
        setOut(SystemOutKey.HTML_SELF_CONTENT, htmlContent);
    }

    @Override
    public void setOutPlainText(String plainText) throws NSException {
        setOut(SystemOutKey.PLAIN_TEXT_CONTENT, plainText);
    }
}
