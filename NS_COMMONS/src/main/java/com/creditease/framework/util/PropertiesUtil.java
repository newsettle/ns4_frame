package com.creditease.framework.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class PropertiesUtil {

    private Properties pro = null;

    public PropertiesUtil(String path) throws Exception {
        pro = loadProperty(path);
    }

    public PropertiesUtil()
    {
    	
    }
    
    public PropertiesUtil(InputStream inputStream) {
        pro = new Properties();
        try {
            pro.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getString(String key) {
        try {
            return pro.getProperty(key);
        } catch (Exception e) {
            throw new RuntimeException("key:" + key);
        }
    }

    public String getString(String key, String defaultValue) throws Exception {
        try {
        	if(pro.getProperty(key) == null)
        	{
        		return defaultValue;
        	}
        	else
        	{
        		return pro.getProperty(key);
        	}
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public int getInt(String key) {
        try {
            return Integer.parseInt(pro.getProperty(key));
        } catch (Exception e) {
            throw new RuntimeException("key:" + key);
        }
    }

    public int getInt(String key, int defualtValue) {
        try {
            return Integer.parseInt(pro.getProperty(key));
        } catch (Exception e) {
            return defualtValue;
        }
    }

    public double getDouble(String key) {
        try {
            return Double.parseDouble(pro.getProperty(key));
        } catch (Exception e) {
            throw new RuntimeException("key:" + key);
        }
    }

    public double getDouble(String key, double defaultValue) {
        try {
            return Double.parseDouble(pro.getProperty(key));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public long getLong(String key) {
        try {
            return Long.parseLong(pro.getProperty(key));
        } catch (Exception e) {
            throw new RuntimeException("key:" + key);
        }
    }

    public long getLong(String key, long defaultValue) {
        try {
            return Long.parseLong(pro.getProperty(key));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public float getFloat(String key) {
        try {
            return Float.parseFloat(pro.getProperty(key));
        } catch (Exception e) {
            throw new RuntimeException("key:" + key);
        }
    }

    public float getFloat(String key, float defaultValue) {
        try {
            return Float.parseFloat(pro.getProperty(key));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public boolean getBoolean(String key){
        try {
            return Boolean.parseBoolean(pro.getProperty(key));
        } catch (Exception e) {
            throw new RuntimeException("key:" + key);
        }
    }

    public boolean getBoolean(String key, boolean defaultValue){
        try {
            return Boolean.parseBoolean(pro.getProperty(key));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public Set<Object> getAllKey() {
        return pro.keySet();
    }

    public Collection<Object> getAllValue() {
        return pro.values();
    }

    public Map<String, Object> getAllKeyValue() {
        Map<String, Object> mapAll = new HashMap<String, Object>();
        Set<Object> keys = getAllKey();

        Iterator<Object> it = keys.iterator();
        while (it.hasNext()) {
            String key = it.next().toString();
            mapAll.put(key, pro.get(key));
        }
        return mapAll;
    }

    private Properties loadProperty(String name) throws Exception {
        String filePath = null;
        try {
            filePath = Thread.currentThread().getContextClassLoader().getResource(name).getFile();
        } catch (Exception e) {
            throw new IllegalArgumentException("找不到配置文件:"+name,e);
        }
        FileInputStream fin = null;
        Properties pro = new Properties();
        try {
            fin = new FileInputStream(filePath);
            pro.load(new InputStreamReader(fin, StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new IllegalArgumentException("找不到配置文件:"+name,e);
        } finally {
            if (fin != null) {
                fin.close();
            }
        }
        return pro;
    }

	public Properties getPro() {
		return pro;
	}

	public void setPro(Properties pro) {
		this.pro = pro;
	}
    
    
}