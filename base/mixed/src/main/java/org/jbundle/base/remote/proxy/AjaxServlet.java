/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.remote.proxy;

/**
 * @(#)DBServlet.java 0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.jbundle.base.remote.proxy.transport.AjaxProxyTask;
import org.jbundle.base.remote.proxy.transport.ProxyTask;
import org.jbundle.base.screen.control.servlet.BasicServlet;
import org.jbundle.base.screen.control.servlet.BasicServlet.SERVLET_TYPE;
import org.jbundle.thin.base.remote.proxy.ProxyConstants;


/**
 * ProxyServlet The servlet to handle proxy messages (tunneled through http).
 */
public class AjaxServlet extends ProxyServlet
    implements BasicServlet, ProxyConstants
{
    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     */
    public AjaxServlet()
    {
        super();
    }
    /**
     * init method.
     * @exception ServletException From inherited class.
     */
    public void init(ServletConfig config) throws ServletException
    {
        super.init(config);
    }
    /**
     * Create the new Proxy Task.
     */
    public ProxyTask createProxyTask()
    {
        return new AjaxProxyTask(this, BasicServlet.SERVLET_TYPE.AJAX);
    }
    /**
     * Set the content type for this type of servlet.
     */
    public void setContentType(HttpServletResponse res)
    {
        res.setContentType("text/html");
    }
}
