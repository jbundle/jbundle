/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field;

/**
 * @(#)CounterField.java    0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import org.bson.types.ObjectId;
import org.jbundle.base.db.Record;
import org.jbundle.base.db.SQLParams;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.DBSQLTypes;
import org.jbundle.model.DBException;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.Converter;

import java.util.Map;

/**
 * CounterField - This is the field class for an auto-sequence field.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class CounterField extends ObjectField
{
	private static final long serialVersionUID = 1L;

    /**
     * Alternate counter field name
     */
	protected String alternateFieldName = null;

    /**
     * An Integer zero.
     */
    public static final Long ZERO = new Long(0);
    /**
     * The minimum integer value.
     */
    public static final Long MIN = Long.MIN_VALUE;     // Lowest value
    /**
     * The maximum integer value.
     */
    public static final Long MAX = Long.MAX_VALUE;     // Highest value
    /**
     * The not-a-number integer value.
     */
    public static final long NAN = Long.MIN_VALUE + 1;
    /**
     * The not-a-number integer value.
     */
    public static final Long MINUS_ONE = new Long(-1);
    public static final String MINHEX = "000000000000000000000000";
    public static final String MAXHEX = "ffffffffffffffffffffffff";

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
     * Get the class of the m_data field.
     * @return the java class of the raw data.
     */
    public Class<?> getDataClass()
    {
        if (m_classData == Object.class) {    // First time only
            if (this.getRecord() != null) // Always
                if (this.getRecord().getTable() != null)
                    if (this.getRecord().getTable().getDatabase() != null)
                        if (this.getRecord().getTable().getDatabase().getProperty(SQLParams.COUNTER_OBJECT_CLASS) != null) {
                            try {
                                this.setDataClass(Class.forName(this.getRecord().getTable().getDatabase().getProperty(SQLParams.COUNTER_OBJECT_CLASS)));
                            } catch (ClassNotFoundException e) {
                                // Ignore - Integer is fine
                            }
                        }
            if (m_classData == Object.class)
                this.setDataClass(Integer.class);   // Default
        }
        return super.getDataClass();
    }
    /**
     * Get the data from this field in the native mongo format
     * @return The data from this field in raw format.
     */
    public Object getBsonData() throws DBException
    {
        if (this.isNull())
            return null;
            try {
                return new ObjectId(padLeft(stripNonNumber(this.getString().indexOf('.')==-1 ? this.getString() : this.getString().substring(0, this.getString().indexOf('.')), true), 24, '0'));
            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println(ex.getMessage());
                throw this.getRecord().getTable().getDatabase().convertError(ex);
            }
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
     * Set this field to the maximum or minimum value.
     * @param iAreaDesc END_SELECT_KEY means set to largest value, others mean smallest.
     */
    public void setToLimit(int iAreaDesc)   // Set this field to the largest or smallest value
    {   // By default compare as ASCII strings
        Object tempLong = null;
        if (iAreaDesc == DBConstants.START_SELECT_KEY) {
            if (this.getDataClass() == Long.class)
                tempLong = MIN;   // Lowest value
            else if (this.getDataClass() == Short.class)
                tempLong = ShortField.MIN;   // Lowest value
            else if (this.getDataClass() == Integer.class)
                tempLong = IntegerField.MIN;   // Lowest value
            else if (this.getDataClass() == String.class)
                tempLong = MINHEX;   // Lowest value
        }
        if (iAreaDesc == DBConstants.END_SELECT_KEY) {
            if (this.getDataClass() == Long.class)
                tempLong = MAX;   // Highest value
            else if (this.getDataClass() == Short.class)
                tempLong = ShortField.MAX;   // Highest value
            else if (this.getDataClass() == Integer.class)
                tempLong = IntegerField.MAX;   // Highest value
            else if (this.getDataClass() == String.class)
                tempLong = MAXHEX;   // Highest value
        }
        this.doSetData(tempLong, DBConstants.DONT_DISPLAY, DBConstants.SCREEN_MOVE);
    }
    /**
     * Get the SQL type of this field.
     * Typically OBJECT or LONGBINARY.
     * @param bIncludeLength Include the field length in this description.
     * @param properties Database properties to determine the SQL type.
     */
    public String getSQLType(boolean bIncludeLength, Map<String, Object> properties)
    {
        String strType = (String)properties.get(DBSQLTypes.COUNTER);
        if (strType == null)
            strType = DBSQLTypes.INTEGER;     // The default SQL Type (Integer)
        return strType;        // The default SQL Type
    }
    /**
     * Convert the native data type (Integer) to a string.
     * @param objData The physical data convert to a string (must be the raw data class).
     * @return A display string representing this binary data.
     */
    public String binaryToString(Object objData)
    {
        initGlobals();
        if (objData == null)
            return Constants.BLANK;
        try {
            return Converter.formatObjectToString(objData, this.getDataClass(), Constants.BLANK);
        } catch (Exception e) {
            return Constants.BLANK;
        }
    }
    /**
     * Convert this string to the native data type (Integer).
     * @param string A string to be converted to this field's binary data.
     * @return The physical data converted from this string (must be the raw data class).
     */
    public Object stringToBinary(String string) throws Exception
    {
        return Converter.convertObjectToDatatype(string, this.getDataClass(), null);
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
    /**
     * Get this field's name.
     * @param addQuotes Add quotes if this field contains a space.
     * @param includeFileName include the file name as file.field.
     * @param externalName Retrieve the field name in the external (SQL?) table
     * @return The field name.
     */
    public String getFieldName(boolean addQuotes, boolean includeFileName, boolean externalName)
    {
        if (externalName)
            if (alternateFieldName != null)
                return alternateFieldName;
        return super.getFieldName(addQuotes, includeFileName, externalName);
    }
    /**
     * Set the alternate field name.
     * @param alternateFieldName
     */
    public void setAlternateFieldName(String alternateFieldName) {
        this.alternateFieldName = alternateFieldName;
    }
}
