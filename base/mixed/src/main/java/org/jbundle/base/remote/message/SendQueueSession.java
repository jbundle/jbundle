package org.jbundle.base.remote.message;

/**
 * @(#)RemoteDatabaseImpl.java  1.16 95/12/14 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 *
 */
import java.rmi.RemoteException;
import java.rmi.server.Unreferenced;

import org.jbundle.base.db.Record;
import org.jbundle.base.remote.BaseSession;
import org.jbundle.base.remote.db.TaskSession;
import org.jbundle.base.util.Utility;
import org.jbundle.model.message.Message;
import org.jbundle.thin.base.message.BaseMessage;
import org.jbundle.thin.base.message.BaseMessageSender;
import org.jbundle.thin.base.remote.RemoteSendQueue;


/**
 * RemoteSessionObject - Implement the Vector database
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class SendQueueSession extends BaseSession
    implements RemoteSendQueue, Unreferenced
{
    private static final long serialVersionUID = 1L;

    /**
     * The server's message queue for this session.
     */
    protected BaseMessageSender m_messageSender = null;

    /**
     * Constructor
     */
    public SendQueueSession() throws RemoteException
    {
        super();
    }
    /**
     * Constructor
     */
    public SendQueueSession(TaskSession parentSessionObject, BaseMessageSender messageSender) throws RemoteException
    {
        this();
        this.init(parentSessionObject, null, null);
        m_messageSender = messageSender;
    }
    /**
     * Constructor
     */
    public void init(BaseSession parentSessionObject, Record record, Object objectID)
    {
        super.init(parentSessionObject, record, objectID);
    }
    /**
     * Free this session.
     */
    public void free()
    {
        super.free();
        m_messageSender = null;
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
        Utility.getLogger().info("send queue unreferenced()");
        this.free();
    }
    /**
     * Send this message to the message queue.
     * @param strCommand Command to perform remotely.
     * @return boolean success.
     */
    public void sendMessage(Message message) throws RemoteException
    {
        Utility.getLogger().info("EJB sendMessage: " + message);
        if (((BaseMessage)message).isProcessedByClient())
            ((BaseMessage)message).setProcessedByClientSession(this.getParentSession());
        m_messageSender.sendMessage(message);
    }
}
