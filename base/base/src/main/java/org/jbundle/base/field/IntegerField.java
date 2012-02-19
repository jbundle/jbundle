/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field;

/**
 * @(#)IntegerField.java    0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Map;

import org.jbundle.base.db.Record;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.DBSQLTypes;
import org.jbundle.model.db.Field;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.FieldInfo;


/**
 * IntegerField - Set up the Integer field.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class IntegerField extends NumberField
{
	private static final long serialVersionUID = 1L;

	/**
     * An Integer zero.
     */
    public static final Integer ZERO = new Integer(0);
    /**
     * The minimum integer value.
     */
    public static final Integer MIN = new Integer(Integer.MIN_VALUE);     // Lowest value
    /**
     * The maximum integer value.
     */
    public static final Integer MAX = new Integer(Integer.MAX_VALUE);     // Highest value
    /**
     * The not-a-number integer value.
     */
    public static final int NAN = Integer.MIN_VALUE + 1;
    /**
     * The not-a-number integer value.
     */
    public static Integer MINUS_ONE = new Integer(-1);
    /**
     * The default field length for a integer field (xx,xxx,xxx).
     */
    public static int INTEGER_DEFAULT_LENGTH = 10;

    /**
     * Constructor.
     */
    public IntegerField()
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
    public IntegerField(Record record, String strName, int iDataLength, String strDesc, Object strDefault)
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
        if (iDataLength == DBConstants.DEFAULT_FIELD_LENGTH)
            m_iMaxLength = INTEGER_DEFAULT_LENGTH;
        m_classData = Integer.class;
    }
    /**
     * Creates a new object of the same class as this object.
     * @return     a clone of this instance.
     * @exception  CloneNotSupportedException  if the object's class does not support the <code>Cloneable</code> interface.
     * @see        java.lang.Cloneable
     */
    public Object clone() throws CloneNotSupportedException
    {
        BaseField field = new IntegerField(null, m_strFieldName, m_iMaxLength, m_strFieldDesc, null);
        field.setRecord(m_record);     // Set table without adding to table field list
        return field;
    }
    /**
     * Compare field to this and return < > or = (-,+,0).
     * @return The compare value.
     */
    public int compareTo(Field field)
    {
        if (field instanceof BaseField) if (!this.isSameType((BaseField)field))
            return super.compareTo(field);
        int long1 = Integer.MIN_VALUE, long2 = Integer.MIN_VALUE;
        Integer tempLong = (Integer)this.getData();   // Get the physical data
        if (tempLong != null)
            long1 = tempLong.intValue();
        tempLong = (Integer)field.getData();    // Get the physical data
        if (tempLong != null)
            long2 = tempLong.intValue();
        if (long1 == long2)
            return 0;
        if (long1 < long2)
            return -1;
        else
            return 1;
    }
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
            else
                statement.setInt(iParamColumn, NAN);
        }
        else
            statement.setInt(iParamColumn, (int)this.getValue());
    }
    /**
     * Move the physical binary data to this SQL parameter row.
     * @param resultset The resultset to get the SQL data from.
     * @param iColumn the column in the resultset that has my data.
     * @exception SQLException From SQL calls.
     */
    public void moveSQLToField(ResultSet resultset, int iColumn) throws SQLException
    {
        int sResult = resultset.getInt(iColumn);
        if (resultset.wasNull())
            this.setString(Constants.BLANK, false, DBConstants.READ_MOVE);  // Null value
        else
        {
            if ((!this.isNullable()) && (sResult == NAN))
                this.setString(Constants.BLANK, false, DBConstants.READ_MOVE);  // Null value
            else
                this.setValue(sResult, false, DBConstants.READ_MOVE);
        }
    }
    /**
     * Get the Value of this field as a double.
     * @return The value of this field.
     */
    public double getValue()
    {           // Get this field's value
        Integer longField = (Integer)this.getData();    // Get the physical data
        if (longField == null)
            return 0;
        return (double)longField.intValue();
    }
    /*
     * Set the Value of this field as a double.
     * @param value The value of this field.
     * @param iDisplayOption If true, display the new field.
     * @param iMoveMove The move mode.
     * @return An error code (NORMAL_RETURN for success).
     */
    public int setValue(double value, boolean bDisplayOption, int moveMode)
    {           // Set this field's value
        Integer tempLong = new Integer((int)value);
        int iErrorCode = this.setData(tempLong, bDisplayOption, moveMode);
        return iErrorCode;
    }
    /**
     * Set this field to the maximum or minimum value.
     * @param iAreaDesc END_SELECT_KEY means set to largest value, others mean smallest.
     */
    public void setToLimit(int iAreaDesc)   // Set this field to the largest or smallest value
    {   // By default compare as ASCII strings
        Integer tempLong = MIN;   // Lowest value
        if (iAreaDesc == DBConstants.END_SELECT_KEY)
            tempLong = MAX;   // Highest value
        this.doSetData(tempLong, DBConstants.DONT_DISPLAY, DBConstants.SCREEN_MOVE);
    }
    /**
     * Convert the native data type (Integer) to a string.
     * @param tempBinary The physical data convert to a string (must be the raw data class).
     * @return A display string representing this binary data.
     */
    public String binaryToString(Object objData) 
    {
        initGlobals();
        if (objData == null)
            return Constants.BLANK;
        synchronized (gIntegerFormat)
        {
            return gIntegerFormat.format(((Integer)objData).intValue());
        }
    }
    /**
     * Convert this string to the native data type (Integer).
     * @param tempString A string to be converted to this field's binary data.
     * @return The physical data converted from this string (must be the raw data class).
     */
    public Object stringToBinary(String string) throws Exception
    {
        return FieldInfo.stringToInteger(string);
    }
    /**
     * Move this physical binary data to this field.
     * @param data The physical data to move to this field (must be an Integer raw data class).
     * @param bDisplayOption If true, display after setting the data.
     * @param iMoveMode The type of move.
     * @return an error code (0 if success).
     */
    public int doSetData(Object vpData, boolean bDisplayOption, int iMoveMode)
    {
        if ((vpData != null) && (!(vpData instanceof Integer)))
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
            int iData = daIn.readInt();
            Integer inData = null;
            if (iData != NAN)
                inData = new Integer(iData);
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
            Integer inData = (Integer)this.getData();
            int iData;
            if (inData == null)
                iData = NAN;
            else
                iData = inData.intValue();
            daOut.writeInt(iData);
            return true;
        } catch (IOException ex)    {
            ex.printStackTrace();
            return false;
        }
    }
    /**
     * Get the SQL type of this field.
     * Typically INTEGER.
     * @param bIncludeLength Include the field length in this description.
     * @param properties Database properties to determine the SQL type.
     */
    public String getSQLType(boolean bIncludeLength, Map<String, Object> properties) 
    {
        String strType = (String)properties.get(DBSQLTypes.INTEGER);
        if (strType == null)
            strType = DBSQLTypes.INTEGER;   // The default SQL Type (Byte)
        return  strType;        // The default SQL Type
    }
}
