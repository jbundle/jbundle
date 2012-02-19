/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.control.servlet.html;

/**
 * @(#)DBServlet.java 0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jbundle.base.field.BaseField;
import org.jbundle.base.model.DBParams;
import org.jbundle.base.model.ScreenModel;
import org.jbundle.base.screen.control.servlet.BasicServlet;
import org.jbundle.base.screen.control.servlet.ServletTask;
import org.jbundle.model.PropertyOwner;
import org.jbundle.model.Task;
import org.jbundle.model.screen.ComponentParent;


/**
 * DBServlet
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
public class HTMLServlet extends BaseServlet
    implements BasicServlet
{
	private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     */
    public HTMLServlet()
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
        ServletTask.initServlet(this, BasicServlet.SERVLET_TYPE.HTML);
    }
    /**
     * Destroy this Servlet and any active applications.
     * This is only called when all users are done using this Servlet.
     */
    public void destroy()
    {
        super.destroy();
        ServletTask.destroyServlet();
    }
    /**
     * returns the servlet info
     */ 
    public String getServletInfo()
    {
        return "This the main servlet";
    }
    /**
     * service method
     *  Valid parameters:
     *      applet=appletclass  (defaults to tour.screen.SApplet.class)
     *          archive=
     *          id=
     *          width=
     *          height=
     *          record=appletrecordclass
     *          screen=appletscreenclass
     *      menu=menukey    (passes the key to the tour.agency.MenuScreen screen)
     *      record=recordclass  (display the default display screen)
     *      screen=screenclass  (display/input the screen data)
     *          bookmark=key    (display the record at this bookmark)
     *          move=First/Prev/Next/Last/New/Refresh/Delete (move from bookmark)
     *          form=input/display/both/bothifdata (Used for param display forms)
     *      title=htmltitle
     *      style=stylesheet (/css/styles/stylesheet.css)
     * @exception ServletException From inherited class.
     * @exception IOException From inherited class.
     */
//test  public static void main(args[])
    /**
     *  process an HTML get.
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
        ServletTask servletTask = new ServletTask(this, BasicServlet.SERVLET_TYPE.HTML);
        this.addBrowserProperties(req, servletTask);
        servletTask.doProcess(this, req, res, null);
        servletTask.free();
    }
    /**
     * Add the browser properties to this servlet task.
     * @param req
     * @param servletTask
     */
    public void addBrowserProperties(HttpServletRequest req, PropertyOwner servletTask)
    {
        String strBrowser = this.getBrowser(req);
        String strOS = this.getOS(req);
        servletTask.setProperty(DBParams.BROWSER, strBrowser);
        servletTask.setProperty(DBParams.OS, strOS);
    }
    /**
     * Do any of the initial servlet stuff.
     * @param servletTask The calling servlet task.
     */
    public void initServletSession(Task servletTask)
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
    public ComponentParent createTopScreen(Task task, Map<String,Object> properties)
    {
        if (properties == null)
            properties = new HashMap<String,Object>();
        properties.put(ScreenModel.VIEW_TYPE, ScreenModel.HTML_TYPE);
        properties.put(DBParams.TASK, task);
        ComponentParent topScreen = (ComponentParent)BaseField.createScreenComponent(ScreenModel.TOP_SCREEN, null, null, null, 0, properties);
        return topScreen;
    }
    /**
     * Get the physical path for this internet path.
     */
    public String getRealPath(HttpServletRequest request, String strFilename)
    {
        return this.getServletContext().getRealPath(strFilename);
    }
}
