package com.creditease.ns.chains.def;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;

import com.creditease.ns.chains.chain.AbstractConditionaleCommand;
import com.creditease.ns.chains.chain.Command;
import com.creditease.ns.chains.chain.ElseCommand;
import com.creditease.ns.chains.exchange.Exchanger;
import com.creditease.ns.chains.util.ExpUtil;

public class ElseCommandDef extends AbstractContainerDef{

	@Override
	public void init(Element element) throws Exception {
		framLog.info("# 初始化ElseCommandDef id:{} #",this.id);
		super.init(element);
	}
	
	public void handle() {
		// TODO Auto-generated method stub
		
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
		ElseCommand curCommand = new ElseCommand();
		
		List<Command> lst = new ArrayList<Command>();
		for (Iterator iterator = children.iterator(); iterator.hasNext(); ) 
		{
			ElementDef elementDef = (ElementDef) iterator.next();
			elementDef.tranverse(lst);
			flowLog.trace("# 组装执行链节点{} 调用ElseCommand元素的children desc:{}  cost:{}ms #", this.getDesc(),System.currentTimeMillis()-startTime);
		}
		curCommand.setCommands(lst);
		curCommand.setDesc(desc);
		targetList.add(curCommand);
		flowLog.trace("# 组装执行链节点{}成功 desc:{} cost:{}ms #", this.getDesc(),System.currentTimeMillis()-startTime);
	}
	
	
	
	public boolean isCanBeRefered() {
		return true;
	}

}
