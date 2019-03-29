package com.crediteease.test;

import java.util.Map;

import com.creditease.ns.chains.chain.AbstractChain;
import com.creditease.ns.chains.chain.Chain;
import com.creditease.ns.chains.chain.Command;
import com.creditease.ns.chains.chain.DefaultChain;
import com.creditease.ns.chains.exchange.Exchanger;
import com.creditease.ns.chains.start.ChainLauncher;

public class DemoController {
	public static void main(String[] args) throws Exception 
	{
//		String chainConfigPath = Thread.currentThread().getContextClassLoader().getResource("demo.xml").getPath();
//		ChainLauncher chainLauncher = ChainLauncher.getInstance();
//		chainLauncher.setResourcePath(chainConfigPath);
//		chainLauncher.startUp();
		
		DefaultChain defaultChain = new DefaultChain();
		defaultChain.init();
		defaultChain.add(new ServiceMessageCommandDemo1());
		defaultChain.add(new ServiceMessageCommandDemo2());
		
		defaultChain.doChain(new Exchanger() {
			
			@Override
			public void setOut(Object key, Object value) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setExchange(Object key, Object value) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public Object getParameter(String key) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Map getOutScope() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Object getOut(Object key) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Map getExchangeScope() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Object getExchange(Object key) {
				// TODO Auto-generated method stub
				return null;
			}
		});
		
	}
}
