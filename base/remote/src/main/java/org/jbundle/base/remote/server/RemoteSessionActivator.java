/*
 * RmiSessionServer.java
 *
 * Created on January 10, 2000, 4:47 PM
 
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.remote.server;

import java.util.Map;

import org.jbundle.base.model.Utility;
import org.jbundle.base.util.Environment;
import org.jbundle.model.App;
import org.jbundle.model.Env;
import org.jbundle.thin.base.remote.RemoteException;
import org.osgi.framework.BundleContext;


public class RemoteSessionActivator extends BaseRemoteSessionActivator
{
    /**
     * Start this service.
     * Override this to do all the startup.
     * @return true if successful.
     */
    public Object startupService(BundleContext bundleContext)
    {
        try {
            Environment env = (Environment)this.getService(Env.class);
            // Note the order that I do this... this is because MainApplication may need access to the remoteapp during initialization
            App app = env.getMessageApplication(true, getServiceProperties());
//            app.setProperty(DBParams.JMSSERVER, DBConstants.TRUE);
//            app.getMessageManager(true);
            return new RemoteSessionServer(app, null, getServiceProperties());	// Doesn't create environment
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    	return null;
    }
}
