/**
 * @(#)MessageInfoApplication.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.main.msg.app;

import java.util.Hashtable;
import java.util.Map;

import org.jbundle.base.message.app.MessageApplication;
import org.jbundle.base.thread.ProcessRunnerTask;
import org.jbundle.base.util.DBConstants;
import org.jbundle.base.util.DBParams;
import org.jbundle.model.Task;

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
    public MessageInfoApplication(Object env, Map<String,Object> properties, Object applet)
    {
        this();
        this.init(env, properties, applet);
    }
    /**
     * Init Method.
     */
    public void init(Object env, Map<String,Object> properties, Object applet)
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
        else
        {
            this.setProperty(DBParams.REMOTE_APP_NAME, DBParams.REMOTE_MESSAGE_APP);
        }
    }

}
