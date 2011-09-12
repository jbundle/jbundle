/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field;

/**
 * @(#)StringField.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import java.util.Map;

import org.jbundle.base.db.Record;
import org.jbundle.base.util.DBConstants;
import org.jbundle.base.util.DBSQLTypes;
import org.jbundle.thin.base.db.Constants;


/**
 * StringField - Set up the field that holds a String.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class StringField extends BaseField
{
	private static final long serialVersionUID = 1L;

	/**
     * The largest String.
     */
    static final public String HIGH_STRING = "zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz";

    /**
     * Constructor.
     */
    public StringField()
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
    public StringField(Record record, String strName, int iDataLength, String strDesc, Object strDefault)
    {
        this();
        this.init(record, strName, iDataLength, strDesc, strDefault);
    }
    /**
     * Initialize this object.
     * @param record The parent record.
     * @param strName The field name.
     * @param iDataLength The maximum string length (pass -1 for default).
     * @param strDesc The string description (usually pass null, to use the resource file desc).
     * @param strDefault The default value (if object, this value is the default value, if string, the string is the default).
     */
    public void init(Record record, String strName, int iDataLength, String strDesc, Object strDefault)
    {
        super.init(record, strName, iDataLength, strDesc, strDefault);
        if (iDataLength == DBConstants.DEFAULT_FIELD_LENGTH)
            m_iMaxLength = 30;
        m_classData = String.class;
    }
    /**
     * Creates a new object of the same class as this object.
     * @return     a clone of this instance.
     * @exception  CloneNotSupportedException  if the object's class does not support the <code>Cloneable</code> interface.
     * @see        java.lang.Cloneable
     */
    public Object clone() throws CloneNotSupportedException
    {
        BaseField field = new StringField(null, this.getFieldName(), m_iMaxLength, this.getFieldDesc(), null);
        field.setRecord(m_record);     // Set table without adding to table field list
        return field;
    }
    /**
     * Return the type of quote that goes around this SQL value.
     * @return char The SQL quote character.
     * @param bStartQuote boolean
     */
    public char getSQLQuote(boolean bStartQuote )
    {
        if (bStartQuote)
            return DBConstants.SQL_START_QUOTE;
        else
            return DBConstants.SQL_END_QUOTE;
    }
    /**
     * Get the SQL type of this field.
     * Typically STRING, VARCHAR, or LONGSTRING if over 127 chars.
     * @param bIncludeLength Include the field length in this description.
     * @param properties Database properties to determine the SQL type.
     */
    public String getSQLType(boolean bIncludeLength, Map<String, Object> properties) 
    {
        String strType = (String)properties.get(DBSQLTypes.STRING);
        if (strType == null)
            strType = "VARCHAR";        // The default SQL Type
        if (this.getMaxLength() < 127)
        {
            String strStart = (String)properties.get("LONGSTRINGSTART");
            if (strStart != null)
            {
                int iStart = Integer.parseInt(strStart);
                if (iStart < this.getMaxLength())
                    strType = (String)properties.get("LONGSTRING");
            }
        }
        if (bIncludeLength)
            strType += "(" + Integer.toString(this.getMaxLength()) + ")";
        return strType;
    }
    /**
     * Retrieve (in string format) from this field.
     *  Data is already in string format, so just return it!
     * @return The string.
     */
    public String getString() 
    {       // Usually overidden
        int maxLength = this.getMaxLength();
        String string = (String)this.getData();
        if (string == null)
            return Constants.BLANK;
        if (string.length() > maxLength)
            string = string.substring(0, maxLength);
        return string;
    }
    /**
     * Current string length of this string.
     * @return The length.
     */
    public int getLength()
    {
        if (m_data == null)
            return 0;
        return ((String)m_data).length(); // Actual length
    }
    /**
     * Convert and move string to this field.
     *  Data is already in string format, so just move it!
     * @param bState the state to set the data to.
     * @param bDisplayOption Display the data on the screen if true.
     * @param iMoveMode INIT, SCREEN, or READ move mode.
     * @return The error code.
     */
    public int setString(String strString, boolean bDisplayOption, int iMoveMode)               // init this field override for other value
    {
        int iMaxLength = this.getMaxLength();
        if (strString != null) if (strString.length() > iMaxLength)
            strString = strString.substring(0, iMaxLength);
        if (strString == null)
            strString = Constants.BLANK;    // To set a null internally, you must call setData directly
        return this.setData(strString, bDisplayOption, iMoveMode);
    }
    /**
     * Get this field to the maximum or minimum value.<p>
     * @param iAreaDesc END_SELECT_KEY means set to largest value, others mean smallest.
     */
    public void setToLimit(int iAreaDesc)   // Set this field to the largest or smallest value
    {   // By default compare as ASCII strings
        String filler = Constants.BLANK;
        if (iAreaDesc == DBConstants.END_SELECT_KEY)
        {   // \376 is consistently the largest in the character set
            filler = HIGH_STRING; // Change the filler pointer
        }
        this.doSetData(filler, DBConstants.DONT_DISPLAY, DBConstants.SCREEN_MOVE);  // will fill with zeros
    }
    /**
     * Move this physical binary data to this field.
     * @param data The physical data to move to this field (must be the String data class).
     * @param bDisplayOption If true, display after setting the data.
     * @param iMoveMode The type of move.
     * @return an error code (0 if success).
     */
    public int doSetData(Object vpData, boolean bDisplayOption, int iMoveMode)
    {
        if ((vpData != null) && (!(vpData instanceof String)))
            return DBConstants.ERROR_RETURN;
        return super.doSetData(vpData, bDisplayOption, iMoveMode);
    }
}
