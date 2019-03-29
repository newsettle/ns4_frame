package com.creditease.ns.chains.chain;


public class ChainFactory {
	public static Chain getDefaultChain()
	{
		return DefaultChainDispatcher.getInstance();
	}
}
