package com.creditease.framework.ext.plugin.impl;

import com.creditease.framework.ext.plugin.Plugin;
import com.creditease.framework.ext.plugin.PluginManager;
import com.creditease.framework.util.Assert;
import com.creditease.ns.log.spi.TransporterLog;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by liuyang on 2019-02-22.
 *
 * @author liuyang
 */
public class DefaultPluginManager implements PluginManager {
    public static PluginManager instance;

    private Map<String, Plugin> plugins = new ConcurrentHashMap<>();

    public static synchronized PluginManager getInstance() {
        if (instance == null) {
            instance = new DefaultPluginManager();
        }
        return instance;
    }

    @Override
    public void registerPlugin(String className, Properties pluginProps) {
        Assert.notNull(className, "注册插件传入的ClassName不能为null");
        Class<Plugin> pluginClass = null;
        try {
            pluginClass = (Class<Plugin>) loadClass(className);
        } catch (Exception e) {
            TransporterLog.logSystemWarn("注册插件-加载插件失败,没有找到对应的Class {} {}", className, pluginProps, e);
            return;
        }
        if (pluginClass == null) {
            TransporterLog.logSystemWarn("注册插件-加载插件失败,加载不到对应的Class {} {}", className, pluginProps);
            return;
        }

        try {
            Plugin plugin = pluginClass.newInstance();
            plugin.register(pluginClass.getCanonicalName(), pluginProps);
            plugins.put(pluginClass.getCanonicalName(), plugin);
        } catch (InstantiationException e) {
            TransporterLog.logSystemWarn("注册插件-加载插件失败,实例化插件类出现错误 {} {}", className, pluginProps, e);
            return;
        } catch (IllegalAccessException e) {
            TransporterLog.logSystemWarn("注册插件-加载插件失败,无权限获取插件类 {} {}", className, pluginProps, e);
            return;
        }
    }

    @Override
    public void load(String pluginName) {

    }

    @Override
    public Plugin getPlugin(String pluginName) {
        return null;
    }

    @Override
    public boolean isLoaded(String pluginName) {
        return false;
    }

    @Override
    public boolean unload(String pluginName) {
        return false;
    }

    @Override
    public List<Plugin> listPlugins() {
        return Collections.unmodifiableList(Collections.list(Collections.enumeration(plugins.values())));
    }

    public void init() {
    }

    private Class loadClass(ClassLoader cl, String className) {
        if (cl == null) {
            cl = this.getClass().getClassLoader();
        }
        try {
            return cl.loadClass(className);
        } catch (ClassNotFoundException e1) {
            return null;
        } catch (NoClassDefFoundError e1) {
            return null;
        } catch (Exception e) {
            e.printStackTrace(); // this is unexpected
            return null;
        }

    }

    private Class loadClass(String className) {
        return bestEffortLoadClass(null, className);
    }

    private Class bestEffortLoadClass(ClassLoader lastGuaranteedClassLoader,
        String className) {
        if (lastGuaranteedClassLoader == null) {
            lastGuaranteedClassLoader = this.getClass().getClassLoader();
        }
        Class result = loadClass(lastGuaranteedClassLoader, className);
        if (result != null) {
            return result;
        }
        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        if (tccl != lastGuaranteedClassLoader) {
            result = loadClass(tccl, className);
        }
        if (result != null) {
            return result;
        }

        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e1) {
            return null;
        } catch (NoClassDefFoundError e1) {
            return null;
        } catch (Exception e) {
            e.printStackTrace(); // this is unexpected
            return null;
        }
    }
}
