/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field.convert;

/**
 * @(#)DescConverter.java 0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import org.jbundle.base.util.ScreenConstants;
import org.jbundle.thin.base.db.Converter;


/**
 * Instead of returing the data, return the field's description.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class DescConverter extends FieldConverter
{
    /**
     * Constructor.
     */
    public DescConverter()
    {
        super();
    }
    /**
     * Constructor.
     * @param converter The next converter in the converter chain.
     */
    public DescConverter(Converter converter)
    {
        this();
        this.init(converter);
    }
    /**
     * Free this converter.
     */
    public void free()
    {
        super.free();
    }
    /**
     * Get the maxumum data length.
     * @return The standard field display length.
     */
    public int getMaxLength() 
    {
        return ScreenConstants.kMaxDescLength;
    }
    /**
     * Retrieve (in string format) from this field.
     * @return This field (description).
     */
    public void getString(String string) 
    {
        string = this.getNextConverter().getFieldDesc();
    }
}
