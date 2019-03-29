package com.creditease.ns.log;

import java.util.UUID;

/**
 * Date: 15-5-29
 * Time: 下午3:37
 */
public class LogUtils {
    /**
     * 0 is getStackTrace(),
     1 is getMethodName(int depth) and
     2 is invoking method.
     * @return  invoking method
     */
    public static String getSimpleMethodName()
    {
        final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
        if(ste.length  <= 2){
            return "";
        }
        return ste[2].getMethodName();
    }

    /**
     * 0 is getStackTrace(),
     1 is getMethodName(int depth) and
     2 is invoking method.
     * @return  invoking method
     */
    public static String getFullyMethodName()
    {
        final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
        if(ste.length  <= 2){
            return "";
        }
        return ste[2].getClassName()+"#"+ste[2].getMethodName();
    }

    private final static char[] DIGITS64 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ-_".toCharArray();

    private static String toIDString(long l) {
        char[] buf = "00000000000".toCharArray(); // 限定11位长度
        int length = 11;
        long least = 63L; // 0x0000003FL
        do {
            buf[--length] = DIGITS64[(int) (l & least)]; // l & least取低6位
            /* 无符号的移位只有右移，没有左移
             * 使用“>>>”进行移位
             * 为什么没有无符号的左移呢，知道原理的说一下哈
             */
            l >>>= 6;
        } while (l != 0);
        return new String(buf);
    }

    /**
     * get Normal UUID
     * @return  UUID
     */
    public static String getUUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    /**
     * get Short UUID
     * @return  UUID
     */
    public static String getShortUUID() {
        UUID u = UUID.randomUUID();
        return toIDString(u.getMostSignificantBits()) + toIDString(u.getLeastSignificantBits());
    }
}
