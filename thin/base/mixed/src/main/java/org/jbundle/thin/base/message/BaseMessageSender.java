package org.jbundle.thin.base.message;

import org.jbundle.model.Message;
import org.jbundle.model.MessageSender;

/**
 * BaseMessageSender.java
 *
 * Created on June 13, 2000, 3:29 AM
 */


/** 
 * Base class for sending messages to an RMS server.
 * @author  Administrator
 * @version 1.0.0
 */
public class BaseMessageSender extends Object
	implements MessageSender
{
    /**
     * My parent message queue.
     */
    protected BaseMessageQueue m_baseMessageQueue = null;

    /**
     * Creates new BaseMessageSender.
     */
    public BaseMessageSender()
    {
        super();
    }
    /**
     * Creates new BaseMessageSender.
     * @param baseMessageQueue My parent message queue.
     */
    public BaseMessageSender(BaseMessageQueue baseMessageQueue)
    {
        this();
        this.init(baseMessageQueue);
    }
    /**
     * Creates new BaseMessageSender.
     * @param baseMessageQueue My parent message queue.
     */
    public void init(BaseMessageQueue baseMessageQueue)
    {
        m_baseMessageQueue = baseMessageQueue;
    }
    /**
     * Free this object.
     */
    public void free()
    {
        m_baseMessageQueue = null;
    }
    /**
     * Get my parent message queue for this receiver.
     * @return My message queue.
     */
    public BaseMessageQueue getMessageQueue()
    {
        return m_baseMessageQueue;
    }
    /**
     * Send this message.
     *  Override this to do something.
     * @param message The message to send.
     */
    public void sendMessage(Message message)
    {
        // Override this to do something
    }
}
