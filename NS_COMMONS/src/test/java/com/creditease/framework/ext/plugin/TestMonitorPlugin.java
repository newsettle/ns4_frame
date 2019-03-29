package com.creditease.framework.ext.plugin;

import com.creditease.ns.log.spi.TransporterLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuyang on 2019-02-23.
 *
 * @author liuyang
 */
public class TestMonitorPlugin extends TestPlugin implements MonitorPlugin {
    private List<MonitorListener> monitorListeners = new ArrayList<>();

    @Override
    public void load(NSEnvironment nsEnvironment) {
        super.load(nsEnvironment);
        monitorListeners.add(new MonitorListener() {
            @Override
            public void processMonitorEvent(MonitorEvent monitorEvent) {
                TransporterLog.logRecordInfo("处理监控事件 步骤:{} 消息:{}",
                    monitorEvent.getEventType(), monitorEvent.getMessage());
            }
        });
    }

    @Override
    public List<MonitorListener> getMonitorListeners() {
        return monitorListeners;
    }

}
