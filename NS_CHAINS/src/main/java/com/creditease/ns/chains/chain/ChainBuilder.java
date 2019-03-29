package com.creditease.ns.chains.chain;

import java.util.ArrayList;
import java.util.List;

import com.creditease.ns.chains.constants.LoggerConstants;
import com.creditease.ns.chains.def.CatalogDef;
import com.creditease.ns.log.NsLog;
import com.creditease.ns.log.spi.LoggerWrapper;

public class ChainBuilder {
	
	public static Chain build(CatalogDef catalogDef) throws Exception
	{
		List<Command> commands = new ArrayList<Command>();
		catalogDef.tranverse(commands);
		DefaultChain chain = new DefaultChain();
		chain.init();
		chain.addAll(commands);
		Chain.flowLog.debug("ChainBuilder构建工作链 成功 catalogDef:{} chain中的命令数:{}", catalogDef.getId(),commands.size());
		return chain;
	}
}
