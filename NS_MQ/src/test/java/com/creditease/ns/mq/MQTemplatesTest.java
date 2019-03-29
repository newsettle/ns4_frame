package com.creditease.ns.mq;

import com.creditease.ns.mq.exception.MQInitError;
import com.creditease.ns.mq.model.Message;
import org.junit.Assert;
import org.junit.Test;
import redis.clients.util.SafeEncoder;

import java.nio.charset.Charset;

public class MQTemplatesTest {
    String single = "127.0.0.1:6379";

    @Test
    public void testClusterRedisMQTemplate() throws Exception {
        MQTemplate template = MQTemplates.clusterRedisMQTemplate();
        String queue = "testClusterRedisMQTemplate";
        template.send(queue, SafeEncoder.encode("你好"));
        Message message = template.receive("testClusterRedisMQTemplate");
        String result = new String(message.getBody(), Charset.forName("UTF-8"));
        Assert.assertEquals("你好", result);
        System.out.println(result);
    }

    @Test
    public void testClusterRedisCustom() throws Exception {
        MQTemplate template = MQTemplates.clusterRedisCustom().setHostAndPorts("127.0.0.1:7000;127.0.0.1:7001;127.0.0.1:7002").build();
        String queue = "SingleRedisCustum";
        template.send(queue, SafeEncoder.encode("你好"));
        Message message = template.receive("SingleRedisCustum");
        String result = new String(message.getBody(), Charset.forName("UTF-8"));
        Assert.assertEquals("你好", result);
        System.out.println(result);

    }

    @Test
    public void testClusterRedisCustomInit() throws Exception {
        //ERROR
        try {
            MQTemplate template = MQTemplates.clusterRedisCustom().setHostAndPorts("127.0.0.1:8080;127.0.0.1:8081;127.0.0.1:8081").build();
        } catch (Exception e) {
            Assert.assertEquals("redis服务不可用 hostAndPorts:127.0.0.1:8080;127.0.0.1:8081;127.0.0.1:8081",e.getMessage());
        }

        //NORMAL
//        try {
//            MQTemplate template = MQTemplates.clusterRedisCustom().setHostAndPorts("127.0.0.1:8080;127.0.0.1:8081;127.0.0.1:8081").build();
//        } catch (Exception e) {
//            Assert.assertEquals("redis服务不可用 hostAndPorts:127.0.0.1:8080;127.0.0.1:8081;127.0.0.1:8081",e.getMessage());
//        }


    }


    @Test
    public void testSingleRedisMQTemplate() throws Exception {
        MQTemplate template = MQTemplates.singleRedisMQTemplate();
        String queue = "testSingleRedisMQTemplate";
        template.send(queue, SafeEncoder.encode("你好"));
        Message message = template.receive("testSingleRedisMQTemplate");
        String result = new String(message.getBody(), Charset.forName("UTF-8"));
        Assert.assertEquals("你好", result);
        System.out.println(result);
    }

    @Test
    public void testSingleRedisCustom() throws Exception {
        MQTemplate template = MQTemplates.singleRedisCustom().setHostAndPort("127.0.0.1:6379").build();
        String queue = "SingleRedisCustum";
        template.send(queue, SafeEncoder.encode("你好"));
        Message message = template.receive("SingleRedisCustum");
        String result = new String(message.getBody(), Charset.forName("UTF-8"));
        Assert.assertEquals("你好", result);
        System.out.println(result);
    }

    @Test
    public void testSingleRedisCustomInit() throws Exception {
        //ERROR redis
        MQTemplate template = null;
        try {
            template = MQTemplates.singleRedisCustom().setHostAndPort(single).setPassword("iBQClmpRzXJt0IqBrEnC").build();
        } catch (MQInitError e) {
            Assert.assertEquals("redis服务不可用 hostAndPort:"+single,e.getMessage());
        }

        //ERROR redis
        MQTemplate template2 = null;
        template2 = MQTemplates.singleRedisCustom().setHostAndPort(single).build();

    }
}