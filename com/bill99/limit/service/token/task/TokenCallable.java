package com.bill99.limit.service.token.task;

import java.util.concurrent.Callable;

import com.bill99.limit.service.token.TokenPool;

/**
 * @author jun.bao
 * @since 2013年7月31日
 */
public class TokenCallable implements Callable<String> {

	private Integer priority;
	private TokenPool tokenPool;

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public TokenCallable() {
		super();
	}

	public TokenCallable(int priority) {
		super();
		this.priority = priority;
	}

	public TokenPool getTokenPool() {
		return tokenPool;
	}

	public void setTokenPool(TokenPool tokenPool) {
		this.tokenPool = tokenPool;
	}

	@Override
	public String call() throws Exception {
		// System.err.println(MessageFormat.format("{0},priority:{1},time:{2}",
		// Thread.currentThread().getName(), priority, System.nanoTime()));
		// Thread.sleep(1000);
		return tokenPool.getToken(priority);
	}
}
