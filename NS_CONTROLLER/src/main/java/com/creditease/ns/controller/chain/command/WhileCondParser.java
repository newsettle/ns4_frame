package com.creditease.ns.controller.chain.command;

import com.creditease.ns.chains.chain.Command;
import com.creditease.ns.chains.exchange.Exchanger;

import java.util.Iterator;

public class WhileCondParser extends CondParser {
	@Override
	public void doCommand(Exchanger exchanger) throws Exception {
		if (canExecute(cond, exchanger)) 
		{
			for (Iterator iterator = commands.iterator(); iterator.hasNext(); ) 
			{
				Command command = (Command) iterator.next();
				command.doCommand(exchanger);
			}
			
			doCommand(exchanger);
		}
	}
}
