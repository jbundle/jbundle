package org.jbundle.base.field;

/**
 * @(#)DoubleField.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
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
import org.jbundle.base.screen.model.TopScreen;
import org.jbundle.base.util.DBConstants;
import org.jbundle.base.util.DBSQLTypes;
import org.jbundle.model.db.Field;
import org.jbundle.thin.base.db.Constants;


/**
 * DoubleField - Set up the Double field.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class DoubleField extends NumberField
{
	private static final long serialVersionUID = 1L;

    /**
     * A double 0.
     */
    public static final Double ZERO = new Double(0.00);
    /**
     * The max double value.
     */
    public static final Double MIN = new Double(Double.MIN_VALUE);
    /**
     * The min double value.
     */
    public static final Double MAX = new Double(Double.MAX_VALUE);
    /**
     * The default field length for a double field (xxx,xxx,xxx,xxx.xx).
     */
    public static int DOUBLE_DEFAULT_LENGTH = 18;

    /**
     * Constructor.
     */
    public DoubleField()
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
    public DoubleField(Record record, String strName, int iDataLength, String strDesc, Object strDefault)
    {
        this();
        this.init(record, strName, iDataLength, strDesc, strDefault);
    }
    /**
     * init.
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
            m_iMaxLength = DOUBLE_DEFAULT_LENGTH;
        m_ibScale = 2;  // Default
        m_classData = Double.class;
    }
    /**
     * Creates a new object of the same class as this object.
     * @return     a clone of this instance.
     * @exception  CloneNotSupportedException  if the object's class does not support the <code>Cloneable</code> interface.
     * @see        java.lang.Cloneable
     */
    public Object clone() throws CloneNotSupportedException
    {
        BaseField field = new DoubleField(null, m_strFieldName, m_iMaxLength, m_strFieldDesc, null);
        field.setRecord(m_record);     // Set table without adding to table field list
        return field;
    }
    /**
     * Compare field to this and return < > or = (-,+,0).
     */
    public int compareTo(Field field)
    {
        if (field instanceof BaseField) if (!this.isSameType((BaseField)field))
            return super.compareTo(field);
        double double1 = Double.MIN_VALUE, double2 = Double.MIN_VALUE;
        Double doubleField = (Double)this.getData();    // Get the physical data
        if (doubleField != null)
            double1 = doubleField.doubleValue();
        doubleField = (Double)field.getData();  // Get the physical data
        if (doubleField != null)
            double2 = doubleField.doubleValue();
        if (double1 == double2)
            return 0;
        if (double1 < double2)
            return -1;
        else
            return 1;
    }
    /**
     * Get the HTML Input Type.
     */
    public String getInputType(String strViewType)
    {
        if (TopScreen.HTML_TYPE.equalsIgnoreCase(strViewType))
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
                statement.setNull(iParamColumn, Types.DOUBLE);
            else
                statement.setDouble(iParamColumn, Double.NaN);
        }
        else
            statement.setDouble(iParamColumn, this.getValue());
    }
    /**
     * Move the physical binary data to this SQL parameter row.
     * @param resultset The resultset to get the SQL data from.
     * @param iColumn the column in the resultset that has my data.
     * @exception SQLException From SQL calls.
     */
    public void moveSQLToField(ResultSet resultset, int iColumn) throws SQLException
    {
        double dResult = resultset.getDouble(iColumn);
        if (resultset.wasNull())
            this.setString(Constants.BLANK, false, DBConstants.READ_MOVE);  // Null value
        else
        {
            if ((!this.isNullable()) && (dResult == Double.NaN))
                this.setString(Constants.BLANK, false, DBConstants.READ_MOVE);  // Null value
            else
                this.setValue(dResult, false, DBConstants.READ_MOVE);
        }
    }
    /**
     * Get the Value of this field as a double.
     * @return The value of this field.
     */
    public double getValue()
    {           // Get this field's value
        Double doubleField = (Double)this.getData();    // Get the physical data
        if (doubleField == null)
            return 0;
        return doubleField.doubleValue();
    }
    /*
     * Set the Value of this field as a double.
     * @param value The value of this field.
     * @param iDisplayOption If true, display the new field.
     * @param iMoveMove The move mode.
     * @return An error code (NORMAL_RETURN for success).
     */
    public int setValue(double value, boolean bDisplayOption, int iMoveMode)
    {           // Set this field's value
        double dRoundAt = Math.pow(10, m_ibScale);
        Double tempdouble = new Double(Math.floor(value * dRoundAt + 0.5) / dRoundAt);
        int errorCode = this.setData(tempdouble, bDisplayOption, iMoveMode);
        return errorCode;
    }
    /**
     * Set this field to the maximum or minimum value.
     * @param iAreaDesc END_SELECT_KEY means set to largest value, others mean smallest.
     */
    public void setToLimit(int iAreaDesc) // Set this field to the largest or smallest value
    {   // By default compare as ASCII strings
        Double tempdouble = MIN;     // Lowest value
        if (iAreaDesc == DBConstants.END_SELECT_KEY)
            tempdouble =  MAX;
        this.doSetData(tempdouble, DBConstants.DONT_DISPLAY, DBConstants.SCREEN_MOVE);
    }
    /**
     * Convert the native data type (double) to a string.
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
            string = gNumberFormat.format(((Double)objData).doubleValue());
            if (m_ibScale != 2)
            {
                gNumberFormat.setMinimumFractionDigits(2);
                gNumberFormat.setMaximumFractionDigits(2);
            }
        }
        return string;
    }
    /**
     * Convert this string to the native data type (date).
     * @param tempString A string to be converted to this field's binary data.
     * @return The physical data converted from this string (must be the raw data class).
     */
    public Object stringToBinary(String string) throws Exception
    {
        return this.stringToDouble(string);
    }
    /**
     * Move the physical binary data to this field (Check the data type) (debug).
     */
    public int doSetData(Object vpData, boolean bDisplayOption, int iMoveMode)
    {
        if ((vpData != null) && (!(vpData instanceof Double)))
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
            double dData = daIn.readDouble();
            Double doData = null;
            if (!Double.isNaN(dData))
                doData = new Double(dData);
            int errorCode = this.setData(doData, DBConstants.DONT_DISPLAY, DBConstants.READ_MOVE);
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
            Double doData = (Double)this.getData();
            double dData;
            if (doData == null)
                dData = Double.NaN;
            else
                dData = doData.doubleValue();
            daOut.writeDouble(dData);
            return true;
        } catch (IOException ex)    {
            ex.printStackTrace();
            return false;
        }
    }
    /**
     * Get the SQL type of this field.
     * Typically DOUBLE.
     * @param bIncludeLength Include the field length in this description.
     * @param properties Database properties to determine the SQL type.
     */
    public String getSQLType(boolean bIncludeLength, Map<String, Object> properties) 
    {
        String strType = (String)properties.get(DBSQLTypes.DOUBLE);
        if (strType == null)
            strType = DBSQLTypes.DOUBLE;        // The default SQL Type (Byte)
        return strType;
    }
}
