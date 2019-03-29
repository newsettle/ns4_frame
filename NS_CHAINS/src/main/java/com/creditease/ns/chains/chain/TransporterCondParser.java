package com.creditease.ns.chains.chain;

import java.util.Map;

import org.mvel2.MVEL;

import com.creditease.ns.chains.exchange.Exchanger;
import com.creditease.ns.log.NsLog;


/**
 * 使用条件判断有如下的问题
 * 当在chains一上来就判断条件 要做起来很麻烦
 * 首先我们要取的是exchanger中的值
 * 而exchanger中的值是通过servicemessage中的exchangescope中的值转换过来的
 * 如果要满足上来就判断的使用 就必须直接将servicemessage中的exchangescope拿来做判断条件
 * 或者在canExecute方法中直接get再做判断或者将其转换成本地一个map再判断
 * 同时需要实现exchanger接口
 * 
 * @author liuyang
 *2016年6月24日上午10:24:56
 */
public class TransporterCondParser extends AbstractConditionaleCommand {
private static NsLog flowLog = Chain.flowLog;
	
	@Override
	public boolean canExecute(String cond, Exchanger exchanger) {
				
		//反射获取servicemessage中的域
		Map exchangeScope = exchanger.getExchangeScope();
		try {
			
			boolean isCan = (Boolean)MVEL.eval(cond,exchangeScope);
			flowLog.debug("业务链条件判断 成功 是否符合条件:{} cond:{} exchangeScope:{}", isCan,cond,exchangeScope);
			return isCan;
		}  catch (SecurityException e) {
			flowLog.error("业务链条件判断 失败 安全异常 cond:{} exchangeScope:{}", false,cond,exchangeScope);
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			flowLog.error("条件判断 失败 传入参数不合法 cond:{} exchangeScope:{}", cond,exchangeScope);
			e.printStackTrace();
		} 

		
		return false;
	}
	
}
