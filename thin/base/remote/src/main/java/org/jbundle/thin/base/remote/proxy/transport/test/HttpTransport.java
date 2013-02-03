/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.remote.proxy.transport.test;

/**
 *  ApplicationServer - The interface to server objects.
 *  Copyright © 2012 jbundle.org. All rights reserved.
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.jbundle.model.RemoteException;
import org.jbundle.thin.base.remote.proxy.ApplicationProxy;
import org.jbundle.thin.base.remote.proxy.transport.BaseTransport;


/**
 *  ApplicationServer - The interface to server objects.
 */
public class HttpTransport extends BaseTransport
{
    protected HttpMessage m_servlet = null;
    protected ObjectOutputStream m_out = null;

    /**
     * Constructor.
     */
    public HttpTransport()
    {
        super();
    }
    /**
     * Constructor.
     */
    public HttpTransport(ApplicationProxy appProxy, String strCommand)
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
        try {
            this.getObjectStream().writeUTF(strValue);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    /**
     * Get the URL connection's output stream (to send params to).
     * @return The (object) output stream.
     */
    public ObjectOutputStream getObjectStream()
    {
        if (m_out == null)
        {
            try {
                URL url = this.getProxyURL();
                m_servlet = new HttpMessage(url);
                OutputStream out = m_servlet.startMessage(m_properties);
                m_out = new ObjectOutputStream(out);
            } catch (MalformedURLException ex)  {
                ex.printStackTrace();
            } catch (IOException ex)  {
                ex.printStackTrace();
            }
        }
        return m_out;
    }
    /**
     * Send this message to the server and return the reply.
     * @return The message.
     */
    public Object sendMessageAndGetReply() throws RemoteException
    {
        try {
            this.getObjectStream();    // If there are no params in this message.
            InputStream in = m_servlet.endMessage();
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
System.out.println("Returns: " + string);
            return string;
        } catch (MalformedURLException ex)  {
            ex.printStackTrace();
        } catch (IOException ex)  {
            ex.printStackTrace();
        }
        return null;
    }
}
