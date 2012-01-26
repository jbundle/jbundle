/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.view.html;

/**
 * @(#)ScreenField.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.io.PrintWriter;
import java.util.Vector;

import org.jbundle.base.field.BaseField;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.Utility;
import org.jbundle.base.screen.model.SPopupBox;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.model.DBException;
import org.jbundle.model.db.Convert;
import org.jbundle.model.screen.ScreenComponent;


/**
 * A screen popup box.
 */
public class HPopupBox extends HScreenField
{
    /**
     * Display values.
     */
    protected Vector<String> m_vDisplays = null;
    /**
     * Field values.
     */
    protected Vector<String> m_vValues = null;

    /**
     * Constructor.
     */
    public HPopupBox()
    {
        super();
    }
    /**
     * Constructor.
     * @param model The model object for this view object.
     * @param bEditableControl Is this control editable?
     */
    public HPopupBox(ScreenField model, boolean bEditableControl)
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
        if (m_vDisplays == null)
            if (model.getConverter().getString() == null)
                this.scanTableItems();
    }
    /**
     * Free.
     */
    public void free()
    {
        super.free();
    }
    /**
     * Scan through the items and cache the values and display strings.
     */
    public void scanTableItems()
    {
        m_vDisplays = new Vector<String>();
        m_vValues = new Vector<String>();
        String strField = null;

        Convert converter = this.getScreenField().getConverter();
        Object data = converter.getData();
        BaseField field = (BaseField)converter.getField();
        boolean bModifiedState = false;
        if (field != null)
            bModifiedState = field.isModified();
        for (int index = 0; index < SPopupBox.MAX_POPUP_ITEMS; index++)
        {
            String strDisplay = converter.convertIndexToDisStr(index);
            boolean bModified = false;
            if (field != null)
                bModified = field.isModified();
            converter.convertIndexToField(index, DBConstants.DONT_DISPLAY, DBConstants.READ_MOVE);  // Don't change
            if (field != null)
                field.setModified(bModified);
            if (converter.getField() != null)
                strField = converter.getField().getString();
            else
                strField = Integer.toString(index);
            if (index > 0)
                if ((strDisplay == null) || (strDisplay.length() == 0))
                    break;      // Far Enough
            m_vDisplays.add(strDisplay);
            m_vValues.add(strField);
        }
        converter.setData(data, DBConstants.DONT_DISPLAY, DBConstants.READ_MOVE);  // Restore to original value
        if (field != null)
            field.setModified(bModifiedState);
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
        strValue = Utility.encodeXML(this.getScreenField().getSFieldValue(false, true));  // Need the RAW data.
        if (m_vDisplays == null)
            this.scanTableItems();
        out.println("<td><select name=" + strFieldName + ">");

        for (int index = 0; index < m_vDisplays.size(); index++)
        {
            String strField = (String)m_vValues.get(index);
            String strDisplay = (String)m_vDisplays.get(index);
            if (strValue.equals(strField))
                out.println("<option value=\"" + strField + "\" selected>" + strDisplay);
            else
                out.println("<option value=\"" + strField + "\">" + strDisplay);
        }
        out.println("</select></td>");
    }
    /**
     * Get the current string value in HTML.<p/>
     * May want to check GetRootScreen().GetScreenType() & kInput/DISPLAY_MODE
     * @param out The html out stream.
     * @exception DBException File exception.
     */
    public void printDisplayControl(PrintWriter out)
    {
        Convert converter = this.getScreenField().getConverter();
        if (m_vDisplays == null)
        {
            String strField = null;
            if (converter != null) if (converter.getField() != null)
                strField = converter.getField().getString();
            this.scanTableItems();
            if (converter != null) if (converter.getField() != null)
                converter.getField().setString(strField);
        }
        String string = DBConstants.BLANK;
        int iIndex = 0;
        if (converter != null)
        {   // This is required for the display or popup fields because displayField() is never called to get the value.
            iIndex = converter.convertFieldToIndex();
            if (iIndex == -1)
            {
                super.printDisplayControl(out);
                return;
            }
            try {
                string = (String)m_vDisplays.get(iIndex);
            } catch (ArrayIndexOutOfBoundsException ex) {
                string = "&nbsp;";
            }
        }
        out.println("<td>" + string + "</td>");
    }
}
