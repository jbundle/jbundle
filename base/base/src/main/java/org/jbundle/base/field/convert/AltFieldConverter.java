/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field.convert;

/**
 * @(#)AltFieldConverter.java 0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.Converter;

/**
 * If the next converter in the chain equals this string (default blank)
 * then use the alternate converter.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class AltFieldConverter extends MultipleFieldConverter
{
    /**
     * The string to use in the comparason.
     */
    protected String m_strCompare = null;

    /**
     * Constructor.
     */
    public AltFieldConverter()
    {
        super();
    }
    /**
     * Constructor.
     * @param converter The next converter in the converter chain.
     * @param cnvDependent The alternate converter to use if the converter matches the string (blank).
     */
    public AltFieldConverter(Converter converter, Converter cnvDependent)
    {
        this();
        this.init(converter, cnvDependent, null);
    }
    /**
     * Constructor.
     * @param converter The next converter in the converter chain.
     * @param cnvDependent The alternate converter to use if the converter matches the string (blank).
     */
    public AltFieldConverter(Converter converter, Converter cnvDependent, String strCompare)
    {
        this();
        this.init(converter, cnvDependent, strCompare);
    }
    /**
     * Initialize this converter.
     * @param converter The next converter in the converter chain.
     * @param cnvDependent The alternate converter to use if the converter matches the string (blank).
     * @param strCompare The string to compare with the next converter's value to decide whether to display the alternate.
     */
    public void init(Converter converter, Converter cnvDependent, String strCompare)
    {
        super.init(converter, cnvDependent);

        if ((strCompare == null) || (strCompare.length() == 0))
            m_strCompare = Constants.BLANK;
        else
            m_strCompare = strCompare;
    }
    /**
     * Free this converter.
     */
    public void free()
    {
        super.free();
        m_strCompare = null;
    }
    /**
     * Should I pass the alternate field (or the main field)?
     * If the next converter equals the target, return 0 (the alternate converter).
     * @return index (-1)= next converter, 0 - n = List of converters.
     */
    public int getIndexOfConverterToPass(boolean bSetData)
    {
        if ((m_strCompare == null) || (m_converterNext.getString().equals(m_strCompare))) // If this field matches the compare string,
            return 0; // Retrieve the dependent field
        else
            return -1;  // Else, Retrieve this info
    }
    /**
     *
     */
    public void setCompareString(String strCompare)
    {
        m_strCompare = strCompare;
    }
}
