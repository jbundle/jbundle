/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db.event;

/**
 * @(#)OnSelectHandler.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import org.jbundle.base.db.Record;
import org.jbundle.base.field.ListenerOwner;
import org.jbundle.base.message.record.RecordMessage;
import org.jbundle.base.message.record.RecordMessageHeader;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.RecordOwner;
import org.jbundle.model.DBException;
import org.jbundle.model.screen.ScreenComponent;
import org.jbundle.thin.base.db.FieldInfo;


/**
 * OnSelectHandler - On record select, read the same record in the other file.
 * This is typically used for grid views that are linked to form view for synchronization.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class OnSelectHandler extends FileListener
{
    /**
     * The record to synchronize with this record.
     */
    protected Record m_recordToSync = null;
    /**
     * If true, update or add a record in progress before syncing this record.
     */
    protected boolean m_bUpdateOnSelect = true;
    /**
     * Record owner.
     */
    protected RecordOwner m_recordOwner = null;
    /**
     * 
     */
    protected int m_iEventToTrigger = DBConstants.SELECT_TYPE;

    /**
    * OnSelectHandler - Constructor.
    */
    public OnSelectHandler()
    {
        super();
    }
    /**
     * OnSelectHandler - Constructor.
     * @param recordToSync The record to synchronize with this one.
     * @param bUpdateOnSelect If true, update or add a record in progress before syncing this record.
     */
    public OnSelectHandler(RecordOwner recordOwner)
    {
        this();
        this.init(null, recordOwner, null, false, DBConstants.SELECT_TYPE);
    }
    /**
     * OnSelectHandler - Constructor.
     * @param recordToSync The record to synchronize with this one.
     * @param bUpdateOnSelect If true, update or add a record in progress before syncing this record.
     */
    public OnSelectHandler(RecordOwner recordOwner, int iEventToTrigger)
    {
        this();
        this.init(null, recordOwner, null, false, iEventToTrigger);
    }
    /**
     * OnSelectHandler - Constructor.
     * @param recordToSync The record to synchronize with this one.
     * @param bUpdateOnSelect If true, update or add a record in progress before syncing this record.
     */
    public OnSelectHandler(Record recordToSync, boolean bUpdateOnSelect)
    {
        this();
        this.init(null, null, recordToSync, bUpdateOnSelect, DBConstants.SELECT_TYPE);
    }
    /**
     * OnSelectHandler - Constructor.
     * @param recordToSync The record to synchronize with this one.
     * @param bUpdateOnSelect If true, update or add a record in progress before syncing this record.
     */
    public OnSelectHandler(Record recordToSync, boolean bUpdateOnSelect, int iEventToTrigger)
    {
        this();
        this.init(null, null, recordToSync, bUpdateOnSelect, iEventToTrigger);
    }
    /**
     * OnSelectHandler - Constructor.
     * @param record My owner (usually passed as null, and set on addListener in setOwner()).
     * @param recordToSync The record to synchronize with this one.
     * @param bUpdateOnSelect If true, update or add a record in progress before syncing this record.
    */
    public void init(Record record, RecordOwner recordOwner, Record recordToSync, boolean bUpdateOnSelect, int iEventToTrigger)
    {
        super.init(record);
        m_recordOwner = recordOwner;
        m_recordToSync = recordToSync;
        m_bUpdateOnSelect = bUpdateOnSelect;
        m_iEventToTrigger = iEventToTrigger;
    }
    /**
     * Set the field or file that owns this listener.
     * I Check to make sure that the base tables are the same first.
     * @param owner My owner.
     */
    public void setOwner(ListenerOwner owner)
    {
        super.setOwner(owner);
        if (owner != null) if (m_recordToSync != null)
        {
            if (!m_recordToSync.getBaseRecord().getTableNames(false).equals(this.getOwner().getBaseRecord().getTableNames(false)))
            {   // Must be the same base tables!
                m_recordToSync = null;
                return;
            }
            m_recordToSync.addListener(new FileRemoveBOnCloseHandler(this));
        }
    }
    /**
     * Called when a change is the record status is about to happen/has happened.
     * If this is a select type, I synchronize the records.
     * @param field If this file change is due to a field, this is the field.
     * @param iChangeType The type of change that occurred.
     * @param bDisplayOption If true, display any changes.
     * @return an error code.
     */
    public int doRecordChange(FieldInfo field, int iChangeType, boolean bDisplayOption)
    { // Read a valid record
        int iErrorCode = super.doRecordChange(field, iChangeType, bDisplayOption);      // Initialize the record
        if (iErrorCode != DBConstants.NORMAL_RETURN)
            return iErrorCode;
        if (iChangeType == m_iEventToTrigger)
            return this.syncRecords();
        return iErrorCode;
    }
    /**
     * SyncRecords - Synchronize the records to the same row.
     * @return an error code.
     */
    public int syncRecords()
    { // Read a valid record
        try
        {
            Object bookmark = this.getOwner().getBaseRecord().getHandle(DBConstants.BOOKMARK_HANDLE);
            
            RecordOwner recordOwner = this.getRecordOwner();    // Get the record owner of the record to sync
            if ((recordOwner instanceof ScreenComponent)
                /*&& (((ScreenField)recordOwner).getScreenFieldView() != null)*/)
            {       // NOTE: This is not a real message, I send it directly to the record.
                RecordMessage message = this.createMessage(bookmark);
                recordOwner.handleMessage(message);
            }
            else
            {   // Kinda HACK - Since there is no screen, just read the record.
                Record record = this.getRecordToSync();
                if (bookmark != null)
                    record = record.setHandle(bookmark, DBConstants.BOOKMARK_HANDLE);
                if ((record == null) || (bookmark == null))
                    record.addNew();  // Not found, start with a NEW record!
            }
        } catch (DBException ex)    {
            ex.printStackTrace();
        }
        return DBConstants.NORMAL_RETURN;
    }
    /**
     * Create the record message.
     * Override this to add information to the message.
     * @param bookmark The bookmark of the record that is selected.
     * @return The record message.
     */
    public RecordMessage createMessage(Object bookmark)
    {
        int iRecordMessageType = DBConstants.SELECT_TYPE;
        RecordMessageHeader messageHeader = new RecordMessageHeader(this.getOwner(), bookmark, null, iRecordMessageType, null);
        RecordMessage message = new RecordMessage(messageHeader);
        message.put(RecordMessageHeader.UPDATE_ON_SELECT, new Boolean(m_bUpdateOnSelect));
        if (this.getRecordToSync() != null)
            message.put(RecordMessageHeader.RECORD_TO_UPDATE, this.getRecordToSync());
        return message;
    }
    /**
     * Get the record to sync with.
     * @return The record.
     */
    public Record getRecordToSync()
    {
        return m_recordToSync;
    }
    /**
     * Get the record owner of the record to sync.
     */
    public RecordOwner getRecordOwner()
    {
        if (m_recordToSync != null)
            return m_recordToSync.getRecordOwner();
        return m_recordOwner;
    }
}
