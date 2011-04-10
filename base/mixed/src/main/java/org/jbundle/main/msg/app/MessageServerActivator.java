/**
 * RmiSessionServer.java
 *
 * Created on January 10, 2000, 4:47 PM
 */
 
package org.jbundle.main.msg.app;

import java.rmi.RemoteException;
import java.util.Map;

import org.jbundle.base.message.core.MessageApplication;
import org.jbundle.base.util.BaseApplication;
import org.jbundle.base.util.Environment;
import org.jbundle.base.util.Utility;
import org.jbundle.thin.base.remote.RemoteTask;
import org.jbundle.thin.base.util.osgi.bundle.BaseBundleService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;

public class MessageServerActivator extends BaseBundleService
{
	
	protected MessageApplication server = null;
	
    String[] args = null;

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
            System.out.println("Starting Message Server");

            Map<String,Object> props = Utility.propertiesToMap(properties);
    	    Environment env = Environment.getEnvironment(props);
    	    
    	        // Note the order that I do this... this is because MainApplication may need access to the remoteapp during initialization
	        BaseApplication messageApplication = new MessageInfoApplication(env, props, null);
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
        }
        if (event.getType() == ServiceEvent.UNREGISTERING)
        {
            if (server != null)
            	server.free();
            server = null;
        }        
    }
}
