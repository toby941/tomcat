package com.bill99.limit.service.token.task;

import java.util.Map;
import java.util.concurrent.Callable;

import com.bill99.limit.util.JDKHttpClient;

/**
 * 用于异步提交http请求 的task
 * 
 * @author jun.bao
 * @since 2013年8月29日
 */
public class HttpCallable implements Callable<String> {

	private String requstURL;
	private Map<String, String> postMap;
	private Boolean isGet;

	public Boolean getIsGet() {
		return isGet;
	}

	public void setIsGet(Boolean isGet) {
		this.isGet = isGet;
	}

	public HttpCallable(Map<String, String> info) {
		super();
		this.postMap = info;
		this.requstURL = "http://localhost:8080/alarm";
	}

	public String getRequstURL() {
		return requstURL;
	}

	public void setRequstURL(String requstURL) {
		this.requstURL = requstURL;
	}

	public Map<String, String> getPostMap() {
		return postMap;
	}

	public void setPostMap(Map<String, String> postMap) {
		this.postMap = postMap;
	}

	@Override
	public String call() throws Exception {
		if (isGet) {
			return JDKHttpClient.doGet(requstURL);
		} else {
			return JDKHttpClient.doPost(requstURL, postMap);
		}
	}

}
