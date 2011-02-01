package org.jbundle.base.message.record;

/**
 * @(#)SyncRecordListenerBehavior.java  0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import org.jbundle.base.db.Record;
import org.jbundle.base.db.event.FileListener;
import org.jbundle.base.db.event.FreeOnFreeHandler;
import org.jbundle.base.field.ListenerOwner;

/**
 * SyncRecordListenerBehavior - This listener keeps the associated record listener up-to-date on
 * what I'm listening for.
 * Here is how it works:
 * When I read a record, I tell the record listener to listen for changes to this record.
 * When I write/delete/add a record, I tell the record listener to stop listening.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class BaseSyncRecordMessageFilterHandler extends FreeOnFreeHandler
    implements RecordMessageConstants
{
    /**
     * The message filter that I need to keep up to date.
     */
    protected transient BaseRecordMessageFilter m_recordMessageFilter = null;

    /**
    * Constructor.
    */
    public BaseSyncRecordMessageFilterHandler()
    {
        super();
    }
    /**
    * Constructor.
    */
    public BaseSyncRecordMessageFilterHandler(BaseRecordMessageFilter recordMessageFilter, boolean bCloseOnFree)
    {
        this();
        this.init(null, recordMessageFilter, bCloseOnFree);
    }
    /**
    * Constructor.
    */
    public void init(Record record, BaseRecordMessageFilter recordMessageFilter, boolean bCloseOnFree)
    {
        super.init(record, recordMessageFilter, null, bCloseOnFree);
        // Should work in the server, if created on the server (but don't replicate down to the server)
        this.setMasterSlaveFlag(FileListener.RUN_IN_MASTER | FileListener.RUN_IN_SLAVE | FileListener.DONT_REPLICATE_TO_SLAVE);
        m_recordMessageFilter = recordMessageFilter;
    }
    /**
     * Free the listener.
     */
    public void free()
    {
        m_recordMessageFilter = null;
        super.free();
    }
    /**
     * Check to see that the base tables are the same first.
     */
     public void setOwner(ListenerOwner owner)
     {
         super.setOwner(owner);
     }
    /**
     * 
     * @return
     */
    public BaseRecordMessageFilter getRecordMessageFilter()
    {
        return m_recordMessageFilter;
    }
}
