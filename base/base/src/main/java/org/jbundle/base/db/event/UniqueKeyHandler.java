/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db.event;

/**
 * @(#)NoDeleteHandler.java 0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import org.jbundle.base.db.KeyArea;
import org.jbundle.base.db.Record;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.CounterField;
import org.jbundle.base.field.DateTimeField;
import org.jbundle.base.field.NumberField;
import org.jbundle.base.model.DBConstants;
import org.jbundle.model.DBException;


/**
 * If there is a duplicate key, bump the count and write the record.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class UniqueKeyHandler extends FileListener
{
    public static final int REPEAT_COUNT = 40;
    public static final int KMS_IN_A_MINUTE = 60 * 1000;    // Milliseconds in a minute

    protected BaseField m_fieldToBump = null;
    protected int m_iBumpAmount = 1;
    
    /**
     * Constructor.
     */
    public UniqueKeyHandler()
    {
        super();
    }
    /**
     * Constructor.
     * @param record The sub-record to check.
     */
    public UniqueKeyHandler(BaseField fieldToBump)
    {
        this();
        this.init(null, fieldToBump);
    }
    /**
     * Constructor.
     * @param record My owner (usually passed as null, and set on addListener in setOwner()).
     */
    public void init(Record record, BaseField fieldToBump)
    {   // For this to work right, the booking number field needs a listener to re-select this file whenever it changes
        super.init(record);
        m_fieldToBump = fieldToBump;
    }
    /**
     * Creates a new object of the same class as this object.
     * @return     a clone of this instance.
     * @exception  CloneNotSupportedException  if the object's class does not support the <code>Cloneable</code> interface.
     * @see        java.lang.Cloneable
     */
    public Object clone() throws CloneNotSupportedException
    {
        return null;        // DO NOT clone this behavior as it is almost always explicitly added in record.addListeners().
    }
    /**
     * Called when a error happens on a file operation, return the errorcode, or fix the problem.
     * @param iErrorCode The error code from the previous listener.
     * @return The new error code.
     */
    public int doErrorReturn(int iChangeType, int iErrorCode)        // init this field override for other value
    {
        if (iChangeType == DBConstants.AFTER_ADD_TYPE)
            if (iErrorCode == DBConstants.DUPLICATE_KEY)
        {
            // First, find the first unique key that is not a counter
            Record record = this.getOwner();
            for (int iKeySeq = 0; iKeySeq < record.getKeyAreaCount(); iKeySeq++)
            {
                KeyArea keyArea = record.getKeyArea(iKeySeq);
                if (keyArea.getUniqueKeyCode() != DBConstants.UNIQUE)
                    continue;   // Could not have been this key
                BaseField field = m_fieldToBump;
                if (field == null)
                    field = keyArea.getField(keyArea.getKeyFields() - 1);
                if (field instanceof CounterField)
                    continue;  // This could not have had a dup key
                if (field instanceof NumberField)
                {   // This is the one
                    if (field instanceof DateTimeField)
                        m_iBumpAmount = KMS_IN_A_MINUTE;
                    long lCurrentValue = (long)field.getValue();
                    for (long lValue = lCurrentValue + 1; lValue < lCurrentValue + REPEAT_COUNT * m_iBumpAmount; lValue = lValue + m_iBumpAmount)
                    {
                        field.setValue(lValue);
                        try {
                            record.add();
                        } catch (DBException ex) {
                            if (ex.getErrorCode() != DBConstants.DUPLICATE_KEY)
                                break;  // Other error
                            continue;   // Ignore duplicate key error
                        }
                        return DBConstants.NORMAL_RETURN;   // Everything is okay now
                    }
                }
            }
        }
        return super.doErrorReturn(iChangeType, iErrorCode);
    }
}
