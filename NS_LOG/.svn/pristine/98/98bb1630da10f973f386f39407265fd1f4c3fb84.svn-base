package com.creditease.ns.log;

import org.junit.Before;
import org.junit.Test;

/**
 * Date: 15-5-27
 * Time: 下午10:21
 */
public class LogTest {
    @Before
    public void init (){
        new LogSetting().setLogPrefix("henn").setLogPath("/Users/henn/log/").init();
        Log.setPrimayKey(String.valueOf(System.currentTimeMillis()));
    }
    @Test
    public void testLogProcess() throws Exception {
        Log.logProcess("Update Stream");
    }

    @Test
    public void testLogProcess2() throws Exception {

    }

    @Test
    public void testLogException() throws Exception {
        Log.setUniqKey();
        Log.setSubPrimary("subKey");
        Log.logError("Update Stream", new RuntimeException("abcdef"));
        Log.logError(ErrorCode.TEST, "测试错误");
    }

    @Test
    public void testLogException2() throws Exception{
        Log.logError(ErrorCode.TEST, "测试错误");
        Log.logError(ErrorCode.TEST, "测试错误");
    }

    @Test
    public void testPerform() throws Exception{
        Perform perform =  new Perform();
        Thread subThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Perform perform =  new Perform();
                while (true){
                    try {
                        perform.performMethodOnce();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        subThread.start();

        while (true){
            perform.performMethod();
        }
    }




}
