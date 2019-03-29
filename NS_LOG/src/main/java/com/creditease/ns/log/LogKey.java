package com.creditease.ns.log;

import org.slf4j.MDC;

/**
 * Date: 15-5-28
 * Time: 下午7:10
 */
public class LogKey {
    public static String getTotalKey(){
        StringBuilder keySb = new StringBuilder();

        String uuid = MDC.get(LogConstants.UUID_KEY);
        if(uuid == null){
            uuid = LogConstants.NONE_UUID;
        }
        keySb.append("[").append(uuid);

        String primaryKey = MDC.get(LogConstants.LOG_PRIMARY_KEY);
        if(primaryKey != null){
            keySb.append(",").append(primaryKey);
        }

        String subPrimaryKey =  MDC.get(LogConstants.LOG_SUB_PRIMARY_KEY);
        if(subPrimaryKey != null){
            keySb.append(",").append(subPrimaryKey);
        }
        keySb.append("]");
        return keySb.toString();
    }
}
