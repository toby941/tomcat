package com.bill99.limit.service.token.task;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author jun.bao
 * @since 2013年7月31日
 */
public class TokenPoolExecutor extends ThreadPoolExecutor {

	public TokenPoolExecutor(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
	}

	@Override
	protected RunnableFuture<String> newTaskFor(Callable c) {
		TokenCallable tokenCallable = (TokenCallable) c;
		return new TokenFutureTask(tokenCallable);
	}

	public static void main(String[] args) throws InterruptedException,
			ExecutionException {
		BlockingQueue<Runnable> queue = new PriorityBlockingQueue<Runnable>(10,
				new Comparator<Runnable>() {

					@Override
					public int compare(Runnable o1, Runnable o2) {
						TokenFutureTask task1 = (TokenFutureTask) o1;
						TokenFutureTask task2 = (TokenFutureTask) o2;
						return task1.getPriority().compareTo(
								task2.getPriority());
					}
				});
		TokenPoolExecutor executor = new TokenPoolExecutor(5, 5, 5000,
				TimeUnit.MILLISECONDS, queue);
		List<Future<String>> list = new ArrayList<Future<String>>();
		for (int i = 0; i < 10; i++) {
			TokenCallable callable = new TokenCallable();
			Future<String> future = executor.submit(callable);
			list.add(future);
		}
		System.out.println("wait result*******************");
		for (Future<String> f : list) {
			System.out.println(f.get());
		}
		// executor.shutdown();
	}
}
