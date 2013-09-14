package com.bill99.limit.service.token;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import com.bill99.limit.domain.ContextConfig;
import com.bill99.limit.domain.TokenPoolConfig;
import com.bill99.limit.domain.TokenQueueConfig;
import com.bill99.limit.service.token.priority.PriorityManager;
import com.bill99.limit.service.token.task.HttpCallable;
import com.bill99.limit.service.token.task.HttpRequestPoolExecutor;
import com.bill99.limit.util.Config;
import com.bill99.limit.util.JAXBUtil;
import com.bill99.limit.util.JDKHttpClient;

/**
 * @author jun.bao
 * @since 2013年8月8日
 */
public class TokenPoolManager {

	protected static org.apache.juli.logging.Log log = org.apache.juli.logging.LogFactory
			.getLog(TokenPoolManager.class);

	public static final ThreadLocal<String> userThreadLocal = new ThreadLocal<String>();

	private Map<String, TokenPool> tokenPoolMap;
	private ContextConfig contextConfig;

	private static TokenPoolManager manager;
	private PriorityManager priorityManager;
	// 两个HttpRequestPoolExecutor分别作为警告信息与快照信息的上传通道(异步上传)，相互之间独立，避免影响
	private HttpRequestPoolExecutor alarmPoolExecutor;
	private HttpRequestPoolExecutor snapshotPoolExecutor;

	public static TokenPoolManager getTokenPoolManager() {
		if (manager == null) {
			manager = new TokenPoolManager();
		}
		return manager;
	}

	private static Integer defaultRequestTimeout = 25;
	private static Integer defaultHoldTimeout = 5;

	/**
	 * 获取token池
	 * 
	 * @param name
	 *            token对应key
	 * @return
	 */
	private TokenPool getPool(String name) {
		if (tokenPoolMap == null) {
			return null;
		}
		return tokenPoolMap.get(name);
	}

	private TokenPoolManager() {
		super();
	}

	private void loadConfig(String contextName) {

		try {
			String getConfigUrl = Config.getConfigUrl(contextName);
			String configXML = JDKHttpClient.doGet(getConfigUrl);
			if (configXML != null && configXML.length() > 0) {
				this.contextConfig = (ContextConfig) JAXBUtil.xml2Bean(
						ContextConfig.class, configXML);
			}
		} catch (Exception e) {
			log.error("loadConfig with context :" + contextName, e);
		}

	}

