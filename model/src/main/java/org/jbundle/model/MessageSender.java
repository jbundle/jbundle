package org.jbundle.model;


public interface MessageSender {

    /**
     * Send this message.
     *  Override this to do something.
     * @param message The message to send.
     */
    public void sendMessage(Message message);
}
