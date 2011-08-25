package org.jbundle.base.screen.control.servlet.html;

/**
 * @(#)DBServlet.java 0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import java.io.File;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import org.jbundle.base.util.DBParams;
import org.jbundle.model.PropertyOwner;

/**
 * RedirectServlet
 * 
 * This servlet is the redirect servlet.
 */
public class BaseServlet extends HttpServlet
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
    /**
     * Get the browser type.
     */
    public String getBrowser(HttpServletRequest req)
    {
        String strAgent = req.getHeader("user-agent");
        if (strAgent == null)
            return OTHER;
        strAgent = strAgent.toUpperCase();
        for (int i = 0; i < BROWSER.length; i++)
        {
            if (strAgent.indexOf(BROWSER[i][1]) != -1)
                return BROWSER[i][0];
        }
        return OTHER;
    }
    /**
     * Get the operating system type.
     */
    public String getOS(HttpServletRequest req)
    {
        String strAgent = req.getHeader("user-agent");
        if (strAgent == null)
            return OTHER;
        strAgent = strAgent.toUpperCase();
        for (int i = 0; i < OS.length; i++)
        {
            if (strAgent.indexOf(OS[i][1]) != -1)
                return OS[i][0];
        }
        return OTHER;
    }

    public static String fixStylesheetPath(String stylesheet, PropertyOwner propertyOwner)
    {
    	if ((stylesheet == null) || (propertyOwner == null))
    		return null;
		String browser = propertyOwner.getProperty(DBParams.BROWSER);
    	boolean ajax = false;
		if (BaseServlet.AJAX.equals(propertyOwner.getProperty(BaseServlet.PATH)))
			ajax = true;
		if (BaseServlet.XSL.equals(propertyOwner.getProperty(BaseServlet.PATH)))
			ajax = true;
		if (BaseServlet.XML.equals(propertyOwner.getProperty(BaseServlet.PATH)))
			ajax = true;
		if (BaseServlet.JAVA.equals(browser))
			ajax = false;
		if ((stylesheet.indexOf('/') == -1) && (stylesheet.indexOf(File.pathSeparator) == -1))
		{
			if (ajax)
			{
				if ((!BaseServlet.WEBKIT.equals(browser)) && (browser != null))
					stylesheet = "docs/styles/xsl/ajax/base/" + stylesheet;
				else
					stylesheet = "docs/styles/xsl/flat/base/" + stylesheet;	// Webkit bug
			}
			else
				stylesheet = "docs/styles/xsl/cocoon/base/" + stylesheet;
		}
		if (!stylesheet.contains("."))
		{
			if (ajax)
				stylesheet = stylesheet + "-ajax";					
			if (BaseServlet.IE.equals(browser))
				stylesheet = stylesheet + "-ie";
			else if (BaseServlet.JAVA.equals(browser))
				stylesheet = stylesheet + "-java";
			stylesheet = stylesheet + ".xsl";
		}
		return stylesheet;
	}

    public static final String IE = "ie";
    public static final String FIREFOX = "firefox";
    public static final String WEBKIT = "webkit";
    public static final String JAVA = "java";
    public static final String OTHER = "other";
    
    public static final String WINDOWS = "WINDOWS";
    public static final String LINUX = "LINUX";
    public static final String MAC = "MAC";
    
    public static String[][] OS = {
        {WINDOWS, WINDOWS},
        {LINUX, LINUX},
        {MAC, MAC},
    };

    public static String[][] BROWSER = {
        {IE, "MSIE"},
        {WEBKIT, "CHROME"},
        {WEBKIT, "SAFARI"},
        {WEBKIT, "OPERA"},
        {JAVA, "JAVA"},
        {FIREFOX, "MOZILLA/5"},
        {WEBKIT, "webkit"},
        {OTHER, ""}
    };
    
    // Typical deploy paths
	public static final String PATH = "path";	

	public static final String ROOT = "/";
	public static final String INDEX = "/index.html";
	public static final String IMAGES = "/images";
	public static final String LIB = "/lib";
	public static final String DOCS = "/docs";
	public static final String PROXY = "/proxy";
	public static final String TOURAPP = "/tourapp";
	public static final String TABLE = TOURAPP + "/table";
	public static final String IMAGE = TOURAPP + "/image";
	public static final String JNLP = TOURAPP + "/jnlp";
	public static final String TOURAPP_WSDL = TOURAPP + "/wsdl";
	public static final String WSDL = "/wsdl";
	public static final String HTML = "/HTMLServlet";
	public static final String HTML2 = TOURAPP + "html";
	public static final String TOURAPP_JNLP = TOURAPP + ".jnlp";
	public static final String XML = TOURAPP + "xml";
	public static final String XSL = TOURAPP + "xsl";
	public static final String XHTML = TOURAPP + "xhtml";
	public static final String JNLP_DOWNLOAD = "/docs/jnlp";	// "*.jnlp";
	public static final String AJAX = "/ajax";
	public static final String MESSAGE = "/message";
	public static final String WS = "/ws";
	public static final String XMLWS = "/xmlws";
}
