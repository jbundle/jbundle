package org.jbundle.base.screen.control.servlet;

/**
 * @(#)DBServlet.java 0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jbundle.base.screen.model.BaseScreen;
import org.jbundle.base.screen.model.MenuScreen;
import org.jbundle.base.screen.model.TopScreen;
import org.jbundle.base.util.DBParams;
import org.jbundle.base.util.Debug;
import org.jbundle.model.DBException;
import org.jbundle.model.RecordOwnerParent;
import org.jbundle.model.Task;
import org.jbundle.thin.base.util.RecordOwnerCollection;


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
public class ServletTask extends BaseHttpTask
    implements Task
{
    /**
     * Needed to get the properties.
     */
    protected HttpServletRequest m_req = null;
    /**
     * Children record owners.
     */
    protected RecordOwnerCollection m_recordOwnerCollection = null;

    /**
     * Constructor.
     */
    public ServletTask()
    {
        super();
    }
    /**
     * Constructor.
     */
    public ServletTask(BasicServlet servlet, SERVLET_TYPE servletType)
    {
        this();
        this.init(servlet, servletType);
    }
    /**
     * Constructor.
     */
    public void init(BasicServlet servlet, SERVLET_TYPE servletType)
    {
        m_recordOwnerCollection = new RecordOwnerCollection(this);
        super.init(servlet, servletType);
    }
    /**
     * Is this task shared between all sessions on this server?
     * If so, I can't use the properties, since they are not unique to this user's task.
     * @return true If is is shared.
     */
    public boolean isShared()
    {
        return false;   // A servlet task is unique for each http session.
    }
    /**
     * Free all the resources belonging to this applet. If all applet screens are closed, shut down the applet.
     */
    public void free()
    {
    	if (m_recordOwnerCollection != null)
    		m_recordOwnerCollection.free();
    	m_recordOwnerCollection = null;
        super.free();   // Remove this session from the list
    }
    /**
     * Process an HTML get or post.
     * @exception ServletException From inherited class.
     * @exception IOException From inherited class.
     */
    public void doProcess(BasicServlet servlet, HttpServletRequest req, HttpServletResponse res, PrintWriter outExt) 
        throws ServletException, IOException
    {
        this.parseArgs(req);    // Parameters are now available to all children of this Task.

        // First see if this is an active session
        if (m_application == null)
            m_application = this.findApplication(req, res);   // Always (note: This task is added to the application tasks)
        else
            Debug.doAssert(false);

        String strUserID = m_application.getProperty(DBParams.USER_ID);
        
        servlet.initServletSession(this);

        TopScreen parentScreen = servlet.createTopScreen(this, null, null);
        BaseScreen screen = parentScreen.getScreen(null, null);
        if (screen != null)
        {
            screen = screen.doServletCommand(parentScreen);  // Move the input params to the record fields
            if (screen == null)
            {   // null = display Default Display Form
                screen = BaseScreen.makeNewScreen(MenuScreen.class.getName(), null, parentScreen, 0, null, true);
            }
        }
        if (screen != null)
        	if (this.getProperty(DBParams.DATATYPE) == null)
        		screen = parentScreen.checkSecurity(screen, parentScreen);  // Screen specified
        if (((strUserID == null) && (m_application.getProperty(DBParams.USER_ID) != null))
            || (!strUserID.equals(m_application.getProperty(DBParams.USER_ID))))
        {   // Special case, the screen changed the user.
            m_application = this.changeCookie((ServletApplication)m_application, req, res);
        }

        servlet.setContentType(res);
        PrintWriter out = outExt;
        if (screen == null)
        {
        	if (out != null)
        		out.println("Error: default screen not available");
            return;
        }
        try
        { // create and execute the query
            // if no parameters, just print the form
            String strDatatype = this.getProperty(DBParams.DATATYPE); // Raw data (such as in image from the DB)
            if (strDatatype == null)
            {
                if (out == null)
                    out = servlet.getOutputStream(res); // Always
                screen.printReport(out);
            }
            else
            {       // Raw data output
                ((org.jbundle.base.screen.view.data.DDataAccessScreen)screen.getScreenFieldView()).sendData(req, res);
            }
            screen.free();
            screen = null;
        }
        catch (DBException ex) {
            if (out != null)
            {
                out.println("<hr>*** SQLException caught ***<p>");
                out.println("Message:  " + ex.getMessage() + "<br>");
                ex.printStackTrace(out);
            }
            ex.printStackTrace();
        }
        catch (java.lang.Exception ex) {
            ex.printStackTrace();
        } finally {
        	if (outExt == null)
        		if (out != null)
        			out.close();
        }
            // I know this is called in destroy(), but I'm being careful (if server forgets to call)
        m_application.removeTask(this);
        m_application = null; // Do not free the application... the HttpSession will
        m_properties = null;    // Not needed anymore
    }
    /**
     *  Set the argument properties.
     */
    public void parseArgs(HttpServletRequest req)
    {
        m_req = req;
    }
    /**
     * Return the 
     */
    public HttpServletRequest getServletRequest()
    {
        return m_req;
    }
    /**
     * Add this record owner to my list.
     * @param recordOwner The recordowner to add
     */
    public boolean addRecordOwner(RecordOwnerParent recordOwner)
    {
    	return m_recordOwnerCollection.addRecordOwner(recordOwner);
    }
    /**
     * Remove this record owner to my list.
     * @param recordOwner The recordowner to remove.
     */
    public boolean removeRecordOwner(RecordOwnerParent recordOwner)
    {
    	return m_recordOwnerCollection.removeRecordOwner(recordOwner);
    }
}
