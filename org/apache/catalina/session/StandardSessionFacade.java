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


package org.apache.catalina.session;


import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;


/**
 * Facade for the StandardSession object.
 *
 * @author Remy Maucherat
 * @version $Id: StandardSessionFacade.java 939518 2010-04-30 00:08:58Z kkolinko $
 */

public class StandardSessionFacade
    implements HttpSession {


    // ----------------------------------------------------------- Constructors


    /**
     * Construct a new session facade.
     */
    public StandardSessionFacade(StandardSession session) {
        super();
        this.session = session;
    }


    /**
     * Construct a new session facade.
     */
    public StandardSessionFacade(HttpSession session) {
        super();
        this.session = session;
    }


    // ----------------------------------------------------- Instance Variables


    /**
     * Wrapped session object.
     */
    private HttpSession session = null;


    // ---------------------------------------------------- HttpSession Methods


    @Override
	public long getCreationTime() {
        return session.getCreationTime();
    }


    @Override
	public String getId() {
        return session.getId();
    }


    @Override
	public long getLastAccessedTime() {
        return session.getLastAccessedTime();
    }


    @Override
	public ServletContext getServletContext() {
        // FIXME : Facade this object ?
        return session.getServletContext();
    }


    @Override
	public void setMaxInactiveInterval(int interval) {
        session.setMaxInactiveInterval(interval);
    }


    @Override
	public int getMaxInactiveInterval() {
        return session.getMaxInactiveInterval();
    }


    @Override
	public HttpSessionContext getSessionContext() {
        return session.getSessionContext();
    }


    @Override
	public Object getAttribute(String name) {
        return session.getAttribute(name);
    }


    @Override
	public Object getValue(String name) {
        return session.getAttribute(name);
    }


    @Override
	public Enumeration getAttributeNames() {
        return session.getAttributeNames();
    }


    @Override
	public String[] getValueNames() {
        return session.getValueNames();
    }


    @Override
	public void setAttribute(String name, Object value) {
        session.setAttribute(name, value);
    }


    @Override
	public void putValue(String name, Object value) {
        session.setAttribute(name, value);
    }


    @Override
	public void removeAttribute(String name) {
        session.removeAttribute(name);
    }


    @Override
	public void removeValue(String name) {
        session.removeAttribute(name);
    }


    @Override
	public void invalidate() {
        session.invalidate();
    }


    @Override
	public boolean isNew() {
        return session.isNew();
    }


}
