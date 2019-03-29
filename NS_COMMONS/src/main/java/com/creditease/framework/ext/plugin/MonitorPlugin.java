package com.creditease.framework.ext.plugin;

import java.util.List;

/**
 * Created by liuyang on 2019-02-23.
 *
 * @author liuyang email:
 */
public interface MonitorPlugin extends Plugin{
    List<MonitorListener> getMonitorListeners();
}
