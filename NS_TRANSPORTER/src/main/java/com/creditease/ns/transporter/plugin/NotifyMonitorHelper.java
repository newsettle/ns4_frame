package com.creditease.ns.transporter.plugin;

import com.creditease.framework.ext.plugin.MonitorEvent;
import com.creditease.framework.ext.plugin.MonitorListener;
import com.creditease.ns.log.NsLog;
import com.creditease.ns.transporter.context.XmlAppTransporterContext;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by liuyang on 2019-02-24.
 *
 * @author liuyang email
 */
public class NotifyMonitorHelper {
    private static NsLog frameLog = NsLog.getFramLog("Transport", "NotifyMonitorHelper");
    private static Map<String, List<MonitorListener>> monitorEventListeners;
    //    private static ThreadPoolExecutor putEventExecutor = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS,
//        new LinkedBlockingQueue<Runnable>());
    private static BlockingQueue<MonitorEvent> eventQueue = new LinkedBlockingQueue<>();
    private static ScheduledExecutorService getEventExecutor = Executors.newSingleThreadScheduledExecutor();
    private static XmlAppTransporterContext xmlAppTransporterContext = (XmlAppTransporterContext) XmlAppTransporterContext.getInstance();
    static {
        getEventExecutor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                try {
                    dispatcherEvents();
                } catch (Throwable t) {
                    frameLog.error("Dispatcher Event error：{}", t);
                }
            }
        }, 0, 10, TimeUnit.MILLISECONDS);

    }

    private static void dispatcherEvents() {

        List<MonitorEvent> events = new ArrayList<>();
        int eventNum = eventQueue.drainTo(events);
        for (int i = 0; i < events.size(); i++) {
            //保证能接受事件了才去初始化对应的listener 否则会出现过早加载的情况
            //在这里实例化 保证listener已经加载
            if (monitorEventListeners == null) {
                monitorEventListeners =
                    xmlAppTransporterContext.getMonitorEventListeners();
            }

            MonitorEvent monitorEvent = events.get(i);
            for (List<MonitorListener> listenerList : monitorEventListeners.values()) {
                for (Iterator<MonitorListener> iterator = listenerList.iterator(); iterator.hasNext(); ) {
                    MonitorListener monitorListener = iterator.next();
                    monitorListener.processMonitorEvent(monitorEvent);
                    frameLog.info("通知监控监听器 {}", monitorEvent);
                }
            }
        }

        frameLog.debug("本次通知监控监听器事件数量 {}", eventNum);
    }

    public static void notifyMonitors(final MonitorEvent monitorEvent) {
        try {
            eventQueue.put(monitorEvent);
            frameLog.debug("放入监控事件 {}", monitorEvent);
        } catch (InterruptedException e) {
            frameLog.error("放入监控事件出错 {}", monitorEvent, e);
        }
    }


}
