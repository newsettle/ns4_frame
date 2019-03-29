package com.creditease.ns.mq;

import com.creditease.ns.mq.exception.MQException;
import com.creditease.ns.mq.model.Header;
import com.creditease.ns.mq.model.Message;
import org.junit.Assert;
import org.junit.Test;

public class TestMQTemplates {
    @Test
    public void testPublishWithExpired() {
        MQTemplate mqTemplate = MQTemplates.singleRedisMQTemplate();
        long startTime = System.currentTimeMillis();
        Message message = new Message();
        Header header = new Header();
        header.setReplyTo("ooo");
        message.setHeader(header);
        try {
            mqTemplate.publish("abc", message, 10);
        } catch (MQException e) {
            System.out.println("throw exception "+ e.getClass().getName());
        }
        long endTime = System.currentTimeMillis() - startTime;
        Assert.assertTrue((endTime >= 10000 && endTime < 12000));
    }
}
