package org.jbundle.base.message.core.dual;

import java.rmi.RemoteException;

import org.jbundle.base.message.core.tree.TreeMessageFilterList;
import org.jbundle.base.util.BaseApplication;
import org.jbundle.base.util.DBParams;
import org.jbundle.thin.base.message.BaseMessage;
import org.jbundle.thin.base.message.BaseMessageFilter;
import org.jbundle.thin.base.message.BaseMessageQueue;
import org.jbundle.thin.base.message.MessageReceiverFilterList;
import org.jbundle.thin.base.message.remote.RemoteMessageReceiver;
import org.jbundle.thin.base.remote.RemoteTask;
import org.jbundle.thin.base.util.Application;


/**
 * A Local Message Receiver pops messages off a local message (FIFO) stack.
 */
public class DualMessageReceiver extends RemoteMessageReceiver
{
    /**
     * The receive queue thread.
     */
    public Thread m_receiveQueueThread = null;

    /**
     * Default constructor.
     */
    public DualMessageReceiver()
    {
        super();
    }
    /**
     * Default constructor.
     */
    public DualMessageReceiver(RemoteTask server, BaseMessageQueue baseMessageQueue) throws RemoteException
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
        m_receiveQueueThread = new Thread(new ReceiveQueueWorker(), "ReceiveQueueWorker");
        m_receiveQueueThread.start();
    }
    /**
     * Free all the resources belonging to this applet. If all applet screens are closed, shut down the applet.
     */
    public void free()
    {
        super.free();
    }
    /**
     * Stop this thread.
     */
    public void stopThisThread()
    {
        if (m_receiveQueueThread != null)
            m_receiveQueueThread.interrupt();   // This will send a null message and stop this thread.
        m_receiveQueueThread = null;
        super.stopThisThread(); // Set the flag to stop this thread.    // Nothing else has to be done... The remote session sends an empty message which stops this thread.
    }
    /**
     * Process the receive message call.
     * Do NOT receive from the remote server... DO receive from the local queue.
     * The worker thread handle remote receives and adds them to the local queue.
     * @return The next message on the queue (hangs until one is available).
     */
    public BaseMessage receiveMessage()
    {
        DualMessageQueue baseMessageQueue = (DualMessageQueue)this.getMessageQueue();
        if (baseMessageQueue == null)
            return null;    // In free
        return baseMessageQueue.getMessageStack().receiveMessage();
    }
    /**
     * Get the message filter list.
     * Create a new filter list the first time.
     * @return The filter list.
     */
    public MessageReceiverFilterList getMessageFilterList()
    {
        if (m_filterList == null)
        {
            String strFilterType = null;
            Application app = (Application)this.getMessageQueue().getMessageManager().getApplication();
            if (app instanceof BaseApplication)
                strFilterType = ((BaseApplication)app).getEnvironment().getProperty(DBParams.MESSAGE_FILTER);
            if (DBParams.TREE_FILTER.equals(strFilterType))
                m_filterList = new TreeMessageFilterList(this);
        }
        return super.getMessageFilterList();
    }
    /**
     * Add this message filter to this receive queue.
     * Also adds a message filter to the remote queue.
     * @param The message filter to add.
     * @return The message filter passed in.
     */
    public void addMessageFilter(BaseMessageFilter messageFilter)
    {
        boolean bThinTarget = messageFilter.isThinTarget();
        messageFilter.setThinTarget(false);  // If this is replicated to a server, the server needs to know that I am thick.
        super.addMessageFilter(messageFilter);
        messageFilter.setThinTarget(bThinTarget);
    }
    /**
     * This is a special worker thread that pulls messages off the remote server and sticks them on the local queue.
     */
    class ReceiveQueueWorker extends Object
        implements Runnable
    {
        /**
         * Constructor.
         */
        public ReceiveQueueWorker()
        {
            super();
        }
        /**
         * Run this task thats only job is to pull messages from the server and place them on the local queue.
         */
        public void run()
        {
            while (m_receiveQueueThread != null)
            {
                BaseMessage message = this.receiveMessage();        // Hang until a message comes through
                if ((m_receiveQueueThread == null) || (message == null))
                    return;      // End of processing, stop this thread.
                getMessageQueue().getMessageSender().sendMessage(message);  // Add to the queue to process
            }
        }
        /**
         * Block until a message is received.
         * Hangs on the remote receive message call.
         * @return The next message.
         */
        public BaseMessage receiveMessage()
        {
            BaseMessage message = null;
            try   {
                message = m_receiveQueue.receiveRemoteMessage();   // Hang until a message comes through
            } catch (RemoteException ex)    {
                ex.printStackTrace();
                return null;    // Remote exception = I'm done
            }
            if (message != null)
                message.setProcessedByServer(true);     // Don't send the message back down.
            return message;
        }
    }
}
