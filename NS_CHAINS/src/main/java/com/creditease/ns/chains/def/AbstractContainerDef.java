package com.creditease.ns.chains.def;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;

import com.creditease.framework.util.StringUtil;
import com.creditease.framework.util.XMLUtil;
import com.creditease.ns.chains.constants.DefConstants;
import com.creditease.ns.chains.context.GlobalScope;
import com.creditease.ns.chains.util.ElementCheckUtil;

public abstract class AbstractContainerDef implements ContainerDef
{
	protected List<ElementDef> children;
	protected Map<String,ElementDef> localScope;
	protected String id;
	protected String desc;
	protected ElementDef parentElementDef;
	protected String tagName;
	
	public void init(Element element) throws Exception {
		
		ElementCheckUtil.checkElement(element);
		
		localScope = new HashMap<String, ElementDef>();
		children = new ArrayList<ElementDef>();
		
		String id = element.getAttribute(DefConstants.idString);
		this.id = id;
		
		String desc = element.getAttribute(DefConstants.descString);
		this.desc = desc;
		
		this.tagName = element.getTagName();
		
		//获取整个children
		Element[] elements	= XMLUtil.getChildren(element);
		for (int i = 0; i < elements.length; i++) 
		{
			ElementDef elementDef = DefFactory.createElmentDef(elements[i]);
			register(elementDef);
		}
		
	}

	public List<ElementDef> getChildren() {
		return children;
	}
	
	public void register(ElementDef elementDef) throws Exception
	{
		long startTime = System.currentTimeMillis();
		if (elementDef.getId() != null && elementDef.getId().trim().length() > 0) 
		{
			if (localScope.containsKey(elementDef.getId())) 
			{
				throw new Exception("本地域中出现同名对象，请确认声明的id"+elementDef.getId()+"没有重复");
			}
			localScope.put(elementDef.getId(), elementDef);
		}
		children.add(elementDef);
//		if (elementDef instanceof AtomicElementDef) 
//		{
//			((AtomicElementDef)elementDef).setContainerDef(this);
//		}
		elementDef.setParentElementDef(this);
	}

	public String getId() {
		return id;
	}
	
	public String getDesc()
	{
		return this.desc;
	}
	
	public void tranverse(List targetList) throws Exception {
		long startTime = System.currentTimeMillis();
		
		if (targetList == null) 
		{
			flowLog.error("# 组装执行链节点{}失败,要放入的节点列表为null id:{} desc:{} children:{} cost:{}ms", this.getClass().getSimpleName(),this.getId(),this.getDesc(),children.size(),System.currentTimeMillis()-startTime);
			throw new NullPointerException("传入的list为null");
		}
		
		for (Iterator iterator = children.iterator(); iterator.hasNext(); ) 
		{
			ElementDef elementDef = (ElementDef) iterator.next();
			elementDef.tranverse(targetList);
		}
		
	}
	
	public ElementDef find(String refId) {
		long startTime = System.currentTimeMillis();
		ElementDef elementDef = this.localScope.get(refId);
		if (elementDef == null) 
		{
			if (parentElementDef != null) 
			{
				try {
					flowLog.trace("{}查找引用 本地域没有找到 往上层域查找 id:{} desc:{} children:{} localScope:{} refId:{} parentclass:{} parentId:{} cost:{}ms", this.getClass().getSimpleName(),this.getId(),this.getDesc(),children.size(),localScope.size(),refId,parentElementDef.getClass().getSimpleName(),parentElementDef.getId(),System.currentTimeMillis()-startTime);
					
					elementDef = parentElementDef.find(refId);
				} catch (Exception e) {
					flowLog.error("{}查找引用 本地域没有找到 往上层域查找 失败 出现未知异常 id:{} desc:{} children:{} localScope:{} refId:{} parentclass:{} parentId:{} cost:{}ms", this.getClass().getSimpleName(),this.getId(),this.getDesc(),children.size(),localScope.size(),refId,parentElementDef.getClass().getSimpleName(),parentElementDef.getId(),System.currentTimeMillis()-startTime,e);
					return elementDef;
				}
			}//找到了最上层
			else 
			{
				//这里表明已经是最上层了
				elementDef = GlobalScope.getInstance().getGlobalScopeElement(refId);
			}
		}
		flowLog.trace("{}查找引用 成功 id:{} desc:{} children:{} localScope:{} refId:{} elementDef:{} cost:{}ms", this.getClass().getSimpleName(),this.getId(),this.getDesc(),children.size(),localScope.size(),refId,elementDef,System.currentTimeMillis()-startTime);
		return elementDef;
	}
	
	//判断一个元素是否是当前元素的上级元素
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
	
