package com.bill99.limit.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author jun.bao
 * @since 2013年9月10日
 */
public class ExceptionUtils {

	/**
	 * 将异常堆栈合并为一个字符串
	 * 
	 * @param throwable
	 * @return
	 */
	public static String getStackTrace(Throwable throwable) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		throwable.printStackTrace(pw);
		return sw.getBuffer().toString();
	}

}