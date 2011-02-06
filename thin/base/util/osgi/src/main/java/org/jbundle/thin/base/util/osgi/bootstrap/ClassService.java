package org.jbundle.thin.base.util.osgi.bootstrap;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public interface ClassService {
	
    /**
     * Find, resolve, and return this class definition.
     * Static convenience method.
     * @param className
     * @return The class definition or null if not found.
     */
    public Class<?> findClassBundle(String interfaceName, String className);
    /**
     * Find, resolve, and return this resource's URL.
     * Static convenience method.
     * @param className
     * @return The class definition or null if not found.
     */
    public URL findBundleResource(String className);
    /**
     * Find, resolve, and return this ResourceBundle.
     * Static convenience method.
     * @param className
     * @return The class definition or null if not found.
     */
    public ResourceBundle findResourceBundle(String className, Locale locale);

}
