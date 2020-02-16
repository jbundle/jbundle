/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field.convert.encode;

/**
 * @(#)PercentConverter.java    0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import org.jbundle.base.field.convert.FieldConverter;
import org.jbundle.base.model.DBConstants;
import org.jbundle.thin.base.db.Converter;


/**
 * Mask all the data except the last four chars.
 * This asterisks except the last four digits.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class MaskConverter extends FieldConverter
{

    /**
     * Constructor.
     */
    public MaskConverter()
    {
        super();
    }
    /**
     * Constructor.
     * @param converter The percent field target.
     */
    public MaskConverter(Converter converter)
    {
        this();
        this.init(converter);
    }
    /**
     * Initialize this converter.
     * @param converter The next converter in the converter chain.
     */
    public void init(Converter converter)
    {
        super.init(converter);
    }
    /**
     * Retrieve (in string format) from this field.
     * @return This field as a percent string.
     */
    public String getString() 
    {
        String string = super.getString();
        if ((string != null) && (string.length() > 0))
        {
            StringBuffer sb = new StringBuffer(string);
            if (sb.length() > m_iUnmaskedChars)
            {
                String strFiller = "" + FILLER;
                for (int i = 0; i < sb.length() - m_iUnmaskedChars; i++)
                {
                    sb.replace(i, i+1, strFiller);
                }
                string = sb.toString();
            }
        }
        return string;
    }
    protected int m_iUnmaskedChars = 4;
    /**
     * Convert and move string to this field.
     * @param strValue the state to set the data to.
     * @param bDisplayOption Display the data on the screen if true.
     * @param iMoveMode INIT, SCREEN, or READ move mode.
     * @return The error code (or NORMAL_RETURN).
     */
    public int setString(String strValue, boolean bDisplayOption, int iMoveMode)
    {
        if ((strValue == null) || (strValue.length() == 0))
            return super.setString(strValue, bDisplayOption, iMoveMode);  // Don't trip change or display
        if (strValue.charAt(0) == FILLER)
            return DBConstants.NORMAL_RETURN;
        return super.setString(strValue, bDisplayOption, iMoveMode);
    }

    public static final char FILLER = '*';
}
