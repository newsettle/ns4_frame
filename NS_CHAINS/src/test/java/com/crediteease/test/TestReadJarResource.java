package com.crediteease.test;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.Resource;

import com.creditease.ns.chains.config.XmlConfigManager;
import com.creditease.ns.chains.config.XmlConfigManager4Spring;

public class TestReadJarResource {
	
	public static void main(String[] args) 
	{
		try {
			XmlConfigManager configManager = XmlConfigManager4Spring.getInstance();
			configManager.startUp();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
