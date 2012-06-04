/**
 * @(#)MessageServerActivator.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.main.msg.app;

import java.util.Map;

import org.jbundle.base.model.DBParams;
import org.jbundle.base.remote.server.BaseRemoteSessionActivator;
import org.jbundle.base.remote.server.RemoteSessionServer;
import org.jbundle.base.util.Environment;
import org.jbundle.model.App;
import org.jbundle.model.Env;
import org.jbundle.thin.base.remote.RemoteException;
import org.osgi.framework.BundleContext;

/**
 *  MessageServerActivator - .
 */
public class MessageServerActivator extends BaseRemoteSessionActivator
{
    /**
     * Default constructor.
     */
    public MessageServerActivator()
    {
        super();
    }
    /**
     * SetupProperties Method.
     */
    public void init(BundleContext bundleContext)
    {
        super.init();

        if (this.getProperty(DBParams.APP_NAME) == null)
            this.setProperty(DBParams.APP_NAME, this.getClass().getName());
    }
    /**
     * Start this service.
     * @return true if successful.
     */
    public Object startupService(BundleContext bundleContext)
    {
        Map<String,Object> props = this.getServiceProperties();
        try {
            Environment env = (Environment)this.getService(Env.class);
            // Note the order that I do this... this is because MainApplication may need access to the remoteapp during initialization
            App app = env.getMessageApplication(false, props);
            RemoteSessionServer service = null;
            if (app == null)
            { // Note the order that I do this... this is because MainApplication may need access to the remoteapp during initialization
                app = new MessageInfoApplication();
                service = new RemoteSessionServer(app, null, props);
            }
            app.init(env, props, null); // Default application (with params).
        	if (service == null)
        		service = new RemoteSessionServer(app, null, props);
//            app.setProperty(DBParams.JMSSERVER, DBConstants.TRUE);
//            app.getMessageManager(true);
            return service;	// Doesn't create environment
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    	return null;
    }

}
