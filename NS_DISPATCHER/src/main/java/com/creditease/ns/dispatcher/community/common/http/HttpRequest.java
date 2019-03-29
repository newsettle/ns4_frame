package com.creditease.ns.dispatcher.community.common.http;

import com.creditease.ns.dispatcher.community.common.ContentType;
import com.creditease.ns.dispatcher.community.common.Request;
import com.creditease.ns.dispatcher.community.http.HttpRequestMethod;

import java.util.List;
import java.util.Map;

public class HttpRequest extends Request {
    private ContentType contentType = ContentType.HTML;
    //请求URI
    private String uri;
    //请求方法
    private HttpRequestMethod method;
    //post表单内容
    private String postContent;
    //header信息
    private Map<String, List<String>> headers;
    //请求参数
    private Map<String, String> params;
    private String queryString;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public HttpRequestMethod getMethod() {
        return method;
    }

    public void setMethod(HttpRequestMethod method) {
        this.method = method;
    }

    public String getPostContent() {
        return postContent;
    }

    public void setPostContent(String postContent) {
        this.postContent = postContent;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }
}
