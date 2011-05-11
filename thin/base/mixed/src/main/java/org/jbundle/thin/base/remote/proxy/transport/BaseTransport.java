package org.jbundle.thin.base.remote.proxy.transport;

/**
 *  ApplicationServer - The interface to server objects.
 *  Copyright (c) 2005 jbundle.org. All rights reserved.
 */
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

import org.jbundle.model.util.Constant;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.remote.proxy.ApplicationProxy;
import org.jbundle.thin.base.remote.proxy.ProxyConstants;
import org.jbundle.util.osgi.finder.ClassServiceImpl;


/**
 *  ApplicationServer - The interface to server objects.
 */
public abstract class BaseTransport extends Object
    implements ProxyConstants
{
    /**
     * The application proxy (which contains the info I need to connect to the server).
     */
    protected ApplicationProxy m_appProxy = null;
    /**
     * The command properties.
     */
    protected Properties m_properties = null;

    /**
     * Constructor.
     */
    public BaseTransport()
    {
        super();
    }
    /**
     * Constructor.
     */
    public BaseTransport(ApplicationProxy appProxy, String strCommand)
    {
        this();
        this.init(appProxy, strCommand);
    }
    /**
     * Constructor.
     */
    public void init(ApplicationProxy appProxy, String strCommand)
    {
        m_appProxy = appProxy;
        m_properties = new Properties();
        this.setProperty(REMOTE_COMMAND, strCommand);
    }
    /**
     * Set this comand property.
     * @param strParam The param name.
     * @param strValue The param value.
     */
    public void setProperty(String strProperty, String strValue)
    {
        if (strValue == null)
            strValue = NULL;
        if (strValue != null)
            m_properties.setProperty(strProperty, strValue);
        else
            m_properties.remove(strProperty);
    }
    /**
     * Add this method param to the param list.
     * (By default, uses the property method... override this to use an app specific method).
     * NOTE: The param name DOES NOT need to be saved, as params are always added and retrieved in order.
     * @param strParam The param name.
     * @param strValue The param value.
     */
    public abstract void addParam(String strParam, String strValue);
    /**
     * Add this method param to the param list.
     * @param strParam The param name.
     * @param strValue The param value.
     */
    public void addParam(String strParam, int iValue)
    {
        this.addParam(strParam, Integer.toString(iValue));
    }
    /**
     * Add this method param to the param list.
     * @param strParam The param name.
     * @param strValue The param value.
     */
    public void addParam(String strParam, boolean bValue)
    {
        this.addParam(strParam, bValue ? Constants.TRUE : Constants.FALSE);
    }
    /**
     * Add this method param to the param list.
     * @param strParam The param name.
     * @param strValue The param value.
     */
    public void addParam(String strParam, Object obj)
    {
        String strValue = this.objectToString(obj);
        this.addParam(strParam, strValue);
    }
    /**
     * Add this method param to the param list.
     * @param strParam The param name.
     * @param strValue The param value.
     */
    public void addParam(String strParam, Map<String,Object> properties)
    {
        this.addParam(strParam, (Object)properties);
    }
    /**
     * Convert this java object to a String.
     * @param obj The java object to convert.
     * @return The object represented as a string.
     */
    public String objectToString(Object obj)
    {
        String string = BaseTransport.convertObjectToString(obj);
        return string;
    }
    /**
     * Convert this encoded string back to a Java Object.
     * @param string The string to convert.
     * @return The java object.
     */
    public Object stringToObject(String string)
    {
        Object obj = BaseTransport.convertStringToObject(string);
        return obj;
    }
    /**
     * Convert this java object to a String.
     * TODO This is expensive, I need to synchronize and use a static writer.
     * @param obj The java object to convert.
     * @return The object represented as a string.
     */
    public static String convertObjectToString(Object obj)
    {
        try {
            ByteArrayOutputStream writer = new ByteArrayOutputStream();
            ObjectOutputStream outStream = new ObjectOutputStream(writer);
            outStream.writeObject(obj);
            outStream.flush();
            writer.flush();
            String string = writer.toString(Constant.OBJECT_ENCODING);//Constants.STRING_ENCODING);
            writer.close();
            outStream.close();
            return string;
        } catch (IOException ex)    {
            ex.printStackTrace();   // Never
        }
        return null;
    }
    /**
     * Convert this encoded string back to a Java Object.
     * TODO This is expensive, I need to synchronize and use a static writer.
     * @param string The string to convert.
     * @return The java object.
     */
    public static Object convertStringToObject(String string)
    {
        return ClassServiceImpl.getClassService().convertStringToObject(string, null, true);
    }
    /**
     * Convert the return value to an object (override if this doesn't just to string to object).
     * @param ojb The returned object to convert.
     * @return The converted object.
     */
    public Object convertReturnObject(Object obj)
    {
        return this.stringToObject((String)obj);
    }
    /**
     * Send this message to the server and return the reply.
     * @return The message.
     */
    public abstract Object sendMessageAndGetReply();
    /**
     * Get the http proxy URL.
     * @return The message.
     */
    public static final String PROXY_SERVLET = "proxy";

    public URL getProxyURL()
        throws MalformedURLException
    {
            String strCodeBase = m_appProxy.getBaseServletPath();
            if ((strCodeBase == null) || (strCodeBase.length() == 0))
            {
                String strServer = m_appProxy.getAppServerName();
                strCodeBase = "http://" + strServer;
            }
            if (strCodeBase.endsWith(Constants.DEFAULT_SERVLET))
                strCodeBase = strCodeBase.substring(0, strCodeBase.length() - (Constants.DEFAULT_SERVLET.length() + 1));
            strCodeBase = strCodeBase + "/" + PROXY_SERVLET;
            URL url = new URL(strCodeBase);
            return url;
    }
}
