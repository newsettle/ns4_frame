/*
 *  Copyright Beijing 58 Information Technology Co.,Ltd.
 *
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package com.creditease.framework.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;


public class ExceptionUtil {
	/**
	 * 将异常信息转换为字符串
	 * @param e
	 * @return
	 */
	public static String getStackTrace(Throwable e) {
		String stackTrace = "";
		Writer writer = null;
		PrintWriter printWriter = null;
		try {
			writer = new StringWriter();
			printWriter = new PrintWriter(writer);
			e.printStackTrace(printWriter);
			stackTrace = writer.toString();
		} catch(Exception ex) {
			ex.printStackTrace();
		} finally {
			if(printWriter != null) {
				try {
					printWriter.close();
				} catch(Exception ex) {
					ex.printStackTrace();
				}
			}
			
			if(writer != null) {
				try {
					writer.close();
				} catch(Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		
		return stackTrace;
	}
}
