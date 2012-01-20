/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field.convert;

/**
 * @(#)CheckConverter.java  0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import java.util.Map;

import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.ScreenModel;
import org.jbundle.base.field.StringField;
import org.jbundle.base.util.DBConstants;
import org.jbundle.model.db.Convert;
import org.jbundle.model.screen.ComponentParent;
import org.jbundle.model.screen.ScreenComponent;
import org.jbundle.model.screen.ScreenLoc;
import org.jbundle.thin.base.db.Converter;


/**
 * Set this string to this field if checked/unchecked.
 * This converter is used to set/clear a field of a string or
 * field value depending on the converter's state.
 * Note: This inherits from FieldDescConverter, so you can supply
 * an alternate description.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class CheckConverter extends FieldDescConverter
{
    /**
     * The string to set the converter to is set to true.
     */
    protected String m_strTargetValue = null;
    /**
     * The string to set this field if set to true.
     */
    protected BaseField m_fldTargetValue = null;
    /**
     * Set to the value if state is set to true (if this is true).
     */
    protected boolean m_bTrueIfMatch = false;
    /**
     * Add/Delete this string value to the target field (ie., Value is XORed).
     */
    protected Boolean m_boolMaskValue = Boolean.FALSE;

    /**
     * Constructor.
     */
    public CheckConverter()
    {
        super();
    }
    /**
     * Constructor.
     * @param converter The target converter to set to this string.
     * @param strTargetValue The string to set the converter to is set to true.
     * @param strAltFieldDesc An alternate description to display.
     * @param bTrueIfMatch If true, set to the alternate value.
     */
    public CheckConverter(Converter converter, String strTargetValue, String strAltFieldDesc, boolean bTrueIfMatch)
    {
        this();
        this.init(converter, strTargetValue, null, strAltFieldDesc, bTrueIfMatch, null);
    }
    /**
     * Constructor.
     * @param converter The target converter to set to this string.
     * @param fldTargetValue The string to set this field if set to true.
     * @param strAltFieldDesc An alternate description to display.
     * @param bTrueIfMatch If true, set to the alternate value.
     */
    public CheckConverter(Converter converter, BaseField fldTargetValue, String strAltFieldDesc, boolean bTrueIfMatch)
    {
        this();
        this.init(converter, null, fldTargetValue, strAltFieldDesc, bTrueIfMatch, null);
    }
    /**
     * Init this converter.
     * @param converter The target converter to set to this string.
     * @param strTargetValue The string to set the converter to is set to true.
     * @param fldTargetValue The string to set this field if set to true.
     * @param strAltFieldDesc An alternate description to display.
     * @param bTrueIfMatch If true, set to the alternate value.
     * @param boolMaskValue
     */
    public void init(Converter converter, String strTargetValue, BaseField fldTargetValue, String strAltFieldDesc, boolean bTrueIfMatch, Boolean boolMaskValue)
    {
        m_fldTargetValue = fldTargetValue;
        m_strTargetValue = strTargetValue;
        m_bTrueIfMatch = bTrueIfMatch;
        if (boolMaskValue != null)
            m_boolMaskValue = boolMaskValue;
        else
        {
            if ((converter != null)
                    && (converter.getField() instanceof StringField))
                m_boolMaskValue = Boolean.TRUE;    // String - default to true (mask)
            else
                m_boolMaskValue = Boolean.FALSE;     // Other - default to false (value = target)
        }
        super.init(converter, strAltFieldDesc, null);
    }
    /**
     * Free this converter.
     */
    public void free()
    {
        super.free();
        m_strTargetValue = null;
        m_fldTargetValue = null;
    }
    /**
     * Get the on/off state of this radio control.
     * @return true if the target string is in the field.
     */
    public boolean getState()
    {
        boolean bReturnValue;
        if (m_strTargetValue != null)
        {
            if (m_boolMaskValue.booleanValue())
                bReturnValue = (this.getString().indexOf(m_strTargetValue) != -1); // Is the target value in the field string
            else
                bReturnValue = m_strTargetValue.equals(this.toString()); // Is the target value in the field string
        }
        else if (m_fldTargetValue != null)
            bReturnValue = this.equals(m_fldTargetValue); // Is the target value in the field string
        else
            bReturnValue = this.getField().isNull(); // Is the field string null?
        if (m_bTrueIfMatch)
            return bReturnValue;
        else
            return !bReturnValue;
    }
    /**
     * For binary fields, set the current state.
     * @param state The state to set this field.
     * @param bDisplayOption Display changed fields if true.
     * @param iMoveMode The move mode.
     * @return The error code (or NORMAL_RETURN).
     */
    public int setState(boolean bState, boolean bDisplayOption, int iMoveMode)
    {
        if (m_strTargetValue != null)
        {
            String fieldString = this.getString();
            if (m_boolMaskValue.booleanValue())
            {
                int index = fieldString.indexOf(m_strTargetValue);
                if (bState == m_bTrueIfMatch)
                {   // Add target string to field!
                    if (index == -1) // Is the target value in the field string?
                    {   // No, Add it!
                        fieldString = m_strTargetValue + fieldString;        // Add the field to the string
                    }
                }
                else
                {   // Take target field out of field
                    if (index != -1)    // Is the target value in the field string
                    {   // Yes, Take it out!
                        fieldString = fieldString.substring(0, index) + fieldString.substring(index+1);   // Strip the field to the string
                    }
                }
            }
            else
                fieldString = m_strTargetValue; // Set to the value
            return this.setString(fieldString, bDisplayOption, iMoveMode);
        }
        else if (m_fldTargetValue != null)
        {
            if (bState == m_bTrueIfMatch)
            	return ((BaseField)this.getField()).moveFieldToThis(m_fldTargetValue, bDisplayOption, iMoveMode);
            else
                return ((BaseField)this.getField()).setString(null);	// Unchecked = clear it.
        }
        else    // Target = null
        {
            if (bState == m_bTrueIfMatch)
                return ((BaseField)this.getField()).setString(null);
            else
                return DBConstants.NORMAL_RETURN; //?
        }
    }
    /**
     * Set up the default control for this field (A SCheckBox).
     *  @param  itsLocation     Location of this component on screen (ie., GridBagConstraint).
     *  @param  targetScreen    Where to place this component (ie., Parent screen or GridBagLayout).
     *  @param  iDisplayFieldDesc Display the label? (optional).
     *  @return   Return the component or ScreenField that is created for this field.
     */
    public ScreenComponent setupDefaultView(ScreenLoc itsLocation, ComponentParent targetScreen, Convert converter, int iDisplayFieldDesc, Map<String, Object> properties)
    {
        return BaseField.createScreenComponent(ScreenModel.CHECK_BOX, itsLocation, targetScreen, converter, iDisplayFieldDesc, properties);
    }
}
