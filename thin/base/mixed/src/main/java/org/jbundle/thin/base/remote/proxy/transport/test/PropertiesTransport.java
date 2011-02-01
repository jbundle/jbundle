package org.jbundle.thin.base.remote.proxy.transport.test;

/**
 *  ApplicationServer - The interface to server objects.
 *  Copyright (c) 2005 jbundle.org. All rights reserved.
 */
import org.jbundle.thin.base.remote.proxy.ApplicationProxy;
import org.jbundle.thin.base.remote.proxy.transport.BaseTransport;
import org.jbundle.thin.base.util.base64.Base64;

/**
 *  PropertiesTransport - The class that packages all properties and params as properties.
 */
public abstract class PropertiesTransport extends BaseTransport
{

    /**
     * Constructor.
     */
    public PropertiesTransport()
    {
        super();
    }
    /**
     * Constructor.
     */
    public PropertiesTransport(ApplicationProxy appProxy, String strCommand)
    {
        this();
        this.init(appProxy, strCommand);
    }
    /**
     * Constructor.
     */
    public void init(ApplicationProxy appProxy, String strCommand)
    {
        super.init(appProxy, strCommand);
    }
    /**
     * Add this method param to the param list.
     * (By default, uses the property method... override this to use an app specific method).
     * NOTE: The param name DOES NOT need to be saved, as params are always added and retrieved in order.
     * @param strParam The param name.
     * @param strValue The param value.
     */
    public void addParam(String strParam, String strValue)
    {
        if (strValue == null)
            strValue = NULL;
        m_properties.setProperty(strParam, strValue);
    }
    /**
     * Convert this java object to a String.
     * @param obj The java object to convert.
     * @return The object represented as a string.
     */
    public String objectToString(Object obj)
    {
        String string = super.objectToString(obj);
        string = PropertiesTransport.encode(string);
        return string;
    }
    /**
     * Convert this encoded string back to a Java Object.
     * @param string The string to convert.
     * @return The java object.
     */
    public Object stringToObject(String string)
    {
        string = PropertiesTransport.decode(string);
        Object obj = super.stringToObject(string);
        return obj;
    }
    /**
     * Encode this string using base64.
     */
    public static String encode(String string)
    {
        string = Base64.encode(string);
        return string;
    }
    /**
     * Decode this object using base64.
     */
    public static String decode(String string)
    {
        string = Base64.decode(string);
        return string;
    }
}
