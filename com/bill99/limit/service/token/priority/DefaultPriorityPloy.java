package com.bill99.limit.service.token.priority;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.catalina.connector.Request;
import org.xml.sax.SAXException;

import com.bill99.limit.util.RequestUtil;
import com.bill99.limit.util.XmlParse;

/**
 * @author jun.bao
 * @since 2013年8月29日
 */
public class DefaultPriorityPloy implements PriorityPloy {

	@Override
	public Integer getPriority(Request request) {

		boolean isSOAPRequest = RequestUtil.isSOAPRequest(request);
		if (isSOAPRequest) {
			Map<String, String> kv = null;
			try {
				InputStream input = request.getInputStream();
				kv = XmlParse.sax(input, null);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (kv != null) {
				for (String key : kv.keySet()) {
					System.out.println("key: " + key + " value: " + kv.get(key));
				}
			}
		}

		return 0;
	}

}
