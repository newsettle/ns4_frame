package com.creditease.ns.chains.chain;

import java.util.Iterator;
import java.util.Map;

import com.creditease.ns.chains.exchange.Exchanger;
import com.creditease.ns.chains.util.ExpUtil;

public abstract class AbstractIfCommand extends AbstractConditionaleCommand{

	@Override
	public void doCommand(Exchanger exchanger) throws Exception {
		
		ExpUtil.startIfElseFlag(exchanger);
		
		if (canExecute(cond, exchanger)) 
		{
			boolean result = ExpUtil.executeStatement(this, exchanger, ConditionableCommand.IFEXECUTECAN);
			
			if (result) 
			{
				for (Iterator iterator = commands.iterator(); iterator.hasNext(); ) 
				{
					Command command = (Command) iterator.next();
					command.doCommand(exchanger);
				}
			}
			
		}
		else 
		{
			doEmpty();
		}
	}
	

}
