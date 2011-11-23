/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.web.httpservice;

import java.util.Dictionary;
import java.util.Hashtable;

import javax.servlet.Servlet;

import org.jbundle.base.screen.control.servlet.html.BaseServlet;
import org.jbundle.base.util.DBConstants;
import org.jbundle.base.util.EnvironmentActivator;
import org.jbundle.util.osgi.BundleService;
import org.jbundle.util.osgi.finder.ClassServiceUtility;
import org.jbundle.util.webapp.osgi.BaseOsgiServlet;
import org.jbundle.util.webapp.osgi.HttpServiceTracker;
import org.jbundle.util.webapp.osgi.OSGiFileServlet;
import org.jbundle.util.webapp.osgi.ResourceHttpServiceTracker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.service.http.HttpContext;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Start up the web service listener.
 * @author don
 */
public class HttpServiceActivator extends org.jbundle.util.webapp.osgi.HttpServiceActivator
{
    
    private String[] aliases = {
            BaseServlet.IMAGES,
            BaseServlet.LIB,
            BaseServlet.DOCS,
//+         BaseServlet.COM,
//+         BaseServlet.ORG,
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

    @Override
    public void serviceChanged(ServiceEvent event) {
        if (event.getType() == ServiceEvent.REGISTERED)
        { // Osgi Service is up, Okay to start the server
            ClassServiceUtility.log(context, LogService.LOG_INFO, "Starting Http Service tracker");
    		if (httpServiceTracker == null)
    		{
    			BundleContext context = event.getServiceReference().getBundle().getBundleContext();
    	        this.checkDependentServicesAndStartup(context, EnvironmentActivator.class.getName(), null);
    		}
        }
        if (event.getType() == ServiceEvent.UNREGISTERING)
            super.serviceChanged(event);    // httpService.close();
    }
    public String[] getAliases()
    {
        return aliases;
    }
    /**
     * Start this service.
     * Override this to do all the startup.
     * @return true if successful.
     */
    public boolean startupThisService(BundleService bundleService, BundleContext context)
    {
        // Good, Environment activator is up. Time to start up http services trackers
        
        for (String alias : getAliases())
        {
            this.makeServletTracker(alias);
        }

        return true;
    }
    
    /**
     * Start this service.
     * Override this to do all the startup.
     * @return true if successful.
     */
    @Override
    public boolean shutdownThisService(BundleService bundleService, BundleContext context)
    {
        for (String alias : getAliases())
        {
            HttpServiceTracker serviceTracker = this.getServiceTracker(context, alias);
            if (serviceTracker != null)
                serviceTracker.close();
        }
        return true;
    }
    
    public ServiceTracker makeServletTracker(String alias)
    {
        Dictionary<String, String> dictionary = new Hashtable<String, String>();

//?        dictionary.put(HttpServiceTracker.SERVICE_PID, getServicePid(context));
//?        dictionary.put(HttpServiceTracker.SERVLET_CLASS, getServletClass(context));
        dictionary.put(HttpServiceTracker.WEB_ALIAS, alias); 
        
        Servlet servlet = null;
        HttpContext httpContext = null;
        HttpServiceTracker serviceTracker = null; 
        try {
            String servicePid = DBConstants.BLANK;

            if ((BaseServlet.IMAGES.equalsIgnoreCase(alias)) 
                    || (BaseServlet.LIB.equalsIgnoreCase(alias))
    //+             || (BaseServlet.COM.equalsIgnoreCase(path))
    //+             || (BaseServlet.ORG.equalsIgnoreCase(path))
                    || (BaseServlet.DOCS.equalsIgnoreCase(alias)))
            {
                serviceTracker = new ResourceHttpServiceTracker(context, httpContext, dictionary);
            }
            if ((BaseServlet.JBUNDLE_RESOURCES.equalsIgnoreCase(alias)) 
                || (BaseServlet.TOURAPP_RESOURCES.equalsIgnoreCase(alias)))
            {
                servlet = new OSGiFileServlet();
                dictionary.put(OSGiFileServlet.BASE_PATH, alias.substring(1) + '/');    // Prepend this to the path
                ((BaseOsgiServlet)servlet).init(context, servicePid, dictionary);
                httpContext = new org.jbundle.util.webapp.files.FileHttpContext(context.getBundle());
            }
            if (BaseServlet.PROXY.equalsIgnoreCase(alias))
            {
                servlet = new org.jbundle.base.remote.proxy.ProxyServlet();
                dictionary.put("remotehost", "localhost");  // Default value
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
                dictionary.put("remotehost", "localhost");  // Default value
            }
            if (BaseServlet.XML.equalsIgnoreCase(alias))
            {
                servlet = new org.jbundle.base.screen.control.servlet.xml.XMLServlet();
//x             dictionary.put("stylesheet-path", "docs/styles/xsl/flat/base/");    // Since stylesheets are in resources
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
//            servlet = new jnlp.sample.servlet.JnlpDownloadServlet();
              httpContext = new JnlpHttpContext(context.getBundle());
            }
            if (BaseServlet.AJAX.equalsIgnoreCase(alias))
            {
                servlet = new org.jbundle.base.remote.proxy.AjaxServlet();
                dictionary.put("remotehost", "localhost");
                dictionary.put("stylesheet-path", "docs/styles/xsl/flat/base/");    // Since webkit still can't handle import
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

        if (httpContext == null)   
            httpContext = this.getHttpContext();
        if (serviceTracker == null)
            serviceTracker = this.createServiceTracker(context, httpContext, dictionary);
        serviceTracker.setServlet(servlet);
        serviceTracker.open();
        context.registerService(ServiceTracker.class.getName(), serviceTracker, dictionary);    // Why isn't this done automatically?
        return serviceTracker;
    }
    
}
