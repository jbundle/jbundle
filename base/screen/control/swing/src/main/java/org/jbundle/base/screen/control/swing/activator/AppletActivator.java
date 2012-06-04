/*
 * RmiSessionServer.java
 *
 * Created on January 10, 2000, 4:47 PM
 
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.control.swing.activator;

import java.util.Map;

import org.jbundle.base.screen.control.swing.SApplet;
import org.jbundle.base.util.BaseThickActivator;
import org.jbundle.model.Task;
import org.jbundle.model.util.Util;
import org.jbundle.thin.base.util.Application;
import org.osgi.framework.BundleContext;

public class AppletActivator extends BaseThickActivator
{
    String[] args = {
//x    		"menu=dev.tourapp.com",
//x    		"background=backgrounds/worldmapalpha.gif",
//x    		"backgroundcolor=#EEEEFF",
//x    		"mainSharedDBName=main_base",
//x    		"local=Jdbc",
//x    		"remote=Jdbc",
//x    		"table=Jdbc",
    		};	// TODO(don) Fix this

    /**
     * Start this service.
     * Override this to do all the startup.
     * @param context bundle context
     * @return true if successful.
     */
    public Object startupService(BundleContext bundleContext)
    {
	        Map<String,Object> propertiesTemp = this.getServiceProperties();
	        Util.parseArgs(propertiesTemp, args);

	        //?server = new SApplet(args);
	        SApplet.main(args);
	        return Application.getRootApplet();

//?    	        Environment env = new Environment(propertiesTemp);
	        // Note the order that I do this... this is because MainApplication may need access to the remoteapp during initialization
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
    	if (((Task)(service)).getApplication() != null)
    		((Task)(service)).getApplication().free();
    	else
    		((Task)(service)).free();
    	return true;
    }
}
