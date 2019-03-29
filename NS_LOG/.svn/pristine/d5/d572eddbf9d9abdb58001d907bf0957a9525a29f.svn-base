package com.creditease.ns.log.encoder;

import java.io.IOException;

import com.creditease.ns.log.converter.NewlLineMessageConverter;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class NSLogEncoder extends PatternLayoutEncoder{
	
	 static {
	        PatternLayout.defaultConverterMap.put("T", NewlLineMessageConverter.class.getName());
	    }
	    @Override
	    public void doEncode(ILoggingEvent event) throws IOException {
	        super.doEncode(event);
	    }

}
