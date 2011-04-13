package org.jbundle.model;


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
