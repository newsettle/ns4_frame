package com.creditease.framework.util;

/**
 * Created by liuyang on 2019-02-22.
 *
 * @author liuyang email
 */
public abstract class Assert {
    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }
}
