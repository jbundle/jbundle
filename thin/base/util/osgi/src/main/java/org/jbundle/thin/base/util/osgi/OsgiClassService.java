package org.jbundle.thin.base.util.osgi;

import java.io.File;
import java.net.URL;
import java.util.Dictionary;

import org.apache.felix.bundlerepository.Capability;
import org.apache.felix.bundlerepository.DataModelHelper;
import org.apache.felix.bundlerepository.Reason;
import org.apache.felix.bundlerepository.Repository;
import org.apache.felix.bundlerepository.RepositoryAdmin;
import org.apache.felix.bundlerepository.Requirement;
import org.apache.felix.bundlerepository.Resolver;
import org.apache.felix.bundlerepository.Resource;
import org.jbundle.model.Task;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

/**
 * ObrUtil
 * 
 * @author don
 * 
 */
public class OsgiClassService implements BundleActivator
{

    private BundleContext bundleContext = null;

    private RepositoryAdmin repositoryAdmin = null;
    
    private static boolean waitingForRepositoryAdmin = false;

    public OsgiClassService()
    {
        super();
    }

    public void start(BundleContext context) throws Exception
    {
        System.out.println("Starting ObrUtil bundle");
        
        bundleContext = context;

        repositoryAdmin = getRepositoryAdmin(context);
        
        if (repositoryAdmin == null)
        {   // Wait until the repository service is up until I start servicing clients
            if (waitingForRepositoryAdmin == false)
                bundleContext.addServiceListener(new RepositoryAdminServiceListener(this, context), "(objectClass=" + RepositoryAdmin.class.getName() + ")");
            waitingForRepositoryAdmin = true;
        }
        else
        {   // If the repository is up, I can get to work
            OsgiClassService.fixRepository(repositoryAdmin, context);
            this.registerOsgiService();
        }
    }

    public void registerOsgiService()
    {
        System.out.println("Registering ObrUtil bundle");
        
        osgiUtil = this;    // NO NO NO
        bundleContext.registerService(OsgiClassService.class.getName(), this, null);        

        waitingForRepositoryAdmin = false;
    }
    
    public void stop(BundleContext context) throws Exception {
        System.out.println("Stopping ObrUtil bundle");
        // I'm unregistered automatically

        bundleContext = null;
        repositoryAdmin = null;
        waitingForRepositoryAdmin = false;
    }

    /**
     * findClassBundle
     * 
     * @param className
     * @param task
     * @param bErrorIfNotFound
     * @return
     */
    public static Class<?> findClassBundle(String className, Task task, boolean bErrorIfNotFound)
    {
        if (osgiUtil == null)
        {
            System.out.println("Error: --- Don't call findClassBundle until the service is up ---");
            return null;
        }
        return osgiUtil.findClassesBundle(className, task, bErrorIfNotFound);
    }
    /**
     * findClassBundle
     * 
     * @param className
     * @param task
     * @param bErrorIfNotFound
     * @return
     */
    public static URL findResourceBundle(String className, Task task, boolean bErrorIfNotFound)
    {
        if (osgiUtil == null)
        {
            System.out.println("Error: --- Don't call findClassBundle until the service is up ---");
            return null;
        }
        return osgiUtil.findResourcesBundle(className, task, bErrorIfNotFound);
    }
    // Not sure of the performance of service lookup, so for now I use a static object
    // Don't use a static, lookup the service like everyone else.
    protected static OsgiClassService osgiUtil = null;   // NO NO NO

    /**
     * findClassBundle
     * 
     * @param className
     * @param task
     * @param bErrorIfNotFound
     * @return
     */
    public Class<?> findClassesBundle(String className, Task task, boolean bErrorIfNotFound)
    {
        if (repositoryAdmin == null)
            return null;

        Class<?> c = makeClassFromBundle(null, className);

        if (c == null) {
            Resource resource = deployThisResource(repositoryAdmin, className, Resolver.START, false);
            if (resource != null)
            {
                c = makeClassFromBundle(resource, className);
            }
        }

        return c;
    }
    /**
     * findClassBundle
     * 
     * @param className
     * @param task
     * @param bErrorIfNotFound
     * @return
     */
    public URL findResourcesBundle(String className, Task task, boolean bErrorIfNotFound)
    {
        if (repositoryAdmin == null)
            return null;

        URL c = getResourceFromBundle(null, className);

        if (c == null) {
            Resource resource = deployThisResource(repositoryAdmin, className, Resolver.START, true);
            if (resource != null)
            {
            	c = getResourceFromBundle(null, className);
            }
        }

        return c;
    }

