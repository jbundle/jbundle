/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field;

/**
 * @(#)CounterField.java    0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import org.jbundle.base.db.Record;
import org.jbundle.base.model.DBConstants;

/**
 * CounterField - This is the field class for an auto-sequence field.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class CounterField extends IntegerField
{
	private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     */
    public CounterField()
    {
        super();
    }
    /**
     * Constructor.
     * @param record The parent record.
     * @param strName The field name.
     * @param iDataLength The maximum string length (pass -1 for default).
     * @param strDesc The string description (usually pass null, to use the resource file desc).
     * @param strDefault The default value (if object, this value is the default value, if string, the string is the default).
     */
    public CounterField(Record record, String strName, int iDataLength, String strDesc, Object strDefault)
    {
        this();
        this.init(record, strName, iDataLength, strDesc, strDefault);
    }
    /**
     * Initialize the object.
     * @param record The parent record.
     * @param strName The field name.
     * @param iDataLength The maximum string length (pass -1 for default).
     * @param strDesc The string description (usually pass null, to use the resource file desc).
     * @param strDefault The default value (if object, this value is the default value, if string, the string is the default).
     */
    public void init(Record record, String strName, int iDataLength, String strDesc, Object strDefault)
    {
        super.init(record, strName, iDataLength, strDesc, strDefault);
        this.setHidden(true);	// Typically counters should never display
    }
    /**
     * Creates a new object of the same class as this object.
     * @return     a clone of this instance.
     * @exception  CloneNotSupportedException  if the object's class does not support the <code>Cloneable</code> interface.
     * @see        java.lang.Cloneable
     */
    public Object clone() throws CloneNotSupportedException
    {
        BaseField field = new CounterField(null, m_strFieldName, m_iMaxLength, m_strFieldDesc, null);
        field.setRecord(m_record);     // Set table without adding to table field list
        return field;
    }
    /**
     * Do I skip getting/putting this field into the SQL param list?
     * For counters, you NEVER write the value, except in the following rare case:
     * <br/>1. Autosequence is not supported in the database.
     * <br/>2. You are doing the first insert (where you are bumping the counter for a valid value).
     * @param iType The type of SQL statement (UPDATE/INSERT/etc).
     * @return true To skip this param (ie., skip insert field if not modified).
     */
    public boolean getSkipSQLParam(int iType)
    {
        boolean bSkip = super.getSkipSQLParam(iType);   // Don't skip this
        if (bSkip)
            return bSkip;   // If super says skip, definitely skip
        if ((iType == DBConstants.SQL_INSERT_TABLE_TYPE) || (iType == DBConstants.SQL_INSERT_VALUE_TYPE))
        {
            if (this.getRecord().getTable() != null)
                if (this.getRecord().getTable().getDatabase() != null)
                    if (!this.getRecord().getTable().getDatabase().isAutosequenceSupport())
                return false; // Special case - autocounters not supported.
            return true;        // Skip auto sequence inserts
        }
        return false;   // For other query types, don't skip
    }
}
