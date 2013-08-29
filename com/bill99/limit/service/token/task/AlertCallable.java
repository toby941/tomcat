package com.bill99.limit.service.token.task;

import java.util.Map;
import java.util.concurrent.Callable;

import com.bill99.limit.util.JDKHttpClient;

/**
 * @author jun.bao
 * @since 2013年8月29日
 */
public class AlertCallable implements Callable<String> {

	private String postURL;
	private Map<String, String> postMap;

	public AlertCallable(Map<String, String> info) {
		super();
		this.postMap = info;
		this.postURL = "http://localhost:8080/alarm";
	}

	public String getPostURL() {
		return postURL;
	}

	public void setPostURL(String postURL) {
		this.postURL = postURL;
	}

	public Map<String, String> getPostMap() {
		return postMap;
	}

	public void setPostMap(Map<String, String> postMap) {
		this.postMap = postMap;
	}

	@Override
	public String call() throws Exception {
		return JDKHttpClient.doPost(postURL, postMap);
	}

}
