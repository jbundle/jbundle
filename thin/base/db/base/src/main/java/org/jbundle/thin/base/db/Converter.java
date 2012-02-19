/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.db;

/**
 * @(#)Converter.java 0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.util.Map;

import org.jbundle.model.db.Convert;
import org.jbundle.model.screen.ComponentParent;
import org.jbundle.model.screen.ScreenComponent;
import org.jbundle.model.screen.ScreenLoc;
import org.jbundle.model.util.DataConverters;



/**
 * Converter converts the screen data representation to the field's binary representation and back.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public abstract class Converter extends DataConverters
    implements Convert
{

    /**
     * Constructor.
     */
    public Converter()
    {
        super();
    }
    /**
     * Constructor.
     * @param obj Not used in converter.
     */
    public Converter(Object obj)
    {
        this();
        this.init(obj);
    }
    /**
     * Initialize this converter.
     * @param obj Not used in converter.
     */
    public void init(Object obj)
    {
    }
    /**
     * Free this converter.
     */
    public void free()
    {
    }
    /**
     * This screen component is displaying this field.
     * @param Object sField The screen component.. either a awt.Component or a ScreenField
     */
    public void addComponent(Object sField)
    {
    }
    /**
     * Remove this control from this field's control list.
     * @param Object sField The screen component.. either a awt.Component or a ScreenField
     */
    public void removeComponent(Object screenField)
    {
        this.free();    // If the screen field is gone, I have no more use, since I am a converter and I have no reference to other objects.
    }
    /**
     * Get the field on the end of this converter chain.
     * @return FieldInfo The field.
     */
    public FieldInfo getField() 
    {
        return null;    // A converter is not a field!
    }
    /**
     * Get the first converter for this field (must be linked to at least one ScreenComponent for this to work).
     * @return The converter object (or this if there is none).
     */
    public Converter getFieldConverter()
    {
        for (int i = 0; ; i++)
        {
            Object ojbComponent = this.getField().getComponent(i);
            if (ojbComponent == null)
                break;
            if (ojbComponent instanceof ScreenComponent)
            {
                Converter conv = (Converter)((ScreenComponent)ojbComponent).getConverter();
                if ((conv != null) && (conv.getField() == this.getField()))
                    return conv;   // Since there is a converter in front of this field, return the converter
            }
        }
        return this;
    }
    /**
     * Get this field's name.
     * @return The name of the field on the end of the converter chain.
     */
    public String getFieldName()
    {
        if (this.getField() == null)
            return null;
        else
            return this.getField().getFieldName(false, false);
    }
    /**
     * Get the field description.
     * Usually overidden.
     * @return The description of the field on the end of the converter chain.
     */
    public String getFieldDesc() 
    {
        return Constants.BLANK;     // By default, there is no temp string
    }
    /**
     * Get current length of this field converted to a string.
     * (For StringField, returns fCurrent, for OtherFields returns fMax.
     * ... this routine is used to set up a buffer to receive the field, w/o wasting space!)
     * Usually overidden.
     * @return The current length (0 if null data, -1 if unknown).
     */
    public int getLength() 
    {
        return 0;
    }
    /**
     * Get the maximum length of this field.
     * Usually overidden.
     * @return The maximum length of this field (0 if unknown).
     */
    public int getMaxLength() 
    {
        return 0;
    }
    /**
     * Get the binary image of this field's data.
     * @return The raw data.
     */
    public Object getData() 
    { // Move raw data from this field
        return null;
    }
    /**
     * Move data to this field (don't override this).
     * This calls setData with DISPLAY and SCREEN_MOVE.
     * @param object The data to set the field to.
     * @return The error code.
     */
    public final int setData(Object object)
    {
        return this.setData(object, Constants.DISPLAY, Constants.SCREEN_MOVE);
    }
    /**
     * Move the physical binary data to this field.
     * @param vpData The data to set the field to.
     * @param bDisplayOption Display the data on the screen if true.
     * @param iMoveMode INIT, SCREEN, or READ move mode.
     * @return The error code.
     */
    public int setData(Object vpData, boolean bDisplayOption, int iMoveMode)
    {       // Must be overidden
        return Constants.NORMAL_RETURN;
    }
    /**
     * Retrieve (in string format) from this field.
     * Override this method to convert the actual Physical Data Type to a String.
     * The default implementation (here) assumes the physical data is in string format.
     * Same as toString().
     * @return The data in string format.
     * @see toString().
     */
    public String getString()
    {
        return Constants.BLANK;
    }
    /**
     * Override Object.toString to convert this field to a String.
     * @return The data in string format.
     * @see getString().
     */
    public String toString ()
    {
        return this.getString();
    }
    /**
     * Convert this string and set the data to this field (don't override this).
     * This calls setString with DISPLAY and SCREEN_MOVE.
     * @param string the string to set the data to.
     * @return The error code.
     */
    public final int setString(String string)
    {
        return this.setString(string, Constants.DISPLAY, Constants.SCREEN_MOVE);
    }
    /**
     * Convert and move string to this field.
     * Override this method to convert the String to the actual Physical Data Type.
     * @param strString the string to set the data to.
     * @param bDisplayOption Display the data on the screen if true.
     * @param iMoveMode INIT, SCREEN, or READ move mode.
     * @return The error code.
     */
    public int setString(String strString, boolean bDisplayOption, int moveMode)    // init this field override for other value
    {
        return Constants.NORMAL_RETURN;
    }
    /**
     * Get the state of this boolean field.
     * Usually overidden.
     * @return True if data is true.
     */
    public boolean getState()               // init this field override for other value
    {
        return true;
    }
    /**
     * Set the state of this field for binary fields (don't override this).
     * This calls setState with DISPLAY and SCREEN_MOVE.
     * @param bState the state to set the data to.
     * @return The error code.
     */
    public final int setState(boolean bState)
    {
        return this.setState(bState, Constants.DISPLAY, Constants.SCREEN_MOVE);
    }
    /**
     * Set the state of this field for binary fields (don't override this).
     * Usually overidden.
     * @param bState the state to set the data to.
     * @param bDisplayOption Display the data on the screen if true.
     * @param iMoveMode INIT, SCREEN, or READ move mode.
     * @return The error code.
     */
    public int setState(boolean state, boolean bDisplayOption, int iMoveMode)
    {
        return Constants.NORMAL_RETURN;
    }
    /**
     * For numeric fields, get the current value for numeric fields.
     * Usually overidden.
     * @return The value of this field's data.
     */
    public double getValue()
    {
        return 0;
    }
    /**
     * For numeric fields, set the current value.
     * This calls setValue with DISPLAY and SCREEN_MOVE.
     * @param value the value to set the data to.
     * @return The error code.
     */
    public int setValue(double value)
    {
        return this.setValue(value, Constants.DISPLAY, Constants.SCREEN_MOVE);
    }
    /**
     * For numeric fields, set the current value.
     * Override this method to convert the value to the actual Physical Data Type.
     * @param bState the state to set the data to.
     * @param bDisplayOption Display the data on the screen if true.
     * @param iMoveMode INIT, SCREEN, or READ move mode.
     * @return The error code.
     */
    public int setValue(double value, boolean bDisplayOption, int moveMode)
    {
        return Constants.NORMAL_RETURN;
    }
    /**
     * Convert the current field value to an index 0 - x.
     * Ususally used for fields the are displayed as popups.
     * Must be overidden.
     * @return The index (-1 for unknown).
     */
    public int convertFieldToIndex()                    // init this field override for other value
    {
        return -1;
    }
    /**
     * Convert this index value to a display string.
     * Ususally used for fields that are displayed as popups.
     * Override if you want to display the string.
     * @return The string for this index.
     */
    public String convertIndexToDisStr(int index)
    {
        return Constants.BLANK;
    }
    /**
     * Set this field to the value to represent this index.
     * Must be overidden.
     * Ususally used for fields that are displayed as popups.
     * @param index the index to set the data to.
     * @param bDisplayOption Display the data on the screen if true.
     * @param iMoveMode INIT, SCREEN, or READ move mode.
     * @return The error code.
     */
    public int convertIndexToField(int index, boolean bDisplayOption, int iMoveMode)
    {
        return Constants.NORMAL_RETURN;
    }
    /**
     * equals - Override the "equals" Object method to compare two fields' raw data.
     * @param field The converter to compare this converter to.
     * @return true if equal.
     */
    public boolean equals(Convert field) 
    { // Override this
        Object objThis = this.getData();
        Object objField = field.getData();
        if ((objThis == null) || (objField == null))
        {
            if ((objThis == null) && (objField == null))
                return true;
            return false;
        }
        return objThis.equals(objField);
    }
    /**
     * Get the image for Buttons and Bitmaps and drag cursors.
     * Also used for image buttons that change bitmaps with their state.
     * @return The name of the bitmap.
     * @see org.jbundle.base.screen.view.swing.VButtonBox
     */
    public String getBitmap()
    {
        return null;
    }
    /**
     * Get the HTML Hyperlink for this field (override).
     * @return The full html hyperlink (ie., mailto:don@don.com).
     */
    public String getHyperlink() 
    {
        return null;
    }
    /**
     * Set up the default control for this field.
     * Calls setupDefaultView with this as the converter (Convenience method).
     *  @param  itsLocation     Location of this component on screen (ie., GridBagConstraint).
     *  @param  targetScreen    Where to place this component (ie., Parent screen or GridBagLayout).
     *  @param  iDisplayFieldDesc Display the label? (optional).
     *  @return   Return the component or ScreenField that is created for this field.
     */
    public final ScreenComponent setupDefaultView(ScreenLoc itsLocation, ComponentParent targetScreen, int iDisplayFieldDesc)  // Add this view to the list
    {
        return this.setupDefaultView(itsLocation, targetScreen, this, iDisplayFieldDesc, null);
    }
    /**
     * Set up the default control for this field.
     *  @param  itsLocation     Location of this component on screen (ie., GridBagConstraint).
     * @param  targetScreen    Where to place this component (ie., Parent screen or GridBagLayout).
     * @param  iDisplayFieldDesc Display the label? (optional).
     *  @return   Return the component or ScreenField that is created for this field.
     */
    public ScreenComponent setupDefaultView(ScreenLoc itsLocation, ComponentParent targetScreen, Convert converter, int iDisplayFieldDesc, Map<String, Object> properties)   // Add this view to the list
    {
        return null;
    }
    /**
     * String all the non-numeric characters from this string.
     * @param string input string.
     * @return The result string.
     */
    public String stripNonNumeric(String string)
    {
        return Converter.stripNonNumber(string);
    }
}
