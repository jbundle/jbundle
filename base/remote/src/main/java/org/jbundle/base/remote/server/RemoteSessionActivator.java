/*
 * RmiSessionServer.java
 *
 * Created on January 10, 2000, 4:47 PM
 
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.remote.server;

import org.jbundle.base.util.BaseApplication;
import org.jbundle.base.util.Environment;
import org.jbundle.base.util.EnvironmentActivator;
import org.jbundle.base.util.MainApplication;
import org.jbundle.base.util.Utility;
import org.jbundle.util.osgi.finder.ClassServiceUtility;
import org.osgi.framework.BundleContext;


public class RemoteSessionActivator extends BaseRemoteSessionActivator
{
    /**
     * Start this service.
     * Override this to do all the startup.
     * @return true if successful.
     */
    public boolean startupThisService(BundleContext bundleContext)
    {
        boolean success = super.startupThisService(bundleContext);

        EnvironmentActivator environmentActivator = (EnvironmentActivator)ClassServiceUtility.getClassService().getClassFinder(bundleContext).getClassBundleService(EnvironmentActivator.class.getName(), null, null, -1);
        Environment env = environmentActivator.getEnvironment();
        // Note the order that I do this... this is because MainApplication may need access to the remoteapp during initialization
        BaseApplication app = new MainApplication();
        server.init(app, null, null);
        app.init(env, Utility.propertiesToMap(this.getProperties()), null); // Default application (with params).
//        app.setProperty(DBParams.JMSSERVER, DBConstants.TRUE);
//        app.getMessageManager(true);
        return success;
    }

	/**
     * Setup the application properties.
	 */
	public void setupProperties()
	{
	    super.setupProperties();
	}
}
