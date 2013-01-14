/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.remote.proxy.transport;

/**
 *  ApplicationServer - The interface to server objects.
 *  Copyright © 2012 jbundle.org. All rights reserved.
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.remote.RemoteException;
import org.jbundle.thin.base.remote.proxy.ApplicationProxy;


/**
 *  ApplicationServer - The interface to server objects.
 */
public class EncodedTransport extends Base64Transport
{

    /**
     * Constructor.
     */
    public EncodedTransport()
    {
        super();
    }
    /**
     * Constructor.
     */
    public EncodedTransport(ApplicationProxy appProxy, String strCommand)
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
//        string = PropertiesTransport.encode(string);
        return string;
    }
    /**
     * Convert this encoded string back to a Java Object.
     * @param string The string to convert.
     * @return The java object.
     */
    public Object stringToObject(String string)
    {
//        string = PropertiesTransport.decode(string);
        Object obj = super.stringToObject(string);
        return obj;
    }
    /**
     * Send this message to the server and return the reply.
     * @return The message.
     */
    public Object sendMessageAndGetReply() throws RemoteException
    {
        URL url = null;
        try {
            url = this.getProxyURL();
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
            return null;
        }
        ServletMessage servlet = new ServletMessage(url);
        while (true)
        {   // to allow for retry on timeout
            long time = 0;
            try {
                time = System.currentTimeMillis();
                InputStream in = servlet.sendMessage(m_properties);
                InputStreamReader reader = new InputStreamReader(in);
                BufferedReader buffReader = new BufferedReader(reader);
                String string = Constants.BLANK;
                while (true)
                {
                    String str = buffReader.readLine();
                    if ((str == null) || (str.length() == 0))
                        break;
                    if (string.length() > 0)
                        string += "\n";
                    string += str;
                }
                return string;
            } catch (IOException ex)  {
                if (System.currentTimeMillis() > time + MAX_WAIT_TIME)
                    continue;   // Server timeout, try again
                throw new RemoteException(ex.getMessage() + ' ' + url, ex.getCause());
            }
        }
    }
    private static final long MAX_WAIT_TIME = 30 * 1000;
}
