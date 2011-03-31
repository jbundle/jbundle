package org.jbundle.bundle.impl;

/**
 * Hello world!
 *
 */
import org.jbundle.base.remote.rmiserver.RemoteSessionServer;
import org.jbundle.base.util.DBConstants;
import org.jbundle.base.util.DBParams;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;

/**
 * 
 * @author don
 *
 */
public class Activator extends Object
		implements BundleActivator, ServiceListener {

    String[] args = {DBParams.JMSSERVER + "=" + DBConstants.TRUE,
            DBParams.REMOTEAPP + "=" + "msgapp",
            DBParams.PROVIDER + "=" + "linux-laptop"};

    private RemoteSessionServer remoteSessionServer = null;

    /**
     * 
     */
    public void start(BundleContext context) throws Exception {
        System.out.println("Starting Server bundle");

//?        super.start(context);
        
    }
    
    /**
     * Called when this service is active.
     * Override this to register your service if you need a service.
     * I Don't register this bootstrap for two reasons:
     * 1. I Don't need this object
     * 2. This object was usually instaniated by bootstrap code copied to the calling classes' jar.
     */
    public void registerClassServiceBootstrap(BundleContext context)
    {
//        super.registerClassServiceBootstrap(context);
        
        try {
			context.addServiceListener(this, "(objectClass=" + this.getClass().getName() + ")");
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
		}
		
		context.registerService(this.getClass().getName(), this, null);	// So other processes can find me
    }

    @Override
    public void serviceChanged(ServiceEvent event) {
        if (event.getType() == ServiceEvent.REGISTERED)
        { // Osgi Service is up, Okay to start the server
            System.out.println("Starting Server");
            remoteSessionServer = RemoteSessionServer.startup(args);
        }
        if (event.getType() == ServiceEvent.UNREGISTERING)
        {
            if (remoteSessionServer != null)
            	remoteSessionServer.shutdown();
        }        
    }
    /**
     * 
     */
    public void stop(BundleContext context) throws Exception {
        System.out.println("Stopping Server bundle");
        
//        super.stop(context);
        
        // No need to Shutdown the server, since this will be unregistered automatically (and service changed will shut this down)
        
    }

}
