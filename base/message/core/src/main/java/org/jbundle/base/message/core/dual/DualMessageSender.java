package org.jbundle.base.message.core.dual;

import java.rmi.RemoteException;

import org.jbundle.model.message.Message;
import org.jbundle.thin.base.message.BaseMessage;
import org.jbundle.thin.base.message.BaseMessageQueue;
import org.jbundle.thin.base.message.remote.RemoteMessageSender;
import org.jbundle.thin.base.remote.RemoteTask;

/**
 * A Local Message Sender pushes sent messages onto a local (FIFO) stack.
 */
public class DualMessageSender extends RemoteMessageSender
{

    /**
     * Default constructor.
     */
    public DualMessageSender()
    {
        super();
    }
    /**
     * Default constructor.
     */
    public DualMessageSender(RemoteTask server, BaseMessageQueue baseMessageQueue) throws RemoteException
    {
        this();
        this.init(server, baseMessageQueue);    // The one and only
    }
    /**
     * Initializes the BaseApplication.
     * Usually you pass the object that wants to use this sesssion.
     * For example, the applet or BaseApplication.
     */
    public void init(RemoteTask server, BaseMessageQueue messageQueue)
        throws RemoteException
    {
        super.init(server, messageQueue);
    }
    /**
     * Free all the resources belonging to this applet. If all applet screens are closed, shut down the applet.
     */
    public void free()
    {
        super.free();
    }
    /**
     * Send this message to the message queue.
     * @param strCommand Command to perform remotely.
     * @return boolean success.
     */
    public void sendMessage(Message message)
    {
        ((DualMessageQueue)this.getMessageQueue()).getMessageStack().sendMessage(message);
        
        ((BaseMessage)message).setProcessedByClient(true);     // Make sure any server doesn't send this back up.

        super.sendMessage(message);
    }
}
