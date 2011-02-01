package org.jbundle.base.remote.proxy;

/**
 * @(#)DBServlet.java 0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jbundle.base.remote.proxy.transport.EncodedProxyTask;
import org.jbundle.base.remote.proxy.transport.ProxyTask;
import org.jbundle.base.screen.control.servlet.BasicServlet;
import org.jbundle.base.screen.control.servlet.ServletTask;
import org.jbundle.base.screen.control.servlet.BaseHttpTask.SERVLET_TYPE;
import org.jbundle.base.screen.model.TopScreen;
import org.jbundle.model.RecordOwnerParent;
import org.jbundle.thin.base.db.FieldList;
import org.jbundle.thin.base.remote.proxy.ProxyConstants;


/**
 * ProxyServlet The servlet to handle proxy messages (tunneled through http).
 */
public class ProxyServlet extends HttpServlet
    implements BasicServlet, ProxyConstants
{
    private static final long serialVersionUID = 1L;

    /**
     * My worker task.
     */
    protected ProxyTask m_servletTask = null;

    /**
     * Constructor.
     */
    public ProxyServlet()
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
        ServletTask.initServlet(this, SERVLET_TYPE.PROXY);
        
        m_servletTask = this.createProxyTask();
    }
    /**
     * Create the new Proxy Task.
     */
    public ProxyTask createProxyTask()
    {
        return new EncodedProxyTask(this, SERVLET_TYPE.PROXY);
    }
    /**
     * Destroy this Servlet and any active applications.
     * This is only called when all users are done using this Servlet.
     */
    public void destroy()
    {
        super.destroy();
        if (m_servletTask != null)
            m_servletTask.free();
        m_servletTask = null;
        ServletTask.destroyServlet();
    }
    /**
     * returns the servlet info
     */ 
    public String getServletInfo()
    {
        return "This the proxy servlet";
    }
    /**
     * service method
     *  Valid parameters:
     * @exception ServletException From inherited class.
     * @exception IOException From inherited class.
     */
    public void doGet(HttpServletRequest req, HttpServletResponse res) 
        throws ServletException, IOException
    {
        this.doProcess(req, res);
    }
    /**
     *  process an HTML post.
     * @exception ServletException From inherited class.
     * @exception IOException From inherited class.
     */
    public void doPost(HttpServletRequest req, HttpServletResponse res) 
        throws ServletException, IOException
    {
        this.doProcess(req, res);
    }
    /**
     * Process an HTML get or post.
     * @exception ServletException From inherited class.
     * @exception IOException From inherited class.
     */
    public void doProcess(HttpServletRequest req, HttpServletResponse res) 
        throws ServletException, IOException
    {
        try {
            res.setDateHeader("Expires", 0);        // Make sure the browser never caches these requests
            res.setHeader("Pragma", "No-cache");
            res.setHeader("Cache-Control", "no-cache");
            m_servletTask.doProcess(this, req, res, null);
        } catch (Throwable ex)  {
            ex.printStackTrace();
        }
    }
    /**
     * Do any of the initial servlet stuff.
     * @param servletTask The calling servlet task.
     */
    public void initServletSession(ServletTask servletTask)
    {
    }
    /**
     * Set the content type for this type of servlet.
     */
    public void setContentType(HttpServletResponse res)
    {
        res.setContentType("text/html");
    }
    /**
     * Get the output stream.
     */
    public PrintWriter getOutputStream(HttpServletResponse res)
        throws IOException
    {
        return res.getWriter();
    }
    /**
     * Get the main screen (with the correct view factory!).
     */
    public TopScreen createTopScreen(RecordOwnerParent parent, FieldList recordMain, Object properties)
    {
        return null;//? new HtmlScreen(parent, recordMain, properties);
    }
    /**
     * Get the physical path for this internet path.
     */
    public String getRealPath(HttpServletRequest request, String strFilename)
    {
        return this.getServletContext().getRealPath(strFilename);
    }
}
