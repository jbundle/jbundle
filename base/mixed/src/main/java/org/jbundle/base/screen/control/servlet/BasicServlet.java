/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.control.servlet;

/**
 * @(#)DBServlet.java 0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
public interface BasicServlet extends javax.servlet.Servlet
{
    /**
     * Set the content type for this type of servlet.
     * (From the BasicServlet interface).
     * @param The http response to set.
     */
    public void setContentType(HttpServletResponse res);
    /**
     * Get the output stream.
     * (From the BasicServlet interface).
     * @param The http response to set.
     * @return The output stream.
     */
    public PrintWriter getOutputStream(HttpServletResponse res)
        throws IOException;
    /**
     * Get the main screen (with the correct view factory!).
     * (From the BasicServlet interface).
     * @param parent The record owner parent.
     * @param recordMain The main record.
     * @param properties The properties for this screen.
     * @return The top screen.
     */
    public TopScreen createTopScreen(RecordOwnerParent parent, FieldList recordMain, Object properties);
    /**
     * Get the physical path for this internet path.
     * (From the BasicServlet interface).
     * @param request The http request.
     * @param strFileName The file name to find in this context.
     * @return The physical path to this file.
     */
    public String getRealPath(HttpServletRequest request, String strFilename);
    /**
     * Do any of the initial servlet stuff.
     * @param servletTask The calling servlet task.
     */
    public void initServletSession(ServletTask servletTask);
}
