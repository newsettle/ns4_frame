package com.creditease.ns.transporter.util;

import java.util.Random;
import java.util.zip.CRC32;

import com.creditease.framework.util.ProtoStuffSerializeUtil;
import com.creditease.ns.mq.model.Header;
import com.creditease.ns.mq.model.Message;

public class BinLogUtil {
	
	/**
	 * 文件的一个条目
	 * tbbin
	 * version
	 * 
	 * crc headercode
	 * length of header
	 * header data
	 * length of body
	 * data of body
	 * ef
	 * 
	 * @param message
	 * @throws Exception
	 */
	public static void binlogWrite(Message message) throws Exception
	{
		String identify = "tpbin";
		byte version = 1;
		long crcheadercode = 0l;
		int lengthOfBody = 0;
		int dataoffset=0;
		int lengthOfHeader = 0;
		int lengthOfData = 0;
		
		byte[] body = message.getBody();
		if (body != null) 
		{
			lengthOfBody = body.length;
		}
		
		Header header = message.getHeader();
		byte[] headerbytes = ProtoStuffSerializeUtil.serializeForCommon(header);
		lengthOfHeader = headerbytes.length;
		
		CRC32 crc32 = new java.util.zip.CRC32();
		crc32.update(headerbytes);
		crcheadercode = crc32.getValue();
		
		
		
	}

}
