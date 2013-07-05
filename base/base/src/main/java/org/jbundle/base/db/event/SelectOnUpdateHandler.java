/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db.event;

/**
 * @(#)SelectOnUpdateHandler.java 0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import org.jbundle.base.db.Record;
import org.jbundle.base.field.ListenerOwner;
import org.jbundle.base.model.DBConstants;
import org.jbundle.thin.base.db.FieldInfo;


/**
 * SelectOnUpdateHandler Besides selects, this listener reads the same record
 * in the target file on valid and new and updated records.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class SelectOnUpdateHandler extends OnSelectHandler
{

    /**
    * SelectOnUpdateHandler - Constructor.
    */
    public SelectOnUpdateHandler()
    {
        super();
    }
    /**
    * SelectOnUpdateHandler - Constructor.
     * @param recordToSync The record to synchronize with this one.
     * @param bUpdateOnSelect If true, update or add a record in progress before syncing this record.
    */
    public SelectOnUpdateHandler(Record recordToSync, boolean bUpdateOnSelect)
    {
        this();
        this.init(null, recordToSync, bUpdateOnSelect);
    }
    /**
    * SelectOnUpdateHandler - Constructor.
     * @param recordToSync The record to synchronize with this one.
     * @param bUpdateOnSelect If true, update or add a record in progress before syncing this record.
    */
    public void init(Record record, Record recordToSync, boolean bUpdateOnSelect)
    {
        super.init(record, null, recordToSync, bUpdateOnSelect, DBConstants.SELECT_TYPE);
    }
    /**
     * Set the field or file that owns this listener.
     * @param owner My owner.
     */
    public void setOwner(ListenerOwner owner)
    {
        super.setOwner(owner);
        if (this.getOwner() != null)
            this.getOwner().setOpenMode(this.getOwner().getOpenMode() | DBConstants.OPEN_REFRESH_AND_LOCK_ON_CHANGE_STRATEGY);    // Make sure keys are updated before sync
    }
    /**
     * Called when a change is the record status is about to happen/has happened.
     * @param field If this file change is due to a field, this is the field.
     * @param changeType The type of change that occurred.
     * @param bDisplayOption If true, display any changes.
     * @return an error code.
     * Synchronize records after an update or add.
     */
    public int doRecordChange(FieldInfo field, int iChangeType, boolean bDisplayOption)
    { // Read a valid record
        int iErrorCode = super.doRecordChange(field, iChangeType, bDisplayOption);      // Initialize the record
        if (iErrorCode != DBConstants.NORMAL_RETURN)
            return iErrorCode;
        if ((iChangeType == DBConstants.AFTER_UPDATE_TYPE)
            || (iChangeType == DBConstants.AFTER_ADD_TYPE))
                return this.syncRecords();
        return iErrorCode;
    }
    /**
     * Called when a valid record is read from the table/query.
     * @param bDisplayOption If true, display any changes.
     * Synchronize records after a dovalid.
     */
    public void doValidRecord(boolean bDisplayOption)
    {   // Sync on a read also
        super.doValidRecord(bDisplayOption);
        this.syncRecords();
    }
}
