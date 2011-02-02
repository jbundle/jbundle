package org.jbundle.thin.base.util.osgi.impl;

import java.net.URL;

import org.jbundle.thin.base.util.osgi.ClassAccess;

/**
 * Create a class in this bundle.
 * This object is created by the ogsi bundle start Activator.
 * Note: This should remain visible to all, since it is the default
 * implementation for all class creation bundles.
 * @author don
 *
 */
public class ClassAccessImpl extends Object
	implements ClassAccess
{
	
	public Class<?> makeClass(String className)
		throws ClassNotFoundException
	{
		Class<?> c = Class.forName(className);
		return c;
	}

    public URL getResource(String name)
    {
    	return ClassAccessImpl.class.getClassLoader().getResource(name);
    }

}
