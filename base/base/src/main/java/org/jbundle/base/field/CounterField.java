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

import org.jbundle.base.db.Record;
import org.jbundle.base.db.SQLParams;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.DBSQLTypes;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.Converter;
import org.jbundle.util.osgi.BundleConstants;
import org.jbundle.util.osgi.finder.ClassServiceUtility;

import java.io.*;
import java.sql.*;
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
    public static Long MINUS_ONE = new Long(-1);

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
                        if (this.getRecord().getTable().getProperty("COUNTER_OBJECT_CLASS") != null) {
                            try {
                                this.setDataClass(Class.forName(this.getRecord().getTable().getProperty("COUNTER_OBJECT_CLASS")));
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
//    public void setToLimit(int iAreaDesc)   // Set this field to the largest or smallest value
//    {   // By default compare as ASCII strings
//        Integer tempLong = MIN;   // Lowest value
//        if (iAreaDesc == DBConstants.END_SELECT_KEY)
//            tempLong = MAX;   // Highest value
//        this.doSetData(tempLong, DBConstants.DONT_DISPLAY, DBConstants.SCREEN_MOVE);
//    }
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
        if (DBSQLTypes.BIGINT.equals(strType))
            sqlType = Types.BIGINT;
        else if (DBSQLTypes.SMALLINT.equals(strType))
            sqlType = Types.SMALLINT;
        return strType;        // The default SQL Type
    }
    protected int sqlType = Types.INTEGER;  // Cached SQL Type
    /**
     * Move the physical binary data to this SQL parameter row.
     * This is overidden to move the physical data type.
     * @param statement The SQL prepare statement.
     * @param iType the type of SQL statement.
     * @param iParamColumn The column in the prepared statement to set the data.
     * @exception SQLException From SQL calls.
     */
    public void getSQLFromField(PreparedStatement statement, int iType, int iParamColumn) throws SQLException
    {
        if (this.isNull())
        {
            if ((this.isNullable()) && (iType != DBConstants.SQL_SELECT_TYPE))
                statement.setNull(iParamColumn, Types.INTEGER);
            else {
                if (sqlType == Types.BIGINT)
                    statement.setLong(iParamColumn, NAN);
                else if (sqlType == Types.SMALLINT)
                    statement.setShort(iParamColumn, ShortField.NAN);
                else
                    statement.setInt(iParamColumn, IntegerField.NAN);
            }
        }
        else {
            if (sqlType == Types.BIGINT)
                statement.setLong(iParamColumn, (long)this.getValue());
            else if (sqlType == Types.SMALLINT)
                statement.setShort(iParamColumn, (short)this.getValue());
            else
                statement.setInt(iParamColumn, (int)this.getValue());
        }
    }
    /**
     * Move the physical binary data to this SQL parameter row.
     * This is overidden to move the physical data type.
     * @param resultset The resultset to get the SQL data from.
     * @param iColumn the column in the resultset that has my data.
     * @exception SQLException From SQL calls.
     */
    public void moveSQLToField(ResultSet resultset, int iColumn) throws SQLException
    {
//        ResultSetMetaData metaData = resultset.getMetaData();
//        int type = metaData.getColumnType(iColumn);
        long result = 0;
        if (sqlType == Types.BIGINT)
            result = resultset.getLong(iColumn);
        else if (sqlType == Types.SMALLINT)
            result = resultset.getShort(iColumn);
        else
            result = resultset.getInt(iColumn);
        if (resultset.wasNull())
            this.setString(Constants.BLANK, false, DBConstants.READ_MOVE);  // Null value
        else
        {
            if ((!this.isNullable()) &&
                    (((sqlType == Types.BIGINT) && (result == NAN)) ||
                     ((sqlType == Types.INTEGER) && (result == IntegerField.NAN)) ||
                     ((sqlType == Types.SMALLINT) && (result == ShortField.NAN))))
                this.setString(Constants.BLANK, false, DBConstants.READ_MOVE);  // Null value
            else
                this.setValue(result, false, DBConstants.READ_MOVE);
        }
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
     * Move this physical binary data to this field.
     * @param vpData The physical data to move to this field (must be an Integer raw data class).
     * @param bDisplayOption If true, display after setting the data.
     * @param iMoveMode The type of move.
     * @return an error code (0 if success).
     */
    public int doSetData(Object vpData, boolean bDisplayOption, int iMoveMode)
    {
        if ((vpData != null) && (vpData.getClass() != this.getDataClass()))
            return DBConstants.ERROR_RETURN;
        return super.doSetData(vpData, bDisplayOption, iMoveMode);
    }
    /**
     * Read the physical data from a stream file and set this field.
     * @param daIn Input stream to read this field's data from
     * @return boolean Success?
     */
    public boolean read(DataInputStream daIn, boolean bFixedLength) // Fixed length = false
    {
        try   {
            long iData = daIn.readLong();
            Long inData = null;
            if (iData != NAN)
                inData = new Long(iData);
            int iErrorCode = this.setData(inData, DBConstants.DONT_DISPLAY, DBConstants.READ_MOVE);
            return (iErrorCode == DBConstants.NORMAL_RETURN);    // Success
        } catch (IOException ex)    {
            ex.printStackTrace();
            return false;
        }
    }
    /**
     * Write the physical data in this field to a stream file.
     * @param daOut Output stream to add this field to.
     * @return boolean Success?
     */
    public boolean write(DataOutputStream daOut, boolean bFixedLength)
    {
        try   {
            Long inData = (Long)this.getData();
            long iData;
            if (inData == null)
                iData = NAN;
            else
                iData = inData.longValue();
            daOut.writeLong(iData);
            return true;
        } catch (IOException ex)    {
            ex.printStackTrace();
            return false;
        }
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
