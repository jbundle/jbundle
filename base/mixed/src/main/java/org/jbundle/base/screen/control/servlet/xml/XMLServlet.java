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

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.jbundle.base.field.BaseField;
import org.jbundle.base.model.DBParams;
import org.jbundle.base.model.ScreenModel;
import org.jbundle.base.screen.control.servlet.html.HTMLServlet;
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
    public void initServletSession(Task servletTask)
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
    public ComponentParent createTopScreen(Task task, Map<String,Object> properties)
    {
        if (properties == null)
            properties = new HashMap<String,Object>();
        properties.put(ScreenModel.VIEW_TYPE, ScreenModel.XML_TYPE);
        properties.put(DBParams.TASK, task);
        ComponentParent topScreen = (ComponentParent)BaseField.createScreenComponent(ScreenModel.TOP_SCREEN, null, null, null, 0, properties);
        return topScreen;
    }
}
