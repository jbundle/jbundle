/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field;

/**
 * @(#)NumberField.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import java.util.Map;

import org.jbundle.base.db.Record;
import org.jbundle.base.screen.model.TopScreen;
import org.jbundle.model.Task;
import org.jbundle.model.db.Convert;
import org.jbundle.model.screen.ComponentParent;
import org.jbundle.model.screen.ScreenComponent;
import org.jbundle.model.screen.ScreenLoc;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.screen.BaseApplet;


/**
 * The base field for all numbers. 
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public abstract class NumberField extends BaseField
{
	private static final long serialVersionUID = 1L;

	/**
     * Constructor.
     */
    public NumberField()
    {
        super();
    }
    /**
     * Constructor.
     * @param record The parent record.
     * @param strName The field name.
     * @param iDataLength The maximum string length (pass -1 for default).
     * @param strDesc The string description (usually pass null, to use the resource file desc).
     * @param strDefault The default value (if object, this value is the default value, if string, the string is the default).
     */
    public NumberField(Record record, String strName, int iDataLength, String strDesc, Object strDefault)
    {
        this();
        this.init(record, strName, iDataLength, strDesc, strDefault);
    }
    /**
     * Initialize the object.
     * @param record The parent record.
     * @param strName The field name.
     * @param iDataLength The maximum string length (pass -1 for default).
     * @param strDesc The string description (usually pass null, to use the resource file desc).
     * @param strDefault The default value (if object, this value is the default value, if string, the string is the default).
     */
    public void init(Record record, String strName, int iDataLength, String strDesc, Object strDefault)
    {
        super.init(record, strName, iDataLength, strDesc, strDefault);
        m_ibScale = 0;
    }
    /**
     * Convert the field's code to the display's index (for popup).
     * @return The value in this field to a 0 based index in a table.
     */
    public int convertFieldToIndex()
    {
        return this.convertValueToIndex(this.getValue()); // Convert this field to an index and return
    }
    /**
     * Convert the display's index to the field value and move to field.
     * @param index The index to convert an set this field to.
     * @param bDisplayOption If true, display the change in the converters.
     * @param iMoveMove The type of move.
     */
    public int convertIndexToField(int index, boolean bDisplayOption, int iMoveMode)
    {
        return this.setValue(this.convertIndexToValue(index), bDisplayOption, iMoveMode); // Convert to value and move to this field
    }
    /**
     * Convert this index to a double value for this field.
     * @param index The index to convert.
     * @return The value this field should be set to for this index.
     */
    public double convertIndexToValue(int index)
    {
        return index;       // By default, don't do any conversion
    }
    /**
     * Convert the field's code to the display's index (for popup) (usually overidden).
     * NOTE This is almost always overidden!!!
     * @param value The value to convert.
     * @return The index this field should have for this value.
     */
    public int convertValueToIndex(double value)
    {
        return (int)value;      // By default, don't do any conversion
    }
    /**
     * Get the HTML Input Type.
     * @return The HTML type (int).
     */
    public String getInputType(String strViewType)
    {
        if (TopScreen.HTML_TYPE.equalsIgnoreCase(strViewType))
            return "int";
        else //if (TopScreen.XML_TYPE.equalsIgnoreCase(strViewType))
            return super.getInputType(strViewType);
    }
    /**
     * Get the number of fractional digits.
     * @return The scale.
     */
    public int getScale()
    {
        return m_ibScale;
    }
    /**
     * For binary fields, return the current state.
     * @retrun The state.
     */
    public boolean getState()
    {
        if (this.getValue() == 0)
            return false;
        else
            return true;
    }
    /**
     * Retrieve (in string format) from this field.
     * Data is already in binary format, so convert it and return the string.
     * @return This field as a string.
     */
    public String getString() 
    {
        Object tempBinary = this.getData();   // Get the physical data
        if (tempBinary != null)
            return this.binaryToString(tempBinary);
        else
            return Constants.BLANK;                         // Clear BaseField
    }
    /**
     * For binary fields, set the current state.
     * @param state The state to set this field.
     * @param bDisplayOption Display changed fields if true.
     * @param iMoveMode The move mode.
     * @return The error code (or NORMAL_RETURN).
     */
    public int setState(boolean state, boolean bDisplayOption, int moveMode)
    {
        double value = 0;
        if (state)
            value = 1;
        return this.setValue(value, bDisplayOption, moveMode);  // Move value to this field
    }
    /**
     * Convert and move string to this field.
     * This Data is in a binary format, so convert it and move it.
     * @param bState the state to set the data to.
     * @param bDisplayOption Display the data on the screen if true.
     * @param iMoveMode INIT, SCREEN, or READ move mode.
     * @return The error code.
     */
    public int setString(String strField, boolean bDisplayOption, int iMoveMode)                 // init this field override for other value
    {
        int iFieldLength = 0;
        Object objTempBinary = null;
        if (strField != null)
        {
            iFieldLength = strField.length();
            int iMaxLength = this.getMaxLength();
            if ((iFieldLength > iMaxLength) || (iFieldLength > 40))
                iFieldLength = iMaxLength;
            try   {
                objTempBinary = this.stringToBinary(strField);
            } catch (Exception ex)  {
                String strError = ex.getMessage();
                if (strError == null)
                    strError = ex.getClass().getName();
                Task task = null;
                if (this.getRecord() != null)
                    if (this.getRecord().getRecordOwner() != null)
                        task = this.getRecord().getRecordOwner().getTask();
                if (task == null)
                    task = BaseApplet.getSharedInstance();
                if (task == null)
                    return -1;
                return task.setLastError(strError);
            }
        }
        return this.setData(objTempBinary, bDisplayOption, iMoveMode);
    }
    /**
     * Set up the default screen control for this field (A SNumberText).
     * @param itsLocation Location of this component on screen (ie., GridBagConstraint).
     * @param targetScreen Where to place this component (ie., Parent screen or GridBagLayout).
     * @param converter The converter to set the screenfield to.
     * @param iDisplayFieldDesc Display the label? (optional).
     * @return Return the component or ScreenField that is created for this field.
     */
    public ScreenComponent setupDefaultView(ScreenLoc itsLocation, ComponentParent targetScreen, Convert converter, int iDisplayFieldDesc, Map<String, Object> properties)
    {
        return createScreenComponent(ScreenModel.NUMBER_TEXT, itsLocation, targetScreen, converter, iDisplayFieldDesc, properties);
    }
    /**
     * Get this field in SQL format.
     * ie., number 1,234.56 -> 1234.56.
     * @return This field as A SQL string.
     */
    public String getSQLString()
    {
        return this.stripNonNumeric(super.getSQLString());      // By Default
    }
    /**
     * Convert this string to this field's binary data format.
     * You MUST override this method.
     * @param tempString A string to be converted to this field's binary data.
     * @return The physical data converted from this string (must be the raw data class).
     */
    public abstract Object stringToBinary(String tempString) throws Exception;
    /**
     * Convert this field's binary data to a string.
     * You MUST override this method.
     * @param tempBinary The physical data convert to a string (must be the raw data class).
     * @return A display string representing this binary data.
     */
    public abstract String binaryToString(Object tempBinary); 
}
