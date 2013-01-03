/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.util.message;

import java.util.Map;

import org.jbundle.model.App;
import org.jbundle.model.message.MessageManager;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.Params;
import org.jbundle.thin.base.message.BaseMessageManager;
import org.jbundle.thin.base.message.BaseMessageQueue;
import org.jbundle.thin.base.message.remote.RemoteMessageQueue;

/**
 * A thin implementation of the message manager to access the remote message queue.
 */
public class RemoteMessageManager extends ThinMessageManager
{
    /**
     * Constructor.
     */
    public RemoteMessageManager()
    {
        super();
    }
    /**
     * Constructor.
     * @param application The parent application.
     * @param strParams The task properties.
     */
    public RemoteMessageManager(App application, String strParams, Map<String, Object> properties)
    {
        this();
        this.init(application, strParams, properties);
    }
    /**
     * Constructor.
     * @param application The parent application.
     * @param strParams The task properties.
     */
    public void init(App application, String strParams, Map<String, Object> properties)
    {
        super.init(application, strParams, properties);
    }
    /**
     * Free this message manager.
     */
    public void free()
    {
        super.free();
    }
    /**
     * Get this Message Queue (or create one if this name doesn't exist).
     * Creates a RemoteMessageQueue if this queue doesn't exist.
     * @param strQueueName The queue name to lookup.
     * @param strQueueType The queue type if this queue needs to be created.
     * @return The message queue.
     */
    public BaseMessageQueue getMessageQueue(String strQueueName, String strQueueType)
    {
        // Look up the message Queue!
        BaseMessageQueue messageQueue = (BaseMessageQueue)super.getMessageQueue(strQueueName, strQueueType);
        if (messageQueue != null)
            return messageQueue;
        return new RemoteMessageQueue(this, strQueueName, strQueueType);
    }
    /**
     * For thin apps, the one and only message manager (for the getMessageManager call).
     */
    protected static BaseMessageManager m_messageManager = null;
    /**
     * Get the remote message manager.
     * NOTE: DO NOT USE THIS except in thin applications!
     * @param app My parent app.
     * @return The base message manager.
     */
    public static MessageManager getMessageManager(App app)
    {
        return RemoteMessageManager.getMessageManager(app, null, null, true);
    }
    public static MessageManager getMessageManager(App app, String strParams, Map<String, Object> properties, boolean bCreateIfNone)
    {
        if (m_messageManager == null)
            if (bCreateIfNone)
        {
            synchronized (app)
            {
                if ((app == null) || ((!Constants.TRUE.equalsIgnoreCase(app.getProperty(Params.MESSAGE_SERVER)))) && (!"local".equalsIgnoreCase(app.getProperty(Params.MESSAGE_SERVER))))
                    m_messageManager = new RemoteMessageManager(app, strParams, properties);    // (remote)
                else
                    m_messageManager = new ThinMessageManager(app, strParams, properties);      // local
            }
        }
        return m_messageManager;
    }
}
