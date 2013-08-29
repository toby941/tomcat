package com.bill99.limit.service.token;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.bill99.limit.service.token.task.TokenCallable;
import com.bill99.limit.service.token.task.TokenPoolExecutor;

/**
 * @author jun.bao
 * @since 2013年7月29日
 */
public class TokenPool implements TokenDispatch {

	private static Integer maxCapacity = 1000;

	private Map<Integer, TokenPoolExecutor> executorMap;

	private Integer holdTimeout;

	private String poolName;

	private Integer[] priorityArry;

	private Map<Integer, LimitArrayBlockingQueue<Token>> queueMap;

	private Integer requestTimeout;

	private final Map<String, Token> tokenMap = new HashMap<String, Token>();

	public TokenPool() {
	}

	@Override
	public void dispatch(Integer priority) {
		if (queueMap == null || queueMap.isEmpty()) {
			return;
		}
		LimitArrayBlockingQueue<Token> needTokenQueue = queueMap.get(priority);
		for (Integer pri : priorityArry) {
			if (pri.equals(priority)) {
				continue;
			}
			LimitArrayBlockingQueue<Token> otherTokenQueue = queueMap.get(pri);

			int count = otherTokenQueue.size();

			if (count > 5) {
				Token token = null;
				try {
					token = otherTokenQueue.poll(1, TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (token != null) {
					needTokenQueue.offer(token);
					return;
				}
			}
		}

	}

	public TokenPoolExecutor getExecutor(Integer priority) {
		return executorMap.get(priority);
	}

	public Map<Integer, TokenPoolExecutor> getExecutorMap() {
		return executorMap;
	}

	public Integer getHoldTimeout() {
		return holdTimeout;
	}

	public String getPoolName() {
		return poolName;
	}

	public Integer[] getPriorityArry() {
		return priorityArry;
	}

	public BlockingQueue<Token> getQueue(Integer priority) {
		return queueMap.get(priority);
	}

	public Map<Integer, LimitArrayBlockingQueue<Token>> getQueueMap() {
		return queueMap;
	}

	public Integer getRequestTimeout() {
		return requestTimeout;
	}

	public String getToken(Integer priority) {
		try {
			BlockingQueue<Token> queue = getQueue(priority);
			Token token = queue.poll(requestTimeout, TimeUnit.SECONDS);
			if (token != null) {
				token.setLastAssessTime(Calendar.getInstance().getTime());
				tokenMap.put(token.getName(), token);
				return token.getName();
			}
		} catch (InterruptedException e) {
			return null;
		}
		return null;
	}

	public Map<String, Token> getTokenMap() {
		return tokenMap;
	}

	public void init() {
		queueMap = new HashMap<Integer, LimitArrayBlockingQueue<Token>>();
		executorMap = new HashMap<Integer, TokenPoolExecutor>();
		priorityArry = new Integer[] { 1, 2, 3 };
		for (Integer index : priorityArry) {

			int tokenCount = index * 5;
			if (index.equals(3)) {
				tokenCount = 9999;
			} else {
				tokenCount = 99991;
			}
			LimitArrayBlockingQueue<Token> queue = new LimitArrayBlockingQueue<Token>(tokenCount, true);
			for (int i = 0; i < tokenCount; i++) {
				Token token = new Token(UUID.randomUUID().toString(), index);
				queue.add(token);
			}
			queue.setDispatch(this);
			queue.setPriority(index);
			queueMap.put(index, queue);
			TokenPoolExecutor executor = new TokenPoolExecutor(10, 10, 18, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
			executorMap.put(index, executor);
		}
	}

	/**
	 * 
	 * @param queueConfigs
	 */
	public void init(List<TokenQueueConfig> queueConfigs, Integer reqTo, Integer holdTo) {

		queueMap = new HashMap<Integer, LimitArrayBlockingQueue<Token>>();
		executorMap = new HashMap<Integer, TokenPoolExecutor>();
		priorityArry = new Integer[queueConfigs.size()];
		requestTimeout = reqTo;
		holdTimeout = holdTo;
		for (int i = 0; i < queueConfigs.size(); i++) {
			TokenQueueConfig config = queueConfigs.get(i);
			Integer tokenCount = config.getTokenCount();
			Integer priority = config.getPriority();
			LimitArrayBlockingQueue<Token> queue = new LimitArrayBlockingQueue<Token>(tokenCount, true);
			for (int j = 0; j < tokenCount; j++) {
				Token token = new Token(UUID.randomUUID().toString(), priority);
				queue.add(token);
			}
			queue.setDispatch(this);
			queue.setPriority(priority);
			queueMap.put(priority, queue);
			TokenPoolExecutor executor = new TokenPoolExecutor(10, 10, requestTimeout, TimeUnit.SECONDS,
					new LinkedBlockingQueue<Runnable>());
			executorMap.put(priority, executor);
			priorityArry[i] = priority;
		}

		// 对优先级按从低到高排序，token调度从低优先级开始(priority越小优先级越高)
		Arrays.sort(priorityArry, new Comparator<Integer>() {
			@Override
			public int compare(Integer a, Integer b) {
				return -(a.compareTo(b));
			}
		});
	}

	public void printQueue(Integer priority) {
		BlockingQueue<Token> queue = queueMap.get(priority);
		System.err.print("priority:" + priority);
		System.err.print(" size:" + queue.size());
		System.err.print("\r\n");

	}

	public boolean releaseToken(String uuid) {
		if (uuid != null) {
			Token token = tokenMap.get(uuid);

			if (token != null) {
				token.reset();
				Integer priority = token.getPriority();
				tokenMap.remove(uuid);
				BlockingQueue<Token> queue = getQueue(priority);
				return queue.offer(token);
			}
		}
		return false;
	}

	public void setExecutorMap(Map<Integer, TokenPoolExecutor> executorMap) {
		this.executorMap = executorMap;
	}

	public void setHoldTimeout(Integer holdTimeout) {
		this.holdTimeout = holdTimeout;
	}

	public void setPoolName(String poolName) {
		this.poolName = poolName;
	}

	public void setPriorityArry(Integer[] priorityArry) {
		this.priorityArry = priorityArry;
	}

	public void setQueueMap(Map<Integer, LimitArrayBlockingQueue<Token>> queueMap) {
		this.queueMap = queueMap;
	}

	public void setRequestTimeout(Integer requestTimeout) {
		this.requestTimeout = requestTimeout;
	}

	public Future<String> submit(Integer priority) {
		TokenCallable callable = new TokenCallable();
		callable.setPriority(priority);
		callable.setTokenPool(this);
		return executorMap.get(priority).submit(callable);
	}

}
