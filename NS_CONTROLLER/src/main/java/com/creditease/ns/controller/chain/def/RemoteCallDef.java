package com.creditease.ns.controller.chain.def;

import com.creditease.framework.util.StringUtil;
import org.w3c.dom.Element;

public class RemoteCallDef extends PublishDef{
	@Override
	public void init(Element e) throws Exception {
		super.init(e);
		if (StringUtil.isEmpty(this.serviceName)) 
		{
			throw new Exception("RemoteCall元素必须配置serviceName属性!");
		}
		
	}
}
