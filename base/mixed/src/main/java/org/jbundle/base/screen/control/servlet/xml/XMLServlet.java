/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.control.servlet.xml;

/**
 * @(#)DBServlet.java 0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import javax.servlet.http.HttpServletResponse;

import org.jbundle.base.screen.control.servlet.ServletTask;
import org.jbundle.base.screen.control.servlet.html.HTMLServlet;
import org.jbundle.base.screen.model.TopScreen;
import org.jbundle.model.RecordOwnerParent;
import org.jbundle.thin.base.db.FieldList;


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
public class XMLServlet extends HTMLServlet
{
	private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     */
    public XMLServlet()
    {
        super();
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
        res.setContentType("text/xml");
    }
    /**
     * Get the main screen (with the correct view factory!).
     */
    public TopScreen createTopScreen(RecordOwnerParent parent, FieldList recordMain, Object properties)
    {
        return new XmlScreen(parent, recordMain, properties);
    }
}
