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
import java.io.StringWriter;

import org.jbundle.base.model.HtmlConstants;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.model.screen.ScreenComponent;


/**
 * This is the base screen for reports.
 * This class can simply output the record in report format.
 */
public class HReportScreen extends HDualReportScreen
{

    /**
     * Constructor.
     */
    public HReportScreen()
    {
        super();
    }
    /**
     * Constructor.
     * @param model The model object for this view object.
     * @param bEditableControl Is this control editable?
     */
    public HReportScreen(ScreenField model, boolean bEditableControl)
    {
        this();
        this.init(model, bEditableControl);
    }
    /**
     * Constructor.
     * @param model The model object for this view object.
     * @param bEditableControl Is this control editable?
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
     * Get the Forms param to be passed on submit.
     * <p>Typically, you want to output the HTML only, not the form.</p>
     * @return  The hidden "forms" param to be passed on submit (input/display/both/bothifdata).
     */
    public String getDefaultFormsParam()
    {
        return HtmlConstants.DISPLAY;
    }
    /**
     * Get this report's output as an HTML string.
     * @return The html for this control.
     */
    public String getHtmlControl()
    {
        StringWriter sw = new StringWriter();
        PrintWriter rw = new PrintWriter(sw);
        this.getScreenField().printData(rw, HtmlConstants.HTML_DISPLAY);      // DO print screen
        String string = sw.toString();
        return string;
    }
}
