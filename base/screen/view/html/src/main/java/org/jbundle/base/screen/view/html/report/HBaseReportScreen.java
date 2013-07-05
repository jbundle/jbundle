/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.view.html.report;

/**
 * @(#)ScreenField.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.base.screen.view.html.HBaseGridScreen;
import org.jbundle.model.screen.ScreenComponent;

/**
 *  HTML Display screen (simply display some HTML in the content area of a screen).
 *  This screen serves a dual purpose; first, it allows the user to enter data in the parameter
 *  form (if isPrintReport() == false), then it outputs the report as Html (when isPrintReport() == true).
 *  WARNING: Be very careful; This screen is used differently when it is used for HTML or a Screen.
 *  When it is a screen, it just throws up an HTML Editor window and asks the http server for the
 *  URL that it is suppose to show.
 *  When it is running on the server, it actually formats and builds the html help page.
 */
public class HBaseReportScreen extends HBaseGridScreen
{

    /**
     * Constructor.
     */
    public HBaseReportScreen()
    {
        super();
    }
    /**
     * Constructor.
     * @param model The model object for this view object.
     * @param bEditableControl Is this control editable?
     */
    public HBaseReportScreen(ScreenField model, boolean bEditableControl)
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
}
