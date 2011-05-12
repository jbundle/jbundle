package org.jbundle.base.message.core.stack;

import java.util.Vector;

import org.jbundle.thin.base.message.BaseMessage;

/**
 * A MessageQueue manages the JMS connections for this message queue.
 * NOTE: The getMessageQueue call in Environment is not finished yet.
 */
public class MessageStack extends Object
{
    /**
     * FIFO Stack of messages.
     */
    protected Vector<BaseMessage> m_stack = new Vector<BaseMessage>();
    /**
     * The thread to synchronize on (this is the receive thread).
     */
    protected Thread m_thread = null;
    /**
     * The parent message queue.
     */
    protected MessageStackOwner m_messageStackOwner = null;
    /**
     * Is this thread waiting for a notify?
     */
    protected boolean m_bWaiting = false;

    /**
     * Default constructor.
     */
    public MessageStack()
    {
        super();
    }
    /**
     * Default constructor.
     */
    public MessageStack(MessageStackOwner messageStackOwner)
    {
        this();
        this.init(messageStackOwner); // The one and only
    }
    /**
     * Initializes the BaseApplication.
     * Usually you pass the object that wants to use this sesssion.
     * For example, the applet or BaseApplication.
     */
    public void init(MessageStackOwner messageStackOwner)
    {
        m_messageStackOwner = messageStackOwner;
    }
    /**
     * Free all the resources belonging to this applet. If all applet screens are closed, shut down the applet.
     */
    public void free()
    {
        if (m_messageStackOwner != null)    // Always
            m_messageStackOwner.setMessageStack(null);
        m_messageStackOwner = null;
        if (m_bWaiting)
        {
            synchronized (m_thread)
            {
                if (m_bWaiting) // Inside the sync block
                    m_stack.removeAllElements();
                m_bWaiting = false;
                if (m_thread != null)
                {
                    m_stack = null;
                    m_thread.interrupt();
                }
                m_thread = null;
            }
        }
        m_stack = null;
        m_thread = null;
    }
    /**
     * Send this message to the message queue.
     * @param strCommand Command to perform remotely.
     * @return boolean success.
     */
    public void sendMessage(BaseMessage message)
    {
        if (message == null)
            return;     // Don't allow a null message to be sent.
        m_stack.add(message);
        if (m_bWaiting)
        {
            synchronized (m_thread)
            {
                if (m_bWaiting) // Inside the sync block
                    m_thread.notify();
            }
        }
    }
    /**
     * Process the receive message call.
     * Note: Be careful, this call blocks until the message is ready to be received.
     * @return The next message (when it becomes available).
     */
    public BaseMessage receiveMessage()
    {
        while ((m_stack != null) && (m_stack.isEmpty()))
        {
            m_thread = Thread.currentThread();
            synchronized(m_thread)
            {
                m_bWaiting = true;
                if (m_stack.isEmpty())      // In case a message was added outside the sync block.
                {
                    try {
                        m_thread.wait();
                    } catch (InterruptedException ex)   {
                        m_stack = null; // This causes a null message to return, which tells the receiving loop to stop (halting this thread).
                        m_thread = null;    // Don't free this twice.
                    }
                }
                m_bWaiting = false;     // Notified
            }
        }
        BaseMessage message = null;
        if (m_stack != null)
            if (!m_stack.isEmpty())
                message = (BaseMessage)m_stack.remove(0);
        return message;
    }
}
