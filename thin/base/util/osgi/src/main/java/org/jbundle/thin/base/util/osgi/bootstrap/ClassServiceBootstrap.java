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
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

/**
 * OsgiBootstrapService - My only job is to get the ClassServiceImpl running.
 * 
 * @author don
 * 
 */
public class ClassServiceBootstrap implements BundleActivator
{
	private static ClassService osgiClassService = null;
	
    public static RepositoryAdmin repositoryAdmin = null;

    /**
     * Be sure to synchronize on this when you change it.
     */
    public static Boolean waitingForRepositoryAdmin = false;

    /**
	 * Good from start to stop.
	 */
    private BundleContext bundleContext = null;

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
        System.out.println("Starting Osgi Bootstrap");

        bundleContext = context;
        repositoryAdmin = ClassServiceBootstrap.getRepositoryAdmin(context, this);
        
        if (repositoryAdmin != null)
        {   // If the repository is up, I can get to work (otherwise, I'll be waiting)
        	ClassServiceBootstrap.addBootstrapRepository(repositoryAdmin, context);
            this.registerOsgiService();
        }
    }
    /**
     * Add the standard obr repository, so I can get the bundles that I need.
     * @param repositoryAdmin
     * @param context
     */
    public static void addBootstrapRepository(RepositoryAdmin repositoryAdmin, BundleContext context)
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
     * Register me as a service.
     */
    public void registerOsgiService()
    {
        System.out.println("Registering ObrUtil bundle");
        
        bundleContext.registerService(ClassServiceBootstrap.class.getName(), this, null);        

        waitingForRepositoryAdmin = false;
    }
    /**
     * Bundle shutting down.
     */
    public void stop(BundleContext context) throws Exception {
        System.out.println("Stopping Osgi Bootstrap");

        bundleContext = null;
        repositoryAdmin = null;
        waitingForRepositoryAdmin = false;
    }
	public static ClassService getOsgiClassService()
	{
	   int count = 0;
	   while (osgiClassService == null)
	   {	// Not running, try to start the class service
		   if (osgiClassService == null)
		   {	// Wait 6 sec and try again
			   Thread thread = Thread.currentThread();
		        synchronized (thread)
		        {
		           try {
		               thread.wait(6000);
		           } catch (InterruptedException ex) {
		               ex.printStackTrace();
		           }
		        }
		   }
	       if (count++ == 10)
	       {
	    	   System.out.println("The OsgiService Bootstrap never started - \n" +
	    	   		"Include it in your bundle and make sure it is listed as an activator!");
	    	   break;	// A minute is too long to wait
	       }
	   }
	   return osgiClassService;
	}
    /**
     * Get the repository admin service.
     * @param context
     * @return
     * @throws InvalidSyntaxException
     */
    public static RepositoryAdmin getRepositoryAdmin(BundleContext context, ClassServiceBootstrap autoStartNotify) throws InvalidSyntaxException
    {
    	RepositoryAdmin admin = null;
    	
        ServiceReference[] ref = context.getServiceReferences(RepositoryAdmin.class.getName(), null);

        if ((ref != null) && (ref.length > 0))
        	admin =  (RepositoryAdmin) context.getService(ref[0]);
        
        if (admin == null)
        	if (autoStartNotify != null)
        {   // Wait until the repository service is up until I start servicing clients
            if (ClassServiceBootstrap.waitingForRepositoryAdmin == false)
                context.addServiceListener(new RepositoryAdminServiceListener(autoStartNotify, context), "(objectClass=" + RepositoryAdmin.class.getName() + ")");
            ClassServiceBootstrap.waitingForRepositoryAdmin = true;
        }

        return admin;
    }
    /**
     * Convenience method to start this service.
     * If admin service is not up yet, this starts it.
     * @param className
     * @return true If I'm up already
     * @return false If the you will have to wait for me to come up.
     */
    @Deprecated
    public static boolean startOsgiUtil(BundleContext context)
    {
        try {
            RepositoryAdmin repositoryAdmin = ClassServiceBootstrap.getRepositoryAdmin(context, null);
            
            if (repositoryAdmin == null)
            {   // Wait until the repository service is up until I start up
//+                synchronized (OsgiClassService.waitingForRepositoryAdmin)
                {
                    if (ClassServiceBootstrap.waitingForRepositoryAdmin == false)
                        context.addServiceListener(new RepositoryAdminServiceListener(null, context), "(objectClass=" + RepositoryAdmin.class.getName() + ")");
                    ClassServiceBootstrap.waitingForRepositoryAdmin = true;
                }
                return false;
            }
            else
            {   // Good, the repository admin is up, bring me up
                return ClassServiceBootstrap.startOsgiService(repositoryAdmin, context);
            }
        } catch (InvalidSyntaxException e) {
            e.printStackTrace();
            return false;
        }
    }
    /**
     * Convenience method to start this service.
     * If admin service is not up yet, this starts it.
     * @param className
     * @return true If I'm up already
     * @return false If you will have to wait for me to come up.
     */
    public static boolean startOsgiService(RepositoryAdmin repositoryAdmin, BundleContext context)
    {
    	ClassServiceBootstrap.waitingForRepositoryAdmin = false;  // You would never call me if the repository wasn't up
        
        // If the repository is not up, but the bundle is deployed, this will find it
        Resource resource = ClassServiceBootstrap.deployThisResource(repositoryAdmin, ClassServiceBootstrap.class.getName(), 0, false);  // Get the bundle info from the repos
        
        String packageName = ClassServiceBootstrap.getPackageName(ClassServiceBootstrap.class.getName(), false);
        Bundle bundle = ClassServiceBootstrap.getBundleFromResource(resource, context, packageName);
        
        if (bundle != null)
        {
            if ((bundle.getState() & Bundle.ACTIVE) == 0)
            {
                try {
                    bundle.start();
                } catch (BundleException e) {
                    e.printStackTrace();
                }
            }
            else
                return true;    // I'm up already
        }
        return false;
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
     * This is stupid, there has to be a way to do this.
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
