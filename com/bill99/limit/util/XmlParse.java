package com.bill99.limit.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * 测试各种xml解析效率
 * 
 * @author jun.bao
 * @since 2013年8月27日
 */
public class XmlParse {

	public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
		// ClassLoader.getSystemResourceAsStream("soap.xml");
		URL url = Thread.currentThread().getContextClassLoader().getResource("com/bill99/soap.xml");
		InputStream in = new FileInputStream(new File(url.getFile()));
		// System.out.println(url.getFile());
		int times = 1;
		Long time1 = Calendar.getInstance().getTimeInMillis();
		for (int i = 0; i < times; i++) {
			sax(in);
		}
		Long time2 = Calendar.getInstance().getTimeInMillis();
		System.out.println(time2 - time1);
	}

	public static Map<String, String> sax(InputStream in) throws ParserConfigurationException, SAXException, FileNotFoundException,
			IOException {
		Map<String, String> map = null;
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			XMLReader reader = parser.getXMLReader();
			List<String> list = new ArrayList<String>();
			list.add("ip");
			list.add("username");
			list.add("mac");
			XMLFilter myFilter = new MyFilter(reader, list);

			DefaultHandler defaultHandler = new MyDefaultHandler();
			myFilter.setContentHandler(defaultHandler);
			myFilter.parse(new InputSource(in));

			map = ((MyDefaultHandler) defaultHandler).getKv();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}

}
