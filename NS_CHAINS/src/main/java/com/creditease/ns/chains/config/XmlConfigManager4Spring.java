package com.creditease.ns.chains.config;

import java.io.File;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.creditease.framework.util.XMLUtil;
import com.creditease.ns.chains.context.GlobalScope;
import com.creditease.ns.chains.def.CatalogsDef;
import com.creditease.ns.chains.def.DefFactory;


public class XmlConfigManager4Spring extends XmlConfigManager
{
	@Override
	public void startUp() throws Exception 
	{
		boolean isAutoReload = false;

		if(System.getProperty(AUTORELOAD_KEY) != null && System.getProperty(AUTORELOAD_KEY).equals("true"))
		{
			isAutoReload = true;
		}
		globalScope = GlobalScope.getInstance();

		loggerWrapper.info("# 加载配置文件流 #");
		loggerWrapper.debug("# 配置文件路径:{} #",resourcePath);
		this.filesToLastModifiedTime = new ConcurrentHashMap<String, Long>();

		loadInputStreams4ConfigOnlyInSpring();
		loggerWrapper.info("# 加载配置文件流 OK #");

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
	}

	public static synchronized XmlConfigManager getInstance() throws Exception{
		if (xmlConfigManager == null) {
			xmlConfigManager = new XmlConfigManager4Spring();
			loggerWrapper.debug("# 创建XmlConfigManager4Spring成功 #");
		}
		return xmlConfigManager;
	}


	protected void loadInputStreams4ConfigOnlyInSpring() throws Exception{
		ResourcePatternResolver resolver = (ResourcePatternResolver) new PathMatchingResourcePatternResolver(); 		


		Resource[] resources = null;

		//分割多个文件
		String[] configPaths = resourcePath.split(",");


		for (String configPath : configPaths) 
		{

			if (configPath.indexOf(".") != -1) 
			{
				resources = resolver.getResources("classpath:"+this.resourcePath);
			}
			else if(configPath.endsWith("/"))
			{
				resources = resolver.getResources("classpath:"+this.resourcePath+"*");
			}
			else
			{
				resources = resolver.getResources("classpath:"+this.resourcePath+"/*");
			}

			if (resources != null && resources.length > 0) 
			{
				for (int i = 0; i < resources.length; i++) 
				{
					loggerWrapper.debug("#  解析配置文件流{} #", resources[i].getURL());

					InputStream inputStream = resources[i].getInputStream();
					loadInputStream4Config(inputStream,resources[i].getURL().toString());
					inputStream.close();
				}
			}
		}


	}


	protected void loadInputStream4Config(InputStream inputStream,String resourceUrl) throws Exception{
		Document doc = XMLUtil.load(inputStream,"utf-8");
		Element root = doc.getDocumentElement();
		CatalogsDef catalogsDef = (CatalogsDef)DefFactory.createElmentDef(root);
		catalogsDef.setFilePath(resourceUrl);
		globalScope.register(catalogsDef,catalogsDef.getLocalScope());
		globalScope.registerCatalogsDef(catalogsDef);
		loggerWrapper.debug("# 解析配置文件流{} OK #");
	}
}
