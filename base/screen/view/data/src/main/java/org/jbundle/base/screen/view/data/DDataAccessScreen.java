/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.view.data;

/**
 * @(#)ScreenField.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.base.screen.view.ScreenFieldViewAdapter;
import org.jbundle.model.DBException;
import org.jbundle.model.screen.ScreenComponent;


/**
 * DDataAccessScreen is the base view class for raw data types
 * for the DataAccessScreen model. You must override this class
 * with a concrete class for a specific data type.
 */
public class DDataAccessScreen extends ScreenFieldViewAdapter
{

    /**
     * Constructor.
     */
    public DDataAccessScreen()
    {
        super();
    }
    /**
     * Constructor.
     * @param model The DataAccessScreen model.
     * @param bEditableControl If true, this view is editable.
     */
    public DDataAccessScreen(ScreenField model, boolean bEditableControl)
    {
        this();
        this.init(model, bEditableControl);
    }
    /**
     * Constructor.
     * @param model The DataAccessScreen model.
     * @param bEditableControl If true, this view is editable.
     */
    public void init(ScreenComponent model, boolean bEditableControl)
    {
        super.init(model, bEditableControl);
    }
    /**
     * Free.
     */
    public void free()
    {
        super.free();
    }
    /**
     * Code to output the raw data to the destination.
     * @exception DBException File exception.
     */
    public void printReport(PrintWriter out, ResourceBundle reg)
        throws DBException
    {
        // Don't do anything here
    }
    /**
     * Process an HTML get or post.
     * You must override this method.
     * @param req The servlet request.
     * @param res The servlet response object.
     * @exception ServletException From inherited class.
     * @exception IOException From inherited class.
     */
    public void sendData(Object req, Object res)
        throws Exception, IOException
    {
        this.sendData((HttpServletRequest)req, (HttpServletResponse)res);
    }
    /**
     * Process an HTML get or post.
     * You must override this method.
     * @param req The servlet request.
     * @param res The servlet response object.
     * @exception ServletException From inherited class.
     * @exception IOException From inherited class.
     */
    public void sendData(HttpServletRequest req, HttpServletResponse res) 
        throws ServletException, IOException
    {
        // You must override this.
    }
}
