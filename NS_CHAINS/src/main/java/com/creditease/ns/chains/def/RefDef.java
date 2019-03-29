package com.creditease.ns.chains.def;

import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Element;

import com.creditease.framework.util.XMLUtil;

public class RefDef extends AbstractAtomicElementDef{

	//	private ElementDef reference; 注释掉这行的原因是要保证每个元素的状态是最新的
	private String ref;

	public void handle() {
		
	}
	
	@Override
	public void init(Element e) throws Exception {
		framLog.info("# 初始化RefDef ref:{} #",this.ref);
		this.ref = XMLUtil.getAttributeAsString(e, "ref", null);
		super.init(e);
		framLog.info("# 初始化RefDef ref:{} OK #",this.ref);
	}

	public void postInit() throws Exception { 
		long startTime = System.currentTimeMillis();
		//现在本地找
		//本地没有找上层
		//一直找到顶层
		//没有报异常
		try {
			//这里只是简单的做了一次检查
			ElementDef reference = find(this.ref);
			if (reference == null) 
			{
				framLog.error("{}postInit失败 没有找到对应的元素 id:{} desc:{} ref:{} cost:{}ms", this.getClass().getSimpleName(),id,desc,ref,System.currentTimeMillis()-startTime);
				throw new Exception("没有找到对应["+ref+"]的元素,请确认ref指定的id正确");
			}
		} catch (Exception e) {
			framLog.error("{}postInit失败,出现异常 id:{} desc:{} ref:{} cost:{}ms", this.getClass().getSimpleName(),id,desc,ref,System.currentTimeMillis()-startTime,e);
			throw new Exception("初始化后没有找到refId["+this.ref+"]对应的引用元素，请确认refId正确");
		}
		
		framLog.trace("{}postInit成功 id:{} desc:{} ref:{} cost:{}ms", this.getClass().getSimpleName(),id,desc,ref,System.currentTimeMillis()-startTime);
	}

	public String getId() {
		return this.id;
	}

	public String getDesc() {
		return this.desc;
	}
	
	public void tranverse(List targetList) throws Exception {
		long startTime = System.currentTimeMillis();
		if (targetList == null) 
		{
			flowLog.error("# 组装执行链节点{}失败,要放入的节点列表为null ref:{} desc:{} cost:{}ms #", this.getClass().getSimpleName(),this.ref,this.getDesc(),System.currentTimeMillis()-startTime);
			throw new NullPointerException("传入的list为null");
		}
		//每次都去获取最新的元素引用 因为都是从内存中获取 所以都是最新的
		ElementDef reference = find(this.ref);
		if(reference == null)
		{
			flowLog.error("# 组装执行链节点{}失败,指定的引用对象为null ref:{} desc:{} cost:{}ms #", this.getClass().getSimpleName(),this.ref,this.getDesc(),System.currentTimeMillis()-startTime);
			throw new IllegalStateException("["+ref+"]出现reference为null的情况");
		}
		
		if (reference instanceof CommandDef) 
		{
			CommandDef commandDef =	(CommandDef) reference;
			targetList.add(commandDef.getCommand());
			flowLog.trace("# 组装执行链节点{}成功 ref:{} desc:{} cost:{}ms #", this.getClass().getSimpleName(),this.ref,this.getDesc(),System.currentTimeMillis()-startTime);
		}
		else 
		{
			reference.tranverse(targetList);
			flowLog.trace("# 组装执行链节点{},引用了非command对象,继续往后遍历 ref:{} desc:{} cost:{}ms #", this.getClass().getSimpleName(),this.ref,this.getDesc(),System.currentTimeMillis()-startTime);
		}
	}
	
	@Override
	public ElementDef find(String refId) throws Exception {
		long startTime = System.currentTimeMillis();
		ElementDef localRefer = this.parentElementDef.find(refId);
		flowLog.trace("查找引用对象{} 成功 id:{} desc:{} refId:{} localRefer:{} cost:{}ms", this.getClass().getSimpleName(),id,desc,refId,localRefer,System.currentTimeMillis()-startTime);
		return localRefer;
	}
	
	@Override
	public void checkLoopEmbedElement(List<ElementDef> parentElementDefs) throws Exception {
		ElementDef reference = find(this.ref);
		//若ref引用了上层元素的任何一个 都认为是循环业务链 会导致无限循环 故抛出异常
		if (reference != null) 
		{
			try
			{
				reference.checkLoopEmbedElement(parentElementDefs);
			}
			catch(Exception e)
			{
				AbstractContainerDef abstractContainerDef = (AbstractContainerDef)reference;
				throw new Exception("探测到循环嵌套元素[ref],refId为["+this.ref+"],循环状态:"+abstractContainerDef.buildListStatusForExceptionDesc(parentElementDefs));
			}
		}
	}
}
