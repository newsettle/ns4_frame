package com.creditease.ns.dispatcher.community.http;

import com.creditease.ns.dispatcher.community.common.ContentType;
import com.creditease.ns.dispatcher.community.common.Context;
import com.creditease.ns.dispatcher.community.common.error.ErrorHandler;
import com.creditease.ns.dispatcher.community.common.error.ErrorHandlerException;
import com.creditease.ns.dispatcher.community.common.error.ErrorType;
import com.creditease.ns.dispatcher.community.common.http.HttpContext;
import com.creditease.ns.dispatcher.community.common.http.HttpRequest;
import com.creditease.ns.dispatcher.community.log.NSLogginHandler;
import com.creditease.ns.dispatcher.convertor.json.JSONConvertor;
import com.creditease.ns.dispatcher.core.ConfigCenter;
import com.creditease.ns.dispatcher.netty.ext.ChannelConfig;
import com.creditease.ns.log.NsLog;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.*;
import io.netty.handler.codec.http.multipart.InterfaceHttpData.HttpDataType;
import io.netty.util.CharsetUtil;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.*;

import static io.netty.buffer.Unpooled.copiedBuffer;
import static io.netty.handler.codec.http.HttpHeaders.Names.*;


public class HttpDispatcherServerHandler extends ChannelInboundHandlerAdapter {
    static NsLog flowLog = NsLog.getFlowLog("Dispatcher", "HTTP转换");
    static NsLog contentLog = NsLog.getLog("ns.dispatcher.content", "Dispatcher", "打印请求内容");

