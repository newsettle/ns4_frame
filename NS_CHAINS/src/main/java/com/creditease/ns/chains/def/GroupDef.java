package com.creditease.ns.chains.def;

import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
/**
 * 可能出现的元素有command ref 暂时不允许嵌套group
 * @author liuyang
 *2015年9月15日下午11:13:07
 */
public class GroupDef extends AbstractContainerDef {

	@Override
	public void init(Element element) throws Exception {
		framLog.info("# 初始化GroupDef id:{} #",this.id);
		super.init(element);
		framLog.info("# 初始化GroupDef id:{} OK #",this.id);
	}
	
	public void handle() {
		
	}

	public void postInit() throws Exception {
		//TODO 检查
		super.postInit();
		if (id == null || id.trim().length() < 1) 
		{
			throw new Exception("必须为Group指定一个id");
		}
	}
	
	
	
}
