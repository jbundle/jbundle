/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field.convert;

/**
 * @(#)FieldDescConverter.java  0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import org.jbundle.thin.base.db.Converter;

/**
 * Returns alternate field description.
 * (or returns the regular desc if the alt is null/empty).
 * Instantiate this class with either a static string or a converter
 * to use the description from.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class FieldDescConverter extends FieldConverter
{
    /**
     * The description to return.
     */
    protected String m_strAltDesc = null;
    /**
     * Use the description of this converter.
     */
    protected Converter m_convDescField = null;

    /**
     * Constructor.
     */
    public FieldDescConverter()
    {
        super();
    }
    /**
     * Constructor.
     * @param converter The next converter in the converter chain.
     * @param strAltDesc The description to use.
     */
    public FieldDescConverter(Converter converter, String strAltDesc)
    {
        this();
        this.init(converter, strAltDesc, null);
    }
    /**
     * Constructor.
     * @param converter The next converter in the converter chain.
     * @param convDescField The converter to get the description from.
     */
    public FieldDescConverter(Converter converter, Converter convDescField)
    {
        this();
        this.init(converter, null, convDescField);
    }
    /**
     * Initialize this converter.
     * @param converter The next converter in the converter chain.
     * @param convDescField The converter to get the description from.
     */
    public void init(Converter converter, String strAltDesc, Converter convDescField)
    {
        super.init(converter);
        m_strAltDesc = strAltDesc;
        m_convDescField = convDescField;
    }
    /**
     * Free this converter.
     */
    public void free()
    {
        super.free();
        m_strAltDesc = null;
    }
    /**
     * Get the field description.
     * @return The field description.
     */
    public String getFieldDesc() 
    {
        if (m_convDescField != null)
            return m_convDescField.getFieldDesc();
        if ((m_strAltDesc == null) || (m_strAltDesc.length() == 0))
            return super.getFieldDesc();
        else
            return m_strAltDesc;
    }
}
