package org.jbundle.thin.base.util.osgi.bootstrap;

import java.io.File;
import java.util.Dictionary;

import org.apache.felix.bundlerepository.DataModelHelper;
import org.apache.felix.bundlerepository.Reason;
import org.apache.felix.bundlerepository.Repository;
import org.apache.felix.bundlerepository.RepositoryAdmin;
import org.apache.felix.bundlerepository.Requirement;
import org.apache.felix.bundlerepository.Resolver;
import org.apache.felix.bundlerepository.Resource;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

/**
 * ClassServiceBootstrap - My only job is to get the ClassServiceImpl running.
 *
 * NOTE: Be very careful when using this class. Remember that this class is both
 * run standalone and it is embedded in a jar. This means that setting a static
 * will only set the static variable for the current version.
 * 
 * This activator class can be used in one of three ways:
 * 1. Import this code into your OSGi module. Call getClassService() and this utility
 * will start the ClassServiceImpl and return a reference.
 * 2. Override this code if you have a service that you want to register. Remember
 * to call context.registerService(this.getClass().getName(), this, null); in your
 * overriding class to register a service.
 * 3. Call getClassService() from your module (not recommended). This works fine if
 * you already have this module installed, but will not work as a bootstrap
 * (since this module would need to be loaded by the bootstrap).
 * 
 * @author don
 * 
 */
public class ClassServiceBootstrap implements BundleActivator
{
	public static final String CLASS_SERVICE_CLASS_NAME = "org.jbundle.thin.base.util.osgi.ClassService";
	public static final String CLASS_SERVICE_IMPL_CLASS_NAME = "org.jbundle.thin.base.util.osgi.service.ClassServiceImpl";

	private static Object cachedClassService = null;
	
    public static RepositoryAdmin repositoryAdmin = null;

    /**
     * Be sure to synchronize on this when you change it.
     */
    public static Boolean waitingForRepositoryAdmin = false;
    public static Boolean waitingForClassService = false;

    /**
	 * Good from start to stop. May be needed by overriding class.
	 */
    protected static BundleContext bundleContext = null;

