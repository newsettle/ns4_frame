package com.creditease.ns.chains.chain;

import com.creditease.ns.chains.exchange.Exchanger;

public interface ConditionableCommand extends Command{
	
	//以下 所有位置 0 表示没有执行， 1表示执行
	public static int INITEXECUTED = 0x01; //初始化状态
	public static int IFEXECUTECAN = 0x03; //二进制 0011 倒数第二位，表示if执行状态
	public static int ELIFEXECUTECAN = 0x05; //二进制  0101
	public static int ELSEEXECUTECAN = 0x09; //二进制 1001
	
	public static String IFELSE_FLAG_KEY = "_if_else_flag_key_";
	
	public boolean canExecute(String cond,Exchanger exchanger);
	public String getCond();
	public void setCond(String cond);
	
}
