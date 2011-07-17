package org.jbundle.web.httpservice;

import org.jbundle.base.util.EnvironmentActivator;
import org.jbundle.util.osgi.BundleService;
import org.jbundle.util.osgi.bundle.BaseBundleService;
import org.jbundle.util.osgi.finder.ClassServiceUtility;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.service.log.LogService;

/**
 * Start up the web service listener.
 * @author don
 */
public class HttpServiceActivator extends BaseBundleService
{
    ServiceTracker httpServiceTracker;
    
    @Override
    public void serviceChanged(ServiceEvent event) {
        if (event.getType() == ServiceEvent.REGISTERED)
        { // Osgi Service is up, Okay to start the server
            ClassServiceUtility.log(context, LogService.LOG_INFO, "Starting Http Service tracker");
    		if (httpServiceTracker == null)
    		{
    			BundleContext context = event.getServiceReference().getBundle().getBundleContext();
    	        this.checkDependentServicesAndStartup(context, EnvironmentActivator.class.getName());
    		}
        }
        if (event.getType() == ServiceEvent.UNREGISTERING)
        {
            ClassServiceUtility.log(context, LogService.LOG_INFO, "Stopping http service tracker");
            httpServiceTracker.close();
            httpServiceTracker = null;
        }        
    }
    /**
     * Start this service.
     * Override this to do all the startup.
     * @return true if successful.
     */
    public boolean startupThisService(BundleService bundleService, BundleContext context)
    {
        //EnvironmentActivator environmentActivator = (EnvironmentActivator)bundleService;
        //Environment env = environmentActivator.getEnvironment();

        httpServiceTracker = new HttpServiceTracker(context);
        httpServiceTracker.open();
        
        return true;
    }
}