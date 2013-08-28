package com.bill99.limit.service.token.task;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * @author jun.bao
 * @since 2013年7月31日
 */
public class TokenFutureTask extends FutureTask<String> {

	private Integer priority;

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public TokenFutureTask(Callable<String> callable) {
		super(callable);
		TokenCallable tokenCallable = (TokenCallable) callable;
		priority = tokenCallable.getPriority();
	}

}
