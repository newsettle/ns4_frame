package com.crediteease.test;

import java.util.HashMap;
import java.util.Map;

import org.mvel2.MVEL;

public class TestMvel {
	public static void main(String[] args) 
	{
		Map map = new HashMap<String, Object>();
		map.put("A",100);
		map.put("B", "aaa");
		map.put("c", "aa");
		
		map.put("NAME", "root");
		
		
		System.out.println("结果:"+MVEL.eval("NAME=='root'", map));
	}
}	
