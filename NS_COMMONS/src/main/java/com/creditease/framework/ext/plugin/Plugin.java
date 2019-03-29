package com.creditease.framework.ext.plugin;

import java.util.Properties;

/**
 * Created by liuyang on 2019-02-22.
 *
 * @author liuyang email
 */
public interface Plugin {
    String getName();

    boolean isLoaded();

    void load(NSEnvironment nsEnvironment);

    void unload();

    void register(String name, Properties props);

    Properties getProperties();
}
