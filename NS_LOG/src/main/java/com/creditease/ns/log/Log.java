package com.creditease.ns.log;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;


/**
 * Date: 15-5-27
 * Time: 下午12:20
 */
public class Log {
    private static Logger processLog = LoggerFactory.getLogger("processLog");
    private static Logger errorLog = LoggerFactory.getLogger("errorLog");
    private static Logger infoLog = LoggerFactory.getLogger("infoLog");
    private static Logger debugLog = LoggerFactory.getLogger("debugLog");


    /**
     * 写入日志的主key
     *
     * @param primayKey 主key
     */
    public static void setPrimayKey(String primayKey) {
        MDC.put(LogConstants.LOG_PRIMARY_KEY, primayKey);
    }

    /**
     * 写入日志的副key
     *
     * @param subPrimaryKey 副key
     */
    public static void setSubPrimary(String subPrimaryKey) {
        MDC.put(LogConstants.LOG_SUB_PRIMARY_KEY, subPrimaryKey);
    }

    /**
     * 写入初始化的UUID
     */
    public static void setUniqKey() {
        MDC.put(LogConstants.UUID_KEY, LogUtils.getShortUUID());
    }

    /**
     * 写入模块名
     *
     * @param moduleName 模块名
     */
    public static void setModuleName(String moduleName) {
        MDC.put(LogConstants.MODULE_NAME, moduleName);
    }

    /**
     * 流程日志
     *
     * @param processName 流程名称
     * @param msg         流程信息
     */
    public static void logProcess(String processName, String msg) {
        processLog.info("[{}],msg:{}", processName, msg);
    }

    /**
     * 流程日志
     *
     * @param processName 流程名称
     * @param logCode     状态码
     * @param msg         流程信息
     */
    public static void logProcess(String processName, LogCode logCode, String msg) {
        processLog.info("[{}],状态码:{},状态信息:{},msg:{}", logCode.getCode(), logCode.getMsg());
    }

    /**
     * 流程日志
     *
     * @param processName 流程名称
     */
    public static void logProcess(String processName) {
        processLog.info("[{}]", processName);
    }

    /**
     * 错误日志输出到process file中
     *
     * @param msg       错误信息
     * @param throwable 异常
     */
    public static void logErrorToProcessFile(String msg, Throwable throwable) {
        processLog.error(msg, throwable);
    }

    /**
     * 错误日志输出到process file中
     *
     * @param msg 错误信息
     */
    public static void logErrorToProcessFile(String msg) {
        processLog.error(msg);
    }

    /**
     * 错误日志输出到process file中
     *
     * @param logCode 错误码
     */
    public static void logErrorToProcessFile(LogCode logCode) {
        processLog.error("错误码:{},错误信息:{}", logCode.getCode(), logCode.getMsg());
    }

    /**
     * 错误日志输出到process file中
     *
     * @param logCode 错误码
     * @param msg     错误信息
     */
    public static void logErrorToProcessFile(LogCode logCode, String msg) {
        processLog.error("错误码:{},错误信息:{},附加信息:{}", logCode.getCode(), logCode.getMsg(), msg);
    }


    /**
     * 错误日志输出到process file中
     *
     * @param logCode 错误码
     * @param msg     错误信息
     */
    public static void logErrorToProcessFile(LogCode logCode, String msg,Throwable throwable) {
        processLog.error("错误码:{},错误信息:{},附加信息:{}", logCode.getCode(), logCode.getMsg(), msg,throwable);
    }

    /**
     * 错误日志
     *
     * @param msg       错误信息
     * @param throwable 异常
     */
    public static void logError(String msg, Throwable throwable) {
        processLog.error(msg, throwable);
    }

    /**
     * 错误日志
     *
     * @param msg 错误信息
     */
    public static void logError(String msg) {
        errorLog.error(msg);
    }

    /**
     * 错误日志
     *
     * @param logCode 错误码
     */
    public static void logError(LogCode logCode) {
        errorLog.error("错误码:{},错误信息:{}", logCode.getCode(), logCode.getMsg());
    }

    /**
     * 错误日志
     *
     * @param logCode 错误码
     * @param msg     错误信息
     */
    public static void logError(LogCode logCode, String msg) {
        errorLog.error("错误码:{},错误信息:{},附加信息:{}", logCode.getCode(), logCode.getMsg(), msg);
    }

    /**
     * 错误日志
     *
     * @param logCode 错误码
     * @param msg     错误信息
     */
    public static void logError(LogCode logCode, String msg,Throwable throwable) {
        errorLog.error("错误码:{},错误信息:{},附加信息:{}", logCode.getCode(), logCode.getMsg(), msg,throwable);
    }

    /**
     * 信息日志
     * @param msg 信息
     */
    public static void logInformation(String msg){
        infoLog.info(msg);
    }

    /**
     * 信息日志
     * @param format 格式化模板
     * @param params 信息参数
     */
    public static void logInformation(String format,Object[] params){
        infoLog.info(format,params);
    }

    /**
     * 信息日志输出到process file中
     * @param msg 信息
     */
    public static void logInfoToProcessFile(String msg){
        processLog.info(msg);
    }

    /**
     * 信息日志输出到process file中
     * @param format 格式化模板
     * @param params 信息参数
     */
    public static void logInfoToProcessFile(String format,Object[] params){
        processLog.info(format,params);
    }

    /**
     * 调试信息
     * @param msg 信息
     */
    public static void logDebug(String msg){
        debugLog.debug(msg);
    }

    /**
     * 调试信息
     * @param format 格式化模板 需要被替换的为{}
     * @param params 信息参数
     */
    public static void logDebug(String format,Object[] params){
        debugLog.debug(format,params);
    }

    /**
     * 调试信息输出到process file中
     * @param msg 信息
     */
    public static void logDebugToProcessFile(String msg){
        processLog.debug(msg);
    }

    /**
     * 调试信息输出到process file中
     * @param format 格式化模板 需要被替换的为{}
     * @param params 信息参数
     */
    public static void logDebugToProcessFile(String format,Object[] params){
        processLog.debug(format,params);
    }




}
