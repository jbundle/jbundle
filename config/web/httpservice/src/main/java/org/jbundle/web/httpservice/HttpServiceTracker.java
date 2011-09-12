/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.web.httpservice;

import java.util.Dictionary;
import java.util.Hashtable;

import javax.servlet.Servlet;

import org.jbundle.base.screen.control.servlet.html.BaseServlet;
import org.jbundle.util.webapp.osgi.OSGiFileServlet;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;

/**
 * HttpServiceTracker - Wait for the http service to come up to add servlets.
 * 
 * @author don
 *
 */
public class HttpServiceTracker extends ServiceTracker{

	// Set this param to change root URL
	public static final String WEB_CONTEXT = "org.jbundle.web.webcontext";
	String webContextPath = null;
	
	/**
	 * Constructor - Listen for HttpService.
	 * @param context
	 */
    public HttpServiceTracker(BundleContext context) {
        super(context, HttpService.class.getName(), null);
    }
    
    /**
     * Http Service is up, add my servlets.
     */
    public Object addingService(ServiceReference reference) {
        HttpService httpService = (HttpService) context.getService(reference);
        
        this.addServices(httpService);
        
        return httpService;
    }
    String[] paths = {
    		BaseServlet.IMAGES,
    		BaseServlet.LIB,
    		BaseServlet.DOCS,
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
     * Http Service is up, add my servlets.
     */
    public void addServices(HttpService httpService) {
    	for (String path : paths)
    	{
    		this.addService(path, httpService);
    	}
    }
    /**
     * Http Service is up, add my servlets.
     */
    public void addService(String path, HttpService httpService) {
        try {
            Servlet servlet = null;
            Dictionary<String,String> dictionary = new Hashtable<String,String>();
            dictionary.put(BaseServlet.PATH, path);
        	HttpContext httpContext = null;	// new MyHttpContext(context.getBundle());
        	webContextPath = context.getProperty(WEB_CONTEXT);
            String fullPath = addURLPath(webContextPath, path);

            if ((BaseServlet.IMAGES.equalsIgnoreCase(path)) 
            	|| (BaseServlet.LIB.equalsIgnoreCase(path))
                || (BaseServlet.DOCS.equalsIgnoreCase(path)))
            {
            	httpService.registerResources(fullPath, path, httpContext);
            }
            if ((BaseServlet.JBUNDLE_RESOURCES.equalsIgnoreCase(path)) 
                || (BaseServlet.TOURAPP_RESOURCES.equalsIgnoreCase(path)))
            {
            	servlet = new OSGiFileServlet();
/*            	try {
            	servlet = new org.apache.catalina.servlets.DefaultServlet();
            	} catch (Exception ex) {
            		System.out.println("------------------------------");
            		ex.printStackTrace();
            		System.out.println("------------------------------");
            	}
            	if (servlet == null)
            		servlet = new org.mortbay.jetty.servlet.DefaultServlet();
//x            	servlet = new org.jbundle.base.screen.control.servlet.html.BaseServlet();
 */
            	httpContext = new org.jbundle.util.webapp.files.FileHttpContext(context.getBundle());
	            httpService.registerServlet(fullPath, servlet, dictionary, httpContext);
            }
            if (BaseServlet.PROXY.equalsIgnoreCase(path))
            {
	            servlet = new org.jbundle.base.remote.proxy.ProxyServlet();
	            dictionary.put("remotehost", "localhost");	// Default value
	            httpService.registerServlet(fullPath, servlet, dictionary, httpContext);
            }
            if ((BaseServlet.TABLE.equalsIgnoreCase(path)) 
            		|| (BaseServlet.IMAGE.equalsIgnoreCase(path))
            		|| (BaseServlet.JNLP.equalsIgnoreCase(path))
            		|| (BaseServlet.WSDL.equalsIgnoreCase(path))
    	    		|| (BaseServlet.TOURAPP_WSDL.equalsIgnoreCase(path))
    	    		|| (BaseServlet.HTML.equalsIgnoreCase(path))
    	    		|| (BaseServlet.HTML2.equalsIgnoreCase(path))
    	    		|| (BaseServlet.TOURAPP_JNLP.equalsIgnoreCase(path))
            		|| (BaseServlet.TOURAPP.equalsIgnoreCase(path)))
            {
            	servlet = new org.jbundle.base.screen.control.servlet.html.HTMLServlet();
                dictionary.put("remotehost", "localhost");	// Default value
            	httpService.registerServlet(fullPath, servlet, dictionary, httpContext);
            }
            if (BaseServlet.XML.equalsIgnoreCase(path))
            {
	            servlet = new org.jbundle.base.screen.control.servlet.xml.XMLServlet();
//x	            dictionary.put("stylesheet-path", "docs/styles/xsl/flat/base/");	// Since stylesheets are in resources
	            dictionary.put("remotehost", "localhost");
	            httpService.registerServlet(fullPath, servlet, dictionary, httpContext);
            }
            if ((BaseServlet.XSL.equalsIgnoreCase(path)) 
            	|| (BaseServlet.XHTML.equalsIgnoreCase(path)))
            {
	            servlet = new org.jbundle.base.screen.control.xslservlet.XSLServlet();
//x	            dictionary.put("stylesheet-path", "docs/styles/xsl/flat/base/");	// Since stylesheets are in resources
	            httpService.registerServlet(fullPath, servlet, dictionary, httpContext);
            }
            if (BaseServlet.JNLP_DOWNLOAD.equalsIgnoreCase(path))
            {
	          servlet = new org.jbundle.util.webapp.jnlpservlet.JnlpServlet();
//	          servlet = new jnlp.sample.servlet.JnlpDownloadServlet();
	          httpContext = new JnlpHttpContext(context.getBundle());
//	          httpService.registerServlet(addURLPath(webContextPath, "*.jnlp"), servlet, dictionary, httpContext);
	          httpService.registerServlet(fullPath, servlet, dictionary, httpContext);
            }
            if (BaseServlet.AJAX.equalsIgnoreCase(path))
            {
            	servlet = new org.jbundle.base.remote.proxy.AjaxServlet();
            	dictionary.put("remotehost", "localhost");
	            dictionary.put("stylesheet-path", "docs/styles/xsl/flat/base/");	// Since webkit still can't handle import
            	httpService.registerServlet(fullPath, servlet, dictionary, httpContext);
            }
            if ((BaseServlet.ROOT.equalsIgnoreCase(path)) 
                	|| (BaseServlet.INDEX.equalsIgnoreCase(path)))
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
	            httpService.registerServlet(fullPath, servlet, dictionary, httpContext);
            }
            if (BaseServlet.MESSAGE.equalsIgnoreCase(path))
            {
	            servlet = new org.jbundle.base.message.trx.transport.html.MessageServlet();
	            dictionary.put("remotehost", "localhost");
	            httpService.registerServlet(fullPath, servlet, dictionary, httpContext);
            }
            if (BaseServlet.WS.equalsIgnoreCase(path))
            {
	            servlet = new org.jbundle.base.message.trx.transport.jaxm.MessageReceivingServlet();
	            dictionary.put("remotehost", "localhost");
	            httpService.registerServlet(fullPath, servlet, dictionary, httpContext);
            }
            if (BaseServlet.XMLWS.equalsIgnoreCase(path))
            {
	            servlet = new org.jbundle.base.message.trx.transport.xml.XMLMessageReceivingServlet();
	            dictionary.put("remotehost", "localhost");
	            httpService.registerServlet(fullPath, servlet, dictionary, httpContext);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Http Service is down, remove my servlets.
     */
    public void removedService(ServiceReference reference, Object service) {
        HttpService httpService = (HttpService) service;
    	for (String path : paths)
    	{
            String fullPath = addURLPath(webContextPath, path);
            httpService.unregister(fullPath);
    	}
        super.removedService(reference, service);
    }
    
    /**
     * Add to http path (**Move this to Util**)
     * @param basePath
     * @param path
     * @return
     */
    public static String addURLPath(String basePath, String path)
    {
    	if (basePath == null)
    		basePath = "";
    	if ((!basePath.endsWith("/")) && (!path.startsWith("/")))
    		path = "/" + path;
    	if (basePath.length() > 0)
    		path = basePath + path;
     	if (path.length() == 0)
    		path = "/";
     	else if ((path.length() > 1) && (path.endsWith("/")))
     		path = path.substring(0, path.length() -1);
    	return path;
    }
}
