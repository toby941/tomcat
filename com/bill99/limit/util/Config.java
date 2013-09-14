package com.bill99.limit.util;

import java.text.MessageFormat;

/**
 * @author jun.bao
 * @since 2013年9月9日
 */
public class Config {

	public static Boolean isDev = true;
	public static String dev_host = "http://192.168.120.160:9090";
	public static String product_host = "http://localhost:9090";

	public static String get_config_url = "/getconfig?name={0}";

	public static String post_alarm_url = "/post/alarm/{0}";

	public static String getHost() {
		if (isDev) {
			return dev_host;
		} else {
			return product_host;
		}
	}

	public static String getAlarmUrl(String name) {
		return MessageFormat.format(getHost() + post_alarm_url, name);
	}

	public static String getConfigUrl(String name) {
		return MessageFormat.format(getHost() + get_config_url, name);
	}
}
