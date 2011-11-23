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
public class MultipleHttpServiceActivator extends org.jbundle.util.webapp.osgi.HttpServiceActivator
{
    
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
    /**
     * Get all the web aliases to add http services for.
     *@return A list of the web services.
     */
    public String[] getAliases()
    {
        return EMPTY_ARRAY; // Override this!
    }
    private static final String[] EMPTY_ARRAY = new String[0];
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
            Dictionary<String, String> properties = new Hashtable<String, String>();
            ServiceTracker serviceTracker = this.makeServletTracker(alias, properties);
            serviceTracker.open();
            context.registerService(ServiceTracker.class.getName(), serviceTracker, properties);    // Why isn't this done automatically?
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
            HttpServiceTracker serviceTracker = getServiceTracker(context, HttpServiceTracker.WEB_ALIAS, alias);
            if (serviceTracker != null)
                serviceTracker.close();
        }
        return true;
    }
    
    /**
     * Make a servlet tracker for the servlet at this alias.
     */
    public ServiceTracker makeServletTracker(String alias, Dictionary<String, String> properties)
    {
        return null;    // Override this
    }
    
}
