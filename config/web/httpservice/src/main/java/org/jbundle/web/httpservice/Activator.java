package org.jbundle.web.httpservice;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Start up the web service listener.
 * @author don
 */
public class Activator implements BundleActivator {
    ServiceTracker httpServiceTracker;
    
    /**
     * Start it up.
     */
    public void start(BundleContext context) throws Exception {
        System.out.println("Starting http service tracker");
        httpServiceTracker = new HttpServiceTracker(context);
        httpServiceTracker.open();
    }

    /**
     * Shut it down.
     */
    public void stop(BundleContext context) throws Exception {
        System.out.println("Stopping http service tracker");
        httpServiceTracker.close();
        httpServiceTracker = null;
    }
}