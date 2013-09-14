package com.bill99.limit.util;

import java.util.List;

/**
 * @author jun.bao
 * @since 2013年9月10日
 */
public class CollectionUtils {

	public static String listToString(List list) {
		StringBuffer sb = new StringBuffer();
		for (Object o : list) {
			sb.append(o.toString() + " ");
		}
		return sb.toString();
	}
}
