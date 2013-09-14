package com.bill99.limit.service.token;

import java.util.concurrent.Future;

/**
 * @author jun.bao
 * @since 2013年9月13日
 */
public interface TokenPool {

	public String getToken(Integer priority);

	public boolean releaseToken(String uuid);

	public String getSnapshot();
	
	public Future<String> submit(Integer priority);

}
