/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.db;

/**
 * @(#)FieldConverter.java  0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import org.jbundle.model.db.Convert;
import org.jbundle.model.screen.ScreenComponent;

/**
 * The base converter for fields.
 * The class maintains the converter chain.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class LinkedConverter extends Converter
{
    /**
     * The next converter in the converter chain.
     */
    protected Converter m_converterNext = null;

    /**
     * Constructor.
     */
    public LinkedConverter()
    {
        super();
    }
    /**
     * Constructor.
     * @param converter The next converter in the converter chain.
     */
    public LinkedConverter(Converter converter)
    {
        this();
        this.init(converter);
    }
    /**
     * Initialize this converter.
     * @param converter The next converter in the converter chain.
     */
    public void init(Converter converter)
    {
        super.init(converter);
        m_converterNext = converter;
    }
    /**
     * Free this converter.
     */
    public void free()
    {
        super.free();
        m_converterNext = null;
    }
    /**
     * Add this component to the components displaying this field.
     * @param Object sField The screen component.. either a awt.Component or a ScreenField.
     */
    public void addComponent(Object screenField)
    {
        if (this.getNextConverter() != null)
            this.getNextConverter().addComponent(screenField);
        else
            super.addComponent(screenField);
    }
    /**
     * Remove this control from this field's control list.
     * @param Object sField The screen component.. either a awt.Component or a ScreenField
     */
    public void removeComponent(Object screenField)
    {
        if (this.getNextConverter() != null)
        {
            FieldInfo field = this.getField();
            this.getNextConverter().removeComponent(screenField);
            // **START SPECIAL CODE** This code is for a specific special case.
            // This code is for the special case when one converter is used by two SFields
            // I Don't free this converter if there is still an SField pointing to me.
            if (field != null)
            {
                for (int i = 0; ; i++)
                {
                    Object screenField2 = field.getComponent(i);
                    if (!(screenField2 instanceof ScreenComponent))
                        break;      // End of loop
                    if (screenField2 != screenField)
                    {   // Okay, this field has another SField, go through the converters and make sure this one is not referenced.
                        Convert converter = ((ScreenComponent)screenField2).getConverter();
                        while ((converter != null) && (converter != field))
                        {
                            if (converter == this)
                                return;     // DO NOT Free this converter, it is referenced by another ScreenField!
                            if (converter instanceof LinkedConverter)
                                converter = ((LinkedConverter)converter).getNextConverter();
                            else
                                converter = null;
                        }
                    }
                }
            }

        }
        super.removeComponent(screenField);   // delete this
    }
    /**
     * Convert the field's code to the display's index (for popup).
     * @return The value in this field to a 0 based index in a table.
     */
    public int convertFieldToIndex()                    // init this field override for other value
    {       // Must be overidden
        if (this.getNextConverter() != null)
            return this.getNextConverter().convertFieldToIndex();
        else
            return super.convertFieldToIndex();
    }
    /**
     * Convert this index to a display field.
     * @param index The index to convert.
     * @return The display string.
     */
    public String convertIndexToDisStr(int index)
    {
        if (this.getNextConverter() != null)
            return this.getNextConverter().convertIndexToDisStr(index);   // By default, use the same string
        else
            return super.convertIndexToDisStr(index);
    }
    /**
     * Convert the display's index to the field value and move to field.
     * @param index The index to convert an set this field to.
     * @param bDisplayOption If true, display the change in the converters.
     * @param iMoveMove The type of move.
     */
    public int convertIndexToField(int index, boolean bDisplayOption, int iMoveMode)
    {       // Must be overidden
        if (this.getNextConverter() != null)
            return this.getNextConverter().convertIndexToField(index, bDisplayOption, iMoveMode);
        else
            return super.convertIndexToField(index, bDisplayOption, iMoveMode);
    }
    /**
     * Get the data on the end of this converter chain.
     * @return The raw data.
     */
    public Object getData() 
    {
        if (this.getNextConverter() != null)
            return this.getNextConverter().getData();
        else
            return super.getData();
    }
    /**
     * For binary fields, set the current state.
     * @param state The state to set this field.
     * @param bDisplayOption Display changed fields if true.
     * @param iMoveMode The move mode.
     * @return The error code (or NORMAL_RETURN).
     */
    public int setData(Object state, boolean bDisplayOption, int iMoveMode)
    {       // Must be overidden
        if (this.getNextConverter() != null)
            return this.getNextConverter().setData(state, bDisplayOption, iMoveMode);
        else
            return super.setData(state, bDisplayOption, iMoveMode);
    }
    /**
     * Get the field on the end of this converter chain.
     * @return The field on the end of the chain.
     */
    public FieldInfo getField()
    {
        if (this.getNextConverter() != null)
            return this.getNextConverter().getField();
        else
            return super.getField();
    }   // Return the source BaseField (if linked to a field!)
    /**
     * Get the field description.
     * @return The field description.
     */
    public String getFieldDesc() 
    {       // Must be overidden
        if (this.getNextConverter() != null)
            return this.getNextConverter().getFieldDesc();
        else
            return super.getFieldDesc();
    }
    /**
     * Get the image for Buttons and Bitmaps and drag cursors.
     * @return The bitmap for this converter.
     */
    public String getBitmap()
    {
        if (this.getNextConverter() != null)
            return this.getNextConverter().getBitmap();
        else
            return super.getBitmap();
    }
    /**
     * Get the current string length of the data at the end of this converter chain.
     * @return The string length (or -1 if unknown).
     */
    public int getLength() 
    {       // Must be overidden
        if (this.getNextConverter() != null)
            return this.getNextConverter().getLength();
        else
            return super.getLength();
    }
    /**
     * Get the maximum length of this field.
     * @return The maximum field length.
     */
    public int getMaxLength() 
    {       // Must be overidden
        if (this.getNextConverter() != null)
            return this.getNextConverter().getMaxLength();
        else
            return super.getMaxLength();
    }
    /**
     * Get the next Converter in the chain.
     */
    public Converter getNextConverter()
    {
        return m_converterNext;
    }
    /**
     * Get the next Converter in the chain.
     */
    public void setNextConverter(Converter converter)
    {
        m_converterNext = converter;
    }
    /**
     * For binary fields, set the current state.
     * @param state The state to set this field.
     * @param bDisplayOption Display changed fields if true.
     * @param iMoveMode The move mode.
     * @return The error code (or NORMAL_RETURN).
     */
    public int setState(boolean state, boolean bDisplayOption, int iMoveMode)
    {       // Must be overidden
        if (this.getNextConverter() != null)
            return this.getNextConverter().setState(state, bDisplayOption, iMoveMode);
        else
            return super.setState(state, bDisplayOption, iMoveMode);
    }
    /**
     * For binary fields, return the current state.
     * @param True is this field is true.
     */
    public boolean getState()               // init this field override for other value
    {       // Must be overidden
        if (this.getNextConverter() != null)
            return this.getNextConverter().getState();
        else
            return super.getState();
    }
    /**
     * Convert and move string to this field.
     * Override this method to convert the String to the actual Physical Data Type.
     * @param strString the state to set the data to.
     * @param bDisplayOption Display the data on the screen if true.
     * @param iMoveMode INIT, SCREEN, or READ move mode.
     * @return The error code (or NORMAL_RETURN).
     */
    public int setString(String strString, boolean bDisplayOption, int iMoveMode)    // init this field override for other value
    {
        if (this.getNextConverter() != null)
            return this.getNextConverter().setString(strString, bDisplayOption, iMoveMode);  // Convert to internal rep and return
        else
            return super.setString(strString, bDisplayOption, iMoveMode);
    }
    /**
     * Retrieve (in string format) from this field.
     * @return The data in string format.
     */
    public String getString()
    {
        if (this.getNextConverter() != null)
            return this.getNextConverter().getString();   // This will work for text fields that require no conversion
        else
            return super.getString();
    }
    /**
     * For numeric fields, set the current value.
     * Override this method to convert the value to the actual Physical Data Type.
     * @param bState the state to set the data to.
     * @param bDisplayOption Display the data on the screen if true.
     * @param iMoveMode INIT, SCREEN, or READ move mode.
     * @return The error code.
     */
    public int setValue(double value, boolean bDisplayOption, int iMoveMode)
    {
        if (this.getNextConverter() != null)
            return this.getNextConverter().setValue(value, bDisplayOption, iMoveMode);
        else
            return super.setValue(value, bDisplayOption, iMoveMode);
    }
    /**
     * For numeric fields, get the current value.
     * @return The converter value as a double.
     */
    public double getValue()
    {
        if (this.getNextConverter() != null)
            return this.getNextConverter().getValue();
        else
            return super.getValue();
    }
}
