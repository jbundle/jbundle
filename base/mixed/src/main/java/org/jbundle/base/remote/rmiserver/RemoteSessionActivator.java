/**
 * RmiSessionServer.java
 *
 * Created on January 10, 2000, 4:47 PM
 */
 
package org.jbundle.base.remote.rmiserver;

import org.jbundle.base.util.DBConstants;
import org.jbundle.base.util.DBParams;
import org.jbundle.thin.base.util.osgi.bundle.BaseBundleService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;

public class RemoteSessionActivator extends BaseBundleService {
	
	protected RemoteSessionServer server = null;
	
    String[] args = {DBParams.JMSSERVER + "=" + DBConstants.TRUE,
            DBParams.REMOTEAPP + "=" + "msgapp",
            DBParams.PROVIDER + "=" + "linux-laptop"};	// TODO(don) Fix this

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
            System.out.println("Starting Server");
    		if (server == null)
    			server = RemoteSessionServer.startup(args);
        }
        if (event.getType() == ServiceEvent.UNREGISTERING)
        {
            if (server != null)
            	server.shutdown();
            server = null;
        }        
    }
}
