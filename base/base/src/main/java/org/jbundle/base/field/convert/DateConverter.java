/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field.convert;

/**
 * @(#)DateConverter.java 0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import org.jbundle.base.field.BaseField;
import org.jbundle.base.model.DBConstants;
import org.jbundle.model.Task;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.Converter;
import org.jbundle.thin.base.db.FieldInfo;


/**
 * This class does all the date conversions.
 * Note: I completely rely on this computer's date formatting with one extra test for year 2000.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class DateConverter extends FieldConverter
{
    /**
     * The date format to use.
     */
    protected int m_sDateFormat = -1;

    /**
     * Constructor.
     */
    public DateConverter()
    {
        super();
    }
    /**
     * Constructor.
     * @param converter The next converter in the converter chain.
     * @param dateFormat The date format.
     */
    public DateConverter(Converter converter, int dateFormat)
    {
        this();
        this.init(converter, dateFormat);
    }
    /**
     * Initialize this converter.
     * @param converter The next converter in the converter chain.
     * @param dateFormat The date format.
     */
    public void init(Converter converter, int dateFormat)
    {
        Converter.initGlobals();
        super.init(converter);
        m_sDateFormat = dateFormat;
    }
    /**
     * Free this converter.
     */
    public void free()
    {
        super.free();
    }
    /**
     * Retrieve (in string format) from this field.
     *  Data is already in binary format, so convert it and return the string.
     * @return This converter as a (date) string.
     */
    public String getString() 
    {
        Object tempBinary = this.getData();  // Get the physical data
        if (tempBinary != null)
            return DateConverter.binaryToString(tempBinary, m_sDateFormat);
        else
            return Constants.BLANK;                         // Clear BaseField
    }
    /**
     * Get date format.
     * @return The date format.
     */
    public int getDateFormat()
    {
        return m_sDateFormat;
    }
    /**
     * Convert and move string to this field.
     * @param bState the state to set the data to.
     * @param bDisplayOption Display the data on the screen if true.
     * @param iMoveMode INIT, SCREEN, or READ move mode.
     * @return The error code.
     */
    public int setString(String strField, boolean bDisplayOption, int iMoveMode)
    {
        int fieldLength = 0;
        Object tempBinary = null;
        if (strField != null)
        {
            fieldLength = strField.length();
            int maxLength = this.getMaxLength();
            if ((fieldLength > maxLength) || (fieldLength > 40))
                fieldLength = maxLength;
            Date dateOld = new Date((long)this.getValue());  // Save current time
            try   {
                tempBinary = DateConverter.stringToBinary(strField, dateOld, m_sDateFormat);
            } catch (Exception ex)  {
                String strError = ex.getMessage();
                if (strError == null)
                    strError = ex.getClass().getName();
                Task task = null;
                if (this.getField() != null)
                    if (((BaseField)this.getField()).getRecord() != null)
                        if (((BaseField)this.getField()).getRecord().getRecordOwner() != null)
                            task = ((BaseField)this.getField()).getRecord().getRecordOwner().getTask();
                //if (task == null)
                //    task = BaseApplet.getSharedInstance();
                return task.setLastError(strError);
            }
        }
        return this.setData(tempBinary, bDisplayOption, iMoveMode);
    }
    /**
     * Convert the native data type (date) to a string.
     * Note that I convert the date to a GMT string. This is the only
     * string that I can guarantee the format to be the same across platform/locale.
     * @param tempBinary The date object to convert to a string.
     * @param sDateFormat The date format.
     * @return The date string.
     */
    public static String binaryToString(Object tempBinary, int sDateFormat) 
    {
        String aStr = Constants.BLANK;
        if (!(tempBinary instanceof Date))
            return aStr;
        Date date = new Date(((Date)tempBinary).getTime());
        if (date.getTime() == 0)
            return aStr;
        if (gDateFormat == null)
            initGlobals();
        synchronized (gCalendar)
        {
            if ((sDateFormat == DBConstants.DATE_ONLY_FORMAT) || (sDateFormat == DBConstants.DATE_FORMAT))
                aStr = gDateFormat.format(date);
            else if ((sDateFormat == DBConstants.SHORT_DATE_ONLY_FORMAT) || (sDateFormat == DBConstants.SHORT_DATE_FORMAT))
                aStr = gDateShortFormat.format(date);
            else if ((sDateFormat == DBConstants.TIME_ONLY_FORMAT) || (sDateFormat == DBConstants.TIME_FORMAT))
                aStr = gTimeFormat.format(date);
            else if ((sDateFormat == DBConstants.SHORT_TIME_ONLY_FORMAT) || (sDateFormat == DBConstants.SHORT_TIME_FORMAT))
                aStr = gDateShortTimeFormat.format(date);
            else if (sDateFormat == DBConstants.SQL_DATE_FORMAT)
                aStr = gDateSqlFormat.format(date);
            else if (sDateFormat == DBConstants.SQL_TIME_FORMAT)
                aStr = gTimeSqlFormat.format(date);
            else if (sDateFormat == DBConstants.SQL_DATE_TIME_FORMAT)
                aStr = gDateTimeSqlFormat.format(date);
            else if (sDateFormat == DBConstants.LONG_TIME_ONLY_FORMAT)
                aStr = gLongTimeFormat.format(date);
            else if (sDateFormat == DBConstants.LONG_DATE_TIME_FORMAT)
                aStr = gLongDateTimeFormat.format(date);
            else if (sDateFormat == DBConstants.HYBRID_TIME_ONLY_FORMAT)
            {
                {
                    gCalendar.setTime(date);
                    if (gCalendar.get(Calendar.SECOND) > 0)
                        aStr = gLongTimeFormat.format(date);
                    else
                        aStr = gTimeFormat.format(date);
                }
            }
            else if (sDateFormat == DBConstants.HYBRID_DATE_TIME_FORMAT)
            {
                gCalendar.setTime(date);
                if (gCalendar.get(Calendar.SECOND) > 0)
                    aStr = gLongDateTimeFormat.format(date);
                else
                    aStr = gDateTimeFormat.format(date);
            }
            else
                aStr = gDateTimeFormat.format(date);
        }
        return aStr;
    }
    /**
     * Convert this string to the native data type (Date).<p>
     * Note: I completely rely on this computer's date formatting with one extra test for year 2000.
     * (69 = 2069, 70 = 1970).
     * @param tempString The string to convert to a date.
     * @param dateOld The old date (in case you need time/date for certain conversions).
     * @param spDateFormat The date format.
     * @return The date.
     */
    public static Date stringToBinary(String tempString, Date dateOld, int spDateFormat) throws Exception
    {
        int sDateFormat = spDateFormat;
        Date date = null; //date.SetStatus(Date.null);
        synchronized (Converter.gCalendar)
        {
            if (tempString == null)
                date = null;
            else if (tempString.equals(Constants.BLANK))
                date = null;
            else
            {
                if (gDateFormat == null)
                    initGlobals();
                if (sDateFormat == DBConstants.HYBRID_DATE_TIME_FORMAT)
                    sDateFormat = DBConstants.LONG_DATE_TIME_FORMAT;
                if (sDateFormat == DBConstants.LONG_DATE_TIME_FORMAT)
                {
                    try   {
                        date = gLongDateTimeFormat.parse(tempString);
                    } catch (ParseException ex)   {
                        date = null;
                    }
                    if (date == null)
                        sDateFormat = DBConstants.DATE_TIME_FORMAT;
                }
                if (sDateFormat == DBConstants.DATE_TIME_FORMAT)
                {
                    try   {
                        date = gDateTimeFormat.parse(tempString);
                    } catch (ParseException ex)   {
                        date = null;
                    }
                    if (date == null)
                    {
                        try   {
                            date = gDateShortTimeFormat.parse(tempString);
                        } catch (ParseException ex)   {
                            date = null;
                        }
                        if (date == null)
                            sDateFormat = DBConstants.DATE_ONLY_FORMAT;
                    }
                }
                if ((sDateFormat == DBConstants.DATE_ONLY_FORMAT) || (sDateFormat == DBConstants.DATE_FORMAT))
                {
                    try   {
                        date = gDateFormat.parse(tempString);
                    } catch (ParseException ex)   {
                        date = null;
                    }
                    if (date == null)
                    {
                        try   {
                            date = gDateShortFormat.parse(tempString);
                        } catch (ParseException ex)   {
                            date = null;
                        }
                    }
                }
                if (sDateFormat == DBConstants.HYBRID_TIME_ONLY_FORMAT)
                    sDateFormat = DBConstants.LONG_TIME_ONLY_FORMAT;
                if (sDateFormat == DBConstants.LONG_TIME_ONLY_FORMAT)
                {
                    try   {
                        date = gLongTimeFormat.parse(tempString);
                    } catch (ParseException ex)   {
                        date = null;
                    }
                    if (date == null)
                        sDateFormat = DBConstants.TIME_ONLY_FORMAT;
                }
                if ((sDateFormat == DBConstants.TIME_ONLY_FORMAT) || (sDateFormat == DBConstants.TIME_FORMAT))
                {
                    try   {
                            date = gTimeFormat.parse(tempString);
                        } catch (ParseException ex)   {
                            date = null;
                        }
                }
                if (date == null) 
                    if ((sDateFormat != DBConstants.DATE_ONLY_FORMAT) && (sDateFormat != DBConstants.DATE_FORMAT))
                {   // Problem with time parsing - maybe am/pm was not there!
                    if (tempString.charAt(tempString.length() - 1) != 'M')
                        return DateConverter.stringToBinary(tempString + " AM", dateOld, spDateFormat);
                }
                if (date == null)
                    date = FieldInfo.stringToDate(tempString, sDateFormat); // Throw everything else I have at it.
            }
            // The following lines fix the year 2000 problem (ie., 2/15/02 -> 2/15/2002)
            if (date != null)
                if ((sDateFormat != DBConstants.TIME_ONLY_FORMAT) && (sDateFormat != DBConstants.TIME_FORMAT))
            {
                gCalendar.setTime(date);
                int iYear = gCalendar.get(Calendar.YEAR);
                if (iYear < 1950)
                {
                    gCalendar.add(Calendar.YEAR, 100);
                    date = gCalendar.getTime();
                }
            }
            // End of year 2000 Fix
            if (date == null)
                return null;
            if (dateOld == null)
                return date;    // Null, don't change
            if (date.getTime() != 0) if (dateOld.equals(date))
                return date;
    
            if (dateOld.getTime() == 0)
            {
                gCalendar.set(DBConstants.FIRST_YEAR, Calendar.JANUARY, 1, 0, 0, 0);
                gCalendar.set(Calendar.MILLISECOND, 0);
                dateOld = gCalendar.getTime();
            }
            if ((sDateFormat == DBConstants.DATE_ONLY_FORMAT) || (sDateFormat == DBConstants.TIME_ONLY_FORMAT))
            {
                gCalendar.set(DBConstants.FIRST_YEAR, Calendar.JANUARY, 1, 0, 0, 0);
                gCalendar.set(Calendar.MILLISECOND, 0);
                dateOld = gCalendar.getTime();
            }
            if ((sDateFormat == DBConstants.DATE_FORMAT) || (sDateFormat == DBConstants.DATE_ONLY_FORMAT))
            {
                gCalendar.setTime(dateOld);
                int iHour = gCalendar.get(Calendar.HOUR_OF_DAY);
                int iMinute = gCalendar.get(Calendar.MINUTE);
                int iSecond = gCalendar.get(Calendar.SECOND);
                int iMilliSecond = gCalendar.get(Calendar.MILLISECOND);
                if (sDateFormat == DBConstants.DATE_ONLY_FORMAT)
                {
                    iHour = DBConstants.HOUR_DATE_ONLY;
                    iMinute = 0;
                    iSecond = 0;
                    iMilliSecond = 0;
                }
                gCalendar.setTime(date);
                int iYear =  gCalendar.get(Calendar.YEAR);
                int iMonth =  gCalendar.get(Calendar.MONTH);
                int iDate =  gCalendar.get(Calendar.DATE);
                gCalendar.set(iYear, iMonth, iDate, iHour, iMinute, iSecond);
                gCalendar.set(Calendar.MILLISECOND, iMilliSecond);
                date = gCalendar.getTime();
            }
            if ((sDateFormat == DBConstants.TIME_FORMAT) || (sDateFormat == DBConstants.TIME_ONLY_FORMAT))
            {
                gCalendar.setTime(date);
                int iHour = gCalendar.get(Calendar.HOUR_OF_DAY);
                int iMinute = gCalendar.get(Calendar.MINUTE);
                int iSecond = gCalendar.get(Calendar.SECOND);
                int iMilliSecond = gCalendar.get(Calendar.MILLISECOND);
                gCalendar.setTime(dateOld);
                int iYear =  gCalendar.get(Calendar.YEAR);
                int iMonth =  gCalendar.get(Calendar.MONTH);
                int iDate =  gCalendar.get(Calendar.DATE);
                if (sDateFormat == DBConstants.TIME_ONLY_FORMAT)
                {
                    iYear =  DBConstants.FIRST_YEAR;
                    iMonth =  Calendar.JANUARY;
                    iDate =  1;
                }
                int iOldHour = gCalendar.get(Calendar.HOUR_OF_DAY);
                int iOldMinute = gCalendar.get(Calendar.MINUTE);
                if ((iHour == iOldHour) && (iMinute == iOldMinute)
                    && (iSecond == 0) && (iMilliSecond == 0))
                {   // If nothing has changed but the seconds and milliseconds, leave them the same (probably parsing a entered field)
                    iSecond = gCalendar.get(Calendar.SECOND);
                    iMilliSecond = gCalendar.get(Calendar.MILLISECOND);
                }
                gCalendar.set(iYear, iMonth, iDate, iHour, iMinute, iSecond);
                gCalendar.set(Calendar.MILLISECOND, iMilliSecond);
                date = gCalendar.getTime();
            }
        }
        return date;
    }
}
