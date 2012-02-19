/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.model.message;

import org.jbundle.model.Freeable;


/**
 * The message queue handles receiving and distributing messages for this queue name and type.
 */
public interface MessageQueue
	extends Freeable
{
    /**
     * Get the message sender.
     * Or create it if it doesn't exist.
     * @return The message sender.
     */
    public MessageSender getMessageSender();
    /**
     * Get the message receiver.
     * Create it if it doesn't exist.
     * @return The message receiver.
     */
    public MessageReceiver getMessageReceiver();
}
