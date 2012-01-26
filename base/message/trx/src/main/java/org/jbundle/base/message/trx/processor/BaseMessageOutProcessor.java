/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.message.trx.processor;

import java.util.Map;

import org.jbundle.base.db.Record;
import org.jbundle.base.message.trx.transport.BaseMessageTransport;
import org.jbundle.model.RecordOwnerParent;
import org.jbundle.thin.base.message.BaseMessage;


/**
 * This is the base class to process an external message.
 * This class handles two distinct but related functions:
 * <br>- It converts a message to an external format and sends it, returning a reply.
 * <br>- It converts an incoming external message and processes it, typically sending a reply.
 */
public class BaseMessageOutProcessor extends BaseInternalMessageProcessor
{

    /**
     * Default constructor.
     */
    public BaseMessageOutProcessor()
    {
        super();
    }
    /**
     * Default constructor.
     */
    public BaseMessageOutProcessor(RecordOwnerParent taskParent, Record recordMain, Map<String, Object> properties)
    {
        this();
        this.init(taskParent, recordMain, properties);    // The one and only
    }
    /**
     * Initializes the MessageProcessor.
     */
    public void init(RecordOwnerParent taskParent, Record recordMain, Map<String, Object> properties)
    {
        super.init(taskParent, recordMain, properties);
    }
    /**
     * Free all the resources belonging to this class.
     */
    public void free()
    {
        super.free();
    }
    /**
     * Run the code in this process (you must override).
     * Process this message.
     */
    public void run()
    {
        BaseMessage internalTrxMessage = (BaseMessage)this.getTargetMessage();      // Create my standard message.
        if (internalTrxMessage != null)
        {
            // Get the transport type from the header and merge the transport properties with my header properties.
            BaseMessageTransport transport = this.getMessageTransport(internalTrxMessage);
            transport.sendMessage(internalTrxMessage, this);
            transport.free();
        }
    }
}
