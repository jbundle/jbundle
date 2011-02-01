package org.jbundle.base.db.jdbc.service;

import org.jbundle.thin.base.util.osgi.ClassAccess;

/**
 * Create a class in this bundle.
 * This object is created by the ogsi bundle start Activator.
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

}
