package com.creditease.ns.chains.chain;

import com.creditease.ns.chains.constants.LoggerConstants;
import com.creditease.ns.chains.exchange.Exchanger;
import com.creditease.ns.log.spi.LoggerWrapper;

public interface Command {
	static String logPrefix = "[Command] ";
	public void doCommand(Exchanger exchanger) throws Exception;
	public boolean isNotBreak();
	
	public String getLogStr();
}
