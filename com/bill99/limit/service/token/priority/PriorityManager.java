package com.bill99.limit.service.token.priority;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.bill99.limit.domain.TokenPoolConfig;

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

	private Map<String, PriorityPloy> priorityMap;

	public static PriorityManager getPriorityManager() {
		if (manager == null) {
			manager = new PriorityManager();
		}
		return manager;
	}

	public void init(List<TokenPoolConfig> configs) {
		for (TokenPoolConfig config : configs) {
			PriorityPloy priorityPloy = new KeyValuePriorityPloy(config);
			priorityMap.put(config.getRequestUrl(), priorityPloy);
		}
	}

	private PriorityManager() {
		super();
		priorityMap = new HashMap<String, PriorityPloy>();
	}

	public Integer getPriority(HttpServletRequest request) {
		String requestURL = request.getRequestURI();
		PriorityPloy priorityPloy = priorityMap.get(requestURL);
		if (priorityPloy != null) {
			return priorityPloy.getPriority(request);
		}
		return Integer.MAX_VALUE;
	}

}