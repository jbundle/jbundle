package org.jbundle.thin.base.util.osgi.bundle;

import java.net.URL;
import java.util.Dictionary;
import java.util.Hashtable;

import org.jbundle.thin.base.util.osgi.finder.ClassFinderUtility;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceRegistration;


/**
 * Bundle resource access utilities for a service.
 * Extend this class with your service activator.
 * @author don
 *
 */
public class BaseBundleService extends Object
	implements BundleService, BundleActivator, ServiceListener
{
	/**
	 * This is not necessary. It is nice for debugging.
	 */
	protected Dictionary<String,String> properties = null;
	
    ServiceRegistration serviceRegistration;
	
    /**
     * Bundle starting up.
     */
    public void start(BundleContext context) throws Exception {
        System.out.println("Starting BaseBundleService bundle");
        
        String packageName = this.getProperty(BundleService.PACKAGE_NAME);
        if (packageName == null)
        	this.setProperty(BundleService.PACKAGE_NAME, packageName = ClassFinderUtility.getPackageName(this.getClass().getName(), false));

		String objectClass = this.getProperty(BundleService.INTERFACE);
		if (objectClass == null)
			objectClass = this.getClass().getName();
        //?String type = properties.get(OsgiService.TYPE);

		try {
			context.addServiceListener(this, /*"(&" +*/ "(objectClass=" + objectClass + ")"); // + "(" + BundleService.PACKAGE_NAME + "=" + packageName + "))");
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
		}
		
        serviceRegistration = context.registerService(objectClass, this, properties);
    }    
    /**
     * Bundle stopping.
     */
    public void stop(BundleContext context) throws Exception {
        System.out.println("Stopping BaseBundleService bundle");
//        Automatically unregistered.
    }

    @Override
    public void serviceChanged(ServiceEvent event) {
    	// Override this to monitor event  if (event.getType() == ServiceEvent.REGISTERED)
    }
    
	/**
	 * Get the properties.
	 * @return the properties.
	 */
	public Dictionary<String,String> getProperties()
	{
		return properties;
	}
	/**
	 * Get the properties.
	 * @return the properties.
	 */
	public void setProperty(String key, String value)
	{
		if (properties == null)
			properties = new Hashtable<String, String>();
		properties.put(key, value);
	}
	/**
	 * Get the properties.
	 * @return the properties.
	 */
	public String getProperty(String key)
	{
		if (properties == null)
			return null;
		return properties.get(key);
	}
	/**
	 * Given this class name, create the Class.
	 * @param className The full class name.
	 * @return The class or null if not found.
	 */
	public Class<?> makeClass(String className)
		throws ClassNotFoundException
	{
		return Class.forName(className);
	}
	/**
	 * Get the URL to the resource with this name.
	 * @param name The full resource path.
	 * @return The resource URL (usually bundle:more).
	 */
    public URL getResource(String name)
    {
    	return BaseBundleService.class.getClassLoader().getResource(name);
    }

}
