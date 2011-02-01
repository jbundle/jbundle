package org.jbundle.base.db.event;

/**
 * @(#)SelectOnUpdateHandler.java 0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import org.jbundle.base.db.Record;
import org.jbundle.base.field.ListenerOwner;
import org.jbundle.base.util.DBConstants;
import org.jbundle.thin.base.db.FieldInfo;


/**
 * SelectOnUpdateHandler Besides selects, this listener reads the same record
 * in the target file on valid and new and updated records.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class RequeryOnUpdateHandler extends FileListener
{
    /**
     *
     */
    protected Record m_recordToSync = null;

    /**
    * SelectOnUpdateHandler - Constructor.
    */
    public RequeryOnUpdateHandler()
    {
        super();
    }
    /**
    * SelectOnUpdateHandler - Constructor.
     * @param recordToSync The record to synchronize with this one.
     * @param bUpdateOnSelect If true, update or add a record in progress before syncing this record.
    */
    public RequeryOnUpdateHandler(Record recordToSync)
    {
        this();
        this.init(null, recordToSync);
    }
    /**
    * SelectOnUpdateHandler - Constructor.
     * @param recordToSync The record to synchronize with this one.
     * @param bUpdateOnSelect If true, update or add a record in progress before syncing this record.
    */
    public void init(Record record, Record recordToSync)
    {
        super.init(record);
        m_recordToSync = recordToSync;
    }
    /**
     * Set the field or file that owns this listener.
     * @param owner My owner.
     */
    public void setOwner(ListenerOwner owner)
    {
        super.setOwner(owner);
        if (owner != null) if (m_recordToSync != null)
            m_recordToSync.addListener(new FileRemoveBOnCloseHandler(this));
    }
    /**
     * Called when a change is the record status is about to happen/has happened.
     * @param field If this file change is due to a field, this is the field.
     * @param iChangeType The type of change that occurred.
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
    /**
     * SyncRecords - Synchronize the records to the same row.
     * @return an error code.
     */
    public int syncRecords()
    { // Read a valid record
        if ((this.getOwner().getOpenMode() & DBConstants.OPEN_DONT_CHANGE_CURRENT_LOCK_TYPE) == DBConstants.OPEN_DONT_CHANGE_CURRENT_LOCK_TYPE)
            return DBConstants.NORMAL_RETURN;   // Don't need to resync if I'm doing a write and refresh.
        m_recordToSync.close();
        return DBConstants.NORMAL_RETURN;
    }
}
