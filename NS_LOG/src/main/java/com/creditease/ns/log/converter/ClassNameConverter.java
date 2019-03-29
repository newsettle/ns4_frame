package com.creditease.ns.log.converter;

import ch.qos.logback.classic.pattern.ClassOfCallerConverter;
import ch.qos.logback.classic.spi.CallerData;
import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * Date: 15-5-27
 * Time: 下午10:23
 */
public class ClassNameConverter extends ClassOfCallerConverter {

    protected String getFullyQualifiedName(ILoggingEvent event) {

        StackTraceElement[] cda = event.getCallerData();
        if (cda != null) {
            if(cda.length > 1){
                return cda[1].getClassName();
            }else if(cda.length == 1){
                return cda[0].getClassName();
            }

        }
            return CallerData.NA;
    }

}