    private static final HttpDataFactory factory = new DefaultHttpDataFactory(false);
    private io.netty.handler.codec.http.HttpRequest httpRequest;
    private ChannelConfig channelConfig;

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        //        if (decoder != null) {
        //            decoder.cleanFiles();
        //        }
    }

    public HttpDispatcherServerHandler() {

    }

    public HttpDispatcherServerHandler(ChannelConfig channelConfig) {
        this.channelConfig = channelConfig;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Context context = new HttpContext();
        context.setId(UUID.randomUUID().toString());
        if (msg instanceof io.netty.handler.codec.http.HttpRequest) {
            context.getRequest().setAccessTimeStamp(System.currentTimeMillis());
            httpRequest = (io.netty.handler.codec.http.HttpRequest) msg;
            String requestUri = httpRequest.getUri();

            if (requestUri.lastIndexOf("favicon.ico") > 0) {
                writeResponse(ctx.channel(), "");
                return;
            }
            NsLog.setMsgId(context.getId());
            NSLogginHandler nsLogginHandler = new NSLogginHandler();
            flowLog.info("接收到新请求 {} {} {}", requestUri, httpRequest.headers(), nsLogginHandler.format(ctx, "省略message信息"));


            //uri
            ((HttpRequest) context.getRequest()).setUri(requestUri);
            //http method
            if (httpRequest.getMethod() == HttpMethod.GET) {
                ((HttpRequest) context.getRequest()).setMethod(HttpRequestMethod.GET);
            } else if (httpRequest.getMethod() == HttpMethod.POST) {
                ((HttpRequest) context.getRequest()).setMethod(HttpRequestMethod.POST);
            } else if (httpRequest.getMethod() == HttpMethod.DELETE) {
                ((HttpRequest) context.getRequest()).setMethod(HttpRequestMethod.DELETE);
            } else if (httpRequest.getMethod() == HttpMethod.PUT) {
                ((HttpRequest) context.getRequest()).setMethod(HttpRequestMethod.PUT);
            } else if (httpRequest.getMethod() == HttpMethod.HEAD) {
                ((HttpRequest) context.getRequest()).setMethod(HttpRequestMethod.HEAD);
            }

            //header
            Map<String, List<String>> headers = new HashMap<String, List<String>>();
            HttpHeaders httpHeaders = httpRequest.headers();
            if (!httpHeaders.isEmpty()) {
                for (Map.Entry<String, String> header : httpHeaders) {

                    List<String> values = headers.get(header.getKey());
                    if (values == null) {
                        values = new ArrayList<String>();
                        headers.put(header.getKey(), values);
                    }
                    values.add(header.getValue());
                }
            }
            ((HttpRequest) context.getRequest()).setHeaders(headers);
            Map<String, String> parameters = new HashMap<String, String>();
            ((HttpRequest) context.getRequest()).setParams(parameters);


            QueryStringDecoder queryStringDecoder = new QueryStringDecoder(httpRequest.getUri());
            int pathEndPos = httpRequest.getUri().indexOf('?');
            if (pathEndPos >= 0) {
                String queryString = queryStringDecoder.uri().substring(queryStringDecoder.path().length() + 1);
                if (queryString != null) {
                    ((HttpRequest) context.getRequest()).setQueryString(queryString);
                }
            }

            Map<String, List<String>> params = queryStringDecoder.parameters();
            if (params != null && params.size() != 0) {
                for (Map.Entry<String, List<String>> entry : params.entrySet()) {
                    String key = entry.getKey();
                    for (String value : entry.getValue()) {
                        parameters.put(key, value);
                    }
                }
            }

            //ip
            context.setFromIP(ctx.channel().remoteAddress().toString());
            context.setToIP(ctx.channel().localAddress().toString());

            String ip = httpHeaders.get("X-Forwarded-For");
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
                ip = httpHeaders.get("Proxy-Client-IP");
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
                ip = httpHeaders.get("WL-Proxy-Client-IP");
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
                ip = httpHeaders.get("HTTP_CLIENT_IP");
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
                ip = httpHeaders.get("HTTP_X_FORWARDED_FOR");
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
                ip = ctx.channel().remoteAddress().toString();
            context.setRealRemoteIp(ip);
            try {
                if (channelConfig != null && channelConfig.isPosibleHttps()) {
                    HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(factory, httpRequest);
                    List<InterfaceHttpData> datas = decoder.getBodyHttpDatas();
                    for (Iterator iterator = datas.iterator(); iterator.hasNext(); ) {
                        InterfaceHttpData interfaceHttpData = (InterfaceHttpData) iterator.next(); //其实就是attribute
                        // keyvalue对 如果是这个 就可以获取其中的参数


                        if (interfaceHttpData.getHttpDataType() == HttpDataType.Attribute) {
                            Attribute attribute = (Attribute) interfaceHttpData;
                            parameters.put(attribute.getName(), attribute.getValue());
                            flowLog.debug("https请求得到post中的参数:{}-{}", attribute.getName(), attribute.getValue());
                        }
                    }
                }

            } catch (HttpPostRequestDecoder.ErrorDataDecoderException e1) {
                ErrorHandler.handle(new ErrorHandlerException(e1, ErrorType.MESSAGE_FORMAT), ctx.channel());
                ctx.channel().close();
                return;
            } catch (HttpPostRequestDecoder.IncompatibleDataDecoderException e1) {
                ErrorHandler.handle(new ErrorHandlerException(e1, ErrorType.MESSAGE_FORMAT), ctx.channel());
                return;
            }

        }


        if (msg instanceof HttpContent) {
            String contentString = null;
            HttpContent httpContent = (HttpContent) msg;

            ByteBuf content = httpContent.content();
            if (content.isReadable()) {
                contentString = content.toString(CharsetUtil.UTF_8);
                if (StringUtils.isNotBlank(contentString)) {

                    if (JSONConvertor.isValidJSON(contentString)) {
                        //post json
                        Map<String, String> jsonParameters = JSONConvertor.jsonToMap(contentString);
                        Map<String, String> parameters = ((HttpRequest) context.getRequest()).getParams();
                        for (Map.Entry<String, String> entry : jsonParameters.entrySet()) {
                            String key = entry.getKey();
                            if (parameters.containsKey(key)) {
                                flowLog.error("请求出现重复Key:{}", key);
                            }
                            ((HttpRequest) context.getRequest()).getParams().put(key, entry.getValue());
                        }
                        ((HttpRequest) context.getRequest()).setPostContent(contentString);
                    } else {
                        //post form
                        QueryStringDecoder queryStringDecoder = new QueryStringDecoder("/tmp?" + contentString);
                        Map<String, List<String>> params = queryStringDecoder.parameters();
                        if (params != null && params.size() != 0) {
                            Map<String, String> parameters = ((HttpRequest) context.getRequest()).getParams();
                            for (Map.Entry<String, List<String>> entry : params.entrySet()) {
                                String key = entry.getKey();
                                if (parameters.containsKey(key)) {
                                    flowLog.error("请求出现重复Key:{}", key);
                                } else {
                                    for (String value : entry.getValue()) {
                                        parameters.put(key, value);
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }
        flowLog.debug("完整请求参数:{}", context);
        //ServerName
        String serverName = getURIPath(((HttpRequest) context.getRequest()).getUri());
        if (serverName == null || serverName.equals("")) {
            serverName = ((HttpRequest) context.getRequest()).getParams().get("server");
        }
        context.getRequest().setServerName(serverName);

        ctx.fireChannelRead(context);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof IOException) {
            if (cause.getMessage().contains("远程主机强迫关闭了一个现有的连接") || cause.getMessage().contains("An existing connection was forcibly closed by the remote host")) {
                //                logger.info("远端主机:" + ctx.channel().remoteAddress().toString() + " 已关闭链接。");
            } else {
                cause.printStackTrace();
            }
        } else {
            cause.printStackTrace();
        }
        ctx.close();
    }


    private void writeResponse(Channel channel, String responseContent) {
        writeResponse(channel, responseContent, HttpResponseStatus.OK);
    }

    private void writeResponse(Channel channel, String responseContent, HttpResponseStatus status) {
        // Convert the response content to a ChannelBuffer.
        ByteBuf buf = copiedBuffer(responseContent, CharsetUtil.UTF_8);

        // Decide whether to close the connection or not.
        boolean close = false;
        if (httpRequest != null && null != httpRequest.headers() && null != httpRequest.headers().get(CONNECTION) && null != httpRequest.getProtocolVersion()) {
            close = HttpHeaders.Values.CLOSE.equalsIgnoreCase(httpRequest.headers().get(CONNECTION))
                    || httpRequest.getProtocolVersion().equals(HttpVersion.HTTP_1_0)
                    && !HttpHeaders.Values.KEEP_ALIVE.equalsIgnoreCase(httpRequest.headers().get(CONNECTION));
        }


        // Build the response object.
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, status, buf);
        response.headers().set(CONTENT_TYPE, ContentType.JSON.toValue());

        if (!close) {
            // There's no need to add 'Content-Length' header
            // if this is the last response.
            response.headers().set(CONTENT_LENGTH, buf.readableBytes());
        }

        Set<Cookie> cookies;
        String value = httpRequest.headers().get(COOKIE);
        if (value == null) {
            cookies = Collections.emptySet();
        } else {
            cookies = CookieDecoder.decode(value);
        }
        if (!cookies.isEmpty()) {
            // Reset the cookies if necessary.
            for (Cookie cookie : cookies) {
                response.headers().add(SET_COOKIE, ServerCookieEncoder.encode(cookie));
            }
        }
        // Write the response.
        ChannelFuture future = channel.writeAndFlush(response);
        // Close the connection after the write operation is done if necessary.
        if (close) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }

    private String getURIPath(String uri) {
        if (StringUtils.isNotBlank(uri)) {
            if (uri.contains("?")) {
                int index = uri.indexOf("?");
                uri = uri.substring(0, index);
            }
            if (ConfigCenter.getConfig.isLongServerName()) {
                if (uri.startsWith("/")) {
                    uri = uri.replaceFirst("/", "");
                }
                return uri;
            } else {
                String[] urlParts = uri.split("/");
                // example /TestService2/CommandChain2
                if (urlParts.length == 2) {
                    return urlParts[1];
                } else if (urlParts.length == 3) {
                    return urlParts[2];
                } else {
                    return null;
                }
            }
        }
        return null;
    }
}
