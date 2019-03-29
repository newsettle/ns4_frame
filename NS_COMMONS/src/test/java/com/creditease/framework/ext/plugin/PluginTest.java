package com.creditease.framework.ext.plugin;

import com.creditease.framework.ext.plugin.impl.DefaultPluginManager;
import com.creditease.framework.util.StringUtil;
import com.creditease.ns.mq.exception.MQConnectionException;
import com.creditease.ns.mq.model.Message;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * Created by liuyang on 2019-02-22.
 *
 * @author liuyang
 * <p>
 * 测试目的:
 * 可以启动自己对ns的扩展功能
 * 可以启动monitorPlugin
 * 可以获取到monitorEvent并处理
 */
public class PluginTest {
    static PluginManager pluginManager;
    static NSEnvironment nsEnvironment;
    static Properties properties;

    @BeforeClass
    public static void setupManager() {
        pluginManager = DefaultPluginManager.getInstance();
        ((DefaultPluginManager) pluginManager).init();
        System.setProperty("NS_TT_PLUGINS", "com.creditease.framework.ext.plugin.TestPlugin");
        nsEnvironment = new TestEnvironment();

        String[] args = new String[]{"-ping 12345", "-app-nam smsResonpse"};
        properties = new Properties();
        properties.put("ALL_CMD_LINE_ARGS", StringUtil.arrayToString(args, " "));
    }

    @Test
    public void testPluginActions() {

        String pluginsInfo = System.getProperty("NS_TT_PLUGINS");
        String[] plugins = pluginsInfo.split(";");
        for (int i = 0; i < plugins.length; i++) {
            pluginManager.registerPlugin(plugins[i], properties);
        }


        List<Plugin> pluginList = pluginManager.listPlugins();
        Assert.assertNotNull(pluginList);
        Assert.assertEquals(1, pluginList.size());

        for (int i = 0; i < pluginList.size(); i++) {
            Plugin plugin = pluginList.get(i);
            Assert.assertNotNull(plugin.getProperties().getProperty("ALL_CMD_LINE_ARGS"));
            Assert.assertEquals("-ping 12345 -app-nam smsResonpse", plugin.getProperties().getProperty(
                "ALL_CMD_LINE_ARGS"));
            Assert.assertNotNull(plugin.getName());
            plugin.load(nsEnvironment);
            Assert.assertEquals(true, plugin.isLoaded());
            Assert.assertEquals("com.creditease.framework.ext.plugin.TestPlugin", plugin.getName());
        }
    }


    @Test
    public void testProcessEvent() {
        //构造MonitorPlugin
        //添加monitorplugin
        pluginManager.registerPlugin("com.creditease.framework.ext.plugin.TestMonitorPlugin",
            properties);
        List<Plugin> pluginList = pluginManager.listPlugins();
        Assert.assertNotNull(pluginList);
        Assert.assertEquals(1, pluginList.size());


        for (int i = 0; i < pluginList.size(); i++) {
            Plugin plugin = pluginList.get(i);
            Assert.assertNotNull(plugin.getProperties().getProperty("ALL_CMD_LINE_ARGS"));
            Assert.assertEquals("-ping 12345 -app-nam smsResonpse", plugin.getProperties().getProperty(
                "ALL_CMD_LINE_ARGS"));
            Assert.assertNotNull(plugin.getName());
            plugin.load(nsEnvironment);
            Assert.assertEquals(true, plugin.isLoaded());
            Assert.assertEquals("com.creditease.framework.ext.plugin.TestMonitorPlugin", plugin.getName());
        //循环判断是monitorplugin 则创建monitorEvent 发给MonitorListener处理
            if (plugin instanceof MonitorPlugin) {
                MonitorPlugin monitorPlugin = (MonitorPlugin) plugin;
                if (monitorPlugin.isLoaded()) {
                    List<MonitorListener> monitorListeners = monitorPlugin.getMonitorListeners();
                    for (Iterator<MonitorListener> iterator = monitorListeners.iterator(); iterator.hasNext(); ) {
                        MonitorListener next = iterator.next();
                        Message message = Mockito.mock(Message.class);
                        MonitorEvent monitorEvent = null;
                        try {
                            monitorEvent = MonitorEvent.createEvent(MonitorEvent.Type.MESSAGE_RECEIVED,
                                message);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (MQConnectionException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InstantiationException e) {
                            e.printStackTrace();
                        }
                        Assert.assertNotNull(monitorEvent);
                        next.processMonitorEvent(monitorEvent);
                    }
                }
            }
        }
    }
}
