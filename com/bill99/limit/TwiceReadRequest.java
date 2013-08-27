package com.bill99.limit;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.ServletInputStream;

import org.apache.catalina.connector.CoyoteInputStream;
import org.apache.catalina.connector.Request;

/**
 * @author jun.bao
 * @since 2013年8月22日
 */
public class TwiceReadRequest extends Request {

	private String body;
	protected static org.apache.juli.logging.Log logger = org.apache.juli.logging.LogFactory.getLog(TwiceReadRequest.class);

	public TwiceReadRequest(Request request) {
		super();
		this.coyoteRequest = request.getCoyoteRequest();
		this.connector = request.getConnector();
		inputBuffer.setRequest(coyoteRequest);
		StringBuilder stringBuilder = new StringBuilder();
		BufferedReader bufferedReader = null;
		try {
			InputStream inputStream = request.getInputStream();
			if (inputStream != null) {
				bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
				char[] charBuffer = new char[128];
				int bytesRead = -1;
				while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
					stringBuilder.append(charBuffer, 0, bytesRead);
				}
			} else {
				stringBuilder.append("");
			}
		} catch (IOException ex) {
			logger.error("Error reading the request body...");
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException ex) {
					logger.error("Error closing bufferedReader...");
				}
			}
		}
		// 用body字符串存储inputStream
		body = stringBuilder.toString();
	}

	/**
	 * 将body字符串转化为inputStream返回
	 */
	@Override
	public ServletInputStream getInputStream() throws IOException {
		final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body.getBytes());
		ServletInputStream inputStream = new ServletInputStream() {
			public int read() throws IOException {
				return byteArrayInputStream.read();
			}
		};
		return inputStream;
	}

	public TwiceReadRequest() {
		super();
		body = "";
	}

	@Override
	public InputStream getStream() {
		System.out.println("call getStream: " + body);
		final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body.getBytes());

		ServletInputStream inputStream = new ServletInputStream() {
			public int read() throws IOException {
				return byteArrayInputStream.read();
			}
		};
		return inputStream;
	}

	public void resetBody() {
		StringBuilder stringBuilder = new StringBuilder();
		BufferedReader bufferedReader = null;

		try {
			InputStream inputStream = new CoyoteInputStream(inputBuffer);

			if (inputStream != null) {
				bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

				char[] charBuffer = new char[128];
				int bytesRead = -1;

				while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
					stringBuilder.append(charBuffer, 0, bytesRead);
				}
			} else {
				stringBuilder.append("xx");
			}
		} catch (IOException ex) {
			logger.error("Error reading the request body...");
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException ex) {
					logger.error("Error closing bufferedReader...");
				}
			}
		}

		body = stringBuilder.toString();
		System.out.println("TwiceReadRequest create body: " + body);
	}

}
