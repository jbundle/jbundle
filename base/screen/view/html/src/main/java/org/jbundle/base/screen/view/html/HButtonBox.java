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
import java.net.URLEncoder;

import org.jbundle.base.db.Record;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.DBParams;
import org.jbundle.base.model.HtmlConstants;
import org.jbundle.base.screen.model.GridScreen;
import org.jbundle.base.screen.model.SButtonBox;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.model.DBException;
import org.jbundle.model.screen.ScreenComponent;
import org.jbundle.thin.base.util.ThinMenuConstants;


/**
 * Implements a standard button.
 */
public class HButtonBox extends HBaseButton
{

    /**
     * Constructor.
     */
    public HButtonBox()
    {
        super();
    }
    /**
     * Constructor.
     * @param model The model object for this view object.
     * @param bEditableControl Is this control editable?
     */
    public HButtonBox(ScreenField model, boolean bEditableControl)
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
     * Get the field column headings in HTML.
     * @param out The html out stream.
     * @exception DBException File exception.
     */
    public void printHtmlHeading(PrintWriter out)
    {
        if (this.getScreenField().getConverter() != null)
            super.printHtmlHeading(out);
        else if (this.getScreenField().getParentScreen() instanceof GridScreen)
        { // These are command buttons such as "Form" or "Detail"
            String strImage = "Form";
            if (((SButtonBox)this.getScreenField()).getImageButtonName() != null)
                strImage = ((SButtonBox)this.getScreenField()).getImageButtonName();
            out.println("<th align=\"center\" valign=\"center\"><img src=\"" + HtmlConstants.IMAGE_DIR + "buttons/" + strImage + ".gif\" width=\"16\" height=\"16\" alt=\"Open this record\"></th>");
        }
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
                String strBookmark = DBConstants.BLANK;
                try {
                    strBookmark = record.getHandle(DBConstants.OBJECT_ID_HANDLE).toString();
                } catch (DBException ex)    {
                    ex.printStackTrace();
                }
                String strImage = "form";
                if (((SButtonBox)this.getScreenField()).getImageButtonName() != null)
                    strImage = ((SButtonBox)this.getScreenField()).getImageButtonName();
                String strButtonCommand = ((SButtonBox)this.getScreenField()).getButtonCommand();
                if ((strButtonCommand == null) || (strButtonCommand.length() == 0))
                    strButtonCommand = ThinMenuConstants.FORM;
                String strCommand = "&" + DBParams.COMMAND + "=" + strButtonCommand;
                try {
                    out.println("<td align=\"center\" valign=\"center\"><a href=\"" + "?record=" + strRecordClass + strCommand + "&" + DBConstants.STRING_OBJECT_ID_HANDLE + "=" + URLEncoder.encode(strBookmark, DBConstants.URL_ENCODING) + "\"><IMG SRC=\"" + HtmlConstants.IMAGE_DIR + "buttons/" + strImage + ".gif\" width=\"16\" height=\"16\" alt=\"Open this record\"></a></td>");
                } catch (java.io.UnsupportedEncodingException ex) {
                    ex.printStackTrace();
                }
            }
            else
            {
                strButtonDesc = ((SButtonBox)this.getScreenField()).getImageButtonName();
                String strCommand = "<td><img src=\"" + HtmlConstants.IMAGE_DIR + "buttons/" + 
                    ((SButtonBox)this.getScreenField()).getImageButtonName() + ".gif\" width=\"16\" height=\"16\" border=\"0\"></td>";
                out.println(strCommand);
            }
        }
        else
        {
            if (strButtonDesc == null)
                strButtonDesc = ((SButtonBox)this.getScreenField()).getButtonCommand();
            out.println("<td><input type=\"" + strControlType + "\" name=\"" + strFieldName + "\" value=\"" + strButtonDesc + "\"/></td>");
        }
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
            && (((SButtonBox)this.getScreenField()).getImageButtonName() != null))
                return false;   // Image only buttons are ignored in HTML
        return super.isPrintableControl(iPrintOptions);   // Return true
    }
    /**
     * Get the current string value in HTML.
     * @param out The html out stream.
     * @exception DBException File exception.
     */
    public void printDisplayControl(PrintWriter out)
    {
        if (this.getScreenField().getConverter() != null)
        {
            String strFieldName = this.getScreenField().getConverter().getField().getFieldName(false, false);
            this.printInputControl(out, null, strFieldName, null, null, null, HtmlConstants.BUTTON, 0);   // Button that does nothing?
        }
        else if (this.getScreenField().getParentScreen() instanceof GridScreen)
        { // These are command buttons such as "Form" or "Detail"
            this.printInputControl(out, null, null, null, null, null, HtmlConstants.BUTTON, 0);   // Button that does nothing?
        }
    }
}
