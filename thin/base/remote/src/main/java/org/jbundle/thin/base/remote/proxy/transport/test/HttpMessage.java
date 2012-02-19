/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.remote.proxy.transport.test;

/*
 * ServletMessage.java
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Properties;

import org.jbundle.thin.base.db.Constants;


/**
 * Send a message to an HTTP servlet. The protocol is a GET or POST request
 * with a URLEncoded string holding the arguments sent as name=value
 * value pairs.
 */
public class HttpMessage {

    /**
     * The GET method constant
     */
    public static int GET = 0;

    /**
     * The POST method constant
     */
    public static int POST = 1;


    /**
     * @param servlet the URL of the servlet to send messages to 
     */
    public HttpMessage(URL servlet)
    {
        this.servlet = servlet;
    }

    /**
     * Send the request using POST. Return the input stream with the
     * response if the request succeeds.
     * @param args the arguments to send to the servlet
     * @exception IOException if error sending request
     * @return the response from the servlet to this message
     */
    public OutputStream startMessage(Properties args) throws IOException
    { 
        URL url = new URL(servlet.toExternalForm() + "?" + toEncodedString(args));
System.out.println("Open URL: " + url.toString());
        m_conn = servlet.openConnection();
        m_conn.setDoOutput(true);
        m_conn.setUseCaches(false);

        // POST the request data (html form encoded)
        return m_out = m_conn.getOutputStream();
    }
    protected URLConnection m_conn = null;
    protected OutputStream m_out = null;
    /**
     * Send the request using POST. Return the input stream with the
     * response if the request succeeds.
     * @param args the arguments to send to the servlet
     * @exception IOException if error sending request
     * @return the response from the servlet to this message
     */
    public InputStream endMessage() throws IOException
    { 
        m_out.close(); // ESSENTIAL for this to work!

        // Read the POST response data
        return m_conn.getInputStream();
    }
    /**
     * Encode the arguments in the property set as a URL-encoded string.
     * Multiple name=value pairs are separated by ampersands.
     * @return the URLEncoded string with name=value pairs
     */
    public String toEncodedString(Properties args) {
        StringBuffer sb = new StringBuffer();
        try {
			if (args != null) {
			    String sep = "";
			    Enumeration<?> names = args.propertyNames();
			    while (names.hasMoreElements()) {
			        String name = (String)names.nextElement();
			        sb.append(sep + URLEncoder.encode(name, Constants.URL_ENCODING) + "=" +
			                  URLEncoder.encode(args.getProperty(name), Constants.URL_ENCODING));
			        sep = "&";
			    }
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
        return sb.toString();
    }

    // Private members
    private URL servlet;
}
