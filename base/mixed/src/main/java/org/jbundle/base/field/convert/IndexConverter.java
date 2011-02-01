
package org.jbundle.base.field.convert;

/**
 * @(#)IndexConverter.java  0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import org.jbundle.base.field.NumberField;
import org.jbundle.thin.base.db.Converter;


/**
 * Display the current display value of an index field.
 * This is typically used on NumberFields that are the index
 * field of a popup array. This converter is usually used to
 * display to value where a popup is not necessary (ie., in a report).
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class IndexConverter extends FieldConverter
{

    /**
     * Constructor.
     */
    public IndexConverter()
    {
        super();
    }
    /**
     * Constructor.
     * @param converter The next converter in the converter chain.
     */
    public IndexConverter(Converter converter)
    {
        this();
        this.init(converter);
    }
    /**
     * Get the maximum length of this field.
     * Survey the first 10 items in the display list and return the longest length.
     * @return The max length.
     */
    public int getMaxLength() 
    { // Survey the first few to get the max length (Only called when setting up screen field)
        String string;
        int maxLength = 1;
        for (int index = 0; index < 10; index++)
        {
            string = this.getNextConverter().convertIndexToDisStr(index);
            if (index > 0) if (string.length() == 0)
                break;      // Far Enough
            if (string.length() > (short)maxLength)
                maxLength = string.length();
        }
        return maxLength;
    }
    /**
     * Retrieve (in string format) from this field.
     * @return The string (convert the current field index to the display string).
     */
    public String getString() 
    {
        int index = this.getNextConverter().convertFieldToIndex();
        return this.getNextConverter().convertIndexToDisStr(index);
    }
    /**
     * Convert and move string to this field.
     * Convert this string to an index and set the index value.
     * Override this method to convert the String to the actual Physical Data Type.
     * @param strString the state to set the data to.
     * @param bDisplayOption Display the data on the screen if true.
     * @param iMoveMode INIT, SCREEN, or READ move mode.
     * @return The error code (or NORMAL_RETURN).
     */
    public int setString(String fieldPtr, boolean bDisplayOption, int moveMode)                 // init this field override for other value
    {
        int index = ((NumberField)this.getNextConverter()).convertStringToIndex(fieldPtr);
        return this.getNextConverter().setValue(index, bDisplayOption, moveMode);
    }
}
