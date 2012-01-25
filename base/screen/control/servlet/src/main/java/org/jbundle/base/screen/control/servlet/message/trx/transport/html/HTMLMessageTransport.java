/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.control.servlet.message.trx.transport.html;

import org.jbundle.base.message.trx.transport.BaseMessageTransport;
import org.jbundle.model.main.msg.db.MessageTransportModel;
import org.jbundle.model.Task;
import org.jbundle.thin.base.message.BaseMessage;


/**
 * This is the base class to process an external message.
 */
public class HTMLMessageTransport extends BaseMessageTransport
{

    /**
     * Default constructor.
     */
    public HTMLMessageTransport()
    {
        super();
    }
    /**
     * Default constructor.
     */
    public HTMLMessageTransport(Task messageSenderReceiver)
    {
        this();
        this.init(messageSenderReceiver);    // The one and only
    }
    /**
     * Initializes the MessageProcessor.
     */
    public void init(Task messageSenderReceiver)
    {
        super.init(messageSenderReceiver, null, null);
    }
    /**
     * Free all the resources belonging to this class.
     */
    public void free()
    {
        super.free();
    }
    /**
     * Get the message type.
     * @return the type of message this concrete class processes.
     */
    public String getMessageTransportType()
    {
        return MessageTransportModel.HTML;
    }
    /**
     * Send the message and (optionally) get the reply.
     * @param nodeMessage The XML tree to send.
     * @param strDest The destination URL string.
     * @return The reply message (or null if none).
     */
    public BaseMessage sendMessageRequest(BaseMessage messageOut)
    {
        return null;    // You can't send via html.
    }
}
