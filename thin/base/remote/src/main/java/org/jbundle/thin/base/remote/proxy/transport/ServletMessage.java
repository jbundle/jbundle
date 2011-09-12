/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.remote.proxy.transport;

/*
 * ServletMessage.java
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
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
public class ServletMessage {

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
    public ServletMessage(URL servlet) {
        this.servlet = servlet;
    }

    /**
     * Send the request using POST. Return the input stream with the
     * response if the request succeeds.
     * @param args the arguments to send to the servlet
     * @exception IOException if error sending request
     * @return the response from the servlet to this message
     */
    public InputStream sendMessage(Properties args) throws IOException { 
	return sendMessage(args, POST);
    }


    /**
     * Send the request. Return the input stream with the response if
     * the request succeeds.
     * @param args the arguments to send to the servlet
     * @param method GET or POST
     * @exception IOException if error sending request
     * @return the response from the servlet to this message
     */
    public InputStream sendMessage(Properties args, int method) 
	throws IOException { 

        // Set this up any way you want -- POST can be used for all calls,
        // but request headers cannot be set in JDK 1.0.2 so the query
        // string still must be used to pass arguments.
        if (method == GET)
        {
            URL url = new URL(servlet.toExternalForm() + "?" + toEncodedString(args));
            return url.openStream(); 
        }
        else
        {
            URLConnection conn = servlet.openConnection();
            conn.setDoOutput(true);
            conn.setUseCaches(false);

            // POST the request data (html form encoded)
            PrintStream out = new PrintStream(conn.getOutputStream());
            if (args != null && args.size() > 0)
            {
                out.print(toEncodedString(args));
            }
            out.close(); // ESSENTIAL for this to work!

            // Read the POST response data
            return conn.getInputStream();
        }
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
			        sb.append(sep).append(URLEncoder.encode(name, Constants.URL_ENCODING)).append("=").append(
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
