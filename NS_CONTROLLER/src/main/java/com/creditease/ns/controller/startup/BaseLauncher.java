package com.creditease.ns.controller.startup;

import com.creditease.framework.util.FileUtils;
import com.creditease.ns.chains.constants.DefConstants;
import com.creditease.ns.chains.start.ChainLauncher;
import com.creditease.ns.controller.chain.def.ControllerCatalogDef;
import com.creditease.ns.controller.chain.def.ControllerConditionDef;
import com.creditease.ns.controller.chain.def.PublishDef;
import com.creditease.ns.controller.chain.def.RemoteCallDef;
import com.creditease.ns.controller.constants.ControllerConstants;
import com.creditease.ns.framework.startup.LifeCycle;
import com.creditease.ns.log.NsLog;


/**
 * 在j2se下启动commons-chain的配置
 * 同时 启动底层的transporter
 * @author liuyang
 *2015年8月20日下午5:29:12
 */
public class BaseLauncher implements LifeCycle {	
	private String transporterConfigPath = "nscontroller.xml";
	private String chainConfigPath = "servicechain.xml";
	private boolean isStarted;
	private static BaseLauncher self = new BaseLauncher();
	private static NsLog frameLog = ControllerConstants.FRAME_LOG;
	
	public static ThreadLocal<String> curConfigFile = new ThreadLocal<String>()
			{
		protected String  initialValue() {

			return null;
		};
			};
	
	public String getTransporterConfigPath() {
		return transporterConfigPath;
	}

	public void setTransporterConfigPath(String transporterConfigPath) {
		this.transporterConfigPath = transporterConfigPath;
	}
	
	public String getChainConfigPath() {
		return chainConfigPath;
	}

	public void setChainConfigPath(String chainConfigPath) {
		this.chainConfigPath = chainConfigPath;
	}

	public synchronized void  startUp() throws Exception
	{
		frameLog.info("### NSController启动  ###");
		if (!isStarted) 
		{
			DefConstants.registerCustomElement("publish", PublishDef.class);
			DefConstants.registerCustomElement("remotecall", RemoteCallDef.class);
			DefConstants.customElements.put(DefConstants.conditionElementName,ControllerConditionDef.class);
			DefConstants.customElements.put(DefConstants.catalogElementName, ControllerCatalogDef.class);
			if (curConfigFile.get() != null) 
			{
				chainConfigPath = curConfigFile.get();
				chainConfigPath = FileUtils.convertToAbsolutePath(chainConfigPath);
			}
			else
			{
				chainConfigPath = Thread.currentThread().getContextClassLoader().getResource("controllerconfig").getPath();
			}
			ChainLauncher chainLauncher = ChainLauncher.getInstance();
			chainLauncher.setResourcePath(chainConfigPath);
			chainLauncher.start();
			
		}
		isStarted = true;
		
		frameLog.info("### NSController启动 OK ###");
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}
	
	
	
	
	public static  BaseLauncher getInstance()
	{
		return self;
	}
	
	public static void main(String[] args) throws Exception 
	{
		BaseLauncher.getInstance().startUp();
	}
}
