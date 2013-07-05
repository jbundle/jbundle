/*

 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field.convert;

/**
 * @(#)PageNoConverter.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import org.jbundle.base.model.DBConstants;

/**
 * Page number converter.
 * Converts a numeric field to "Page xx".
 * You must have a behavior to increment the page number, this only returns the string.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class PageNoConverter extends FieldConverter
{
    /**
     * The current page number.
     */
    protected int m_PageNo = 0;
    /**
     * The local word "page".
     */
    protected String m_strPageDesc = null;

    /**
     * Constructor.
     */
    public PageNoConverter()
    {
        super();
    }
    /**
     * Constructor.
     * @param strPageDesc The local string for the word "page".
     */
    public PageNoConverter(String strPageDesc)
    {
        this();
        this.init(strPageDesc);
    }
    /**
     * Initialize this Converter.
     * @param strPageDesc The local string for the word "page".
     */
    public void init(String strPageDesc)
    {
        super.init(null);
        m_PageNo = 0;
        m_strPageDesc = strPageDesc;
        if ((m_strPageDesc == null) || (m_strPageDesc.length() == 0))
            m_strPageDesc = "Page";
    }
    /**
     * Maximum field length is 15.
     * @return The max field length.
     */
    public int getMaxLength() 
    {
        return 15;      // 15 characters
    }
    /**
     * Retrieve (in string format) from this field.
     * @return a sting of the format "page xx".
     */
    public String getString() 
    {
        String string = m_strPageDesc + ' ' + Integer.toString(m_PageNo);
        return string;
    }
    /**
     * Convert and move string to this field.
     * Ignored in this class.
     */
    public int setString(String strString, boolean bDisplayOption, int iMoveMode)               // init this field override for other value
    {
        return DBConstants.NORMAL_RETURN;
    }
}
