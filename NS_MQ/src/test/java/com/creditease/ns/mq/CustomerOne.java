package com.creditease.ns.mq;

import com.creditease.ns.mq.exception.MQException;
import com.creditease.ns.mq.model.Message;

import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CustomerOne implements Runnable {
	public static Logger logger = LoggerFactory.getLogger(CustomerOne.class);
    public static final String queueName = "abcd";

    public String name;

    public CustomerOne(int i) {
        this.name = String.valueOf(i);
    }

    @Override
    public void run() {
        MQTemplate template = MQTemplates.singleRedisMQTemplate();
        while (true) {
            try {
                long start = System.currentTimeMillis();
                Message message = template.receive(queueName);
                message.setBody("万岁".getBytes(Charset.forName("UTF-8")));
                template.reply(message);
                long cost = System.currentTimeMillis() - start;
                if(cost > 50)
                	logger.debug("出现耗时:[{}] cost:{}ms",message.getHeader().getMessageID(),cost);
            } catch (MQException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            CustomerOne p = new CustomerOne(i);
            Thread pt = new Thread(p);
            pt.start();
        }
    }
}
