package org.jbundle.thin.base.util.osgi.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.felix.bundlerepository.RepositoryAdmin;
import org.apache.felix.bundlerepository.Resolver;
import org.apache.felix.bundlerepository.Resource;
import org.jbundle.thin.base.util.osgi.bootstrap.ClassAccess;
import org.jbundle.thin.base.util.osgi.bootstrap.ClassService;
import org.jbundle.thin.base.util.osgi.bootstrap.ClassServiceBootstrap;
import org.jbundle.thin.base.util.osgi.bootstrap.RepositoryAdminServiceListener;
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
public class ClassServiceImpl implements BundleActivator, ClassService
{
	/**
	 * Good from start to stop.
	 */
    private BundleContext bundleContext = null;

    /**
     * Service to find resources by class name.
     */
    public ClassServiceImpl()
    {
        super();
    }
    
    /**
     * Bundle starting.
     * If the service listener is up, register me, else wait.
     */
    public void start(BundleContext context) throws Exception
    {
        System.out.println("Starting ObrUtil bundle");
        
        bundleContext = context;
    }
    /**
     * Bundle shutting down.
     */
    public void stop(BundleContext context) throws Exception {
        System.out.println("Stopping ObrUtil bundle");
        // I'm unregistered automatically

        bundleContext = null;
    }
    /**
     * Find, resolve, and return this class definition.
     * @param className
     * @return The class definition or null if not found.
     */
    public Class<?> findClassBundle(String className)
    {
        if (ClassServiceBootstrap.repositoryAdmin == null)
            return null;

        Class<?> c = getClassFromBundle(null, className);

        if (c == null) {
            Resource resource = ClassServiceBootstrap.deployThisResource(ClassServiceBootstrap.repositoryAdmin, className, Resolver.START, false);
            if (resource != null)
            {
                c = getClassFromBundle(resource, className);
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
        if (ClassServiceBootstrap.repositoryAdmin == null)
            return null;

        URL url = getResourceFromBundle(null, className);

        if (url == null) {
            Resource resource = ClassServiceBootstrap.deployThisResource(ClassServiceBootstrap.repositoryAdmin, className, Resolver.START, true);
            if (resource != null)
            {
            	url = getResourceFromBundle(resource, className);
            }
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
        if (ClassServiceBootstrap.repositoryAdmin == null)
            return null;

        ResourceBundle resourceBundle = this.getResourceBundleFromBundle(null, className, locale);

        if (resourceBundle == null) {
            Resource resource = ClassServiceBootstrap.deployThisResource(ClassServiceBootstrap.repositoryAdmin, className, Resolver.START, true);
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
    private ClassAccess getClassAccessService(String className)
    {
        try {
            String filter = "(className=" + className + ")";
            ServiceReference[] refs = bundleContext.getServiceReferences(ClassAccess.class.getName(), filter);

            if (refs != null)
            {
                ClassAccess classAccess = (ClassAccess) bundleContext.getService(refs[0]);
                return classAccess;
            }
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
    private Class<?> getClassFromBundle(Resource resource, String className)
    {
        Class<?> c = null;
        try {
            if (resource == null)
            {
                ClassAccess classAccess = this.getClassAccessService(className);
                if (classAccess != null)
                	c = classAccess.makeClass(className);
            }
            else
            {
            	Bundle bundle = ClassServiceBootstrap.getBundleFromResource(resource, bundleContext, ClassServiceBootstrap.getPackageName(className, false));
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
    private URL getResourceFromBundle(Resource resource, String className)
    {
        URL url = null;
        if (resource == null)
        {
            ClassAccess classAccess = this.getClassAccessService(className);
            if (classAccess != null)
                url = classAccess.getResource(className);
        }
        else
        {
        	Bundle bundle = ClassServiceBootstrap.getBundleFromResource(resource, bundleContext, ClassServiceBootstrap.getPackageName(className, true));
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
    private ResourceBundle getResourceBundleFromBundle(Resource resource, String baseName, Locale locale)
    {
    	ResourceBundle resourceBundle = null;
        if (resource == null)
        {
            ClassAccess classAccess = this.getClassAccessService(baseName);
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
        	Bundle bundle = ClassServiceBootstrap.getBundleFromResource(resource, bundleContext, ClassServiceBootstrap.getPackageName(baseName, true));
            if (USE_NO_RESOURCE_HACK)
            {
                try {
                	// TODO - If I have to do this, then I will have to link up the resourcebundle using the locales.
                	baseName = baseName.replace('.', File.separatorChar) + ClassServiceBootstrap.PROPERTIES;
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
}
