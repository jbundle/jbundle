/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db.filter;

/**
 * @(#)StringSubFileBehavior.java 0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import org.jbundle.base.db.Record;
import org.jbundle.base.util.DBConstants;

/**
 * A StringSubFileFilter is a record filter that you can use to
 * specify a staring and/or ending string for a key area.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class StringSubFileFilter extends DependentFileFilter
{
    /**
     * First string in the key fields.
     */
    protected String m_strFirst = null;
    /**
     * Second string in the key fields.
     */
    protected String m_strSecond = null;
    /**
     * Third string in the key fields.
     */
    protected String m_strThird = null;

    /**
     * Constructor.
     */
    public StringSubFileFilter()
    {
        super();
    }
    /**
     * Constructor.
     * @param strFirst First string in the key fields.
     * @param strSecond Second string in the key fields.
     * @param strThird Third string in the key fields.
     * @param iFieldSeq The First field sequence of the key.
     * @param iFieldSeq2 The Second field sequence of the key (-1 for none).
     * @param iFieldSeq3 The Third field sequence of the key (-1 for none).
     */
    public StringSubFileFilter(String mainString, int iFieldSeq, String strSecond, int secondFilesField, String strThird, int thirdFilesField)
    {
        this();
        this.init(null, mainString, iFieldSeq, strSecond, secondFilesField, strThird, thirdFilesField);
    }
    /**
     * Constructor.
     * @param strFirst First string in the key fields.
     * @param strSecond Second string in the key fields.
     * @param strThird Third string in the key fields.
     * @param iFieldSeq The First field sequence of the key.
     * @param iFieldSeq2 The Second field sequence of the key (-1 for none).
     * @param iFieldSeq3 The Third field sequence of the key (-1 for none).
     */
    public void init(Record record, String mainString, int iFieldSeq, String strSecond, int secondFilesField, String strThird, int thirdFilesField)
    {
        super.init(record, iFieldSeq, secondFilesField, thirdFilesField);
        m_strFirst = mainString;
        m_strSecond = strSecond;
        m_strThird = strThird;
    }
    /**
     * Creates a new object of the same class as this object.
     * @return     a clone of this instance.
     * @exception  CloneNotSupportedException  if the object's class does not support the <code>Cloneable</code> interface.
     * @see        java.lang.Cloneable
     */
    public Object clone() throws CloneNotSupportedException
    {
        StringSubFileFilter newBehavior = new StringSubFileFilter(m_strFirst, m_iThisFileFieldSeq, m_strSecond, m_iThisFileFieldSeq2, m_strThird, m_iThisFileFieldSeq3);
        return newBehavior;
    }
    /**
     * Setup the target key field to the initial string values.
     * @oaram bDisplayOption If true, display changes.
     * @param boolSetModified - If not null, set this field's modified flag to this value
     * @return false If this key was set to all nulls.
     */
    public boolean setMainKey(boolean bDisplayOption, Boolean boolSetModified, boolean bSetIfModified)
    {
        super.setMainKey(bDisplayOption, boolSetModified, bSetIfModified);
        boolean bNonNulls = false;  // Default to yes, all keys are null.
        if (m_fldThisFile != null)
        {
            m_fldThisFile.setString(m_strFirst, bDisplayOption, DBConstants.READ_MOVE);     // Set the booking number in pax file
            if (!m_fldThisFile.isNull())
                bNonNulls = true;   // Non null.
        }
        if (m_fldThisFile2 != null)
        {
            m_fldThisFile2.setString(m_strSecond, bDisplayOption, DBConstants.READ_MOVE);
            if (!m_fldThisFile2.isNull())
                bNonNulls = true;   // Non null.
        }
        if (m_fldThisFile3 != null)
        {
            m_fldThisFile3.setString(m_strThird, bDisplayOption, DBConstants.READ_MOVE);
            if (!m_fldThisFile3.isNull())
                bNonNulls = true;   // Non null.
        }
        return bNonNulls;
    }
    /**
     * Set the string for the first key field.
     * @param strFirst First string in the key fields.
     */
    public void setFirst(String strFirst)
    {
        m_strFirst = strFirst;
    }
    /**
     * Set the string for the second key field.
     * @param strSecond Second string in the key fields.
     */
    public void setSecond(String strSecond)
    {
        m_strSecond = strSecond;
    }
}
