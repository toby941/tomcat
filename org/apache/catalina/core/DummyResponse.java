/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.apache.catalina.core;


import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.connector.Request;


/**
 * Dummy response object, used for JSP precompilation.
 *
 * @author Remy Maucherat
 * @version $Id: DummyResponse.java 939336 2010-04-29 15:00:41Z kkolinko $
 */

public class DummyResponse
    implements HttpServletResponse {

    public DummyResponse() {
    }


    public void setAppCommitted(boolean appCommitted) {}
    public boolean isAppCommitted() { return false; }
    public Connector getConnector() { return null; }
    public void setConnector(Connector connector) {}
    public int getContentCount() { return -1; }
    public Context getContext() { return null; }
    public void setContext(Context context) {}
    public boolean getIncluded() { return false; }
    public void setIncluded(boolean included) {}
    public String getInfo() { return null; }
    public Request getRequest() { return null; }
    public void setRequest(Request request) {}
    public ServletResponse getResponse() { return null; }
    public OutputStream getStream() { return null; }
    public void setStream(OutputStream stream) {}
    public void setSuspended(boolean suspended) {}
    public boolean isSuspended() { return false; }
    public void setError() {}
    public boolean isError() { return false; }
    public ServletOutputStream createOutputStream() throws IOException {
        return null;
    }
    public void finishResponse() throws IOException {}
    public int getContentLength() { return -1; }
    @Override
	public String getContentType() { return null; }
    public PrintWriter getReporter() { return null; }
    public void recycle() {}
    public void write(int b) throws IOException {}
    public void write(byte b[]) throws IOException {}
    public void write(byte b[], int off, int len) throws IOException {}
    @Override
	public void flushBuffer() throws IOException {}
    @Override
	public int getBufferSize() { return -1; }
    @Override
	public String getCharacterEncoding() { return null; }
    @Override
	public void setCharacterEncoding(String charEncoding) {}
    @Override
	public ServletOutputStream getOutputStream() throws IOException {
        return null;
    }
    @Override
	public Locale getLocale() { return null; }
    @Override
	public PrintWriter getWriter() throws IOException { return null; }
    @Override
	public boolean isCommitted() { return false; }
    @Override
	public void reset() {}
    @Override
	public void resetBuffer() {}
    @Override
	public void setBufferSize(int size) {}
    @Override
	public void setContentLength(int length) {}
    @Override
	public void setContentType(String type) {}
    @Override
	public void setLocale(Locale locale) {}

    public Cookie[] getCookies() { return null; }
    public String getHeader(String name) { return null; }
    public String[] getHeaderNames() { return null; }
    public String[] getHeaderValues(String name) { return null; }
    public String getMessage() { return null; }
    public int getStatus() { return -1; }
    public void reset(int status, String message) {}
    @Override
	public void addCookie(Cookie cookie) {}
    @Override
	public void addDateHeader(String name, long value) {}
    @Override
	public void addHeader(String name, String value) {}
    @Override
	public void addIntHeader(String name, int value) {}
    @Override
	public boolean containsHeader(String name) { return false; }
    @Override
	public String encodeRedirectURL(String url) { return null; }
    @Override
	public String encodeRedirectUrl(String url) { return null; }
    @Override
	public String encodeURL(String url) { return null; }
    @Override
	public String encodeUrl(String url) { return null; }
    public void sendAcknowledgement() throws IOException {}
    @Override
	public void sendError(int status) throws IOException {}
    @Override
	public void sendError(int status, String message) throws IOException {}
    @Override
	public void sendRedirect(String location) throws IOException {}
    @Override
	public void setDateHeader(String name, long value) {}
    @Override
	public void setHeader(String name, String value) {}
    @Override
	public void setIntHeader(String name, int value) {}
    @Override
	public void setStatus(int status) {}
    @Override
	public void setStatus(int status, String message) {}


}
