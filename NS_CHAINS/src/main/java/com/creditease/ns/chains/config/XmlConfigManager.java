package com.creditease.ns.chains.config;

import com.creditease.framework.util.XMLUtil;
import com.creditease.ns.chains.constants.ChainConstants;
import com.creditease.ns.chains.context.GlobalScope;
import com.creditease.ns.chains.def.CatalogsDef;
import com.creditease.ns.chains.def.DefFactory;
import com.creditease.ns.chains.start.ChainLauncher;
import com.creditease.ns.log.NsLog;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class XmlConfigManager implements ConfigManager
{
	protected static NsLog loggerWrapper = ChainLauncher.framLog;

	protected String resourcePath;

	protected static XmlConfigManager xmlConfigManager = null;

	protected GlobalScope globalScope;

	protected String logPrefix = "[XmlConfigManager] ";

	protected Map<String,Long> filesToLastModifiedTime;

	protected static String AUTORELOAD_KEY = "autoreload";

	boolean isStarted = false;

	public XmlConfigManager(){
	}

	private List<File> configFiles = new ArrayList<File>();

	public static synchronized XmlConfigManager getInstance() throws Exception{
		if (xmlConfigManager == null) {
			xmlConfigManager = new XmlConfigManager();
		}
		return xmlConfigManager;
	}

	public void startUp() throws Exception 
	{
		boolean isAutoReload = false;

		if(System.getProperty(AUTORELOAD_KEY) != null && System.getProperty(AUTORELOAD_KEY).equals("true"))
		{
			isAutoReload = true;
		}
		globalScope = GlobalScope.getInstance();

		loggerWrapper.info("# 加载配置文件 #");
		loggerWrapper.debug("# 配置文件路径:{} #",resourcePath);

		//分割多个文件
		String[] configPaths = resourcePath.split(",");

		this.filesToLastModifiedTime = new ConcurrentHashMap<String, Long>();

		for (String configPath : configPaths) 
		{
			File configFile = new File(configPath);
			if (configFile.isDirectory()) 
			{
				loadDirConfig(configFile);
			}
			else 
			{
				loadFileConfig(configFile);
			}
		}

		loggerWrapper.info("# 加载配置文件 OK #");

		List<CatalogsDef> allCatalogsDefs =	globalScope.getAllCatalogsDef();
		for (Iterator<CatalogsDef> iterator = allCatalogsDefs.iterator(); iterator.hasNext(); ) 
		{
			CatalogsDef catalogsDef = (CatalogsDef) iterator.next();

			if (!isAutoReload) 
			{
				if (catalogsDef.isLoaded()) 
				{
					continue;
				}
			}
			
			catalogsDef.postInit();
			catalogsDef.setLoaded(true);
		}

		if(isAutoReload)
		{
			loggerWrapper.info("# 启动自动加载心跳线程 #");
			loggerWrapper.debug("# 心跳间隔时长:{}ms #",ChainConstants.HEART_BEAT_INTERVAL_SECONDS);
			ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
			scheduledExecutorService.scheduleWithFixedDelay(new HeartBeatThread(), ChainConstants.HEART_BEAT_INTERVAL_SECONDS, ChainConstants.HEART_BEAT_INTERVAL_SECONDS, TimeUnit.MILLISECONDS);
			loggerWrapper.info("# 启动自动加载心跳线程 OK #");
		}

	}

	public void destroy() 
	{

	}

	public String getResourcePath() {
		return resourcePath;
	}

	public void setResourcePath(String resourcePath) {
		this.resourcePath = resourcePath;
	}

	/**
	 * 读取一个目录的catalogs文件
	 * @param dir
	 */
	protected void loadDirConfig(File dir) throws Exception{
		FileFilter fileFilter = new XmlAndDirFileFilter();
		File[] files = dir.listFiles(fileFilter);
		for (int i=0;i<files.length;i++){
			File file = files[i];
			loadFileConfig(file);
		}
	}

	/**
	 * 
 	<catalogs desc="">
		<command id="唯一标识" queue="queuename" class="className" desc="command的描述" />
		<group id="唯一标识">
			<command id="唯一标识" queue="queuename" class="className" desc="command的描述" />
			<ref name = "zzz" ref="command的唯一标识" desc="">
		</group>
		<catalog id="catalog名称">
			<command id="唯一标识" queue="queuename" class="className" desc="command的描述" />
			<chain id="唯一标识">
				<ref ref="command的唯一标识或者group的唯一标识" />
				<ref ref="command的唯一标识或者group的唯一标识" />
				<!--condition暂时不用else condition是获取传入的消息中的键值来做判断的 -->
				<condition id="sndSmsFlag" cond="sndSmsFlag=='1'" desc="如果sndSmsFlag=1，需要下发短信">
					<ref ref="command的唯一标识或者group的唯一标识" desc="下发短信通知"/>
				</condition>
			</chain>	
		</catalog>
	</catalogs>﻿

	 * @param file
	 * @throws Exception
	 */

	protected void loadFileConfig(File file) throws Exception{
		long startTime = System.currentTimeMillis();
		if (file.isDirectory()) 
		{
			loadDirConfig(file);
			return;
		}
		loggerWrapper.debug("# 解析配置文件{} #",file.getAbsolutePath());
		Document doc = XMLUtil.load(file);
		Element root = doc.getDocumentElement();
		CatalogsDef catalogsDef = (CatalogsDef)DefFactory.createElmentDef(root);
		catalogsDef.setFilePath(file.getAbsolutePath());
		
		if (globalScope.getCatalogsDefByFilePath(catalogsDef.getFilePath()) != null) 
		{
			loggerWrapper.warn("# 发现重复的catalogsDef{},不会重复注册 #",file.getAbsolutePath());
			return;
		}
		
		globalScope.register(catalogsDef,catalogsDef.getLocalScope());
		globalScope.registerCatalogsDef(catalogsDef);
		this.configFiles.add(file);
		this.filesToLastModifiedTime.put(file.getAbsolutePath(), file.lastModified());
		loggerWrapper.debug("# 解析配置文件{} OK #",file.getAbsolutePath());
	}


	/**
	 * 重载文件
	 * 首先尝试读取配置文件 文件出错，则沿用老的一套配置
	 * 然后尝试解析其中的元素 出现错误 则沿用老的一套配置
	 * 然后阻塞正在获取元素的对象
	 * @param file
	 * @throws Exception 
	 */
	protected void reloadFile(File file) throws Exception 
	{
		long startTime = System.currentTimeMillis();
		loggerWrapper.info("# 自动重载配置文件{} (3) #",file.getAbsolutePath());
		Document tempDoc = null;
		try
		{
			tempDoc = XMLUtil.load(file);
		}
		catch(Exception e)
		{
			loggerWrapper.error("# (1/3)分析配置文件 X #",e);
			return;
		}
		loggerWrapper.debug("# (1/3)分析配置文件 √ #");

		Document doc = tempDoc;

		Element root = doc.getDocumentElement();
		CatalogsDef tempCatalogsDef = null;  
		try
		{
			tempCatalogsDef = (CatalogsDef)DefFactory.createElmentDef(root);
		}
		catch(Exception e)
		{
			loggerWrapper.debug("# (2/3)解析Catalogs所有元素 X #",e);
			return;
		}
		loggerWrapper.debug("# (2/3)解析Catalogs所有元素 √ #");

		CatalogsDef catalogsDef = tempCatalogsDef;
		catalogsDef.setFilePath(file.getAbsolutePath());
		globalScope.reRegister(catalogsDef,catalogsDef.getLocalScope());
		globalScope.registerCatalogsDef(catalogsDef);
		catalogsDef.postInit();
		loggerWrapper.debug("# (3/3)注册catalogs到全局域中 √ #");
		loggerWrapper.info("# 自动重载配置文件{} OK #",file.getAbsolutePath());
	}

	protected void clearOldElementDefsOf(CatalogsDef catalogsDef) 
	{

	}

	protected void initConfig()
	{

	}

	class XmlAndDirFileFilter implements FileFilter {

		public boolean accept( File pathname) {
			String tmp=pathname.getName().toLowerCase();
			if(tmp.endsWith(".xml") || pathname.isDirectory()){
				return true;
			}
			return false;
		}
	}

	class HeartBeatThread implements Runnable
	{
		@Override
		public void run() {
			//定期检查files 
			long startTime = System.currentTimeMillis();
			try
			{
				//先判断是否有已经删除的文件
				checkExistedFiles(configFiles);
				List<File> newestFiles = getAllFiles(resourcePath);
				//文件已经存在
				//文件是新增加的
				for (Iterator<File> iterator = newestFiles.iterator(); iterator.hasNext(); ) 
				{
					File file = (File) iterator.next();
					boolean isFound = false;
					long newLastModified = file.lastModified();
					for (Iterator<File> iterator2 = configFiles.iterator(); iterator2.hasNext(); ) 
					{
						/*这里因为每次getLastModified总是取拿最新的时间 
						 * 所以两个无法进行比较 只能去保留以前的值和现在新获取的值进行比较
						 */

						File existedFile = (File) iterator2.next();
						long oldLastModified = filesToLastModifiedTime.get(existedFile.getAbsolutePath());
						if (existedFile.getAbsolutePath().equals(file.getAbsolutePath())) 
						{
							//有新增文件或者文件有修改 我们都需要重新加载文件
							//为了防止当我们要加载文件的时候 文件又突然被删除的情况 我们需要再次判断文件是否存在
							isFound = true; 
							if(oldLastModified != newLastModified)
							{
								if (file.exists()) 
								{
									filesToLastModifiedTime.put(file.getAbsolutePath(), newLastModified);
									reloadFile(file);
								}
							}

						}
					}

					if (!isFound) 
					{
						if (file.exists()) 
						{
							filesToLastModifiedTime.put(file.getAbsolutePath(), newLastModified);
							reloadFile(file);
						}
					}


				}

				configFiles = newestFiles;
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}

		}

		private List<File> getAllFiles(String resourcePath) {
			File configFile = new File(resourcePath);
			List<File> files = new ArrayList<File>();
			if (configFile.isDirectory()) 
			{
				File[] cFiles = configFile.listFiles();
				for (int i = 0; i < cFiles.length; i++) 
				{
					files.addAll(getAllFiles(cFiles[i].getAbsolutePath()));
				}
			}
			else 
			{
				files.add(configFile);
			}
			return files;

		}
		//检查文件是否已经被删除 删除从内存中清除
		private void checkExistedFiles(List<File> configFiles) {
			for (Iterator<File> iterator = configFiles.iterator(); iterator.hasNext(); ) 
			{
				File file = (File) iterator.next();
				if (!file.exists()) 
				{
					//如果文件被删除 清理对应的catalogsdef
					CatalogsDef catalogsDef = globalScope.getCatalogsDefByFilePath(file.getAbsolutePath());
					if (catalogsDef != null) 
					{
						catalogsDef.writeLock();
						try {
							globalScope.unRegisterCatalogsDef(catalogsDef);
							globalScope.unRegister(catalogsDef,catalogsDef.getLocalScope());
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						finally
						{
							catalogsDef.writeUnLock();
						}
					}
					iterator.remove();
				}
			}

		}

	}




}
