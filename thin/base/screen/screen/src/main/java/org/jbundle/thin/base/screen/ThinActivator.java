/*
 * RmiSessionServer.java
 *
 * Created on January 10, 2000, 4:47 PM
 
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.screen;

import java.util.Hashtable;
import java.util.Map;

import org.jbundle.model.screen.BaseAppletReference;
import org.jbundle.model.util.Util;
import org.jbundle.thin.base.util.Application;
import org.jbundle.util.osgi.bundle.BaseBundleActivator;
import org.osgi.framework.BundleContext;

public class ThinActivator extends BaseBundleActivator
{
	
    String[] args = {
    		"menu=Thin",
//x    		"background=backgrounds/worldmapalpha.gif",
//x    		"backgroundcolor=#EEEEFF",
//x    		"mainSharedDBName=main_base",
//x    		"local=Jdbc",
//x    		"remote=Jdbc",
//x    		"table=Jdbc",
    		"connectionType=proxy",
    		"codebase=localhost:8181/",
    		"server=localhost",
    		};	// TODO(don) Fix this

	/**
	 * Setup the application properties.
	 * Override this to set the properties.
	 * @param bundleContext BundleContext
	 */
	public void init()
	{
		super.init();
	}

    /**
     * Start this service.
     * Override this to do all the startup.
     * @param context bundle context
     * @return true if successful.
     */
    public Object startupService(BundleContext bundleContext)
    {
        Map<String,Object> propertiesTemp = new Hashtable<String,Object>();
        Util.parseArgs(propertiesTemp, args);

        //?server = new SApplet(args);
        ThinApplet.main(args);
        return Application.getRootApplet();
        //?Environment env = new Environment(propertiesTemp);
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
    	((BaseAppletReference)service).free();
    	return true;
    }

}
