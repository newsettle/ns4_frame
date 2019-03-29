package com.creditease.ns.chains.chain;

import java.util.Iterator;
import java.util.List;

import com.creditease.ns.chains.exchange.Exchanger;

public abstract class AbstractConditionaleCommand implements ConditionableCommand{
	protected List<Command> commands;
	protected String cond;
	protected String desc;
	
	
	public void doCommand(Exchanger exchanger) throws Exception {
		if (canExecute(cond, exchanger)) 
		{
			for (Iterator iterator = commands.iterator(); iterator.hasNext(); ) 
			{
				Command command = (Command) iterator.next();
				command.doCommand(exchanger);
			}
		}
		else 
		{
			doEmpty();
		}
	}

	protected void doEmpty()
	{
		//TODO 打印日志
	}

	public List<Command> getCommands() {
		return commands;
	}


	public void setCommands(List<Command> commands) {
		this.commands = commands;
	}


	public String getCond() {
		return cond;
	}


	public void setCond(String cond) {
		this.cond = cond;
	}
	
	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	@Override
	public String getLogStr() {
		return "[执行条件判断命令] ["+cond+"] ["+(commands == null ? 0 : commands.size())+"] ["+desc+"]";
	}
	
	@Override
	public boolean isNotBreak() {
		return false;
	}
}
