package com.creditease.ns.chains.chain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.creditease.framework.exception.NotStopException;
import com.creditease.framework.exception.StopException;
import com.creditease.ns.chains.exchange.Exchanger;

public class DefaultChain extends AbstractChain{

	protected List<Command> commands;
	
	public void doChain(Exchanger exchanger) throws Exception {
		long startTime = System.currentTimeMillis();
		flowLog.debug("# 执行业务工作链 commands数量:{} exchanger:{} #", commands.size(),exchanger);
		for (Iterator iterator = commands.iterator(); iterator.hasNext(); ) 
		{
			
			Command command = (Command) iterator.next();
			
			try
			{
				command.doCommand(exchanger);
				flowLog.info("# 执行命令[{}] OK #", command.getLogStr());
			}
			catch(Exception e)
			{	
				if ((e instanceof RuntimeException) || (e instanceof StopException) || (!(e instanceof NotStopException ) && !command.isNotBreak())) 
				{
					flowLog.error("执行业务链出现错误需要中断 exchanger:{} {} [cost:{}ms]",exchanger,command.getLogStr(),System.currentTimeMillis()-startTime);
					throw e;
				}
				else
				{
					flowLog.error("执行业务链出现错误但不需要中断 exchanger:{} {} [cost:{}ms]",exchanger,command.getLogStr(),System.currentTimeMillis()-startTime,e);
				}
			}
			flowLog.trace("# 正在执行命令{} 成功 exchanger:{} cost:{}ms #",command.getLogStr(),exchanger,System.currentTimeMillis()-startTime);
		}
		flowLog.debug("exchanger:{}", exchanger);
		flowLog.debug("# 执行业务工作链 OK commands数量:{} cost:{}ms #", commands.size(),System.currentTimeMillis()-startTime);
	}

	public void add(Command command) {
		this.commands.add(command);
	}
	
	public void addAll(List<Command> commands)
	{
		this.commands.addAll(commands);
	}

	public void init()
	{
		this.commands = new ArrayList<Command>();
	}

	@Override
	public String getLogStr() {
		return "[执行默认链] [commands:"+(commands == null ? 0 : commands.size())+"]";
	}


	
}
