package com.creditease.ns.dispatcher.core;


import com.creditease.framework.util.PropertiesUtil;
import com.creditease.ns.log.NsLog;
import com.creditease.ns.mq.MQTemplate;
import com.creditease.ns.mq.MQTemplates;
import org.apache.commons.lang.StringUtils;

public class ConfigCenter {
    private static NsLog initLog = NsLog.getFramLog("Dispatcher", "分发器");
    public static ConfigCenter getConfig = new ConfigCenter();

    private String protocolType;
    /**
     * http相关
     */
    private int httpPort;
    private final int defaultHttpPort = 8027;
    private int httpsPort;
    private final int defaultHttpsPort = 8043;
    private final int defaultTcpPort = 8543;
    private String caPath;
    private String keyPath;
    //长域名，不做uri解析
    private boolean longServerName;

    /**
     * tcp相关
     */
    private int tcpPort;


    /**
     * rpc相关
     */
    private final String DEFAULT_QUEUENAME = "controller";
    private String queueName;
    private int rpcExecutorSize;
    private int rpcQueueLength;
    private String dispatcherFileCheck;
    private boolean jsonOutSignType;
    private String localScanPackage;
    private boolean localSpring;
    private boolean localOnly;


    private int timeout = 3600;
    private boolean isHideRequestScope = false;

    private String configPath;


    private ConfigCenter() {
        try {

            PropertiesUtil pu = null;

            if (configPath == null) {
                pu = new PropertiesUtil("ns_dispatcher.properties");
            } else {
                pu = new PropertiesUtil(configPath);
            }

            this.protocolType = pu.getString("protocol.type", "http");
            initLog.info("启动通讯协议类型，protocol.type:{}", this.protocolType);


            int _httpPort = pu.getInt("http.port", defaultHttpPort);
            this.setHttpPort(_httpPort);
            initLog.info("读取配置:HTTP端口号:{}", _httpPort);

            this.caPath = pu.getString("https.ca.path");
            this.keyPath = pu.getString("https.key.path");
            if (!StringUtils.isEmpty(caPath) && !StringUtils.isEmpty(keyPath)) {
                int _httpsPort = pu.getInt("https.port", defaultHttpsPort);
                this.setHttpsPort(_httpsPort);
                initLog.info("读取配置:HTTPS端口号:{}", _httpsPort);
            }


            this.longServerName = pu.getBoolean("http.longservername", false);

            initLog.info("http服务名解析使用长域名，不解析，http.longservername:{}", this.longServerName);


            this.timeout = pu.getInt("dispatcher.send.timeout", timeout);

            initLog.info("读取配置:处理超时时间，dispatcher.send.timeout:{}", timeout);

            String _queueName = pu.getString("dispatcher.queuename", DEFAULT_QUEUENAME);
            this.setQueueName(_queueName);
            initLog.info("读取配置:发送Controller队列名称，dispatcher.queuename:{}", _queueName);

            this.isHideRequestScope = pu.getBoolean("rpc.servicemessage.hiderequestscope", false);

            initLog.info("读取配置:是否隐藏请求域，rpc.servicemessage.hiderequestscope:{}", this.isHideRequestScope);

            this.dispatcherFileCheck = pu.getString("dispathcer.filecheck");

            initLog.info("读取配置:系统稳定性文件检测路径，dispathcer.filecheck:{}", this.dispatcherFileCheck);

            this.rpcQueueLength = pu.getInt("rpc.queue.length", 200);

            initLog.info("读取配置:RPC等待队列长度，rpc.queue.length:{}", this.rpcQueueLength);

            this.rpcExecutorSize = pu.getInt("rpc.executor.size", 50);

            initLog.info("读取配置:业务执行线程数,rpc.executor.size:{}", this.rpcExecutorSize);

            this.jsonOutSignType = pu.getBoolean("dispatcher.out.sign.type", false);

            initLog.info("读取配置:是否开启输出签名模式，dispatcher.out.sign.type:{}", this.jsonOutSignType);

            this.localScanPackage = pu.getString("dispatcher.local.scan.package");

            initLog.info("本地小服务，扫描包名，dispatcher.local.scan.package:{}", this.localScanPackage);

            this.localSpring = pu.getBoolean("dispatcher.local.spring", false);

            initLog.info("本地小服务，是否打开spring容器，dispatcher.local.spring:{}", this.localSpring);

            this.localOnly = pu.getBoolean("dispatcher.local.only", false);

            initLog.info("单独启动小程序，不发送controller，dispatcher.local.only:{}", this.localOnly);

            this.tcpPort = pu.getInt("tcp.port", defaultTcpPort);

            initLog.info("tcp服务端口号，tcp.port:{}", this.tcpPort);

        } catch (Exception e) {
            throw new RuntimeException("load ns_dispatcher.properties error", e);
        }
    }

    public int getHttpPort() {
        return httpPort;
    }

    public void setHttpPort(int httpPort) {
        this.httpPort = httpPort;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public int getHttpsPort() {
        return httpsPort;
    }

    public void setHttpsPort(int httpsPort) {
        this.httpsPort = httpsPort;
    }

    public String getCaPath() {
        return caPath;
    }

    public String getKeyPath() {
        return keyPath;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public boolean isHideRequestScope() {
        return isHideRequestScope;
    }

    public void setHideRequestScope(boolean isHideRequestScope) {
        this.isHideRequestScope = isHideRequestScope;
    }

    public String getConfigPath() {
        return configPath;
    }

    public void setConfigPath(String configPath) {
        this.configPath = configPath;
    }

    public int getRpcExecutorSize() {
        return rpcExecutorSize;
    }

    public void setRpcExecutorSize(int rpcExecutorSize) {
        this.rpcExecutorSize = rpcExecutorSize;
    }

    public int getRpcQueueLength() {
        return rpcQueueLength;
    }

    public void setRpcQueueLength(int rpcQueueLength) {
        this.rpcQueueLength = rpcQueueLength;
    }

    public String getDispatcherFileCheck() {
        return dispatcherFileCheck;
    }

    public void setDispatcherFileCheck(String dispatcherFileCheck) {
        this.dispatcherFileCheck = dispatcherFileCheck;
    }

    public boolean isJsonOutSignType() {
        return jsonOutSignType;
    }

    public void setJsonOutSignType(boolean jsonOutSignType) {
        this.jsonOutSignType = jsonOutSignType;
    }

    public String getLocalScanPackage() {
        return localScanPackage;
    }

    public void setLocalScanPackage(String localScanPackage) {
        this.localScanPackage = localScanPackage;
    }

    public boolean isLocalSpring() {
        return localSpring;
    }

    public void setLocalSpring(boolean localSpring) {
        this.localSpring = localSpring;
    }

    public int getTcpPort() {
        return tcpPort;
    }

    public void setTcpPort(int tcpPort) {
        this.tcpPort = tcpPort;
    }

    public String getProtocolType() {
        return protocolType;
    }

    public void setProtocolType(String protocolType) {
        this.protocolType = protocolType;
    }

    public boolean isLocalOnly() {
        return localOnly;
    }

    public void setLocalOnly(boolean localOnly) {
        this.localOnly = localOnly;
    }

    public boolean isLongServerName() {
        return longServerName;
    }

    public void setLongServerName(boolean longServerName) {
        this.longServerName = longServerName;
    }
}