    /**
     * 
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
     * startOsgiUtil
     * 
     * @param className
     * @return true If I'm up already
     * @return false If the you will have to wait for me to come up.
     */
    public static boolean startOsgiUtil(BundleContext context)
    {
        try {
            RepositoryAdmin repositoryAdmin = getRepositoryAdmin(context);
            
            if (repositoryAdmin == null)
            {   // Wait until the repository service is up until I start up
//+                synchronized (waitingForRepositoryAdmin)
                {
                    if (waitingForRepositoryAdmin == false)
                        context.addServiceListener(new RepositoryAdminServiceListener(null, context), "(objectClass=" + RepositoryAdmin.class.getName() + ")");
                    waitingForRepositoryAdmin = true;
                }
                return false;
            }
            else
            {   // Good, the repository is up, bring me up
                return startOsgiService(repositoryAdmin, context);
            }
        } catch (InvalidSyntaxException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean startOsgiService(RepositoryAdmin repositoryAdmin, BundleContext context)
    {
        waitingForRepositoryAdmin = false;  // You would never call me if the repos wasn't up
        
        Resource resource = deployThisResource(repositoryAdmin, OsgiClassService.class.getName(), 0, false);  // Get the bundle info from the repos
        String packageName = getPackageName(OsgiClassService.class.getName(), false); // If the repository is not up, but the bundle is deployed, this will find it
        
        Bundle bundle = getBundleFromResource(resource, context, packageName);
        
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
                return true;    // It's up already
        }

        return false;
    }
    /**
     * makeClassFromBundle
     * 
     * @param className
     * @return
     */
    private Class<?> makeClassFromBundle(Resource resource, String className)
    {
        Class<?> c = null;
        try {
            if (resource != null)
            {
            	Bundle bundle = getBundleFromResource(resource, bundleContext, getPackageName(className, false));
	            c = bundle.loadClass(className);
            }
            else
            {
                String filter = "(className=" + className + ")";
                ServiceReference[] refs = bundleContext.getServiceReferences(ClassAccess.class.getName(), filter);
	
	            if (refs != null)
	            {
	                ClassAccess classAccess = (ClassAccess) bundleContext.getService(refs[0]);
	
	                c = classAccess.makeClass(className);
	            }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvalidSyntaxException e) {
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
        URL c = null;
        try {
	        if (resource != null)
	        {
	        	Bundle bundle = getBundleFromResource(resource, bundleContext, getPackageName(className, true));
	            c = bundle.getEntry(className);
	        }
	        else
	        {
	            String filter = "(className=" + className + ")";
	            ServiceReference[] refs = bundleContext.getServiceReferences(ClassAccess.class.getName(), filter);
	
	            if (refs != null)
	            {
	                ClassAccess classAccess = (ClassAccess) bundleContext.getService(refs[0]);
	
	                c = classAccess.getResource(className);
	            }
	        }
        } catch (InvalidSyntaxException e) {
            e.printStackTrace();
        }
        return c;
    }

    public static RepositoryAdmin getRepositoryAdmin(BundleContext context) throws InvalidSyntaxException
    {
        ServiceReference[] ref = context.getServiceReferences(RepositoryAdmin.class.getName(), null);

        if ((ref == null) || (ref.length == 0))
            return null;
        
        return (RepositoryAdmin) context.getService(ref[0]);
    }

    public static void fixRepository(RepositoryAdmin repositoryAdmin, BundleContext context)
    {
        if (repositoryAdmin == null)
            return;
        String repository = context.getProperty("jbundle.repository.url");
        if (repository != null)
        	if (repository.length() > 0)
        		OsgiClassService.addRepository(repositoryAdmin, repository);

//        repository = "file:" + System.getProperty("user.home") + File.separator + "repository.xml";
//        OsgiClassService.addRepository(repositoryAdmin, repository);
        
//        repository = "file:" + System.getProperty("user.home") + File.separator + ".m2" + File.separator  + "repository" + File.separator  + "repository.xml";
//        OsgiClassService.addRepository(repositoryAdmin, repository);        

        repository = "file:" + System.getProperty("user.home") + File.separator + ".m2" + File.separator  + "full-repository.xml";
        OsgiClassService.addRepository(repositoryAdmin, repository);        
    }
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
