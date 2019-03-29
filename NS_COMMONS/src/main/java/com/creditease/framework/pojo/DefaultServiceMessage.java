package com.creditease.framework.pojo;

import com.creditease.framework.exception.NSException;
import com.creditease.framework.scope.*;
import com.creditease.framework.util.JsonUtil;
import com.creditease.framework.util.ProtoStuffSerializeUtil;
import com.creditease.ns.mq.model.Header;

import java.util.Map;

public class DefaultServiceMessage implements ServiceMessage {
    private Header header;
    private final RequestScope requestScope;
    private final ExchangeScope exchangeScope;
    private final OutScope outScope;

    private Map<String, String> propertiesMap;

    @Override
    public Header getHeader() {
        return header;
    }


    @Override
    public void setHeader(Header header) {
        this.header = header;
    }

    @Override
    public String getParameter(String paramName) {
        return (String) requestScope.get(paramName);
    }

    @Override
    public <T> T getParameterByType(String paramName, Class<T> clazz) {
        return clazz.cast(requestScope.get(paramName));
    }

    @Override
    public void setExchange(ExchangeKey key, Object value) throws NSException {
        try {
            checkExchangeKey(key);
        } catch (Exception e) {
            throw new NSException(" [放入Exchange域值] [失败] [" + key + "] [" + value + "]", e);
        }
        try {
            exchangeScope.put(key.toString(), ProtoStuffSerializeUtil.serializeForCommon(value));
        } catch (Exception e) {
            throw new NSException("[放入Exchange域值] [对象转换protobuffer数据失败] [" + key + "] [" + value + "]", e);
        }
    }


    @Override
    public String getExchange(ExchangeKey key) throws NSException {
        return getExchangeByType(key, String.class);
    }

    /**
     * 2015年11月20日 账务组发现了这样一种情况
     * 他们有个泛型list 原本是不跨queue传递的 但是后来改业务需要跨queue传递
     * 但是他们以前由于是本地传递list 所以直接修改list中的对象的值 然后再一个业务链
     * 中的下一个节点 会自然拿到的修改过值得list 但是跨queue就出现问题了
     * 因为我们是本地缓存存了一份list 而同样的一个list会放在跨queue传递的exchanger中
     * 如果不调用setExchange方法重新把修改过的list再放入到跨queue传递的exchanger中
     * 那么就会出现跨queue传递 依然看到的是第一次未修改前的List
     * <p>
     * 所以这里需要注意 所有要跨queue传递的对象 如果修改后 必须再setExchange一下 否则
     * 修改会丢失
     *
     * @param key
     * @param clazz
     * @return
     * @throws NSException 2015年11月20日下午10:12:24
     */

    @Override
    public synchronized <T> T  getExchangeByType(ExchangeKey key, Class<T> clazz) throws NSException {
        try {
            checkExchangeKey(key);
        } catch (Exception e) {
            throw new NSException(" [获取Exchange域值] [失败] [" + key + "] [" + clazz + "]", e);
        }
        byte[] contentbytes = (byte[]) exchangeScope.get(key.toString());
        if (contentbytes == null) {
            return null;
        }

        try {
            return (T) ProtoStuffSerializeUtil.unSerializeForCommon(contentbytes);
        } catch (Exception e) {
            throw new NSException("[获取Exchange域值] [protobuff转换对象失败] [" + key + "] [" + clazz + "] [" + contentbytes.length + "]", e);
        }
    }


    @Override
    public void setOut(OutKey outKey, Object value) throws NSException {
        try {
            checkOutKey(outKey);
        } catch (Exception e) {
            throw new NSException(" [放入Out域] [失败] [" + outKey + "] [" + value + "]", e);
        }
        try {
            if (value instanceof RetInfo) {
                RetInfo retInfo = (RetInfo) value;
                //outScope.put(outKey.toString(), JsonUtil.jsonFromObject("\"retCode\":\"" + retInfo.getCode() + "\", \"retInfo\":\"" + retInfo.getMsg()));
                // dingzhiwei 修改返回码的值少双引号bug
                outScope.put(outKey.toString(), JsonUtil.jsonFromObject("\"retCode\":\"" + retInfo.getCode() + "\", \"retInfo\":\"" + retInfo.getMsg() + "\""));
            } else {
                outScope.put(outKey.toString(), JsonUtil.jsonFromObject(value));
            }
        } catch (Exception e) {
            throw new NSException("[放入Out域] [对象转换json数据失败] [" + outKey + "] [" + value + "]", e);
        }
    }

