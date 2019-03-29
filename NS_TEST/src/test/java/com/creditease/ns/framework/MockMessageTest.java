package com.creditease.ns.framework;

import com.creditease.framework.pojo.ServiceMessage;
import com.creditease.framework.scope.SystemOutKey;
import com.creditease.framework.scope.SystemRetInfo;
import com.creditease.ns.mq.model.Header;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

public class MockMessageTest {

    @Test
    public void getHeader() {
        ServiceMessage message = new MockMessage();
        Header header = message.getHeader();
        Assert.assertNotNull(header);
    }

    @Test
    public void getJsonOut() throws Exception {
        ServiceMessage message = new MockMessage();
        message.setOut(SystemOutKey.SIGN_INFO,"sfsdf");
        message.setOut(SystemOutKey.RETURN_CODE, SystemRetInfo.NORMAL);
        HashMap<Object, Object> objectObjectHashMap = new HashMap<>();
        objectObjectHashMap.put("a", "b");
        objectObjectHashMap.put("c", "f");
        objectObjectHashMap.put("e", "z");
        message.setOut(TestOutKey.TEST_MAP, objectObjectHashMap);
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        message.setOut(TestOutKey.TEST_LIST, list);
        message.setOut(TestOutKey.TEST_STRING,"abcdefg");
        System.out.println(message.getJsonOut());
    }
}