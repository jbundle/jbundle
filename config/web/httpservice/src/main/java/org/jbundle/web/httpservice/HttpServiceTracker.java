/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.web.httpservice;

import java.util.Dictionary;

import javax.servlet.Servlet;

import org.jbundle.base.screen.control.servlet.html.BaseServlet;
import org.jbundle.base.util.DBConstants;
import org.jbundle.util.webapp.osgi.BaseOsgiServlet;
import org.jbundle.util.webapp.osgi.OSGiFileServlet;
import org.osgi.framework.BundleContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

/**
 * HttpServiceTracker - Wait for the http service to come up to add servlets.
 * 
 * @author don
 *
 */
public class HttpServiceTracker extends org.jbundle.util.webapp.osgi.HttpServiceTracker {

	/**
	 * Constructor - Listen for HttpService.
	 * @param context
	 */
    public HttpServiceTracker(BundleContext context) {
        super(context, null, null);
    }
    
    String[] paths = {
    		BaseServlet.IMAGES,
    		BaseServlet.LIB,
    		BaseServlet.DOCS,
//+    		BaseServlet.COM,
//+    		BaseServlet.ORG,
    		BaseServlet.JBUNDLE_RESOURCES,
    		BaseServlet.TOURAPP_RESOURCES,
    		BaseServlet.WS,
    		BaseServlet.WSDL,
    		BaseServlet.PROXY,
    		BaseServlet.TABLE,
    		BaseServlet.IMAGE,
    		BaseServlet.JNLP,
    		BaseServlet.TOURAPP_WSDL,
    		BaseServlet.HTML,
    		BaseServlet.HTML2,
    		BaseServlet.AJAX,
    		BaseServlet.TOURAPP_JNLP,
    		BaseServlet.TOURAPP,
    		BaseServlet.XML,
    		BaseServlet.XSL,
    		BaseServlet.XHTML,
    		BaseServlet.JNLP_DOWNLOAD,
    		BaseServlet.MESSAGE,
    		BaseServlet.XMLWS,
    		BaseServlet.ROOT,
    };
    /**
     * Get all the web paths to add.
     * @return
     */
    public String[] getServletAliases(Dictionary<String, String> dictionary)
    {
    	return paths;
    }
    /**
     * Http Service is up, add my servlet.
     */
    public Servlet addService(String alias, Dictionary<String, String> dictionary, HttpService httpService)
    {
        if ((BaseServlet.IMAGES.equalsIgnoreCase(alias)) 
                || (BaseServlet.LIB.equalsIgnoreCase(alias))
//+             || (BaseServlet.COM.equalsIgnoreCase(path))
//+             || (BaseServlet.ORG.equalsIgnoreCase(path))
                || (BaseServlet.DOCS.equalsIgnoreCase(alias)))
        {
            alias = this.fixAlias(alias);
            try {
                httpService.registerResources(alias, alias, httpContext);
            } catch (NamespaceException e) {
                e.printStackTrace();
            }
            return null;
        }
        else
            return super.addService(alias, dictionary, httpService);
    }
    /**
     * Create the servlet.
     * The SERVLET_CLASS property must be supplied.
     * @param dictionary
     * @return
     */
    public Servlet makeServlet(String alias, Dictionary<String, String> dictionary)
    {
        Servlet servlet = null;
        try {
        	String servicePid = DBConstants.BLANK;

            if ((BaseServlet.JBUNDLE_RESOURCES.equalsIgnoreCase(alias)) 
                || (BaseServlet.TOURAPP_RESOURCES.equalsIgnoreCase(alias)))
            {
            	servlet = new OSGiFileServlet();
                alias = this.fixAlias(alias);
            	dictionary.put(OSGiFileServlet.BASE_PATH, alias.substring(1) + '/');	// Prepend this to the path
                ((BaseOsgiServlet)servlet).init(context, servicePid, dictionary);
            	httpContext = new org.jbundle.util.webapp.files.FileHttpContext(context.getBundle());
            }
            if (BaseServlet.PROXY.equalsIgnoreCase(alias))
            {
	            servlet = new org.jbundle.base.remote.proxy.ProxyServlet();
	            dictionary.put("remotehost", "localhost");	// Default value
            }
            if ((BaseServlet.TABLE.equalsIgnoreCase(alias)) 
            		|| (BaseServlet.IMAGE.equalsIgnoreCase(alias))
            		|| (BaseServlet.JNLP.equalsIgnoreCase(alias))
            		|| (BaseServlet.WSDL.equalsIgnoreCase(alias))
    	    		|| (BaseServlet.TOURAPP_WSDL.equalsIgnoreCase(alias))
    	    		|| (BaseServlet.HTML.equalsIgnoreCase(alias))
    	    		|| (BaseServlet.HTML2.equalsIgnoreCase(alias))
    	    		|| (BaseServlet.TOURAPP_JNLP.equalsIgnoreCase(alias))
            		|| (BaseServlet.TOURAPP.equalsIgnoreCase(alias)))
            {
            	servlet = new org.jbundle.base.screen.control.servlet.html.HTMLServlet();
                dictionary.put("remotehost", "localhost");	// Default value
            }
            if (BaseServlet.XML.equalsIgnoreCase(alias))
            {
	            servlet = new org.jbundle.base.screen.control.servlet.xml.XMLServlet();
//x	            dictionary.put("stylesheet-path", "docs/styles/xsl/flat/base/");	// Since stylesheets are in resources
	            dictionary.put("remotehost", "localhost");
            }
            if ((BaseServlet.XSL.equalsIgnoreCase(alias)) 
            	|| (BaseServlet.XHTML.equalsIgnoreCase(alias)))
            {
	            servlet = new org.jbundle.base.screen.control.xslservlet.XSLServlet();
                dictionary.put("remotehost", "localhost");
            }
            if (BaseServlet.JNLP_DOWNLOAD.equalsIgnoreCase(alias))
            {
	          servlet = new org.jbundle.util.webapp.jnlpservlet.JnlpServlet();
//	          servlet = new jnlp.sample.servlet.JnlpDownloadServlet();
	          httpContext = new JnlpHttpContext(context.getBundle());
            }
            if (BaseServlet.AJAX.equalsIgnoreCase(alias))
            {
            	servlet = new org.jbundle.base.remote.proxy.AjaxServlet();
            	dictionary.put("remotehost", "localhost");
	            dictionary.put("stylesheet-path", "docs/styles/xsl/flat/base/");	// Since webkit still can't handle import
            }
            if ((BaseServlet.ROOT.equalsIgnoreCase(alias)) 
                	|| (BaseServlet.INDEX.equalsIgnoreCase(alias)))
            {
	            dictionary.put("regex", "www.+.tourgeek.com");
	            dictionary.put("regexTarget", "demo/index.html");
	            dictionary.put("ie", "tourappxsl");
	            dictionary.put("firefox", "tourappxsl");
	            dictionary.put("chrome", "tourappxsl");
	            dictionary.put("safari", "tourappxsl");
	            dictionary.put("webkit", "tourappxsl");
	            dictionary.put("java", "tourappxhtml");
	            servlet = new org.jbundle.util.webapp.redirect.RegexRedirectServlet();
            }
            if (BaseServlet.MESSAGE.equalsIgnoreCase(alias))
            {
	            servlet = new org.jbundle.base.message.trx.transport.html.MessageServlet();
	            dictionary.put("remotehost", "localhost");
            }
            if (BaseServlet.WS.equalsIgnoreCase(alias))
            {
	            servlet = new org.jbundle.base.message.trx.transport.jaxm.MessageReceivingServlet();
	            dictionary.put("remotehost", "localhost");
            }
            if (BaseServlet.XMLWS.equalsIgnoreCase(alias))
            {
	            servlet = new org.jbundle.base.message.trx.transport.xml.XMLMessageReceivingServlet();
	            dictionary.put("remotehost", "localhost");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return servlet;
    }
}
