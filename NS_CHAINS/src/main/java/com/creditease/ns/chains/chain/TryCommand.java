package com.creditease.ns.chains.chain;

import java.util.List;
import java.util.Map;

public interface TryCommand extends Command{
	public void setNormalList(List<Command> normalList);
	public void setExceptionsMap(Map<String,List<Command>> exceptionsCommands);
	public void setFinallyList(List<Command> finallyList);
	public void setDesc(String desc);
	public void setExceptions(Map<String,Exception> exceptionList);
}
