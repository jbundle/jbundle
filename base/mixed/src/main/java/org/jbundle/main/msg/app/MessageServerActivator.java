/**
 * RmiSessionServer.java
 *
 * Created on January 10, 2000, 4:47 PM
 */
 
package org.jbundle.main.msg.app;

import java.rmi.RemoteException;
import java.util.Map;

import org.jbundle.base.util.BaseAppActivator;
import org.jbundle.base.util.DBConstants;
import org.jbundle.base.util.DBParams;
import org.jbundle.base.util.Environment;
import org.jbundle.base.util.EnvironmentActivator;
import org.jbundle.thin.base.remote.RemoteTask;
import org.jbundle.thin.base.util.Application;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;

public class MessageServerActivator extends BaseAppActivator
{
	
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {

		this.setProperty(DBParams.JMSSERVER, DBConstants.TRUE);

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
    		if (application == null)
    		{
    			BundleContext context = event.getServiceReference().getBundle().getBundleContext();
    	        this.checkDependentServicesAndStartup(context, EnvironmentActivator.class.getName());
    		}
        }
        if (event.getType() == ServiceEvent.UNREGISTERING)
        {
            if (application != null)
            	application.free();
            application = null;
        }        
    }
    /**
     * Start this service.
     * Override this to do all the startup.
     * @return true if successful.
     */
    public Application startupThisApp(Environment env, Map<String, Object> props)
    {
    	Application application = new MessageInfoApplication(env, props, null);
        if (env.getDefaultApplication() != null)
            if (env.getDefaultApplication() != application)
        {
            RemoteTask server = (RemoteTask)application.getRemoteTask(null);
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
        return application;
    }
}
