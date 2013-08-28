package com.bill99.limit.service.token;

/**
 * @author jun.bao
 * @since 2013年8月28日
 */
public class TokenQueueConfig {

	public Integer getTokenCount() {
		return tokenCount;
	}

	public void setTokenCount(Integer tokenCount) {
		this.tokenCount = tokenCount;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public Integer getRequestTimeout() {
		return requestTimeout;
	}

	public void setRequestTimeout(Integer requestTimeout) {
		this.requestTimeout = requestTimeout;
	}

	public Integer getHoldTimeout() {
		return holdTimeout;
	}

	public void setHoldTimeout(Integer holdTimeout) {
		this.holdTimeout = holdTimeout;
	}

	private Integer tokenCount;
	private Integer priority;
	private Integer requestTimeout;
	private Integer holdTimeout;
}
