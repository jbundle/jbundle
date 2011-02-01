package org.jbundle.base.screen.view.xml;

/**
 * @(#)ScreenField.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.io.PrintWriter;
import java.util.Vector;

import org.jbundle.base.field.BaseField;
import org.jbundle.base.screen.model.SPopupBox;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.base.util.DBConstants;
import org.jbundle.base.util.Utility;
import org.jbundle.model.DBException;
import org.jbundle.thin.base.db.Converter;


/**
 * A screen popup box.
 */
public class XPopupBox extends XScreenField
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
    public XPopupBox()
    {
        super();
    }
    /**
     * Constructor.
     */
    public XPopupBox(ScreenField model,boolean bEditableControl)
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

        Converter converter = this.getScreenField().getConverter();
        Object data = converter.getData();
        BaseField field = (BaseField)converter.getField();
        boolean bModifiedState = false;
        boolean[] states = null;
        if (field != null)
        {
            bModifiedState = field.isModified();
            states = field.setEnableListeners(false);
        }
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
        {
            field.setModified(bModifiedState);
            field.setEnableListeners(states);
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
    public void printInputControl(PrintWriter out, String strFieldDesc, String strFieldName, String strSize, String strMaxSize, String strValue, String strControlType, String strFieldType)
    {
        strValue = Utility.encodeXML(this.getScreenField().getSFieldValue(false, true));  // Need the RAW data.
        if (m_vDisplays == null)
            this.scanTableItems();
        out.println("<xfm:exclusiveSelect xform=\"form1\" ref=\"" + strFieldName + "\" style=\"list-ui:listbox;\">");
        out.println("   <xfm:caption>" + strFieldDesc + "</xfm:caption>");

        for (int index = 0; index < m_vDisplays.size(); index++)
        {
            String strField = (String)m_vValues.get(index);
            String strDisplay = (String)m_vDisplays.get(index);
            if (strValue.equals(strField))
//                out.println("<option value=\"" + strField + "\" selected>" + strDisplay);
                out.println("<xfm:item value=\"" + strField + "\">" + strDisplay + "</xfm:item>");
            else
                out.println("<xfm:item value=\"" + strField + "\">" + strDisplay + "</xfm:item>");
        }
        out.println("</xfm:exclusiveSelect>");
    }
    /**
     * Get the current string value in HTML.<p/>
     * May want to check GetRootScreen().GetScreenType() & kInput/DISPLAY_MODE
     * @exception DBException File exception.
     */
    public void printDisplayControl(PrintWriter out, String strFieldDesc, String strFieldName, String strFieldType)
    {
        String strData = Utility.encodeXML(this.getScreenField().getSFieldValue(true, false));
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
     * Print this field's data in XML format.
     * @return true if default params were found for this form.
     * @param out The http output stream.
     * @exception DBException File exception.
     */
    public boolean printData(PrintWriter out, int iPrintOptions)
    {
        boolean bFieldsFound = false;
        String strFieldName = this.getScreenField().getSFieldParam();

        String string = DBConstants.BLANK;
        int iIndex = 0;
        Converter converter = this.getScreenField().getConverter();
        if (m_vDisplays == null)
        {
            String strField = null;
            if (converter != null) if (converter.getField() != null)
                strField = converter.getField().getString();
            this.scanTableItems();
            if (converter != null) if (converter.getField() != null)
                converter.getField().setString(strField);
        }
        if (converter != null)
        {   // This is required for the display or popup fields because displayField() is never called to get the value.
            iIndex = converter.convertFieldToIndex();
            if (iIndex == -1)
            {
                return super.printData(out, iPrintOptions);
            }
            try {
                string = (String)m_vDisplays.get(iIndex);
            } catch (ArrayIndexOutOfBoundsException ex) {
                string = DBConstants.BLANK;
            }
        }

        String strFieldData = string; // this.getScreenField().getSFieldValue(true, false);
        if (this.getScreenField().getConverter() != null)
                strFieldData = Utility.encodeXML(strFieldData);
        out.println("    " + Utility.startTag(strFieldName) + strFieldData + Utility.endTag(strFieldName));
        return bFieldsFound;
    }
    /**
     * Get this control's value as it was submitted by the HTML post operation.
     * @return The value the field was set to.
     */
    public String getSFieldProperty(String strFieldName)
    {
        String strValue = super.getSFieldProperty(strFieldName);
        // Hack - For some weird reason DOJO returns the display value instead of the 'option' value. Need to look up the field value.
        Converter converter = this.getScreenField().getConverter();
        String strConverter = converter.toString();
        if (((strValue != null) && (strValue.equals(strConverter)))
        	|| ((strValue == null) && (strConverter == null)))
        {	// The value has not changed, return it!
        	if (converter.getField() != null)	// Always
        		return converter.getField().toString();
        }
        for (int index = 0; index < SPopupBox.MAX_POPUP_ITEMS; index++)
        {
            String strDisplay = converter.convertIndexToDisStr(index);
            if (((strValue != null) && (strValue.equals(strDisplay)))
                	|| ((strValue == null) && (strDisplay == null)))
            {
            	if (converter.convertIndexToField(index, DBConstants.DISPLAY, DBConstants.SCREEN_MOVE) == DBConstants.NORMAL_RETURN)
            		return converter.getField().toString();
            }
            if (index > 0)
                if ((strDisplay == null) || (strDisplay.length() == 0))
                    break;      // Far Enough
        }
        return strValue;
    }
}
