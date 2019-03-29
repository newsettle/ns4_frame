import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 这是一个性能测试程序
 * 我们的目的想要统计出如下几个问题:
 * 1.1秒并发(发出并获取响应)
 * 2.2秒并发
 * 3.3秒并发
 * 4.平均响应时间
 * 5.资源消耗情况
 * 
 * 这个应用应该有如下的性质
 * 1.可以指定尝试的次数
 * 2.可以指定尝试的线程数
 * 3.可以指定访问目标
 * 
 * @author liuyang
 *2015年10月19日下午4:22:55
 */
public class PerformanceTester {
	static Logger logger = LoggerFactory.getLogger(PerformanceTester.class);
	
	private static int status = 0;
	private static int testTimes = 3;
	private static int concurrentThreadNums = 1;
	private static String targetUrl = "http://127.0.0.1:8899/testInnerChain?name=root&password=123456";
	/*
	 * 在指定的测试次数中
	 * 	循环启动指定数量的线程
	 *有三个桶 存放着当次的访问数据
	 *每次执行结束后 清理桶并统计得到的统计信息
	 *
	 *然后综合计算平均响应时间 总响应时间/测试次数
	 *
	 */

	public static void main(String[] args) throws InterruptedException 
	{
		//TODO getOpt 利用commons cli去做
		
		//采用默认值三次
//		for (int i = 0; i < testTimes; i++)
		for (int i = 0; i < 100; i++)
//		while(true)
		{
			ExecutorService executorService = Executors.newCachedThreadPool();
			for (int j = 0; j < concurrentThreadNums; j++) 
			{
				executorService.execute(new TestBusiness());
			}
			executorService.shutdown();
			executorService.awaitTermination(20000, TimeUnit.MILLISECONDS);
		}
	}
	
	public static void reset()
	{
		status = 0;
	}
	
	static class TestBusiness implements Runnable
	{

		@Override
		public void run() {
			//访问特定的dispatcher地址
			long startTime = System.currentTimeMillis();
			try {
				URL url = new URL(targetUrl);
				URLConnection httpURLConnection = url.openConnection();
				httpURLConnection.setUseCaches(false);
				httpURLConnection.connect();
				Object o = httpURLConnection.getContent();
				long cost = (System.currentTimeMillis()-startTime);
				if (cost > 100) 
				{
					logger.debug(o + " cost:"+cost+"ms");
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		
	}

}
