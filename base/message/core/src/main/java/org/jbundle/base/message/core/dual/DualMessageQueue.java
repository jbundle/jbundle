package org.jbundle.base.message.core.dual;

import java.rmi.RemoteException;

import org.jbundle.base.message.core.stack.MessageStack;
import org.jbundle.base.message.core.stack.MessageStackOwner;
import org.jbundle.thin.base.message.BaseMessageManager;
import org.jbundle.thin.base.message.BaseMessageReceiver;
import org.jbundle.thin.base.message.BaseMessageSender;
import org.jbundle.thin.base.message.remote.RemoteMessageQueue;
import org.jbundle.thin.base.remote.RemoteTask;
import org.jbundle.thin.base.util.Application;

/**
 * A MessageQueue manages the JMS connections for this message queue.
 * A Dual Message Queue distributes messages locally within the same JVM.
 * It also sends a copy to the remote server to be distributed. When a message
 * is received from the remote server, it is added to the local queue.
 */
public class DualMessageQueue extends RemoteMessageQueue
    implements MessageStackOwner
{
    /**
     * The message stack.
     */
    protected MessageStack m_messageStack = null;

    /**
     * Default constructor.
     */
    public DualMessageQueue()
    {
        super();
    }
    /**
     * Default constructor.
     */
    public DualMessageQueue(BaseMessageManager manager, String strQueueName, String strQueueType)
    {
        this();
        this.init(manager, strQueueName, strQueueType); // The one and only
    }
    /**
     * Initializes the BaseApplication.
     * Usually you pass the object that wants to use this sesssion.
     * For example, the applet or BaseApplication.
     */
    public void init(BaseMessageManager manager, String strQueueName, String strQueueType)
    {
        super.init(manager, strQueueName, strQueueType);
    }
    /**
     * Free all the resources belonging to this applet. If all applet screens are closed, shut down the applet.
     */
    public void free()
    {
        if (m_messageStack != null)
            m_messageStack.free();
        m_messageStack = null;
        super.free();
    }
    /**
     * Get the message sender.
     */
    public BaseMessageSender createMessageSender()
    {
        RemoteTask server = (RemoteTask)((Application)this.getMessageManager().getApplication()).getRemoteTask(null);
        try   {
            return new DualMessageSender(server, this);
        } catch (RemoteException ex)    {
            ex.printStackTrace();
        }
        return null;
    }
    /**
     * Get the message sender.
     */
    public BaseMessageReceiver createMessageReceiver()
    {
        RemoteTask server = (RemoteTask)((Application)this.getMessageManager().getApplication()).getRemoteTask(null);
        try   {
            return new DualMessageReceiver(server, this);
        } catch (RemoteException ex)    {
            ex.printStackTrace();
        }
        return null;
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
}
