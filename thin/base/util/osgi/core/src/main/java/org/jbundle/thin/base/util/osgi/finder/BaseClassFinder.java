package org.jbundle.thin.base.util.osgi.finder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.jbundle.thin.base.util.osgi.bundle.BundleService;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

/**
 * OsgiClassService - Service to find and load bundle classes and resources.
 * 
 * @author don
 * 
 */
public abstract class BaseClassFinder extends Object
	implements BundleActivator, ClassFinder
{
	/**
	 * Good from start to stop.
	 */
    protected BundleContext bundleContext = null;

    /**
     * Service to find resources by class name.
     * Singleton.
     */
    protected BaseClassFinder()
    {
        super();
    }
    
    /**
     * Bundle starting.
     * If the service listener is up, register me, else wait.
     */
    public void start(BundleContext context) throws Exception
    {
        System.out.println("Starting and registering the (repository) ClassService");
        
        bundleContext = context;

        context.registerService(ClassFinder.class.getName(), this, null);	// Should be only one of these
    }
    /**
     * Bundle shutting down.
     */
    public void stop(BundleContext context) throws Exception {
        System.out.println("Stopping ClassService bundle");
        // I'm unregistered automatically

        bundleContext = null;
    }
    /**
     * Find, resolve, and return this class definition.
     * @param className
     * @return The class definition or null if not found.
     */
    public Class<?> findClassBundle(String interfaceName, String className)
    {
        //if (ClassServiceBootstrap.repositoryAdmin == null)
        //    return null;

        Class<?> c = this.getClassFromBundle(null, className);

        if (c == null) {
            Object resource = this.deployThisResource(className, true, false);
            if (resource != null)
            {
            	c = this.getClassFromBundle(null, className);	// It is possible that the newly started bundle registered itself
            	if (c == null)
            		c = this.getClassFromBundle(resource, className);
            }
        }

        return c;
    }
    /**
     * Find, resolve, and return this resource's URL.
     * @param className
     * @return The class definition or null if not found.
     */
    public URL findBundleResource(String className)
    {
        //if (ClassServiceBootstrap.repositoryAdmin == null)
        //    return null;

        URL url = this.getResourceFromBundle(null, className);

        if (url == null) {
            Object resource = this.deployThisResource(className, true, true);
            if (resource != null)
            	url = this.getResourceFromBundle(resource, className);
        }

        return url;
    }
    /**
     * Find, resolve, and return this ResourceBundle.
     * @param className
     * @return The class definition or null if not found.
     * TODO: Need to figure out how to get the bundle's class loader, so I can set up the resource chain
     */
    public ResourceBundle findResourceBundle(String className, Locale locale)
    {
        //if (ClassServiceBootstrap.repositoryAdmin == null)
        //    return null;

        ResourceBundle resourceBundle = this.getResourceBundleFromBundle(null, className, locale);

        if (resourceBundle == null) {
            Object resource = this.deployThisResource(className, true, true);
            if (resource != null)
            {
            	resourceBundle = this.getResourceBundleFromBundle(resource, className, locale);
            	if (resourceBundle == null)
            	{
            		Class<?> c = this.getClassFromBundle(resource, className);
            		if (c != null)
            		{
					   try {
						   resourceBundle = (ResourceBundle)c.newInstance();
					   } catch (InstantiationException e)   {
					       e.printStackTrace();	// Never
					   } catch (IllegalAccessException e)   {
					       e.printStackTrace();	// Never
					   } catch (Exception e) {
					       e.printStackTrace();	// Never
					   }            			
            		}
            	}
            }
        }

        return resourceBundle;
    }
    /**
     * Find this class's class access registered class access service in the current workspace.
     * @param className
     * @return
     */
    private BundleService getClassAccessService(String className)
    {
        try {
        	String packageName = ClassFinderUtility.getPackageName(className, true);
            String filter = "(" + BundleService.PACKAGE_NAME + "=" + packageName + ")";
            ServiceReference[] refs = bundleContext.getServiceReferences(BundleService.class.getName(), filter);

            if ((refs != null) && (refs.length > 0))
                return (BundleService)bundleContext.getService(refs[0]);
        } catch (InvalidSyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * Find this class's bundle in the repository
     * @param className
     * @return
     */
    private Class<?> getClassFromBundle(Object resource, String className)
    {
        Class<?> c = null;
        try {
            if (resource == null)
            {
                BundleService classAccess = this.getClassAccessService(className);
                if (classAccess != null)
                	c = classAccess.makeClass(className);
            }
            else
            {
            	Bundle bundle = this.getBundleFromResource(resource, bundleContext, ClassFinderUtility.getPackageName(className, false));
	            c = bundle.loadClass(className);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return c;
    }
    /**
     * makeClassFromBundle
     * 
     * @param className
     * @return
     */
    private URL getResourceFromBundle(Object resource, String className)
    {
        URL url = null;
        if (resource == null)
        {
            BundleService classAccess = this.getClassAccessService(className);
            if (classAccess != null)
                url = classAccess.getResource(className);
        }
        else
        {
        	Bundle bundle = this.getBundleFromResource(resource, bundleContext, ClassFinderUtility.getPackageName(className, true));
            url = bundle.getEntry(className);
        }
        return url;
    }
    /**
     * makeClassFromBundle
     * 
     * @param className
     * @return
     */
    boolean USE_NO_RESOURCE_HACK = true; // TODO - There must be a way to get the class loader????
    private ResourceBundle getResourceBundleFromBundle(Object resource, String baseName, Locale locale)
    {
    	ResourceBundle resourceBundle = null;
        if (resource == null)
        {
            BundleService classAccess = this.getClassAccessService(baseName);
            if (classAccess != null)
            {
                if (USE_NO_RESOURCE_HACK)
                {
	                try {
						URL url = classAccess.getResource(baseName);
						InputStream stream = url.openStream();
						resourceBundle = new PropertyResourceBundle(stream);
					} catch (IOException e) {
						e.printStackTrace();
					}
                }
                else
                {
                    ClassLoader loader = classAccess.getClass().getClassLoader();
                	resourceBundle = ResourceBundle.getBundle(baseName, locale, loader);
                }
            }
        }
        else
        {
        	Bundle bundle = this.getBundleFromResource(resource, bundleContext, ClassFinderUtility.getPackageName(baseName, true));
            if (USE_NO_RESOURCE_HACK)
            {
                try {
                	// TODO - If I have to do this, then I will have to link up the resourcebundle using the locales.
                	baseName = baseName.replace('.', File.separatorChar) + ClassFinderUtility.PROPERTIES;
					URL url = bundle.getEntry(baseName);
					if (url != null)
						resourceBundle = new PropertyResourceBundle(url.openStream());
				} catch (IOException e) {
					e.printStackTrace();
				}
            }
            else
            {
            	ClassLoader loader = bundle.getClass().getClassLoader();
            	resourceBundle = ResourceBundle.getBundle(baseName, locale, loader);
            }
        }
        return resourceBundle;
    }
    /**
     * Find this resource in the repository, then deploy and optionally start it.
     * @param className
     * @param options 
     * @return
     */
    public abstract Object deployThisResource(String className, boolean start, boolean resourceType);
    /**
     * Find the currently installed bundle that exports this package.
     * NOTE: This is stupid, there has to be a way to do this.
     * @param resource
     * @param context
     * @return
     */
    public abstract Bundle getBundleFromResource(Object resource, BundleContext context, String packageName);

}
