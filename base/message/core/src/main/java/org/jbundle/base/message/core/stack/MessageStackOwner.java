/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.message.core.stack;


/**
 * A MessageQueue manages the JMS connections for this message queue.
 * NOTE: The getMessageQueue call in Environment is not finished yet.
 */
public interface MessageStackOwner
{
    /**
     * Get the message stack.
     * Create it if it doesn't exist.
     * @return The message stack.
     */
    public MessageStack getMessageStack();
    /**
     * Set the message stack.
     * Create it if it doesn't exist.
     * @return The message stack.
     */
    public void setMessageStack(MessageStack messageStack);
}
