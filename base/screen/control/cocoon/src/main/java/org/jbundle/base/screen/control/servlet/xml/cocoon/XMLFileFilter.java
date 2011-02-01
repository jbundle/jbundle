package org.jbundle.base.screen.control.servlet.xml.cocoon;

/**
 * @(#)DBServlet.java 0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import javax.servlet.http.HttpServletRequest;

/**
 * XMLFileFilter - File producer.
 * NOTE: This is exactly like a cocoon FileProducer, except this class takes
 * the filename as a parameter (ie., ?file=filepath).
 * The advantage is all files can be referenced as if they were in the root
 * directory.
 */
public class XMLFileFilter extends XMLFilter
{
    public static final String FILE_PARAM = "file";

    /**
     * Constructor.
     */
    public XMLFileFilter()
    {
        super();
    }
    /**
     * Get the status.
     */
    public String getPath(HttpServletRequest request)
    {
        return "";
    }
    /**
     * Get the status.
     */
    public String getStatus()
    {
        return "Help Producer";
    }
    /**
     * Process the stream and return the request.
     */
    public Reader getStream(HttpServletRequest request)
        throws IOException
    {
        Reader reader = null;

        String strParams[] = request.getParameterValues(FILE_PARAM);                // Menu page
        if (strParams != null)
            if (strParams.length > 0)
                if (strParams[0] != null)
        {
            String strFilename = strParams[0];
            strFilename = this.getRealPath(request, strFilename);
            reader = new FileReader(strFilename);
            return reader;
        }

        String string =
            "<?xml version=\"1.0\"?>" +
            "<?xml-stylesheet type=\"text/xsl\" href=\"docs/styles/xsl/help.xsl\"?>" +
            "<?cocoon-process type=\"xslt\"?>" +
                "<html>" +
                "<head>" +
                "<title>File not found</title>" +
                "</head>" +
                "<body>" +
                "File not found" +
                "</body>" +
                "</html>";
        return new StringReader(string);
    }
}
