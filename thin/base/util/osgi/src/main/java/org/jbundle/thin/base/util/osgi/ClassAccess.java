package org.jbundle.thin.base.util.osgi;

public interface ClassAccess {
	
    public static final String CLASS_NAME = "className";
    public static final String INTERFACE = "interface";
    public static final String TYPE = "type";

    public Class<?> makeClass(String className)
		throws ClassNotFoundException;

}
