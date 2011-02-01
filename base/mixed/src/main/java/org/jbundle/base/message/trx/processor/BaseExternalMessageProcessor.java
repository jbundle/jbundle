package org.jbundle.base.message.trx.processor;

import java.util.Map;
import java.util.Properties;

import org.jbundle.base.db.Record;
import org.jbundle.model.RecordOwnerParent;
import org.jbundle.model.Task;


/**
 * This is the base class to process an external message.
 * This class handles two distinct but related functions:
 * <br>- It converts a message to an external format and sends it, returning a reply.
 * <br>- It converts an incoming external message and processes it, typically sending a reply.
 */
public abstract class BaseExternalMessageProcessor extends BaseMessageProcessor
{
    /**
     * Default constructor.
     */
    public BaseExternalMessageProcessor()
    {
        super();
    }
    /**
     * Default constructor.
     */
    public BaseExternalMessageProcessor(RecordOwnerParent taskParent, Record recordMain, Properties properties)
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
}
