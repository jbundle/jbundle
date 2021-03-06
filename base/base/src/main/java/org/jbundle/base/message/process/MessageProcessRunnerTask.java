/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.message.process;

/**
 * @(#)DBServlet.java 0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import java.util.HashMap;
import java.util.Map;

import org.jbundle.base.model.DBParams;
import org.jbundle.base.thread.BaseProcess;
import org.jbundle.base.thread.ProcessRunnerTask;
import org.jbundle.model.App;
import org.jbundle.thin.base.message.BaseMessage;


/**
 * A MessageProcessRunner waits around for the message. When a message comes through on this
 * queue, the correct task is created and run. If another message is waiting, the process is run
 * again. When the last message is received, the process is freed.
 */
public class MessageProcessRunnerTask extends ProcessRunnerTask
{
    /**
     * A temporary holder for the message between the handleMessage and the run method.
     */
    protected BaseMessage m_message = null;

    /**
     * Constructor.
     */
    public MessageProcessRunnerTask()
    {
        super();
    }
    /**
     * Constructor.
     */
    public MessageProcessRunnerTask(App application, String strParams, Map<String,Object> properties)
    {
        this();
        this.init(application, strParams, properties);
    }
    /**
     * Constructor.
     */
    public void init(App application, String strParams, Map<String, Object> properties)
    {
        super.init(application, strParams, properties);
    }
    /**
     * Constructor.
     */
    public void free()
    {
        super.free();
        // Add code here to unblock this task!
    }
    /**
     * Set the message for this process runner.
     */
    public void setMessage(BaseMessage message)
    {
        m_message = message;
    }
    /**
     * Get the name of this process and run it.
     */
    public void runTask()
    {
        super.runTask();    // This typically does everything (starts the messageprocessor)
    }
    /**
     * Run this process.
     * @param job The process to run.
     * @param properties The properties to pass to this process.
     */
    public void runProcess(BaseProcess job, Map<String,Object> properties)
    {
        if (properties == null)
            properties = new HashMap<String,Object>();
        Map<String,Object> propMessage = m_message.getMessageHeader().getProperties();
        if (propMessage != null)
            properties.putAll(propMessage);
        properties.put(DBParams.MESSAGE, m_message);
        
        job.init(this, null, properties);

        job.run();
        job.free();
    }
}

