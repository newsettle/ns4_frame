package com.creditease.ns.controller;

import com.creditease.framework.pojo.DefaultServiceMessage;
import com.creditease.framework.scope.RequestScope;
import com.creditease.framework.scope.SystemRequestKey;
import com.creditease.framework.util.ProtoStuffSerializeUtil;
import com.creditease.ns.mq.MQTemplate;
import com.creditease.ns.mq.MQTemplates;
import com.creditease.ns.mq.exception.MQException;
import com.creditease.ns.mq.model.DeliveryMode;
import com.creditease.ns.mq.model.Header;
import com.creditease.ns.mq.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;


public class TestController implements Runnable {
	public static Logger logger = LoggerFactory.getLogger(TestController.class);
    public static final String queueName = "liuyang_controller";
    public static final String serverName = "testHtml";
    public String name;

    public TestController(int i) {
        name = String.valueOf(i);
    }

//    @Override
    public void run() {
        MQTemplate template = MQTemplates.singleRedisMQTemplate();
        while (true) {
            long start = System.currentTimeMillis();
            Message message = new Message();
            Header header = new Header(DeliveryMode.SYNC);
            header.setServerName(serverName);
            message.setHeader(header);
            Map params = new HashMap();
            params.put("name","ABCD");
            params.put("password", "ABCD");
            RequestScope scope = new RequestScope(params);
            scope.put(SystemRequestKey.SERVER_NAME, serverName);
            DefaultServiceMessage bodyMessage = new DefaultServiceMessage(scope);
            message.setBody(ProtoStuffSerializeUtil.serialize(bodyMessage, DefaultServiceMessage.class));
            try {

               template.publish(queueName, message);
               long cost = System.currentTimeMillis() - start;
               if(cost > 50)
            	   System.out.println("发送队列{}:cost:" + cost);
            } catch (MQException e) {
                e.printStackTrace();
            }
//            System.out.println(new String(messsage.getBody(), Charset.forName("UTF-8")));
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            TestController c = new TestController(i);
            Thread ct = new Thread(c);
            ct.start();
        }
    }
}
