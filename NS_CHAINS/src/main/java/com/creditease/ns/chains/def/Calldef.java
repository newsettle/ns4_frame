package com.creditease.ns.chains.def;

import java.util.List;

import org.w3c.dom.Element;

import com.creditease.ns.chains.constants.DefConstants;
import com.creditease.ns.chains.context.GlobalScope;

//call没有id 只有refCatalog属性 后面是引用的catalog的id
public class Calldef extends AbstractAtomicElementDef{
	private String refCatalog;
//这里注释掉这个catalogdef缓存，因为我们要保证每次拿到的catalogdef都是最新的
//	private CatalogDef catalogDef;
	
	@Override
	public void init(Element e) throws Exception {
		framLog.info("# 初始化Calldef #");
		super.init(e);
		this.refCatalog = e.getAttribute("refCatalog");
		if (refCatalog == null || refCatalog.trim().length() < 1) 
		{
			throw new Exception("call元素必须执行refCatalog");
		}
		
		framLog.info("# 初始化Calldef OK desc:{} refCatalog:{} #", this.getClass().getSimpleName(),id,desc,refCatalog);
	}
	
	public void tranverse(List targetList) throws Exception {
		long startTime = System.currentTimeMillis();
		if (targetList == null) 
		{
			flowLog.error("# 组装执行链节点{}失败,传入的targetList为null id:{} desc:{} refCatalog:{} cost:{}ms", this.getClass().getSimpleName(),this.getId(),this.getDesc(),refCatalog,System.currentTimeMillis()-startTime);
			throw new NullPointerException("传入的list为null");
		}
		//保证每次都是用最新的 保证状态完整性
		CatalogDef catalogDef = GlobalScope.getInstance().getCatalogDef(refCatalog);
		if (catalogDef == null) 
		{
			flowLog.error("# 组装执行链节点{}失败,指定的refCatalog不存在 id:{} desc:{} refCatalog:{} cost:{}ms", this.getClass().getSimpleName(),id,desc,refCatalog,System.currentTimeMillis()-startTime);
			throw new Exception("call引用的catalog"+refCatalog+"不存在");
		}
		catalogDef.tranverse(targetList);
		flowLog.trace("# 组装执行链节点{}成功 id:{} desc:{} targetList:{} refCatalog:{} cost:{}ms", this.getClass().getSimpleName(),id,desc,targetList.size(),refCatalog,System.currentTimeMillis()-startTime);
	}

	public void postInit() throws Exception {
		//检查call 引用的是否是
		CatalogDef catalogDef = GlobalScope.getInstance().getCatalogDef(refCatalog);
		if (catalogDef == null) 
		{
			framLog.error("{}postInit失败,因为对应的catalogRef不存在 id:{} desc:{} refCatalog:{}", this.getClass().getSimpleName(),id,desc,refCatalog);
			throw new Exception("call引用的catalog"+refCatalog+"不存在");
		}
		
		checkParentElementDef();
	}

	public ElementDef getParentElementDef() {
		return this.parentElementDef;
	}

	@Override
	protected void checkParentElementDef() throws Exception {
		super.checkParentElementDef();
		if (!(this.parentElementDef instanceof ChainDef)) 
		{
			throw new Exception("call标签只能放在chain标签中");
		}
	}
	
	@Override
	public void checkLoopEmbedElement(List<ElementDef> parentElementDefs) throws Exception {
		CatalogDef catalogDef = GlobalScope.getInstance().getCatalogDef(refCatalog);
		if(catalogDef != null)
		{
			try
			{
				catalogDef.checkLoopEmbedElement(parentElementDefs);
			}
			catch(Exception e)
			{
				AbstractContainerDef abstractContainerDef = (AbstractContainerDef)catalogDef;
				throw new Exception("探测到循环嵌套元素[refCatalog],refCatalog为["+this.refCatalog+"],循环状态:"+abstractContainerDef.buildListStatusForExceptionDesc(parentElementDefs));
			}
		}
	}
}
