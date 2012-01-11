/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.model.db;

import org.jbundle.model.Freeable;

/**
 * @(#)Converter.java 0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */



/**
 * Converter converts the screen data representation to the field's binary representation and back.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public interface Convert
    extends Freeable
{
    /**
     * Initialize this converter.
     * @param obj Not used in converter.
     */
    public void init(Object obj);
    /**
     * This screen component is displaying this field.
     * @param Object sField The screen component.. either a awt.Component or a ScreenField
     */
    public void addComponent(Object sField);
    /**
     * Remove this control from this field's control list.
     * @param Object sField The screen component.. either a awt.Component or a ScreenField
     */
    public void removeComponent(Object screenField);
    /**
     * Get the field on the end of this converter chain.
     * @return FieldInfo The field.
     */
    public Field getField();
    /**
     * Get the first converter for this field (must be linked to at least one ScreenComponent for this to work).
     * @return The converter object (or this if there is none).
     */
    public Convert getFieldConverter();
    /**
     * Get this field's name.
     * @return The name of the field on the end of the converter chain.
     */
    public String getFieldName();
    /**
     * Get the field description.
     * Usually overidden.
     * @return The description of the field on the end of the converter chain.
     */
    public String getFieldDesc();
    /**
     * Get current length of this field converted to a string.
     * (For StringField, returns fCurrent, for OtherFields returns fMax.
     * ... this routine is used to set up a buffer to receive the field, w/o wasting space!)
     * Usually overidden.
     * @return The current length (0 if null data, -1 if unknown).
     */
    public int getLength();
    /**
     * Get the maximum length of this field.
     * Usually overidden.
     * @return The maximum length of this field (0 if unknown).
     */
    public int getMaxLength();
    /**
     * Get the binary image of this field's data.
     * @return The raw data.
     */
    public Object getData();
    /**
     * Move data to this field (don't override this).
     * This calls setData with DISPLAY and SCREEN_MOVE.
     * @param object The data to set the field to.
     * @return The error code.
     */
    public int setData(Object object);
    /**
     * Move the physical binary data to this field.
     * @param vpData The data to set the field to.
     * @param bDisplayOption Display the data on the screen if true.
     * @param iMoveMode INIT, SCREEN, or READ move mode.
     * @return The error code.
     */
    public int setData(Object vpData, boolean bDisplayOption, int iMoveMode);
    /**
     * Retrieve (in string format) from this field.
     * Override this method to convert the actual Physical Data Type to a String.
     * The default implementation (here) assumes the physical data is in string format.
     * Same as toString().
     * @return The data in string format.
     * @see toString().
     */
    public String getString();
    /**
     * Override Object.toString to convert this field to a String.
     * @return The data in string format.
     * @see getString().
     */
    public String toString ();
    /**
     * Convert this string and set the data to this field (don't override this).
     * This calls setString with DISPLAY and SCREEN_MOVE.
     * @param string the string to set the data to.
     * @return The error code.
     */
    public int setString(String string);
    /**
     * Convert and move string to this field.
     * Override this method to convert the String to the actual Physical Data Type.
     * @param strString the string to set the data to.
     * @param bDisplayOption Display the data on the screen if true.
     * @param iMoveMode INIT, SCREEN, or READ move mode.
     * @return The error code.
     */
    public int setString(String strString, boolean bDisplayOption, int moveMode);
    /**
     * Get the state of this boolean field.
     * Usually overidden.
     * @return True if data is true.
     */
    public boolean getState();
    /**
     * Set the state of this field for binary fields (don't override this).
     * This calls setState with DISPLAY and SCREEN_MOVE.
     * @param bState the state to set the data to.
     * @return The error code.
     */
    public int setState(boolean bState);
    /**
     * Set the state of this field for binary fields (don't override this).
     * Usually overidden.
     * @param bState the state to set the data to.
     * @param bDisplayOption Display the data on the screen if true.
     * @param iMoveMode INIT, SCREEN, or READ move mode.
     * @return The error code.
     */
    public int setState(boolean state, boolean bDisplayOption, int iMoveMode);
    /**
     * For numeric fields, get the current value for numeric fields.
     * Usually overidden.
     * @return The value of this field's data.
     */
    public double getValue();
    /**
     * For numeric fields, set the current value.
     * This calls setValue with DISPLAY and SCREEN_MOVE.
     * @param value the value to set the data to.
     * @return The error code.
     */
    public int setValue(double value);
    /**
     * For numeric fields, set the current value.
     * Override this method to convert the value to the actual Physical Data Type.
     * @param bState the state to set the data to.
     * @param bDisplayOption Display the data on the screen if true.
     * @param iMoveMode INIT, SCREEN, or READ move mode.
     * @return The error code.
     */
    public int setValue(double value, boolean bDisplayOption, int moveMode);
    /**
     * Convert the current field value to an index 0 - x.
     * Ususally used for fields the are displayed as popups.
     * Must be overidden.
     * @return The index (-1 for unknown).
     */
    public int convertFieldToIndex();
    /**
     * Convert this index value to a display string.
     * Ususally used for fields that are displayed as popups.
     * Override if you want to display the string.
     * @return The string for this index.
     */
    public String convertIndexToDisStr(int index);
    /**
     * Set this field to the value to represent this index.
     * Must be overidden.
     * Ususally used for fields that are displayed as popups.
     * @param index the index to set the data to.
     * @param bDisplayOption Display the data on the screen if true.
     * @param iMoveMode INIT, SCREEN, or READ move mode.
     * @return The error code.
     */
    public int convertIndexToField(int index, boolean bDisplayOption, int iMoveMode);
    /**
     * equals - Override the "equals" Object method to compare two fields' raw data.
     * @param field The converter to compare this converter to.
     * @return true if equal.
     */
    public boolean equals(Convert field);
    /**
     * Set up the default control for this field.
     * Calls setupDefaultView with this as the converter.
     *  @param  itsLocation     Location of this component on screen (ie., GridBagConstraint).
     *  @param  targetScreen    Where to place this component (ie., Parent screen or GridBagLayout).
     *  @param  iDisplayFieldDesc Display the label? (optional).
     *  @return   Return the component or ScreenField that is created for this field.
     */
    public Object setupDefaultView(Object itsLocation, Object targetScreen, int iDisplayFieldDesc) ;
    /**
     * Set up the default control for this field.
     *  @param  itsLocation     Location of this component on screen (ie., GridBagConstraint).
     *  @param  targetScreen    Where to place this component (ie., Parent screen or GridBagLayout).
     *  @param  iDisplayFieldDesc Display the label? (optional).
     *  @return   Return the component or ScreenField that is created for this field.
     */
    public Object setupDefaultView(Object itsLocation, Object targetScreen, Convert converter, int iDisplayFieldDesc);
}
