package com.bill99.limit.service.token.priority;

import javax.servlet.http.HttpServletRequest;

/**
 * 获取业务优先级接口
 * 
 * @author jun.bao
 * @since 2013年8月29日
 */
public interface PriorityPloy {
	public static String contentTypeXML = "xml";

	/**
	 * 解析request请求参数 获取优先级
	 * 
	 * @param request
	 * @return
	 */
	public Integer getPriority(HttpServletRequest request);

}
