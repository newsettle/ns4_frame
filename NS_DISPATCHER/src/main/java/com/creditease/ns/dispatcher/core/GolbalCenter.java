package com.creditease.ns.dispatcher.core;

import com.creditease.ns.framework.startup.LifeCycle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GolbalCenter {
    private final static Map<String, LifeCycle> container = new ConcurrentHashMap<>();
    private final static List<LifeCycle> addSorted = new ArrayList<>();

    public static void add(LifeCycle item) {
        container.put(item.getClass().getName(), item);
        addSorted.add(item);
    }

    public static <T> T get(Class<T> clazz) {
        LifeCycle lifeCycle = container.get(clazz.getName());
        return clazz.cast(lifeCycle);
    }

    public static List<LifeCycle> getAddList() {
        return addSorted;
    }
}
