/*

 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field;

/**
 * @(#)EmptyField.java  0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import org.jbundle.base.db.Record;
import org.jbundle.base.util.DBConstants;
import org.jbundle.thin.base.db.Constants;


/**
 * UnusedField - This field is not used (In shared files).<p>
 *
 * @version 1.0.0
 * @author    Don Corley
 */
/**
 * EmptyField - Special BaseField if this file no longer exists in the file.
 */
public class UnusedField extends BaseField
{
	private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     */
    public UnusedField()
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
    public UnusedField(Record record, String strName, int iDataLength, String strDesc, Object strDefault)
    {
        this();
        this.init(record, strName, iDataLength, strDesc, strDefault);
    }
    /**
     * Constructor.
     */
    public UnusedField(Record record)
    {
        this();
        this.init(record, Constants.BLANK, 0, Constants.BLANK, null);
    }
    /**
     * Constructor.
     * @param record The parent record.
     * @param strName The field name.
     * @param iDataLength The maximum string length (pass -1 for default).
     * @param strDesc The string description (usually pass null, to use the resource file desc).
     * @param strDefault The default value (if object, this value is the default value, if string, the string is the default).
     */
    public void init(Record record, String strName, int iDataLength, String strDesc, Object strDefault)
    {
//        super.init(record, Constants.BLANK, 0, Constants.BLANK, null);
        super.init(record, strName, 0, strDesc, null);
        m_data = null;
    }
    /**
     * Creates a new object of the same class as this object.
     * @return     a clone of this instance.
     * @exception  CloneNotSupportedException  if the object's class does not support the <code>Cloneable</code> interface.
     * @see        java.lang.Cloneable
     */
    public Object clone() throws CloneNotSupportedException
    {
        BaseField field = new EmptyField(null);
        field.setRecord(m_record);     // Set table without adding to table field list
        return field;
    }
    /**
     * Get the dirty flag.
     * @return True is this field has been changes since the last init.
     */
    public boolean isModified()
    {
        return false;   // Never modified!
    }
    /**
     * getText - Retrieve (in string format) from this field.
     * @return a blank string.
     */
    public String getString()
    {
        return Constants.BLANK;
    }
    /**
     * Move the physical binary data to this field.
     * All ignored for this class.
     */
    public int setString(String strString, boolean bDisplayOption, int moveMode)
    {
        return DBConstants.NORMAL_RETURN;
    }
    /**
     * Get a pointer to the binary image of this field's data.
     * @return The raw data.
     */
    public Object doGetData() 
    { // Move raw data from this field
        return null;    // Never return data.
    }
    /**
     * SetToLimit - Get this field to the maximum or minimum value.
     * All ignored for this class.
     */
    public void setToLimit(int iAreaDesc)   // Set this field to the largest or smallest value
    {
    }
}
