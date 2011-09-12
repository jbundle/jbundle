/*
 *  @(#)MessageStormProcess.
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.app.test.test.screen.lock;

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
import org.jbundle.base.thread.*;
import org.jbundle.thin.base.message.*;
import org.jbundle.main.msg.db.*;
import org.jbundle.base.message.trx.message.*;

/**
 *  MessageStormProcess - .
 */
public class MessageStormProcess extends BaseProcess
{
    /**
     * Default constructor.
     */
    public MessageStormProcess()
    {
        super();
    }
    /**
     * Constructor.
     */
    public MessageStormProcess(RecordOwnerParent taskParent, Record recordMain, Map<String,Object> properties)
    {
        this();
        this.init(taskParent, recordMain, properties);
    }
    /**
     * Initialize class fields.
     */
    public void init(RecordOwnerParent taskParent, Record recordMain, Map<String, Object> properties)
    {
        super.init(taskParent, recordMain, properties);
    }
    /**
     * Run Method.
     */
    public void run()
    {
        int MULTIPLIER = (int)(0.250 * TestScreenLockStorm.WAIT_MULTIPLIER);
        for (int i = TestScreenLockStorm.COUNT; i > 0; i--)
        {
            this.startProcess(TestUpdateProcess.class.getName(), RunProcessInField.LOCAL_PROCESS, Integer.toString(i));
            synchronized(this)
            {
                try {
                    this.wait(i * MULTIPLIER); // Start with 2.5 seconds, and drop
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    /**
     * StartProcess Method.
     */
    public void startProcess(String strProcessName, String strProcessType, String strProcessCount)
    {
        Map<String,Object> properties = new HashMap<String,Object>();
        properties.put(DBParams.PROCESS, strProcessName);
        properties.put("count", strProcessCount);
        Application app = (Application)this.getTask().getApplication();
        String strQueueName = MessageConstants.TRX_SEND_QUEUE;
        String strQueueType = MessageConstants.INTRANET_QUEUE;
        BaseMessage message = new MapMessage(new TrxMessageHeader(strQueueName, strQueueType, properties), properties);
        String strProcess = Utility.propertiesToURL(null, properties);
        
        if (RunProcessInField.REMOTE_PROCESS.equalsIgnoreCase(strProcessType))
        {
            app.getMessageManager().sendMessage(message);
        }
        else if (RunProcessInField.LOCAL_PROCESS.equalsIgnoreCase(strProcessType))
        {
            app.getTaskScheduler().addTask(new ProcessRunnerTask(app, strProcess, null));
        }
        else // LOCAL
        {
            new ProcessRunnerTask(app, strProcess, null).run();
        }
    }

}
