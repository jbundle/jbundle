/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.message.trx.processor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

import javax.xml.datatype.XMLGregorianCalendar;

import org.jbundle.base.db.Record;
import org.jbundle.model.RecordOwnerParent;
import org.jbundle.model.Task;
import org.jbundle.thin.base.message.BaseMessage;


/**
 * This is the base class to process an external message.
 * This class handles two distinct but related functions:
 * <br>- It converts a message to an external format and sends it, returning a reply.
 * <br>- It converts an incoming external message and processes it, typically sending a reply.
 */
public class BaseMessageInProcessor extends BaseExternalMessageProcessor
{
    /**
     * Default constructor.
     */
    public BaseMessageInProcessor()
    {
        super();
    }
    /**
     * Default constructor.
     */
    public BaseMessageInProcessor(RecordOwnerParent taskParent, Record recordMain, Map<String, Object> properties)
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
