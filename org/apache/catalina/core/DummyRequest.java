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


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.catalina.Context;
import org.apache.catalina.Host;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.connector.Response;
import org.apache.tomcat.util.buf.MessageBytes;


/**
 * Dummy request object, used for request dispatcher mapping, as well as
 * JSP precompilation.
 *
 * @author Remy Maucherat
 * @version $Id: DummyRequest.java 939336 2010-04-29 15:00:41Z kkolinko $
 */

public class DummyRequest
    implements HttpServletRequest {

    public DummyRequest() {
    }

    public DummyRequest(String contextPath, String decodedURI,
                        String queryString) {
        this.contextPath = contextPath;
        this.decodedURI = decodedURI;
        this.queryString = queryString;
    }

    protected String contextPath = null;
    protected String decodedURI = null;
    protected String queryString = null;

    protected String pathInfo = null;
    protected String servletPath = null;
    protected Wrapper wrapper = null;

    protected FilterChain filterChain = null;
    
    private static Enumeration dummyEnum = new Enumeration(){
        @Override
		public boolean hasMoreElements(){
            return false;
        }
        @Override
		public Object nextElement(){
            return null;
        }
    };

    @Override
	public String getContextPath() {
        return (contextPath);
    }

    public MessageBytes getContextPathMB() {
        return null;
    }

    public ServletRequest getRequest() {
        return (this);
    }

    public String getDecodedRequestURI() {
        return decodedURI;
    }

    public MessageBytes getDecodedRequestURIMB() {
        return null;
    }

    public FilterChain getFilterChain() {
        return (this.filterChain);
    }

    public void setFilterChain(FilterChain filterChain) {
        this.filterChain = filterChain;
    }

    @Override
	public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String query) {
        queryString = query;
    }

    @Override
	public String getPathInfo() {
        return pathInfo;
    }

    public void setPathInfo(String path) {
        pathInfo = path;
    }

    public MessageBytes getPathInfoMB() {
        return null;
    }

    public MessageBytes getRequestPathMB() {
        return null;
    }

    @Override
	public String getServletPath() {
        return servletPath;
    }

    public void setServletPath(String path) {
        servletPath = path;
    }

    public MessageBytes getServletPathMB() {
        return null;
    }

    public Wrapper getWrapper() {
        return (this.wrapper);
    }

    public void setWrapper(Wrapper wrapper) {
        this.wrapper = wrapper;
    }

    public String getAuthorization() { return null; }
    public void setAuthorization(String authorization) {}
    public Connector getConnector() { return null; }
    public void setConnector(Connector connector) {}
    public Context getContext() { return null; }
    public void setContext(Context context) {}
    public Host getHost() { return null; }
    public void setHost(Host host) {}
    public String getInfo() { return null; }
    public Response getResponse() { return null; }
    public void setResponse(Response response) {}
    public Socket getSocket() { return null; }
    public void setSocket(Socket socket) {}
    public InputStream getStream() { return null; }
    public void setStream(InputStream input) {}
    public void addLocale(Locale locale) {}
    public ServletInputStream createInputStream() throws IOException {
        return null;
    }
    public void finishRequest() throws IOException {}
    public Object getNote(String name) { return null; }
    public Iterator getNoteNames() { return null; }
    public void removeNote(String name) {}
    public void setContentType(String type) {}
    public void setNote(String name, Object value) {}
    public void setProtocol(String protocol) {}
    public void setRemoteAddr(String remoteAddr) {}
    public void setRemoteHost(String remoteHost) {}
    public void setScheme(String scheme) {}
    public void setServerName(String name) {}
    public void setServerPort(int port) {}
    @Override
	public Object getAttribute(String name) { return null; }
    @Override
	public Enumeration getAttributeNames() { return null; }
    @Override
	public String getCharacterEncoding() { return null; }
    @Override
	public int getContentLength() { return -1; }
    public void setContentLength(int length) {}
    @Override
	public String getContentType() { return null; }
    @Override
	public ServletInputStream getInputStream() throws IOException {
        return null;
    }
    @Override
	public Locale getLocale() { return null; }
    @Override
	public Enumeration getLocales() { return null; }
    @Override
	public String getProtocol() { return null; }
    @Override
	public BufferedReader getReader() throws IOException { return null; }
    @Override
	public String getRealPath(String path) { return null; }
    @Override
	public String getRemoteAddr() { return null; }
    @Override
	public String getRemoteHost() { return null; }
    @Override
	public String getScheme() { return null; }
    @Override
	public String getServerName() { return null; }
    @Override
	public int getServerPort() { return -1; }
    @Override
	public boolean isSecure() { return false; }
    @Override
	public void removeAttribute(String name) {}
    @Override
	public void setAttribute(String name, Object value) {}
    @Override
	public void setCharacterEncoding(String enc)
        throws UnsupportedEncodingException {}
    public void addCookie(Cookie cookie) {}
    public void addHeader(String name, String value) {}
    public void addParameter(String name, String values[]) {}
    public void clearCookies() {}
    public void clearHeaders() {}
    public void clearLocales() {}
    public void clearParameters() {}
    public void recycle() {}
    public void setAuthType(String authType) {}
    public void setContextPath(String path) {}
    public void setMethod(String method) {}
    public void setRequestedSessionCookie(boolean flag) {}
    public void setRequestedSessionId(String id) {}
    public void setRequestedSessionURL(boolean flag) {}
    public void setRequestURI(String uri) {}
    public void setSecure(boolean secure) {}
    public void setUserPrincipal(Principal principal) {}
    @Override
	public String getParameter(String name) { return null; }
    @Override
	public Map getParameterMap() { return null; }
    @Override
	public Enumeration getParameterNames() { return dummyEnum; }
    @Override
	public String[] getParameterValues(String name) { return null; }
    @Override
	public RequestDispatcher getRequestDispatcher(String path) {
        return null;
    }
    @Override
	public String getAuthType() { return null; }
    @Override
	public Cookie[] getCookies() { return null; }
    @Override
	public long getDateHeader(String name) { return -1; }
    @Override
	public String getHeader(String name) { return null; }
    @Override
	public Enumeration getHeaders(String name) { return null; }
    @Override
	public Enumeration getHeaderNames() { return null; }
    @Override
	public int getIntHeader(String name) { return -1; }
    @Override
	public String getMethod() { return null; }
    @Override
	public String getPathTranslated() { return null; }
    @Override
	public String getRemoteUser() { return null; }
    @Override
	public String getRequestedSessionId() { return null; }
    @Override
	public String getRequestURI() { return null; }
    public void setDecodedRequestURI(String uri) {}
    @Override
	public StringBuffer getRequestURL() { return null; }
    @Override
	public HttpSession getSession() { return null; }
    @Override
	public HttpSession getSession(boolean create) { return null; }
    @Override
	public boolean isRequestedSessionIdFromCookie() { return false; }
    @Override
	public boolean isRequestedSessionIdFromURL() { return false; }
    @Override
	public boolean isRequestedSessionIdFromUrl() { return false; }
    @Override
	public boolean isRequestedSessionIdValid() { return false; }
    @Override
	public boolean isUserInRole(String role) { return false; }
    @Override
	public Principal getUserPrincipal() { return null; }
    @Override
	public String getLocalAddr() { return null; }    
    @Override
	public String getLocalName() { return null; }
    @Override
	public int getLocalPort() { return -1; }
    @Override
	public int getRemotePort() { return -1; }
    
}

