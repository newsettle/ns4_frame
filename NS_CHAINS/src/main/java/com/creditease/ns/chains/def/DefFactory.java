package com.creditease.ns.chains.def;

import org.w3c.dom.Element;

import com.creditease.ns.chains.constants.DefConstants;
import com.creditease.ns.chains.constants.LoggerConstants;
import com.creditease.ns.chains.start.ChainLauncher;
import com.creditease.ns.log.NsLog;
import com.creditease.ns.log.spi.LoggerWrapper;

public class DefFactory {
	private static NsLog loggerWrapper = ChainLauncher.framLog;
	
	public static ElementDef createElmentDef(Element e) throws Exception
	{
		{
			Class  elementDefClass = DefConstants.customElements.get(e.getTagName());
			if (elementDefClass != null) 
			{
				ElementDef elementDef =	(ElementDef)elementDefClass.newInstance();
				elementDef.init(e);
				loggerWrapper.trace("构造{}并初始化 tag:{}",elementDefClass.getCanonicalName(), e.getTagName());
				return elementDef;
			}
			
		}
		throw new Exception("未识别的Element["+e.getTagName()+"]");
	}
}
