package com.creditease.ns.chains.chain;

import com.creditease.ns.chains.constants.LoggerConstants;
import com.creditease.ns.chains.exchange.Exchanger;
import com.creditease.ns.log.NsLog;
import com.creditease.ns.log.spi.LoggerWrapper;

public interface Chain extends Command{
	static NsLog  flowLog = NsLog.getFlowLog("NsChainFlow", "NsChainFlow");
	public void doChain(Exchanger exchanger) throws Exception;
	public void add(Command command);
}
