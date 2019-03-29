package com.creditease.ns.chains.chain;

import java.util.Iterator;
import java.util.List;

import com.creditease.ns.chains.exchange.Exchanger;
import com.creditease.ns.chains.util.ExpUtil;

public  class ElseCommand extends AbstractConditionaleCommand{
	
	@Override
	public void doCommand(Exchanger exchanger) throws Exception {
		
		boolean result = ExpUtil.executeStatement(this, exchanger, ConditionableCommand.ELSEEXECUTECAN);
		if (result) 
		{
			for (Iterator iterator = commands.iterator(); iterator.hasNext(); ) 
			{
				Command command = (Command) iterator.next();
				command.doCommand(exchanger);
			}
		}
	}

	@Override
	public boolean canExecute(String cond, Exchanger exchanger) {
		return false;
	}


}
