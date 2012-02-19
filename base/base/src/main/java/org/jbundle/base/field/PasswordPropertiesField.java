/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field;

/**
 * @(#)PropertiesField.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;

import org.jbundle.base.db.Record;
import org.jbundle.base.field.convert.encode.XorEncryptedConverter;
import org.jbundle.thin.base.util.base64.Base64;


/**
 * A field which holds a Java Properties object.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class PasswordPropertiesField extends PropertiesField
{
	private static final long serialVersionUID = 1L;

    protected Set<String> m_setPropertiesDescriptions = null;

    /**
     * Constructor.
     */
    public PasswordPropertiesField()
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
    public PasswordPropertiesField(Record record, String strName, int iDataLength, String strDesc, Object strDefault)
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
    }
    /**
     * Creates a new object of the same class as this object.
     * @return     a clone of this instance.
     * @exception  CloneNotSupportedException  if the object's class does not support the <code>Cloneable</code> interface.
     * @see        java.lang.Cloneable
     */
    public Object clone() throws CloneNotSupportedException
    {
        BaseField field = new PasswordPropertiesField(null, m_strFieldName, m_iMaxLength, m_strFieldDesc, null);
        field.setRecord(m_record);     // Set table without adding to table field list
        return field;
    }
    /**
     * 
     */
    public void free()
    {
        super.free();
    }
    /**
     * Add this to the list of properties that must be encrypted.
     * @param strProperty
     */
    public void addPasswordProperty(String strProperty)
    {
        if (m_setPropertiesDescriptions == null)
            m_setPropertiesDescriptions = new HashSet<String>();
        if (strProperty != null)
            m_setPropertiesDescriptions.add(strProperty);
        else
            m_setPropertiesDescriptions.remove(strProperty);
    }
    /**
     * Set this property in the user's property area.
     * @param strProperty The property key.
     * @param strValue The property value.
     * @param iDisplayOption If true, display the new field.
     * @param iMoveMove The move mode.
     * @return An error code (NORMAL_RETURN for success).
     */
    public int setProperty(String strProperty, String strValue, boolean bDisplayOption, int iMoveMode)
    {
        if (m_setPropertiesDescriptions != null)
            if (m_setPropertiesDescriptions.contains(strProperty))
        {
                byte[] rgbValue = strValue.getBytes();
                rgbValue = XorEncryptedConverter.encrypt(rgbValue, key);
                char[] chars = Base64.encode(rgbValue);
                strValue = new String(chars);
        }
        return super.setProperty(strProperty, strValue, bDisplayOption, iMoveMode);
    }
    private static final byte key = 'P';
    /**
     * 
     * @param string
     * @return
     */
    public static String decrypt(String string)
    {
        byte[] rgbValue = string.getBytes();
        try {
            rgbValue = Base64.decode(rgbValue);
            rgbValue = XorEncryptedConverter.encrypt(rgbValue, key);
            string = new String(rgbValue, Base64.DEFAULT_ENCODING);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return string;        
    }
} 
