/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.view.xml;

/**
 * @(#)ScreenField.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.io.PrintWriter;

import org.jbundle.base.field.HtmlField;
import org.jbundle.base.field.XMLPropertiesField;
import org.jbundle.base.field.XmlField;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.ScreenFieldView;
import org.jbundle.base.model.ScreenModel;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.base.screen.view.zml.ZScreenField;
import org.jbundle.base.util.Utility;
import org.jbundle.base.util.XMLTags;
import org.jbundle.model.DBException;
import org.jbundle.model.db.Convert;
import org.jbundle.model.screen.ScreenComponent;


/**
 * The base view for XML components.
 */
public abstract class XScreenField extends ZScreenField
{
    public static final String DEFAULT_CONTROL_TYPE = "textbox";
    public static final String DEFAULT_CONTROL_LENGTH = "40";
    
    /**
     * Constructor.
     */
    public XScreenField()
    {
        super();
    }
    /**
     * Constructor.
     * @param model The model object for this view object.
     * @param bEditableControl Is this control editable?
     */
    public XScreenField(ScreenField model, boolean bEditableControl)
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
     * Get the current string value in XML.<p/>
     * May want to check GetRootScreen().GetScreenType() & kInput/DISPLAY_MODE
     * @exception DBException File exception.
     * @return true if any fields were found.
     */
    public boolean printControl(PrintWriter out, int iPrintOptions)
    {
        if (!this.getScreenField().isInputField())
            return false;
        if (this.getScreenField().isToolbar())
            return false;
        String strFieldDesc = DBConstants.BLANK;
        if (this.getScreenField().getConverter() != null)
            strFieldDesc = this.getScreenField().getConverter().getFieldDesc();
        String strFieldName = this.getScreenField().getSFieldParam();
        String strControlType = this.getInputType(null);
        String strFieldType = this.getInputType(ScreenModel.DOJO_TYPE);
        if ((this.getScreenField().isEnabled() == true) && (!this.getScreenField().getParentScreen().isPrintReport()))
        {   // Input field and not printing a report
            Convert converter = this.getScreenField().getConverter();
            int iMaxSize = 10;
            if (converter != null)
                iMaxSize = converter.getMaxLength();
            String strMaxSize = Integer.toString(iMaxSize);
            String strSize = DEFAULT_CONTROL_LENGTH;
            if (iMaxSize < 40)
                strSize = strMaxSize;
            String strValue = this.getScreenField().getSFieldValue(true, false);
        // Overriding methods will replace with: text int checkbox radio hidden float date url textbox
            strFieldDesc = Utility.replace(strFieldDesc, " ", "\240");  // spaces -> nbsp
            this.printInputControl(out, strFieldDesc, strFieldName, strSize, strMaxSize, strValue, strControlType, strFieldType);
        }
        else
            this.printDisplayControl(out, strFieldDesc, strFieldName, strFieldType);
        return true;
    }
    /**
     * Print this field's data in XML format.
     * @return true if default params were found for this form.
     * @param out The http output stream.
     * @exception DBException File exception.
     */
    public boolean printData(PrintWriter out, int iPrintOptions)
    {
        boolean bFieldsFound = false;
        String strFieldName = this.getScreenField().getSFieldParam();
        String strFieldData = this.getScreenField().getSFieldValue(true, false);
        if ((this.getScreenField().getConverter() != null)
            && (!(this.getScreenField().getConverter().getField() instanceof XmlField))
            && (!(this.getScreenField().getConverter().getField() instanceof HtmlField))
            && (!(this.getScreenField().getConverter().getField() instanceof XMLPropertiesField)))
                strFieldData = Utility.encodeXML(strFieldData);
        out.println("    " + Utility.startTag(strFieldName) + strFieldData + Utility.endTag(strFieldName));
        return bFieldsFound;
    }
    /**
     * Display the start form in input format.
     * @param out The out stream.
     * @param iPrintOptions The view specific attributes.
     */
    public void printControlStartForm(PrintWriter out, int iPrintOptions)
    {
        out.println(Utility.startTag(XMLTags.CONTROLS));
    }
    /**
     * Display the start form in input format.
     * @param out The out stream.
     * @param iPrintOptions The view specific attributes.
     */
    public void printControlEndForm(PrintWriter out, int iPrintOptions)
    {
        out.println(Utility.endTag(XMLTags.CONTROLS));
    }
    /**
     * Display this field in XML input format.
     * @param strFieldType The field type
     */
    public void printInputControl(PrintWriter out, String strFieldDesc, String strFieldName, String strSize, String strMaxSize, String strValue, String strControlType, String strFieldType)
    {
        out.println(" <xfm:" + strControlType + " xform=\"form1\" ref=\"" + strFieldName + "\" cols=\"" + strSize + "\" type=\"" + strFieldType + "\">");
        out.println("   <xfm:caption>" + strFieldDesc + "</xfm:caption>");
        out.println(" </xfm:" + strControlType + ">");
    }
    /**
     * Get the HTML Alignment type.
     * @return The data alignment.
     */
    public String getControlAlignment()
    {
        return super.getControlAlignment();
    }
    /**
     * Get the current string value in HTML.<p/>
     * May want to check GetRootScreen().GetScreenType() & kInput/DISPLAY_MODE
     * @param strFieldType The field type
     * @exception DBException File exception.
     */
    public void printDisplayControl(PrintWriter out, String strFieldDesc, String strFieldName, String strFieldType)
    {
        if (this.getScreenField().getConverter() == null)
            return;
        String strData = Utility.encodeXML(this.getScreenField().getSFieldValue(true, false));
        if ((strData == null) || (strData.length() == 0))
            strData = DBConstants.BLANK;  //?"<br>";
//+        String strHyperlink = this.getScreenField().getConverter().getHyperlink();
//+     if (strHyperlink != null) if (strHyperlink.length() > 0)
//+         strField = "<A HREF=\"" + strHyperlink + "\">" + strField + "<A>";
        String strAlignment = this.getControlAlignment();
        if (strAlignment.length() > 0)
            strAlignment = "align=\"" + strAlignment + "\" ";
        if (strFieldName.length() > 0)
            strFieldName = "ref=\"" + strFieldName + "\"";
        out.println("<fo:block " + strAlignment + strFieldName + ">" + strData);
        out.println("   <xfm:caption>" + strFieldDesc + "</xfm:caption>");
        out.println("</fo:block>");
    }
    /**
     * Do I create a separate control for the description for this type of control.
     * Generally yes, but you may want to override in checkboxes or buttons to include the description in the control.
     * @return True as buttons have the description contained in the control.
     */
    public boolean getSeparateFieldDesc()
    {
        return false; // A field desc is never necessary for screen views, as the xsl:caption on the control contains the desc.
    }
    /**
     * Get the rich text type for this control.
     * @return The type (Such as HTML, Date, etc... very close to HTML control names).
     */
    public String getInputType(String strViewType)
    {
        if (ScreenModel.DOJO_TYPE.equalsIgnoreCase(strViewType))
            strViewType = ScreenModel.HTML_TYPE;     // Temporary (return field type)
        return super.getInputType(strViewType);
    }
}
