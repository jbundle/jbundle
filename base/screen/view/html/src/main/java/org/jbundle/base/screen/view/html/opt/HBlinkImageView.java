/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.view.html.opt;

/**
 * @(#)ScreenField.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.io.PrintWriter;

import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.base.screen.view.html.HScreenField;
import org.jbundle.model.screen.ScreenComponent;


/**
 * Image view with multiple image display.
 * Not implemented for HTML.
 */
public class HBlinkImageView extends HScreenField
{
    /**
     * Constructor.
     */
    public HBlinkImageView()
    {
        super();
    }
    /**
     * Constructor.
     * @param model The model object for this view object.
     * @param bEditableControl Is this control editable?
     */
    public HBlinkImageView(ScreenField model, boolean bEditableControl)
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
     * display this field in html input format.
     * @param out The html out stream.
     * @param strFieldDesc The field description.
     * @param strFieldName The field name.
     * @param strSize The control size.
     * @param strMaxSize The string max size.
     * @param strValue The default value.
     * @param strControlType The control type.
     * @param iHtmlAttribures The attributes.
     */
    public void printInputControl(PrintWriter out, String strFieldDesc, String strFieldName, String strSize, String strMaxSize, String strValue, String strControlType, int iHtmlAttributes)
    {
        String strImage = "";
        out.println("<td>" + strImage + "</td>");
    }
}
