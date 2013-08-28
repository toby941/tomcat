package com.bill99.limit.service.token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author jun.bao
 * @since 2013年8月8日
 */
public class TokenPoolManager {

	public static final ThreadLocal<String> userThreadLocal = new ThreadLocal<String>();

	private Map<String, TokenPool> tokenPoolMap;

	private static TokenPoolManager manager;

	public static TokenPoolManager getTokenPoolManager() {
		if (manager == null) {
			manager = new TokenPoolManager();
			manager.init();
		}
		return manager;
	}

	private static Integer defaultRequestTimeout = 5;
	private static Integer defaultHoldTimeout = 5;

	private TokenPool getPool(String name) {
		return tokenPoolMap.get(name);
	}

	private TokenPoolManager() {
		super();
	}

	public void init() {

		List<TokenQueueConfig> queueConfigs = new ArrayList<TokenQueueConfig>();
		for (int i = 0; i < 5; i++) {
			TokenQueueConfig config = new TokenQueueConfig();
			config.setPriority(i);
			config.setTokenCount((i + 1) * 10);
		}
		TokenPool pool = new TokenPool();
		pool.init(queueConfigs, defaultRequestTimeout, defaultHoldTimeout);
		tokenPoolMap = new HashMap<String, TokenPool>();
		tokenPoolMap.put("pool", pool);
	}

	public boolean releaseToken(String name) {
		TokenPool pool = getPool(name);
		return pool.releaseToken(userThreadLocal.get());
	}

	public boolean getToken(String poolName, int priority) {
		TokenPool pool = getPool(poolName);
		if (pool == null) {
			return true;
		}
		Future<String> future = pool.submit(priority);
		String threadName = Thread.currentThread().getName();
		try {
			System.out.println("begin thread: " + threadName);
			String result = future.get(28, TimeUnit.SECONDS);
			if (result != null && result.length() > 0) {
				userThreadLocal.set(result);
				return true;
			}
			return false;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		} catch (ExecutionException e) {
			e.printStackTrace();
			return false;
		} catch (TimeoutException e) {
			System.out.println("timeout thread: " + threadName);
			e.printStackTrace();
			return false;
		}
	}
}
