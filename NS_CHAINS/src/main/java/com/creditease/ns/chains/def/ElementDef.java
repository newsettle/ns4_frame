package com.creditease.ns.chains.def;

import java.util.List;

import org.w3c.dom.Element;

import com.creditease.ns.chains.start.ChainLauncher;
import com.creditease.ns.log.NsLog;

public interface ElementDef {
	static NsLog framLog = ChainLauncher.framLog;
	static NsLog flowLog = NsLog.getFlowLog("元素动态加载过程", "解析的元素被动态加载");
	public String getId();
	public void handle() throws Exception;
	public void init(Element e) throws Exception;
	public String getDesc();
	public void tranverse(List targetList) throws Exception;
	public ElementDef find(String refId) throws Exception;
	public void postInit() throws Exception;
	public boolean isCanBeRefered();
	public void setParentElementDef(ElementDef elementDef);
	public ElementDef getParentElementDef();
	public void checkLoopEmbedElement(List<ElementDef> parentElementDefs) throws Exception;
	public boolean isSuperiorElement(ElementDef elementDef);
}
