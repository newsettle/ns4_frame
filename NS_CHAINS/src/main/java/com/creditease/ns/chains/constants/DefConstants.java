package com.creditease.ns.chains.constants;

import java.util.concurrent.ConcurrentHashMap;

import com.creditease.ns.chains.def.Calldef;
import com.creditease.ns.chains.def.CatalogDef;
import com.creditease.ns.chains.def.CatalogsDef;
import com.creditease.ns.chains.def.CatchBlockDef;
import com.creditease.ns.chains.def.ChainDef;
import com.creditease.ns.chains.def.CommandDef;
import com.creditease.ns.chains.def.ConditionDef;
import com.creditease.ns.chains.def.ContainerDef;
import com.creditease.ns.chains.def.ElementDef;
import com.creditease.ns.chains.def.ElseCommandDef;
import com.creditease.ns.chains.def.FinallyBlockDef;
import com.creditease.ns.chains.def.GroupDef;
import com.creditease.ns.chains.def.RefDef;
import com.creditease.ns.chains.def.TryBlockDef;
import com.creditease.ns.log.spi.LoggerWrapper;

public class DefConstants {
	private static LoggerWrapper loggerWrapper = LoggerConstants.DEF_LOGGER;
	public static ConcurrentHashMap<String,Class> customElements = new ConcurrentHashMap<String, Class>();
	public final static String commandElementName = "command";
	public final static String groupElementName = "group";
	public final static String catalogElementName = "catalog";
	public final static String chainElementName = "chain";
	public final static String refElementName = "ref";
	public final static String conditionElementName = "condition";
	public final static String catalogsElementName = "catalogs";
	public final static String callElementName = "call";
	public final static String idString = "id";
	public final static String descString = "desc";
	public final static String tryBlockElementName = "tryBlock";
	public final static String catchBlockElementName = "catchBlock";
	public final static String finallyBlockElementName = "finallyBlock";
	public final static String ifBlockElementName = "if";
	public final static String elifBlockElementName = "elif";
	public final static String elseBlockElementName = "else";
	
	
	static
	{
		customElements.put(commandElementName,  CommandDef.class);
		customElements.put(groupElementName, GroupDef.class);
		customElements.put(catalogElementName, CatalogDef.class);
		customElements.put(chainElementName, ChainDef.class);
		customElements.put(refElementName, RefDef.class);
		customElements.put(conditionElementName, ConditionDef.class);
		customElements.put(catalogsElementName, CatalogsDef.class);
		customElements.put(callElementName, Calldef.class);
		customElements.put(tryBlockElementName, TryBlockDef.class);
		customElements.put(catchBlockElementName, CatchBlockDef.class);
		customElements.put(finallyBlockElementName, FinallyBlockDef.class);
		customElements.put(ifBlockElementName, ConditionDef.class);
		customElements.put(elifBlockElementName, ConditionDef.class);
		customElements.put(elseBlockElementName, ElseCommandDef.class);
	}
	
	private static void putCustomElment(String tagName,Class  elementDef) throws Exception
	{
		Class  cl = customElements.putIfAbsent(tagName,elementDef);
		if (cl != null) 
		{
			loggerWrapper.logDebug("[DefConstants] [注册自定义标签] [失败] [已存在同名标签] [{}] [{}]", tagName,elementDef.getCanonicalName());
			throw new Exception("此tagName["+tagName+"]已经注册过了,请重新换一个tagName");
		}
		loggerWrapper.logDebug("[DefConstants] [注册自定义标签] [成功] [{}] [{}]", tagName,elementDef.getCanonicalName());
	}
	
	public static void registerCustomElement(String tagName,Class  elementDef) throws Exception
	{
		if (!ElementDef.class.isAssignableFrom(elementDef)) 
		{
			loggerWrapper.logDebug("[DefConstants] [注册自定义标签] [失败] [注册的Class必须实现ElementDef接口] [{}] [{}]", tagName,elementDef.getCanonicalName());
			throw new Exception("此class["+elementDef.getCanonicalName()+"]没有实现ElementDef接口");
		}
		putCustomElment(tagName, elementDef);
	}
	
}
