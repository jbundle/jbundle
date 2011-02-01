package org.jbundle.thin.base.remote.proxy.transport.test;

/**
 *  ApplicationServer - The interface to server objects.
 *  Copyright (c) 2005 jbundle.org. All rights reserved.
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.jbundle.thin.base.remote.proxy.ApplicationProxy;
import org.jbundle.thin.base.remote.proxy.transport.ServletMessage;


/**
 *  ApplicationServer - The interface to server objects.
 */
public class MessageTransport extends PropertiesTransport
{

    /**
     * Constructor.
     */
    public MessageTransport()
    {
        super();
    }
    /**
     * Constructor.
     */
    public MessageTransport(ApplicationProxy appProxy, String strCommand)
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
     * Send this message to the server and return the reply.
     * @return The message.
     */
    public Object sendMessageAndGetReply()
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
