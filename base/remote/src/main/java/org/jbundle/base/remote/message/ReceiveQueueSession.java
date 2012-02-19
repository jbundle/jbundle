/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.remote.message;

/**
 * @(#)RemoteDatabaseImpl.java  1.16 95/12/14 Don Corley
 *
 * Copyright © 2012 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 *
 */
import java.util.Map;

import org.jbundle.base.db.Record;
import org.jbundle.base.message.core.stack.MessageStack;
import org.jbundle.base.message.core.stack.MessageStackOwner;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.Utility;
import org.jbundle.base.remote.BaseSession;
import org.jbundle.base.remote.db.TaskSession;
import org.jbundle.model.message.Message;
import org.jbundle.model.message.MessageManager;
import org.jbundle.thin.base.message.BaseMessage;
import org.jbundle.thin.base.message.BaseMessageFilter;
import org.jbundle.thin.base.message.BaseMessageManager;
import org.jbundle.thin.base.message.BaseMessageReceiver;
import org.jbundle.thin.base.message.JMessageListener;
import org.jbundle.thin.base.remote.RemoteException;
import org.jbundle.thin.base.remote.RemoteReceiveQueue;
import org.jbundle.thin.base.remote.RemoteSession;
import org.jbundle.thin.base.remote.Unreferenced;
import org.jbundle.thin.base.util.Application;


