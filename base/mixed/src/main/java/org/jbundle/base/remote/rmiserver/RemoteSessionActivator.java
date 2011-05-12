/**
 * RmiSessionServer.java
 *
 * Created on January 10, 2000, 4:47 PM
 */
 
package org.jbundle.base.remote.rmiserver;

import java.rmi.RemoteException;
import java.util.Map;

import org.jbundle.base.util.BaseApplication;
import org.jbundle.base.util.DBConstants;
import org.jbundle.base.util.DBParams;
import org.jbundle.base.util.Environment;
import org.jbundle.base.util.EnvironmentActivator;
import org.jbundle.base.util.MainApplication;
import org.jbundle.base.util.Utility;
import org.jbundle.thin.base.message.MessageConstants;
import org.jbundle.thin.base.remote.ApplicationServer;
import org.jbundle.thin.base.remote.RemoteTask;
import org.jbundle.util.osgi.BundleService;
import org.jbundle.util.osgi.bundle.BaseBundleService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;

public class RemoteSessionActivator extends BaseBundleService
	implements ApplicationServer
{
	protected RemoteSessionServer server = null;
	
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		this.setProperty(DBParams.JMSSERVER, DBConstants.FALSE);
		this.setProperty(DBParams.REMOTEAPP, "msgapp");
		this.setProperty(DBParams.PROVIDER, "linux-laptop");
        if (this.getProperty(DBParams.FREEIFDONE) == null)
        	this.setProperty(DBParams.FREEIFDONE, DBConstants.FALSE);   // Don't free when only the last app is running.
        if (this.getProperty(DBParams.LOCAL) == null)
        	this.setProperty(DBParams.LOCAL, DBParams.JDBC); // Remote Server ALWAYS uses Jdbc
        if (this.getProperty(DBParams.REMOTE) == null)
        	this.setProperty(DBParams.REMOTE, DBParams.JDBC);
        if (this.getProperty(DBParams.TABLE) == null)
        	this.setProperty(DBParams.TABLE, DBParams.JDBC);
        if (this.getProperty(MessageConstants.MESSAGE_FILTER) == null)
        	this.setProperty(MessageConstants.MESSAGE_FILTER, MessageConstants.TREE_FILTER);  // Default for a server
		
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
    		{
    			BundleContext context = event.getServiceReference().getBundle().getBundleContext();
    	        this.checkDependentServicesAndStartup(context, EnvironmentActivator.class.getName());
    		}
        }
        if (event.getType() == ServiceEvent.UNREGISTERING)
        {
            if (server != null)
            	server.shutdown();
            server = null;
        }
    }
    /**
     * Start this service.
     * Override this to do all the startup.
     * @return true if successful.
     */
    public boolean startupThisService(BundleService bundleService)
    {
        Map<String,Object> props = Utility.propertiesToMap(this.getProperties());
        server = RemoteSessionServer.startupServer(props);	// Doesn't create environment

        EnvironmentActivator environmentActivator = (EnvironmentActivator)bundleService;
        Environment env = environmentActivator.getEnvironment();
        // Note the order that I do this... this is because MainApplication may need access to the remoteapp during initialization
        BaseApplication app = new MainApplication();
        server.setApp(app);
        app.init(env, props, null); // Default application (with params).
//        app.setProperty(DBParams.JMSSERVER, DBConstants.TRUE);
//        app.getMessageManager(true);
    	return true;
    }

	@Override
	public RemoteTask createRemoteTask(Map<String, Object> properties)
			throws RemoteException {
		if (server == null)
			return null;
		return server.createRemoteTask(properties);
	}
}
