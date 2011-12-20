/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.main.schedule.app;

import java.util.Map;

import org.jbundle.base.util.DBParams;
import org.jbundle.base.util.Environment;
import org.jbundle.base.util.EnvironmentActivator;
import org.jbundle.base.util.Utility;
import org.jbundle.thin.base.util.Application;
import org.jbundle.util.osgi.bundle.BaseBundleService;
import org.jbundle.util.osgi.finder.BaseClassFinderService;
import org.jbundle.util.osgi.finder.ClassServiceUtility;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;

/**
 * 
 * @author don
 *
 */
public class BaseAppActivator extends BaseBundleService
{
	
	protected Application application = null;
	
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		
		if (this.getProperty(DBParams.LOCAL) == null)
			this.setProperty(DBParams.LOCAL, DBParams.JDBC); // OSGi Server apps usually use Jdbc
		if (this.getProperty(DBParams.REMOTE) == null)
			this.setProperty(DBParams.REMOTE, DBParams.JDBC);
		if (this.getProperty(DBParams.TABLE) == null)
			this.setProperty(DBParams.TABLE, DBParams.JDBC);

		super.start(context);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
	}

    @Override
    public void serviceChanged(ServiceEvent event) {
        if (event.getType() == ServiceEvent.REGISTERED)
        { // Osgi Service is up, Okay to start the server
    		if (application == null)
    		{
    			BundleContext context = event.getServiceReference().getBundle().getBundleContext();
    	        this.checkDependentServicesAndStartup(context, EnvironmentActivator.class.getName(), null);
    		}
        }
        if (event.getType() == ServiceEvent.UNREGISTERING)
        {
            if (application != null)
            	application.free();
            application = null;
        }        
    }
    /**
     * Start this service.
     * Override this to do all the startup.
     * @return true if successful.
     */
    public boolean startupThisService(BundleContext bundleContext)
    {
//    	Utility.getLogger().info("Starting App Server");

        Map<String,Object> props = Utility.propertiesToMap(this.getProperties());

        EnvironmentActivator environmentActivator = (EnvironmentActivator)ClassServiceUtility.getClassService().getClassFinder(bundleContext).getClassBundleService(EnvironmentActivator.class.getName(), null, null, -1);
        Environment env = environmentActivator.getEnvironment();
	    
	        // Note the order that I do this... this is because MainApplication may need access to the remoteapp during initialization
        application = this.startupThisApp(env, props);

    	return true;
    }
    /**
     * Start this service.
     * Override this to do all the startup.
     * @return true if successful.
     */
    public Application startupThisApp(Environment env, Map<String, Object> props)
    {
        return null;	// Override this new MessageInfoApplication(env, props, null);
    }
}
