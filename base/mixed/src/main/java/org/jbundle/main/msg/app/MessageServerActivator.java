/*
 * RmiSessionServer.java
 *
 * Created on January 10, 2000, 4:47 PM
 
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.main.msg.app;

import java.util.Map;

import org.jbundle.base.remote.rmiserver.BaseRemoteSessionActivator;
import org.jbundle.base.remote.rmiserver.RemoteSessionServer;
import org.jbundle.base.util.BaseApplication;
import org.jbundle.base.util.DBParams;
import org.jbundle.base.util.Environment;
import org.jbundle.base.util.EnvironmentActivator;
import org.jbundle.base.util.Utility;
import org.jbundle.thin.base.remote.RemoteException;
import org.jbundle.thin.base.remote.RemoteTask;
import org.jbundle.util.osgi.BundleService;
import org.osgi.framework.BundleContext;

public class MessageServerActivator extends BaseRemoteSessionActivator
{
    /**
     * Setup the application properties.
     */
    public void setupProperties()
    {
        if (this.getProperty(DBParams.REMOTEAPP) == null)
            this.setProperty(DBParams.REMOTEAPP, "msgapp");

        super.setupProperties();
    }
	
    /**
     * Start this service.
     * Override this to do all the startup.
     * @return true if successful.
     */
    public boolean startupThisService(BundleService bundleService, BundleContext context)
    {
        Map<String,Object> props = Utility.propertiesToMap(this.getProperties());
        server = RemoteSessionServer.startupServer(props);  // Doesn't create environment

        EnvironmentActivator environmentActivator = (EnvironmentActivator)bundleService;
        Environment env = environmentActivator.getEnvironment();
        // Note the order that I do this... this is because MainApplication may need access to the remoteapp during initialization
        BaseApplication app = new MessageInfoApplication();
        server.setApp(app);
        app.init(env, props, null); // Default application (with params).
//        app.setProperty(DBParams.JMSSERVER, DBConstants.TRUE);
//        app.getMessageManager(true);
        if (env.getDefaultApplication() != null)
            if (env.getDefaultApplication() != app)
        {
            RemoteTask server = (RemoteTask)app.getRemoteTask(null);
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
