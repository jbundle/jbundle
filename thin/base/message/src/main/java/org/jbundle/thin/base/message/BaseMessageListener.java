/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.message;

import org.jbundle.model.Freeable;
import org.jbundle.model.util.Constant;

/**
 * A BaseMessageListener is an abstract class for handling incoming messages.
 */
public class BaseMessageListener extends Object
    implements JMessageListener, Freeable
{
    /**
     * Keep track of any filters that have me as a listener.
     */
    protected MessageListenerFilterList m_messageFilterList = null;

    /**
     * Constructor.
     */
    public BaseMessageListener()
    {
        super();
    }
    /**
     * Constructor.
     * Note: A default filter is created and added to the receiver to feed messages to this (new) listener.
     * @param messageReceiver The message receiver that this listener is added to.
     */
    public BaseMessageListener(BaseMessageReceiver messageReceiver)
    {
        this();
        this.init(messageReceiver, null);
    }
    /**
     * Constructor.
     * Note: This listener is automatically added to this (new) filter.
     * @param messageFilter The filter for this message listener.
     */
    public BaseMessageListener(BaseMessageFilter messageFilter)
    {
        this();
        this.init(null, messageFilter);
    }
    /**
     * Constructor.
     * @param messageReceiver The message receiver that this listener is added to.
     * @param messageFilter The filter for this message listener.
     */
    public void init(BaseMessageReceiver messageReceiver, BaseMessageFilter messageFilter)
    {
        if (messageFilter == null)
        {
            if (messageReceiver != null)
            {
                messageFilter = messageReceiver.createDefaultFilter(this, true);    // Add filter to handler
                return; // All done. (createDefaultFilter adds the message listener to the new filter)
            }
        }
        if (messageFilter != null)
            messageFilter.addMessageListener(this);     // This will also add the message filter to my list.
    }
    /**
     * Free.
     */
    public void free()
    {
        if (m_messageFilterList != null)
            m_messageFilterList.free(); // This will remove me from all the message filters.
        m_messageFilterList = null;
    }
    /**
     * A record with this datasource handle changed, notify any behaviors that are checking.
     * NOTE: Be very careful as this code is running in an independent thread
     * (synchronize to the task before calling record calls).
     * NOTE: For now, you are only notified of the main record changes.
     * @param message The message to handle.
     * @return The error code.
     */
    public int handleMessage(BaseMessage message)
    {
        return Constant.NORMAL_RETURN; // Override this to process change
    }
    /**
     * Add this message filter to my list.
     * NOTE: DO NOT call this method, this method is auto-called when you do filter.addMessageListener(listener);
     * @param messageFilter The message filter to add.
     */
    public void addListenerMessageFilter(BaseMessageFilter messageFilter)
    {
        if (m_messageFilterList == null)
            m_messageFilterList = new MessageListenerFilterList(this);
        m_messageFilterList.addMessageFilter(messageFilter);
    }
    /**
     * Have the message listener remove this filter from its list.
     * NOTE: DO NOT call this method, this method is auto-called when you do filter.addMessageListener(listener);
     * @param messageFilter The message filter to remove.
     */
    public void removeListenerMessageFilter(BaseMessageFilter messageFilter)
    {
        if (m_messageFilterList != null)
            m_messageFilterList.removeMessageFilter(messageFilter);
    }
    /**
     * Have the message listener remove this filter from its list.
     * NOTE: DO NOT call this method, this method is auto-called when you do filter.addMessageListener(listener);
     * @param messageFilter The message filter to remove.
     */
    public BaseMessageFilter getListenerMessageFilter(int iIndex)
    {
        if (m_messageFilterList != null)
            return m_messageFilterList.getMessageFilter(iIndex);
        return null;
    }
    /**
     * Is this listener going to send its messages to a thin client?
     * @return true if yes.
     */
    public boolean isThinListener()
    {
        return false;
    }
}
