package com.creditease.ns.chains.start;


import com.creditease.framework.util.FileUtils;
import com.creditease.ns.chains.config.XmlConfigManager;
import com.creditease.ns.chains.config.XmlConfigManager4Spring;
import com.creditease.ns.chains.context.GlobalScope;
import com.creditease.ns.framework.spring.GenSpringPlugin;
import com.creditease.ns.framework.spring.SpringPlugin;
import com.creditease.ns.framework.startup.LifeCycle;
import com.creditease.ns.log.NsLog;
import com.creditease.ns.log.util.PrintUtil;

/**
 * 整个框架的启动的地方
 * @author liuyang
 *2015年9月14日下午6:35:27
 */
public class ChainLauncher implements LifeCycle {
	public static NsLog framLog = NsLog.getFramLog("启动NS_CHAINS", "启动NS_CHAINS"); 
	private static ChainLauncher self = null;
	private static String CHAIN_CONFIG_KEY = "chainconfig";
	private String resourcePath;
	private boolean parseConfigFileInSpring = false;
    
    public static final String SYSTEM_DISABLE_CHAINS_RESTARTUP = "chains_disable_all_restartup";
    
    public static ThreadLocal<String> curConfigFile = new ThreadLocal<String>()
    		{
    			protected String  initialValue() {
    				
    				return null;
    			};
    		};
    
    
    public ChainLauncher(){
    	
    	System.out.println("加载ChainLauncher");
    	
    }

    public static synchronized ChainLauncher getInstance() throws Exception{
        if (self == null) {
        	self = new ChainLauncher();
        }
        return self;
    }

    
    

	public void startUp() throws Exception{
		boolean isSpring = false;
		SpringPlugin springPlugin = null;
		try
		{
			framLog.debug("# 加载spring插件 #");
			Class.forName("org.springframework.beans.factory.BeanFactory");
			isSpring = true;
			springPlugin = new GenSpringPlugin();
			springPlugin.init();
			framLog.debug("# 加载spring插件 OK #");
		}
		catch(ClassNotFoundException e)
		{
		}

		GlobalScope.hasSpring = isSpring;
		GlobalScope chainsContext = GlobalScope.getInstance();
		if (isSpring) 
		{
			chainsContext.setSpringPlugin(springPlugin);
		}
		chainsContext.init();

		XmlConfigManager configManager;
		try {
//			configManager = XmlConfigManager.getInstance();
			configManager = new XmlConfigManager();
			configManager.setResourcePath(this.resourcePath);
			configManager.startUp();
			chainsContext.setConfigManager(configManager);
		} catch (Exception e) {
			framLog.error("# 解析配置出现异常 #",e);
			throw e;
		}
		
	}

	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	public String getResourcePath() {
		return resourcePath;
	}

	public void setResourcePath(String resourcePath) {
		this.resourcePath = resourcePath;
	}
    

	public boolean isParseConfigFileInSpring() {
		return parseConfigFileInSpring;
	}

	public void setParseConfigFileInSpring(boolean parseConfigFileInSpring) {
		this.parseConfigFileInSpring = parseConfigFileInSpring;
	}

	public  void start() throws Exception
	{			
		long startTime = System.currentTimeMillis();
		PrintUtil.printNs4();
		framLog.info("###启动NS_CHAINS框架###");
		
		String filePath = null;
		
		if (this.getResourcePath() == null) 
		{
			if (curConfigFile.get() != null) 
			{
				filePath = curConfigFile.get();
				framLog.debug("# 找到当前线程包含有配置文件路径 {} #", filePath);
			}
			
			if (filePath == null || filePath.trim().length() < 1) 
			{
				filePath = System.getProperty(CHAIN_CONFIG_KEY);
			}	
			
			if (filePath == null || filePath.trim().length() < 1) 
			{
				filePath = "nschainconfig";
			}
			filePath = FileUtils.convertToAbsolutePath(filePath);
			setResourcePath(filePath);
		}
		else
		{
			framLog.debug("# 已经设置了ResourcePath {} #", this.getResourcePath());
		}
		
		startUp();
		framLog.debug("# 配置文件路径:{} #",filePath);
		framLog.info("###启动NS_CHAINS框架 OK，共耗时:{}ms###",System.currentTimeMillis()-startTime);
	}

	

}
