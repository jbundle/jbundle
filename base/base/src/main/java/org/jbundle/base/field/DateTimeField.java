/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field;

/**
 * @(#)DateTimeField.java 0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.jbundle.base.db.Record;
import org.jbundle.base.db.SQLParams;
import org.jbundle.base.field.convert.DateConverter;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.DBSQLTypes;
import org.jbundle.base.model.ScreenConstants;
import org.jbundle.base.model.ScreenModel;
import org.jbundle.model.db.Convert;
import org.jbundle.model.db.Field;
import org.jbundle.model.screen.ComponentParent;
import org.jbundle.model.screen.ScreenComponent;
import org.jbundle.model.screen.ScreenLoc;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.Converter;
import org.jbundle.util.jcalendarbutton.JCalendarPopup;
import org.jbundle.util.jcalendarbutton.JTimePopup;

/**
 * DateTimeField - Set up the Date field.
 * This object is rarely used without one of the Date converters.
 * Do not use the default implementaion here, because it is very poor.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class DateTimeField extends NumberField
    implements PropertyChangeListener
{
	private static final long serialVersionUID = 1L;

	/**
     * Quote to surround date for a SQL query.
     */
    public static char gchSqlDateQuote = '#';
    /**
     * The default field length for a datetime field (mmm dd, yyyy hh:mm am++++).
     */
    public static int DATETIME_DEFAULT_LENGTH = 25;
    /**
     * Used for date calculations.
     */
    public static Calendar m_calendar = Calendar.getInstance();

    /**
     * Constructor.
     */
    public DateTimeField()
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
    public DateTimeField(Record record, String strName, int iDataLength, String strDesc, Object strDefault)
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
        if (m_calendar == null)
            m_calendar = Calendar.getInstance();
        super.init(record, strName, iDataLength, strDesc, strDefault);
        if (iDataLength == DBConstants.DEFAULT_FIELD_LENGTH)
            m_iMaxLength = DATETIME_DEFAULT_LENGTH;
        m_classData = java.util.Date.class;
    }
    /**
     * Creates a new object of the same class as this object.
     * @return     a clone of this instance.
     * @exception  CloneNotSupportedException  if the object's class does not support the <code>Cloneable</code> interface.
     * @see        java.lang.Cloneable
     */
    public Object clone() throws CloneNotSupportedException
    {
        BaseField field = new DateTimeField(null, m_strFieldName, m_iMaxLength, m_strFieldDesc, null);
        field.setRecord(m_record);     // Set table without adding to table field list
        return field;
    }
    /**
     * Convert the native data type (date) to a string.
     * Uses the binaryToString method in DateConverter.
     * @param tempBinary The physical data convert to a string (must be the raw data class).
     * @return A display string representing this binary data.
     */
    public String binaryToString(Object tempBinary) 
    {
        return DateConverter.binaryToString(tempBinary, DBConstants.HYBRID_DATE_TIME_FORMAT);
    }
    /**
     * Compare field to this and return < > or = (-,+,0).
     * @return compare value.
     */
    public int compareTo(Field field)
    {
        if (field instanceof BaseField) if (!this.isSameType((BaseField)field))
            return super.compareTo(field);
        m_calendar.set(DBConstants.FIRST_YEAR, Calendar.JANUARY, 1, 0, 0, 0);
        java.util.Date dateSmall = m_calendar.getTime();
        java.util.Date time1 = dateSmall;
        java.util.Date time2 = dateSmall;
        java.util.Date dateValue = (java.util.Date)this.getData();  // Get the physical data
        if (dateValue != null)
            time1 = dateValue;
        dateValue = (java.util.Date)field.getData();    // Get the physical data
        if (dateValue != null)
            time2 = dateValue;
        return time1.compareTo(time2);
    }
    /**
     * Get the HTML Input Type.
     * @return The html type (date).
     */
    public String getInputType(String strViewType)
    {
        if (ScreenModel.HTML_TYPE.equalsIgnoreCase(strViewType))
            return "date";
        else //if (TopScreen.XML_TYPE.equalsIgnoreCase(strViewType))
            return super.getInputType(strViewType);
    }
    /**
     * Move the physical binary data to this SQL parameter row.
     * This method used the setTimestamp statement method.
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
                statement.setNull(iParamColumn, Types.TIMESTAMP);
        }
        else
        {
            java.util.Date time = (java.util.Date)this.getData();
            Converter.initGlobals();
            Converter.gCalendar.setTime(time);
//x            if (Converter.gCalendar.getTimeZone().inDaylightTime(time)) // Hack (DST Problem?)
//x                Converter.gCalendar.add(Calendar.MILLISECOND, Converter.gCalendar.getTimeZone().getDSTSavings());
            java.sql.Timestamp sqlDate = new java.sql.Timestamp(Converter.gCalendar.getTimeInMillis());
            statement.setTimestamp(iParamColumn, sqlDate);
        }
    }
    /**
     * Move the physical binary data to this SQL parameter row.
     * This method uses the getTimestamp resultset method.
     * @param resultset The resultset to get the SQL data from.
     * @param iColumn the column in the resultset that has my data.
     * @exception SQLException From SQL calls.
     */
    public void moveSQLToField(ResultSet resultset, int iColumn) throws SQLException
    {
        java.util.Date dateResult = resultset.getTimestamp(iColumn);
        if (resultset.wasNull())
            this.setString(Constants.BLANK, false, DBConstants.READ_MOVE);  // Null value
        else
            this.setValue((double)dateResult.getTime(), false, DBConstants.READ_MOVE);
    }
    /**
     * Set the type of quote that goes around this SQL value.
     * @param gchSqlDateQuote The character to use to quote the date.
     */
    public static void setSQLQuote(char chSqlDateQuote)
    {
        gchSqlDateQuote = chSqlDateQuote;
    }
    /**
     * Return the type of quote that goes around this SQL value.
     * @return char
     * @param bStartQuote boolean
     */
    public char getSQLQuote(boolean bStartQuote)
    {
        return gchSqlDateQuote;
    }
    /**
     * Get this field in SQL format.
     * For dates, I use the DateConverter.binaryToString SQL formats (ie., XX/XX/XX).
     * @return The date formatted as a SQL string.
     */
    public String getSQLString()
    {
        return DateConverter.binaryToString(this.getData(), DBConstants.SQL_DATE_TIME_FORMAT);
    }
    /**
     * Get the Value of this field as a double.
     * Converts the time from getTime() to a double (may lose some precision).
     * @return The value of this field.
     */
    public double getValue()
    {           // Get this field's value
        java.util.Date dateValue = (java.util.Date)this.getData();  // Get the physical data
        if (dateValue == null)
            return 0;
        return (double)dateValue.getTime();
    }
    /**
     * Set the Value of this field as a double.
     * @param value The value of this field.
     * @param iDisplayOption If true, display the new field.
     * @param iMoveMove The move mode.
     * @return An error code (NORMAL_RETURN for success).
     */
    public int setValue(double value, boolean bDisplayOption, int iMoveMode)
    {           // Set this field's value
        java.util.Date dateTemp = new java.util.Date((long)value);
        int iErrorCode = this.setData(dateTemp, bDisplayOption, iMoveMode);
        return iErrorCode;
    }
    /**
     * Get this field as a java date (the raw date type).
     * @return This datetime.
     */
    public java.util.Date getDateTime()
    {
        return (java.util.Date)this.getData();  // Get the physical data
    }
    /**
     * Get this field as a java calendar value.
     * @return This field as a calendar value.
     */
    public Calendar getCalendar()
    {           // Get this field's value
        java.util.Date dateValue = (java.util.Date)this.getData();  // Get the physical data
        if (dateValue == null)
            return null;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateValue);
        return calendar;
    }
    /**
     * Change the date and time of day.
     * @param date The date to set.
     * @param bDisplayOption Display changed fields if true.
     * @param iMoveMode The move mode.
     * @return The error code (or NORMAL_RETURN).
     */
    public int setDateTime(java.util.Date date, boolean bDisplayOption, int iMoveMode)
    {
        if (date != null)
            return this.setValue(date.getTime(), bDisplayOption, iMoveMode);
        else
            return this.setData(null, bDisplayOption, iMoveMode);
    }
    /**
     * Change the date without changing the time.
     * @param date The date to set (only date portion is used).
     * @param bDisplayOption Display changed fields if true.
     * @param iMoveMode The move mode.
     * @return The error code (or NORMAL_RETURN).
     */
    public int setDate(java.util.Date date, boolean bDisplayOption, int iMoveMode)
    {
        java.util.Date m_DateTime = (java.util.Date)m_data;
        int iHour = 0;
        int iMinute = 0;
        int iSecond = 0;
        if (m_DateTime != null) //|| (m_CurrentLength == 0))
        {
            m_calendar.setTime(m_DateTime);
            iHour = m_calendar.get(Calendar.HOUR_OF_DAY);
            iMinute = m_calendar.get(Calendar.MINUTE);
            iSecond = m_calendar.get(Calendar.SECOND);
        }
        java.util.Date dateNew = null;
        if (date != null)
        {
	        m_calendar.setTime(date);
	        int iYear =  m_calendar.get(Calendar.YEAR);
	        int iMonth =  m_calendar.get(Calendar.MONTH);
	        int iDate =  m_calendar.get(Calendar.DATE);
	        m_calendar.set(iYear, iMonth, iDate, iHour, iMinute, iSecond);
	        dateNew = m_calendar.getTime();
        }
        return this.setDateTime(dateNew, bDisplayOption, iMoveMode);
    }
    /**
     * Change the time without changing the date.
     * @param time The date to set (only time portion is used).
     * @param bDisplayOption Display changed fields if true.
     * @param iMoveMode The move mode.
     * @return The error code (or NORMAL_RETURN).
     */
    public int setTime(java.util.Date time, boolean bDisplayOption, int iMoveMode)
    {
        java.util.Date m_DateTime = (java.util.Date)m_data;
        int iYear = DBConstants.FIRST_YEAR;
        int iMonth = DBConstants.FIRST_DAY;
        int iDate = DBConstants.FIRST_MONTH;
        if (m_DateTime != null) //|| (m_CurrentLength == 0))
        {
            m_calendar.setTime(m_DateTime);
            iYear = m_calendar.get(Calendar.YEAR);
            iMonth = m_calendar.get(Calendar.MONTH);
            iDate = m_calendar.get(Calendar.DATE);
        }
        m_calendar.setTime(time);
        int iHour = m_calendar.get(Calendar.HOUR_OF_DAY);
        int iMinute = m_calendar.get(Calendar.MINUTE);
        int iSecond = m_calendar.get(Calendar.SECOND);
        m_calendar.set(iYear, iMonth, iDate, iHour, iMinute, iSecond);
        java.util.Date dateNew = m_calendar.getTime();
        return this.setDateTime(dateNew, bDisplayOption, iMoveMode);
    }
    /**
     * Set to the min or max value.
     * @param iAreaDesc END_SELECT_KEY means set to largest value, others mean smallest.
     */
    public void setToLimit(int iAreaDesc) // Set this field to the largest or smallest value
    {   // By default compare as ASCII strings
        m_calendar.set(DBConstants.FIRST_YEAR, Calendar.JANUARY, 1, 0, 0, 0);
        if (iAreaDesc == DBConstants.END_SELECT_KEY)
            m_calendar.set(DBConstants.LAST_YEAR, Calendar.DECEMBER, 31, 23, 59, 59); // Highest value
        java.util.Date time = m_calendar.getTime();   // Lowest value
        this.doSetData(time, DBConstants.DONT_DISPLAY, DBConstants.SCREEN_MOVE);
    }
    /**
     * Set up the default screen control for this field (SEditText with a DateConverter followed by a SCannedBox).
     * @param itsLocation Location of this component on screen (ie., GridBagConstraint).
     * @param targetScreen Where to place this component (ie., Parent screen or GridBagLayout).
     * @param converter The converter to set the screenfield to.
     * @param iDisplayFieldDesc Display the label? (optional).
     * @return Return the component or ScreenField that is created for this field.
     * For a Date field, use DateConverter.
     */
    public ScreenComponent setupDefaultView(ScreenLoc itsLocation, ComponentParent targetScreen, Convert converter, int iDisplayFieldDesc, Map<String, Object> properties)
    {
        if (!(converter instanceof DateConverter))
            converter = new DateConverter((Converter)converter, DBConstants.HYBRID_DATE_TIME_FORMAT);
        int iFormatType = ((DateConverter)converter).getDateFormat();
        ScreenComponent screenField = createScreenComponent(ScreenModel.EDIT_TEXT, itsLocation, targetScreen, converter, iDisplayFieldDesc, properties);
        if ((iFormatType == DBConstants.DATE_FORMAT)
           || (iFormatType == DBConstants.DATE_TIME_FORMAT)
           || (iFormatType == DBConstants.DATE_ONLY_FORMAT)
           || (iFormatType == DBConstants.SHORT_DATE_FORMAT)
           || (iFormatType == DBConstants.SHORT_DATE_TIME_FORMAT)
           || (iFormatType == DBConstants.SHORT_DATE_ONLY_FORMAT)
           || (iFormatType == DBConstants.LONG_DATE_TIME_FORMAT)
           || (iFormatType == DBConstants.HYBRID_DATE_TIME_FORMAT))
        {   // Add Calendar button (If not HTML)
            properties = new HashMap<String,Object>();
            properties.put(ScreenModel.FIELD, this);
            properties.put(ScreenModel.COMMAND, JCalendarPopup.CALENDAR_ICON);
            properties.put(ScreenModel.IMAGE, JCalendarPopup.CALENDAR_ICON);
            ScreenComponent pSScreenField = createScreenComponent(ScreenModel.CANNED_BOX, targetScreen.getNextLocation(ScreenConstants.RIGHT_OF_LAST, ScreenConstants.DONT_SET_ANCHOR), targetScreen, converter, iDisplayFieldDesc, properties);
            pSScreenField.setRequestFocusEnabled(false);
        }
        if ((iFormatType == DBConstants.TIME_FORMAT)
           || (iFormatType == DBConstants.DATE_TIME_FORMAT)
           || (iFormatType == DBConstants.TIME_ONLY_FORMAT)
           || (iFormatType == DBConstants.SHORT_TIME_FORMAT)
           || (iFormatType == DBConstants.SHORT_DATE_TIME_FORMAT)
           || (iFormatType == DBConstants.SHORT_TIME_ONLY_FORMAT)
           || (iFormatType == DBConstants.LONG_DATE_TIME_FORMAT)
           || (iFormatType == DBConstants.HYBRID_DATE_TIME_FORMAT)
           || (iFormatType == DBConstants.LONG_TIME_ONLY_FORMAT)
           || (iFormatType == DBConstants.HYBRID_TIME_ONLY_FORMAT))
        {   // Add Calendar button (If not HTML)
            properties = new HashMap<String,Object>();
            properties.put(ScreenModel.FIELD, this);
            properties.put(ScreenModel.COMMAND, JTimePopup.TIME_ICON);
            properties.put(ScreenModel.IMAGE, JTimePopup.TIME_ICON);
            ScreenComponent pSScreenField = createScreenComponent(ScreenModel.CANNED_BOX, targetScreen.getNextLocation(ScreenConstants.RIGHT_OF_LAST, ScreenConstants.DONT_SET_ANCHOR), targetScreen, converter, iDisplayFieldDesc, properties);
            pSScreenField.setRequestFocusEnabled(false);
        }
        return screenField;
    }
    /**
     * SetValue in current calendar.
     * @param value The date (as a calendar value) to set.
     * @param bDisplayOption Display changed fields if true.
     * @param iMoveMode The move mode.
     * @return The error code (or NORMAL_RETURN).
     */
    public int setCalendar(Calendar value, boolean bDisplayOption, int iMoveMode)
    {           // Set this field's value
        java.util.Date dateTemp = value.getTime();
        int errorCode = this.setData(dateTemp, bDisplayOption, iMoveMode);
        return errorCode;
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
        return DateConverter.stringToBinary(tempString, dateOld, DBConstants.HYBRID_DATE_TIME_FORMAT);
    }
    /**
     * Return the current date with a 0 time.
     * @return The date.
     */
    public static double todaysDate()
    {
        java.util.Date date = new java.util.Date();
        m_calendar.setTime(date);
        m_calendar.set(Calendar.HOUR_OF_DAY, 0);
        m_calendar.set(Calendar.MINUTE, 0);
        m_calendar.set(Calendar.SECOND, 0);
        m_calendar.set(Calendar.MILLISECOND, 0);
        date = m_calendar.getTime();
        return date.getTime(); // Convert to double
    }
    /**
     * Return the current date and time as a double.
     * @return The current date and time.
     */
    public static double currentTime()
    {
        java.util.Date time = new java.util.Date();
        m_calendar.setTime(time);
//x        m_calendar.set(Calendar.SECOND, 0);
        m_calendar.set(Calendar.MILLISECOND, 0);
        time = m_calendar.getTime();
        return time.getTime(); // Convert to double
    }
    /**
     * Move this physical binary data to this field.
     * Must be java Date type.
     * @param data The physical data to move to this field (must be the correct raw data class).
     * @param bDisplayOption If true, display after setting the data.
     * @param iMoveMode The type of move.
     * @return an error code (0 if success).
     */
    public int doSetData(Object vpData, boolean bDisplayOption, int iMoveMode)
    {
        if ((vpData != null) && (!(vpData instanceof java.util.Date)))
            return DBConstants.ERROR_RETURN;
        return super.doSetData(vpData, bDisplayOption, iMoveMode);
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
            long lData = daIn.readLong();
            java.util.Date daData = null;
            if (lData != Long.MIN_VALUE + 1)
                daData = new java.util.Date(lData);
            int errorCode = this.setData(daData, DBConstants.DONT_DISPLAY, DBConstants.READ_MOVE);
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
            java.util.Date daData = (java.util.Date)this.getData();
            long lData;
            if (daData == null)
                lData = Long.MIN_VALUE + 1;
            else
                lData = daData.getTime();
            daOut.writeLong(lData);
            return true;
        } catch (IOException ex)    {
            ex.printStackTrace();
            return false;
        }
    }
    /**
     * Get the SQL type of this field.
     * Typically DATETIME.
     * @param bIncludeLength Include the field length in this description.
     * @param properties Database properties to determine the SQL type.
     */
    public String getSQLType(boolean bIncludeLength, Map<String, Object> properties) 
    {
        String strType = (String)properties.get(DBSQLTypes.DATETIME);
        if (strType == null)
            strType = DBSQLTypes.DATETIME;      // The default SQL Type
        return strType;
    }
    /**
     * This method gets called when a bound property is changed.
     * This is required to listen to changes by the date popup control.
     * @param evt A PropertyChangeEvent object describing the event source and the property that has changed.
     */
    public void propertyChange(PropertyChangeEvent evt)
    {
        if (JCalendarPopup.DATE_PARAM.equalsIgnoreCase(evt.getPropertyName()))
            if (evt.getNewValue() instanceof java.util.Date)
                this.setDateTime((java.util.Date)evt.getNewValue(), true, DBConstants.SCREEN_MOVE);
    }
}
