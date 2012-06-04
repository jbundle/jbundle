/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.config.web.httpservice;

import java.util.Dictionary;

import javax.servlet.Servlet;

import org.jbundle.base.model.DBConstants;
import org.jbundle.base.screen.control.servlet.html.BaseServlet;
import org.jbundle.base.util.Environment;
import org.jbundle.base.util.EnvironmentActivator;
import org.jbundle.model.Env;
import org.jbundle.util.webapp.base.BaseOsgiServlet;
import org.jbundle.util.webapp.base.BaseWebappServlet;
import org.jbundle.util.webapp.base.HttpServiceTracker;
import org.jbundle.util.webapp.base.MultipleHttpServiceActivator;
import org.jbundle.util.webapp.base.ResourceHttpServiceTracker;
import org.osgi.framework.BundleContext;
import org.osgi.service.http.HttpContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Start up the web service listener.
 * @author don
 */
public class HttpServiceActivator extends MultipleHttpServiceActivator
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

    /**
     * Make sure the dependent services are up, then call startupService.
     * @param versionRange Bundle version
     * @param baseBundleServiceClassName
     * @return false if I'm waiting for the service to startup.
     */
    public boolean checkDependentServices(BundleContext context)
    {
    	boolean success = this.addDependentService(context, Env.class.getName(), Environment.class.getName(), null, null);
    	success = success & super.checkDependentServices(context);
    	return success;
    }

    /**
     * Get all the web aliases to add http services for.
     *@return A list of the web services.
     */
    public String[] getAliases()
    {
        return aliases;
    }
    
    /**
     * Make a servlet tracker for the servlet at this alias.
     */
    public ServiceTracker makeServletTracker(String alias, Dictionary<String, String> properties)
    {

//?        dictionary.put(HttpServiceTracker.SERVICE_PID, getServicePid(context));
//?        dictionary.put(HttpServiceTracker.SERVLET_CLASS, getServletClass(context));
        properties.put(BaseWebappServlet.ALIAS, alias); 
        
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
                serviceTracker = new ResourceHttpServiceTracker(context, httpContext, properties);
            }
            if ((BaseServlet.JBUNDLE_RESOURCES.equalsIgnoreCase(alias)) 
                || (BaseServlet.TOURAPP_RESOURCES.equalsIgnoreCase(alias)))
            {
                servlet = new BaseOsgiServlet();
                properties.put(BaseOsgiServlet.BASE_PATH, alias + '/');    // Prepend this to the path
                ((BaseWebappServlet)servlet).init(context, servicePid, properties);
                httpContext = new org.jbundle.util.webapp.files.FileHttpContext(context.getBundle());
            }
            if (BaseServlet.PROXY.equalsIgnoreCase(alias))
            {
                servlet = new org.jbundle.base.remote.proxy.ProxyServlet();
                properties.put("remotehost", "localhost");  // Default value
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
                properties.put("remotehost", "localhost");  // Default value
            }
            if (BaseServlet.XML.equalsIgnoreCase(alias))
            {
                servlet = new org.jbundle.base.screen.control.servlet.xml.XMLServlet();
//x             dictionary.put("stylesheet-path", "docs/styles/xsl/flat/base/");    // Since stylesheets are in resources
                properties.put("remotehost", "localhost");
            }
            if ((BaseServlet.XSL.equalsIgnoreCase(alias)) 
                || (BaseServlet.XHTML.equalsIgnoreCase(alias)))
            {
                servlet = new org.jbundle.base.screen.control.xslservlet.XSLServlet();
                properties.put("remotehost", "localhost");
            }
            if (BaseServlet.JNLP_DOWNLOAD.equalsIgnoreCase(alias))
            {
              servlet = new jnlp.sample.servlet.JnlpDownloadServlet();
              httpContext = new JnlpHttpContext(context.getBundle());
            }
            if (BaseServlet.AJAX.equalsIgnoreCase(alias))
            {
                servlet = new org.jbundle.base.remote.proxy.AjaxServlet();
                properties.put("remotehost", "localhost");
                properties.put("stylesheet-path", "docs/styles/xsl/flat/base/");    // Since webkit still can't handle import
            }
            if ((BaseServlet.ROOT.equalsIgnoreCase(alias)) 
                    || (BaseServlet.INDEX.equalsIgnoreCase(alias)))
            {
                // This redirects a www request to /demo/index.html
                properties.put("regex", "www.+.tourgeek.com");
                properties.put("regexTarget", "demo/index.html");
                // This redirects non-matching requests to the proper servlet
                properties.put(BaseWebappServlet.IE, BaseServlet.XSL);
                properties.put(BaseWebappServlet.FIREFOX, BaseServlet.XSL);
                //properties.put("chrome", "tourappxsl");
                //properties.put("safari", "tourappxsl");
                properties.put(BaseWebappServlet.WEBKIT, BaseServlet.XSL);
                properties.put(BaseWebappServlet.MOBILE, BaseServlet.XHTML);
                properties.put(BaseWebappServlet.JAVA, BaseServlet.XHTML);
                servlet = new org.jbundle.util.webapp.redirect.RegexRedirectServlet();
            }
            if (BaseServlet.MESSAGE.equalsIgnoreCase(alias))
            {
                servlet = new org.jbundle.base.screen.control.servlet.message.trx.transport.html.MessageServlet();
                properties.put("remotehost", "localhost");
            }
            if (BaseServlet.WS.equalsIgnoreCase(alias))
            {
                servlet = new org.jbundle.base.message.trx.transport.jaxm.MessageReceivingServlet();
                properties.put("remotehost", "localhost");
            }
            if (BaseServlet.XMLWS.equalsIgnoreCase(alias))
            {
                servlet = new org.jbundle.base.screen.control.servlet.message.trx.transport.xml.XMLMessageReceivingServlet();
                properties.put("remotehost", "localhost");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (httpContext == null)   
            httpContext = this.getHttpContext();
        if (serviceTracker == null)
            serviceTracker = this.createServiceTracker(context, httpContext, properties);
        serviceTracker.setServlet(servlet);
        return serviceTracker;
    }
    
}
