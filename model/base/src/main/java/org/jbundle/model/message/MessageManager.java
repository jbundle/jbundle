/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.model.message;

import org.jbundle.model.Freeable;



/**
 * The MessageManager organizes the message queues.
 * NOTE: This should probably implement the Task interface (Note: app is already passed in).
 */
public interface MessageManager
	extends Freeable
{
    /**
     * Get this Message Queue (or create one if this name doesn't exist).
     * Override this to supply the correct message queue if it hasn't already been built.
     * @param strQueueName The queue name to lookup.
     * @param strQueueType The queue type if this queue needs to be created.
     * @return The message queue.
     */
    public MessageQueue getMessageQueue(String strQueueName, String strQueueType);
    /**
     * Add this message filter to the appropriate queue.
     * The message filter contains the queue name and type and a listener to send the message to.
     * @param messageFilter The message filter to add.
     * @return An error code.
     */
    public int addMessageFilter(MessageFilter messageFilter);
}
