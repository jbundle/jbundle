/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field;

/**
 * @(#)DateField.java 0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Calendar;
import java.util.Map;

import org.jbundle.base.db.Record;
import org.jbundle.base.db.SQLParams;
import org.jbundle.base.field.convert.DateConverter;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.DBSQLTypes;
import org.jbundle.model.db.Convert;
import org.jbundle.model.screen.ComponentParent;
import org.jbundle.model.screen.ScreenComponent;
import org.jbundle.model.screen.ScreenLoc;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.Converter;


/**
 * DateField - Set up the Date only.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class DateField extends DateTimeField
{
	private static final long serialVersionUID = 1L;

	/**
     * The default field length for a date field (mmm dd, yyyy).
     */
    public static int DATE_DEFAULT_LENGTH = 12;

    /**
     * Constructor.
     */
    public DateField()
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
    public DateField(Record record, String strName, int iDataLength, String strDesc, Object strDefault)
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
            m_iMaxLength = DATE_DEFAULT_LENGTH;
    }
    /**
     * Creates a new object of the same class as this object.
     * @return     a clone of this instance.
     * @exception  CloneNotSupportedException  if the object's class does not support the <code>Cloneable</code> interface.
     * @see        java.lang.Cloneable
     */
    public Object clone() throws CloneNotSupportedException
    {
        BaseField field = new DateField(null, m_strFieldName, m_iMaxLength, m_strFieldDesc, null);
        field.setRecord(m_record);     // Set table without adding to table field list
        return field;
    }
    /**
     * Set up the default screen control for this field (Default using a DateConverter).
     * @param itsLocation Location of this component on screen (ie., GridBagConstraint).
     * @param targetScreen Where to place this component (ie., Parent screen or GridBagLayout).
     * @param converter The converter to set the screenfield to.
     * @param iDisplayFieldDesc Display the label? (optional).
     * @return Return the component or ScreenField that is created for this field.
     * For a Date field, use DateConverter.
     */
    public ScreenComponent setupDefaultView(ScreenLoc itsLocation, ComponentParent targetScreen, Convert converter, int iDisplayFieldDesc, Map<String, Object> properties)
    {
        converter = new DateConverter((Converter)converter, DBConstants.DATE_FORMAT);
        return super.setupDefaultView(itsLocation, targetScreen, converter, iDisplayFieldDesc, properties);
    }
    /**
     * Convert the native data type (date) to a string.
     * Calls the DateConverter method.
     * @param tempBinary The physical data convert to a string (must be the raw data class).
     * @return A display string representing this binary data.
     * @see DateConverter#binaryToString(tempBinary, DBConstants.DATE_ONLY_FORMAT).
     */
    public String binaryToString(Object tempBinary) 
    {
        return DateConverter.binaryToString(tempBinary, DBConstants.DATE_ONLY_FORMAT);
    }
    /**
     * Convert this string to this field's binary data format.
     * Calls the DateConverter method.
     * @param tempString A string to be converted to this field's binary data.
     * @return The physical data converted from this string (must be the raw data class).
     */
    public Object stringToBinary(String tempString) throws Exception
    {
        java.util.Date dateOld = new java.util.Date((long)this.getValue());   // Save current time
        return DateConverter.stringToBinary(tempString, dateOld, DBConstants.DATE_ONLY_FORMAT);
    }
    /**
     * Change the date and time of day.
     * @param date The date to set (only date portion is used).
     * @param bDisplayOption Display changed fields if true.
     * @param iMoveMode The move mode.
     * @return The error code (or NORMAL_RETURN).
     */
    public int setDateTime(java.util.Date date, boolean bDisplayOption, int iMoveMode)
    {
        if (date == null)
            return this.setData(date, bDisplayOption, iMoveMode);
        m_calendar.setTime(date);
        m_calendar.set(Calendar.HOUR_OF_DAY, DBConstants.HOUR_DATE_ONLY);
        m_calendar.set(Calendar.MINUTE, 0);
        m_calendar.set(Calendar.SECOND, 0);
        m_calendar.set(Calendar.MILLISECOND, 0);
        date = m_calendar.getTime();
        return this.setValue(date.getTime(), bDisplayOption, iMoveMode);
    }
    /**
     * SetValue in current calendar.
     * @param value The date (as a calendar value) to set (only date portion is used).
     * @param bDisplayOption Display changed fields if true.
     * @param iMoveMode The move mode.
     * @return The error code (or NORMAL_RETURN).
     */
    public int setCalendar(Calendar value, boolean bDisplayOption, int moveMode)
    {           // Set this field's value
        if (value != null)
        {
            value.set(Calendar.HOUR_OF_DAY, 0);
            value.set(Calendar.MINUTE, 0);
            value.set(Calendar.SECOND, 0);
            value.set(Calendar.MILLISECOND, 0);
        }
        return super.setCalendar(value, bDisplayOption, moveMode);
    }
    /**
     * Move the physical binary data to this SQL parameter row.
     * @param resultset The resultset to get the SQL data from.
     * @param iColumn the column in the resultset that has my data.
     * @exception SQLException From SQL calls.
     */
    public void moveSQLToField(ResultSet resultset, int iColumn) throws SQLException
    {
        java.util.Date dateResult = resultset.getDate(iColumn);
        if (resultset.wasNull())
            this.setString(Constants.BLANK, false, DBConstants.READ_MOVE);  // Null value
        else
            this.setValue((double)dateResult.getTime(), false, DBConstants.READ_MOVE);
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
            if ((!this.isNullable())
                || (iType == DBConstants.SQL_SELECT_TYPE)
                || (DBConstants.FALSE.equals(this.getRecord().getTable().getDatabase().getProperties().get(SQLParams.NULL_TIMESTAMP_SUPPORTED)))) // HACK for Access
            {   // Access does not allow you to pass a null for a timestamp (must pass a 0)
                java.sql.Timestamp sqlDate = new java.sql.Timestamp(0);
                statement.setTimestamp(iParamColumn, sqlDate);
            }
            else
                statement.setNull(iParamColumn, Types.DATE);
        }
        else
        {
            java.sql.Date sqlDate = new java.sql.Date((long)this.getValue());
            statement.setDate(iParamColumn, sqlDate);
        }
    }
    /**
     * Get this field in SQL format.
     * For dates, I use the DateConverter.binaryToString SQL formats (ie., XX/XX/XX).
     * @return The date formatted as a SQL string.
     */
    public String getSQLString()
    {
        return DateConverter.binaryToString(this.getData(), DBConstants.SQL_DATE_FORMAT);
    }
    /**
     * Get the SQL type of this field.
     * Typically DATE.
     * @param bIncludeLength Include the field length in this description.
     * @param properties Database properties to determine the SQL type.
     */
    public String getSQLType(boolean bIncludeLength, Map<String, Object> properties) 
    {
        String strType = (String)properties.get(DBSQLTypes.DATE);
        if (strType == null)
            strType = DBSQLTypes.DATE;      // The default SQL Type (Byte)
        return strType;
    }
}
