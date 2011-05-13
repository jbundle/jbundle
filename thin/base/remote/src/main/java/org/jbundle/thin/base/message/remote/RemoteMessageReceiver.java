package org.jbundle.thin.base.message.remote;

import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.Map;

import org.jbundle.model.message.Message;
import org.jbundle.model.message.MessageFilter;
import org.jbundle.model.util.Util;
import org.jbundle.thin.base.message.BaseMessage;
import org.jbundle.thin.base.message.BaseMessageFilter;
import org.jbundle.thin.base.message.BaseMessageQueue;
import org.jbundle.thin.base.message.BaseMessageReceiver;
import org.jbundle.thin.base.remote.RemoteReceiveQueue;
import org.jbundle.thin.base.remote.RemoteSession;
import org.jbundle.thin.base.remote.RemoteTask;


/**
 * The MessageReceiver processes incoming messages.
 */
public class RemoteMessageReceiver extends BaseMessageReceiver
{
    /**
     * My connection to the remote receive queue.
     */
    protected RemoteReceiveQueue m_receiveQueue = null;

    /**
     * Constructor.
     */
    public RemoteMessageReceiver()
    {
        super();
    }
    /**
     * Constructor.
     * @param server The remote server.
     * @param baseMessageQueue My parent message queue.
     */
    public RemoteMessageReceiver(RemoteTask server, BaseMessageQueue baseMessageQueue) throws RemoteException
    {
        this();
        this.init(server, baseMessageQueue);
    }
    /**
     * Constructor.
     * @param server The remote server.
     * @param baseMessageQueue My parent message queue.
     */
    public void init(RemoteTask server, BaseMessageQueue baseMessageQueue)
        throws RemoteException
    {
        super.init(baseMessageQueue);
        m_receiveQueue = server.createRemoteReceiveQueue(baseMessageQueue.getQueueName(), baseMessageQueue.getQueueType());
    }
    /**
     * Free the receiver (and the remote session).
     */
    public void free()
    {
        this.stopThisThread();      // This needs to be called first (even though super.free() calls it).
        super.free();               // Free all the filters next (This is also tell the remotesessions to clean up).
        if (m_receiveQueue != null)
        {
            try {
                m_receiveQueue.freeRemoteSession();
            } catch (RemoteException ex)    {
                ex.printStackTrace();
            }
        }
        m_receiveQueue = null;
    }
    /**
     * Stop this thread.
     */
    public void stopThisThread()
    {
        super.stopThisThread(); // Set the flag to stop this thread.    // Nothing else has to be done... The remote session sends an empty message which stops this thread.
    }
    /**
     * Block until a message is received.
     * Hangs on the remote receive message call.
     * @return The next message.
     */
    public Message receiveMessage()
    {
        Message message = null;
        try   {
            message = m_receiveQueue.receiveRemoteMessage();   // Hang until a message comes through
        } catch (RemoteException ex)    {
            ex.printStackTrace();
            return null;    // Remote exception = I'm done!
        }
        if (message instanceof BaseMessage)
            ((BaseMessage)message).setProcessedByServer(true);     // Don't send the message back down.
        return message;
    }
    /**
     * Add this message filter to this receive queue.
     * Also adds a message filter to the remote queue.
     * @param The message filter to add.
     * @return The message filter passed in.
     */
    public void addMessageFilter(MessageFilter messageFilter)
    {
        super.addMessageFilter(messageFilter);
        try   {
            // If at all possible, pass the queue's current session, so the filter is in the proper environment
            if (((BaseMessageFilter)messageFilter).isCreateRemoteFilter())   // Almost always true
            {       // Create the remote version of this filter.
                RemoteSession remoteSession = (RemoteSession)((BaseMessageFilter)messageFilter).getRemoteSession();
                BaseMessageFilter remoteFilter = m_receiveQueue.addRemoteMessageFilter((BaseMessageFilter)messageFilter, remoteSession);
                ((BaseMessageFilter)messageFilter).setRemoteFilterInfo(remoteFilter.getQueueName(), remoteFilter.getQueueType(), remoteFilter.getFilterID(), remoteFilter.getRegistryID());
            }
        } catch (RemoteException ex)    {
            ex.printStackTrace();
        }
    }
    /**
     * Remove this message filter from this queue.
     * Also remove the remote message filter.
     * @param messageFilter The message filter to remove.
     * @param bFreeFilter If true, free this filter.
     * @return True if successful.
     */
    public boolean removeMessageFilter(MessageFilter messageFilter, boolean bFreeFilter)
    {
        boolean bSuccess = false;
        try   {
            if (((BaseMessageFilter)messageFilter).getRemoteFilterID() == null)
                bSuccess = true;    // No remote filter to remove
            else
                bSuccess = m_receiveQueue.removeRemoteMessageFilter((BaseMessageFilter)messageFilter, bFreeFilter);
        } catch (RemoteException ex)    {
            ex.printStackTrace();
        }
        if (!bSuccess)
            Util.getLogger().warning("Remote listener not removed"); // Never
        return super.removeMessageFilter(messageFilter, bFreeFilter);
    }
    /**
     * Update this filter with this new information.
     * Override this to do something if there is a remote version of this filter.
     * @param messageFilter The message filter I am updating.
     * @param properties New filter information (ie, bookmark=345).
     */
    public void setNewFilterProperties(BaseMessageFilter messageFilter, Object[][] mxProperties, Map<String, Object> propFilter)
    {
        super.setNewFilterProperties(messageFilter, mxProperties, propFilter);      // Does nothing.
        try   {
            if (messageFilter.isUpdateRemoteFilter())   // Almost always true
                if (messageFilter.getRemoteFilterID() != null)     // If the remote filter exists
                    m_receiveQueue.updateRemoteFilterProperties(messageFilter, mxProperties, propFilter);
        } catch (RemoteException ex)    {
            ex.printStackTrace();
        }
    }
    /**
     * Do I send this message to the remote server?
     * Remember to check for the filter match.
     * @return true If I do (default).
     */
    public boolean isSendRemoteMessage(BaseMessage message)
    {
        Iterator<BaseMessageFilter> iterator = this.getMessageFilterList().getFilterList(null);    // ALL The filters (not just matches).
        while (iterator.hasNext())
        {
            BaseMessageFilter filter = iterator.next();
            if (!filter.isRemoteFilter())   // Always send down when filter is a remote copy
                if (filter.isSendRemoteMessage(message) == false)
                    return false;      // Don't send this message down
        }
        return true;
    }
}
