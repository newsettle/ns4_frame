package com.creditease.ns.controller.chain.def;

import com.creditease.ns.chains.def.ConditionDef;
import com.creditease.ns.controller.constants.ControllerConstants;
import org.w3c.dom.Element;

public class ControllerConditionDef extends ConditionDef
{
	//不想配置classname
	@Override
	public void init(Element e) throws Exception {
		super.init(e);
		if (className == null || className.trim().length() < 1) 
		{
			className = ControllerConstants.DEFAULT_CONDITION_COND_CLASSNAME;
		}
	}
}