	public  boolean isCanBeRefered() {
		return true;
	}
	public void setParentElementDef(ElementDef elementDef) {
		this.parentElementDef = elementDef;
	}
	
	public ElementDef getParentElementDef() {
		return this.parentElementDef;
	}
	
	public void postInit() throws Exception {
		long startTime = System.currentTimeMillis();
		//检查children是否存在 不能是空的容器元素
		if (children == null || children.size() < 1) 
		{
			throw new Exception("容器类元素不能为空，必须指定子元素");
		}
		//检查children的id和自己的id是否重复 即自己引用自己
		for (Iterator iterator = children.iterator(); iterator.hasNext(); ) 
		{
			ElementDef elementDef = (ElementDef) iterator.next();
			if (elementDef.getId() != null && elementDef.getId().trim().length() > 0) 
			{
				if (elementDef.getId().equals(this.getId())) 
				{
					throw new Exception("容器类元素内部不能有和容器本身id"+this.getId()+"相同名称的元素");
				}
				
				//TODO 容器内部嵌套的容器不能里面又含有它的上级容器
				//比如 <group id=A><ref ref=B></ref></group>
				//<group id=B><ref ref=A> 得做检查防止父子嵌套
			}
			
		}
		//如果父级elementdef存在 其是否是container
		if (parentElementDef != null) 
		{
			if (!(parentElementDef instanceof ContainerDef)) 
			{
				throw new Exception("非容器类元素"+parentElementDef.getId()+"内部不能放任何元素");
			}
		}
		else 
		{
			if (!(this instanceof CatalogsDef)) 
			{
				throw new Exception("只有Catalogs元素可以独立存在");
			}
		}
		
		
		for (Iterator iterator = children.iterator(); iterator.hasNext(); ) 
		{
			ElementDef elementDef = (ElementDef) iterator.next();
			try {
				framLog.trace("{}postInit 调用children{} id:{} desc:{} children:{} localScope:{} childId:{} cost:{}ms", this.getClass().getSimpleName(),elementDef.getClass().getSimpleName(),this.getId(),this.getDesc(),children.size(),localScope.size(),elementDef.getId(),System.currentTimeMillis()-startTime);
				elementDef.postInit();
			} catch (Exception e) {
				flowLog.error("{}postInit 调用children{} 失败 出现未知异常 id:{} desc:{} children:{} localScope:{} childId:{} cost:{}ms", this.getClass().getSimpleName(),elementDef.getClass().getSimpleName(),this.getId(),this.getDesc(),children.size(),localScope.size(),elementDef.getId(),System.currentTimeMillis()-startTime,e);
				throw new Exception("postInit出现异常",e);
			}
		}
		
		framLog.trace("{}postInit 成功 id:{} desc:{} children:{} localScope:{} cost:{}ms", this.getClass().getSimpleName(),this.getId(),this.getDesc(),children.size(),localScope.size(),System.currentTimeMillis()-startTime);
	}
	@Override
	public ElementDef getElementDefById(String id) {
		return this.localScope.get(id);
	}
	
	@Override
	public Map<String, ElementDef> getLocalScope() {
		return this.localScope;
	}
	
	@Override
	public void checkLoopEmbedElement(List<ElementDef> parentElementDefs) throws Exception
	{
		if (parentElementDefs.contains(this)) 
		{
			String loopStatus = buildListStatusForExceptionDesc(parentElementDefs);
			throw new Exception("探测到循环嵌套元素["+tagName+"],id为["+this.getId()+"] 循环状态:loopStatus");
		}
		parentElementDefs.add(this);
		for (Iterator iterator = children.iterator(); iterator.hasNext(); ) 
		{
			//保证传入下级元素的只有它的父元素 不要掺杂别的同级元素
			List<ElementDef> lst = new ArrayList<ElementDef>();
			lst.addAll(parentElementDefs);
			
			ElementDef elementDef = (ElementDef) iterator.next();
			elementDef.checkLoopEmbedElement(lst);
		}
		
	}
	
	@Override
	public boolean equals(Object obj) {
		ElementDef def = (ElementDef) obj;
		if(!StringUtil.isEmpty(this.id))
		{
			return this.id.equals(def.getId());
		}
		return super.equals(obj);
	}
	
	public String buildListStatusForExceptionDesc(List<ElementDef> parentElementDefs)
	{
		StringBuilder builder = new StringBuilder();
		
		for (Iterator iterator = parentElementDefs.iterator(); iterator.hasNext(); ) 
		{
			ElementDef elementDef = (ElementDef) iterator.next();
			builder.append(elementDef.getId() + " -> ");
		}
		
		builder.append(this.getId());
		return builder.toString();
	}
}
