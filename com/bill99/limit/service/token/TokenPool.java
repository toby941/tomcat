package com.bill99.limit.service.token;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.bill99.limit.service.token.task.TokenCallable;
import com.bill99.limit.service.token.task.TokenPoolExecutor;

/**
 * @author jun.bao
 * @since 2013年7月29日
 */
public class TokenPool implements TokenDispatch {

	private static Integer maxCapacity = 1000;

	/**
	 * 进行令牌分配的任务掉度集合，按优先级分类
	 */
	private Map<Integer, TokenPoolExecutor> executorMap;

	private Integer holdTimeout;

	private String poolName;

	private Integer[] priorityArry;

	/**
	 * 空闲token集合，按优先级分类
	 */
	private Map<Integer, LimitArrayBlockingQueue<Token>> queueMap;

	private Integer requestTimeout;

	/**
	 * 存放所有正在使用的token，非线程安全，近似结果
	 */
	private final Map<String, Token> tokenMap = new HashMap<String, Token>();

	public TokenPool() {
		super();
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

			if (count > 1) {
				Token token = null;
				try {
					token = otherTokenQueue.poll(1, TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (token != null) {
					System.out.println("dispatch priority" + pri + " to " + priority);
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
			TokenPoolExecutor executor = new TokenPoolExecutor(10, 10, requestTimeout, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(
					maxCapacity));
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

	/**
	 * 提交请求token任务
	 * 
	 * @param priority
	 * @return 一个支持异步结果的Future对象
	 */
	public Future<String> submit(Integer priority) {
		TokenCallable callable = new TokenCallable();
		callable.setPriority(priority);
		callable.setTokenPool(this);
		return executorMap.get(priority).submit(callable);
	}

	/**
	 * 获取tokenpool的运行快照,快照元素：各个优先级队列token使用占比，排队数量，各优先级token平均请求时间，持有时间
	 * TokenPoolExecutor相关指标： <br/>
	 * {@link ThreadPoolExecutor#getActiveCount()} 返回主动执行任务的近似线程数。<br/>
	 * {@link ThreadPoolExecutor#getCompletedTaskCount()} 返回已完成执行的近似任务总数。<br/>
	 * {@link ThreadPoolExecutor#getTaskCount()} 返回曾计划执行的近似任务总数。<br/>
	 * {@link ThreadPoolExecutor#getPoolSize()} 返回池中的当前线程数。<br/>
	 * {@link ThreadPoolExecutor#getQueue()} 返回此执行程序使用的任务队列。<br/>
	 * 
	 * 
	 * @return
	 */
	public String getSnapshot() {

		StringBuffer freeTokenSummarySB = new StringBuffer();
		for (Integer priority : queueMap.keySet()) {
			freeTokenSummarySB.append("priority: " + priority + " count: " + queueMap.get(priority).size() + ";");
		}

		StringBuffer executorSummarySB = new StringBuffer();
		for (Integer priority : executorMap.keySet()) {
			TokenPoolExecutor executor = executorMap.get(priority);
			String info = MessageFormat.format(
					"priority:{0},activecount:{1},completedtaskcount:{2},taskcount:{3},poolsize：{4},queuesize:{5}; ", executor
							.getActiveCount(), executor.getCompletedTaskCount(), executor.getTaskCount(), executor.getPoolSize(), executor
							.getQueue().size());
			executorSummarySB.append(info);
		}

		Integer inUseToken = tokenMap.size();

		return MessageFormat.format("free token summary [{0}],executor summary [{1}] ,in use token [{2}]", freeTokenSummarySB.toString(),
				executorSummarySB.toString(), inUseToken);
	}
}
