package com.bill99.limit.service.token.priority;

import org.apache.catalina.connector.Request;

/**
 * @author jun.bao
 * @since  2013年9月4日 
 */
/**
 * 限流优先级获取统一入口
 * 
 * @author jun.bao
 * @since 2013年8月29日
 */
public class PriorityManager {

	private static PriorityManager manager;

	private PriorityPloy priorityPloy;

	public static PriorityManager getPriorityManager() {
		if (manager == null) {
			manager = new PriorityManager();
			manager.init();
		}
		return manager;
	}

	public void init() {
		priorityPloy = new DefaultPriorityPloy();
	}

	private PriorityManager() {
		super();
	}

	public Integer getPriority(String requestURI, Request request) {
		return priorityPloy.getPriority(request);
	}

}