package com.creditease.ns.log;

import java.util.HashMap;
import java.util.Map;

import com.creditease.ns.log.util.PrintUtil;

/**
* @ClassName: NsLogTest 
* @Description: NsLog测试,使用示例
* @author dingzhiwei
* @date 2015年11月5日 下午1:14:32 
*/
public class NsLogTest {

	// 目前分成五大类日志类别
	// getFramLog	-	输出框架启动相关日志
	// getFlowLog	-	输出框架处理业务流程相关日志
	// getMqLog		-	输出框架mq的相关日志
	// getTaskLog	-	输出框架后端任务处理日志
	// getBizLog	-	输出具体业务系统相关业务日志
	// 参数1:moduleName,  参数2：muduleDesc
	// 无参数时输出日志不会打印模块名和描述信息
	// 日志输出格式示例：
	// 1106143147.001 [main] Ns.Fram>NsLogTest.42 [1JLb3j1rQVG9zIehUtslr9,A,B]线程控制|线程控制描述 - 进入controle,参数exchage={method=POST, msg=一笔测试交}
	
//	private static final NsLog _framlog = NsLog.getFramLog("initConfig", "初始化配置"); 
//	private static final NsLog _flowlog = NsLog.getFramLog("线程控制", "线程控制描述");
	
	// 扩展:可以自己指定日志大类别,为了统一,不建议自己指定
	private static final NsLog _log = NsLog.getLog("Ms.nb");
	
	private static final NsLog fileLog = NsLog.getLog("fileLog");
	
	public static void main(String[] args) {
		
/*		LogSetting l = new LogSetting();
		l.setAll("_", "log\\a");
		l.init();*/
		
		
		// 设置UUID
		// 消息ID,NS框架使用
		NsLog.setMsgId("89RML004");
//		NsLog.setPrimayKey("A");
		// 业务系统使用
		NsLog.setSubPrimary("rq09758");
		// 输出ns4框架标识
		PrintUtil.printNs4();
		// 输出jvm信息
		PrintUtil.printJVM();
		while(true) {
			//NsLog.setUniqKey();
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("msg", "一笔测试交");
			paramMap.put("method", "POST");
//			NsTestLogger._framlog.info("进入controle,参数exchage={}", paramMap);
//			NsTestLogger._flowlog.info("通道路由规则,共{}步", 5);
//			NsTestLogger._flowlog.debug("第{}步:加载参数", "1/5");
//			NsTestLogger._flowlog.debug("第{}步:参数name={},sex={}", "2/5", "dingzhiwei", "2");
//			NsTestLogger._flowlog.debug("第{}步:转换路由", "3/5");
//			NsTestLogger._flowlog.debug("第{}步:测试通道", "4/5");
//			NsTestLogger._flowlog.debug("第{}步:选择路由通道{}", "5/5", "联动优势");
//			NsTestLogger._flowlog.info("通道路由规则 ok");
//			NsTestLogger._framlog.error(new NullPointerException(), "空指针异常");
//			
//			NsTestLogger._mqlog.info("发送队列,quene={}", "awk");
//			NsTestLogger._tasklog.info("短信扫描开始:{}", System.currentTimeMillis());
//			NsTestLogger._bizlog.info("账户写入数据,a={},b={}", "a", "b");
			
			_log.warn("输出自定义类别日志");
			fileLog.info("输出日志文件到文件 {}", "fadsdfasdf");
			try {
				Thread.sleep(30*1000); // 半分钟输出一次
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
		
		
	}
	
}
