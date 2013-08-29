package com.bill99.limit.service.token.task;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 异步提交报警信息的线程调度类
 * 
 * @author jun.bao
 * @since 2013年8月29日
 */
public class AlarmPoolExecutor extends ThreadPoolExecutor {

	private static final Integer coreSize = 2;
	private static final Integer maxSize = 10;
	private static Long keepTime = 10L;
	private static Integer workQueueSize = 1000;

	public AlarmPoolExecutor() {
		super(coreSize, maxSize, keepTime, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(workQueueSize), new LogAlarmPolicy());
	}

	/**
	 * 当task无法执行时将信息记录本地日志
	 * 
	 * @author jun.bao
	 * 
	 */
	public static class LogAlarmPolicy implements RejectedExecutionHandler {
		protected static org.apache.juli.logging.Log log = org.apache.juli.logging.LogFactory.getLog(LogAlarmPolicy.class);

		public LogAlarmPolicy() {
		}

		public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
			AlertCallable alertCallable = (AlertCallable) r;
			Map<String, String> alarmInfo = alertCallable.getPostMap();
			StringBuffer sb = new StringBuffer();
			for (String key : alarmInfo.keySet()) {
				sb.append(key + ": " + alarmInfo.get(key) + "&");
			}
			log.error("post alarm task rejected,alarm info:" + sb.toString());
		}
	}

}
