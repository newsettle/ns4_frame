package com.creditease.ns.log;

/**
 * 
* @ClassName: NsLogInf 
* @Description: log接口
* @author dingzhiwei
* @date 2015年11月5日 上午11:23:54 
*
 */
public interface NsLogInf {
    public void debug(String message, Object... args);
    public void info(String message, Object... args);
    public void warn(String message, Object... args);
    public void error(Throwable e, String message, Object... args);
	//仅仅用于打印辅助调试信息（而在debug()中已经，无需单独代码调用）
    public boolean isDebugEnabled();
    public boolean isInfoEnabled();
}

