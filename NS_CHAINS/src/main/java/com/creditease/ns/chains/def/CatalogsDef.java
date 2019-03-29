package com.creditease.ns.chains.def;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.w3c.dom.Element;

import com.creditease.ns.chains.context.GlobalScope;


public class CatalogsDef extends AbstractContainerDef{
	private String filePath;
	private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	private boolean isLoaded = false;
	
	public void handle() {

	}

	@Override
	public void init(Element element) throws Exception {
		framLog.info("# 初始化CatalogsDef id:{} #");
		super.init(element);
		framLog.info("# 初始化CatalogsDef id:{} desc:{} children:{} OK #", this.getClass().getSimpleName(),id,desc,children.size());
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	

	public String getId() {
		return null;
	}


	public boolean isCanBeRefered() {
		return false;
	}

	public void readLock()
	{
		this.readWriteLock.readLock().lock();
	}
	
	public void readUnLock()
	{
		this.readWriteLock.readLock().unlock();
	}
	
	public void writeLock()
	{
		this.readWriteLock.writeLock().lock();
	}
	
	public void writeUnLock()
	{
		this.readWriteLock.writeLock().unlock();
	}

	public boolean isLoaded() {
		return isLoaded;
	}

	public void setLoaded(boolean isLoaded) {
		this.isLoaded = isLoaded;
	}
	
}

