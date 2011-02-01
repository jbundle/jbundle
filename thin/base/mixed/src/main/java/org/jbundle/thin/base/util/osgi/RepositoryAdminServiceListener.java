package org.jbundle.thin.base.util.osgi;

import org.apache.felix.bundlerepository.RepositoryAdmin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

/**
 * RepositoryAdminServiceListener - Notify me when the repository admin is up.
 * 
 * @author don
 * 
 */
public class RepositoryAdminServiceListener  implements ServiceListener
{
    BundleContext context = null;
    
    OsgiClassService osgiUtil = null;
    
    public RepositoryAdminServiceListener(OsgiClassService osgiUtil, BundleContext context)
    {
        this.context = context;
        this.osgiUtil = osgiUtil;
    }
    /**
     * 
     * @param event
     */
    @Override
    public void serviceChanged(ServiceEvent event)
    {
        if (event.getType() == ServiceEvent.REGISTERED)
        {   // Repository admin came up 
            ServiceReference serviceReference = event.getServiceReference();
            Bundle bundle = serviceReference.getBundle();
            BundleContext context = bundle.getBundleContext();
            Object service = context.getService(serviceReference);
            RepositoryAdmin repositoryAdmin = null;
            if (service instanceof RepositoryAdmin)
                repositoryAdmin = (RepositoryAdmin)service; // Always
            OsgiClassService.fixRepository(repositoryAdmin, context);
            if (osgiUtil == null)
                OsgiClassService.startOsgiService(repositoryAdmin, context);    // Now that I have the repo, start the OsgiUtilService
            else
                osgiUtil.registerOsgiService(); // Now that the repository started, you can register my started service for others to use
        }
        if (event.getType() == ServiceEvent.UNREGISTERING)
        {
            // What do I do?
        }
    }
}
