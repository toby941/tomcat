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


package org.apache.catalina.connector;

import java.io.BufferedReader;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.catalina.Globals;
import org.apache.catalina.util.StringManager;


import org.apache.catalina.security.SecurityUtil;

/**
 * Facade class that wraps a Coyote request object.  
 * All methods are delegated to the wrapped request.
 *
 * @author Craig R. McClanahan
 * @author Remy Maucherat
 * @author Jean-Francois Arcand
 * @version $Id: RequestFacade.java 939336 2010-04-29 15:00:41Z kkolinko $
 */

@SuppressWarnings("deprecation")
public class RequestFacade implements HttpServletRequest {
        
        
    // ----------------------------------------------------------- DoPrivileged
    
    private final class GetAttributePrivilegedAction
            implements PrivilegedAction {
        
        @Override
		public Object run() {
            return request.getAttributeNames();
        }            
    }
     
    
    private final class GetParameterMapPrivilegedAction
            implements PrivilegedAction {
        
        @Override
		public Object run() {
            return request.getParameterMap();
        }        
    }    
    
    
    private final class GetRequestDispatcherPrivilegedAction
            implements PrivilegedAction {

        private String path;

        public GetRequestDispatcherPrivilegedAction(String path){
            this.path = path;
        }
        
        @Override
		public Object run() {   
            return request.getRequestDispatcher(path);
        }           
    }    
    
    
    private final class GetParameterPrivilegedAction
            implements PrivilegedAction {

        public String name;

        public GetParameterPrivilegedAction(String name){
            this.name = name;
        }

        @Override
		public Object run() {       
            return request.getParameter(name);
        }           
    }    
    
     
    private final class GetParameterNamesPrivilegedAction
            implements PrivilegedAction {
        
        @Override
		public Object run() {          
            return request.getParameterNames();
        }           
    } 
    
    
    private final class GetParameterValuePrivilegedAction
            implements PrivilegedAction {

        public String name;

        public GetParameterValuePrivilegedAction(String name){
            this.name = name;
        }

        @Override
		public Object run() {       
            return request.getParameterValues(name);
        }           
    }    
  
    
    private final class GetCookiesPrivilegedAction
            implements PrivilegedAction {
        
        @Override
		public Object run() {       
            return request.getCookies();
        }           
    }      
    
    
    private final class GetCharacterEncodingPrivilegedAction
            implements PrivilegedAction {
        
        @Override
		public Object run() {       
            return request.getCharacterEncoding();
        }           
    }   
        
    
    private final class GetHeadersPrivilegedAction
            implements PrivilegedAction {

        private String name;

        public GetHeadersPrivilegedAction(String name){
            this.name = name;
        }
        
        @Override
		public Object run() {       
            return request.getHeaders(name);
        }           
    }    
        
    
    private final class GetHeaderNamesPrivilegedAction
            implements PrivilegedAction {

        @Override
		public Object run() {       
            return request.getHeaderNames();
        }           
    }  
            
    
    private final class GetLocalePrivilegedAction
            implements PrivilegedAction {

        @Override
		public Object run() {       
            return request.getLocale();
        }           
    }    
            
    
    private final class GetLocalesPrivilegedAction
            implements PrivilegedAction {

        @Override
		public Object run() {       
            return request.getLocales();
        }           
    }    
    
    private final class GetSessionPrivilegedAction
            implements PrivilegedAction {

        private boolean create;
        
        public GetSessionPrivilegedAction(boolean create){
            this.create = create;
        }
                
        @Override
		public Object run() {  
            return request.getSession(create);
        }           
    }

    // ----------------------------------------------------------- Constructors


    /**
     * Construct a wrapper for the specified request.
     *
     * @param request The request to be wrapped
     */
    public RequestFacade(Request request) {

        this.request = request;

    }


    // ----------------------------------------------------- Instance Variables


    /**
     * The wrapped request.
     */
    protected Request request = null;


    /**
     * The string manager for this package.
     */
    protected static StringManager sm =
        StringManager.getManager(Constants.Package);


    // --------------------------------------------------------- Public Methods


