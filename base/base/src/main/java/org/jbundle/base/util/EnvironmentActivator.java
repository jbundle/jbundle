/*
 * RmiSessionServer.java
 *
 * Created on January 10, 2000, 4:47 PM
 
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.util;

import java.util.Map;

import org.jbundle.base.model.Utility;
import org.jbundle.model.Env;
import org.osgi.framework.BundleContext;

public class EnvironmentActivator extends BaseThickActivator
{
    /**
     * Start this service.
     * Override this to do all the startup.
     * @param context bundle context
     * @return true if successful.
     */
    public Object startupService(BundleContext bundleContext)
    {
        Map<String,Object> props = this.getServiceProperties();
	    return Environment.getEnvironment(props);	// There is no need to check if it is already up (only 1 can run)
    }
    /**
     * Stop this service.
     * Override this to do all the startup.
     * @param bundleService
     * @param context bundle context
     * @return true if successful.
     */
    public boolean shutdownService(Object service, BundleContext context)
    {
    	getEnvironment().free();
    	return true;
    }

    /**
     * Get the environment.
     * @return
     */
    public Environment getEnvironment()
    {
    	return (Environment)service;
    }
    /**
     * Get the interface/service class name.
     * @return
     */
    public Class<?> getInterfaceClass()
    {
		return Env.class;
    }
}
