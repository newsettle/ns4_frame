package com.creditease.framework.ext.plugin;

import com.creditease.framework.util.Assert;

import java.util.Properties;

/**
 * Created by liuyang on 2019-02-22.
 *
 * @author liuyang
 */
public class TestPlugin implements Plugin {
    private Properties properties = new Properties();
    private String pluginName;
    private boolean loaded;


    @Override
    public String getName() {
        return pluginName;
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }

    @Override
    public void load(NSEnvironment nsEnvironment) {
        loaded = true;
    }

    @Override
    public void unload() {

    }

    @Override
    public void register(String name, Properties props) {
        Assert.notNull(props, "注册插件传入的Properties不能为null");
        properties.putAll(props);
        pluginName = name;
    }

    @Override
    public Properties getProperties() {
        return properties;
    }

}
