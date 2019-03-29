package com.creditease.ns.chains.def;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Element;

import com.creditease.ns.chains.chain.Command;

public class CatalogDef extends AbstractContainerDef{

	@Override
	public void init(Element element) throws Exception {
		framLog.info("# 初始化CatalogDef id:{} #",this.id);
		super.init(element);
		framLog.info("# 初始化CatalogDef id:{} OK #",this.id);
	}
	
	public void handle() {
		// TODO Auto-generated method stub
		
	}

	public void postInit() throws Exception {
		super.postInit();
		//catalog必须有id
		if (id == null || id.trim().length() < 1) 
		{
			throw new Exception("必须为Catalog指定id");
		}
		
		
		//检查children中是否存在多个chain
		boolean chainDefFinded = false;
		for (Iterator iterator = children.iterator(); iterator.hasNext(); ) 
		{
			ElementDef elementDef = (ElementDef) iterator.next();
			if (elementDef instanceof ChainDef) 
			{
				if (!chainDefFinded) 
				{
					chainDefFinded = true;
				}
				else 
				{
					throw new Exception("一个Catalog中只能放一个Chain定义");
				}
			}
			
			//测试是否有死链存在
			//死链就是链中的节点出现了循环嵌套的情况，A包含B B包含C C又包含A或者B等
			List<ElementDef> elementDefs = new ArrayList<ElementDef>();
			elementDefs.add(this);
			elementDef.checkLoopEmbedElement(elementDefs);
			
		}
		
		if (!chainDefFinded) 
		{
			throw new Exception("Catalog中必须放入一个Chain定义");
		}
		

		
	}

	
	public boolean isCanBeRefered() {
		return false;
	}
	
	public CatalogDef()
	{
	}
	
	@Override
	public void tranverse(List targetList) throws Exception {
		long startTime = System.currentTimeMillis();
		
		if (targetList == null) 
		{
			flowLog.error("# 组装执行链节点{}失败,要放入的节点列表为null id:{} desc:{} children:{} cost:{}ms #", this.getClass().getSimpleName(),this.getId(),this.getDesc(),children.size(),System.currentTimeMillis()-startTime);
			throw new NullPointerException("传入的list为null");
		}
		
		for (Iterator iterator = children.iterator(); iterator.hasNext(); ) 
		{
			ElementDef elementDef = (ElementDef) iterator.next();
			//如果是容器类元素那么就调用容器类的遍历方法
			if(elementDef instanceof ChainDef)
			{
				elementDef.tranverse(targetList);
			}
			//如果是定义 command 等 则不执行遍历 但要保证其在这个容器内部的作用域内
			
			flowLog.trace("遍历{} 遍历到{} 成功 id:{} desc:{} children:{} targetList:{} cost:{}ms", this.getClass().getSimpleName(),elementDef.getClass().getSimpleName(),elementDef.getId(),elementDef.getDesc(),children.size(),targetList.size(),System.currentTimeMillis()-startTime);
		}
		
		flowLog.trace("# 组装执行链节点{}成功 id:{} desc:{} children:{} cost:{}ms #", this.getClass().getSimpleName(),this.getId(),this.getDesc(),children.size(),System.currentTimeMillis()-startTime);
	}
}
