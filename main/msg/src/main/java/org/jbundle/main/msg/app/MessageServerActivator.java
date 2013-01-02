/**
 * @(#)MessageServerActivator.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.main.msg.app;

import java.util.*;

import org.jbundle.base.db.*;
import org.jbundle.thin.base.util.*;
import org.jbundle.thin.base.db.*;
import org.jbundle.base.db.event.*;
import org.jbundle.base.db.filter.*;
import org.jbundle.base.field.*;
import org.jbundle.base.field.convert.*;
import org.jbundle.base.field.event.*;
import org.jbundle.base.model.*;
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.model.db.*;
import org.jbundle.model.screen.*;
import org.jbundle.base.remote.server.*;
import org.osgi.framework.*;
import org.jbundle.util.osgi.finder.*;
import org.jbundle.thin.base.remote.*;

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
     * MessageServerActivator Method.
     */
    public MessageServerActivator(Object object)
    {
        this();
        this.init(object);
    }
    /**
     * Initialize class fields.
     */
    public void init(Object object)
    {
        /*
        super.init(object);   // Never used
        */
    }
    /**
     * Init Method.
     */
    public void init()
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
            props = Utility.putAllIfNew(props, env.getProperties());  // Use the same params as environment
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
        //            app.setProperty(DBParams.MESSAGE_SERVER, DBConstants.TRUE);
        //            app.getMessageManager(true);
            return service;    // Doesn't create environment
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

}
