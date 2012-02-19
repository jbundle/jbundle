/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field;

/**
 * @(#)ShortField.java  0.00 12-Feb-97 Don Corley
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
 * ShortField - Set up the Short field.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class ShortField extends NumberField
{
	private static final long serialVersionUID = 1L;

	/**
     * A short zero.
     */
    public static final Short ZERO = new Short((short)0);
    /**
     * The smallest short value.
     */
    public static final Short MIN = new Short(Short.MIN_VALUE);
    /**
     * The largest short value.
     */
    public static final Short MAX = new Short(Short.MAX_VALUE);
    /**
     * Not-a-number for shorts.
     */
    public static final short NAN = (short)(Short.MIN_VALUE + 1);
    /**
     * Not-a-number for shorts.
     */
    public static Short ONE = new Short((short)1);

    /**
     * The default field length for a short field (x,xxx).
     */
    public static int SHORT_DEFAULT_LENGTH = 5;

    /**
     * Constructor.
     */
    public ShortField()
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
    public ShortField(Record record, String strName, int iDataLength, String strDesc, Object strDefault)
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
            m_iMaxLength = SHORT_DEFAULT_LENGTH;
        m_classData = Short.class;
    }
    /**
     * Creates a new object of the same class as this object.
     * @return     a clone of this instance.
     * @exception  CloneNotSupportedException  if the object's class does not support the <code>Cloneable</code> interface.
     * @see        java.lang.Cloneable
     */
    public Object clone() throws CloneNotSupportedException
    {
        BaseField field = new ShortField(null, m_strFieldName, m_iMaxLength, m_strFieldDesc, null);
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
        short short1 = Short.MIN_VALUE, short2 = Short.MAX_VALUE;
        Short tempShort = (Short)this.getData();    // Get the physical data
        if (tempShort != null)
            short1 = tempShort.shortValue();
        tempShort = (Short)field.getData();   // Get the physical data
        if (tempShort != null)
            short2 = tempShort.shortValue();
        if (short1 == short2)
            return 0;
        if (short1 < short2)
            return -1;
        else
            return 1;
    }
    /**
     * Move the physical binary data to this SQL parameter row.
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
            statement.setShort(iParamColumn, (short)this.getValue());
    }
    /**
     * Move the physical binary data to this SQL parameter row.
     * @param resultset The resultset to get the SQL data from.
     * @param iColumn the column in the resultset that has my data.
     * @exception SQLException From SQL calls.
     */
    public void moveSQLToField(ResultSet resultset, int iColumn) throws SQLException
    {
        short sResult = resultset.getShort(iColumn);
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
        Short shortField = (Short)this.getData(); // Get the physical data
        if (shortField == null)
            return 0;
        return (double)shortField.shortValue();
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
        Short tempshort = new Short((short)value);
        int errorCode = this.setData(tempshort, bDisplayOption, moveMode);
        return errorCode;
    }
    /**
     * Set this field to the maximum or minimum value.
     * @param iAreaDesc END_SELECT_KEY means set to largest value, others mean smallest.
     */
    public void setToLimit(int iAreaDesc) // Set this field to the largest or smallest value
    {   // By default compare as ASCII strings
        Short tempshort = MIN;        // Lowest value
        if (iAreaDesc == DBConstants.END_SELECT_KEY)
            tempshort = MAX;      // Highest value
        this.doSetData(tempshort, DBConstants.DONT_DISPLAY, DBConstants.SCREEN_MOVE);
    }
    /**
     * Move this physical binary data to this field.
     * @param data The physical data to move to this field (must be Short raw data class).
     * @param bDisplayOption If true, display after setting the data.
     * @param iMoveMode The type of move.
     * @return an error code (0 if success).
     */
    public int doSetData(Object vpData, boolean bDisplayOption, int iMoveMode)
    {
        if ((vpData != null) && (!(vpData instanceof Short)))
            return DBConstants.ERROR_RETURN;
        return super.doSetData(vpData, bDisplayOption, iMoveMode);
    }
    /**
     * Convert the native data type (Short) to a string.
     * @param tempBinary The physical data convert to a string (must be the raw data class).
     * @return A display string representing this binary data.
     */
    public String binaryToString(Object objData) 
    {
        initGlobals();
        if (objData == null)
            return Constants.BLANK;
        String strReturn = null;
        synchronized (gIntegerFormat)
        {
            strReturn = gIntegerFormat.format(((Short)objData).shortValue());
        }
        return strReturn;
    }
    /**
     * Convert this string to the native data type (Short).
     * @param tempString A string to be converted to this field's binary data.
     * @return The physical data converted from this string (must be the raw data class).
     */
    public Object stringToBinary(String string) throws Exception
    {
        return FieldInfo.stringToShort(string);
    }
    /**
     * Read the physical data from a stream file and set this field.
     * @param daIn Input stream to read this field's data from.
     * @param bFixedLength If false (default) be sure to save the length in the stream.
     * @return boolean Success?
     */
    public boolean read(DataInputStream daIn, boolean bFixedLength) // Fixed length = false
    {
        try   {
            short sData = daIn.readShort();
            Short shData = null;
            if (sData != NAN)
                shData = new Short(sData);
            int errorCode = this.setData(shData, DBConstants.DONT_DISPLAY, DBConstants.READ_MOVE);
            return (errorCode == DBConstants.NORMAL_RETURN);    // Success
        } catch (IOException ex)    {
            ex.printStackTrace();
            return false;
        }
    }
    /**
     * Write the physical data in this field to a stream file.
     * @param daOut Output stream to add this field to.
     * @param bFixedLength If false (default) be sure to get the length from the stream.
     * @return boolean Success?
     */
    public boolean write(DataOutputStream daOut, boolean bFixedLength)
    {
        try   {
            Short shData = (Short)this.getData();
            short sData;
            if (shData == null)
                sData = NAN;
            else
                sData = shData.shortValue();
            daOut.writeShort(sData);
            return true;
        } catch (IOException ex)    {
            ex.printStackTrace();
            return false;
        }
    }
    /**
     * Get the SQL type of this field.
     * Typically SHORT or SMALLINT.
     * @param bIncludeLength Include the field length in this description.
     * @param properties Database properties to determine the SQL type.
     * @return The SQL Type.
     */
    public String getSQLType(boolean bIncludeLength, Map<String, Object> properties) 
    {
        String strType = (String)properties.get(DBSQLTypes.SHORT);
        if (strType == null)
            strType = DBSQLTypes.SMALLINT;      // The default SQL Type (Byte)
        return  strType;        // The default SQL Type
    }
}
