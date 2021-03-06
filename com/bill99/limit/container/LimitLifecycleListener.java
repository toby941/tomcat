package com.bill99.limit.container;

import org.apache.catalina.Context;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Constants;
import org.apache.catalina.util.StringManager;

import com.bill99.limit.service.token.TokenPoolManager;

/**
 * 限流功能初始化以及销毁清理监听器，本着对业务应用最小改动原则 此监听器不在server.xml中配置注册，而是通过硬编码在
 * {@link StandardContext#init()} 中注册,获取context的上下文信息后，对每个context(即应用)进行限流功能初始化
 * 
 * @author jun.bao
 * @since 2013年8月21日
 */
public class LimitLifecycleListener implements LifecycleListener {
	/**
	 * The Context we are associated with.
	 */
	protected Context context = null;
	protected static org.apache.juli.logging.Log log = org.apache.juli.logging.LogFactory.getLog(LimitLifecycleListener.class);
	/**
	 * The string resources for this package.
	 */
	protected static final StringManager sm = StringManager.getManager(Constants.Package);
	/**
	 * Initialized flag.
	 */
	protected boolean initialized = false;

	@Override
	public void lifecycleEvent(LifecycleEvent event) {
		// Identify the context we are associated with
		try {
			context = (Context) event.getLifecycle();
		} catch (ClassCastException e) {
			log.error(sm.getString("contextConfig.cce", event.getLifecycle()), e);
			return;
		}
		if (event.getType().equals(Lifecycle.INIT_EVENT)) {
			init(context.getName());
		} else if (event.getType().equals(Lifecycle.STOP_EVENT)) {
			stop();
		}
	}

	protected synchronized void init(String contextName) {
		if (initialized) {
			log.warn("lifecycleEvent already init with context: " + context.getPath());
			return;
		}
		log.warn(this.getClass().getName() + " init TokenPoolManager with context :" + contextName);
		TokenPoolManager.getTokenPoolManager().init(contextName);
		initialized = true;
	}

	protected synchronized void stop() {
		if (!initialized) {
			log.error("have not been init!");
			return;
		}
		context = null;
	}
}
