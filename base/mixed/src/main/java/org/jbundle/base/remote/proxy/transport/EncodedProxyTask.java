/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.remote.proxy.transport;

/**
 * @(#)DBServlet.java 0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jbundle.base.screen.control.servlet.BasicServlet;
import org.jbundle.thin.base.remote.RemoteException;
import org.jbundle.thin.base.util.base64.Base64;


/**
 * EncodedProxyTask The servlet to handle proxy messages (tunneled through http).
 */
public class EncodedProxyTask extends ProxyTask
{

    /**
     * Constructor.
     */
    public EncodedProxyTask()
    {
        super();
    }
    /**
     * Constructor.
     */
    public EncodedProxyTask(BasicServlet servlet, SERVLET_TYPE servletType)
    {
        this();
        this.init(servlet, servletType);
    }
    /**
     * Constructor.
     */
    public void init(BasicServlet servlet, SERVLET_TYPE servletType)
    {
        super.init(servlet, servletType);
    }
    /**
     * Get the next (String) param.
     * Typically this is overidden in the concrete implementation.
     * @param strName The param name (in most implementations this is optional).
     * @return The next param as a string.
     */
    public String getNextStringParam(String strName)
    {
        String string = this.getProperty(strName);
        if (NULL.equals(string))
            string = null;
        return string;
    }
    /**
     * Sent/send this return string.
     * @param out The return output stream.
     * @param strReturn The string to return.
     */
    public void setErrorReturn(PrintWriter out, RemoteException ex)
        throws RemoteException
    {
        this.setReturnObject(out, ex);
    }
    /**
     * Sent/send this return string.
     * @param out The return output stream.
     * @param strReturn The string to return.
     */
    public void setReturnObject(PrintWriter out, Object objReturn)
    {
        String strReturn = org.jbundle.thin.base.remote.proxy.transport.BaseTransport.convertObjectToString(objReturn);
        strReturn = Base64.encode(strReturn);
        this.setReturnString(out, strReturn);
    }
    /**
     * Get the next (String) param.
     * @param strName The param name (in most implementations this is optional).
     * @return The next param as a string.
     */
    public Object getNextObjectParam(InputStream in, String strName, Map<String, Object> properties)
    {
        String strParam = this.getNextStringParam(in, strName, properties);
        strParam = Base64.decode(strParam);
        return org.jbundle.thin.base.remote.proxy.transport.BaseTransport.convertStringToObject(strParam);
    }
    /**
     * Process an HTML get or post.
     * @exception ServletException From inherited class.
     * @exception IOException From inherited class.
     */
    public void doProcess(BasicServlet servlet, HttpServletRequest req, HttpServletResponse res, PrintWriter out) 
        throws ServletException, IOException
    {
        super.doProcess(servlet, req, res, out);
    }
}
