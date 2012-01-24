/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.message.trx.transport.html;

/**
 * @(#)DBServlet.java 0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jbundle.base.db.Record;
import org.jbundle.base.message.trx.message.TrxMessageHeader;
import org.jbundle.base.model.DBParams;
import org.jbundle.base.model.RecordOwner;
import org.jbundle.base.screen.control.servlet.html.HTMLServlet;
import org.jbundle.model.Task;
import org.jbundle.model.main.msg.db.MessageLogModel;
import org.jbundle.thin.main.msg.db.MessageLog;


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
public class MessageServlet extends HTMLServlet
{
    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     */
    public MessageServlet()
    {
        super();
    }
    /**
     * Process an HTML get or post.
     * @exception ServletException From inherited class.
     * @exception IOException From inherited class.
     */
    public void doProcess(HttpServletRequest req, HttpServletResponse res) 
        throws ServletException, IOException
    {
        super.doProcess(req, res);
    }
    /**
     * Do any of the initial servlet stuff.
     * @param servletTask The calling servlet task.
     */
    public void initServletSession(Task servletTask)
    {
        String strTrxID = servletTask.getProperty(TrxMessageHeader.LOG_TRX_ID);
        if (strTrxID != null)
        {   // Good, they are referencing a transaction (access the transaction properties).
            MessageLogModel recMessageLog = (MessageLogModel)Record.makeRecordFromClassName(MessageLog.THICK_CLASS, (RecordOwner)servletTask.getApplication().getSystemRecordOwner());
            try {
                recMessageLog = recMessageLog.getMessageLog(strTrxID);
                if (recMessageLog != null)
                {
                    servletTask.setProperty(TrxMessageHeader.LOG_TRX_ID, strTrxID);
                    String strHTMLScreen = recMessageLog.getProperty("screen" /*ScreenMessageTransport.SCREEN_SCREEN */);
                    if (strHTMLScreen != null)  // Special case, html screen is different from java screen.
                        servletTask.setProperty(DBParams.SCREEN, strHTMLScreen);
                }
            } finally {
                recMessageLog.free();
            }
        }  
    }
}
