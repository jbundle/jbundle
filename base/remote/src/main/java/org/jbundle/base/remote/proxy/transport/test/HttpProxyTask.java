/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.remote.proxy.transport.test;

/**
 * @(#)DBServlet.java 0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jbundle.base.remote.proxy.transport.ProxyTask;
import org.jbundle.base.screen.control.servlet.BasicServlet;


/**
 * ServletTask.
 * 
 * This servlet is the main servlet.
 * <p>
 * The possible params are:
 * <pre>
 *  record - Create a default HTML screen for this record (Display unless "move" param)
 *  screen - Create this HTML screen
 *  limit - For Displays, limit the records displayed
 *  form - If "yes" display the imput form above the record display
 *  move - HTML Input screen - First/Prev/Next/Last/New/Refresh/Delete
 *  applet - applet, screen=applet screen
 *              applet params: archive/id/width/height/cabbase
 *  menu - Display this menu page
 * </pre>
 */
public class HttpProxyTask extends ProxyTask
{

    /**
     * Constructor.
     */
    public HttpProxyTask()
    {
        super();
    }
    /**
     * Constructor.
     */
    public HttpProxyTask(BasicServlet servlet, BasicServlet.SERVLET_TYPE servletType)
    {
        this();
        this.init(servlet, servletType);
    }
    /**
     * Constructor.
     */
    public void init(BasicServlet servlet, BasicServlet.SERVLET_TYPE servletType)
    {
        super.init(servlet, servletType);
    }
    /**
     * Get the next (String) param.
     * Typically this is overidden in the concrete implementation.
     * @param strName The param name (in most implementations this is optional).
     * @return The next param as a string.
     */
    public String getNextStringParam(InputStream in, String strName, Map<String, Object> properties)
    {
        String string = null;
        string = this.getProperty(strName);
/*        try {
            string = ((ObjectInputStream)in).readUTF();
        } catch (IOException ex)    {
            ex.printStackTrace();
            string = null;
        }
*/
        if (NULL.equals(string))
            string = null;
        return string;
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
