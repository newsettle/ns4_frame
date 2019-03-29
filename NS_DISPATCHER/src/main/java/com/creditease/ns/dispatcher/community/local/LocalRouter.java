package com.creditease.ns.dispatcher.community.local;

import com.creditease.ns.dispatcher.core.ConfigCenter;
import com.creditease.ns.framework.startup.LifeCycle;
import com.creditease.ns.log.NsLog;
import org.apache.commons.lang.StringUtils;
import org.reflections.Reflections;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class LocalRouter implements LifeCycle {
    private static NsLog log = NsLog.getFramLog("Dispatcher", "本地路由");
    private  Map<String, Class> routerMap;

    @Override
    public void startUp() throws Exception {
        routerMap = new ConcurrentHashMap<>();
        log.info("本地路由启动");
        Reflections reflections = new Reflections("com.creditease.ns.dispatcher.community.local.impl");
        Set<Class<?>> classesList = reflections.getTypesAnnotatedWith(LocalActionMapping.class);
        for (Class localActionMappingClass : classesList) {
            LocalActionMapping annotation = (LocalActionMapping) localActionMappingClass.getAnnotation(LocalActionMapping.class);
            register(annotation.server(), localActionMappingClass);
            log.info("系统本地服务注入：{}->{}", annotation.server(), localActionMappingClass);
        }
        if (StringUtils.isNotBlank(ConfigCenter.getConfig.getLocalScanPackage())) {
            Reflections userDefineRef = new Reflections(ConfigCenter.getConfig.getLocalScanPackage());
            Set<Class<?>> userLocalWorker = userDefineRef.getTypesAnnotatedWith(LocalActionMapping.class);
            for (Class localActionMappingClass : userLocalWorker) {
                LocalActionMapping annotation = (LocalActionMapping) localActionMappingClass.getAnnotation(LocalActionMapping.class);
                register(annotation.server(), localActionMappingClass);
                log.info("自定义本地服务注入：{}->{}", annotation.server(), localActionMappingClass);
            }
        }

    }

    @Override
    public void destroy() throws Exception {

    }

    public void register(String name, Class localActionWorkerClazz) {
        routerMap.put(name, localActionWorkerClazz);
    }

    public Class route(String name) {
        return routerMap.get(name);
    }
}
