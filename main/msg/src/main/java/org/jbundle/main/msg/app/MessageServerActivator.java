/**
 * @(#)MessageServerActivator.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.main.msg.app;

import java.awt.*;
import java.util.*;

import org.jbundle.base.db.*;
import org.jbundle.thin.base.util.*;
import org.jbundle.thin.base.db.*;
import org.jbundle.base.db.event.*;
import org.jbundle.base.db.filter.*;
import org.jbundle.base.field.*;
import org.jbundle.base.field.convert.*;
import org.jbundle.base.field.event.*;
import org.jbundle.base.screen.model.*;
import org.jbundle.base.screen.model.util.*;
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
    public MessageServerActivator(Record record, String strName, int iDataLength, String strDesc, Object strDefault)
    {
        this();
        this.init(record, strName, iDataLength, strDesc, strDefault);
    }
    /**
     * Init Method.
     */
    public void init(Record record, String strName, int iDataLength, String strDesc, Object strDefault)
    {
        // No super methods
    }
    /**
     * SetupProperties Method.
     */
    public void setupProperties()
    {
        if (this.getProperty(DBParams.APP_NAME) == null)
            this.setProperty(DBParams.APP_NAME, this.getClass().getName());
        
        super.setupProperties();
    }
    /**
     * Start this service.
     * @return true if successful.
     */
    public boolean startupThisService(BundleContext bundleContext)
    {
        boolean success = super.startupThisService(bundleContext);
        
        EnvironmentActivator environmentActivator = (EnvironmentActivator)ClassServiceUtility.getClassService().getClassFinder(bundleContext).getClassBundleService(EnvironmentActivator.class.getName(), null, null, -1);
        Environment env = environmentActivator.getEnvironment();
        // Note the order that I do this... this is because MainApplication may need access to the remoteapp during initialization
        BaseApplication app = new MessageInfoApplication();
        server.init(app, null, null);
        app.init(env, Utility.propertiesToMap(this.getProperties()), null); // Default application (with params).
        //app.setProperty(DBParams.JMSSERVER, DBConstants.TRUE);
        //app.getMessageManager(true);
        if (env.getDefaultApplication() != null)
            if (env.getDefaultApplication() != app)
        {
            RemoteTask server = (RemoteTask)app.getRemoteTask(null);
            RemoteTask appServer = (RemoteTask)env.getDefaultApplication().getRemoteTask(null, null, false);
            if ((server != null) && (appServer != null))
            {
                try {
                    // Tell the remote session who my main session is
                    // so it can NOW where not to send server record
                    // messages (to eliminate echos in the client).
                    appServer.setRemoteMessageTask(server); // Should have done all the apps in this env!
                } catch (RemoteException ex)    {
                    ex.printStackTrace();
                }
            }
        }
        return success;
    }

}
