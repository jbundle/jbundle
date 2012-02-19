/*

 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field;

/**
 * @(#)CurrencyTextField.java 0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import org.jbundle.base.db.Record;

/**
 * CurrencyTextField - BaseField definition for a currency field.
 * Saved as text internally.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class CurrencyTextField extends StringField
{
	private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     */
    public CurrencyTextField()
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
    public CurrencyTextField(Record record, String strName, int iDataLength, String strDesc, Object strDefault)
    {
        this();
        this.init(record, strName, iDataLength, strDesc, strDefault);
    }
    /**
     * Creates a new object of the same class as this object.
     * @return     a clone of this instance.
     * @exception  CloneNotSupportedException  if the object's class does not support the <code>Cloneable</code> interface.
     * @see        java.lang.Cloneable
     */
    public Object clone() throws CloneNotSupportedException
    {
        BaseField field = new CurrencyTextField(null, m_strFieldName, m_iMaxLength, m_strFieldDesc, null);
        field.setRecord(m_record);     // Set table without adding to table field list
        return field;
    }
    /**
     * Get the value of this field (as a double).
     * @return double value of this field.
     */
    public double getValue()
    {           // Get this field's value
        if (m_data == null)
            return 0;
        double doubleField = Double.valueOf((String)m_data).doubleValue();
        return doubleField;
    }
}
