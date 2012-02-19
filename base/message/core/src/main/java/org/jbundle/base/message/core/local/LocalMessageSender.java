/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.message.core.local;

import org.jbundle.model.message.Message;
import org.jbundle.thin.base.message.BaseMessageQueue;
import org.jbundle.thin.base.message.BaseMessageSender;

/**
 * A Local Message Sender pushes sent messages onto a local (FIFO) stack.
 */
public class LocalMessageSender extends BaseMessageSender
{
    /**
     * Default constructor.
     */
    public LocalMessageSender()
    {
        super();
    }
    /**
     * Default constructor.
     */
    public LocalMessageSender(BaseMessageQueue messageQueue)
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
     * Send this message to the message queue.
     * @param strCommand Command to perform remotely.
     * @return boolean success.
     */
    public void sendMessage(Message message)
    {
        ((LocalMessageQueue)this.getMessageQueue()).getMessageStack().sendMessage(message);
    }
}
