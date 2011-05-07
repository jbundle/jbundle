package org.jbundle.thin.base.message.remote;

import java.rmi.RemoteException;

import org.jbundle.thin.base.message.BaseMessage;
import org.jbundle.thin.base.message.BaseMessageManager;
import org.jbundle.thin.base.message.BaseMessageQueue;
import org.jbundle.thin.base.message.BaseMessageReceiver;
import org.jbundle.thin.base.message.BaseMessageSender;
import org.jbundle.thin.base.remote.RemoteTask;
import org.jbundle.thin.base.util.Application;


/**
 * A MessageQueue manages the message connections for this message queue.
 * A Remote Message Queue uses a proxy (server) to handle message distribution.
 * A message is sent to the remote session, the receive queue hangs on a remote
 * call waiting for a message to be received.
 */
public class RemoteMessageQueue extends BaseMessageQueue
{

    /**
     * Default constructor.
     */
    public RemoteMessageQueue()
    {
        super();
    }
    /**
     * Default constructor.
     * @param manager My parent message manager.
     * @param strQueueName This queue's name.
     * @param strQueueType This queue's type (LOCAL/JMS).
     */
    public RemoteMessageQueue(BaseMessageManager manager, String strQueueName, String strQueueType)
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
        super.init(manager, strQueueName, strQueueType);
    }
    /**
     * Free this object.
     */
    public void free()
    {
        super.free();
    }
    /**
     * Create a new (Remote) message sender.
     * @return The message sender.
     */
    public BaseMessageSender createMessageSender()
    {
        RemoteTask server = (RemoteTask)((Application)this.getMessageManager().getApplication()).getRemoteTask(null);
        try   {
            return new RemoteMessageSender(server, this);
        } catch (RemoteException ex)    {
            ex.printStackTrace();
        }
        return null;
    }
    /**
     * Create a new (Remote) message receiver.
     * @return The message receiver.
     */
    public BaseMessageReceiver createMessageReceiver()
    {
        RemoteTask server = (RemoteTask)((Application)this.getMessageManager().getApplication()).getRemoteTask(null);
        try   {
            return new RemoteMessageReceiver(server, this);
        } catch (RemoteException ex)    {
            ex.printStackTrace();
        }
        return null;
    }
    /**
     * Do I send this message to the remote server?
     * @return true If I do (default).
     */
    public boolean isSendRemoteMessage(BaseMessage message)
    {
        return ((RemoteMessageReceiver)this.getMessageReceiver()).isSendRemoteMessage(message);
    }
}
