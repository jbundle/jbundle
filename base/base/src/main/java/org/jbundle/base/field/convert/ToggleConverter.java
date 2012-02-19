/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field.convert;

/**
 * @(#)ToggleConverter.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import org.jbundle.thin.base.db.Converter;

/**
 * Every time this field is set to anything, the converter is toggled on or off.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class ToggleConverter extends FieldConverter
{

    /**
     * Constructor.
     */
    public ToggleConverter()
    {
        super();
    }
    /**
     * Constructor.
     * @param converter The next converter in the converter chain.
     */
    public ToggleConverter(Converter converter)
    {
        this();
        this.init(converter);
    }
    /**
     * Convert and move string to this field.
     * Toggle the field value.
     * Override this method to convert the String to the actual Physical Data Type.
     * @param strString the state to set the data to.
     * @param bDisplayOption Display the data on the screen if true.
     * @param iMoveMode INIT, SCREEN, or READ move mode.
     * @return The error code (or NORMAL_RETURN).
     */
    public int setString(String string, boolean bDisplayOption, int moveMode)
    {
        boolean bNewState = !this.getNextConverter().getState();
        return this.getNextConverter().setState(bNewState, bDisplayOption, moveMode);  // Toggle the state
    }
}