    /**
     * Clear facade.
     */
    public void clear() {
        request = null;
    }

    
    /**
     * Prevent cloning the facade.
     */
    @Override
	protected Object clone()
        throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }


    // ------------------------------------------------- ServletRequest Methods


    @Override
	public Object getAttribute(String name) {

        if (request == null) {
            throw new IllegalStateException(
                            sm.getString("requestFacade.nullRequest"));
        }

        return request.getAttribute(name);
    }


    @Override
	public Enumeration getAttributeNames() {

        if (request == null) {
            throw new IllegalStateException(
                            sm.getString("requestFacade.nullRequest"));
        }

        if (Globals.IS_SECURITY_ENABLED){
            return (Enumeration)AccessController.doPrivileged(
                new GetAttributePrivilegedAction());        
        } else {
            return request.getAttributeNames();
        }
    }


    @Override
	public String getCharacterEncoding() {

        if (request == null) {
            throw new IllegalStateException(
                            sm.getString("requestFacade.nullRequest"));
        }

        if (Globals.IS_SECURITY_ENABLED){
            return (String)AccessController.doPrivileged(
                new GetCharacterEncodingPrivilegedAction());
        } else {
            return request.getCharacterEncoding();
        }         
    }


    @Override
	public void setCharacterEncoding(String env)
            throws java.io.UnsupportedEncodingException {

        if (request == null) {
            throw new IllegalStateException(
                            sm.getString("requestFacade.nullRequest"));
        }

        request.setCharacterEncoding(env);
    }


    @Override
	public int getContentLength() {

        if (request == null) {
            throw new IllegalStateException(
                            sm.getString("requestFacade.nullRequest"));
        }

        return request.getContentLength();
    }


    @Override
	public String getContentType() {

        if (request == null) {
            throw new IllegalStateException(
                            sm.getString("requestFacade.nullRequest"));
        }

        return request.getContentType();
    }


    @Override
	public ServletInputStream getInputStream() throws IOException {

        if (request == null) {
            throw new IllegalStateException(
                            sm.getString("requestFacade.nullRequest"));
        }

        return request.getInputStream();
    }


    @Override
	public String getParameter(String name) {

        if (request == null) {
            throw new IllegalStateException(
                            sm.getString("requestFacade.nullRequest"));
        }

        if (Globals.IS_SECURITY_ENABLED){
            return (String)AccessController.doPrivileged(
                new GetParameterPrivilegedAction(name));
        } else {
            return request.getParameter(name);
        }
    }


    @Override
	public Enumeration getParameterNames() {

        if (request == null) {
            throw new IllegalStateException(
                            sm.getString("requestFacade.nullRequest"));
        }

        if (Globals.IS_SECURITY_ENABLED){
            return (Enumeration)AccessController.doPrivileged(
                new GetParameterNamesPrivilegedAction());
        } else {
            return request.getParameterNames();
        }
    }


    @Override
	public String[] getParameterValues(String name) {

        if (request == null) {
            throw new IllegalStateException(
                            sm.getString("requestFacade.nullRequest"));
        }

        String[] ret = null;

        /*
         * Clone the returned array only if there is a security manager
         * in place, so that performance won't suffer in the nonsecure case
         */
        if (SecurityUtil.isPackageProtectionEnabled()){
            ret = (String[]) AccessController.doPrivileged(
                new GetParameterValuePrivilegedAction(name));
            if (ret != null) {
                ret = ret.clone();
            }
        } else {
            ret = request.getParameterValues(name);
        }

        return ret;
    }


    @Override
	public Map getParameterMap() {

        if (request == null) {
            throw new IllegalStateException(
                            sm.getString("requestFacade.nullRequest"));
        }

        if (Globals.IS_SECURITY_ENABLED){
            return (Map)AccessController.doPrivileged(
                new GetParameterMapPrivilegedAction());        
        } else {
            return request.getParameterMap();
        }
    }


    @Override
	public String getProtocol() {

        if (request == null) {
            throw new IllegalStateException(
                            sm.getString("requestFacade.nullRequest"));
        }

        return request.getProtocol();
    }


    @Override
	public String getScheme() {

        if (request == null) {
            throw new IllegalStateException(
                            sm.getString("requestFacade.nullRequest"));
        }

        return request.getScheme();
    }


    @Override
	public String getServerName() {

        if (request == null) {
            throw new IllegalStateException(
                            sm.getString("requestFacade.nullRequest"));
        }

        return request.getServerName();
    }


    @Override
	public int getServerPort() {

        if (request == null) {
            throw new IllegalStateException(
                            sm.getString("requestFacade.nullRequest"));
        }

        return request.getServerPort();
    }


    @Override
	public BufferedReader getReader() throws IOException {

        if (request == null) {
            throw new IllegalStateException(
                            sm.getString("requestFacade.nullRequest"));
        }

        return request.getReader();
    }


    @Override
	public String getRemoteAddr() {

        if (request == null) {
            throw new IllegalStateException(
                            sm.getString("requestFacade.nullRequest"));
        }

        return request.getRemoteAddr();
    }


    @Override
	public String getRemoteHost() {

        if (request == null) {
            throw new IllegalStateException(
                            sm.getString("requestFacade.nullRequest"));
        }

        return request.getRemoteHost();
    }


    @Override
	public void setAttribute(String name, Object o) {

        if (request == null) {
            throw new IllegalStateException(
                            sm.getString("requestFacade.nullRequest"));
        }

        request.setAttribute(name, o);
    }


    @Override
	public void removeAttribute(String name) {

        if (request == null) {
            throw new IllegalStateException(
                            sm.getString("requestFacade.nullRequest"));
        }

        request.removeAttribute(name);
    }


    @Override
	public Locale getLocale() {

        if (request == null) {
            throw new IllegalStateException(
                            sm.getString("requestFacade.nullRequest"));
        }

        if (Globals.IS_SECURITY_ENABLED){
            return (Locale)AccessController.doPrivileged(
                new GetLocalePrivilegedAction());
        } else {
            return request.getLocale();
        }        
    }


    @Override
	public Enumeration getLocales() {

        if (request == null) {
            throw new IllegalStateException(
                            sm.getString("requestFacade.nullRequest"));
        }

        if (Globals.IS_SECURITY_ENABLED){
            return (Enumeration)AccessController.doPrivileged(
                new GetLocalesPrivilegedAction());
        } else {
            return request.getLocales();
        }        
    }


    @Override
	public boolean isSecure() {

        if (request == null) {
            throw new IllegalStateException(
                            sm.getString("requestFacade.nullRequest"));
        }

        return request.isSecure();
    }


    @Override
	public RequestDispatcher getRequestDispatcher(String path) {

        if (request == null) {
            throw new IllegalStateException(
                            sm.getString("requestFacade.nullRequest"));
        }

        if (Globals.IS_SECURITY_ENABLED){
            return (RequestDispatcher)AccessController.doPrivileged(
                new GetRequestDispatcherPrivilegedAction(path));
        } else {
            return request.getRequestDispatcher(path);
        }
    }

    @Override
	public String getRealPath(String path) {

        if (request == null) {
            throw new IllegalStateException(
                            sm.getString("requestFacade.nullRequest"));
        }

        return request.getRealPath(path);
    }


    @Override
	public String getAuthType() {

        if (request == null) {
            throw new IllegalStateException(
                            sm.getString("requestFacade.nullRequest"));
        }

        return request.getAuthType();
    }


    @Override
	public Cookie[] getCookies() {

        if (request == null) {
            throw new IllegalStateException(
                            sm.getString("requestFacade.nullRequest"));
        }

        Cookie[] ret = null;

        /*
         * Clone the returned array only if there is a security manager
         * in place, so that performance won't suffer in the nonsecure case
         */
        if (SecurityUtil.isPackageProtectionEnabled()){
            ret = (Cookie[])AccessController.doPrivileged(
                new GetCookiesPrivilegedAction());
            if (ret != null) {
                ret = ret.clone();
            }
        } else {
            ret = request.getCookies();
        }

        return ret;
    }


    @Override
	public long getDateHeader(String name) {

        if (request == null) {
            throw new IllegalStateException(
                            sm.getString("requestFacade.nullRequest"));
        }

        return request.getDateHeader(name);
    }


    @Override
	public String getHeader(String name) {

        if (request == null) {
            throw new IllegalStateException(
                            sm.getString("requestFacade.nullRequest"));
        }

        return request.getHeader(name);
    }


    @Override
	public Enumeration getHeaders(String name) {

        if (request == null) {
            throw new IllegalStateException(
                            sm.getString("requestFacade.nullRequest"));
        }

        if (Globals.IS_SECURITY_ENABLED){
            return (Enumeration)AccessController.doPrivileged(
                new GetHeadersPrivilegedAction(name));
        } else {
            return request.getHeaders(name);
        }         
    }


    @Override
	public Enumeration getHeaderNames() {

        if (request == null) {
            throw new IllegalStateException(
                            sm.getString("requestFacade.nullRequest"));
        }

        if (Globals.IS_SECURITY_ENABLED){
            return (Enumeration)AccessController.doPrivileged(
                new GetHeaderNamesPrivilegedAction());
        } else {
            return request.getHeaderNames();
        }             
    }


    @Override
	public int getIntHeader(String name) {

        if (request == null) {
            throw new IllegalStateException(
                            sm.getString("requestFacade.nullRequest"));
        }

        return request.getIntHeader(name);
    }


    @Override
	public String getMethod() {

        if (request == null) {
            throw new IllegalStateException(
                            sm.getString("requestFacade.nullRequest"));
        }

        return request.getMethod();
    }


    @Override
	public String getPathInfo() {

        if (request == null) {
            throw new IllegalStateException(
                            sm.getString("requestFacade.nullRequest"));
        }

        return request.getPathInfo();
    }


    @Override
	public String getPathTranslated() {

        if (request == null) {
            throw new IllegalStateException(
                            sm.getString("requestFacade.nullRequest"));
        }

        return request.getPathTranslated();
    }


    @Override
	public String getContextPath() {

        if (request == null) {
            throw new IllegalStateException(
                            sm.getString("requestFacade.nullRequest"));
        }

        return request.getContextPath();
    }


    @Override
	public String getQueryString() {

        if (request == null) {
            throw new IllegalStateException(
                            sm.getString("requestFacade.nullRequest"));
        }

        return request.getQueryString();
    }


    @Override
	public String getRemoteUser() {

        if (request == null) {
            throw new IllegalStateException(
                            sm.getString("requestFacade.nullRequest"));
        }

        return request.getRemoteUser();
    }


    @Override
	public boolean isUserInRole(String role) {

        if (request == null) {
            throw new IllegalStateException(
                            sm.getString("requestFacade.nullRequest"));
        }

        return request.isUserInRole(role);
    }


    @Override
	public java.security.Principal getUserPrincipal() {

        if (request == null) {
            throw new IllegalStateException(
                            sm.getString("requestFacade.nullRequest"));
        }

        return request.getUserPrincipal();
    }


    @Override
	public String getRequestedSessionId() {

        if (request == null) {
            throw new IllegalStateException(
                            sm.getString("requestFacade.nullRequest"));
        }

        return request.getRequestedSessionId();
    }


    @Override
	public String getRequestURI() {

        if (request == null) {
            throw new IllegalStateException(
                            sm.getString("requestFacade.nullRequest"));
        }

        return request.getRequestURI();
    }


    @Override
	public StringBuffer getRequestURL() {

        if (request == null) {
            throw new IllegalStateException(
                            sm.getString("requestFacade.nullRequest"));
        }

        return request.getRequestURL();
    }


    @Override
	public String getServletPath() {

        if (request == null) {
            throw new IllegalStateException(
                            sm.getString("requestFacade.nullRequest"));
        }

        return request.getServletPath();
    }


    @Override
	public HttpSession getSession(boolean create) {

        if (request == null) {
            throw new IllegalStateException(
                            sm.getString("requestFacade.nullRequest"));
        }

        if (SecurityUtil.isPackageProtectionEnabled()){
            return (HttpSession)AccessController.
                doPrivileged(new GetSessionPrivilegedAction(create));
        } else {
            return request.getSession(create);
        }
    }

    @Override
	public HttpSession getSession() {

        if (request == null) {
            throw new IllegalStateException(
                            sm.getString("requestFacade.nullRequest"));
        }

        return getSession(true);
    }


    @Override
	public boolean isRequestedSessionIdValid() {

        if (request == null) {
            throw new IllegalStateException(
                            sm.getString("requestFacade.nullRequest"));
        }

        return request.isRequestedSessionIdValid();
    }


    @Override
	public boolean isRequestedSessionIdFromCookie() {

        if (request == null) {
            throw new IllegalStateException(
                            sm.getString("requestFacade.nullRequest"));
        }

        return request.isRequestedSessionIdFromCookie();
    }


    @Override
	public boolean isRequestedSessionIdFromURL() {

        if (request == null) {
            throw new IllegalStateException(
                            sm.getString("requestFacade.nullRequest"));
        }

        return request.isRequestedSessionIdFromURL();
    }


    @Override
	public boolean isRequestedSessionIdFromUrl() {

        if (request == null) {
            throw new IllegalStateException(
                            sm.getString("requestFacade.nullRequest"));
        }

        return request.isRequestedSessionIdFromURL();
    }


    @Override
	public String getLocalAddr() {

        if (request == null) {
            throw new IllegalStateException(
                            sm.getString("requestFacade.nullRequest"));
        }

        return request.getLocalAddr();
    }


    @Override
	public String getLocalName() {

        if (request == null) {
            throw new IllegalStateException(
                            sm.getString("requestFacade.nullRequest"));
        }

        return request.getLocalName();
    }


    @Override
	public int getLocalPort() {

        if (request == null) {
            throw new IllegalStateException(
                            sm.getString("requestFacade.nullRequest"));
        }

        return request.getLocalPort();
    }


    @Override
	public int getRemotePort() {

        if (request == null) {
            throw new IllegalStateException(
                            sm.getString("requestFacade.nullRequest"));
        }

        return request.getRemotePort();
    }

    public boolean getAllowTrace() {
        return request.getConnector().getAllowTrace();
    }
}
