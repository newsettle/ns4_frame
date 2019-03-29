package com.creditease.ns.log;

/**
 * Date: 15-5-27
 * Time: 下午12:24
 */
public class LogConstants {
    public static final String LOG_PRIMARY_KEY = "primaryKey";
    public static final String LOG_SUB_PRIMARY_KEY = "subprimaryKey";
    public static final String NONE_PRIMARY_KEY = "noneprimaryKey";
    public static final String NONE_UUID = "+";
    public static final String UUID_KEY = "uuid";
    public static final String MODULE_NAME = "moduleName";
    public static final String WIN_EOL = "\r\n";
    public static final String LINUX_EOL = "\n";
    public static final String WIN_LINUX_EOL_REG = "\r\n|\n|\r";
    public static final String PART_SPLIT = " - ";
    public static final String AUTOCONFIG_FILE = "logback.xml";
    public static final String DEFAULT_LOG_DIR = "/log/";
    public static final String MDC_PREFIX_KEY = "prefixcontent";
    
    public static final String SPLIT_CATEGORY = ".>";
    public static final String CONNECT_CATEGORY = ">";
    public static final String SPLIT_MODULE = "|";
    public static final String CATEGORY_NS_FRAM = "ns.fram";
    public static final String CATEGORY_NS_FLOW = "ns.flow";
    public static final String CATEGORY_NS_MQ = "ns.mq";
    public static final String CATEGORY_NS_TASK = "ns.task";
    public static final String CATEGORY_NS_BIZ = "ns.biz";
    
	//txnId
    public static final String MDC_KEY_TXNID = "txnId";
	//orderId
    public static final String MDC_KEY_ORDERID = "orderId";
	//系统单号
    public static final String MDC_KEY_SYSTEMORDERID = "systemOrderId";
	//批次号
    public static final String MDC_KEY_BATCHID = "batchId";
	//交易状态
    public static final String MDC_KEY_TRADESTATUS = "tradeStatus";
	//响应码
    public static final String MDC_KEY_RESPONSECODE = "responseCode";
  //响应码描述
    public static final String MDC_KEY_RESPONSECODEMESS = "responseCodeMess";
	//商户号
    public static final String MDC_KEY_MERCHANTID = "merchantId";
	//通道商户号
    public static final String MDC_KEY_CHANNELMERCHANTID = "channelMerchantId";
	//通道名称
    public static final String MDC_KEY_CHANNELNAME = "channelName";
	//支付类型
    public static final String MDC_KEY_PAYTYPE = "payType";
    //业务类型 比如说是批量 还是单笔交易
    public static final String MDC_KEY_TXNTYPE = "txnType";
    //更新时间
    public static final String MDC_KEY_UPDATETIME = "updateTime";
    //备注
    public static final String MDC_KEY_REMARK = "remark";
    //错误描述
    public static final String MDC_KEY_ERRORDESP= "errorDesp";
    //校验状态
    public static final String MDC_KEY_CHKSTATUS= "chkSts";
    
    public static final String MDC_KEY_OPERATORID = "operatorId";
	
    
    //第三方响应码
    public static final String MDC_KEY_THIRDHEADCODE = "thirdHeadCode";
    //第三方响应头信息
    public static final String MDC_KEY_THIRDHEADMSG = "thirdHeadMsg";
    //第三方响应体信息
    public static final String MDC_KEY_THIRDBODYCODE = "thirdBodyCode";
    
    public static final String MDC_KEY_THIRDBODYMSG = "thirdBodyMsg";
    
    public static final String MDC_KEY_CHANID = "chanId";
    public static final String MDC_KEY_LOGFILENAME = "logFileName";
    public static final String MDC_KEY_STTLAMT = "sttlAmt";
    public static final String MDC_KEY_ACTUALSTTLAMT = "actualSttlAmt";
    public static final String MDC_KEY_SPLITFLG = "splitFlg";
    public static final String MDC_KEY_PROCSTS = "procSts";
    public static final String MDC_KEY_NOTIFYURL = "notifyUrl";
    public static final String MDC_KEY_OUTTXNID = "outTxnId";
    public static final String MDC_KEY_EXTXNTD = "exTxnTd";
    public static final String MDC_KEY_CAPTURETIME = "captureTime";
    public static final String MDC_KEY_EXETIME = "exeTime";
    public static final String MDC_KEY_TRACETIME = "traceTime";
    public static final String MDC_KEY_RESERVETM = "reserveTm";
    public static final String MDC_KEY_NSACCSTTLDT = "nsAccSttlDt";
    public static final String MDC_KEY_NSSTTLDT = "nsSttlDt";
    public static final String MDC_KEY_POSTINGSTS = "postingSts";
    public static final String MDC_KEY_CARDTP = "cardTp";
    public static final String MDC_KEY_DBTRISSRCD = "dbtrIssrCd";
    public static final String MDC_KEY_DBTRIDTP = "dbtrIdTp";
    public static final String MDC_KEY_DBTRIDNUMBER = "dbtrIdNumber";
    public static final String MDC_KEY_DBTRACCTID = "dbtrAcctId";
    public static final String MDC_KEY_DBTRNM = "dbtrNm";
    public static final String MDC_KEY_DBTRCONTACTNO = "dbtrContactno";
    public static final String MDC_KEY_CDTRISSRCD = "cdtrIssrCd";
    public static final String MDC_KEY_CDTRIDTP = "cdtrIdTp";
    public static final String MDC_KEY_CDTRIDNUMBER = "cdtrIdNumber";
    public static final String MDC_KEY_CDTRACCTID = "cdtrAcctId";
    public static final String MDC_KEY_CDTRNM = "cdtrNm";
    public static final String MDC_KEY_CDTRCONTACTNO = "cdtrContactno";
    public static final String MDC_KEY_CHANNLEMSG = "channleMsg";
    public static final String MDC_KEY_NOTICEMSG = "noticeMsg";
    public static final String MDC_KEY_VERSIONNO = "versionNo";
    public static final String MDC_KEY_EXD4 = "exd4";
    public static final String MDC_KEY_EXD5 = "exd5";
    public static final String MDC_KEY_EXD6 = "exd6";
    public static final String MDC_KEY_EXD7 = "exd7";
    public static final String MDC_KEY_EXD8 = "exd8";
    public static final String MDC_KEY_EXD9 = "exd9";
    
    
    
}
