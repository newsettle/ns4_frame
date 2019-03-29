package com.creditease.ns.transporter.context;

import com.creditease.framework.ext.plugin.*;
import com.creditease.framework.ext.plugin.impl.DefaultPluginManager;
import com.creditease.framework.listener.ExceptionListener;
import com.creditease.framework.util.StringUtil;
import com.creditease.ns.framework.startup.LifeCycle;
import com.creditease.ns.framework.startup.LifeCycleManager;
import com.creditease.ns.log.NsLog;
import com.creditease.ns.log.util.PrintUtil;
import com.creditease.ns.transporter.buffer.BufferManager;
import com.creditease.ns.transporter.buffer.DefaultBufferManager;
import com.creditease.ns.transporter.buffer.DefaultBufferManagerProxy;
import com.creditease.ns.transporter.config.InQueueInfo;
import com.creditease.ns.transporter.config.XmlConfigManager;
import com.creditease.ns.transporter.executor.Executors;
import com.creditease.ns.transporter.executor.ThreadExecutorManager;
import com.creditease.ns.transporter.fetch.DefaultFetcherManager;
import com.creditease.ns.transporter.handle.DefaultHandlerManager;
import com.creditease.ns.transporter.plugin.TransporterEnvironment;
import com.creditease.ns.transporter.send.DefaultSenderManager;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class XmlAppTransporterContext implements LifeCycleManager, TransporterContext, Runnable {

    private static final NsLog frameLog = NsLog.getFramLog("Transport", "XmlAppTransporterContext");
    public static final String ALL_CMD_LINE_ARGS = "ALL_CMD_LINE_ARGS";

    private boolean isStarted;

    private String resoucePath;

    private static XmlAppTransporterContext self;

    private boolean isRunning = false;

    public static final String CONFIG_KEY = "configfile";

    public static final String PLUGIN_CLASSES_KEY = "NS_TT_PLUGINS";

    private Map<String, ExceptionListener> exceptionListeners = new LinkedHashMap<String, ExceptionListener>();

    private Stack<LifeCycle> lifeCycles = new Stack<LifeCycle>();

    private PluginManager pluginManager;

    private BufferManager bufferManager;

    private Map<String, List<MonitorListener>> monitorEventListeners = new ConcurrentHashMap<>();


    @Override
    public void startUp() {

        //启动ContextManager去加载配置文件，读取配置文件
        frameLog.info("# 启动框架各组件(5) #");
        XmlConfigManager configManager = new XmlConfigManager();
        configManager.setResourcePath(resoucePath);
        configManager.setXmlAppTransporterContext(this);
        configManager.startUp();
        registerLifeCycle(configManager);
        frameLog.info("# (1/6)启动ConfigManager OK #");

        Map<String, InQueueInfo> inQueueInfos = configManager.getQueueNameToQueueInfos();

        DefaultBufferManager defaultBufferManager = new DefaultBufferManager();
        defaultBufferManager.setQueueNameToInQueueInfo(inQueueInfos);
        defaultBufferManager.startUp();
        registerLifeCycle(defaultBufferManager);
        this.bufferManager = defaultBufferManager;
        frameLog.info("# (2/6)启动BufferManager OK #");

        DefaultSenderManager defaultSenderManager = new DefaultSenderManager();
        defaultSenderManager.setQueueNameToQueueInfos(inQueueInfos);
        defaultSenderManager.setBufferManager(defaultBufferManager);
        defaultSenderManager.startUp();
        registerLifeCycle(defaultSenderManager);
        frameLog.info("# (3/6)启动SenderManager OK #");

        DefaultHandlerManager handlerManager = new DefaultHandlerManager();
        handlerManager.setQueueNameToQueueInfos(inQueueInfos);
        handlerManager.setBufferManager(defaultBufferManager);
        handlerManager.setConfigManager(configManager);
        handlerManager.setContext(this);
        handlerManager.startUp();
        registerLifeCycle(handlerManager);
        frameLog.info("# (4/6)启动HandlerManager OK #");

        ThreadExecutorManager executorManager = new ThreadExecutorManager();
        handlerManager.startUp();
        Executors.init(executorManager);
        registerLifeCycle(executorManager);
        frameLog.info("# (5/6)启动ThreadPoolExecutorManager OK #");

        DefaultFetcherManager fetcherManager = new DefaultFetcherManager();
        fetcherManager.setQueueNameToQueueInfos(inQueueInfos);
        fetcherManager.setBufferManager(defaultBufferManager);
        fetcherManager.startUp();
        registerLifeCycle(fetcherManager);
        frameLog.info("# (6/6)启动FetcherManager OK #");

        isStarted = true;

        while (isRunning) {
            try {
                TimeUnit.MILLISECONDS.sleep(2000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        frameLog.info("# 启动框架各组件(6) OK #");
    }

    public void startPlugins(String[] args) {
        if (pluginManager == null) {
            pluginManager = DefaultPluginManager.getInstance();
            ((DefaultPluginManager) pluginManager).init();
        }

        //构造需要传入的Properties
        Properties properties = new Properties();
        if (args != null && args.length > 0) {
            properties.put(ALL_CMD_LINE_ARGS, StringUtil.arrayToString(args, " "));
        }

        //得到所有的插件类名
        String pluginsInfo = System.getProperty(PLUGIN_CLASSES_KEY);
        if (pluginsInfo != null) {
            String[] plugins = pluginsInfo.split(";");

            //注册插件
            for (int i = 0; i < plugins.length; i++) {
                frameLog.info("# 开始注册插件{} #", plugins[i]);
                pluginManager.registerPlugin(plugins[i], properties);
                frameLog.info("# 注册插件{}完毕#", plugins[i]);
            }

            //构造当前环境信息
            BufferManager proxy = new DefaultBufferManagerProxy(bufferManager);
            NSEnvironment nsEnvironment = new TransporterEnvironment(proxy);

            //加载插件
            List<Plugin> pluginList = pluginManager.listPlugins();
            for (int i = 0; i < pluginList.size(); i++) {
                Plugin plugin = pluginList.get(i);
                frameLog.info("# 开始加载插件{} #", plugin.getName());
                plugin.load(nsEnvironment);
                if (plugin instanceof MonitorPlugin) {
                    List<MonitorListener> monitorListeners = ((MonitorPlugin) plugin).getMonitorListeners();
                    monitorEventListeners.put(plugin.getName(), Collections.unmodifiableList(monitorListeners));
                    frameLog.info("# 发现监控插件 加载对应的监听器 {} {} #", monitorEventListeners.keySet().size(), monitorListeners.size());
                }
                frameLog.info("# 加载插件{}完毕 #", plugin.getName());
            }
        }
    }

    public void unloadPlugins() {
        List<Plugin> pluginList = pluginManager.listPlugins();
        for (int i = 0; i < pluginList.size(); i++) {
            Plugin plugin = pluginList.get(i);
            frameLog.info("# 开始卸载插件{} #", plugin.getName());
            plugin.unload();
            frameLog.info("# 卸载插件{}成功 #", plugin.getName());
        }
    }

    public static void main(String[] args) {
        PrintUtil.printNs4();
        PrintUtil.printJVM();
        String filePath = System.getProperty(CONFIG_KEY);
        if (filePath == null) {
            throw new RuntimeException("请指定配置文件位置");
        }
        frameLog.info("### TRANSPORTER FRAMEWORK START ###");
        //		XmlAppTransporterContext appTransporterContext = (XmlAppTransporterContext)XmlAppTransporterContext
        //		.getInstance();
        XmlAppTransporterContext appTransporterContext = new XmlAppTransporterContext();
        Runtime.getRuntime().addShutdownHook(new Thread(appTransporterContext));
        appTransporterContext.setResoucePath(filePath);
        appTransporterContext.startUp();
        frameLog.debug("# 配置文件路径:{} #", filePath);
        frameLog.info("### TRANSPORTER FRAMEWORK START  OK ###");

        //处理插件
        frameLog.info("# 开始加载NS4插件 #");
        appTransporterContext.startPlugins(args);
        frameLog.info("# 加载NS4插件完毕 #");
        self = appTransporterContext;

    }

    public String getResoucePath() {
        return resoucePath;
    }

    public void setResoucePath(String resoucePath) {
        this.resoucePath = resoucePath;
    }

    public static synchronized TransporterContext getInstance() {
        if (self == null) {
            self = new XmlAppTransporterContext();
        }
        return self;
    }


    @Override
    public void destroy() {
        while (!lifeCycles.empty()) {
            LifeCycle lifeCycle = (LifeCycle) lifeCycles.pop();
            try {
                lifeCycle.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        unloadPlugins();
        this.isRunning = false;
        this.isStarted = false;
        this.self = null;
    }


    public void registerExceptionListener(String queueName, ExceptionListener listener) {
        exceptionListeners.put(queueName, listener);
    }

    public ExceptionListener getExceptionListener(String queueName) {
        return exceptionListeners.get(queueName);
    }

    public void registerLifeCycle(LifeCycle lifeCycle) {
        this.lifeCycles.add(lifeCycle);
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        System.out.println("[Transporter] [开始关闭] [cost:" + (System.currentTimeMillis() - startTime) + "ms]");
        this.destroy();
        System.out.println("[Transporter] [关闭] [成功] [cost:" + (System.currentTimeMillis() - startTime) + "ms]");
    }

    public PluginManager getPluginManager() {
        return pluginManager;
    }

    public Map<String, List<MonitorListener>> getMonitorEventListeners() {
        return Collections.unmodifiableMap(monitorEventListeners);
    }
}
