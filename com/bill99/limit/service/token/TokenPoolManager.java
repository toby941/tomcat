package com.bill99.limit.service.token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.catalina.connector.Request;

import com.bill99.limit.service.token.priority.PriorityManager;
import com.bill99.limit.service.token.task.AlarmPoolExecutor;
import com.bill99.limit.service.token.task.AlertCallable;

/**
 * @author jun.bao
 * @since 2013年8月8日
 */
public class TokenPoolManager {

	public static final ThreadLocal<String> userThreadLocal = new ThreadLocal<String>();

	private Map<String, TokenPool> tokenPoolMap;

	private static TokenPoolManager manager;
	private PriorityManager priorityManager;
	private AlarmPoolExecutor alarmPoolExecutor;

	public static TokenPoolManager getTokenPoolManager() {
		if (manager == null) {
			manager = new TokenPoolManager();
			manager.init();
		}
		return manager;
	}

	private static Integer defaultRequestTimeout = 25;
	private static Integer defaultHoldTimeout = 5;

	private TokenPool getPool(String name) {
		return tokenPoolMap.get(name);
	}

	private TokenPoolManager() {
		super();
	}

	public void loadConfig() {

	}

	public void init() {
		loadConfig();
		List<TokenQueueConfig> queueConfigs = new ArrayList<TokenQueueConfig>();
		for (int i = 0; i < 5; i++) {
			TokenQueueConfig config = new TokenQueueConfig();
			config.setPriority(i);
			config.setTokenCount((i + 1) * 3);
			queueConfigs.add(config);
		}
		TokenPool pool = new TokenPool();
		pool.init(queueConfigs, defaultRequestTimeout, defaultHoldTimeout);
		tokenPoolMap = new HashMap<String, TokenPool>();
		tokenPoolMap.put("/demo/second.do", pool);
		priorityManager = PriorityManager.getPriorityManager();
		alarmPoolExecutor = new AlarmPoolExecutor();
	}

	public boolean releaseToken(String name) {
		TokenPool pool = getPool(name);
		// 对于没有限流策略的请求，因为没有token，此处直接返回true
		if (pool != null) {
			return pool.releaseToken(userThreadLocal.get());
		}
		return true;

	}

	/**
	 * 根据 {@link Request#getRequestURI()}确定对应 {@link TokenPool}与对应的优先级参数解析方式
	 * ws请求：ContextPath: null, ContentType: text/xml; charset=utf-8, PathInfo:
	 * null,Protocol: HTTP/1.0, QueryString:null, RequestURI:
	 * /demo/apipay/services/BatchPayWS, ServletPath: null
	 * 
	 * http请求：ContextPath: null, ContentType: null, PathInfo: null,Protocol:
	 * HTTP/1.1, QueryString:null, RequestURI: /demo/second.do, ServletPath:
	 * null
	 * 
	 * @param request
	 * @return true-获取令牌成功可执行业务请求 false-令牌获取失败，业务请求执行中止 if priority==null-
	 *         true请求不限流
	 */
	public Boolean getToken(Request request) {
		String requestURI = request.getRequestURI();
		Integer priority = priorityManager.getPriority(requestURI, request);
		if (priority == null) {
			System.out.println("no priority return true");
			return true;
		}
		return getToken(requestURI, priority);
	}

	private boolean getToken(String requestURI, int priority) {
		TokenPool pool = getPool(requestURI);
		// requestURI无限流情况下，请求放行
		if (pool == null) {
			System.out.println("no pool with " + requestURI);
			return true;
		}
		Future<String> future = pool.submit(priority);
		try {
			String result = future.get(defaultRequestTimeout, TimeUnit.SECONDS);
			if (result != null && result.length() > 0) {
				userThreadLocal.set(result);
				return true;
			}
			return false;
		} catch (Exception e) {
			sendGetTokenTimeOutAlert(requestURI, priority, e);
			e.printStackTrace();
			return false;
		}

	}

	private Map<String, String> getAlarmInfo(String requestURI, int priority) {
		Map<String, String> map = new HashMap<String, String>();
		TokenPool pool = getPool(requestURI);
		map.put("name", requestURI);
		map.put("priority", String.valueOf(priority));
		map.put("size", String.valueOf(pool.getQueue(priority).size()));
		return map;
	}

	/**
	 * 异步上传报警信息
	 * 
	 * @param requestURI
	 * @param priority
	 * @param exception
	 */
	public void sendGetTokenTimeOutAlert(String requestURI, int priority, Exception exception) {
		Map<String, String> map = getAlarmInfo(requestURI, priority);
		map.put("error", exception.getMessage());
		map.put("threadName", Thread.currentThread().getName());
		AlertCallable callable = new AlertCallable(map);
		alarmPoolExecutor.submit(callable);
	}
}
