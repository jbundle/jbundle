/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field;

/**
 * @(#)FloatField.java  0.00 12-Feb-97 Don Corley
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
import org.jbundle.base.db.SQLParams;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.DBSQLTypes;
import org.jbundle.base.model.ScreenModel;
import org.jbundle.model.db.Field;
import org.jbundle.thin.base.db.Constants;


/**
 * FloatField - Set up the Float field.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class FloatField extends NumberField
{
	private static final long serialVersionUID = 1L;

	/**
     * A Float zero.
     */
    public static final Float ZERO = new Float(0.00);
    /**
     * The smallest float value.
     */
    public static final Float MIN = new Float(Float.MIN_VALUE);
    /**
     * The largest float value.
     */
    public static final Float MAX = new Float(Float.MAX_VALUE);
    /**
     * The default field length for a float field (x,xxx.xx).
     */
    public static int FLOAT_DEFAULT_LENGTH = 8;


    /**
     * Constructor.
     */
    public FloatField()
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
    public FloatField(Record record, String strName, int iDataLength, String strDesc, Object strDefault)
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
            m_iMaxLength = FLOAT_DEFAULT_LENGTH;
        m_ibScale = 2;  // Default
        m_classData = Float.class;
    }
    /**
     * Creates a new object of the same class as this object.
     * @return     a clone of this instance.
     * @exception  CloneNotSupportedException  if the object's class does not support the <code>Cloneable</code> interface.
     * @see        java.lang.Cloneable
     */
    public Object clone() throws CloneNotSupportedException
    {
        BaseField field = new FloatField(null, m_strFieldName, m_iMaxLength, m_strFieldDesc, null);
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
        float float1 = Float.MIN_VALUE, float2 = Float.MIN_VALUE;
        Float floatField = (Float)this.getData(); // Get the physical data
        if (floatField != null)
            float1 = floatField.floatValue();
        floatField = (Float)field.getData();    // Get the physical data
        if (floatField != null)
            float2 = floatField.floatValue();
        if (float1 == float2)
            return 0;
        if (float1 < float2)
            return -1;
        else
            return 1;
    }
    /**
     * Get the HTML Input Type.
     * @return The html type (float).
     */
    public String getInputType(String strViewType)
    {
        if (ScreenModel.HTML_TYPE.equalsIgnoreCase(strViewType))
            return "float";
        else //if (TopScreen.XML_TYPE.equalsIgnoreCase(strViewType))
            return super.getInputType(strViewType);
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
                statement.setNull(iParamColumn, Types.FLOAT);
            else
                statement.setFloat(iParamColumn, Float.NaN);
        }
        else
        {
            if (DBConstants.FALSE.equals(this.getRecord().getTable().getDatabase().getProperties().get(SQLParams.FLOAT_XFER_SUPPORTED)))    // HACK for Access
                super.getSQLFromField(statement, iType, iParamColumn);
            else
                statement.setFloat(iParamColumn, (float)this.getValue());
        }
    }
    /**
     * Move the physical binary data to this SQL parameter row.
     * @param resultset The resultset to get the SQL data from.
     * @param iColumn the column in the resultset that has my data.
     * @exception SQLException From SQL calls.
     */
    public void moveSQLToField(ResultSet resultset, int iColumn) throws SQLException
    {
        float fResult = resultset.getFloat(iColumn);
        if (resultset.wasNull())
            this.setString(Constants.BLANK, false, DBConstants.READ_MOVE);  // Null value
        else
        {
            if ((!this.isNullable()) && (fResult == Float.NaN))
                this.setString(Constants.BLANK, false, DBConstants.READ_MOVE);  // Null value
            else
                this.setValue(fResult, false, DBConstants.READ_MOVE);
        }
    }
    /**
     * Get the Value of this field as a double.
     * @return The value of this field.
     */
    public double getValue()
    {           // Get this field's value
        Float floatField = (Float)this.getData(); // Get the physical data
        if (floatField == null)
            return 0.00;
        return floatField.floatValue();
    }
    /*
     * Set the Value of this field as a double.
     * Note The value is rounded at the scale position.
     * @param value The value of this field.
     * @param iDisplayOption If true, display the new field.
     * @param iMoveMove The move mode.
     * @return An error code (NORMAL_RETURN for success).
     */
    public int setValue(double value, boolean bDisplayOption, int iMoveMode)
    {           // Set this field's value
        double dRoundAt = Math.pow(10, m_ibScale);
        Float tempfloat = new Float((float)(Math.floor(value * dRoundAt + 0.5) / dRoundAt));
        int iErrorCode = this.setData(tempfloat, bDisplayOption, iMoveMode);
        return iErrorCode;
    }
    /**
     * Set this field to the maximum or minimum value.
     * @param iAreaDesc END_SELECT_KEY means set to largest value, others mean smallest.
     */
    public void setToLimit(int iAreaDesc) // Set this field to the largest or smallest value
    {   // By default compare as ASCII strings
        Float tempfloat = MIN;          // Lowest value
        if (iAreaDesc == DBConstants.END_SELECT_KEY)
            tempfloat = MAX;
        this.doSetData(tempfloat, DBConstants.DONT_DISPLAY, DBConstants.SCREEN_MOVE);
    }
    /**
     * Convert this field's binary data to a string.
     * @param tempBinary The physical data convert to a string (must be the raw data class).
     * @return A display string representing this binary data.
     */
    public String binaryToString(Object objData) 
    {
        initGlobals();
        if (objData == null)
            return Constants.BLANK;
        String string = null;
        synchronized (gNumberFormat)
        {
            if (m_ibScale != 2)
            {
                gNumberFormat.setMinimumFractionDigits(m_ibScale);
                gNumberFormat.setMaximumFractionDigits(m_ibScale);
            }
            string = gNumberFormat.format(((Float)objData).floatValue());
            if (m_ibScale != 2)
            {
                gNumberFormat.setMinimumFractionDigits(2);
                gNumberFormat.setMaximumFractionDigits(2);
            }
        }
        return string;
    }
    /**
     * Convert this string to this field's binary data format.
     * @param tempString A string to be converted to this field's binary data.
     * @return The physical data converted from this string (must be the raw data class).
     */
    public Object stringToBinary(String string) throws Exception
    {
        return this.stringToFloat(string);
    }
    /**
     * Move this physical binary data to this field.
     * @param data The physical data to move to this field (must be Float raw data class).
     * @param bDisplayOption If true, display after setting the data.
     * @param iMoveMode The type of move.
     * @return an error code (0 if success).
     */
    public int doSetData(Object data, boolean bDisplayOption, int iMoveMode)
    {
        if ((data != null) && (!(data instanceof Float)))
            return DBConstants.ERROR_RETURN;
        return super.doSetData(data, bDisplayOption, iMoveMode);
    }
    /**
     * Read the physical data from a stream file and set this field.
     * @param daIn Input stream to read this field's data from
     * @return boolean Success?
     */
    public boolean read(DataInputStream daIn, boolean bFixedLength) // Fixed length = false
    {
        try   {
            float fData = daIn.readFloat();
            Float flData = null;
            if (!Float.isNaN(fData))
                flData = new Float(fData);
            int errorCode = this.setData(flData, DBConstants.DONT_DISPLAY, DBConstants.READ_MOVE);
            return (errorCode == DBConstants.NORMAL_RETURN);    // Success
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
            Float flData = (Float)this.getData();
            float fData;
            if (flData == null)
                fData = Float.NaN;
            else
                fData = flData.floatValue();
            daOut.writeFloat(fData);
            return true;
        } catch (IOException ex)    {
            ex.printStackTrace();
            return false;
        }
    }
    /**
     * Get the SQL type of this field.
     * Typically FLOAT.
     * @param bIncludeLength Include the field length in this description.
     * @param properties Database properties to determine the SQL type.
     */
    public String getSQLType(boolean bIncludeLength, Map<String, Object> properties) 
    {
        String strType = (String)properties.get(DBSQLTypes.FLOAT);
        if (strType == null)
            strType = DBSQLTypes.FLOAT;     // The default SQL Type (Byte)
        return  strType;        // The default SQL Type
    }
}
