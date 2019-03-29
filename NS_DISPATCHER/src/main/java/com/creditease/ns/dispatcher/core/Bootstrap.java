package com.creditease.ns.dispatcher.core;


import com.creditease.ns.dispatcher.community.http.HttpServer;
import com.creditease.ns.dispatcher.community.local.LocalRouter;
import com.creditease.ns.dispatcher.community.tcp.TcpServer;
import com.creditease.ns.framework.spring.GenSpringPlugin;
import com.creditease.ns.framework.startup.LifeCycle;
import com.creditease.ns.log.NsLog;
import com.creditease.ns.log.util.PrintUtil;

public class Bootstrap {
    private static NsLog initLog = NsLog.getFramLog("Dispatcher", "分发器");

    public static void main(String[] args) throws Exception {
        PrintUtil.printNs4();
        PrintUtil.printJVM();
        initLog.info("开启式启动");

        GolbalCenter.add(new MQCenter());
        GolbalCenter.add(new LocalRouter());
        GolbalCenter.add(new ErrorMessageCenter());


        if (ConfigCenter.getConfig.isLocalSpring()) {
            GolbalCenter.add(new GenSpringPlugin());
        }

        if ("http".equals(ConfigCenter.getConfig.getProtocolType())) {
            GolbalCenter.add(new HttpServer());
        } else if ("tcp".equals(ConfigCenter.getConfig.getProtocolType())) {
            GolbalCenter.add(new TcpServer());
        } else {
            GolbalCenter.add(new HttpServer());
        }


        for (LifeCycle startPart : GolbalCenter.getAddList()) {
            startPart.startUp();
        }
    }
}
