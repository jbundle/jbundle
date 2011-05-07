package org.jbundle.model.message;


public interface MessageFilter {

    /**
     * Get the queue name for this message header.
     * @return The queue name.
     */
    public String getQueueName();
    /**
     * Get the queue type for this message header.
     * @return The queue type.
     */
    public String getQueueType();
}