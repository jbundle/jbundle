/*

 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field.convert;

/**
 * @(#)FlagDepFieldBeh.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import org.jbundle.thin.base.db.Converter;

/**
 * If the flag is checked, use an alternate converter.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class FlagDepFieldConverter extends MultipleFieldConverter
{
    /**
     * The converter to check the state of (if true, use the alternate converter).
     */
    protected Converter m_convCheckmark = null;

    /**
     * Constructor.
     */
    public FlagDepFieldConverter()
    {
        super();
    }
    /**
     * Constructor.
     * @param converter The next converter in the converter chain.
     * @param convCheckmark The converter to check the state of (if true, use the alternate converter).
     * @param converterAlt The converter to use if the checkmark is true.
     */
    public FlagDepFieldConverter(Converter converter, Converter convAlternate, Converter convCheckmark)
    {
        this();
        this.init(converter, convAlternate, convCheckmark);
    }
    /**
     * Initialize this converter.
     * @param converter The next converter in the converter chain.
     * @param convCheckmark The converter to check the state of (if true, use the alternate converter).
     * @param converterAlt The converter to use if the checkmark is true.
     */
    public void init(Converter converter, Converter convAlternate, Converter convCheckmark)
    {
        super.init(converter, convAlternate);
        m_convCheckmark = convCheckmark;
    }
    /**
     * Free this converter.
     */
    public void free()
    {
        super.free();
        m_convCheckmark = null;
    }
    /**
     * Add this component to the components displaying this field.
     * Make sure all the converter have this screenfield on their list.
     * @param Object sField The screen component.. either a awt.Component or a ScreenField.
     */
    public void addComponent(Object screenField)
    { // Set up the dependencies, This will recompute if any change from these three fields
        super.addComponent(screenField);
        m_convCheckmark.addComponent(screenField);
    }
    /**
     * Remove this control from this field's control list.
     * @param Object sField The screen component.. either a awt.Component or a ScreenField
     */
    public void removeComponent(Object screenField)
    { // Set up the dependencies, This will recompute if any change from these three fields
        m_convCheckmark.removeComponent(screenField);
        super.removeComponent(screenField);
    }
    /**
     * Should I pass the alternate field (or the main field)?
     * Pass the alternate field if the checkmark is true.
     * @return index (-1)= next converter, 0 - n = List of converters
     */
    public int getIndexOfConverterToPass(boolean bSetData)
    {
        if (m_convCheckmark != null)
            if (m_convCheckmark.getState() == true)
                return 0; // Retrieve the alternate field
        return -1;  // Else, Retrieve this info
    }
}
