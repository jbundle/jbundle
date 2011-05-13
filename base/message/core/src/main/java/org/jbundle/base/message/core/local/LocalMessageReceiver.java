package org.jbundle.base.message.core.local;

import org.jbundle.base.message.core.tree.TreeMessageFilterList;
import org.jbundle.model.App;
import org.jbundle.model.message.Message;
import org.jbundle.thin.base.message.BaseMessageQueue;
import org.jbundle.thin.base.message.BaseMessageReceiver;
import org.jbundle.thin.base.message.MapMessage;
import org.jbundle.thin.base.message.MessageConstants;
import org.jbundle.thin.base.message.MessageReceiverFilterList;

/**
 * A Local Message Receiver pops messages off a local message (FIFO) stack.
 */
public class LocalMessageReceiver extends BaseMessageReceiver
{
    /**
     * Default constructor.
     */
    public LocalMessageReceiver()
    {
        super();
    }
    /**
     * Default constructor.
     */
    public LocalMessageReceiver(BaseMessageQueue messageQueue)
    {
        this();
        this.init(messageQueue);    // The one and only
    }
    /**
     * Initializes the BaseApplication.
     * Usually you pass the object that wants to use this sesssion.
     * For example, the applet or BaseApplication.
     */
    public void init(BaseMessageQueue messageQueue)
    {
        super.init(messageQueue);
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
        super.stopThisThread(); // Set the flag to stop this thread.
        // Now, send a message which will be received by this thread; since continueLooping is off, this thread will stop.
        ((LocalMessageQueue)this.getMessageQueue()).getMessageStack().sendMessage(new MapMessage(null, null));
    }
    /**
     * Process the receive message call.
     * pend(don) NOTE: This will not work as an EJB, because the receiveMessage call blocks.
     */
    public Message receiveMessage()
    {
        return ((LocalMessageQueue)this.getMessageQueue()).getMessageStack().receiveMessage();
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
            App app = this.getMessageQueue().getMessageManager().getApplication();
            if (app != null)
                strFilterType = app.getProperty(MessageConstants.MESSAGE_FILTER);
            if (MessageConstants.TREE_FILTER.equals(strFilterType))
                m_filterList = new TreeMessageFilterList(this);
        }
        return super.getMessageFilterList();
    }
}
