package org.jbundle.thin.base.util.osgi.service;

import java.net.URL;
import java.util.Dictionary;

import org.jbundle.thin.base.util.osgi.bootstrap.ClassAccess;

/**
 * Create a class in this bundle.
 * This object is created by the ogsi bundle start Activator.
 * Note: This should remain visible to all, since it is the default
 * implementation for all class creation bundles.
 * NOTE: Do NOT use the local members as this class is typically instantiated via reflection,
 * so the local members are null.
 * @author don
 *
 */
public class ClassAccessImpl extends Object
	implements ClassAccess
{
	/**
	 * This is not necessary. It is nice for debugging.
	 */
	private Dictionary<String,String> properties = null; 
	
	/**
	 * Default constructor.
	 */
	public ClassAccessImpl()
	{
		super();
	}
	/**
	 * Default constructor.
	 */
	public ClassAccessImpl(Dictionary<String,String> properties)
	{
		this();
		this.init(properties);
	}
	/**
	 * Default constructor.
	 */
	public void init(Dictionary<String,String> properties)
	{
		this.properties = properties;
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
    	return ClassAccessImpl.class.getClassLoader().getResource(name);
    }

}