    @Override
    public String getOut(OutKey outKey) throws NSException {
        return getOutByType(outKey, String.class);
    }

    @Override
    public <T> T getOutByType(OutKey outKey, Class<T> clazz) throws NSException {
        try {
            checkOutKey(outKey);
        } catch (Exception e) {
            throw new NSException(" [获取Out域值] [失败] [" + outKey + "] [" + clazz + "]", e);
        }

        String json = outScope.get(outKey.toString());
        if (json == null) {
            return null;
        }
        try {
            return JsonUtil.objectFromJson(json, clazz);
        } catch (Exception e) {
            throw new NSException(" [获取Out域值] [json转对象失败] [" + outKey + "] [" + clazz + "][" + json + "]", e);
        }
    }

    public String toString() {

        if (this.propertiesMap != null) {
            String hideRequestscope = this.propertiesMap.get("hideRequestscope");

            if ("true".equalsIgnoreCase(hideRequestscope)) {
                return "messageheader:" + header + " exchangeScope:" + exchangeScope + " outScope:" + outScope + "";
            }
        }

        return "messageheader:" + header + " requestScope:" + requestScope + " exchangeScope:" + exchangeScope + " outScope:" + outScope + "";
    }

    private void checkExchangeKey(ExchangeKey key) throws Exception {
        if (key == null) {
            throw new NullPointerException("不接受null值作为key");
        }

        if (!key.getClass().isEnum()) {
            throw new IllegalArgumentException("传入的ExchangeKey" + key + "不是Enum");
        }
    }

    private void checkOutKey(OutKey key) throws Exception {
        if (key == null) {
            throw new NullPointerException("不接受null值作为key");
        }

        if (!key.getClass().isEnum()) {
            throw new IllegalArgumentException("传入的OutKey" + key + "不是Enum");
        }
    }

    public DefaultServiceMessage(RequestScope requestScope) {
        this.requestScope = requestScope;
        this.exchangeScope = new ExchangeScope();
        this.outScope = new OutScope();
    }

    public DefaultServiceMessage(RequestScope requestScope, ExchangeScope exchangeScope, OutScope outScope) {
        this.requestScope = requestScope;
        this.exchangeScope = exchangeScope;
        this.outScope = outScope;
    }

    @Override
    public String getJsonOut() throws NSException {

        StringBuilder jsonBuilder = new StringBuilder();
        if (outScope != null && !outScope.isEmpty()) {
            jsonBuilder.append("{");
            String resultInfo = this.getOut(SystemOutKey.RETURN_CODE);
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


            String singeOut = outScope.get(SystemOutKey.SINGLE_OUT.toString()) == null?outScope.get(SystemOutKey.SINGLE_OUT):outScope.get(SystemOutKey.SINGLE_OUT.toString());
            if (singeOut != null) {
                jsonBuilder.append(",\"data\":");
                jsonBuilder.append(singeOut);
                jsonBuilder.append("}");
            } else {
                jsonBuilder.append(",\"data\":{");
                for (Map.Entry<String, String> entry : outScope.entrySet()) {
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
    public void clearAllOut() throws NSException {
        this.outScope.clear();
    }

    @Override
    public void removeOut(OutKey outKey) throws NSException {
        try {
            checkOutKey(outKey);
        } catch (Exception e) {
            throw new NSException(" [删除Out域] [失败] [" + outKey + "] [", e);
        }
        try {
            outScope.remove(outKey.toString());
        } catch (Exception e) {
            throw new NSException("[删除Out域] [失败] [" + outKey + "]", e);
        }
    }


    @Override
    public void setOutPlainText(String plainText) throws NSException {
        setOut(SystemOutKey.PLAIN_TEXT_CONTENT, plainText);
    }

    public void putProperty(String key, String value) {
        this.propertiesMap.put(key, value);
    }

    public String getPropertyValue(String key) {
        return this.propertiesMap.get(key);
    }


    public Map<String, String> getPropertiesMap() {
        return propertiesMap;
    }


    public void setPropertiesMap(Map<String, String> propertiesMap) {
        this.propertiesMap = propertiesMap;
    }


}
