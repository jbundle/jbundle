/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.db;

/**
 * @(#)Converter.java 0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
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

import org.jbundle.model.db.Convert;
import org.jbundle.model.db.ScreenComponent;



/**
 * Converter converts the screen data representation to the field's binary representation and back.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public abstract class Converter extends Object
    implements Convert
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
    public Converter()
    {
        super();
    }
    /**
     * Constructor.
     * @param obj Not used in converter.
     */
    public Converter(Object obj)
    {
        this();
        this.init(obj);
    }
    /**
     * Initialize this converter.
     * @param obj Not used in converter.
     */
    public void init(Object obj)
    {
    }
    /**
     * Free this converter.
     */
    public void free()
    {
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
     * This screen component is displaying this field.
     * @param Object sField The screen component.. either a awt.Component or a ScreenField
     */
    public void addComponent(Object sField)
    {
    }
    /**
     * Remove this control from this field's control list.
     * @param Object sField The screen component.. either a awt.Component or a ScreenField
     */
    public void removeComponent(Object screenField)
    {
        this.free();    // If the screen field is gone, I have no more use, since I am a converter and I have no reference to other objects.
    }
    /**
     * Get the field on the end of this converter chain.
     * @return FieldInfo The field.
     */
    public FieldInfo getField() 
    {
        return null;    // A converter is not a field!
    }
    /**
     * Get the first converter for this field (must be linked to at least one ScreenComponent for this to work).
     * @return The converter object (or this if there is none).
     */
    public Converter getFieldConverter()
    {
        for (int i = 0; ; i++)
        {
            Object ojbComponent = this.getField().getComponent(i);
            if (ojbComponent == null)
                break;
            if (ojbComponent instanceof ScreenComponent)
            {
                Converter conv = (Converter)((ScreenComponent)ojbComponent).getConverter();
                if ((conv != null) && (conv.getField() == this.getField()))
                    return conv;   // Since there is a converter in front of this field, return the converter
            }
        }
        return this;
    }
    /**
     * Get this field's name.
     * @return The name of the field on the end of the converter chain.
     */
    public String getFieldName()
    {
        if (this.getField() == null)
            return null;
        else
            return this.getField().getFieldName(false, false);
    }
    /**
     * Get the field description.
     * Usually overidden.
     * @return The description of the field on the end of the converter chain.
     */
    public String getFieldDesc() 
    {
        return Constants.BLANK;     // By default, there is no temp string
    }
    /**
     * Get current length of this field converted to a string.
     * (For StringField, returns fCurrent, for OtherFields returns fMax.
     * ... this routine is used to set up a buffer to receive the field, w/o wasting space!)
     * Usually overidden.
     * @return The current length (0 if null data, -1 if unknown).
     */
    public int getLength() 
    {
        return 0;
    }
    /**
     * Get the maximum length of this field.
     * Usually overidden.
     * @return The maximum length of this field (0 if unknown).
     */
    public int getMaxLength() 
    {
        return 0;
    }
    /**
     * Get the binary image of this field's data.
     * @return The raw data.
     */
    public Object getData() 
    { // Move raw data from this field
        return null;
    }
    /**
     * Move data to this field (don't override this).
     * This calls setData with DISPLAY and SCREEN_MOVE.
     * @param object The data to set the field to.
     * @return The error code.
     */
    public final int setData(Object object)
    {
        return this.setData(object, Constants.DISPLAY, Constants.SCREEN_MOVE);
    }
    /**
     * Move the physical binary data to this field.
     * @param vpData The data to set the field to.
     * @param bDisplayOption Display the data on the screen if true.
     * @param iMoveMode INIT, SCREEN, or READ move mode.
     * @return The error code.
     */
    public int setData(Object vpData, boolean bDisplayOption, int iMoveMode)
    {       // Must be overidden
        return Constants.NORMAL_RETURN;
    }
    /**
     * Retrieve (in string format) from this field.
     * Override this method to convert the actual Physical Data Type to a String.
     * The default implementation (here) assumes the physical data is in string format.
     * Same as toString().
     * @return The data in string format.
     * @see toString().
     */
    public String getString()
    {
        return Constants.BLANK;
    }
    /**
     * Override Object.toString to convert this field to a String.
     * @return The data in string format.
     * @see getString().
     */
    public String toString ()
    {
        return this.getString();
    }
    /**
     * Convert this string and set the data to this field (don't override this).
     * This calls setString with DISPLAY and SCREEN_MOVE.
     * @param string the string to set the data to.
     * @return The error code.
     */
    public final int setString(String string)
    {
        return this.setString(string, Constants.DISPLAY, Constants.SCREEN_MOVE);
    }
    /**
     * Convert and move string to this field.
     * Override this method to convert the String to the actual Physical Data Type.
     * @param strString the string to set the data to.
     * @param bDisplayOption Display the data on the screen if true.
     * @param iMoveMode INIT, SCREEN, or READ move mode.
     * @return The error code.
     */
    public int setString(String strString, boolean bDisplayOption, int moveMode)    // init this field override for other value
    {
        return Constants.NORMAL_RETURN;
    }
    /**
     * Get the state of this boolean field.
     * Usually overidden.
     * @return True if data is true.
     */
    public boolean getState()               // init this field override for other value
    {
        return true;
    }
    /**
     * Set the state of this field for binary fields (don't override this).
     * This calls setState with DISPLAY and SCREEN_MOVE.
     * @param bState the state to set the data to.
     * @return The error code.
     */
    public final int setState(boolean bState)
    {
        return this.setState(bState, Constants.DISPLAY, Constants.SCREEN_MOVE);
    }
    /**
     * Set the state of this field for binary fields (don't override this).
     * Usually overidden.
     * @param bState the state to set the data to.
     * @param bDisplayOption Display the data on the screen if true.
     * @param iMoveMode INIT, SCREEN, or READ move mode.
     * @return The error code.
     */
    public int setState(boolean state, boolean bDisplayOption, int iMoveMode)
    {
        return Constants.NORMAL_RETURN;
    }
    /**
     * For numeric fields, get the current value for numeric fields.
     * Usually overidden.
     * @return The value of this field's data.
     */
    public double getValue()
    {
        return 0;
    }
    /**
     * For numeric fields, set the current value.
     * This calls setValue with DISPLAY and SCREEN_MOVE.
     * @param value the value to set the data to.
     * @return The error code.
     */
    public int setValue(double value)
    {
        return this.setValue(value, Constants.DISPLAY, Constants.SCREEN_MOVE);
    }
    /**
     * For numeric fields, set the current value.
     * Override this method to convert the value to the actual Physical Data Type.
     * @param bState the state to set the data to.
     * @param bDisplayOption Display the data on the screen if true.
     * @param iMoveMode INIT, SCREEN, or READ move mode.
     * @return The error code.
     */
    public int setValue(double value, boolean bDisplayOption, int moveMode)
    {
        return Constants.NORMAL_RETURN;
    }
    /**
     * Convert the current field value to an index 0 - x.
     * Ususally used for fields the are displayed as popups.
     * Must be overidden.
     * @return The index (-1 for unknown).
     */
    public int convertFieldToIndex()                    // init this field override for other value
    {
        return -1;
    }
    /**
     * Convert this index value to a display string.
     * Ususally used for fields that are displayed as popups.
     * Override if you want to display the string.
     * @return The string for this index.
     */
    public String convertIndexToDisStr(int index)
    {
        return Constants.BLANK;
    }
    /**
     * Set this field to the value to represent this index.
     * Must be overidden.
     * Ususally used for fields that are displayed as popups.
     * @param index the index to set the data to.
     * @param bDisplayOption Display the data on the screen if true.
     * @param iMoveMode INIT, SCREEN, or READ move mode.
     * @return The error code.
     */
    public int convertIndexToField(int index, boolean bDisplayOption, int iMoveMode)
    {
        return Constants.NORMAL_RETURN;
    }
    /**
     * equals - Override the "equals" Object method to compare two fields' raw data.
     * @param field The converter to compare this converter to.
     * @return true if equal.
     */
    public boolean equals(Convert field) 
    { // Override this
        Object objThis = this.getData();
        Object objField = field.getData();
        if ((objThis == null) || (objField == null))
        {
            if ((objThis == null) && (objField == null))
                return true;
            return false;
        }
        return objThis.equals(objField);
    }
    /**
     * Get the image for Buttons and Bitmaps and drag cursors.
     * Also used for image buttons that change bitmaps with their state.
     * @return The name of the bitmap.
     * @see org.jbundle.base.screen.view.swing.VButtonBox
     */
    public String getBitmap()
    {
        return null;
    }
    /**
     * Get the HTML Hyperlink for this field (override).
     * @return The full html hyperlink (ie., mailto:don@don.com).
     */
    public String getHyperlink() 
    {
        return null;
    }
    /**
     * Set up the default control for this field.
     * Calls setupDefaultView with this as the converter.
     *  @param  itsLocation     Location of this component on screen (ie., GridBagConstraint).
     *  @param  targetScreen    Where to place this component (ie., Parent screen or GridBagLayout).
     *  @param  iDisplayFieldDesc Display the label? (optional).
     *  @return   Return the component or ScreenField that is created for this field.
     */
    public Object setupDefaultView(Object itsLocation, Object targetScreen, int iDisplayFieldDesc)  // Add this view to the list
    {
        return this.setupDefaultView(itsLocation, targetScreen, this, iDisplayFieldDesc);
    }
    /**
     * Set up the default control for this field.
     *  @param  itsLocation     Location of this component on screen (ie., GridBagConstraint).
     *  @param  targetScreen    Where to place this component (ie., Parent screen or GridBagLayout).
     *  @param  iDisplayFieldDesc Display the label? (optional).
     *  @return   Return the component or ScreenField that is created for this field.
     */
    public Object setupDefaultView(Object itsLocation, Object targetScreen, Convert converter, int iDisplayFieldDesc)   // Add this view to the list
    {
        return null;
    }
    /**
     * String all the non-numeric characters from this string.
     * @param string input string.
     * @return The result string.
     */
    public String stripNonNumeric(String string)
    {
        return Converter.stripNonNumber(string);
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
            Converter.initGlobals();
            Calendar calendar = Converter.gCalendar;
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
        return Converter.convertObjectToDatatype(objData, classData, objDefault, -1);
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
        Converter.initGlobals();
        if (objData instanceof String)
        {   // Convert to local format
            try {
                if (classData == Short.class)
                    objData = FieldInfo.stringToShort((String)objData);
                else if (classData == Integer.class)
                    objData = FieldInfo.stringToInteger((String)objData);
                else if (classData == Float.class)
                    objData = FieldInfo.stringToFloat((String)objData, iScale);
                else if (classData == Double.class)
                    objData = FieldInfo.stringToDouble((String)objData, iScale);
                else if (classData == Long.class)
                {
                    objData = FieldInfo.stringToDouble((String)objData, iScale);
                    objData = new Long(((Double)objData).longValue());
                }
                else if (classData == Date.class)
                {
                    Date objDate = null;
                    try {
                        objDate = FieldInfo.stringToDate((String)objData, iScale); // Try them all
                    } catch (ParseException ex)   {
                        objDate = null;
                    }
                    if (objDate == null)
                    {
                        try {
                            synchronized (Converter.gCalendar)
                            {
                                objDate = Converter.gGMTDateTimeFormat.parse((String)objData);
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
                return Converter.convertObjectToDatatype(objDefault, classData, null, iScale);    // No chance of infinite recursion
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
        objData = Converter.convertObjectToDatatype(objData, classData, objDefault);
        if (objData == null)
            return Constants.BLANK;
        if (objData.getClass() != classData)
            return objData.toString();
        Converter.initGlobals();
        if ((classData != Object.class) && (classData != String.class)
            && (classData == objData.getClass()))
        {
            try {
                if (classData == Short.class)
                    synchronized (Converter.gIntegerFormat)
                    {
                        return Converter.gIntegerFormat.format(((Short)objData).shortValue());
                    }
                else if (classData == Integer.class)
                    synchronized (Converter.gIntegerFormat)
                    {
                        return Converter.gIntegerFormat.format(((Integer)objData).intValue());
                    }
                else if (classData == Float.class)
                    synchronized (Converter.gNumberFormat)
                    {
                        return Converter.gNumberFormat.format(((Float)objData).floatValue());
                    }
                else if (classData == Double.class)
                    synchronized (Converter.gNumberFormat)
                    {
                        return Converter.gNumberFormat.format(((Double)objData).doubleValue());
                    }
                else if (classData == java.util.Date.class)
                {
                    synchronized (Converter.gCalendar)
                    {
                        Converter.gCalendar.setTime((Date)objData);
                        boolean bTime = true;
                        boolean bDate = true;
                        if ((Converter.gCalendar.get(Calendar.HOUR_OF_DAY) == 0)
                            && (Converter.gCalendar.get(Calendar.MINUTE) == 0)
                            && (Converter.gCalendar.get(Calendar.SECOND) == 0)
                            && (Converter.gCalendar.get(Calendar.MILLISECOND) == 0))
                                bTime = false;
                        if ((Converter.gCalendar.get(Calendar.MONTH) == Calendar.JANUARY)
                            && (Converter.gCalendar.get(Calendar.DATE) == 1)
                            && (Converter.gCalendar.get(Calendar.YEAR) == Constants.FIRST_YEAR))
                                bDate = false;
                        if (((Date)objData).getTime() == 0)
                            {bDate = bTime = false;}
                        if (bTime && bDate)
                            return Converter.gDateTimeFormat.format((Date)objData);
                        else if (bDate)
                            return Converter.gDateFormat.format((Date)objData);
                        else if (bTime)
                            return Converter.gTimeFormat.format((Date)objData);
                        else
                            return Constants.BLANK;
                    }
                }
                else if (classData == Boolean.class)
                {
                    if (((Boolean)objData).booleanValue())
                        return Constants.TRUE;
                    else
                        return Constants.FALSE;
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
            objData = Constants.BLANK;
        return (String)objData;
    }
}
