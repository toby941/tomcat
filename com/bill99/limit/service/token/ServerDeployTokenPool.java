package com.bill99.limit.service.token;

import java.util.concurrent.Future;

/**
 * 服务端分配token，客户端直接请求 <br/>
 * 多节点客户端同时请求单点的服务端 压力？<br/>
 * 若部署多节点的服务端 token数量一致性保证 消耗效率
 * 
 * @author jun.bao
 * @since 2013年9月13日
 */
public class ServerDeployTokenPool implements TokenPool {

	private String requestTokenUrl;
	private Integer requestTimeout;
	private String poolName;

	@Override
	public String getToken(Integer priority) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean releaseToken(String uuid) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getSnapshot() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<String> submit(Integer priority) {
		// TODO Auto-generated method stub
		return null;
	}

}
