/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.control.servlet.html;

/**
 * @(#)DBServlet.java 0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.jbundle.base.model.DBParams;
import org.jbundle.base.screen.control.servlet.ServletTask;
import org.jbundle.model.App;
import org.jbundle.model.PropertyOwner;
import org.jbundle.model.util.Util;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.util.webapp.base.BaseWebappServlet;

/**
 * RedirectServlet
 * 
 * This is the base servlet.
 */
public class BaseServlet extends BaseWebappServlet
{
	private static final long serialVersionUID = 1L;

    /**
     * init method.
     * @exception ServletException From inherited class.
     */
    public void init(ServletConfig config) throws ServletException
    {
        super.init(config);
    }
    /**
     * Destroy this Servlet and any active applications.
     * This is only called when all users are done using this Servlet.
     */
    public void destroy()
    {
        super.destroy();
    }

    public InputStream getFileStream(ServletTask servletTask, String filename, String filepath) throws MalformedURLException
	{
		URL fileURL = null;
		App app = null;
		if (servletTask != null)
			app = servletTask.getApplication();

		if (filepath == null)
			filepath = Constants.RES_LOCATION;
		String fullFilepath = Util.getFullFilename(filename, null, filepath, true);
		if (app != null)
		    fileURL = app.getResourceURL(fullFilepath, null);
		else
		    fileURL = this.getClass().getClassLoader().getResource(fullFilepath);
		
		if (fileURL == null)
		{
			if (fullFilepath.indexOf(':') == -1)
				fullFilepath = "file:" + fullFilepath;
			fileURL = new URL(fullFilepath);
		}
		
		if (fileURL != null)
		{
		    try {
		        InputStream is = fileURL.openStream();
		        return is;
		    } catch (IOException ex)    {
		    	// return null;
		    }
		}
		return null;
	}

	public static String fixStylesheetPath(String stylesheet, PropertyOwner propertyOwner, boolean addBrowserType)
    {
    	if ((stylesheet == null) || (propertyOwner == null))
    		return null;
		String browser = propertyOwner.getProperty(DBParams.BROWSER);
    	boolean ajax = false;
		if (BaseServlet.AJAX.equals(propertyOwner.getProperty(BaseServlet.ALIAS)))
			ajax = true;
		if (BaseServlet.XSL.equals(propertyOwner.getProperty(BaseServlet.ALIAS)))
			ajax = true;
		if (BaseServlet.XML.equals(propertyOwner.getProperty(BaseServlet.ALIAS)))
			ajax = true;
		if (BaseServlet.JAVA.equals(browser))
			ajax = false;
		if ((stylesheet.indexOf('/') == -1) && (stylesheet.indexOf(File.pathSeparator) == -1))
		{
			if (ajax)
			{
				if ((!BaseServlet.WEBKIT.equals(browser)) && (browser != null))
					stylesheet = JBUNDLE_RESOURCES + "/docs/styles/xsl/ajax/base/" + stylesheet;
				else
					stylesheet = JBUNDLE_RESOURCES + "/docs/styles/xsl/flat/base/" + stylesheet;	// Webkit bug
			}
			else
				stylesheet = JBUNDLE_RESOURCES + "/docs/styles/xsl/cocoon/all/" + stylesheet;	// They should have specified the directory
		}
	    if (!stylesheet.contains("."))
		{
	        if (addBrowserType)
	        {
    			if (ajax)
    				stylesheet = stylesheet + "-ajax";					
    			if (BaseServlet.IE.equals(browser))
    				stylesheet = stylesheet + "-ie";
                else if (BaseServlet.JAVA.equals(browser))
                    stylesheet = stylesheet + "-java";
                else if (BaseServlet.MOBILE.equals(browser))
                    stylesheet = stylesheet + "-mobile";
	        }
			stylesheet = stylesheet + ".xsl";
		}
		return stylesheet;
	}

    // Typical deploy paths
	public static final String ROOT = "/";
	public static final String INDEX = "index.html";
	public static final String IMAGES = "images";
	public static final String LIB = "lib";
	public static final String DOCS = "docs";
	public static final String COM = "com";
	public static final String ORG = "org";
	public static final String JBUNDLE_RESOURCES = "org/jbundle/res";
	public static final String TOURAPP_RESOURCES = "com/tourapp/res";
	public static final String PROXY = "proxy";
	public static final String TOURAPP = Constants.DEFAULT_SERVLET;
	public static final String TABLE = TOURAPP + "/table";
	public static final String IMAGE = TOURAPP + "/image";
	public static final String JNLP = TOURAPP + "/jnlp";
	public static final String TOURAPP_WSDL = TOURAPP + "/wsdl";
	public static final String WSDL = "wsdl";
	public static final String HTML = "HTMLServlet";
	public static final String HTML2 = TOURAPP + "html";
	public static final String TOURAPP_JNLP = TOURAPP + ".jnlp";
	public static final String XML = TOURAPP + "xml";
	public static final String XSL = TOURAPP + "xsl";
	public static final String XHTML = TOURAPP + "xhtml";
	public static final String JNLP_DOWNLOAD = "docs/jnlp";	// "*.jnlp";
	public static final String AJAX = "ajax";
	public static final String MESSAGE = "message";
	public static final String WS = "ws";
	public static final String XMLWS = "xmlws";
	public static final String FAVICON = "favicon.ico";
}
