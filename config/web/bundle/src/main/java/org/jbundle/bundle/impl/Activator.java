package org.jbundle.bundle.impl;

/**
 * Hello world!
 *
 */
import org.jbundle.base.remote.rmiserver.RemoteSessionServer;
import org.jbundle.base.util.DBConstants;
import org.jbundle.base.util.DBParams;
import org.jbundle.thin.base.util.osgi.bootstrap.*;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

/**
 * 
 * @author don
 *
 */
public class Activator implements BundleActivator, ServiceListener {
    ServiceRegistration helloServiceRegistration;

    String[] args = {DBParams.JMSSERVER + "=" + DBConstants.TRUE,
            DBParams.REMOTEAPP + "=" + "msgapp",
            DBParams.PROVIDER + "=" + "linux-laptop"};
    
    /**
     * 
     */
    public void start(BundleContext context) throws Exception {
        System.out.println("Starting Server bundle");
        
        ServiceReference[] ref = context.getServiceReferences(ClassServiceBootstrap.class.getName(), null);

        if ((ref == null) || (ref.length == 0))
        {
            context.addServiceListener(this, "(objectClass=" + ClassServiceBootstrap.class.getName() + ")");
            ClassServiceBootstrap.startOsgiUtil(context);    // HACK - OsgiUtil is suppose to autostart, here I start it manually
        }
        else
        { // Osgi Service is up, Okay to start the server
            System.out.println("Starting Server");
            RemoteSessionServer.main(args);         
        }   
        
    }
    
    @Override
    public void serviceChanged(ServiceEvent event) {
        if (event.getType() == ServiceEvent.REGISTERED)
        { // Osgi Service is up, Okay to start the server
            System.out.println("Starting Server");
            RemoteSessionServer.main(args);         
        }
        if (event.getType() == ServiceEvent.UNREGISTERING)
        {
            // What do I do?
        }        
    }
    /**
     * 
     */
    public void stop(BundleContext context) throws Exception {
        System.out.println("Stopping Server bundle");
        
        // +++ Shutdown the server
        
    }

}
