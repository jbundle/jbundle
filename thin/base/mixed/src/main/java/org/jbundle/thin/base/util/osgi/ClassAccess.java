package org.jbundle.thin.base.util.osgi;


public interface ClassAccess {
	
	public Class<?> makeClass(String className)
		throws ClassNotFoundException;

}
