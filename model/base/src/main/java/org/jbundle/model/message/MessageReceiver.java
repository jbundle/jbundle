/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.model.message;




public interface MessageReceiver {

    /**
     * Block until a message is received.
     * You must override this abstract method.
     * @return The next message.
     */
    public Message receiveMessage();

    /**
     * Add this message filter to this receive queue.
     * @param The message filter to add.
     */
    public void addMessageFilter(MessageFilter messageFilter);
    /**
     * Remove this message filter from this queue.
     * Note: This will remove a filter that equals this filter, accounting for a copy
     * passed from a remote client.
     * @param messageFilter The message filter to remove.
     * @param bFreeFilter If true, free this filter.
     * @return True if successful.
     */
    public boolean removeMessageFilter(MessageFilter messageFilter, boolean bFreeFilter);
}
