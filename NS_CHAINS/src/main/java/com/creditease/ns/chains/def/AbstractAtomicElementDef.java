package com.creditease.ns.chains.def;

import java.util.List;

import org.w3c.dom.Element;

import com.creditease.framework.util.StringUtil;
import com.creditease.ns.chains.constants.DefConstants;
import com.creditease.ns.chains.util.ElementCheckUtil;

public abstract class AbstractAtomicElementDef implements AtomicElementDef{

	protected String id;
	protected String desc;
	protected ElementDef parentElementDef;
	
	public String getId() {
		return this.id;
	}

	public void init(Element e) throws Exception{
		ElementCheckUtil.checkElement(e);
		this.id = e.getAttribute(DefConstants.idString);
		this.desc = e.getAttribute(DefConstants.descString);
	}
	
	public void handle() {
		// TODO Auto-generated method stub
		
	}


	public String getDesc() {
		return this.desc;
	}

	
	
	public boolean isCanBeRefered() {
		return true;
	}
	
	public void setParentElementDef(ElementDef elementDef) {
		this.parentElementDef = elementDef;
	}
	
	public ElementDef getParentElementDef() {
		return this.parentElementDef;
	}
	
	public ElementDef find(String refId) throws Exception {
		if (refId.equals(this.id)) 
		{
			return this;
		}
		
		return this.parentElementDef.find(refId);
	}
	
	protected void checkParentElementDef() throws Exception
	{
		if (parentElementDef != null) 
		{
			if (!(parentElementDef instanceof ContainerDef)) 
			{
				throw new Exception("非容器类元素["+parentElementDef.getId()+"]内部不能放任何元素");
			}
		}
		else 
		{
			throw new Exception("非容器元素必须存放于容器元素内");
		}
		
	}
	
	@Override
	public void checkLoopEmbedElement(List<ElementDef> parentElementDefs) throws Exception {
		return;
	}
	
	@Override
	//判断一个元素是否是当前元素的上级元素 xml静态结构的父子关系
	public boolean isSuperiorElement(ElementDef elementDef)
	{
		boolean isSuperior =  false;
		if (this.parentElementDef != null) 
		{
			if(this.parentElementDef.equals(elementDef))
			{
				isSuperior = true;
			}
			else
			{
				isSuperior = this.parentElementDef.isSuperiorElement(elementDef);
			}
		}
		
		return isSuperior;
	}
}