/**
 * RemoteSessionObject - Implement the Vector database
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class ReceiveQueueSession extends BaseSession
    implements RemoteReceiveQueue, JMessageListener, MessageStackOwner, Unreferenced
{
    private static final long serialVersionUID = 1L;

    /**
     * The local version of the message receiver.
     */
    protected BaseMessageReceiver m_messageReceiver = null;
    /**
     * The message stack.
     */
    protected MessageStack m_messageStack = null;
    /**
     * Last message sent up.
     */
    protected BaseMessage m_messageLastMessage = null;

    /**
     * Constructor
     */
    public ReceiveQueueSession() throws RemoteException
    {
        super();
    }
    /**
     * Constructor
     */
    public ReceiveQueueSession(TaskSession parentSessionObject, BaseMessageReceiver messageReceiver) throws RemoteException
    {
        this();
        this.init(parentSessionObject, null, null);
        m_messageReceiver = messageReceiver;
    }
    /**
     * Constructor
     */
    public void init(BaseSession parentSessionObject, Record record, Map<String, Object> objectID)
    {
        super.init(parentSessionObject, record, objectID);
    }
    /**
     * Free this session.
     */
    public void free()
    {
        BaseMessageManager messageManager = null;
        if (this.getTask() != null) // May have been freed already.
            if (this.getTask().getApplication() != null)
                messageManager = (BaseMessageManager)((Application)this.getTask().getApplication()).getMessageManager(false);
        if (messageManager != null)
            messageManager.freeFiltersWithListener(this);
        if (m_messageStack != null)
            m_messageStack.free();
        m_messageStack = null;
        super.free();
    }
    /**
     * Release the session and its resources.
     */
    public void freeRemoteSession() throws RemoteException
    {
        this.free();
    }
    /**
     * Called by the RMI runtime sometime after the runtime determines that
     * the reference list, the list of clients referencing the remote object,
     * becomes empty.
     * @since JDK1.1
     */
    public void unreferenced()
    {
        Utility.getLogger().info("receive queue unreferenced()");
        this.free();
    }
    /**
     * Process the receive message call.
     * pend(don) NOTE: This will not work as an EJB, because the receiveMessage call blocks.
     * pend(don) Where is an RMI timeout handled? here?
     */
    public Message receiveRemoteMessage() throws RemoteException
    {
        Utility.getLogger().info("EJB receiveMessage (waiting)");
        Message message = this.getMessageStack().receiveMessage();
        Utility.getLogger().info("EJB receiveMessage (returning) " + message);
        return message;
    }
    /**
     * Listen for messages.
     * @param message The message to send back to the client.
     */
    public int handleMessage(BaseMessage message)
    {
        Utility.getLogger().info("RemoteQueue handling message " + message);
        if (message.getProcessedByClientSession() == this.getParentSession())
            return DBConstants.NORMAL_RETURN;     // Do not process this message (the client has already taken care of it).
//?        if (message.getProcessedByClientSession() instanceof RemoteObject)
//?            if (this.getParentSession() instanceof RemoteObject) // Always
//?                if (((RemoteObject)this.getParentSession()).getRef().remoteEquals(((RemoteObject)message.getProcessedByClientSession()).getRef()))
//?                    return DBConstants.NORMAL_RETURN;   // Same client.
        if (message == m_messageLastMessage)
            return DBConstants.NORMAL_RETURN;   // Don't send a message to the same client twice.
        m_messageLastMessage = message;
        this.getMessageStack().sendMessage(message);
        return DBConstants.NORMAL_RETURN;
    }
    /**
     * Given a copy of the client's message filter, set up a remote filter.
     * @param messageFilter The message filter.
     * @param remoteSession The remote session.
     * @return The message filter.
     */
    public BaseMessageFilter addRemoteMessageFilter(BaseMessageFilter messageFilter, RemoteSession remoteSession) throws RemoteException
    {
        BaseMessageFilter remoteFilter = messageFilter; // The actual remote filter.
        messageFilter.setRegistryID(null);  // The needs to be null to add this filter to the receiver.
        messageFilter.setFilterID(null);  // The needs to be null to add this filter to the receiver.

        remoteFilter.setCreateRemoteFilter(true);      // This must be set by the session.
        remoteFilter.setUpdateRemoteFilter(true);      // (This is a transient field you MUST set the initial value)
        Utility.getLogger().info("EJB addRemoteMessageFilter session: " + remoteSession);
        // Give the filter the remote environment
        MessageManager messageManager = ((Application)this.getTask().getApplication()).getMessageManager();
        if (remoteSession == null)
            messageManager.addMessageFilter(remoteFilter); // If there was a remote session, setupRemoteSessionFilter would have added the filter.
        else
        {
            remoteFilter = remoteSession.setupRemoteSessionFilter(remoteFilter); // This has the effect of calling: messageFilter.linkRemoteSession(remoteSession);
            remoteFilter = ((BaseMessageReceiver)messageManager.getMessageQueue(remoteFilter.getQueueName(), remoteFilter.getQueueType()).getMessageReceiver()).getMessageFilter(remoteFilter.getFilterID());  // Must look it up
        }
        remoteFilter.addMessageListener(this);

        messageFilter.setQueueName(remoteFilter.getQueueName());    // Info to pass to client.
        messageFilter.setQueueType(remoteFilter.getQueueType());    // Info to pass to client.
        messageFilter.setFilterID(remoteFilter.getFilterID());      // Info to pass to client.
        messageFilter.setRegistryID(remoteFilter.getRegistryID());
        return messageFilter;   // All client needs if the name/type/and ID of the remote filter. (Don't pass the remoteFilter as it's class may not be accessable to thin).
    }
    /**
     * Remove this listener (called from remote).
     * @param messageFilter The message filter.
     */
    public boolean removeRemoteMessageFilter(BaseMessageFilter messageFilter, boolean bFreeFilter) throws RemoteException
    {
        Utility.getLogger().info("EJB removeMessageFilter filter: " + messageFilter);
        MessageManager messageManager = ((Application)this.getTask().getApplication()).getMessageManager();
        BaseMessageReceiver messageReceiver = (BaseMessageReceiver)messageManager.getMessageQueue(messageFilter.getRemoteFilterQueueName(), messageFilter.getRemoteFilterQueueType()).getMessageReceiver();
        boolean bRemoved = false;
        if (messageReceiver != null)
            bRemoved = messageReceiver.removeMessageFilter(messageFilter.getRemoteFilterID(), bFreeFilter);
        return bRemoved;
    }
    /**
     * Update this filter with this new information.
     * @param messageFilter The message filter I am updating.
     * @param properties New filter information (ie, bookmark=345).
     */
    public void updateRemoteFilterProperties(BaseMessageFilter messageFilter, Object[][] mxProperties, Map<String,Object> propFilter) throws RemoteException
    {
        Utility.getLogger().info("EJB updateRemoteFilter properties: " + mxProperties);
        // Give the filter the remote environment
        MessageManager messageManager = ((Application)this.getTask().getApplication()).getMessageManager();
        messageFilter = ((BaseMessageReceiver)messageManager.getMessageQueue(messageFilter.getQueueName(), messageFilter.getQueueType()).getMessageReceiver()).getMessageFilter(messageFilter.getRemoteFilterID());  // Must look it up
        if (messageFilter != null)  // Always
        {
            if (mxProperties != null)
                messageFilter.setFilterTree(mxProperties);
            if (propFilter != null)
            {
                propFilter = messageFilter.handleUpdateFilterMap(propFilter);   // Update this object's local filter.
                messageFilter.setFilterMap(propFilter);            // Update any remote copy of this.
            }
        }
    }
    /**
     * Get the message stack.
     * Create it if it doesn't exist.
     * @return The message stack.
     */
    public MessageStack getMessageStack()
    {
        if (m_messageStack == null)
            m_messageStack = new MessageStack(this);
        return m_messageStack;
    }
    /**
     * Set the message stack.
     * Create it if it doesn't exist.
     * @return The message stack.
     */
    public void setMessageStack(MessageStack messageStack)
    {
        m_messageStack = messageStack;
    }
    /**
     * Is this listener going to send its messages to a thin client?
     * @return true if yes.
     */
    public boolean isThinListener()
    {
        return true;    // Yes, I am sending messages to clients that could potentially be thin.
    }
}
