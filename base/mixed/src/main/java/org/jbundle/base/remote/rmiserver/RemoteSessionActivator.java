/**
 * RmiSessionServer.java
 *
 * Created on January 10, 2000, 4:47 PM
 */
 
package org.jbundle.base.remote.rmiserver;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class RemoteSessionActivator implements BundleActivator {
	
	protected RemoteSessionServer server = null;
	
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		if (server == null)
			server = RemoteSessionServer.startup(null);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		if (server != null)
			server.shutdown();
	}

}