    /**
     * Service to find resources by class name.
     */
    public ClassServiceBootstrap()
    {
        super();
    }
    /**
     * Bundle starting.
     * If the service listener is up, register me, else wait.
     */
    public void start(BundleContext context) throws Exception
    {
        System.out.println("Starting ClassServiceBootstrap");

        bundleContext = context;
        repositoryAdmin = this.getRepositoryAdmin(context, this);
        
        if (repositoryAdmin != null)
        {   // If the repository is up, I can get to work (otherwise, I'll be waiting)
        	this.addBootstrapRepository(repositoryAdmin, context);
            this.registerClassServiceBootstrap(context);
        }
    }
    /**
     * Get the repository admin service.
     * @param context
     * @return
     * @throws InvalidSyntaxException
     */
    public RepositoryAdmin getRepositoryAdmin(BundleContext context, ClassServiceBootstrap autoStartNotify) throws InvalidSyntaxException
    {
    	RepositoryAdmin admin = null;
    	
        ServiceReference[] ref = context.getServiceReferences(RepositoryAdmin.class.getName(), null);

        if ((ref != null) && (ref.length > 0))
        	admin =  (RepositoryAdmin) context.getService(ref[0]);
        
        if (admin == null)
        	if (autoStartNotify != null)
                if (waitingForRepositoryAdmin == false)
        {   // Wait until the repository service is up until I start servicing clients
            context.addServiceListener(new RepositoryAdminServiceListener(autoStartNotify, context), "(" + Constants.OBJECTCLASS + "=" + RepositoryAdmin.class.getName() + ")");
            waitingForRepositoryAdmin = true;
        }

        return admin;
    }
    /**
     * Add the standard obr repository, so I can get the bundles that I need.
     * @param repositoryAdmin
     * @param context
     */
    public void addBootstrapRepository(RepositoryAdmin repositoryAdmin, BundleContext context)
    {
        if (repositoryAdmin == null)
            return;
        String repository = context.getProperty("jbundle.repository.url");
        if (repository != null)
        	if (repository.length() > 0)
        		ClassServiceBootstrap.addRepository(repositoryAdmin, repository);

        repository = "file:" + System.getProperty("user.home") + File.separator + ".m2" + File.separator  + "full-repository.xml";
        ClassServiceBootstrap.addRepository(repositoryAdmin, repository);        
    }
    /**
     * Add this repository to my available repositories.
     * @param repositoryAdmin
     * @param repository
     */
    public static void addRepository(RepositoryAdmin repositoryAdmin, String repository)
    {
        try {
            if (repository != null)
            {
                boolean duplicate = false;
                for (Repository repo : repositoryAdmin.listRepositories())
                {
                    if (repository.equalsIgnoreCase(repo.getURI()))
                        duplicate = true;
                }
                if (!duplicate)
                {
                    Repository repo = repositoryAdmin.addRepository(repository);
                    if (repo == null)
                        repositoryAdmin.removeRepository(repository);   // Ignore repos not found
                }
            }
        } catch (Exception e) {
            // Ignore exception e.printStackTrace();
        }
    }
    /**
     * Called when this service is active.
     * Override this to register your service if you need a service.
     * I Don't register this bootstrap for two reasons:
     * 1. I Don't need this object
     * 2. This object was usually instanciated by bootstrap code copied to the calling classes' jar.
     */
    public void registerClassServiceBootstrap(BundleContext context)
    {
        waitingForRepositoryAdmin = false;

        System.out.println("ClassServiceBootstrap is up");

        this.startClassService(repositoryAdmin, context);    // Now that I have the repo, start the ClassService
    }
    /**
     * Bundle shutting down.
     */
    public void stop(BundleContext context) throws Exception {
        System.out.println("Stopping ClassService Bootstrap");

        bundleContext = null;
        repositoryAdmin = null;
        waitingForRepositoryAdmin = false;
        waitingForClassService = false;
        cachedClassService = null;
    }
    /**
     * Get the class service.
     * This call should activate this bundle and start the ClassService.
     * @return
     */
	public static Object getClassService()
	{
		if (cachedClassService != null)
			return cachedClassService;

		// First time or not running, try to find the class service
		cachedClassService = findClassService(true);
		
		return cachedClassService;
	}
	/**
	 * Get the class service.
	 * @param waitForStart TODO
	 * @return The class service or null if it doesn't exist.
	 */
	public static Object findClassService(boolean waitForStart)
	{
        if (bundleContext == null)
		{
			System.out.println("Error: ClassServiceBootstrap was never started\n" + 
					"Add it as your bundle activator");
			return null;
		}

        Object classService = null;
    	
		try {
			ServiceReference[] ref = bundleContext.getServiceReferences(CLASS_SERVICE_CLASS_NAME, null);
		
			if ((ref != null) && (ref.length > 0))
				classService =  bundleContext.getService(ref[0]);
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
		}

		if (classService == null)
			if (waitForStart)
				if (waitingForClassService == false)
		{
			waitingForClassService = true;
			// TODO Minor synchronization issue here
			Thread thread = Thread.currentThread();
			try {
				bundleContext.addServiceListener(new ClassServiceListener(thread, bundleContext), "(" + Constants.OBJECTCLASS + "=" + CLASS_SERVICE_CLASS_NAME + ")");
			} catch (InvalidSyntaxException e) {
				e.printStackTrace();
			}

			// Wait a minute for the ClassService to come up while the activator starts this service
			synchronized (thread)
			{
				try {
					thread.wait(60000);
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
			}
			waitingForClassService = false;
			
			try {
				ServiceReference[] ref = bundleContext.getServiceReferences(CLASS_SERVICE_CLASS_NAME, null);
			
				if ((ref != null) && (ref.length > 0))
					classService =  bundleContext.getService(ref[0]);
			} catch (InvalidSyntaxException e) {
				e.printStackTrace();
			}

			if (classService == null)
				System.out.println("The ClassService never started - \n" +
					"Include the bootstrap code in your bundle and make sure it is listed as an activator!");
		}

		return classService;
	}
    /**
     * Convenience method to start this service.
     * If admin service is not up yet, this starts it.
     * @param className
     * @return true If I'm up already
     * @return false If I had a problem.
     */
    public boolean startClassService(RepositoryAdmin repositoryAdmin, BundleContext context)
    {
    	if (cachedClassService != null)
    		return true;	// Never
    	cachedClassService = findClassService(false);	// See if someone else started it up
    	if (cachedClassService != null)
    		return true;	// Already up!
        // If the repository is not up, but the bundle is deployed, this will find it
        Resource resource = ClassServiceBootstrap.deployThisResource(repositoryAdmin, CLASS_SERVICE_IMPL_CLASS_NAME, 0, false);  // Get the bundle info from the repos
        
        String packageName = ClassServiceBootstrap.getPackageName(CLASS_SERVICE_IMPL_CLASS_NAME, false);
        Bundle bundle = ClassServiceBootstrap.getBundleFromResource(resource, context, packageName);
        
        if (bundle != null)
        {
            if (((bundle.getState() & Bundle.ACTIVE) == 0)
            		&& ((bundle.getState() & Bundle.STARTING) == 0))
            {
                try {
                    bundle.start();
                } catch (BundleException e) {
                    e.printStackTrace();
                }
            }
            cachedClassService = getClassService();	// This will wait until it is active to return
            return (cachedClassService != null);	// Success
        }
        return false;	// Error! Where is my bundle?
    }
    /**
     * Find this resource in the repository, then deploy and optionally start it.
     * @param className
     * @param options 
     * @return
     */
    public static Resource deployThisResource(RepositoryAdmin repositoryAdmin, String className, int options, boolean resourceType)
    {
        DataModelHelper helper = repositoryAdmin.getHelper();
        String packageName = getPackageName(className, resourceType);
        String filter2 = "(package=" + packageName + ")"; // + "(version=xxx)"
        Requirement requirement = helper.requirement("package", filter2);
        Requirement[] requirements = { requirement };// repositoryAdmin
        Resource[] resources = repositoryAdmin.discoverResources(requirements);

        if ((resources != null) && (resources.length > 0))
        {
            Resource resource = resources[0];

            Resolver resolver = repositoryAdmin.resolver();
            resolver.add(resource);
            if (resolver.resolve(options))
            {
                resolver.deploy(options);

                return resource;    // Success
                
            } else {
                Reason[] reqs = resolver.getUnsatisfiedRequirements();
                for (int i = 0; i < reqs.length; i++) {
                    System.out.println("Unable to resolve: " + reqs[i]);
                }
            }
        }
        return null;   //Error
    }
    /**
     * Find the currently installed bundle that exports this package.
     * NOTE: This is stupid, there has to be a way to do this.
     * @param resource
     * @param context
     * @return
     */
    public static Bundle getBundleFromResource(Resource resource, BundleContext context, String packageName)
    {
        if (context == null)
            return null;
        Bundle[] bundles = context.getBundles();
        Bundle bestBundle = null;
        String bestVersion = null;
        for (Bundle bundle : bundles)
        {
            if (resource != null)
            {
                if ((bundle.getSymbolicName().equals(resource.getSymbolicName())) && (bundle.getVersion().equals(resource.getVersion())))
                    return bundle;               
            }
            else if (packageName != null)
            {
                Dictionary<?, ?> dictionary = bundle.getHeaders();
                String packages = (String)dictionary.get("Export-Package");
                String version = (String)dictionary.get("Bundle-Version");
                if (packages != null)
                {
                    StringBuilder sb = new StringBuilder(packages);
                    while (true)
                    {
                        int start = sb.indexOf("=\"");
                        if (start == -1)
                            break;
                        int end = sb.indexOf("\"", start + 2);
                        if ((start > -1) && (end > -1))
                            sb.delete(start, end + 1);
                        else
                            break;  // never
                    }
                    while (true)
                    {
                        int semi = sb.indexOf(";");
                        if (semi == -1)
                            break;
                        int comma = sb.indexOf(",", semi);
                        if (comma == -1)
                            comma = sb.length();
                        else if (sb.charAt(comma + 1) == ' ')
                            comma++;
                        if ((semi > -1) && (comma > -1))
                            sb.delete(semi, comma);
                        else
                            break;  // never
                    }
                    
                    String[] packs = sb.toString().split(",");
                    for (String pack : packs)
                    {
                        if (packageName.equals(pack))
                        {
                        	if ((bestVersion == null)
                        		|| (bestVersion.compareTo(version) < 0))
                        	{
                        		bestBundle = bundle;	// TODO Newest version NO NO NO - Should get requested versions ie., 'x.x.)'
                        		bestVersion = version;
                        	}
                        }
                    }
                }
            }
        }
        
        return bestBundle;
    }
    /**
     * Get the package name of this class name
     * @param className
     * @return
     */
    public static String getPackageName(String className, boolean resource)
    {
        String packageName = null;
        if (className != null)
        {
        	if (resource)
        		if (className.endsWith(PROPERTIES))
        			className = className.substring(0, className.length() - PROPERTIES.length());
            if (className.lastIndexOf('.') != -1)
                packageName = className.substring(0, className.lastIndexOf('.'));
        }
        return packageName;
    }
    public static final String PROPERTIES = ".properties";
}
