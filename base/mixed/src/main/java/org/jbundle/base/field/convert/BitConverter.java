/*

 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field.convert;

/**
 * @(#)BitConverter.java    0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import java.util.Map;

import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.ScreenModel;
import org.jbundle.model.db.Convert;
import org.jbundle.model.screen.ComponentParent;
import org.jbundle.model.screen.ScreenComponent;
import org.jbundle.model.screen.ScreenLoc;
import org.jbundle.thin.base.db.Converter;


/**
 * Set/Clear this bit depending on the state of the next converter.
 * (ie., bit 0 = L.O. Bit of 32bit word).
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class BitConverter extends FieldConverter
{
    /**
     * The bit to set.
     */
    protected int m_iBitNumber = 0;
    /**
     * Return true if this bit is on (if this variable is true).
     */
    protected boolean m_bTrueIfMatch = false;
    /**
     * Return true if this bit is on (if this variable is true).
     */
    protected boolean m_bTrueIfNull = false;

    /**
     * Constructor.
     */
    public BitConverter()
    {
        super();
    }
    /**
     * Constructor.
     * @param converter The field to set the bit in (Should be a NumberField).
     * @param iBitNumber The bit number to set with this converter (0 = L.O. bit).
     * @param trueIfMatch Return true if this bit is on (if this variable is true).
     */
    public BitConverter(Converter converter, int iBitNumber, boolean trueIfMatch)
    {
        this();
        this.init(converter, iBitNumber, trueIfMatch, false);
    }
    /**
     * Constructor.
     * @param converter The field to set the bit in (Should be a NumberField).
     * @param iBitNumber The bit number to set with this converter (0 = L.O. bit).
     * @param trueIfMatch Return true if this bit is on (if this variable is true).
     */
    public BitConverter(Converter converter, int iBitNumber, boolean trueIfMatch, boolean bTrueIfNull)
    {
        this();
        this.init(converter, iBitNumber, trueIfMatch, bTrueIfNull);
    }
    /**
     * Initialize this converter.
     * @param converter The field to set the bit in (Should be a NumberField).
     * @param iBitNumber The bit number to set with this converter (0 = L.O. bit).
     * @param trueIfMatch Return true if this bit is on (if this variable is true).
     */
    public void init(Converter converter, int iBitNumber, boolean trueIfMatch, boolean bTrueIfNull)
    {
        super.init(converter);
        m_iBitNumber = iBitNumber;
        m_bTrueIfMatch = trueIfMatch;
        m_bTrueIfNull = bTrueIfNull;
    }
    /**
     * Free this converter.
     */
    public void free()
    {
        super.free();
    }
    /**
     * For binary fields, return the current state.
     * Gets the state of the target bit.
     * @param True is this field is true.
     */
    public boolean getState()
    {
        if (this.getData() == null)
            return m_bTrueIfNull;
        int fieldValue = (int)this.getValue();
        boolean returnValue = false;
        if ((fieldValue & (1 << m_iBitNumber)) != 0)
            returnValue = true;
        if (m_bTrueIfMatch)
            return returnValue;
        else
            return !returnValue;
    }
    /**
     * For binary fields, set the current state.
     * Sets the target bit to the state.
     * @param state The state to set this field.
     * @param bDisplayOption Display changed fields if true.
     * @param iMoveMode The move mode.
     * @return The error code (or NORMAL_RETURN).
     */
    public int setState(boolean bState, boolean bDisplayOption, int iMoveMode)
    {
        int iFieldValue = (int)this.getValue();
        if (!m_bTrueIfMatch)
            bState = !bState; // Do opposite operation
        if (bState)
            iFieldValue |= (1 << m_iBitNumber);     // Set the bit
        else
            iFieldValue &= ~(1 << m_iBitNumber);    // Clear the bit
        return this.setValue(iFieldValue, bDisplayOption, iMoveMode);
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
