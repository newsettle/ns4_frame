package com.creditease.ns.chains.def;

import java.util.List;
import java.util.Map;

public interface ContainerDef extends ElementDef {
	public List<ElementDef> getChildren();
	public ElementDef getElementDefById(String id);
	public Map<String,ElementDef> getLocalScope();
}
