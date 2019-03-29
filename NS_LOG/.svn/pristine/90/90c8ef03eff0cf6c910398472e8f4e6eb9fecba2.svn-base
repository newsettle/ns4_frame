package com.creditease.ns.log.converter;

import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

import com.creditease.ns.log.LogConstants;
import com.creditease.ns.log.LogKey;

/**
 * @ClassName: NSMessageContentConverter
 * @Description: 参考NSMessageConverter,在消息中增加了模块名和描述
 * @author dingzhiwei
 * @date 2015年11月6日 下午3:15:08
 */
public class NSMessageContentConverter extends MessageConverter {
	@Override
	public String convert(ILoggingEvent event) {
		String message = super.convert(event);
		if (message == null) {
			return "";
		}

		boolean endWithEnter = message.endsWith(LogConstants.LINUX_EOL);
		String[] stringLines = message.split(LogConstants.WIN_LINUX_EOL_REG);

		if (stringLines == null) {
			return message;
		}

		StringBuilder sb = new StringBuilder();
		String module = "";
		// 从logger的名字中得到模块名和描述
		if (event.getLoggerName().indexOf(LogConstants.SPLIT_CATEGORY) != -1)
			module = event.getLoggerName().substring(
					event.getLoggerName().indexOf(LogConstants.SPLIT_CATEGORY)
							+ LogConstants.SPLIT_CATEGORY.length());

		for (int i = 0; i < stringLines.length; i++) {
			sb.append(LogKey.getTotalKey());
			if (i == 0){
				if(module != null && !"".equals(module)) sb.append(" ");
				sb.append(module);
			}
			sb.append(LogConstants.PART_SPLIT).append(stringLines[i]);
			if (i != stringLines.length - 1) {
				sb.append(LogConstants.WIN_EOL);
			} else {
				if (endWithEnter) {
					sb.append(LogConstants.WIN_EOL);
				}
			}
		}
		return sb.toString();

	}
}
