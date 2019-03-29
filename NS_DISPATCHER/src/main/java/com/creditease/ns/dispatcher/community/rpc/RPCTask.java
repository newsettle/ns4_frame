package com.creditease.ns.dispatcher.community.rpc;

import static io.netty.handler.codec.http.HttpHeaders.Names.*;

import java.util.HashMap;
import java.util.Map;

import com.creditease.framework.pojo.ServiceMessage;
import com.creditease.framework.work.ActionWorker;
import com.creditease.ns.dispatcher.community.common.ContentType;
import com.creditease.ns.dispatcher.community.common.Context;
import com.creditease.ns.dispatcher.community.common.ProtocolType;
import com.creditease.ns.dispatcher.community.common.Response;
import com.creditease.ns.dispatcher.community.common.error.ErrorHandler;
import com.creditease.ns.dispatcher.community.common.error.ErrorHandlerException;
import com.creditease.ns.dispatcher.community.common.http.HttpRequest;
import com.creditease.ns.dispatcher.community.common.http.HttpResponse;
import com.creditease.ns.dispatcher.community.common.tcp.TcpRequest;
import com.creditease.ns.dispatcher.community.common.tcp.TcpResponse;
import com.creditease.ns.dispatcher.community.local.LocalRouter;
import com.creditease.ns.dispatcher.core.ErrorMessageCenter;
import com.creditease.ns.dispatcher.community.common.error.ErrorType;
import com.creditease.ns.dispatcher.core.GolbalCenter;
import com.creditease.ns.dispatcher.core.MQCenter;
import com.creditease.ns.framework.spring.GenSpringPlugin;
import com.creditease.ns.mq.exception.MQTimeOutException;
import com.google.common.base.Charsets;

import com.creditease.framework.pojo.DefaultServiceMessage;
import com.creditease.framework.scope.*;
import com.creditease.framework.util.ProtoStuffSerializeUtil;
import com.creditease.ns.dispatcher.community.http.*;
import com.creditease.ns.dispatcher.core.ConfigCenter;
import com.creditease.ns.log.NsLog;
import com.creditease.ns.mq.MQTemplate;
import com.creditease.ns.mq.model.DeliveryMode;
import com.creditease.ns.mq.model.Header;
import com.creditease.ns.mq.model.Message;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

public class RPCTask implements Runnable {
    private final static NsLog flowLog = NsLog.getFlowLog("RPCTask", "rcp执行线程");

    private ChannelHandlerContext ctx;
    private Context context;

    public RPCTask(ChannelHandlerContext ctx, Context httpContext) {
        this.ctx = ctx;
        this.context = httpContext;
    }

