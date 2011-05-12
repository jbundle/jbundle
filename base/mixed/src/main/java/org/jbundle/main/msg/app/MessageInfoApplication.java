/**
 *  @(#)MessageInfoApplication.
 *  Copyright © 2010 tourapp.com. All rights reserved.
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
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.base.message.app.MessageApplication;
import org.jbundle.base.message.core.*;
import org.jbundle.main.msg.db.*;
import org.jbundle.thin.base.message.*;
import javax.swing.*;
import org.jbundle.base.message.trx.server.*;
import org.jbundle.base.thread.*;

/**
 *  MessageInfoApplication - The application that handles messaging.
NOTE: Typically there is only one of these.
In demo deployments, there is one for each sub-domain, so the incoming message
will be handled using the correct databases..
 */
public class MessageInfoApplication extends MessageApplication
{
    public static final String AUTOSTART = "autostart";
    /**
     * Default constructor.
     */
    public MessageInfoApplication()
    {
        super();
    }
    /**
     * MessageInfoApplication Method.
     */
    public MessageInfoApplication(Object env, Map<String,Object> properties, JApplet applet)
    {
        this();
        this.init(env, properties, applet);
    }
    /**
     * Init Method.
     */
    public void init(Object env, Map<String,Object> properties, JApplet applet)
    {
        super.init(env, properties, applet);
        //?if (this.registerUniqueApplication(null, null) != Constants.NORMAL_RETURN)
        {
        //?    this.free();    // Don't start this application (It's already running somewhere)
        //?    return;
        }
        if (DBConstants.TRUE.equalsIgnoreCase(this.getProperty(DBParams.JMSSERVER)))
        {
            Map<String,Object> propProcess = new Hashtable<String,Object>();
            propProcess.put(DBParams.PROCESS, MessageInitialProcess.class.getName());
            Task task = new ProcessRunnerTask(this, null, propProcess);
            task.run(); // Don't run this async (for now).
        }
    }

}
