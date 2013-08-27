package com.bill99.limit;

import org.apache.catalina.Context;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Constants;
import org.apache.catalina.util.StringManager;

/**
 * @author jun.bao
 * @since 2013年8月21日
 */
public class StartListener implements LifecycleListener {
	/**
	 * The Context we are associated with.
	 */
	protected Context context = null;
	protected static org.apache.juli.logging.Log log = org.apache.juli.logging.LogFactory.getLog(StartListener.class);
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
		// System.out.println("lifecycleEvent: " + event.getType());
		try {
			context = (Context) event.getLifecycle();
		} catch (ClassCastException e) {
			log.error(sm.getString("contextConfig.cce", event.getLifecycle()), e);
			return;
		}
		if (event.getType().equals(Lifecycle.INIT_EVENT)) {
			init();
		} else if (event.getType().equals(StandardContext.STOP_EVENT)) {
			stop();
		}
	}

	protected synchronized void init() {
		if (initialized) {
			log.error("already init!");
			return;
		}
		initialized = true;
		log.error(context.getPath());
	}

	protected synchronized void stop() {
		if (!initialized) {
			log.error("have not been init!");
			return;
		}
		context = null;
		log.error("finish stop;");
	}
}
