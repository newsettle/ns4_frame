package com.creditease.ns.log.converter;


import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.creditease.ns.log.LogConstants;
import com.creditease.ns.log.LogKey;

public class NSMessageConverter extends MessageConverter {
    @Override
    public String convert(ILoggingEvent event) {
        String message = super.convert(event);
        if(message == null){
            return "";
        }


        boolean endWithEnter = message.endsWith(LogConstants.LINUX_EOL);
        String[] stringLines = message.split(LogConstants.WIN_LINUX_EOL_REG);


        if(stringLines == null){
            return message;
        }

        StringBuilder sb = new StringBuilder();

        for(int i=0;i<stringLines.length;i++){
            sb.append(LogKey.getTotalKey()).append(LogConstants.PART_SPLIT).append(stringLines[i]);
            if(i != stringLines.length-1){
                sb.append(LogConstants.WIN_EOL);
            }else {
                if(endWithEnter){
                    sb.append(LogConstants.WIN_EOL);
                }
            }
        }
        return sb.toString();

    }
}
