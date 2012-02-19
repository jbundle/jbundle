/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.message.trx.transport.direct;

import org.jbundle.base.message.trx.transport.client.ClientMessageTransport;
import org.jbundle.model.main.msg.db.MessageTransportModel;
import org.jbundle.model.Task;


/**
 * This is the base class to process an external message.
 */
public class DirectMessageTransport extends ClientMessageTransport
{
    
    /**
     * Default constructor.
     */
    public DirectMessageTransport()
    {
        super();
    }
    /**
     * Default constructor.
     */
    public DirectMessageTransport(Task messageSenderReceiver)
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
        return MessageTransportModel.DIRECT;
    }
}
