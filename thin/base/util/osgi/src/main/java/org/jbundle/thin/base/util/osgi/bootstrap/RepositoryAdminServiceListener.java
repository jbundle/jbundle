package org.jbundle.thin.base.util.osgi.bootstrap;

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
public class RepositoryAdminServiceListener implements ServiceListener
{
    BundleContext context = null;
    
    ClassServiceBootstrap osgiUtil = null;
    
    public RepositoryAdminServiceListener(ClassServiceBootstrap osgiUtil, BundleContext context)
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
            ClassServiceBootstrap.addBootstrapRepository(repositoryAdmin, context);
            if (osgiUtil == null)
            	ClassServiceBootstrap.startOsgiService(repositoryAdmin, context);    // Now that I have the repo, start the OsgiUtilService
            else
                osgiUtil.registerClassServiceBootstrap(context); // Now that the repository started, you can register my started service for others to use
        }
        if (event.getType() == ServiceEvent.UNREGISTERING)
        {
            // What do I do?
        }
    }
}
