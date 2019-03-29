package com.creditease.framework.ext.plugin;

/**
 * Created by liuyang on 2019-02-22.
 *
 * @author liuyang
 */
public interface ExecutablePlugin extends Plugin {
    Object execute(Object... args);
}
