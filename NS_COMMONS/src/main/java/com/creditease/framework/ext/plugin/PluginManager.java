package com.creditease.framework.ext.plugin;

import java.util.List;
import java.util.Properties;

/**
 * Created by liuyang on 2019-02-22.
 *
 * @author liuyang
 */
public interface PluginManager {
    void registerPlugin(String className, Properties pluginProps);

    void load(String pluginName);

    Plugin getPlugin(String pluginName);

    boolean isLoaded(String pluginName);

    boolean unload(String pluginName);

    List<Plugin> listPlugins();
}