    @Override
    public void run() {
        NsLog.setMsgId(context.getId());
        Response response = context.getResponse();
        try {
            String serverName = context.getRequest().getServerName();
            if (serverName == null) {
                flowLog.info("判断服务名为空，直接返回");
                throw new ErrorHandlerException(ErrorType.SERVER_NOT_FOUND);
            }
            flowLog.debug("解析服务名为:{}", serverName);
            RequestScope scope = null;
            if (context.getProtocolType() == ProtocolType.HTTP) {
                Map<String, String> params = ((HttpRequest) context.getRequest()).getParams();
                if (params == null) {
                    params = new HashMap<>();
                }
                scope = new RequestScope(params);
                scope.put(SystemRequestKey.SERVER_NAME, serverName);

                //是否启动out sign 类型，即返回内容带签名字段
                scope.put(SystemRequestKey.USE_SIGN_TYPE, ConfigCenter.getConfig.isJsonOutSignType());
                scope.put(SystemRequestKey.ALL_PARAMS, params);

                if (((HttpRequest) context.getRequest()).getQueryString() != null) {
                    scope.put(SystemRequestKey.QUERY_STRING, ((HttpRequest) context.getRequest()).getQueryString());
                }
            } else if (context.getProtocolType() == ProtocolType.TCP) {
                scope = new RequestScope(new HashMap<String, String>());
                scope.put(SystemRequestKey.SERVER_NAME, serverName);
                flowLog.debug("ESB_HEADER:{}", ((TcpRequest) context.getRequest()).getHeadMessage());
                scope.put(SystemRequestKey.ESB_HEADER, ((TcpRequest) context.getRequest()).getHeadMessage());
                flowLog.debug("ESB_XML:{}", ((TcpRequest) context.getRequest()).getXMLContent());
                scope.put(SystemRequestKey.ESB_XML, ((TcpRequest) context.getRequest()).getXMLContent());
            }

            DefaultServiceMessage bodyMessage = new DefaultServiceMessage(scope);

            bodyMessage.setPropertiesMap(new HashMap<String, String>());
            //应账务组需求，需要隐藏请求域的值
            bodyMessage.putProperty("hideRequestscope", ConfigCenter.getConfig.isHideRequestScope() + "");
            Message message = new Message();
            Header header = new Header(context.getId(), DeliveryMode.SYNC);
            header.setServerName(serverName);
            message.setHeader(header);
            message.setBody(ProtoStuffSerializeUtil.serializeForCommon(bodyMessage));
            flowLog.debug("接受请求到准备数据耗时:{}ms", System.currentTimeMillis() - context.getRequest().getAccessTimeStamp());

            if (context.getProtocolType() == ProtocolType.HTTP) {
                flowLog.info("http消息转化成框架内容消息 httpHeaders:{} messageHeaders:{}", ((HttpRequest) context.getRequest()).getHeaders(), message.getHeader());
                Message serviceMessage = runService(message);
                ((HttpResponse) context.getResponse()).writeOut(ctx.channel(), serviceMessage, (HttpResponse) response);
            } else if (context.getProtocolType() == ProtocolType.TCP) {
                flowLog.info("tcp消息转化成框架内容消息 tcpRequest:{} messageHeaders:{}", context.getRequest(), message.getHeader());
                Message serviceMessage = runService(message);
                ((TcpResponse) context.getResponse()).writeOut(ctx.channel(), serviceMessage, (TcpResponse) response);
            }
            flowLog.debug("接受请求到响应耗时:{}ms", System.currentTimeMillis() - context.getRequest().getAccessTimeStamp());


        } catch (Exception e) {
            e.printStackTrace();
            Exception exp = e;
            if (e instanceof MQTimeOutException) {
                exp = new ErrorHandlerException(e, ErrorType.REQUEST_TIMEOUT);
            } else {
                exp = new ErrorHandlerException(e, ErrorType.UNKOWN_ERROR);
            }

            ErrorHandler.handle((ErrorHandlerException) exp, ctx.channel(), context);

        }
    }

    private Message runService(Message message) throws Exception {
        Message response = null;
        //local
        Class localWorkActionClazz = GolbalCenter.get(LocalRouter.class).route(message.getHeader().getServerName());
        if (localWorkActionClazz != null) {
            Object workActionObject;
            if (ConfigCenter.getConfig.isLocalSpring()) {
                workActionObject = GolbalCenter.get(GenSpringPlugin.class).getBeanByClassName(localWorkActionClazz);
            } else {
                workActionObject = localWorkActionClazz.newInstance();
            }
            if (workActionObject instanceof ActionWorker) {
                ServiceMessage serviceMessage = (ServiceMessage) ProtoStuffSerializeUtil.unSerializeForCommon(message.getBody());
                ((ActionWorker) workActionObject).doWork(serviceMessage);
                message.setBody(ProtoStuffSerializeUtil.serializeForCommon(serviceMessage));
                response = message;
            }
        } else if (!ConfigCenter.getConfig.isLocalOnly()) {

            MQTemplate template = GolbalCenter.get(MQCenter.class).getMqTemplate();
            response = template.publish(ConfigCenter.getConfig.getQueueName(), message, ConfigCenter.getConfig.getTimeout());
        } else {
            //在开启独立本地小程序且本地扯未匹配到服务时
            ServiceMessage serviceMessage = (ServiceMessage) ProtoStuffSerializeUtil.unSerializeForCommon(message.getBody());
            serviceMessage.setOut(SystemOutKey.RETURN_CODE, SystemRetInfo.CTRL_NOT_FOUND_SEVICE_ERROR);
            message.setBody(ProtoStuffSerializeUtil.serializeForCommon(serviceMessage));
            response = message;
        }
        return response;
    }


    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public Context getContext() {
        return context;
    }
}
