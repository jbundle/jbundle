/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.message.record;

/**
 * @(#)SyncRecordListenerBehavior.java  0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.util.Hashtable;

import org.jbundle.base.db.Record;
import org.jbundle.base.field.ListenerOwner;
import org.jbundle.base.model.DBConstants;
import org.jbundle.model.DBException;
import org.jbundle.thin.base.db.FieldInfo;


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
public class SyncRecordMessageFilterHandler extends BaseSyncRecordMessageFilterHandler
{

    /**
    * Constructor.
    */
    public SyncRecordMessageFilterHandler()
    {
        super();
    }
    /**
    * Constructor.
    */
    public SyncRecordMessageFilterHandler(BaseRecordMessageFilter recordMessageFilter, boolean bCloseOnFree)
    {
        this();
        this.init(null, recordMessageFilter, bCloseOnFree);
    }
    /**
    * Constructor.
    */
    public void init(Record record, BaseRecordMessageFilter recordMessageFilter, boolean bCloseOnFree)
    {
        super.init(record, recordMessageFilter, bCloseOnFree);
        // Should work in the server, if created on the server (but don't replicate down to the server)
        this.updateListener(null);  // Stop listening
    }
    /**
     * Free the listener.
     */
    public void free()
    {
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
     * Called when a valid record is read from the table/query.
     */
    public void doValidRecord(boolean bDisplayOption) // init this field override for other value
    {
        Object bookmark = null;
        try   {
            bookmark = this.getOwner().getHandle(DBConstants.BOOKMARK_HANDLE);
        } catch (DBException ex)    {
            ex.printStackTrace();
        }
        this.updateListener(bookmark);
        super.doValidRecord(bDisplayOption);
    }
    /**
    * On select, synchronize the records.
    */
    public int doRecordChange(FieldInfo field, int iChangeType, boolean bDisplayOption)
    { // Read a valid record
        int iErrorCode = super.doRecordChange(field, iChangeType, bDisplayOption);      // Initialize the record
        if (iErrorCode == DBConstants.NORMAL_RETURN)
        {
            if ((iChangeType == DBConstants.ADD_TYPE)
                || (iChangeType == DBConstants.UPDATE_TYPE)
                || (iChangeType == DBConstants.DELETE_TYPE))
                    this.updateListener(null);  // Stop listening
            if (iErrorCode == DBConstants.AFTER_REQUERY_TYPE)
//?             || (iErrorCode == DBConstants.SELECT_TYPE))
            {
    //+         ???
            }
        }
        return iErrorCode;
    }
    /**
     * Update the listener to listen for changes to the following record.
     */
    public void updateListener(Object bookmark)
    {
        if (m_recordMessageFilter != null)
        {
            if (bookmark == null)
                bookmark = NO_BOOKMARK;
            Hashtable<String,Object> properties = new Hashtable<String,Object>();
            properties.put(DBConstants.STRING_OBJECT_ID_HANDLE, bookmark);
            m_recordMessageFilter.updateFilterTree(properties);
        }
    }
}
