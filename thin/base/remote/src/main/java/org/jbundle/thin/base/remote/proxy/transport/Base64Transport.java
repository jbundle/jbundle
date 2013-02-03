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

import org.jbundle.model.RemoteException;
import org.jbundle.thin.base.remote.proxy.ApplicationProxy;
import org.jbundle.thin.base.util.base64.Base64;


/**
 *  ApplicationServer - The interface to server objects.
 */
public abstract class Base64Transport extends BaseTransport
{

    /**
     * Constructor.
     */
    public Base64Transport()
    {
        super();
    }
    /**
     * Constructor.
     */
    public Base64Transport(ApplicationProxy appProxy, String strCommand)
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
     * Convert this java object to a String.
     * @param obj The java object to convert.
     * @return The object represented as a string.
     */
    public String objectToString(Object obj)
    {
        String string = super.objectToString(obj);
        string = Base64.encode(string);
        return string;
    }
    /**
     * Convert this encoded string back to a Java Object.
     * @param string The string to convert.
     * @return The java object.
     */
    public Object stringToObject(String string)
    {
        string = Base64.decode(string);
        Object obj = super.stringToObject(string);
        return obj;
    }
    /**
     * Send this message to the server and return the reply.
     * @return The message.
     */
    public Object sendMessageAndGetReply() throws RemoteException
    {
        try {
            URL url = this.getProxyURL();
            ServletMessage servlet = new ServletMessage(url);
            InputStream in = servlet.sendMessage(m_properties);
            InputStreamReader reader = new InputStreamReader(in);
            BufferedReader buffReader = new BufferedReader(reader);
            String string = "";
            while (true)
            {
                String str = buffReader.readLine();
                if ((str == null) || (str.length() == 0))
                    break;
                if (string.length() > 0)
                    string += "\n";
                string += str;
            }
System.out.println("ProxyTransport.sendandreceive() reply: " + string);
            return string;
        } catch (MalformedURLException ex)  {
            ex.printStackTrace();
        } catch (IOException ex)  {
            ex.printStackTrace();
        }
        return null;
    }
}
