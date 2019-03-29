package com.creditease.ns.controller.chain.def;

import com.creditease.ns.chains.def.CatalogDef;
import com.creditease.ns.log.NsLog;
import com.creditease.ns.transporter.context.XmlAppTransporterContext;
import org.w3c.dom.Element;

public class ControllerCatalogDef extends CatalogDef {
	private String contentType;
	private static NsLog frameLog = NsLog.getFramLog("Controller", "ControllerCatalogDef");
	@Override
	public void init(Element element) throws Exception {
		super.init(element);
		String ctype = element.getAttribute("contentType");
		if (ctype == null || ctype.trim().length() < 1) 
		{
			ctype = "json";
		}
		
		this.contentType = ctype;
		frameLog.debug("初始化ControllerCatalogDef 成功 {}", contentType);
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
}
