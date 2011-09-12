/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.view.html.report;

/**
 * @(#)ScreenField.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.io.PrintWriter;

import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.base.screen.view.html.HBaseScreen;
import org.jbundle.base.screen.view.html.HScreenField;
import org.jbundle.base.util.HtmlConstants;
import org.jbundle.model.DBException;


/**
 * ScreenField - This is the information which tells the system about a field on the
 *  screen... where it is, what type it is, and the file and field position.
 *  <p> TODO:
 *  1. Scroll into view if focused
 *  2. Tab = tab, even for JTextAreas (Could be a swing bug)
 */
public class HHeadingScreen extends HBaseScreen
{

    /**
     * Constructor.
     */
    public HHeadingScreen()
    {
        super();
    }
    /**
     * Constructor.
     * @param model The model object for this view object.
     * @param bEditableControl Is this control editable?
     */
    public HHeadingScreen(ScreenField model, boolean bEditableControl)
    {
        this();
        this.init(model, bEditableControl);
    }
    /**
     * Constructor.
     * @param model The model object for this view object.
     * @param bEditableControl Is this control editable?
     */
    public void init(ScreenField model, boolean bEditableControl)
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
     * Output this screen using HTML.
     * @exception DBException File exception.
     */
    public void printDataStartForm(PrintWriter out, int iPrintOptions)
    {
        if ((iPrintOptions & HtmlConstants.DETAIL_SCREEN) != 0)
            out.println("<tr>\n<td colspan=\"20\">");
        out.println("<table border=\"0\" cellspacing=\"10\">\n<tr>");
    }
    /**
     * Output this screen using HTML.
     * @exception DBException File exception.
     */
    public void printScreenFieldData(ScreenField sField, PrintWriter out, int iPrintOptions)
    {
        ((HScreenField)sField.getScreenFieldView()).printHtmlHeading(out);
        ((HScreenField)sField.getScreenFieldView()).printDisplayControl(out);
    }
    /**
     * Output this screen using HTML.
     * @exception DBException File exception.
     */
    public void printDataEndForm(PrintWriter out, int iPrintOptions)
    {
        out.println("</tr>\n</table>");
        if ((iPrintOptions & HtmlConstants.DETAIL_SCREEN) != 0)
            out.println("</td>\n</tr>");
    }
    /**
     * Display the start form in input format.
     * @param out The out stream.
     * @param iPrintOptions The view specific attributes.
     */
    public void printDataStartField(PrintWriter out, int iPrintOptions)
    {
//        out.println("<td>");
    }
    /**
     * Display the start form in input format.
     * @param out The out stream.
     * @param iPrintOptions The view specific attributes.
     */
    public void printDataEndField(PrintWriter out, int iPrintOptions)
    {
//        out.println("</td>");
    }
}
