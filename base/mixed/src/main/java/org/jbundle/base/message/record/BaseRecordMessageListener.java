/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.message.record;

import org.jbundle.base.db.Record;
import org.jbundle.base.db.event.FileListener;
import org.jbundle.base.db.event.FreeOnFreeHandler;
import org.jbundle.base.util.Utility;
import org.jbundle.model.Freeable;
import org.jbundle.thin.base.message.BaseMessage;
import org.jbundle.thin.base.message.BaseMessageFilter;
import org.jbundle.thin.base.message.BaseMessageListener;
import org.jbundle.thin.base.message.BaseMessageReceiver;


/**
 * Notify a record of an incoming message.
 */
public class BaseRecordMessageListener extends BaseMessageListener
    implements Freeable
{
    /**
     * The record this listener is based on.
     */
    protected Record m_record = null;
    /**
     * Handler to remove on free.
     */
    private FreeOnFreeHandler m_closeOnFreeBehavior = null;

    /**
     * Constructor.
     */
    public BaseRecordMessageListener()
    {
        super();
    }
    /**
     * Constructor.
     * @param bUpdateRecord If true, Record is an independent record which should be read and updated on changes.
     */
    public BaseRecordMessageListener(Record record)
    {
        this();
        this.init(null, record, null);
    }
    /**
     * Constructor.
     * @param bUpdateRecord If true, Record is an independent record which should be read and updated on changes.
     */
    public BaseRecordMessageListener(BaseMessageReceiver messageHandler, Record record)
    {
        this();
        this.init(messageHandler, record, null);
    }
    /**
     * Constructor.
     */
    public void init(BaseMessageReceiver messageHandler, Record record, BaseMessageFilter messageFilter)
    {
        super.init(messageHandler, messageFilter); // This will remove this listener on close.
        m_record = record;
        m_record.addListener(m_closeOnFreeBehavior = new FreeOnFreeHandler(this));
    }
    /**
     * Constructor.
     */
    public void free()
    {
        super.free();
        if (m_closeOnFreeBehavior != null)
            if (m_record != null)
        {
            FileListener listener = m_closeOnFreeBehavior;
            m_closeOnFreeBehavior = null;
            m_record.removeListener(listener, false);
        }
    }
    /**
     * Receive the message.
     * Be very careful of multithreading issues,
     * as this will be running in an independent thread.
     */
    public int handleMessage(BaseMessage message)
    {
        // Override this!
        Utility.getLogger().info("BaseRecordMessageListener/95 " + message);
        return super.handleMessage(message);
    }
}
