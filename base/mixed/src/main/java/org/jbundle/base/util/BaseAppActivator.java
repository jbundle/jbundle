package org.jbundle.base.util;

import java.util.Map;

import org.jbundle.thin.base.util.Application;
import org.jbundle.util.osgi.BundleService;
import org.jbundle.util.osgi.bundle.BaseBundleService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;

public class BaseAppActivator extends BaseBundleService
{
	
	protected Application application = null;
	
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
    		if (application == null)
    		{
    			BundleContext context = event.getServiceReference().getBundle().getBundleContext();
    	        this.checkDependentServicesAndStartup(context, EnvironmentActivator.class.getName());
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
    public boolean startupThisService(BundleService bundleService)
    {
        System.out.println("Starting Message Server");

        Map<String,Object> props = Utility.propertiesToMap(this.getProperties());

        EnvironmentActivator environmentActivator = (EnvironmentActivator)bundleService;
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
