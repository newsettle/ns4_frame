package com.creditease.ns.transporter.executor;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class ExecutorsTest {
    ThreadExecutorManager manager;

    @Before
    public void init() throws Exception {
        manager = new ThreadExecutorManager();
        manager.startUp();
        Executors.init(manager);
    }

    @Test
    public void makeFixedThreadPoolExecutor() throws Exception {
        final AtomicInteger singleCount = new AtomicInteger(0);
        final AtomicInteger fixedCount = new AtomicInteger(0);

        ThreadPoolExecutor fixed = Executors.getInstance().makeFixedThreadPoolExecutor("fixed",10);
        ThreadPoolExecutor single = Executors.getInstance().makeSingleThreadPoolExecutor("single");

        int needCount = new Random().nextInt(1000);
        System.out.println("needCount:" + needCount);
        for (int i = 0; i < needCount; i++) {
            fixed.execute(new Runnable() {
                @Override
                public void run() {
                    fixedCount.incrementAndGet();
                    System.out.println("来一段：fixed"+ System.currentTimeMillis());
                    try {
                        TimeUnit.MILLISECONDS.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            single.execute(new Runnable() {
                @Override
                public void run() {
                    singleCount.incrementAndGet();
                    System.out.println("来一段：single"+ System.currentTimeMillis());
                    try {
                        TimeUnit.MILLISECONDS.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }


        manager.destroy();
        Assert.assertEquals(needCount, singleCount.get());
        Assert.assertEquals(needCount, fixedCount.get());
    }
}