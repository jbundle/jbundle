package org.jbundle.base.screen.control.xslservlet;

/**
 * @(#)DBServlet.java 0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jbundle.base.screen.control.servlet.ServletTask;
import org.jbundle.base.screen.control.servlet.BaseHttpTask.SERVLET_TYPE;


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
public class CocoonServletWrapper extends Object
{
/* org.apache.cocoon.servlet.CocoonServlet
{
	private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     *
    public CocoonServletWrapper()
    {
        super();
    }
    /**
     * init method.
     * @exception ServletException From inherited class.
     *
    public void init(ServletConfig config) throws ServletException
    {
        super.init(config);
        ServletTask.initServlet(this, SERVLET_TYPE.COCOON);
    }
    /**
     * Destroy this Servlet and any active applications.
     * This is only called when all users are done using this Servlet.
     *
    public void destroy()
    {
        super.destroy();
        ServletTask.destroyServlet();
    }
    /**
     *  process an HTML post.
     * @exception ServletException From inherited class.
     * @exception IOException From inherited class.
     *
    public void service(HttpServletRequest req, HttpServletResponse resp) 
        throws ServletException, IOException
    {
        super.service(req, resp);
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
}
