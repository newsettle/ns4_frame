package com.creditease.ns.controller.chain.command;

import com.creditease.framework.pojo.ServiceMessage;
import com.creditease.framework.scope.ExchangeScope;
import com.creditease.framework.util.ReflectionUtils;
import com.creditease.ns.chains.chain.AbstractIfCommand;
import com.creditease.ns.chains.chain.JVMapVariableResolverFactory;
import com.creditease.ns.chains.exchange.Exchanger;
import com.creditease.ns.controller.constants.ControllerConstants;
import com.creditease.ns.log.NsLog;
import org.mvel2.MVEL;
import org.mvel2.integration.VariableResolverFactory;

public class IfCondParser extends AbstractIfCommand {

	private static NsLog flowLog = ControllerConstants.FLOW_LOG;
	
	@Override
	public boolean canExecute(String cond, Exchanger exchanger) {
		
		ServiceMessage serviceMessage =	(ServiceMessage)exchanger.
				getExchangeScope().get(ControllerConstants.DEFAULT_SERVICEMESSAGE_KEY);
		
		//反射获取servicemessage中的域
		String exchangeScopeName = "exchangeScope";
		ExchangeScope exchangeScope;
		try {
			exchangeScope = (ExchangeScope)ReflectionUtils.getFieldValue(exchangeScopeName, serviceMessage);
			VariableResolverFactory factory = new  JVMapVariableResolverFactory(exchangeScope);
			boolean isCan = (Boolean)MVEL.eval(cond, factory);
			flowLog.debug("条件判断 成功 是否符合条件:{} cond:{} exchangeScope:{}", isCan,cond,exchangeScope);
			return isCan;
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		return false;
	}
	
}
