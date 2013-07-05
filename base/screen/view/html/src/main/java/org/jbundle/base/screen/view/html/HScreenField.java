/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.view.html;

/**
 * @(#)ScreenField.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.io.PrintWriter;

import org.jbundle.base.model.HtmlConstants;
import org.jbundle.base.model.Utility;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.base.screen.view.yml.YScreenField;
import org.jbundle.model.DBException;
import org.jbundle.model.db.Convert;
import org.jbundle.model.screen.ScreenComponent;
import org.jbundle.thin.base.db.Converter;


/**
 * The base view for HTML components.
 */
public abstract class HScreenField extends YScreenField
{
    /**
     * Constructor.
     */
    public HScreenField()
    {
        super();
    }
    /**
     * Constructor.
     * @param model The model object for this view object.
     * @param bEditableControl Is this control editable?
     */
    public HScreenField(ScreenField model, boolean bEditableControl)
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
     * Get the HTML Alignment type.
     * @return the HTML alignment (as a string).
     */
    public String getControlAlignment()
    {
        return super.getControlAlignment();
    }
    /**
     * display this field's description in html format.
     * @param out The html out stream.
     * @param strFieldDesc The field description.
     * @param iHtmlAttribures The attributes.
     */
    public void printHtmlControlDesc(PrintWriter out, String strFieldDesc, int iHtmlAttributes)
    {
        if ((iHtmlAttributes & HtmlConstants.HTML_ADD_DESC_COLUMN) != 0)
            out.println("<td align=\"right\">" + strFieldDesc + "</td>");
    }
    /**
     * Display this field in html input format.
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
        out.println("<td><input type=\"" + strControlType + "\" name=\"" + strFieldName + "\" size=\"" + 
            strSize + "\" maxlength=\"" + strMaxSize + "\" value=\"" + strValue + "\"/></td>");
    }
    /**
     * Get the current string value in HTML.<p/>
     * May want to check GetRootScreen().GetScreenType() & INPUT/DISPLAY_MODE
     * @exception DBException File exception.
     * @param out The html out stream.
     * @param iHtmlAttribures The attributes.
     * @return true if any fields were found.
     */
    public boolean printData(PrintWriter out, int iHtmlAttributes)
    {
        String strFieldDesc = "&nbsp;";
        if (this.getScreenField().getConverter() != null)
            strFieldDesc = this.getScreenField().getConverter().getFieldDesc();
        this.printHtmlControlDesc(out, strFieldDesc, iHtmlAttributes);
        if (this.getScreenField().isEnabled() == false)
            iHtmlAttributes = iHtmlAttributes & (~HtmlConstants.HTML_INPUT);    // Display control
        if ((iHtmlAttributes & HtmlConstants.HTML_INPUT) != 0)
        {   // Input field
            String strFieldName = this.getHtmlFieldParam();
            Convert converter = this.getScreenField().getConverter();
            int iMaxSize = 10;  //?
            if (converter != null)
                iMaxSize = converter.getMaxLength();
            String strMaxSize = Integer.toString(iMaxSize);
            String strSize = "40";
            if (iMaxSize < 40)
                strSize = strMaxSize;
            String strValue = this.getScreenField().getSFieldValue(false, false);
        // Overriding methods will replace with: text int checkbox radio hidden float date url textbox
            String strControlType = this.getInputType(null);
            this.printInputControl(out, strFieldDesc, strFieldName, strSize, strMaxSize, strValue, strControlType, iHtmlAttributes);
        }
        else
            this.printDisplayControl(out);
        return true;
    }
    /**
     * Get the current string value in HTML.<p />
     * May want to check GetRootScreen().GetScreenType() & INPUT/DISPLAY_MODE
     * @param out The html out stream.
     * @exception DBException File exception.
     */
    public void printDisplayControl(PrintWriter out)
    {
        if (this.getScreenField().getConverter() == null)
            return;
        String strField = Utility.encodeXML(this.getScreenField().getSFieldValue(true, false));
        if ((strField == null) || (strField.length() == 0))
            strField = "&nbsp;";    //?"<br>";
        String strHyperlink = ((Converter)this.getScreenField().getConverter()).getHyperlink();
        if (strHyperlink != null) if (strHyperlink.length() > 0)
            strField = "<a href=\"" + strHyperlink + "\">" + strField + "<a>";
        String strAlignment = this.getControlAlignment();
        out.println("<td" + strAlignment + ">" + strField + "</td>");
    }
    /**
     * Get the field column headings in HTML.
     * @param out The html out stream.
     * @exception DBException File exception.
     */
    public void printHtmlHeading(PrintWriter out)
    {
        if (this.getScreenField().getConverter() == null)
            return;
        String strField = this.getScreenField().getConverter().getFieldDesc();
        if ((strField == null) || (strField.length() == 0))
            strField = "&nbsp;";    //?"<br>";
        out.println("<th>" + strField + "</th>");
    }
}
