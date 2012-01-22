/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field.convert;

/**
 * @(#)GlConverter.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import java.util.Map;

import org.jbundle.base.field.BaseField;
import org.jbundle.base.model.ScreenModel;
import org.jbundle.model.db.Convert;
import org.jbundle.model.screen.ComponentParent;
import org.jbundle.model.screen.ScreenComponent;
import org.jbundle.model.screen.ScreenLoc;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.Converter;


/**
 * Convert string to/from G/L account number.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class GlConverter extends FieldConverter
{

    /**
     * Constructor.
     */
    public GlConverter()
    {
        super();
    }
    /**
     * Constructor.
     * @param converter The next converter in the converter chain.
     */
    public GlConverter(Converter converter)
    {
        this();
        this.init(converter);
    }
    /**
     * Get the maximum length of this field.
     * @return The length plus one character for the dash.
     */
    public int getMaxLength() 
    {       // Must be overidden
        return super.getMaxLength() + 1;    // Include a space for the dash
    }
    /**
     * Retrieve (in string format) from this field.
     * @return The string.
     */
    public String getString() 
    { // By default, get the data as-is
        String string = this.stripNonNumeric(super.getString());
        int dataLength = string.length();
        if (dataLength > 4)
            string = string.substring(0, 4) + "-" + string.substring(4);
        return string;
    }
    /**
     * Convert and move string to this field.
     * Override this method to convert the String to the actual Physical Data Type.
     * @param strString the state to set the data to.
     * @param bDisplayOption Display the data on the screen if true.
     * @param iMoveMode INIT, SCREEN, or READ move mode.
     * @return The error code.
     */
    public int setString(String strString, boolean bDisplayOption, int moveMode)                    // init this field override for other value
    { // By default, move the data as-is
        String string = Constants.BLANK;
        int fieldLength = strString.length();
        for (int source = 0; source < (int)fieldLength; source++)
        {
            if ((strString.charAt(source) >= '0') && (strString.charAt(source) <= '9'))
                string += strString.charAt(source);
        }
        fieldLength = string.length();
        if ((fieldLength <= 4) && (fieldLength > 0))
            string += "000";
        return super.setString(string, bDisplayOption, moveMode); // Convert to internal rep and return
    }
    /**
     * Set up the default control for this field (an SEditText).
     *  @param  itsLocation     Location of this component on screen (ie., GridBagConstraint).
     *  @param  targetScreen    Where to place this component (ie., Parent screen or GridBagLayout).
     *  @param  iDisplayFieldDesc Display the label? (optional).
     *  @return   Return the component or ScreenField that is created for this field.
     */
    public ScreenComponent setupDefaultView(ScreenLoc itsLocation, ComponentParent targetScreen, Convert converter, int iDisplayFieldDesc, Map<String, Object> properties)
    {
        return BaseField.createScreenComponent(ScreenModel.EDIT_TEXT, itsLocation, targetScreen, converter, iDisplayFieldDesc, properties);
    }
}
