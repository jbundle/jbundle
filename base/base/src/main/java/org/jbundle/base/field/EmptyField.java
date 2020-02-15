/*

 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field;

/**
 * @(#)EmptyField.java  0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import org.jbundle.base.db.Record;
import org.jbundle.base.model.DBConstants;
import org.jbundle.thin.base.db.Constants;


/**
 * BaseField - This is the base class for all fields.<p>
 *
 * @version 1.0.0
 * @author    Don Corley
 */
/**
 * EmptyField - Special BaseField if this file no longer exists in the file.
 */
public class EmptyField extends BaseField
{
	private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     */
    public EmptyField()
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
    public EmptyField(Record record, String strName, int iDataLength, String strDesc, Object strDefault)
    {
        this();
        this.init(record, strName, iDataLength, strDesc, strDefault);
    }
    /**
     * Constructor.
     */
    public EmptyField(Record record)
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
        super.init(record, Constants.BLANK, 0, Constants.BLANK, null);
        m_data = Constants.BLANK;
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
    public int setString(String string, boolean bDisplayOption, int moveMode)
    {
        return DBConstants.NORMAL_RETURN;
    }
    /**
     * SetToLimit - Get this field to the maximum or minimum value.
     * All ignored for this class.
     */
    public void setToLimit(int iAreaDesc)   // Set this field to the largest or smallest value
    {
    }
}
