/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field;

/**
 * @(#)PropertiesField.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Map;
import java.util.Properties;

import org.jbundle.base.db.Record;
import org.jbundle.base.model.DBConstants;


/**
 * A field which holds a Java Properties object.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class XMLPropertiesField extends PropertiesField
{
	private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     */
    public XMLPropertiesField()
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
    public XMLPropertiesField(Record record, String strName, int iDataLength, String strDesc, Object strDefault)
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
            m_iMaxLength = BIG_DEFAULT_LENGTH;
    }
    /**
     * Creates a new object of the same class as this object.
     * @return     a clone of this instance.
     * @exception  CloneNotSupportedException  if the object's class does not support the <code>Cloneable</code> interface.
     * @see        java.lang.Cloneable
     */
    public Object clone() throws CloneNotSupportedException
    {
        BaseField field = new XMLPropertiesField(null, m_strFieldName, m_iMaxLength, m_strFieldDesc, null);
        field.setRecord(m_record);     // Set table without adding to table field list
        return field;
    }
    /**
     * Load the properties from the string and parse them.
     * @return The java properties.
     */
    public Map<String,Object> internalStringToProperties(String strProperties)
    {
        return XMLPropertiesField.xmlToProperties(strProperties);
    }
    /**
     * Load the properties from the string and parse them.
     * @return The java properties.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Map<String,Object> xmlToProperties(String strProperties)
    {
        Properties properties = new Properties();
        if ((strProperties != null) && (strProperties.length() > 0))
        {
            strProperties = DTD + strProperties;
            ByteArrayOutputStream baOut = new ByteArrayOutputStream();
            OutputStreamWriter osOut = new OutputStreamWriter(baOut);
            try   {
                osOut.write(strProperties);     // Char->Byte
                osOut.flush();
                baOut.flush();
                ByteArrayInputStream baIn = new ByteArrayInputStream(baOut.toByteArray());
                properties.loadFromXML(baIn);
            } catch (IOException ex)    {
                ex.printStackTrace();
            }
        }
        return (Map)properties;
    }
    public static final String DTD = "<!DOCTYPE properties SYSTEM \"http://java.sun.com/dtd/properties.dtd\">\n";
    /**
     * Convert these java properties to a string.
     * @param properties The java properties.
     * @return The properties string.
     */
    public String propertiesToInternalString(Map<String,Object> properties)
    {
        return XMLPropertiesField.propertiesToXML(properties);
    }
    /**
     * Convert these java properties to a string.
     * @param properties The java properties.
     * @return The properties string.
     */
    public static String propertiesToXML(Map<String,Object> map)
    {
        String strProperties = null;
        ByteArrayOutputStream baOut = new ByteArrayOutputStream();
        try   {
            Properties properties = new Properties();
            properties.putAll(map);
            properties.storeToXML(baOut, PROPERTIES_COMMENT);
            byte[] rgBytes = baOut.toByteArray();
            ByteArrayInputStream baIn = new ByteArrayInputStream(rgBytes);
            InputStreamReader isIn = new InputStreamReader(baIn); // byte -> char
            char[] cbuf = new char[rgBytes.length];
            isIn.read(cbuf, 0, rgBytes.length);
            if (cbuf.length == rgBytes.length)
                strProperties = new String(cbuf);
        } catch (IOException ex)    {
            ex.printStackTrace();
        }
        if (strProperties != null)
        {
            int iStart = strProperties.indexOf("<properties");
            if (iStart != -1)
                strProperties = strProperties.substring(iStart);
        }
        return strProperties;
    }
} 
