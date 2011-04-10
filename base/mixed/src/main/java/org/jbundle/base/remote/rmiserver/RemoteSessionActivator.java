/**
 * RmiSessionServer.java
 *
 * Created on January 10, 2000, 4:47 PM
 */
 
package org.jbundle.base.remote.rmiserver;

import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.Map;

import org.jbundle.base.util.BaseApplication;
import org.jbundle.base.util.DBConstants;
import org.jbundle.base.util.DBParams;
import org.jbundle.base.util.Environment;
import org.jbundle.base.util.MainApplication;
import org.jbundle.thin.base.remote.ApplicationServer;
import org.jbundle.thin.base.remote.RemoteTask;
import org.jbundle.thin.base.util.Util;
import org.jbundle.thin.base.util.osgi.bundle.BaseBundleService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;

public class RemoteSessionActivator extends BaseBundleService
	implements ApplicationServer
{
	
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
    		{
//    			server = RemoteSessionServer.startup(args);
    			// Startup the job scheduler
    	        Map<String,Object> propertiesTemp = new Hashtable<String,Object>();
    	        Util.parseArgs(propertiesTemp, args);
    	        if (propertiesTemp.get(DBParams.FREEIFDONE) == null)
    	            propertiesTemp.put(DBParams.FREEIFDONE, DBConstants.FALSE);   // Don't free when only the last app is running.
    	        if (propertiesTemp.get(DBParams.LOCAL) == null)
    	            propertiesTemp.put(DBParams.LOCAL, DBParams.JDBC); // Remote Server ALWAYS uses Jdbc
    	        if (propertiesTemp.get(DBParams.REMOTE) == null)
    	            propertiesTemp.put(DBParams.REMOTE, DBParams.JDBC);
    	        if (propertiesTemp.get(DBParams.TABLE) == null)
    	            propertiesTemp.put(DBParams.TABLE, DBParams.JDBC);
    	        if (propertiesTemp.get(DBParams.MESSAGE_FILTER) == null)
    	            propertiesTemp.put(DBParams.MESSAGE_FILTER, DBParams.TREE_FILTER);  // Default for a server

    	        server = RemoteSessionServer.startupServer(propertiesTemp);

    	        Environment env = new Environment(propertiesTemp);
    	        // Note the order that I do this... this is because MainApplication may need access to the remoteapp during initialization
    	        BaseApplication app = new MainApplication();
    	        server.setApp(app);
    	        Map<String,Object> properties = null;
    	        if (args != null)
    	        {
    	            properties = new Hashtable<String,Object>();
    	            Util.parseArgs(properties, args);
    	        }
    	        app.init(env, properties, null); // Default application (with params).
//    	        app.setProperty(DBParams.JMSSERVER, DBConstants.TRUE);
//    	        app.getMessageManager(true);
    		}
        }
        if (event.getType() == ServiceEvent.UNREGISTERING)
        {
            if (server != null)
            	server.shutdown();
            server = null;
        }        
    }

	@Override
	public RemoteTask createRemoteTask(Map<String, Object> properties)
			throws RemoteException {
		if (server == null)
			return null;
		return server.createRemoteTask(properties);
	}
}
