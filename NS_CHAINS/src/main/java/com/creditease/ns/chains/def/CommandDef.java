package com.creditease.ns.chains.def;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import com.creditease.framework.util.StringUtil;
import com.creditease.framework.util.XMLUtil;
import com.creditease.ns.chains.chain.Command;
import com.creditease.ns.chains.constants.DefConstants;
import com.creditease.ns.chains.context.GlobalScope;

public class CommandDef  extends AbstractAtomicElementDef {
	private String queueName;
	private String className;
	
	
	public String getQueueName() {
		return queueName;
	}
	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public CommandDef() {
	}

	public void init(Element e) throws Exception{
		framLog.info("# 初始化CommandDef id:{} #",this.id);
		Element commandElement = e;
		super.init(e);
		String className = XMLUtil.getAttributeAsString(commandElement,"class", null);
		this.id = id;
		this.className = className;
		this.desc = desc;
		
		framLog.info("# 初始化CommandDef id:{} desc:{} className:{} tagName:{} OK #", this.getClass().getSimpleName(),id,desc,className,e.getTagName());
	}
	public void handle() {
		// TODO Auto-generated method stub
		
	}
	
	
	public void postInit() throws Exception{
		long startTime = System.currentTimeMillis();
		//检查
		try {
			Class<Command> cl = (Class<Command>) Class.forName(className);
			if(!Command.class.isAssignableFrom(cl))
				throw new Exception("指定的Class["+className+"]必须实现Command接口");
		} catch (ClassNotFoundException e) {
			framLog.error("{}postInit失败,找不到对应的class id:{} desc:{} className:{}", this.getClass().getSimpleName(),id,desc,className,System.currentTimeMillis()-startTime,e);
			throw new Exception("没有找到对应的Class["+className+"]");
		}
	}
	public void tranverse(List targetList) throws Exception {
		//每次创建一个新的命令放入
		long startTime = System.currentTimeMillis();
		
		if (targetList == null) 
		{
			flowLog.error("# 组装执行链节点{}失败,要放入的节点列表为null id:{} desc:{} cost:{}ms #", this.getClass().getSimpleName(),this.getId(),this.getDesc(),System.currentTimeMillis()-startTime);
			throw new NullPointerException("传入的list为null");
		}
		try
		{
			Command command = getCommand();
			targetList.add(command);
			flowLog.trace("# 组装执行链节点{}成功 id:{} desc:{} targetsize:{} cost:{}ms #", this.getClass().getSimpleName(),this.getId(),this.getDesc(),targetList.size(),System.currentTimeMillis()-startTime);
		}
		catch(Exception e)
		{
			flowLog.error("# 组装执行链节点{}失败,出现异常 id:{} desc:{} cost:{}ms #", this.getClass().getSimpleName(),this.getId(),this.getDesc(),System.currentTimeMillis()-startTime);
			throw e;
		}
		
		
	}
	
	public Command getCommand() throws ClassNotFoundException, InstantiationException, IllegalAccessException
	{
		Class<Command> cl = (Class<Command>) Class.forName(className);
		if(!GlobalScope.hasSpring)
		{
			return cl.newInstance();
		}
		else 
		{
			return (Command)GlobalScope.getInstance().getSpringPlugin().getBeanByClassName(cl);
		}
	}
}
