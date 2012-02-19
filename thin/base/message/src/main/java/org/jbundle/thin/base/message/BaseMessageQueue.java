/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.message;

import org.jbundle.model.message.MessageQueue;


/**
 * The message queue handles receiving and distributing messages for this queue name and type.
 */
public abstract class BaseMessageQueue extends Object
	implements MessageQueue
{
    /**
     * My parent message manager.
     */
    protected BaseMessageManager m_manager = null;
    /**
     * My queue name.
     */
    protected String m_strQueueName = null;
    /**
     * The queue's type (LOCAL/JMS).
     */
    protected String m_strQueueType = null;
    /**
     * The sender for this queue.
     */
    protected BaseMessageSender m_sender = null;
    /**
     * The receiver for this queue.
     */
    protected BaseMessageReceiver m_receiver = null;

    /**
     * Default constructor.
     */
    public BaseMessageQueue()
    {
        super();
    }
    /**
     * Default constructor.
     * @param manager My parent message manager.
     * @param strQueueName This queue's name.
     * @param strQueueType This queue's type (LOCAL/JMS).
     */
    public BaseMessageQueue(BaseMessageManager manager, String strQueueName, String strQueueType)
    {
        this();
        this.init(manager, strQueueName, strQueueType);   // The one and only
    }
    /**
     * Initializes this message queue.
     * Adds this queue to the message manager.
     * @param manager My parent message manager.
     * @param strQueueName This queue's name.
     * @param strQueueType This queue's type (LOCAL/JMS).
     */
    public void init(BaseMessageManager manager, String strQueueName, String strQueueType)
    {
        m_manager = manager;
        m_strQueueName = strQueueName;
        m_strQueueType = strQueueType;
        
        if (m_manager != null)
            m_manager.addMessageQueue(this);
    }
    /**
     * Free all the resources belonging to this object.
     */
    public void free()
    {
        if (m_receiver != null)
            m_receiver.free();
        m_receiver = null;
        if (m_sender != null)
            m_sender.free();
        m_sender = null;

        if (m_manager != null)
            m_manager.removeMessageQueue(this);

        m_manager = null;
        m_strQueueName = null;
        m_strQueueType = null;
    }
    /**
     * Remove all the filters that have this as a listener.
     * @param listener Filters with this listener will be removed.
     */
    public void freeFiltersWithListener(JMessageListener listener)
    {
        if (m_receiver != null)
            m_receiver.freeFiltersWithListener(listener);
    }
    /**
     * Remove all the filters that have this as a listener.
     * @param listener Filters with this listener will be removed (pass null to free them all).
     */
    public void freeFiltersWithSource(Object objSource)
    {
        if (m_receiver != null)
            m_receiver.freeFiltersWithSource(objSource);
    }
    /**
     * Remove all the filters that have this as a listener.
     * @param listener Filters with this listener will be removed (pass null to free them all).
     */
    public void freeListenersWithSource(Object objSource)
    {
        if (m_receiver != null)
            m_receiver.freeListenersWithSource(objSource);
    }
    /**
     * Get the queue name.
     * @return The queue name.
     */
    public String getQueueName()
    {
        return m_strQueueName;
    }
    /**
     * Get the queue type.
     * @return The queue type.
     */
    public String getQueueType()
    {
        return m_strQueueType;
    }
    /**
     * Get the parent message manager.
     * @return The message manager.
     */
    public BaseMessageManager getMessageManager()
    {
        return m_manager;
    }
    /**
     * Get the parent message manager.
     * @return The message manager.
     */
    public void setMessageManager(BaseMessageManager manager)
    {
        m_manager = manager;
    }
    /**
     * Get the message sender.
     * Or create it if it doesn't exist.
     * @return The message sender.
     */
    public BaseMessageSender getMessageSender()
    {
        if (m_sender == null)
            m_sender = this.createMessageSender();
        return m_sender;
    }
    /**
     * Create the message sender.
     * Override this abstract method.
     * @return The new message sender.
     */
    public abstract BaseMessageSender createMessageSender();
    /**
     * Get the message receiver.
     * Create it if it doesn't exist.
     * @return The message receiver.
     */
    public BaseMessageReceiver getMessageReceiver()
    {
        if (m_receiver == null)
        {
            m_receiver = this.createMessageReceiver();
            if (m_receiver != null)
                new Thread(m_receiver, "MessageReceiver").start();
        }
        return m_receiver;
    }
    /**
     * Create a new message receiver.
     * @return The new message receiver.
     */
    public abstract BaseMessageReceiver createMessageReceiver();
}
