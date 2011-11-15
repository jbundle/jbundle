/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.web.httpservice;

import java.util.Dictionary;
import java.util.Hashtable;

import javax.servlet.Servlet;

import org.jbundle.base.screen.control.servlet.html.BaseServlet;
import org.jbundle.base.util.DBConstants;
import org.jbundle.util.webapp.osgi.BaseOsgiServlet;
import org.jbundle.util.webapp.osgi.OSGiFileServlet;
import org.osgi.framework.BundleContext;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;

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
    public String[] getServletNames()
    {
    	return paths;
    }
    /**
     * Http Service is up, add my servlet.
     */
    public Servlet addService(String name, HttpService httpService) {
        Servlet servlet = null;
        try {
            Dictionary<String,String> dictionary = new Hashtable<String,String>();
            dictionary.put(BaseServlet.PATH, name);
        	HttpContext httpContext = this.httpContext;
            String alias = this.getPathFromName(name);
        	String servicePid = DBConstants.BLANK;

            if ((BaseServlet.IMAGES.equalsIgnoreCase(name)) 
            	|| (BaseServlet.LIB.equalsIgnoreCase(name))
//+            	|| (BaseServlet.COM.equalsIgnoreCase(path))
//+            	|| (BaseServlet.ORG.equalsIgnoreCase(path))
                || (BaseServlet.DOCS.equalsIgnoreCase(name)))
            {
            	httpService.registerResources(alias, name, httpContext);
            }
            if ((BaseServlet.JBUNDLE_RESOURCES.equalsIgnoreCase(name)) 
                || (BaseServlet.TOURAPP_RESOURCES.equalsIgnoreCase(name)))
            {
            	servlet = new OSGiFileServlet();
            	dictionary.put(OSGiFileServlet.BASE_PATH, alias.substring(1) + '/');	// Prepend this to the path
                ((BaseOsgiServlet)servlet).init(context, servicePid, dictionary);
            	httpContext = new org.jbundle.util.webapp.files.FileHttpContext(context.getBundle());
            }
            if (BaseServlet.PROXY.equalsIgnoreCase(name))
            {
	            servlet = new org.jbundle.base.remote.proxy.ProxyServlet();
	            dictionary.put("remotehost", "localhost");	// Default value
            }
            if ((BaseServlet.TABLE.equalsIgnoreCase(name)) 
            		|| (BaseServlet.IMAGE.equalsIgnoreCase(name))
            		|| (BaseServlet.JNLP.equalsIgnoreCase(name))
            		|| (BaseServlet.WSDL.equalsIgnoreCase(name))
    	    		|| (BaseServlet.TOURAPP_WSDL.equalsIgnoreCase(name))
    	    		|| (BaseServlet.HTML.equalsIgnoreCase(name))
    	    		|| (BaseServlet.HTML2.equalsIgnoreCase(name))
    	    		|| (BaseServlet.TOURAPP_JNLP.equalsIgnoreCase(name))
            		|| (BaseServlet.TOURAPP.equalsIgnoreCase(name)))
            {
            	servlet = new org.jbundle.base.screen.control.servlet.html.HTMLServlet();
                dictionary.put("remotehost", "localhost");	// Default value
            }
            if (BaseServlet.XML.equalsIgnoreCase(name))
            {
	            servlet = new org.jbundle.base.screen.control.servlet.xml.XMLServlet();
//x	            dictionary.put("stylesheet-path", "docs/styles/xsl/flat/base/");	// Since stylesheets are in resources
	            dictionary.put("remotehost", "localhost");
            }
            if ((BaseServlet.XSL.equalsIgnoreCase(name)) 
            	|| (BaseServlet.XHTML.equalsIgnoreCase(name)))
            {
	            servlet = new org.jbundle.base.screen.control.xslservlet.XSLServlet();
            }
            if (BaseServlet.JNLP_DOWNLOAD.equalsIgnoreCase(name))
            {
	          servlet = new org.jbundle.util.webapp.jnlpservlet.JnlpServlet();
//	          servlet = new jnlp.sample.servlet.JnlpDownloadServlet();
	          httpContext = new JnlpHttpContext(context.getBundle());
            }
            if (BaseServlet.AJAX.equalsIgnoreCase(name))
            {
            	servlet = new org.jbundle.base.remote.proxy.AjaxServlet();
            	dictionary.put("remotehost", "localhost");
	            dictionary.put("stylesheet-path", "docs/styles/xsl/flat/base/");	// Since webkit still can't handle import
            }
            if ((BaseServlet.ROOT.equalsIgnoreCase(name)) 
                	|| (BaseServlet.INDEX.equalsIgnoreCase(name)))
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
            if (BaseServlet.MESSAGE.equalsIgnoreCase(name))
            {
	            servlet = new org.jbundle.base.message.trx.transport.html.MessageServlet();
	            dictionary.put("remotehost", "localhost");
            }
            if (BaseServlet.WS.equalsIgnoreCase(name))
            {
	            servlet = new org.jbundle.base.message.trx.transport.jaxm.MessageReceivingServlet();
	            dictionary.put("remotehost", "localhost");
            }
            if (BaseServlet.XMLWS.equalsIgnoreCase(name))
            {
	            servlet = new org.jbundle.base.message.trx.transport.xml.XMLMessageReceivingServlet();
	            dictionary.put("remotehost", "localhost");
            }
            if (servlet instanceof BaseOsgiServlet)
                ((BaseOsgiServlet) servlet).init(context, servicePid, dictionary);
            if (servlet != null)
                httpService.registerServlet(alias, servlet, dictionary, httpContext);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return servlet;
    }
}
