/**
 * @(#)JobSchedulerApp.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.main.schedule.app;

import java.util.Map;

import org.jbundle.base.thread.ProcessRunnerTask;
import org.jbundle.base.util.BaseApplication;
import org.jbundle.model.message.MessageManager;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.message.BaseMessage;
import org.jbundle.thin.base.message.BaseMessageListener;
import org.jbundle.thin.base.message.BaseMessageReceiver;
import org.jbundle.thin.base.message.MapMessage;
import org.jbundle.thin.base.message.MessageConstants;
import org.jbundle.thin.base.screen.message.RemoteMessageManager;
import org.jbundle.thin.base.thread.PrivateTaskScheduler;

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
        
        MessageManager messageManager = RemoteMessageManager.getMessageManager(this);
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
