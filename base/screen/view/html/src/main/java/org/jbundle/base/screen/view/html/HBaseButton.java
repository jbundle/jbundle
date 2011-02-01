package org.jbundle.base.screen.view.html;

/**
 * @(#)ScreenField.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.io.PrintWriter;

import org.jbundle.base.screen.model.ScreenField;


/**
 * The base class for all buttons and check boxes.
 */
public class HBaseButton extends HScreenField
{

    /**
     * Constructor.
     */
    public HBaseButton()
    {
        super();
    }
    /**
     * Constructor.
     * @param model The model object for this view object.
     * @param bEditableControl Is this control editable?
     */
    public HBaseButton(ScreenField model,boolean bEditableControl)
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
        strControlType = "checkbox";
        String strDefault = "";
        if (strValue.length() > 0) if (strValue.charAt(0) == 'Y')
            strDefault = " checked";
        out.println("<td><input type=\"" + strControlType + "\" name=\"" + strFieldName + "\" value=\"Yes\"" + strDefault + "/></td>");
    }
    /**
     * Do I create a separate control for the description for this type of control.
     * Generally yes, but you may want to override in checkboxes or buttons to include the description in the control.
     * @return True as buttons have the description contained in the control.
     */
    public boolean getSeparateFieldDesc()
    {
        return false; // **Switch this line to use separate desc (labels) for checkboxes**
    }
}
