/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.message.trx.processor;

import java.util.Map;

import org.jbundle.base.db.Record;
import org.jbundle.model.RecordOwnerParent;
import org.jbundle.thin.base.message.BaseMessage;


/**
 * This is the base class to process an error message.
 * This class handles a message that resulted in an error.
 */
public class BaseMessageErrorProcessor extends BaseMessageProcessor
{
    /**
     * Default constructor.
     */
    public BaseMessageErrorProcessor()
    {
        super();
    }
    /**
     * Default constructor.
     */
    public BaseMessageErrorProcessor(RecordOwnerParent taskParent, Record recordMain, Map<String, Object> properties)
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
     * Given the converted message for return, do any further processing before returing the message.
     * @param internalMessage The internal return message just as it was converted from the source.
     * @return A reply message if applicable.
     */
    public BaseMessage processMessage(BaseMessage message)
    {
        return message;     // If there is not error processing, just return the supplied message
    }
}
