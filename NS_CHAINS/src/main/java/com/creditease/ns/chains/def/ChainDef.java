package com.creditease.ns.chains.def;

import java.util.List;

import org.w3c.dom.Element;

public class ChainDef extends AbstractContainerDef{

	@Override
	public void init(Element element) throws Exception {
		framLog.info("# 初始化ChainDef id:{} #",this.id);
		super.init(element);
		framLog.info("# 初始化ChainDef id:{} OK #",this.id);
	}
	
	public void handle() {
		// TODO Auto-generated method stub
		
	}


	public boolean isCanBeRefered() {
		return true;
	}

}
