/*
 * BaseMessageSender.java
 *
 * Created on June 13, 2000, 3:29 AM
 
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.message.remote;

import org.jbundle.model.message.Message;
import org.jbundle.thin.base.message.BaseMessage;
import org.jbundle.thin.base.message.BaseMessageQueue;
import org.jbundle.thin.base.message.BaseMessageSender;
import org.jbundle.thin.base.message.MessageConstants;
import org.jbundle.thin.base.remote.RemoteException;
import org.jbundle.thin.base.remote.RemoteSendQueue;
import org.jbundle.thin.base.remote.RemoteTask;


/** 
 * Base class for sending messages to an RMS server.
 * @author  Administrator
 * @version 1.0.0
 */
public class RemoteMessageSender extends BaseMessageSender
{
    /**
     * The remote send queue.
     */
    protected RemoteSendQueue m_sendQueue = null;

    /**
     * Creates new BaseMessageSender.
     */
    public RemoteMessageSender()
    {
        super();
    }
    /**
     * Creates new MessageSender.
     * @param server The remote server.
     * @param baseMessageQueue My parent message queue.
     */
    public RemoteMessageSender(RemoteTask server, BaseMessageQueue baseMessageQueue)
        throws RemoteException
    {
        this();
        this.init(server, baseMessageQueue);
    }
    /**
     * Creates new MessageSender.
     * @param server The remote server.
     * @param baseMessageQueue My parent message queue.
     */
    public void init(RemoteTask server, BaseMessageQueue baseMessageQueue)
        throws RemoteException
    {
        super.init(baseMessageQueue);
        m_sendQueue = server.createRemoteSendQueue(baseMessageQueue.getQueueName(), baseMessageQueue.getQueueType());
    }
    /**
     * Free the receiver (and the remote session).
     */
    public void free()
    {
        if (m_sendQueue != null)
        {
            try {
                m_sendQueue.freeRemoteSession();
            } catch (RemoteException ex)    {
                ex.printStackTrace();
            }
        }
        m_sendQueue = null;
        super.free();
    }
    /**
     * Send this message to the remote send queue (who will pass it on).
     * @param message The message to send.
     */
    public void sendMessage(Message message)
    {
        try   {
            if (((RemoteMessageQueue)this.getMessageQueue()).isSendRemoteMessage((BaseMessage)message) == false)
                return;
            if (((BaseMessage)message).isProcessedByServer())
                return;     // Don't send back down (the server already processed it).
            if (((BaseMessage)message).isConsumed())
                return;     // Don't send if already handled.
            if (MessageConstants.LOCAL_QUEUE.equalsIgnoreCase(((BaseMessage)message).getMessageHeader().getQueueType()))
                return;     // Local queue only
            synchronized (m_sendQueue)
            {   // In case this is called from another task
                m_sendQueue.sendMessage(message);
            }
        } catch (RemoteException ex)    {
            ex.printStackTrace();
        }
    }
}
