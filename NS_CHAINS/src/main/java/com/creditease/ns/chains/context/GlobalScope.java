package com.creditease.ns.chains.context;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.creditease.ns.chains.config.ConfigManager;
import com.creditease.ns.chains.def.CatalogDef;
import com.creditease.ns.chains.def.CatalogsDef;
import com.creditease.ns.chains.def.ElementDef;
import com.creditease.ns.framework.spring.SpringPlugin;
import com.creditease.ns.log.NsLog;

public class GlobalScope {

	private static NsLog flowLog = NsLog.getFlowLog("全局域状态日志打印","主要用作调试");

	//这个属性比较特殊，这里使用了一个trick，因为我们其实本质上需要catalogsdef下的元素都是跨文件共享的
	//但是本质上它又属于catalogsdef的localscope中的，为了容易管理又能被快速的查找，所以这里加入了这样的map
	private ConcurrentHashMap<String,CatalogsDef> globalIdToElement;

	private ConcurrentHashMap<String,CatalogsDef> filePathToCatalogs;

	private String logPrefix = "[GlobalScope] ";

	public static  boolean hasSpring = false;

	private SpringPlugin springPlugin;

	private ConfigManager configManager;
	
	private boolean isInited = false;

	public ConfigManager getConfigManager() {
		return configManager;
	}

	public void setConfigManager(ConfigManager configManager) {
		this.configManager = configManager;
	}

	private static GlobalScope globalScope = null;

	private GlobalScope(){
	}

	public static synchronized GlobalScope getInstance(){
		if (globalScope == null) {
			globalScope = new GlobalScope();
		}
		return globalScope;
	}

	public void registerCatalogsDef(CatalogsDef catalogsDef)
	{
		this.filePathToCatalogs.put(catalogsDef.getFilePath(), catalogsDef);
		flowLog.debug("# 注册catalogs到全局域成功 {} {} {}", catalogsDef.getFilePath(),catalogsDef.getId(),filePathToCatalogs.size());
	}

	public void unRegisterCatalogsDef(CatalogsDef catalogsDef)
	{
		this.filePathToCatalogs.remove(catalogsDef.getFilePath());
		flowLog.debug("# 移除catalogs 成功 {} {} {}", catalogsDef.getFilePath(),catalogsDef.getId(),filePathToCatalogs.size());
	}
	//这里没有包含catalogs
	private void register(ElementDef elementDef) throws Exception
	{
		if(elementDef instanceof CatalogsDef)
		{
			return;
		}

		ElementDef def = globalIdToElement.putIfAbsent(elementDef.getId(), (CatalogsDef)elementDef.getParentElementDef());
		if(def != null)
		{
			//不允许出现重复的id
			{
				flowLog.warn("注册ElementDef 失败 存在相同的id {} {}", elementDef.getId(),elementDef.getDesc());
//				throw new Exception("全局域中出现相同id["+elementDef.getId()+"]的元素，请更换id");
			}
		}
		flowLog.debug("注册ElementDef 成功 {} {}", elementDef.getId(),elementDef.getDesc());

	}
	
	private void reRegister(ElementDef elementDef) throws Exception
	{
		if(elementDef instanceof CatalogsDef)
		{
			return;
		}

		unRegister(elementDef);
		ElementDef def = globalIdToElement.putIfAbsent(elementDef.getId(), (CatalogsDef)elementDef.getParentElementDef());
		if(def != null)
		{
			//不允许出现重复的id
			{
				flowLog.error("注册ElementDef 失败 存在相同的id {} {}", elementDef.getId(),elementDef.getDesc());
				throw new Exception("全局域中出现相同id["+elementDef.getId()+"]的元素，请更换id");
			}
		}
		flowLog.debug("注册ElementDef 成功 {} {}", elementDef.getId(),elementDef.getDesc());

	}

	private void unRegister(ElementDef elementDef)
	{
		globalIdToElement.remove(elementDef.getId());
		flowLog.debug("卸载ElementDef 成功 {} {}", elementDef.getId(),elementDef.getDesc());

	}


	public void register(CatalogsDef catalogsDef,Map<String, ElementDef> map) throws Exception
	{
		for (Iterator<String> iterator = map.keySet().iterator(); iterator.hasNext(); ) 
		{
			String elementId = (String) iterator.next();
			register(map.get(elementId));
		}
	}
	
	public void reRegister(CatalogsDef catalogsDef,Map<String, ElementDef> map) throws Exception
	{
		catalogsDef.writeLock();
		try
		{
			unRegister(catalogsDef,map);
			for (Iterator<String> iterator = map.keySet().iterator(); iterator.hasNext(); ) 
			{
				String elementId = (String) iterator.next();
				reRegister(map.get(elementId));
	
			}
		}
		finally
		{
			catalogsDef.writeUnLock();
		}
	}
	

	public void unRegister(CatalogsDef catalogsDef,Map<String, ElementDef> map) throws Exception
	{
		for (Iterator<String> iterator = map.keySet().iterator(); iterator.hasNext(); ) 
		{
			String elementId = (String) iterator.next();
			unRegister(map.get(elementId));

		}
	}

	public synchronized void init()
	{
		if (!isInited) 
		{
			globalIdToElement = new ConcurrentHashMap<String, CatalogsDef>();
			filePathToCatalogs = new ConcurrentHashMap<String, CatalogsDef>();
			isInited = true;
			flowLog.debug("初始化GlobalScope成功");
		}
			
	}

	public CatalogDef getCatalogDef(String catalogId)
	{
		//这里加读锁 防止在重载更新catalogsdef时 给了组装链的组装器一个中间状态的对象
		CatalogsDef catalogsDef = globalIdToElement.get(catalogId);
		if (catalogsDef != null) 
		{
			catalogsDef.readLock();
			try
			{
				ElementDef elementDef = catalogsDef.getElementDefById(catalogId);
				if (elementDef == null) 
				{
					flowLog.warn("获取CatalogDef失败，没有获取到对应的ElementDef {} {} {}",catalogId,globalIdToElement,filePathToCatalogs);
					return null;
				}
	
				if (elementDef instanceof CatalogDef) 
				{
					return (CatalogDef)elementDef;
				}
				else 
				{
					flowLog.debug("获取CatalogDef 失败 获取元素不是CatalogDef {} {} {}", elementDef.getId(),elementDef.getDesc(),elementDef.getClass().getSimpleName());
				}
			}
			finally
			{
				catalogsDef.readUnLock();
			}

		}

		flowLog.warn("获取CatalogDef失败，没有获取到对应的catalogsDef {} {} {}",catalogId,globalIdToElement,filePathToCatalogs);
		
		return null;

	}
	public List<CatalogsDef> getAllCatalogsDef()
	{
		return 	new ArrayList<CatalogsDef>(filePathToCatalogs.values());
	}

	public ElementDef getGlobalScopeElement(String elementId)
	{
		CatalogsDef catalogsDef = globalIdToElement.get(elementId);
		if (catalogsDef != null) 
		{
			catalogsDef.readLock();
			ElementDef elementDef =	catalogsDef.getElementDefById(elementId);
			catalogsDef.readUnLock();
			return elementDef;
		}
		return null;
	}

	public SpringPlugin getSpringPlugin() {
		return springPlugin;
	}

	public void setSpringPlugin(SpringPlugin springPlugin) {
		this.springPlugin = springPlugin;
	}

	public CatalogsDef getCatalogsDefByFilePath(String filePath)
	{
		return this.filePathToCatalogs.get(filePath);
	}


}
