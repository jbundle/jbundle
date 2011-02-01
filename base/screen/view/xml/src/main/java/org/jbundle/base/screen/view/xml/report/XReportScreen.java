package org.jbundle.base.screen.view.xml.report;

/**
 * @(#)ScreenField.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.base.util.HtmlConstants;

/**
 * This is the base screen for reports.
 * This class can simply output the record in report format.
 */
public class XReportScreen extends XDualReportScreen
{

    /**
     * Constructor.
     */
    public XReportScreen()
    {
        super();
    }
    /**
     * Constructor.
     */
    public XReportScreen(ScreenField model, boolean bEditableControl)
    {
        this();
        this.init(model, bEditableControl);
    }
    /**
     * Constructor.
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
     * Get the Forms param to be passed on submit.
     * <p>Typically, you want to output the HTML only, not the form.</p>
     * @return  The hidden "forms" param to be passed on submit (input/display/both/bothifdata).
     */
    public String getDefaultFormsParam()
    {
        return HtmlConstants.DISPLAY;
    }
}
