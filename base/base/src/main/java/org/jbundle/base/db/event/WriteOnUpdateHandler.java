/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db.event;

/**
 * @(#)SelectOnUpdateHandler.java 0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import org.jbundle.base.db.Record;

/**
 * Write/update and refresh the main record if this file has a record that is being added or changed.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class WriteOnUpdateHandler extends UpdateOnCloseHandler
{
    
    /**
    * SelectOnUpdateHandler - Constructor.
    */
    public WriteOnUpdateHandler()
    {
        super();
    }
    /**
    * SelectOnUpdateHandler - Constructor.
     * @param recordToSync The record to synchronize with this one.
     * @param bUpdateOnSelect If true, update or add a record in progress before syncing this record.
    */
    public WriteOnUpdateHandler(Record recordToSync, boolean bRefreshAfterUpdate)
    {
        this();
        this.init(null, recordToSync, bRefreshAfterUpdate, false, true);
    }
    /**
     * Constructor.
     * @param record My owner (usually passed as null, and set on addListener in setOwner()).
     * @param recordToUpdate Record to update on close.
     */
    public void init(Record record, Record recordToUpdate, boolean bRefreshAfterUpdate, boolean bUpdateOnClose, boolean bUpdateOnUpdate)
    {
        super.init(record, recordToUpdate, bRefreshAfterUpdate, bUpdateOnClose, bUpdateOnUpdate, null);
    }
}
