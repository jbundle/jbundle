/*
 * RmiSessionServer.java
 *
 * Created on January 10, 2000, 4:47 PM
 
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.util;

import java.util.Map;

import org.jbundle.util.osgi.bundle.BaseBundleService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;

public class EnvironmentActivator extends BaseBundleService
{
	protected Environment environment = null;
	
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
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
            System.out.println("Starting Environment");

            Map<String,Object> props = Utility.propertiesToMap(this.getProperties());
    	    environment = Environment.getEnvironment(props);	// There is no need to check if it is already up (only 1 can run)
    	}
        if (event.getType() == ServiceEvent.UNREGISTERING)
        {
            if (environment != null)
            	environment.free();
            environment = null;
        }        
    }
    
    /**
     * Get the environment.
     * @return
     */
    public Environment getEnvironment()
    {
    	return environment;
    }
}
