package com.creditease.ns.chains.chain;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.creditease.ns.chains.exchange.Exchanger;

public class TryCommandBase implements TryCommand{

	protected String desc;
	protected List<Command> normalList;
	protected Map<String,List<Command>> exceptionsMap;
	protected List<Command> finallyList;
	protected Map<String,Exception> exceptions;
	
	
	/**
	 * 执行自己的normalList 如果出现异常，则根据异常类判断扔出的异常类是否和catch中配置的异常类一致
	 * 或者是其子类，如果是则执行异常链，最后执行完异常链后，执行finally链
	 * @param exchanger
	 * @throws Exception
	 * 2016年9月22日下午5:43:16
	 */
	@Override
	public void doCommand(Exchanger exchanger) throws Exception {
		
		try
		{
			for (Iterator iterator = normalList.iterator(); iterator.hasNext(); ) 
			{
				Command command = (Command) iterator.next();
				command.doCommand(exchanger);
			}
		}
		catch(Exception e)
		{
			//执行异常链
			//需要判断执行哪个catch
			List<Command> commands = routeToExceptionList(e);
			
			if (commands != null) 
			{
				exeExceptionCommands(commands, exchanger);
			}
			else
			{
				//没有配置catch 就直接扔出异常
				throw e;
			}
		}
		finally
		{
			if (finallyList != null && finallyList.size() > 0) 
			{
				exeFinallyCommands(finallyList,exchanger);
			}
		}
	}


	@Override
	public String getLogStr() {
		return null;
	}

	@Override
	public void setNormalList(List<Command> normalList) {
		this.normalList = normalList;
	}

	@Override
	public void setExceptionsMap(Map<String, List<Command>> exceptionsCommands) {
		this.exceptionsMap = exceptionsCommands;
	}

	@Override
	public void setFinallyList(List<Command> finallyList) {
		this.finallyList = finallyList;
	}
	
	public List<Command> getNormalList() {
		return normalList;
	}


	public List<Command> getFinallyList() {
		return finallyList;
	}


	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}


	public Map<String, List<Command>> getExceptionsMap() {
		return exceptionsMap;
	}


	@Override
	public boolean isNotBreak() {
		// TODO Auto-generated method stub
		return false;
	}

	protected List<Command> routeToExceptionList(Exception e)
	{
		Exception exception = exceptions.get(e.getClass().getCanonicalName());
		
		if (exception == null) 
		{
			//判断e是否是exceptionlist中任何一个类的子类
			
			Collection<Exception> exList = exceptions.values();
			
			
			for (Exception exception2 : exList) 
			{
				//如果e是配置的exception的子类
				if (exception2.getClass().isAssignableFrom(e.getClass())) 
				{
					List<Command> lst = exceptionsMap.get(exception2.getClass().getCanonicalName());
					return lst;
				}
			}
			
			return exceptionsMap.get("java.lang.Exception");
		}
		else
		{
			return exceptionsMap.get(e.getClass().getCanonicalName());
		}
	}


	public Map<String, Exception> getExceptions() {
		return exceptions;
	}


	public void setExceptions(Map<String, Exception> exceptions) {
		this.exceptions = exceptions;
	}

	protected void exeExceptionCommands(List<Command> extList,Exchanger exchanger) throws Exception
	{
		for (Iterator iterator = extList.iterator(); iterator.hasNext(); ) 
		{
			Command command = (Command) iterator.next();
			command.doCommand(exchanger);
		}
	}

	protected void exeFinallyCommands(List<Command> finallyList,Exchanger exchanger) throws Exception
	{
		for (Iterator iterator = finallyList.iterator(); iterator.hasNext(); ) 
		{
			Command command = (Command) iterator.next();
			command.doCommand(exchanger);
		}
	}
	
}
