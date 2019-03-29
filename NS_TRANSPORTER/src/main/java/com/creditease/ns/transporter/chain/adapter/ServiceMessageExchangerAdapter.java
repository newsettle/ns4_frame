package com.creditease.ns.transporter.chain.adapter;

import java.util.List;
import java.util.Map;

import com.creditease.framework.exception.NSException;
import com.creditease.framework.pojo.ServiceMessage;
import com.creditease.framework.scope.ExchangeKey;
import com.creditease.framework.scope.ExchangeScope;
import com.creditease.framework.scope.OutKey;
import com.creditease.framework.scope.OutScope;
import com.creditease.framework.scope.RequestScope;
import com.creditease.ns.chains.exchange.Exchanger;
import com.creditease.ns.mq.model.Header;
/**
 * 这个类是为了满足账务组的需求进行添加的
 * 以前的逻辑是servicemessage中的exchange域主要用来进行queue之间的传递信息
 * 传递信息格式主要是json 但是由于这个接口也可以被当做本地链之间的传递媒介使用，所以出现了一些使用
 * 上的追加需求，比如复杂泛型的传递 要保持在本地同一个jvm中始终是一个对象 同一个对象必须保持同一个
 * 引用，这样导致结果就是原有的defaultservicemessage出现了二意定义 即既有本地化传递消息的需求
 * 也有跨queue之间的需求，而且开发人员的理解都是在其中的exchange域中传递对象 对于这种解决方式之一
 * 是加一个local域 本地放入这个域中 但是这个方案第一需要开发人员多理解一个概念 同时要记住
 * 自己的逻辑什么时候放入local什么时候放入exchange 增加了开发复杂度 第二是当一些传递在需求上发生
 * 改变时 开发人员也需求改代码 第三是已经开发了一些代码 在改动也比较麻烦，所以综合考虑 增加现在的这
 * 个类 做一个适配 它本质上是defaultservicemessage的一个代理 当开发人员放入exchange值时，本地
 * 需要的传递信息从这个代理中的局部交换域中获得 同时放入的exchange值也会写入被代理的servicemessage
 * 中的exchange域中 但是这种解决办法存在一个问题就是 会有很多临时的只限于本地传递的对象被放入了跨
 * queue之间的传递当中 他们会被序列化反序列化 降低整体的性能 对于这种 还需要进一步找到更好的解决办法
 * @author liuyang
 *2015年10月28日上午11:34:18
 */

/**
 * 找到了一种解决多传递冗余本地数据的办法
 * 即我们放入一个Abandable的接口
 * 对于只在本地传递的key 需要实现这个接口 
 * 实现了这个接口的key只在本地传递 不会跨queue传递
 * @author liuyang
 *2015年10月29日下午7:41:11
 */
public class ServiceMessageExchangerAdapter implements ServiceMessage {

	private ServiceMessage serviceMessage;
	
    private  RequestScope requestScope;
    private  Exchanger exchanger;
    private  OutScope outScope;
    private final String logPrefix = "[ServiceMessageExchangerAdapter]";
	@Override
	public Header getHeader() {
		return serviceMessage.getHeader();
	}

	@Override
	public void setHeader(Header header) throws NSException {
	}

	@Override
	public <T> T getParameterByType(String paramName, Class<T> clazz) {
		return serviceMessage.getParameterByType(paramName, clazz);
	}

	@Override
	public String getParameter(String paramName) {
		return serviceMessage.getParameter(paramName);
	}


	@Override
	public void setExchange(ExchangeKey key, Object value) throws NSException {
		//这里需要做特殊处理
		doSetExchange(key, value);
		if (!(key instanceof Abandonable)) 
		{
			serviceMessage.setExchange(key, value);
		}
	}

	@Override
	public String getExchange(ExchangeKey key) throws NSException {
		//这里需要做特殊处理
		String exchangerResult = (String)this.exchanger.getExchange(key);
		if(exchangerResult == null && !(key instanceof Abandonable))
		{
			String s = this.serviceMessage.getExchange(key);
			doSetExchange(key, s);
			return s;
		}
		return exchangerResult;
	}

	@Override
	public <T> T getExchangeByType(ExchangeKey key, Class<T> clazz)
			throws NSException {
		
		Object object = this.exchanger.getExchange(key);
		if (object == null && !(key instanceof Abandonable)) 
		{
			object = this.serviceMessage.getExchangeByType(key, clazz);
			doSetExchange(key, object);
		}
		return (T)object;
	}

	@Override
	public void setOut(OutKey outKey, Object value) throws NSException {
		this.serviceMessage.setOut(outKey, value);
	}

	@Override
	public String getOut(OutKey outKey) throws NSException {
		return this.serviceMessage.getOut(outKey);
	}

	@Override
	public <T> T getOutByType(OutKey outKey, Class<T> clazz) throws NSException {
		return this.serviceMessage.getOutByType(outKey, clazz);
	}

	@Override
	public String getJsonOut() throws NSException {
		return this.serviceMessage.getJsonOut();
	}

	@Override
	public void setOutHtmlAsRedirect(String url) throws NSException {
		this.serviceMessage.setOutHtmlAsRedirect(url);
	}

	@Override
	public void setOutHtmlAsWinOnload(Map<String, String> form, String url)
			throws NSException {
		this.serviceMessage.setOutHtmlAsWinOnload(form, url);
	}

	public ServiceMessage getServiceMessage() {
		return serviceMessage;
	}

	public void setServiceMessage(ServiceMessage serviceMessage) {
		this.serviceMessage = serviceMessage;
	}

	public RequestScope getRequestScope() {
		return requestScope;
	}

	public void setRequestScope(RequestScope requestScope) {
		this.requestScope = requestScope;
	}

	public Exchanger getExchanger() {
		return exchanger;
	}

	public void setExchanger(Exchanger exchanger) {
		this.exchanger = exchanger;
	}

	public OutScope getOutScope() {
		return outScope;
	}

	public void setOutScope(OutScope outScope) {
		this.outScope = outScope;
	}

	@Override
	public void clearAllOut() throws NSException {
		this.serviceMessage.clearAllOut();
	}

	@Override
	public void removeOut(OutKey outKey) throws NSException {
		this.serviceMessage.removeOut(outKey);
	}
	
	private void doSetExchange(ExchangeKey key,Object value)
	{
		this.exchanger.setExchange(key, value);
		this.exchanger.setExchange(key.toString(), value);
	}

	@Override
	public void setOutHtmlConent(String htmlContent) throws NSException {
		this.serviceMessage.setOutHtmlConent(htmlContent);
	}
	
	@Override
	public void setOutPlainText(String plainText) throws NSException {
		this.serviceMessage.setOutPlainText(plainText);
		
	}
}
