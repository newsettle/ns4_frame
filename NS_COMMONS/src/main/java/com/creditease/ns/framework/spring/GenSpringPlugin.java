package com.creditease.ns.framework.spring;

import org.springframework.aop.support.AopUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.creditease.framework.work.ActionWorker;
import com.creditease.framework.work.Worker;
import com.creditease.ns.framework.startup.LifeCycle;
import com.creditease.ns.log.spi.TransporterLog;


public class GenSpringPlugin implements LifeCycle, SpringPlugin {
    private final static String[] paths = {"classpath*:/config/spring/**/*applicationContext.xml",
            "classpath*:/spring/**/*applicationContext.xml",
            "classpath*:/spring/**/*applicationContext.xml",
            "classpath*:/spring/**/applicationContext-*.xml",
            "classpath*:/config/**/*applicationContext-*.xml",
            "classpath*:**/applicationContext.xml",
            "classpath*:**/*-applicationContext-*.xml",
            "classpath*:**/applicationContext-*.xml"
    };
    private static final String LOG_PREFIX = "GenSpring";
    private static ClassPathXmlApplicationContext context;

    @Override
    public synchronized void startUp() {
        if (context == null) {
            context = new ClassPathXmlApplicationContext(paths);
            TransporterLog.logSystemInfo(LOG_PREFIX + " [加载spring配置文件] [成功]", null);
        }
    }

    public static ClassPathXmlApplicationContext getContext()
    {
    	return context;
    }
    
    @Override
    public void destroy() {
        context = null;
    }

    @Override
    public Object getBean(String beanId) {
        return context.getBean(beanId);
    }

    /**
     * 在2015-09-29日发现在测试时开启aop后无法根据具体的service类型获取实例
     * 查询资料后确认当开启aop后由于spring内部会创建代理，如果serviceclass实现了一个接口或者继承了一个类
     * 那么代理本身的机制就是会去实现相同的接口或者继承相同的类然后包裹真正的serviceclass
     * 导致最终无法根据具体的类型获取到bean
     * 
     * 改造如下:
     * 1.判断是否开启了AOP
     * 2.开启了AOP后判断是否传入的类型对应的对象是被代理了
     * 3.代理了之后就去获取真正的被代理的类型，如果等于传入的class 那么就确认这个代理实例是需要的实例
     * 	 返回这个实例
     */
    /**
     * 2015-11-04日
     * 这里原来写死了 只认为worker相关的类会被spring使用
     * 现在支持所有实现了接口然后被aop代理的类
     */
    @Override
    public Object getBeanByClassName(Class<?> className) {
    	boolean isAop = false;
		try {
			Class.forName("org.springframework.aop.Pointcut");
			isAop = true;
		} catch (ClassNotFoundException e) {
		}
		
    	if(isAop)
    	{
			boolean isAopObject = false;
			if(Worker.class.isAssignableFrom(className) || ActionWorker.class.isAssignableFrom(className))
			{
				String[] strs = this.context.getBeanNamesForType(Worker.class);
				if(strs.length < 1)
				{
					strs = this.context.getBeanNamesForType(ActionWorker.class);
				}
		    	for(String str:strs)
		    	{
		    		Object proxy = context.getBean(str);
		    		isAopObject = AopUtils.isAopProxy(proxy);
		    		if(isAopObject)
		    		{
		    			Class target = AopUtils.getTargetClass(proxy);
		    			
		    			if (target.isAssignableFrom(className)) 
						{
							return proxy;
						}
		    		}
		    	}
			}
			else {
				String[] strs = this.context.getBeanNamesForType(className);
				if (strs.length < 1) {
					Class<?>[] interfaces = className.getInterfaces();
					for (int i = 0; i < interfaces.length; i++) {
						strs = this.context.getBeanNamesForType(interfaces[i]);
						if (strs.length > 0) {
					    	for(String str:strs)
					    	{
					    		Object proxy = context.getBean(str);
					    		isAopObject = AopUtils.isAopProxy(proxy);
					    		if(isAopObject)
					    		{
					    			Class target = AopUtils.getTargetClass(proxy);
					    			
					    			if (target.isAssignableFrom(className)) 
									{
										return proxy;
									}
					    		}
					    	}
						}
						
						
					}
					
				}
				else if(strs.length == 1)
				{
					return context.getBean(strs[0]);
				}
				else
				{
				  	for(String str:strs)
			    	{
			    		Object proxy = context.getBean(str);
			    		isAopObject = AopUtils.isAopProxy(proxy);
			    		if(isAopObject)
			    		{
			    			Class target = AopUtils.getTargetClass(proxy);
			    			
			    			if (target.isAssignableFrom(className)) 
							{
								return proxy;
							}
			    		}
			    	}
				}
			}
    	}
        return context.getBean(className);
    }

	@Override
	public void init() throws Exception {
		startUp();
	}
}
