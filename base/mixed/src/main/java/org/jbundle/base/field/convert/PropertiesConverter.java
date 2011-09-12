/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field.convert;

/**
 * @(#)PropertiesConverter.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import org.jbundle.base.field.PropertiesField;
import org.jbundle.base.util.DBConstants;
import org.jbundle.thin.base.db.Converter;


/**
 * Filter out this target property from a property field.
 * The field on the end of the converter chain must be a Properties field.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class PropertiesConverter extends FieldConverter
{
    /**
     * The property to display/set in the properties field.
     */
    protected String m_strProperty = null;

    /**
     * Constructor.
     */
    public PropertiesConverter()
    {
        super();
    }
    /**
     * Constructor.
     * @param converter The property field.
     * @param strProperty The property to display/set in the properties field.
     */
    public PropertiesConverter(Converter converter, String strProperty)
    {
        this();
        this.init(converter, strProperty);
    }
    /**
     * Initialize this converter.
     * @param converter The property field.
     * @param strProperty The property to display/set in the properties field.
     */
    public void init(Converter converter, String strProperty)
    {
        m_strProperty = strProperty;
        super.init(converter);
    }
    /**
     * Free this converter.
     */
    public void free()
    {
        m_strProperty = null;
        super.free();
    }
    /**
     * Retrieve (in string format) from this field.
     * @return This property from the property field.
     */
    public String getString() 
    {
        if (this.getField() instanceof PropertiesField)   // Always
            return ((PropertiesField)this.getField()).getProperty(m_strProperty);
        return DBConstants.BLANK;
    }
    /**
     * Convert and move string to this field.
     * Set this property in the property field.
     * @param strString the state to set the data to.
     * @param bDisplayOption Display the data on the screen if true.
     * @param iMoveMode INIT, SCREEN, or READ move mode.
     * @return The error code (or NORMAL_RETURN).
     */
    public int setString(String fieldPtr, boolean bDisplayOption, int iMoveMode)                    // init this field override for other value
    {
        if (this.getField() instanceof PropertiesField)   // Always
            return ((PropertiesField)this.getField()).setProperty(m_strProperty, fieldPtr, bDisplayOption, iMoveMode);
        return DBConstants.NORMAL_RETURN;
    }
}
