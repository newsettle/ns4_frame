package com.creditease.ns.chains.def;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Element;

import com.creditease.framework.util.StringUtil;
import com.creditease.ns.chains.chain.Command;
import com.creditease.ns.chains.chain.TryCommand;
import com.creditease.ns.chains.chain.TryCommandBase;


/*
 * <tryBlock>
 * <command>
 * </command>
 * <catchBlock  exception="异常类全名">
 * </catchBlock>
 * 
 * <catchBlock exceptionClass="异常类全名">
 * 
 * </catchBlock>
 * <finallyBlock>
 * <command>
 * </command>
 * </finallyBlock>
 * 
 * </tryBlock>
 * 
 * 实现思想是tryblock是个特殊的命令
 * 它里面包含了自己的一系列命令
 * 正常命令链
 * 异常命令链
 * 最终命令链
 * 
 * 然后会将正常命令链和最终命令链融合，异常命令链和最终命令链融合 最终形成正常命令链，异常命令链
 */
public class TryBlockDef extends AbstractContainerDef{
	
	protected String className = null;
	
	
	public void init(Element e) throws Exception {
		framLog.info("# 初始化TryBlockDef #");
		this.className = e.getAttribute("class");
		super.init(e);
		framLog.info("# 初始化TryBlockDef id:{} desc:{} tagName:{} OK #", id,desc,e.getTagName());
	}

	@Override
	public void tranverse(List targetList) throws Exception {
		long startTime = System.currentTimeMillis();
		if (targetList == null) 
		{
			flowLog.error("# 组装执行链节点{}失败,要放入的节点列表为null id:{} desc:{} cost:{}ms #", this.getClass().getSimpleName(),this.getId(),this.getDesc(),System.currentTimeMillis()-startTime);
			throw new NullPointerException("传入的list为null");
		}
		
	/*
	 * 创建一个trycommand 主要逻辑如下:
	 * 1.将这个trycommand拼装到chains上
	 * 2.将tryblock包裹的命令节点，封装成正常命令链 异常命令链和最终命令链
	 * 
	 * 
	 * 
	 */
		TryCommand curCommand = null;
		if (className == null || className.trim().length() < 1) 
		{
			curCommand = new TryCommandBase();
			flowLog.trace("# 组装执行链节点{},没有定义自己的class 使用默认的匿名command id:{} desc:{} cond:{} className:{} cost:{}ms #", this.getClass().getSimpleName(),this.getId(),this.getDesc(),System.currentTimeMillis()-startTime);
		}
		else
		{
			Class<TryCommandBase> cl = (Class<TryCommandBase>)Class.forName(className);
			curCommand = cl.newInstance();
			flowLog.trace("# 组装执行链节点{},使用自己定义的command id:{} desc:{} classname:{} cost:{}ms #", this.getClass().getSimpleName(),this.getId(),this.getDesc(),this.className,System.currentTimeMillis()-startTime);
		}
		
		List<Command> normalList = new ArrayList<Command>();
		TryTranverseStatus tranverseStatus = TryTranverseStatus.trying;
		Map<String,List<Command>> exceptionsCommands = new HashMap<String, List<Command>>();
		List<Command> finallyList = new ArrayList<Command>();
		
		for (Iterator iterator = children.iterator(); iterator.hasNext(); ) 
		{
			ElementDef elementDef = (ElementDef) iterator.next();
			
			switch (tranverseStatus) {
			case trying:
				if (elementDef instanceof CatchBlockDef) 
				{
					tranverseStatus = TryTranverseStatus.exceptioning;
					List<Command> exceptList = new ArrayList<Command>();
					handleExceptionList(elementDef, exceptList, exceptionsCommands);
				}
				else if (elementDef instanceof FinallyBlockDef) 
				{
					tranverseStatus = TryTranverseStatus.finallying;
					handleFinallyList(elementDef, finallyList);
				}
				else
				{
					handleNormalList(elementDef, normalList);
				}	
				break;
			case exceptioning:
				if (elementDef instanceof FinallyBlockDef) 
				{
					tranverseStatus = TryTranverseStatus.finallying;
					handleFinallyList(elementDef, finallyList);
				}
				else if(elementDef instanceof CatchBlockDef)
				{
					List<Command> exceptList = new ArrayList<Command>();
					handleExceptionList(elementDef, exceptList, exceptionsCommands);
				}
				else
				{
					//理论上如果有了exception后只会碰见finally元素和exception元素，不能有普通的element元素
					throw new Exception("Exception后只能跟随Exception或者finally元素");
				}
				
				break;
			case finallying:
				//除了finally节点，出现别的节点都认为是语法错误
				throw new Exception("finally元素后不能再定义任何元素");
			}
			flowLog.trace("# 组装执行链节点{} 调用try元素的children id:{} desc:{} className:{} curElementId:{} curElementClass:{} cost:{}ms #", this.getClass().getSimpleName(),this.getId(),this.getDesc(),elementDef.getId(),elementDef.getClass().getSimpleName(),System.currentTimeMillis()-startTime);
		}
		curCommand.setNormalList(normalList);
		curCommand.setExceptionsMap(exceptionsCommands);
		
		//出于性能原因，我们将exception类做一个缓存
		Set<String> exceptionsSet = exceptionsCommands.keySet();
		Map<String,Exception> exceptionMap = new HashMap<String, Exception>();
		
		for (String exceptionClassName : exceptionsSet) 
		{
			Class<Exception> cl = (Class<Exception>)Class.forName(exceptionClassName);
			Exception exception = cl.newInstance();
			exceptionMap.put(exceptionClassName, exception);
		}
		
		curCommand.setExceptions(exceptionMap);
		
		curCommand.setFinallyList(finallyList);
		curCommand.setDesc(desc);
		
		targetList.add(curCommand);
		flowLog.trace("# 调用try元素的children{}成功 id:{} desc:{} cost:{}ms #", this.getClass().getSimpleName(),this.getId(),this.getDesc(),System.currentTimeMillis()-startTime);
	}
	
	public void handle() {
		// TODO Auto-generated method stub
		
	}

	public void postInit() throws Exception {
		//检查
		super.postInit();
		if (className != null && className.trim().length() > 0)
		{
			Class  cl =  Class.forName(className);
			if(!TryCommand.class.isAssignableFrom(cl))
			{
				throw new Exception("实现自定义的TryCommand类，必须继承TryCommand抽象类");
			}
		}
	}
	
	private void handleNormalList(ElementDef elementDef,List<Command> normalList) throws Exception
	{
		elementDef.tranverse(normalList);
	}

	private void handleExceptionList(ElementDef elementDef,List<Command> exceptList,Map<String,List<Command>> exceptListsMap) throws Exception
	{
		elementDef.tranverse(exceptList);
		
		CatchBlockDef exceptDef = (CatchBlockDef)elementDef;
		
		String exClassName = exceptDef.getException();
		
		if (StringUtil.isEmpty(exClassName)) 
		{
			exClassName = "java.lang.Exception";
		}
		
		if (exceptListsMap.containsKey(exClassName)) 
		{
			throw new Exception("发现重复定义的catch异常类["+exClassName+"]");
		}
		
		exceptListsMap.put(exClassName, exceptList);
	}
	
	private void handleFinallyList(ElementDef elementDef,List<Command> finallyList) throws Exception
	{
		elementDef.tranverse(finallyList);
	}
	
	private enum TryTranverseStatus
	{
		trying,exceptioning,finallying
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}
}
