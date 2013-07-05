/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.model.util;

/**
 * @(#)Converter.java 0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.jbundle.model.util.Constant;


/**
 * Converter converts the screen data representation to the field's binary representation and back.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public abstract class DataConverters extends Object
{
    public static DateFormat gDateFormat = null;
    public static DateFormat gTimeFormat = null;
    public static DateFormat gLongTimeFormat = null;
    public static DateFormat gDateTimeFormat = null;
    public static DateFormat gLongDateTimeFormat = null;
    public static DateFormat gDateShortFormat = null;
    public static DateFormat gDateShortTimeFormat = null;
    public static DateFormat gDateSqlFormat = null;
    public static DateFormat gTimeSqlFormat = null;
    public static DateFormat gDateTimeSqlFormat = null;
    public static DateFormat gGMTDateTimeFormat = null; // Use this format to pass dates as strings.
    public static Calendar gCalendar = null;
    public static NumberFormat gIntegerFormat = null;
    public static NumberFormat gCurrencyFormat = null;
    public static NumberFormat gNumberFormat = null;
    public static NumberFormat gPercentFormat = null;
    public static char gchDot = '.';        // Decimal separator
    public static char gchMinus = '-';      // Minus sign
    /**
     * Constructor.
     */
    public DataConverters()
    {
        super();
    }
    /**
     * Initialize the global text format classes.
     */
    public static void initGlobals()
    {
        if (gDateFormat == null)
        {   // NOTE: You MUST synchronize before using any of these
            // For dates and times, synchronize on the gCalendar
            gCalendar = Calendar.getInstance();

            gDateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
            gTimeFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
            gLongTimeFormat = DateFormat.getTimeInstance(DateFormat.MEDIUM);
            gDateTimeFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
            gLongDateTimeFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
            gDateShortFormat = DateFormat.getDateInstance(DateFormat.SHORT);
            gDateShortTimeFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
            gGMTDateTimeFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);

            gDateSqlFormat = gDateShortFormat;  // By default use these formats - Change if different
            gTimeSqlFormat = gTimeFormat;
            gDateTimeSqlFormat = gDateShortTimeFormat;
            // For all of these, synchronize on the object you are using
            gIntegerFormat = NumberFormat.getInstance();
            gIntegerFormat.setParseIntegerOnly(true);

            gCurrencyFormat = NumberFormat.getCurrencyInstance();
            
            gNumberFormat = NumberFormat.getInstance();
            gNumberFormat.setParseIntegerOnly(false);
            gNumberFormat.setMinimumFractionDigits(2);
            gNumberFormat.setMaximumFractionDigits(2);
            if (gNumberFormat instanceof DecimalFormat)
            {
                gchDot = ((DecimalFormat)gNumberFormat).getDecimalFormatSymbols().getDecimalSeparator();
                gchMinus = ((DecimalFormat)gNumberFormat).getDecimalFormatSymbols().getMinusSign();
            }

            gPercentFormat = NumberFormat.getPercentInstance();
        }
    }
    /**
     * Utility to strip all the non-numeric characters from this string.
     * @param string input string.
     * @return The result string.
     */
    public static String stripNonNumber(String string)
    {
    	if (string == null)
    		return null;
        for (int i = 0; i < string.length(); i++)
        {
            char ch = string.charAt(i);
            if (!(Character.isDigit(ch)))
                if (ch != gchDot)
                if (ch != gchMinus)
                {
                    string = string.substring(0, i) + string.substring(i + 1);
                    i--;
                }
        }
        return string;
    }
    /**
     * Convert this time to a date (ie., Hour, min, sec = 0).
     * @param time
     * @return
     */
    public static Date convertTimeToDate(Date time)
    {
        if (time != null)
        {
            DataConverters.initGlobals();
            Calendar calendar = DataConverters.gCalendar;
            calendar.setTime(time);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            time = calendar.getTime();
        }       
        return time;
    }
    /**
     * Get this property from the map and convert it to the target class.
     * @param properties The map object to get the property from.
     * @param strKey The key of the property.
     * @param classData The target class to convert the property to.
     * @param objDefault The default value.
     * @return The data in the correct class.
     */
    public static Object convertObjectToDatatype(Object objData, Class<?> classData, Object objDefault)
        throws Exception
    {
        return DataConverters.convertObjectToDatatype(objData, classData, objDefault, -1);
    }   
    /**
     * Get this property from the map and convert it to the target class.
     * @param properties The map object to get the property from.
     * @param strKey The key of the property.
     * @param classData The target class to convert the property to.
     * @param objDefault The default value.
     * @return The data in the correct class.
     */
    public static Object convertObjectToDatatype(Object objData, Class<?> classData, Object objDefault, int iScale)
        throws Exception
    {
        if (objData == null)
            return objDefault;
        if (objData.getClass() == classData)
            return objData;
        objData = objData.toString();   // Make sure it can be converted.
        DataConverters.initGlobals();
        if (objData instanceof String)
        {   // Convert to local format
            try {
                if (classData == Short.class)
                    objData = DataConverters.stringToShort((String)objData);
                else if (classData == Integer.class)
                    objData = DataConverters.stringToInteger((String)objData);
                else if (classData == Float.class)
                    objData = DataConverters.stringToFloat((String)objData, iScale);
                else if (classData == Double.class)
                    objData = DataConverters.stringToDouble((String)objData, iScale);
                else if (classData == Long.class)
                {
                    objData = DataConverters.stringToDouble((String)objData, iScale);
                    objData = new Long(((Double)objData).longValue());
                }
                else if (classData == Date.class)
                {
                    Date objDate = null;
                    try {
                        objDate = DataConverters.stringToDate((String)objData, iScale); // Try them all
                    } catch (ParseException ex)   {
                        objDate = null;
                    }
                    if (objDate == null)
                    {
                        try {
                            synchronized (DataConverters.gCalendar)
                            {
                                objDate = DataConverters.gGMTDateTimeFormat.parse((String)objData);
                            }
                        } catch (ParseException ex)   {
                            objDate = null;
                        }
                    }
                    objData = objDate;
                }
                else if (classData == Boolean.class)
                    objData = new Boolean((String)objData);
                else
                    objData = (String)objData;
            } catch (Exception ex)  {
                throw ex;
            }
        }
        if (objData == null)
            if (objDefault != null)
                return DataConverters.convertObjectToDatatype(objDefault, classData, null, iScale);    // No chance of infinite recursion
        return objData;
    }
    /**
     * Convert this object to an formatted string.
     * @param properties The map object to get the property from.
     * @param strKey The key of the property.
     * @param classData The target class to convert the property to.
     * @param objDefault The default value.
     * @return The data formatted as a string (note - this will never be null - since it is a string).
     */
    public static String formatObjectToString(Object objData, Class<?> classData, Object objDefault)
        throws Exception
    {
        objData = DataConverters.convertObjectToDatatype(objData, classData, objDefault);
        if (objData == null)
            return Constant.BLANK;
        if (objData.getClass() != classData)
            return objData.toString();
        DataConverters.initGlobals();
        if ((classData != Object.class) && (classData != String.class)
            && (classData == objData.getClass()))
        {
            try {
                if (classData == Short.class)
                    synchronized (DataConverters.gIntegerFormat)
                    {
                        return DataConverters.gIntegerFormat.format(((Short)objData).shortValue());
                    }
                else if (classData == Integer.class)
                    synchronized (DataConverters.gIntegerFormat)
                    {
                        return DataConverters.gIntegerFormat.format(((Integer)objData).intValue());
                    }
                else if (classData == Float.class)
                    synchronized (DataConverters.gNumberFormat)
                    {
                        return DataConverters.gNumberFormat.format(((Float)objData).floatValue());
                    }
                else if (classData == Double.class)
                    synchronized (DataConverters.gNumberFormat)
                    {
                        return DataConverters.gNumberFormat.format(((Double)objData).doubleValue());
                    }
                else if (classData == java.util.Date.class)
                {
                    synchronized (DataConverters.gCalendar)
                    {
                        DataConverters.gCalendar.setTime((Date)objData);
                        boolean bTime = true;
                        boolean bDate = true;
                        if ((DataConverters.gCalendar.get(Calendar.HOUR_OF_DAY) == 0)
                            && (DataConverters.gCalendar.get(Calendar.MINUTE) == 0)
                            && (DataConverters.gCalendar.get(Calendar.SECOND) == 0)
                            && (DataConverters.gCalendar.get(Calendar.MILLISECOND) == 0))
                                bTime = false;
                        if ((DataConverters.gCalendar.get(Calendar.MONTH) == Calendar.JANUARY)
                            && (DataConverters.gCalendar.get(Calendar.DATE) == 1)
                            && (DataConverters.gCalendar.get(Calendar.YEAR) == Constant.FIRST_YEAR))
                                bDate = false;
                        if (((Date)objData).getTime() == 0)
                            {bDate = bTime = false;}
                        if (bTime && bDate)
                            return DataConverters.gDateTimeFormat.format((Date)objData);
                        else if (bDate)
                            return DataConverters.gDateFormat.format((Date)objData);
                        else if (bTime)
                            return DataConverters.gTimeFormat.format((Date)objData);
                        else
                            return Constant.BLANK;
                    }
                }
                else if (classData == Boolean.class)
                {
                    if (((Boolean)objData).booleanValue())
                        return Constant.TRUE;
                    else
                        return Constant.FALSE;
                }
                else
                    objData = objData.toString();
            } catch (Exception ex)  {
                ex.printStackTrace();
                objData = null;
            }
        }
        else
            objData = objData.toString();
        if (objData == null)
            if (objDefault != null)
                objData = objDefault.toString();
        if (objData == null)
            objData = Constant.BLANK;
        return (String)objData;
    }
    /**
     * Convert this string to a Short.
     * @param strString string to convert.
     * @return Short value.
     * @throws Exception NumberFormatException.
     */
    public static Short stringToShort(String strString) throws Exception
    {
        Number objData;
        initGlobals();      // Make sure you have the utilities
        if ((strString == null) || (strString.equals(Constant.BLANK)))
            return null;
        strString = DataConverters.stripNonNumber(strString);
        try   {
            synchronized (gIntegerFormat)
            {
                objData = gIntegerFormat.parse(strString);
            }
            if (!(objData instanceof Short))
            {
                if (objData instanceof Number)
                    objData = new Short(objData.shortValue());
                else
                    objData = null;
            }
        } catch (ParseException ex)   {
            objData = null;
        }
        if (objData == null)
        {
            try   {
                objData = new Short(strString);
            } catch (NumberFormatException ex)  {
                throw ex;
            }
        }
        return (Short)objData;
    }
    /**
     * Convert this string to a Integer.
     * @param strString string to convert.
     * @return Integer value.
     * @throws Exception NumberFormatException.
     */
    public static Integer stringToInteger(String strString) throws Exception
    {
        Number objData;
        initGlobals();      // Make sure you have the utilities
        if ((strString == null) || (strString.equals(Constant.BLANK)))
            return null;
        strString = DataConverters.stripNonNumber(strString);
        try   {
            synchronized (gIntegerFormat)
            {
                objData = gIntegerFormat.parse(strString);
            }
            if (!(objData instanceof Integer))
            {
                if (objData instanceof Number)
                    objData = new Integer(objData.intValue());
                else
                    objData = null;
            }
        } catch (ParseException ex)   {
            objData = null;
        }
        if (objData == null)
            if (strString != null)
            if (strString.length() > 0)
        {
            try   {
                objData = new Integer(strString);
            } catch (NumberFormatException ex)  {
                throw ex;
            }
        }
        return (Integer)objData;
    }
    /**
     * Convert this string to a Float.
     * @param strString string to convert.
     * @return Float value.
     * @throws Exception NumberFormatException.
     */
    public static Float stringToFloat(String strString, int ibScale) throws Exception
    {
        Number objData;
        initGlobals();      // Make sure you have the utilities
        if ((strString == null) || (strString.equals(Constant.BLANK)))
            return null;
        strString = DataConverters.stripNonNumber(strString);
        try   {
            synchronized (gNumberFormat)
            {
                objData = gNumberFormat.parse(strString);
            }
            if (!(objData instanceof Float))
            {
                if (objData instanceof Number)
                    objData = new Float(objData.floatValue());
                else
                    objData = null;
            }
        } catch (ParseException ex)   {
            objData = null;
        }
        if (objData == null)
        {
            try   {
                objData = new Float(strString);
            } catch (NumberFormatException ex)  {
                throw ex;
            }
        }
        if (ibScale != -1)
            objData = new Float(Math.floor(((Float)objData).floatValue() * Math.pow(10, ibScale) + 0.5) / Math.pow(10, ibScale));
        return (Float)objData;
    }
    /**
     * Convert this string to a Double.
     * @param strString string to convert.
     * @return Double value.
     * @throws Exception NumberFormatException.
     */
    public static Double stringToDouble(String strString, int ibScale) throws Exception
    {
        Number objData;
        initGlobals();      // Make sure you have the utilities
        if ((strString == null) || (strString.equals(Constant.BLANK)))
            return null;
        strString = DataConverters.stripNonNumber(strString);
        try   {
            synchronized (gNumberFormat)
            {
                objData = gNumberFormat.parse(strString);
            }
            if (!(objData instanceof Double))
            {
                if (objData instanceof Number)
                    objData = new Double(objData.doubleValue());
                else
                    objData = null;
            }
        } catch (ParseException ex)   {
            objData = null;
        }
        if (objData == null)
        {
            try   {
                objData = new Double(strString);
            } catch (NumberFormatException ex)  {
                throw ex;
            }
        }
        if (ibScale != -1)
            objData = new Double(Math.floor(((Double)objData).doubleValue() * Math.pow(10, ibScale) + 0.5) / Math.pow(10, ibScale));
        return (Double)objData;
    }
    /**
     * Convert this string to a Date.
     * Runs sequentually through the following formats: DateTime, DateShortTime,
     * Date, DateShort, Time.
     * @param strString string to convert.
     * @return Date value.
     * @throws Exception NumberFormatException.
     */
    public static Date stringToDate(String strString, int ibScale) throws Exception
    {
        Date objData;
        Exception except = null;
        initGlobals();      // Make sure you have the utilities
        if ((strString == null) || (strString.equals(Constant.BLANK)))
            return null;
        for (int i = 1; i <= 6; i++)
        {
            DateFormat df = null;
            switch (i)
            {
            case 1:
                df = gDateTimeFormat;break;
            case 2:
                df = gDateShortTimeFormat;break;
            case 3:
                df = gDateFormat;break;
            case 4:
                df = gDateShortFormat;break;
            case 5:
            default:
                df = gTimeFormat;break;
            case 6:
                df= gGMTDateTimeFormat;break;
            }
            try   {
                synchronized (gCalendar)
                {
                    objData = df.parse(strString);
                    if (ibScale != -1)
                    {
                        DataConverters.gCalendar.setTime(objData);
                        if (ibScale == Constant.DATE_ONLY)
                        {
                            DataConverters.gCalendar.set(Calendar.HOUR_OF_DAY, 0);
                            DataConverters.gCalendar.set(Calendar.MINUTE, 0);
                            DataConverters.gCalendar.set(Calendar.SECOND, 0);
                            DataConverters.gCalendar.set(Calendar.MILLISECOND, 0);
                        }
                        if (ibScale == Constant.TIME_ONLY)
                        {
                            DataConverters.gCalendar.set(Calendar.YEAR, Constant.FIRST_YEAR);
                            DataConverters.gCalendar.set(Calendar.MONTH, Calendar.JANUARY);
                            DataConverters.gCalendar.set(Calendar.DATE, 1);
                        }
                        DataConverters.gCalendar.set(Calendar.MILLISECOND, 0);
                        objData = DataConverters.gCalendar.getTime();
                    }
                }
                return objData;
            } catch (ParseException ex)   {
                except = ex;
                // continue with the next parse
            }
        }
        if (except != null)
            throw except;
        return null;
    }
}
