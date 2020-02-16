/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field.convert;

/**
 * @(#)FirstMLastConverter.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import org.jbundle.base.db.Record;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.model.DBConstants;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.Converter;


/**
 * Display First/Middle/Last name together, but separate the
 * strings into different physical fields.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class FirstMLastConverter extends FieldConverter
{
    /**
     * Target record.
     */
    protected Record m_recThis = null;
    /**
     * Field sequence of the prefix field.
     */
    protected String m_iNamePrefix = null;
    /**
     * Field sequence of the First name field.
     */
    protected String m_iNameFirst = null;
    /**
     * Field sequence of the middle name field.
     */
    protected String m_iNameMiddle = null;
    /**
     * Field sequence of the sur name field.
     */
    protected String m_iNameSur = null;
    /**
     * Field sequence of the suffix field.
     */
    protected String m_iNameSuffix = null;
    /**
     * Field sequence of the title field.
     */
    protected String m_iNameTitle = null;

    /**
     * Constructor.
     */
    public FirstMLastConverter()
    {
        super();
    }
    /**
     * Constructor.
     * @param thisFile Target record.
     * @param iNamePrefix Field sequence of the prefix field.
     * @param iNameFirst Field sequence of the First name field.
     * @param iNameMiddle Field sequence of the middle name field.
     * @param iNameSur Field sequence of the suffix field.
     */
    public FirstMLastConverter(Record thisFile, String iNamePrefix, String iNameFirst, String iNameMiddle, String iNameSur)
    {
        this();
        this.init(null, thisFile, iNamePrefix, iNameFirst, iNameMiddle, iNameSur, null, null);
    }
    /**
     * Constructor.
     * @param thisFile Target record.
     * @param iNamePrefix Field sequence of the prefix field.
     * @param iNameFirst Field sequence of the First name field.
     * @param iNameMiddle Field sequence of the middle name field.
     * @param iNameSur Field sequence of the sur name field.
     * @param iNameSuffix Field sequence of the suffix field.
     * @param iNameTitle Field sequence of the title field.
     */
    public FirstMLastConverter(Record thisFile, String iNamePrefix, String iNameFirst, String iNameMiddle, String iNameSur, String iNameSuffix, String iNameTitle)
    {
        this();
        this.init(null, thisFile, iNamePrefix, iNameFirst, iNameMiddle, iNameSur, iNameSuffix, iNameTitle);
    }
    /**
     * Constructor.
     * @param convFullName Target record.
     * @param iNamePrefix Field sequence of the prefix field.
     * @param iNameFirst Field sequence of the First name field.
     * @param iNameMiddle Field sequence of the middle name field.
     * @param iNameSur Field sequence of the sur name field.
     * @param iNameSuffix Field sequence of the suffix field.
     * @param iNameTitle Field sequence of the title field.
     */
    public FirstMLastConverter(Converter convFullName, String iNamePrefix, String iNameFirst, String iNameMiddle, String iNameSur, String iNameSuffix, String iNameTitle)
    {
        this();
        this.init(convFullName, ((BaseField)convFullName.getField()).getRecord(), iNamePrefix, iNameFirst, iNameMiddle, iNameSur, iNameSuffix, iNameTitle);
    }
    /**
     * Initialize this object.
     * @param thisFile Target record.
     * @param iNamePrefix Field sequence of the prefix field.
     * @param iNameFirst Field sequence of the First name field.
     * @param iNameMiddle Field sequence of the middle name field.
     * @param iNameSur Field sequence of the sur name field.
     * @param iNameSuffix Field sequence of the suffix field.
     * @param iNameTitle Field sequence of the title field.
     */
    public void init(Converter converter, Record thisFile, String iNamePrefix, String iNameFirst, String iNameMiddle, String iNameSur, String iNameSuffix, String iNameTitle)
    {
        super.init(converter);
        m_recThis = thisFile;
        m_iNamePrefix = iNamePrefix;
        m_iNameFirst = iNameFirst;
        m_iNameMiddle = iNameMiddle;
        m_iNameSur = iNameSur;
        m_iNameSuffix = iNameSuffix;
        m_iNameTitle = iNameTitle;

        //if (m_iNameFirst == -1)
        //    m_iNameFirst = m_iNamePrefix + 1;
        //if (m_iNameSur == -1)
        //    m_iNameSur = m_iNameMiddle + 1;
        //if (m_iNameMiddle == -1)
        //    if (m_iNameFirst + 1 != m_iNameSur)
        // = m_iNameFirst + 1;
    }
    /**
     * Free this converter.
     */
    public void free()
    {
        super.free();
        m_recThis = null;
    }
    /**
     * Add this component to the components displaying this field.
     * @param screenField sField The screen component.. either a awt.Component or a ScreenField.
     */
    public void addComponent(Object screenField)
    { // Set up the dependencies

        if (m_iNamePrefix != null)
            m_recThis.getField(m_iNamePrefix).addComponent(screenField);
        m_recThis.getField(m_iNameFirst).addComponent(screenField);
        if (m_iNameMiddle != null)
            m_recThis.getField(m_iNameMiddle).addComponent(screenField);
        m_recThis.getField(m_iNameSur).addComponent(screenField);
        if (m_iNameSuffix != null)
            m_recThis.getField(m_iNameSuffix).addComponent(screenField);
        if (m_iNameTitle != null)
            m_recThis.getField(m_iNameTitle).addComponent(screenField);

        super.addComponent(screenField);
    }
    /**
     * Remove this control from this field's control list.
     * @param screenField sField The screen component.. either a awt.Component or a ScreenField.
     */
    public void removeComponent(Object screenField)
    { // Set up the dependencies

        if (m_iNamePrefix != null)
            m_recThis.getField(m_iNamePrefix).removeComponent(screenField);
        m_recThis.getField(m_iNameFirst).removeComponent(screenField);
        if (m_iNameMiddle != null)
            m_recThis.getField(m_iNameMiddle).removeComponent(screenField);
        m_recThis.getField(m_iNameSur).removeComponent(screenField);
        if (m_iNameSuffix != null)
            m_recThis.getField(m_iNameSuffix).removeComponent(screenField);
        if (m_iNameTitle != null)
            m_recThis.getField(m_iNameTitle).removeComponent(screenField);

        super.removeComponent(screenField);
    }
    /**
     * Get the field description.
     * @return "Name".
     */
    public String getFieldDesc() 
    {
        return "Name";
    }
    /**
     * Get the maximum field length.
     * Maximum field length is 30.
     * @return The maximum field length.
     */
    public int getMaxLength() 
    {
        return 30;      // 30 characters?
    }
    /**
     * Retrieve (in string format) from this field.
     * @return Concatinate the fields and return the result.
     */
    public String getString() 
    {
        String strFinal = FirstMLastConverter.partsToName(m_recThis, m_iNamePrefix, m_iNameFirst, m_iNameMiddle, m_iNameSur, m_iNameSuffix, m_iNameTitle);
        int maxLength = this.getMaxLength();
        if (strFinal.length() > maxLength)
            strFinal = strFinal.substring(0, maxLength);    // Truncate to max length
        return strFinal;
    }
    /**
     * Convert this complete name to the component parts.
     * @param record Target record.
     * @param iNamePrefix Field sequence of the prefix field.
     * @param iNameFirst Field sequence of the First name field.
     * @param iNameMiddle Field sequence of the middle name field.
     * @param iNameSur Field sequence of the sur name field.
     * @param iNameSuffix Field sequence of the suffix field.
     * @param iNameTitle Field sequence of the title field.
     * @return Concatinate the fields and return the result.
     */
    public static String partsToName(Record record, String iNamePrefix, String iNameFirst, String iNameMiddle, String iNameSur, String iNameSuffix, String iNameTitle)
    {
        String string = null;
        String strFinal = Constants.BLANK;
        if (iNamePrefix != null)
        {
            string = record.getField(iNamePrefix).getString();
            if (string.length() != 0)
                strFinal += string + ". ";
        }
        string = record.getField(iNameFirst).getString();
        if (string.length() != 0)
            strFinal += string + " ";
        if (iNameMiddle != null)
        {
            string = record.getField(iNameMiddle).getString();
            if (string.length() != 0)
                strFinal += string + " ";
        }
        string = record.getField(iNameSur).getString();
        if (string.length() != 0)
            strFinal += string;
        if (iNameSuffix != null)
        {
            string = record.getField(iNameSuffix).getString();
            if (string.length() != 0)
                strFinal += " " + string;
        }
        if (iNameTitle != null)
        {
            string = record.getField(iNameTitle).getString();
            if (string.length() != 0)
                strFinal += " " + string;
        }
        return strFinal;
    }
    /**
     * Convert and move string to this field.
     * Split the part of this string into the target fields.
     * Override this method to convert the String to the actual Physical Data Type.
     * @param strSource the state to set the data to.
     * @param bDisplayOption Display the data on the screen if true.
     * @param iMoveMode INIT, SCREEN, or READ move mode.
     * @return The error code (or NORMAL_RETURN).
     */
    public int setString(String strSource, boolean bDisplayOption, int iMoveMode)               // init this field override for other value
    {
        int iErrorReturn = FirstMLastConverter.nameToParts(strSource, bDisplayOption, iMoveMode, m_recThis, m_iNamePrefix, m_iNameFirst, m_iNameMiddle, m_iNameSur, m_iNameSuffix, m_iNameTitle);
        if (iErrorReturn == DBConstants.NORMAL_RETURN)
            if (this.getNextConverter() != null)
                iErrorReturn = super.setString(strSource, bDisplayOption, iMoveMode);
        return iErrorReturn;
    }
    /**
     * Split the part of this string into the target fields.
     * Override this method to convert the String to the actual Physical Data Type.
     * @param strSource the state to set the data to.
     * @param bDisplayOption Display the data on the screen if true.
     * @param iMoveMode INIT, SCREEN, or READ move mode.
     * @return The error code (or NORMAL_RETURN).
     */
    public static int nameToParts(String strSource, boolean bDisplayOption, int iMoveMode, Record record, String iNamePrefix, String iNameFirst, String iNameMiddle, String iNameSur, String iNameSuffix, String iNameTitle)
    {
        char temp;
        String strFirstName = Constants.BLANK, strLastName = Constants.BLANK, strMiddleName = Constants.BLANK, strPrefix = Constants.BLANK, strSuffix = Constants.BLANK, strTitle = Constants.BLANK;
        StringBuffer stringbuf = new StringBuffer(strSource);                   // Get the text just moved here
        int firstChar = 0, lastChar = stringbuf.length() - 1;
        for (int charPos = firstChar + 1; charPos < lastChar; charPos++)
        {   // Convert to mixed upper and lower characters
            if ((stringbuf.charAt(charPos) == ' ') || (stringbuf.charAt(charPos) == '/') || (stringbuf.charAt(charPos) == ',') || (stringbuf.charAt(charPos) == '.'))
            {
                charPos++;      // Next character can be upper
                continue;
            }
            if ((stringbuf.charAt(charPos) >= 'a') && (stringbuf.charAt(charPos) <= 'z')) // !!Make this international friendly
                break;          // Found a lower case, don't convert stringbuf
            temp = stringbuf.charAt(charPos);   // Convert to lower
            temp = Character.toLowerCase(temp);
            stringbuf.setCharAt(charPos, temp);
        }
        for (int charPos = firstChar + 1; charPos < lastChar; charPos++)
        {
            if (stringbuf.charAt(charPos) == ' ')
            {
                if (charPos == firstChar)
                    firstChar++;
                else if (strFirstName.length() == 0)
                {
                    strFirstName = stringbuf.toString().substring(firstChar, charPos);
                    firstChar = charPos + 1;
                    if (strLastName.length() == 0)
                    {
                        for (int charPos2 = lastChar - 1; charPos2 >= charPos; charPos2--)
                        {
                            if (stringbuf.charAt(charPos2) == ' ')
                            {
                                strLastName = stringbuf.toString().substring(charPos2 + 1, lastChar + 1);
                                if (strLastName.indexOf('.') != -1)
                                { // This is a suffix or title
                                    if (strTitle.length() == 0)
                                        strTitle = strLastName;
                                    else if (strSuffix.length() == 0)
                                        strSuffix = strLastName;
                                    strLastName = Constants.BLANK;
                                    lastChar = charPos2 - 1;
                                    continue;
                                }
                                lastChar = charPos2 - 1;
                                break;
                            }
                        }
                    }
                }
                continue;
            }
            if ((stringbuf.charAt(charPos) == ',') || (stringbuf.charAt(charPos) == '/'))
            {
                if (strLastName.length() == 0)
                {
                    strLastName = stringbuf.toString().substring(firstChar, charPos);
                    firstChar = charPos + 1;
                }
                continue;
            }
            if (stringbuf.charAt(charPos) == '.')
            {
                if (charPos - firstChar <= 3) if (strPrefix.length() == 0)
                {
                    boolean bPrefixFlag = false;
                    if (stringbuf.toString().substring(firstChar, charPos - firstChar).equalsIgnoreCase("Mr"))
                        bPrefixFlag = true;
                    if (stringbuf.toString().substring(firstChar, charPos - firstChar).equalsIgnoreCase("Ms"))
                        bPrefixFlag = true;
                    if (stringbuf.toString().substring(firstChar, charPos - firstChar).equalsIgnoreCase("Mrs"))
                        bPrefixFlag = true;
                    if (stringbuf.toString().substring(firstChar, charPos - firstChar).equalsIgnoreCase("Dr"))
                        bPrefixFlag = true;
                    if (bPrefixFlag)
                    {
                        strPrefix = stringbuf.toString().substring(firstChar, charPos - firstChar);
                        firstChar = charPos + 1;
                    }
                }
                continue;
            }
        }
        if (firstChar <= lastChar)
        {
            if (strLastName.length() == 0)
                strLastName = stringbuf.toString().substring(firstChar, lastChar + 1);
            else
            if (strFirstName.length() == 0)
                    strFirstName = stringbuf.toString().substring(firstChar, lastChar + 1);
                else
                    strMiddleName = stringbuf.toString().substring(firstChar, lastChar + 1);
        }
    // Now, move the strings to their respective fields
        if (iNameMiddle == null)
            if (strMiddleName.length() != 0)
                if (!strMiddleName.equals(" "))
                    strFirstName = strFirstName + " " + strMiddleName;
        if (strFirstName.length() > 0)
        {
            temp = strFirstName.charAt(0);
            temp = Character.toUpperCase(temp);
            if (temp != strFirstName.charAt(0))
                strFirstName = temp + strFirstName.substring(1);
        }
        record.getField(iNameFirst).setData(strFirstName, bDisplayOption, iMoveMode);
        if (strMiddleName.length() != 0)
        {
            temp = strMiddleName.charAt(0);
            temp = Character.toUpperCase(temp);
            if (temp != strMiddleName.charAt(0))
                strMiddleName = temp + strMiddleName.substring(1);
        }
        if (iNameMiddle != null)
            record.getField(iNameMiddle).setData(strMiddleName, bDisplayOption, iMoveMode);
        if (strLastName.length() != 0)
        {
            temp = strLastName.charAt(0);
            temp = Character.toUpperCase(temp);
            if (temp != strLastName.charAt(0))
                strLastName = temp + strLastName.substring(1);
        }
        record.getField(iNameSur).setData(strLastName, bDisplayOption, iMoveMode);
        record.getField(iNamePrefix).setData(strPrefix, bDisplayOption, iMoveMode);
        return DBConstants.NORMAL_RETURN;
    }
}
