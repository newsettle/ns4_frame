package com.creditease.ns.chains.util;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import com.creditease.framework.util.StringUtil;

public class ElementCheckUtil {
	
	public static List<String> elementsOfNotRequiredDescAttribute = new ArrayList<String>(){{add("chain");}};
	public static void checkElement(Element e) throws Exception
	{
		String tagName = e.getTagName();
		if(!elementsOfNotRequiredDescAttribute.contains(tagName))
		{
			if(StringUtil.isEmpty(e.getAttribute("desc")))
			{
				throw new Exception("NsChains配置的["+e.getTagName()+"]元素["+(StringUtil.isEmpty(e.getAttribute("id")) ? "":e.getAttribute("id"))+"]必须配置desc属性!");
			}
		}
	}
	
}
