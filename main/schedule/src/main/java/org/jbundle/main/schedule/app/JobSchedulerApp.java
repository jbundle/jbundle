/**
 * @(#)JobSchedulerApp.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.main.schedule.app;

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
import org.jbundle.model.db.*;
import org.jbundle.model.screen.*;
import javax.swing.*;
import org.jbundle.thin.base.thread.*;
import org.jbundle.base.thread.*;
import org.jbundle.thin.base.message.*;
import org.jbundle.thin.base.screen.message.*;

/**
 *  JobSchedulerApp - This application schedules jobs on the calendar queue for execution
and executes them on the correct dates..
 */
public class JobSchedulerApp extends BaseApplication
{
    /**
     * Default constructor.
     */
    public JobSchedulerApp()
    {
        super();
    }
    /**
     * JobSchedulerApp Method.
     */
    public JobSchedulerApp(Object env, Map<String,Object> properties, Object applet)
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
        if (this.registerUniqueApplication(null, null) != Constants.NORMAL_RETURN)
        {
            this.free();    // Don't start this application (It's already running somewhere)
            return;
        }
        // Okay good. I'm responsible for scheduling and running scheduled tasks.
        final int MAX_TASKS = 1;    // For now, run them sequentially
        this.setTaskScheduler(new PrivateTaskScheduler(this, MAX_TASKS, true));
        
        BaseMessageManager messageManager = RemoteMessageManager.getMessageManager(this);
        String strQueueName = this.getProperty(MessageConstants.QUEUE_NAME);
        if (strQueueName == null)
            strQueueName = MessageConstants.TRX_RECEIVE_QUEUE; // Never
        String strQueueType = this.getProperty(MessageConstants.QUEUE_TYPE);
        if (strQueueType == null)
            strQueueType = MessageConstants.INTRANET_QUEUE; // Never
        BaseMessageReceiver receiver = (BaseMessageReceiver)messageManager.getMessageQueue(strQueueName, strQueueType).getMessageReceiver();
        new BaseMessageListener(receiver)    // Listener added to filter.
        {
            public int handleMessage(BaseMessage message)
            {   // Whenever I'm pinged, restart the JobScanner which rescans the current job list
                Map<String,Object> properties = null;
                if (message instanceof MapMessage)  // Always
                    properties = (Map)message.getData();
                ProcessRunnerTask task = new ProcessRunnerTask(JobSchedulerApp.this, null, properties);
                getTaskScheduler().addTask(task);
                return super.handleMessage(message);
            }
        };
    }

}
