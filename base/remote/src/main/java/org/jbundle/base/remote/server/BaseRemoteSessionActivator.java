/*
 * RmiSessionServer.java
 *
 * Created on January 10, 2000, 4:47 PM
 
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.remote.server;

import java.util.Map;

import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.DBParams;
import org.jbundle.base.util.BaseThickActivator;
import org.jbundle.base.util.Environment;
import org.jbundle.model.Env;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.Params;
import org.jbundle.thin.base.message.MessageConstants;
import org.jbundle.thin.base.remote.ApplicationServer;
import org.jbundle.thin.base.remote.RemoteException;
import org.jbundle.thin.base.remote.RemoteTask;
import org.osgi.framework.BundleContext;


public class BaseRemoteSessionActivator extends BaseThickActivator
	implements ApplicationServer
{
	/**
	 * Setup the application properties.
	 * Override this to set the properties.
	 */
	public void init()
	{
        super.init();
	}
	/**
	 * Get the activator properties to pass to the service.
	 * @return The properties in String,Object format
	 */
    public Map<String,Object> getServiceProperties()
    {
        Map<String,Object> properties = super.getServiceProperties();
        properties = addConfigProperty(properties, DBParams.LOCAL, DBParams.JDBC); // Remote Server ALWAYS uses Jdbc
        properties = addConfigProperty(properties, DBParams.REMOTE, DBParams.JDBC);
        properties = addConfigProperty(properties, DBParams.TABLE, DBParams.JDBC);
        properties = addConfigProperty(properties, Params.APP_NAME, Params.DEFAULT_REMOTE_APP);
        properties = addConfigProperty(properties, Params.JMSSERVER, DBConstants.TRUE);
        properties = addConfigProperty(properties, DBParams.FREEIFDONE, Constants.FALSE);   // Don't free when only the last app is running.
        properties = addConfigProperty(properties, MessageConstants.MESSAGE_FILTER, MessageConstants.TREE_FILTER);  // Default for a server
        return properties;
    }
    /**
     * Make sure the dependent services are up, then call startupService.
     * @param versionRange Bundle version
     * @param baseBundleServiceClassName
     * @return false if I'm waiting for the service to startup.
     */
    public boolean checkDependentServices(BundleContext bundleContext)
    {	// Note: I pass my properties up to the environment
    	boolean success = this.addDependentService(context, Env.class.getName(), Environment.class.getName(), null, this.getProperties());
    	success = success & super.checkDependentServices(bundleContext);
    	return success;
    }
    /**
     * Start this service.
     * Override this to do all the startup.
     * @return true if successful.
     */
    public Object startupService(BundleContext bundleContext)
    {
    	return null;	// Override this
    }
    /**
     * 
     */
	@Override
	public RemoteTask createRemoteTask(Map<String, Object> properties)
			throws RemoteException {
		if (service == null)
			return null;
		return ((RemoteSessionServer)service).createRemoteTask(properties);
	}
    /**
     * Get the environment.
     * @return
     */
    public RemoteSessionServer getRemoteSessionServer()
    {
        return (RemoteSessionServer)service;
    }
}
