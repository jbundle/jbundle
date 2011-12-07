/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.message.app;

import org.jbundle.base.message.core.local.LocalMessageQueue;
import org.jbundle.base.util.BaseApplication;
import org.jbundle.base.util.DBConstants;
import org.jbundle.base.util.DBParams;
import org.jbundle.thin.base.message.BaseMessageQueue;
import org.jbundle.thin.base.message.MessageConstants;
import org.jbundle.thin.base.screen.message.ThinMessageManager;
import org.jbundle.thin.base.util.Application;


/**
 * The MessageManager organizes the message queues.
 */
public class ThickMessageManager extends ThinMessageManager
{
    /**
     * Constuctor.
     */
    public ThickMessageManager()
    {
        super();
    }
    /**
     * Constuctor.
     */
    public ThickMessageManager(Application app)
    {
        this();
        this.init(app);
    }
    /**
     * Constuctor.
     */
    public void init(Application app)
    {
        super.init(app);
    }
    /**
     * Free.
     */
    public void free()
    {
        super.free();
    }
    /**
     * Get this Message Queue (or create one if this name doesn't exist).
     */
    public BaseMessageQueue getMessageQueue(String strQueueName, String strQueueType)
    {
        // Look up the message Queue!
        BaseMessageQueue messageQueue = (BaseMessageQueue)super.getMessageQueue(strQueueName, strQueueType);
        if (messageQueue != null)
            return messageQueue;
        Object server = null;
        boolean bCreateIfNotFound = false;
        if ((this.getApplication().getProperty(DBParams.REMOTE_HOST) != null)
            || (this.getApplication().getProperty(DBParams.REMOTE_APP_NAME) != null))
                bCreateIfNotFound = true;   // If you explicitly specify a remote server, create the server if new.
        if (((BaseApplication)this.getApplication()).getEnvironment().getDefaultApplication() != null)
            if (!MessageConstants.LOCAL_QUEUE.equalsIgnoreCase(strQueueType))
                if (!DBConstants.TRUE.equalsIgnoreCase(this.getApplication().getProperty(DBParams.JMSSERVER)))  // Don't try to get a remote message server if I am the remote message server
                    server = ((BaseApplication)this.getApplication()).getEnvironment().getDefaultApplication().getRemoteTask(null, null, bCreateIfNotFound);
        // This assumes that I only have a server for client processes (Should be correct).
        if (server != null)
            return new org.jbundle.base.message.core.dual.DualMessageQueue(this, strQueueName, strQueueType); // Handle remote and local
        else
            return new LocalMessageQueue(this, strQueueName);
    }
}
