package com.creditease.ns.chains.def;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;

import com.creditease.framework.util.XMLUtil;
import com.creditease.ns.chains.chain.AbstractConditionaleCommand;
import com.creditease.ns.chains.chain.Command;
import com.creditease.ns.chains.exchange.Exchanger;
import com.creditease.ns.chains.util.ExpUtil;

public class ConditionDef extends AbstractContainerDef{
	private String cond;
	protected String className; //实现conditionablecommand
	
	public void init(Element e) throws Exception {
		framLog.info("# 初始化ConditionDef #");
		this.cond = XMLUtil.getAttributeAsString(e,"cond",null);
		this.className = e.getAttribute("class");
		super.init(e);
		if (cond == null) 
		{
			framLog.error("# 初始化ConditionDef因为缺少cond条件失败,className:{} #", this.getClass().getSimpleName());
			throw new Exception("condition元素必须指明cond条件");
		}
		
		framLog.info("# 初始化ConditionDef  desc:{} cond:{} className:{} tagName:{} OK #", this.getClass().getSimpleName(),id,desc,cond,className,e.getTagName());
	}

	@Override
	public void tranverse(List targetList) throws Exception {
		long startTime = System.currentTimeMillis();
		if (targetList == null) 
		{
			flowLog.error("# 组装执行链节点{}失败,要放入的节点列表为null id:{} desc:{} cost:{}ms #", this.getClass().getSimpleName(),this.getId(),this.getDesc(),System.currentTimeMillis()-startTime);
			throw new NullPointerException("传入的list为null");
		}
		
		//创建一个conditionablecommand放入targetlist中
		//然后创建一个新的list 然后调用子元素的tranverse 保证放入的都是command
		AbstractConditionaleCommand curCommand = null;
		if (className == null || className.trim().length() < 1) 
		{
			curCommand = new AbstractConditionaleCommand() {
				
				public void setCond(String cond) {
					this.cond = cond;
				}
				
				public boolean canExecute(String cond, Exchanger exchanger) {
					Map map =	exchanger.getExchangeScope();
					boolean isCan = false;
					try {
						isCan = ExpUtil.checkCond(cond, map);
					} catch (Exception e) {
						flowLog.error("条件判断,检查是否符合配置的条件 map中不包含对应的属性 cond:{} exchanger:{}",cond,exchanger,e);
					}
					return isCan;
				}
				
				@Override
				public void setDesc(String desc) {
					super.setDesc(desc);
				}
			};
			
			flowLog.trace("# 组装执行链节点{},没有定义自己的class 使用默认的匿名command id:{} desc:{} cond:{} className:{} cost:{}ms #", this.getClass().getSimpleName(),this.getId(),this.getDesc(),System.currentTimeMillis()-startTime);
		}
		else
		{
			Class<AbstractConditionaleCommand> cl = (Class<AbstractConditionaleCommand>)Class.forName(className);
			curCommand = cl.newInstance();
			flowLog.trace("# 组装执行链节点{},使用自己定义的command id:{} desc:{} cond:{} className:{} cost:{}ms #", this.getClass().getSimpleName(),this.getId(),this.getDesc(),System.currentTimeMillis()-startTime);
		}
		
		List<Command> lst = new ArrayList<Command>();
		for (Iterator iterator = children.iterator(); iterator.hasNext(); ) 
		{
			ElementDef elementDef = (ElementDef) iterator.next();
			elementDef.tranverse(lst);
			flowLog.trace("# 组装执行链节点{} 调用cond元素的children id:{} desc:{} cond:{} className:{} curElementId:{} curElementClass:{} cost:{}ms #", this.getClass().getSimpleName(),this.getId(),this.getDesc(),elementDef.getId(),elementDef.getClass().getSimpleName(),System.currentTimeMillis()-startTime);
		}
		curCommand.setCommands(lst);
		curCommand.setCond(cond);
		curCommand.setDesc(desc);
		targetList.add(curCommand);
		flowLog.trace("# 组装执行链节点{}成功 id:{} desc:{} cond:{} className:{} cost:{}ms #", this.getClass().getSimpleName(),this.getId(),this.getDesc(),System.currentTimeMillis()-startTime);
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
			if(!AbstractConditionaleCommand.class.isAssignableFrom(cl))
			{
				throw new Exception("实现自定义的条件判断类，必须继承AbstractConditionaleCommand抽象类");
			}
		}
	}

	
}
