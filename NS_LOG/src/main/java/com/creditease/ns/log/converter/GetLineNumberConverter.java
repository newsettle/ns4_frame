package com.creditease.ns.log.converter;

import ch.qos.logback.classic.pattern.LineOfCallerConverter;
import ch.qos.logback.classic.spi.CallerData;
import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * Date: 15-5-27
 * Time: 下午8:27
 */
public class GetLineNumberConverter extends LineOfCallerConverter {
    public String convert(ILoggingEvent le) {
        StackTraceElement[] cda = le.getCallerData();
        if (cda != null) {
            if(cda.length > 1){
                return Integer.toString(cda[1].getLineNumber());
            }else if(cda.length == 1){
                return Integer.toString(cda[0].getLineNumber());
            }

        }

        return CallerData.NA;
    }
}