	public void init(String contextName) {
		loadConfig(contextName);
		if (contextConfig == null) {
			return;
		}

		List<TokenPoolConfig> tokenPoolConfigs = contextConfig
				.getTokenPoolConfigs();
		tokenPoolMap = new HashMap<String, TokenPool>();
		priorityManager = PriorityManager.getPriorityManager();
		if (tokenPoolConfigs != null && tokenPoolConfigs.size() > 0) {
			for (TokenPoolConfig tokenPoolConfig : tokenPoolConfigs) {
				List<TokenQueueConfig> queueConfigs = tokenPoolConfig
						.getTokenQueueConfigs();
				ClientDeployTokenPool pool = new ClientDeployTokenPool();
				pool.init(queueConfigs, tokenPoolConfig.getRequestTimeout(),
						tokenPoolConfig.getHoldTimeout());
				tokenPoolMap.put(tokenPoolConfig.getRequestUrl(), pool);
			}
			priorityManager.init(tokenPoolConfigs);
		}

		alarmPoolExecutor = new HttpRequestPoolExecutor();
		snapshotPoolExecutor = new HttpRequestPoolExecutor();
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
	 * 根据 {@link Request#getRequestURI()}确定对应 {@link ClientDeployTokenPool}
	 * 与对应的优先级参数解析方式 ws请求：ContextPath: null, ContentType: text/xml;
	 * charset=utf-8, PathInfo: null,Protocol: HTTP/1.0, QueryString:null,
	 * RequestURI: /demo/apipay/services/BatchPayWS, ServletPath: null
	 * http请求：ContextPath: null, ContentType: null, PathInfo: null,Protocol:
	 * HTTP/1.1, QueryString:null, RequestURI: /demo/second.do, ServletPath:
	 * null
	 * 
	 * @param request
	 * @return true-获取令牌成功可执行业务请求 false-令牌获取失败，业务请求执行中止 if priority==null-
	 *         true请求不限流
	 */
	public boolean getToken(HttpServletRequest request) {
		String requestURI = request.getRequestURI();
		TokenPool pool = getPool(requestURI);
		// requestURI无限流情况下，请求放行
		if (pool == null) {
			log.warn("no pool with " + requestURI);
			return true;
		}

		Integer priority = null;
		if (priorityManager == null) {
			// requestURI无优先级配置策略，请求按默认优先级处理
			log.warn("no priorityManager exist,client token request always return true");
			priority = Integer.MAX_VALUE;
		} else {
			priority = priorityManager.getPriority(request);
		}

		Future<String> future = pool.submit(priority);
		try {
			String result = future.get(defaultRequestTimeout, TimeUnit.SECONDS);
			if (result != null && result.length() > 0) {
				userThreadLocal.set(result);
				return true;
			}
			// 没有获取到token，资源紧张需通知服务器
			sendGetTokenTimeOutAlert(requestURI, priority, null);
			return false;
		} catch (Exception e) {
			sendGetTokenTimeOutAlert(requestURI, priority, e);
			log.error("get token time out", e);
			return false;
		}
	}

	private Map<String, String> getAlarmInfo(String requestURI, int priority) {
		Map<String, String> map = new HashMap<String, String>();
		TokenPool pool = getPool(requestURI);
		map.put("pn", requestURI);
		map.put("p", String.valueOf(priority));
		map.put("t", Calendar.getInstance().getTime().toString());
		// map.put("size", String.valueOf(pool.getQueue(priority).size()));
		return map;
	}

	/**
	 * 异步上传报警信息
	 * 
	 * @param requestURI
	 * @param priority
	 * @param exception
	 */
	public void sendGetTokenTimeOutAlert(String requestURI, int priority,
			Exception exception) {
		Map<String, String> map = getAlarmInfo(requestURI, priority);
		if (exception != null) {
			map.put("i", com.bill99.limit.util.ExceptionUtils
					.getStackTrace(exception));
		}
		map.put("threadName", Thread.currentThread().getName());
		HttpCallable callable = new HttpCallable(Config.getAlarmUrl(), map,
				true);
		alarmPoolExecutor.submit(callable);
	}

	/**
	 * 发送token池快照信息
	 */
	public void sendSnapshot() {
		StringBuffer snapshot = new StringBuffer();

		for (String key : tokenPoolMap.keySet()) {
			TokenPool pool = tokenPoolMap.get(key);
			String summary = pool.getSnapshot();
			snapshot.append(MessageFormat.format("name:{0},summary:{1} ", key,
					summary));
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("snapshot", snapshot.toString());
		HttpCallable callable = new HttpCallable(Config.getSnapshotUrl(), map,
				true);
		snapshotPoolExecutor.submit(callable);

		System.out.println(Calendar.getInstance().getTime() + "send Snapshot");
	}

	@Override
	public String toString() {

		StringBuffer tokenPoolMapSb = new StringBuffer();
		if (tokenPoolMap != null) {
			for (String key : tokenPoolMap.keySet()) {
				tokenPoolMapSb.append("(" + key + ": " + tokenPoolMap.get(key));
			}
			tokenPoolMapSb.append(" )");
		}

		return "TokenPoolManager [tokenPoolMap=" + tokenPoolMapSb.toString()
				+ ", contextConfig=" + contextConfig + ", priorityManager="
				+ priorityManager + ", alarmPoolExecutor=" + alarmPoolExecutor
				+ ", snapshotPoolExecutor=" + snapshotPoolExecutor + "]";
	}

}
