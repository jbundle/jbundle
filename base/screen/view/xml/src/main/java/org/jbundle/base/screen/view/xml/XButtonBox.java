/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.view.xml;

/**
 * @(#)ScreenField.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.io.PrintWriter;

import org.jbundle.base.db.Record;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.DBParams;
import org.jbundle.base.model.HtmlConstants;
import org.jbundle.base.model.Utility;
import org.jbundle.base.screen.model.GridScreen;
import org.jbundle.base.screen.model.SButtonBox;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.model.DBException;
import org.jbundle.model.screen.ScreenComponent;
import org.jbundle.thin.base.util.ThinMenuConstants;


/**
 * Implements a standard button.
 */
public class XButtonBox extends XBaseButton
{

    /**
     * Constructor.
     */
    public XButtonBox()
    {
        super();
    }
    /**
     * Constructor.
     * @param model The model object for this view object.
     * @param bEditableControl Is this control editable?
     */
    public XButtonBox(ScreenField model, boolean bEditableControl)
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
     * Display this sub-control in html input format?
     * @param iPrintOptions The view specific print options.
     * @return True if this sub-control is printable.
     */
    public boolean isPrintableControl(int iPrintOptions)
    {
        String strButtonDesc = ((SButtonBox)this.getScreenField()).getButtonDesc();
        if ((strButtonDesc == null)
            && (((SButtonBox)this.getScreenField()).getImageButtonName() != null)
        	&& (!this.isSingleDataImage()))
                return false;   // Image only buttons are ignored in HTML
        return super.isPrintableControl(iPrintOptions);   // Return true
    }
    /**
     * Special case - if this button is linked to data and this is the only sfield, then display it.
     * @return
     */
    public boolean isSingleDataImage()
    {
    	if (this.getScreenField().getConverter() != null)
        	if (this.getScreenField().getConverter().getField() != null)
            	if (this.getScreenField().getConverter().getField().getComponent(1) == null)
    		return true;
    	return false;
    }
    /**
     * display this field in html input format.
     */
    public void printInputControl(PrintWriter out, String strFieldDesc, String strFieldName, String strSize, String strMaxSize, String strValue, String strControlType, String strFieldType)
    {
        if (!HtmlConstants.BUTTON.equalsIgnoreCase(strControlType))
        {
            strFieldName = DBParams.COMMAND;
            strControlType = HtmlConstants.SUBMIT;
        }
        String strButtonDesc = ((SButtonBox)this.getScreenField()).getButtonDesc();
        if ((strButtonDesc == null)
            && (((SButtonBox)this.getScreenField()).getImageButtonName() != null))
        {
            if (this.getScreenField().getParentScreen() instanceof GridScreen)
            { // These are command buttons such as "Form" or "Detail"
                GridScreen gridScreen = (GridScreen)this.getScreenField().getParentScreen();
                Record record = gridScreen.getMainRecord();
                String strRecordClass = record.getClass().getName();
                String strImage = DBConstants.FORM;
                if (((SButtonBox)this.getScreenField()).getImageButtonName() != null)
                    strImage = ((SButtonBox)this.getScreenField()).getImageButtonName();
                String strButtonCommand = ((SButtonBox)this.getScreenField()).getButtonCommand();
                if ((strButtonCommand == null) || (strButtonCommand.length() == 0))
                    strButtonCommand = ThinMenuConstants.FORM;
//x                String strCommand = "&" + DBParams.COMMAND + "=" + strButtonCommand;
//?                try {
//?                    out.println("<td align=\"center\" valign=\"center\"><a href=\"" + "?record=" + strRecordClass + strCommand + "&" + DBConstants.STRING_OBJECT_ID_HANDLE + "=" + URLEncoder.encode(strBookmark, DBConstants.URL_ENCODING) + "\"><IMG SRC=\"" + HtmlConstants.IMAGE_DIR + "buttons/" + strImage + ".gif\" width=\"16\" height=\"16\" alt=\"Open this record\"></a></td>");
//?                } catch (java.io.UnsupportedEncodingException ex) {
//?                    ex.printStackTrace();
//?                }
                if (strButtonDesc == null)
                    strButtonDesc = DBConstants.BLANK;
                strButtonCommand = strButtonCommand + "&amp;record=" + strRecordClass + "&amp;objectID=";
                out.println(" <xfm:trigger name=\"" + strImage + "\" ref=\"" + strFieldName + "\" command=\"" + strButtonCommand + "\">");
                out.println("   <xfm:hint>" + strFieldDesc + "</xfm:hint>");
                out.println("   <xfm:image>" + strImage + "</xfm:image>");
                out.println("   <xfm:caption>" + strButtonDesc + "</xfm:caption>");
                out.println(" </xfm:trigger>");
            }
            else
            {
                strButtonDesc = ((SButtonBox)this.getScreenField()).getImageButtonName();
                strControlType = "image";
                strFieldType = "image";
//?             ? = this.getScreenField()).getImageButtonName();
                super.printInputControl(out, strFieldDesc, strFieldName, strSize, strMaxSize, strValue, strControlType, strFieldType);
            }
        }
        else
        {
            strControlType = "submit";
            if (strButtonDesc == null)
            {
                strButtonDesc = ((SButtonBox)this.getScreenField()).getButtonCommand();
//?                strControlType = "trigger";
            }
            String strImage = "Form";
            if (((SButtonBox)this.getScreenField()).getImageButtonName() != null)
                strImage = ((SButtonBox)this.getScreenField()).getImageButtonName();
//?            else if (!strButtonDesc.equals(((SButtonBox)this.getScreenField()).getButtonCommand()))
//?                strControlType = "trigger";
            if (this.getScreenField().getConverter() == null)
                strFieldName = strButtonDesc;   // Since this isn't tied to a field, set this to the command name.
//x            out.println("<td><input type=\"" + strControlType + "\" name=\"" + strFieldName + "\" value=\"" + strButtonDesc + "\"/></td>");
            out.println(" <xfm:" + strControlType + " name=\"" + strButtonDesc + "\" ref=\"" + strFieldName + "\">");
            out.println("   <xfm:hint>" + strFieldDesc + "</xfm:hint>");
            out.println("   <xfm:image>" + strImage + "</xfm:image>");
            out.println("   <xfm:caption>" + strButtonDesc + "</xfm:caption>");
            out.println(" </xfm:" + strControlType + ">");
        }
    }
    /**
     * Display this field in html input format.
     */
    public boolean printData(PrintWriter out, int iPrintOptions)
    {
        String strFieldName = this.getScreenField().getSFieldParam();
        String strButtonDesc = ((SButtonBox)this.getScreenField()).getButtonDesc();
        if ((strButtonDesc == null)
            && (((SButtonBox)this.getScreenField()).getImageButtonName() != null))
        {
            if (this.getScreenField().getParentScreen() instanceof GridScreen)
            { // These are command buttons such as "Form" or "Detail"
                GridScreen gridScreen = (GridScreen)this.getScreenField().getParentScreen();
                Record record = gridScreen.getMainRecord();
                String strBookmark = DBConstants.BLANK;
                try {
                    strBookmark = record.getHandle(DBConstants.OBJECT_ID_HANDLE).toString();
                } catch (DBException ex)    {
                    ex.printStackTrace();
                }
                if (this.isSingleDataImage())
                	strBookmark = this.getScreenField().getConverter().toString();
                
                out.println("    " + Utility.startTag(strFieldName) + strBookmark + Utility.endTag(strFieldName));
                return true;
            }
            else
                return super.printData(out, iPrintOptions);
        }
        else
            return super.printData(out, iPrintOptions);
    }
    /**
     * Get the current string value in HTML.
     * @exception DBException File exception.
     */
    public void printDisplayControl(PrintWriter out, String strFieldDesc, String strFieldName, String strFieldType)
    {
        if (this.getScreenField().getConverter() != null)
        {
            this.printInputControl(out, strFieldDesc, strFieldName, null, null, null, "button", strFieldType);   // Button that does nothing?
        }
        else if (this.getScreenField().getParentScreen() instanceof GridScreen)
        { // These are command buttons such as "Form" or "Detail"
            this.printInputControl(out, null, strFieldName, null, null, null, "button", strFieldType);   // Button that does nothing?
        }
    }
    /**
     * Get the rich text type for this control.
     * @return The type (Such as HTML, Date, etc... very close to HTML control names).
     */
    public String getInputType(String strViewType)
    {
        return HtmlConstants.BUTTON;
    }
}
