/*
 * RmiSessionServer.java
 *
 * Created on January 10, 2000, 4:47 PM
 
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.util;

import java.util.HashMap;
import java.util.Map;

import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.DBParams;
import org.jbundle.base.model.Utility;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.Params;
import org.jbundle.thin.base.message.MessageConstants;
import org.jbundle.util.osgi.bundle.BaseBundleActivator;

public class BaseThickActivator extends BaseBundleActivator
{
	/**
	 * Constructor.
	 */
	public BaseThickActivator()
	{
		super();
	}

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
	 * Get the activator properties to pass to the service.
	 * @return The properties in String,Object format
	 */
    public Map<String,Object> getServiceProperties()
    {
        Map<String,Object> properties = Utility.propertiesToMap(this.getProperties());
        if (properties == null)
        	properties = new HashMap<String,Object>();
        properties = addConfigProperty(properties, DBParams.LOCAL, null); // Remote Server ALWAYS uses Jdbc
        properties = addConfigProperty(properties, DBParams.REMOTE, null);
        properties = addConfigProperty(properties, DBParams.TABLE, null);
        return properties;
    }
    /**
     * Add this property if it exists in the OSGi config file.
     * @param properties
     * @param key
     * @param defaultValue
     * @return
     */
    public Map<String,Object> addConfigProperty(Map<String,Object> properties, String key, String defaultValue)
    {
    	if (this.getProperty(key) != null)
    		properties.put(key, this.getProperty(key));
    	else if (defaultValue != null)
    		properties.put(key, defaultValue);
    	return properties;
    }

}
