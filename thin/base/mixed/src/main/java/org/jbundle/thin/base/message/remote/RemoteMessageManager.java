package org.jbundle.thin.base.message.remote;

import org.jbundle.thin.base.message.BaseMessageManager;
import org.jbundle.thin.base.message.BaseMessageQueue;
import org.jbundle.thin.base.message.ThinMessageManager;
import org.jbundle.thin.base.util.Application;

/**
 * A thin implementation of the message manager to access the remote message queue.
 */
public class RemoteMessageManager extends ThinMessageManager
{
    /**
     * Constuctor.
     */
    public RemoteMessageManager()
    {
        super();
    }
    /**
     * Constuctor.
     * @param app My parent application.
     */
    public RemoteMessageManager(Application app)
    {
        this();
        this.init(app);
    }
    /**
     * Constuctor.
     * @param app My parent application.
     */
    public void init(Application app)
    {
        super.init(app);
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
    public static BaseMessageManager getMessageManager(Application app)
    {
        return RemoteMessageManager.getMessageManager(app, true);
    }
    public static BaseMessageManager getMessageManager(Application app, boolean bCreateIfNone)
    {
        if (m_messageManager == null)
            if (bCreateIfNone)
        {
            synchronized (app)
            {
                m_messageManager = new RemoteMessageManager(app);
            }
        }
        return m_messageManager;
    }
}
