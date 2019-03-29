package com.creditease.ns.chains.def;

import org.w3c.dom.Element;

public class CatchBlockDef extends AbstractContainerDef{

	protected String exception = null;
	
	public void init(Element e) throws Exception {
		framLog.info("# 初始化ExceptionBlockDef #");
		this.exception = e.getAttribute("exception");
		super.init(e);
		framLog.info("# 初始化ExceptionBlockDef id:{} desc:{} tagName:{} OK #", id,desc,e.getTagName());
	}

	
	public void handle() {
		// TODO Auto-generated method stub
		
	}

	public void postInit() throws Exception {
		//检查
		super.postInit();
		if (exception != null && exception.trim().length() > 0)
		{
			Class  cl =  Class.forName(exception);
			if(!Exception.class.isAssignableFrom(cl))
			{
				throw new Exception("要捕获的异常必须是Exception的子类");
			}
		}
	}


	public String getException() {
		return exception;
	}

	public void setException(String exception) {
		this.exception = exception;
	}

	
}
