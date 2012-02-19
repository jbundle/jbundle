/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.message;

import java.util.Hashtable;
import java.util.Map;

import org.jbundle.model.App;
import org.jbundle.model.message.Message;
import org.jbundle.model.message.MessageFilter;
import org.jbundle.model.message.MessageManager;
import org.jbundle.model.message.MessageQueue;
import org.jbundle.model.message.MessageReceiver;
import org.jbundle.model.message.MessageSender;
import org.jbundle.model.util.Constant;
import org.jbundle.model.util.Param;
import org.jbundle.thin.base.thread.AutoTask;


/**
 * The MessageManager organizes the message queues.
 * NOTE: This implements the Task interface (Note: app is already passed in).
 */
public class BaseMessageManager extends AutoTask
	implements MessageManager
{
    /**
     * My Message queues.
     */
    protected Hashtable<String,BaseMessageQueue> m_messageMap = null;

    /**
     * Constructor.
     */
    public BaseMessageManager()
    {
        super();
    }
    /**
     * Constructor.
     * @param application The parent application.
     * @param strParams The task properties.
     */
    public BaseMessageManager(App application, String strParams, Map<String, Object> properties)
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
        if (this.getProperty(Param.REMOTE_APP_NAME) == null)
            this.setProperty(Param.REMOTE_APP_NAME, "org.jbundle.main.msg.app.MessageServerActivator"/*Param.REMOTE_MESSAGE_APP*/);
        m_messageMap = new Hashtable<String,BaseMessageQueue>();
    }
    /**
     * Free this message manager.
     */
    public void free()
    {
        if (m_messageMap != null)
        {
            for (BaseMessageQueue messageQueue : m_messageMap.values())
            {
                if (messageQueue != null)
                {       // Don't worry about removing these, since you are removing them all.
                    messageQueue.setMessageManager(null);   // Make sure it doesn't try to remove itself again
                    messageQueue.free();
                }                
            }
        }
        m_messageMap = null;
        super.free();
    }
    /**
     * Remove all the filters that have this as a listener.
     * @param listener Filters with this listener will be removed.
     */
    public void freeFiltersWithListener(JMessageListener listener)
    {
        if (m_messageMap != null)
        {
            for (BaseMessageQueue messageQueue : m_messageMap.values())
            {
                if (messageQueue != null)
                    messageQueue.freeFiltersWithListener(listener);
            }
        }
    }
    /**
     * Remove all the filters that have this as a listener.
     * @param listener Filters with this listener will be removed.
     */
    public void freeFiltersWithSource(Object objSource)
    {
        if (m_messageMap != null)
        {
            for (BaseMessageQueue messageQueue : m_messageMap.values())
            {
                if (messageQueue != null)
                    messageQueue.freeFiltersWithSource(objSource);
            }
        }
    }
    /**
     * Remove all the filters that have this as a listener.
     * @param listener Filters with this listener will be removed.
     */
    public void freeListenersWithSource(Object objSource)
    {
        if (m_messageMap != null)
        {
            for (BaseMessageQueue messageQueue : m_messageMap.values())
            {
                if (messageQueue != null)
                    messageQueue.freeListenersWithSource(objSource);
            }
        }
    }
    /**
     * Get this Message Queue (or create one if this name doesn't exist).
     * Override this to supply the correct message queue if it hasn't already been built.
     * @param strQueueName The queue name to lookup.
     * @param strQueueType The queue type if this queue needs to be created.
     * @return The message queue.
     */
    public MessageQueue getMessageQueue(String strQueueName, String strQueueType)
    {   // Look up the message Queue!
        return (BaseMessageQueue)m_messageMap.get(strQueueName);
    }
    /**
     * Add this message queue to the map.
     * Note: Don't call this, it is called from the MessageQueue constructor.
     * @param messageQueue The message queue to add to my list.
     */
    public void addMessageQueue(BaseMessageQueue messageQueue)
    {
        m_messageMap.put(messageQueue.getQueueName(), messageQueue);
    }
    /**
     * Remove this message queue from the map.
     * Note: Don't call this, it is called from the MessageQueue constructor.
     * @param messageQueue The message queue to remove from my list.
     */
    public void removeMessageQueue(BaseMessageQueue messageQueue)
    {
        m_messageMap.remove(messageQueue.getQueueName());
    }
    /**
     * Add this message filter to the appropriate queue.
     * The message filter contains the queue name and type and a listener to send the message to.
     * @param messageFilter The message filter to add.
     * @return An error code.
     */
    public int addMessageFilter(MessageFilter messageFilter)
    {
        MessageReceiver receiver = this.getMessageQueue(messageFilter.getQueueName(), messageFilter.getQueueType()).getMessageReceiver();
        receiver.addMessageFilter(messageFilter);
        return Constant.NORMAL_RETURN;
    }
    /**
     * Send this message to the appropriate queue.
     * The message's message header has the queue name and type.
     * @param The message to send.
     * @return An error code.
     */
    public int sendMessage(Message message)
    {
        BaseMessageHeader messageHeader = ((BaseMessage)message).getMessageHeader();
        String strQueueType = messageHeader.getQueueType();
        String strQueueName = messageHeader.getQueueName();
        MessageSender sender = this.getMessageQueue(strQueueName, strQueueType).getMessageSender();
        if (sender != null)
            sender.sendMessage(message);
        else
            return Constant.ERROR_RETURN;  // Queue doesn't exist
        return Constant.NORMAL_RETURN;
    }
}
