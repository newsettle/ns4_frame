package com.creditease.ns.controller.chain.def;

import com.creditease.framework.util.StringUtil;
import com.creditease.framework.util.XMLUtil;
import com.creditease.ns.chains.chain.Command;
import com.creditease.ns.chains.def.AbstractAtomicElementDef;
import com.creditease.ns.controller.chain.command.DefaultPublishCommand;
import org.w3c.dom.Element;

import java.util.List;


/**
 * publishdef是这样一种元素
 * 它有classname属性，有queue
 * @author liuyang
 *2015年9月17日上午11:16:07
 */
public class PublishDef extends AbstractAtomicElementDef{
	
	protected String queueName;
	protected String className;
	protected boolean isSync;
	protected int timeout = 2 * 60; //单位秒
	protected String serviceName;
	protected boolean isBreak;
	
	@Override
	public void init(Element e) throws Exception {
		queueName = XMLUtil.getAttributeAsString(e, "queueName", null);
		className = e.getAttribute("class");
		String timeoutStr = e.getAttribute("timeout");
		
		if (timeoutStr != null && timeoutStr.trim().length() > 0) 
		{
			try
			{
				timeout = Integer.parseInt(timeoutStr);
			}
			catch(Exception e1)
			{
				e1.printStackTrace();
			}
		}
		
		
		String sync = e.getAttribute("isSync");
		if (sync == null || sync.trim().length() < 1 || !sync.equalsIgnoreCase("false")) 
		{
			isSync = true;
		}
		else 
		{
			isSync = false;
		}
		
		String sn = e.getAttribute("serviceName");
		if (!StringUtil.isEmpty(sn)) 
		{
			serviceName = sn;
		}

		String breakAttr = e.getAttribute("isBreak");
		if (breakAttr == null || breakAttr.trim().length() < 1 || !breakAttr.equalsIgnoreCase("true"))
		{
			isBreak = false;
		}
		else
		{
			isBreak = true;
		}
		
		super.init(e);
	}

	@Override
	public void tranverse(List targetList) throws Exception {
		Command command = getPublish();
		targetList.add(command);
	}

	@Override
	public void postInit() throws Exception {
		//检查
	}
	
	private Command getPublish() throws ClassNotFoundException
	, InstantiationException, IllegalAccessException
	{
		Command command = null;
		if (className == null || className.trim().length() < 1) 
		{
			DefaultPublishCommand defaultPublishCommand = new DefaultPublishCommand();
			defaultPublishCommand.setQueueName(queueName);
			defaultPublishCommand.setSync(isSync);
			defaultPublishCommand.setDesc(this.desc);
			defaultPublishCommand.setTimeout(timeout);
			defaultPublishCommand.setBreak(isBreak);
			if (!StringUtil.isEmpty(serviceName)) 
			{
				defaultPublishCommand.setServiceName(serviceName);
			}
			
			command = defaultPublishCommand;
			return command;
		}
		else 
		{
			Class cl = Class.forName(className);
			command = (Command)cl.newInstance();
			//TODO  需要set一些属性
		}
		return command;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	
	
}
