package com.creditease.ns.mq;

import com.creditease.ns.mq.exception.MQException;
import com.creditease.ns.mq.model.Message;

import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ProductorOne implements Runnable {
	public static Logger logger = LoggerFactory.getLogger(ProductorOne.class);
    public static final String queueName = "abcd";
    public String name;

    public ProductorOne(int i) {
        name = String.valueOf(i);
    }

    @Override
    public void run() {
        MQTemplate template = MQTemplates.singleRedisMQTemplate();
        while (true) {
            Message messsage = null;
            try {
                long start = System.currentTimeMillis();
                messsage = template.publish(queueName, "中华人民共和国".getBytes(Charset.forName("UTF-8")));
                long cost = System.currentTimeMillis() - start;
                if(cost > 50)	
                	logger.debug("+++++++++++++++++++++++++++++++++++++++3333==========出现耗时[{}] cost:{}",messsage.getHeader().getMessageID(),cost);
            } catch (MQException e) {
                e.printStackTrace();
            }
//            System.out.println(new String(messsage.getBody(), Charset.forName("UTF-8")));
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            ProductorOne c = new ProductorOne(i);
            Thread ct = new Thread(c);
            ct.start();
        }
    }
}
