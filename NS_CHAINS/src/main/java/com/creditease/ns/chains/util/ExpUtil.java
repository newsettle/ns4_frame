package com.creditease.ns.chains.util;

import java.util.Map;
import java.util.Stack;

import org.mvel2.MVEL;

import com.creditease.ns.chains.chain.Command;
import com.creditease.ns.chains.chain.ConditionableCommand;
import com.creditease.ns.chains.chain.AbstractIfCommand;
import com.creditease.ns.chains.exchange.Exchanger;

public class ExpUtil {
	public static boolean checkCond(String cond,Map map) throws Exception
	{
		return (Boolean) MVEL.eval(cond, map);
	}
	
	
	private static void setIfElseFlag(Exchanger exchanger,int newFlag) throws Exception
	{
		Stack<Integer>	ifFlagStack = (Stack<Integer>)exchanger.getExchange(ConditionableCommand.IFELSE_FLAG_KEY);
		Integer flagInteger = ifFlagStack.pop();
		ifFlagStack.push(newFlag);
	}
	
	@SuppressWarnings("unchecked")
	public static void startIfElseFlag(Exchanger exchanger) throws Exception
	{
	
		Stack<Integer>	ifFlagStack = (Stack<Integer>)exchanger.getExchange(ConditionableCommand.IFELSE_FLAG_KEY);
		if (ifFlagStack == null) 
		{
			ifFlagStack = new Stack<Integer>();
			exchanger.setExchange(ConditionableCommand.IFELSE_FLAG_KEY, ifFlagStack);
		}
		
		ifFlagStack.push(ConditionableCommand.INITEXECUTED);
	}
	
	@SuppressWarnings("unchecked")
	public static boolean executeStatement(Command command,Exchanger exchanger,int expectedFlag) throws Exception
	{
		if (exchanger.getExchange(ConditionableCommand.IFELSE_FLAG_KEY) == null) 
		{
			throw new Exception("判断是否执行语句出现错误，没有找到对应的状态栈");
		} 
		
		Stack<Integer> flagStack = ((Stack<Integer>)exchanger.getExchange(ConditionableCommand.IFELSE_FLAG_KEY));
		
		int flag = flagStack.peek().intValue();
		
		flag =	(flag | expectedFlag);
		
		if (flag == expectedFlag) 
		{
			setIfElseFlag(exchanger,flag);
			return true;
		}
		else
		{
			return false;
		}
	}
	
}
