package com.creditease.ns.controller.constants;

import com.creditease.ns.log.NsLog;

public class ControllerConstants {
	public static final String DEFAULT_SERVICEMESSAGE_KEY = "NS_CONTROLLER_SERVICE_MESSAGE_KEY";
	public static String DEFAULT_CONDITION_COND_CLASSNAME = "com.creditease.ns.controller.chain.command.CondParser";
	public static String CONTROLLER_CONTENTTYPE = "controller_contenttype_key";
	public static NsLog FRAME_LOG = NsLog.getFramLog("ControllerStart","ControllerStart");
	public static NsLog FLOW_LOG = NsLog.getFlowLog("ControllerFlow","ControllerFlow");
}
