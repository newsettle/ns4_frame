package com.creditease.ns.log;

import org.perf4j.StopWatch;
import org.perf4j.slf4j.Slf4JStopWatch;

import java.util.concurrent.TimeUnit;

/**
 * Date: 15-5-28
 * Time: 下午8:24
 */
public class Perform {
    public void performMethod() throws Exception{
        StopWatch stopWatch = new Slf4JStopWatch(LogUtils.getFullyMethodName());
        TimeUnit.MILLISECONDS.sleep(100);
        stopWatch.stop();
    }

    public void performMethodOnce() throws Exception{
        StopWatch stopWatch = new Slf4JStopWatch(LogUtils.getFullyMethodName());
        TimeUnit.MILLISECONDS.sleep(200);
        stopWatch.stop();
    }

}
