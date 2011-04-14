/**
 * RmiSessionServer.java
 *
 * Created on January 10, 2000, 4:47 PM
 */
 
package org.jbundle.main.msg.app;

import java.rmi.RemoteException;
import java.util.Map;

import org.jbundle.base.message.core.MessageApplication;
import org.jbundle.base.util.Environment;
import org.jbundle.base.util.EnvironmentActivator;
import org.jbundle.base.util.Utility;
import org.jbundle.thin.base.remote.RemoteTask;
import org.jbundle.thin.base.util.osgi.bundle.BaseBundleService;
import org.jbundle.thin.base.util.osgi.bundle.BundleService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;

public class MessageServerActivator extends BaseBundleService
{
	
	protected MessageApplication messageApplication = null;
	
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
	}

    @Override
    public void serviceChanged(ServiceEvent event) {
        if (event.getType() == ServiceEvent.REGISTERED)
        { // Osgi Service is up, Okay to start the server
    		if (messageApplication == null)
    		{
    			BundleContext context = event.getServiceReference().getBundle().getBundleContext();
    	        this.checkDependentServicesAndStartup(context, EnvironmentActivator.class.getName());
    		}
        }
        if (event.getType() == ServiceEvent.UNREGISTERING)
        {
            if (messageApplication != null)
            	messageApplication.free();
            messageApplication = null;
        }        
    }
    /**
     * Start this service.
     * Override this to do all the startup.
     * @return true if successful.
     */
    public boolean startupThisService(BundleService bundleService)
    {
        System.out.println("Starting Message Server");

        Map<String,Object> props = Utility.propertiesToMap(this.getProperties());

        EnvironmentActivator environmentActivator = (EnvironmentActivator)bundleService;
        Environment env = environmentActivator.getEnvironment();
	    
	        // Note the order that I do this... this is because MainApplication may need access to the remoteapp during initialization
        messageApplication = new MessageInfoApplication(env, props, null);
        if (env.getDefaultApplication() != null)
            if (env.getDefaultApplication() != messageApplication)
        {
            RemoteTask server = (RemoteTask)messageApplication.getRemoteTask(null);
            RemoteTask appServer = (RemoteTask)env.getDefaultApplication().getRemoteTask(null, null, false);
            if ((server != null) && (appServer != null))
            {
                try {
                    // Tell the remote session who my main session is
                    // so it can know where not to send server record
                    // messages (to eliminate echos in the client).
                    appServer.setRemoteMessageTask(server); // Should have done all the apps in this env!
                } catch (RemoteException ex)    {
                    ex.printStackTrace();
                }
            }
        }

    	return true;
    }
}
