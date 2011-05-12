package org.jbundle.base.message.core.local;

import org.jbundle.base.message.core.stack.MessageStack;
import org.jbundle.base.message.core.stack.MessageStackOwner;
import org.jbundle.thin.base.message.BaseMessageManager;
import org.jbundle.thin.base.message.BaseMessageQueue;
import org.jbundle.thin.base.message.BaseMessageReceiver;
import org.jbundle.thin.base.message.BaseMessageSender;
import org.jbundle.thin.base.message.MessageConstants;

/**
 * A MessageQueue manages the JMS connections for this message queue.
 * A Local Message Queue distributes messages locally within the same JVM.
 * A send message is pressed onto a stack. The stack is poped (in FIFO) as
 * messageReceive is called (from BaseMessageReceiver).
 */
public class LocalMessageQueue extends BaseMessageQueue
    implements MessageStackOwner
{
    /**
     * The message stack.
     */
    protected MessageStack m_messageStack = null;

    /**
     * Default constructor.
     */
    public LocalMessageQueue()
    {
        super();
    }
    /**
     * Default constructor.
     */
    public LocalMessageQueue(BaseMessageManager manager, String strQueueName)
    {
        this();
        this.init(manager, strQueueName); // The one and only
    }
    /**
     * Initializes the BaseApplication.
     * Usually you pass the object that wants to use this sesssion.
     * For example, the applet or BaseApplication.
     */
    public void init(BaseMessageManager manager, String strQueueName)
    {
        super.init(manager, strQueueName, MessageConstants.INTRANET_QUEUE);
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
        return new LocalMessageSender(this);
    }
    /**
     * Get the message sender.
     */
    public BaseMessageReceiver createMessageReceiver()
    {
        return new LocalMessageReceiver(this);
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
