package com.bill99.limit.service.token.priority;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

import com.bill99.limit.domain.TokenPoolConfig;
import com.bill99.limit.domain.TokenQueueConfig;
import com.bill99.limit.util.StringParse;
import com.bill99.limit.util.XmlParse;

/**
 * 按预定义的key解析请求中携带的value，并组合成key 获取优先级
 * 
 * @author jun.bao
 * @since 2013年9月10日
 */
public class KeyValuePriorityPloy implements PriorityPloy {
	protected static Log log = LogFactory.getLog(KeyValuePriorityPloy.class);

	private Map<String, Integer> priorityMap;
	private String requestURL;
	private List<String> filterKeys;
	private String contentType;

	public KeyValuePriorityPloy() {
		super();
	}

	public KeyValuePriorityPloy(TokenPoolConfig config) {
		super();
		requestURL = config.getRequestUrl();
		contentType = config.getContentType();
		/*
		 * 此处的key按照 key-key-key 形式组合，一个或多个
		 */
		String key = config.getRequestKey();
		StringTokenizer st = new StringTokenizer(key, "-");
		filterKeys = new ArrayList<String>();
		while (st.hasMoreTokens()) {
			filterKeys.add(st.nextToken());
		}
		priorityMap = new HashMap<String, Integer>();
		for (TokenQueueConfig queueConfig : config.getTokenQueueConfigs()) {
			/*
			 * 此处的value+? 多个之间用;作为分隔符号
			 */
			String value = queueConfig.getValue();
			Integer priority = queueConfig.getPriority();
			StringTokenizer sTokenizer = new StringTokenizer(value, ";");
			while (sTokenizer.hasMoreTokens()) {
				priorityMap.put(sTokenizer.nextToken(), priority);
			}
		}
	}

	@Override
	public Integer getPriority(HttpServletRequest request) {
		try {
			Map<String, String> kv = null;
			if (contentTypeXML.equals(contentType)) {
				kv = XmlParse.sax(request.getInputStream(), filterKeys);
			} else {
				kv = StringParse.parse(request.getInputStream(), filterKeys);
			}
			// 请求参数中没有关键key，则按最低优先级处理
			if (kv == null || kv.size() == 0) {
				return Integer.MAX_VALUE;
			}
			StringBuffer sbKey = new StringBuffer();
			/**
			 * 将关键value组装成value1-value2-value3形式作为key查找对应优先级
			 */
			for (String value : kv.values()) {
				sbKey.append(value + "-");
			}
			String key = sbKey.substring(0, sbKey.length() - 1);
			Integer priority = priorityMap.get(key);
			if (priority == null) {
				priority = Integer.MAX_VALUE;
			}
			return priority;
		} catch (IOException e) {
			log.error("getPriority error", e);
		}
		return Integer.MAX_VALUE;
	}

}
