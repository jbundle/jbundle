/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db.event;

/**
 * Copyright © 2012 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 *
 * Enable on valid and disable on new.
 */

import org.jbundle.base.db.Record;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.ListenerOwner;
import org.jbundle.base.model.DBConstants;
import org.jbundle.model.DBException;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.FieldInfo;


/**
 * Update table if this file is freed or updated.
 */
public class UpdateOnCloseHandler extends FileListener
{
    /**
     * Record to update on close.
     */
    protected Record m_recordToUpdate = null;
    /**
     * Refresh after update (rare, but possible).
     */
    protected boolean m_bRefreshAfterUpdate = false;
    /**
     * 
     */
    protected boolean m_bUpdateOnClose = false;
    /**
     * 
     */
    protected boolean m_bUpdateOnUpdate = false;
    /**
     * 
     */
    protected BaseField fieldToUpdate = null;
    /**
     * Constructor.
     */
    public UpdateOnCloseHandler()
    {
        super();
    }
    /**
     * Constructor.
     * @param recordToUpdate Record to update on close (if null, defaults to this file).
     */
    public UpdateOnCloseHandler(Record recordToUpdate)
    {
        this();
        this.init(null, recordToUpdate, false, true, false, null);
    }
    /**
     * Constructor.
     * @param recordToUpdate Record to update on close (if null, defaults to this file).
     */
    public UpdateOnCloseHandler(Record recordToUpdate, boolean bRefreshAfterUpdate)
    {
        this();
        this.init(null, recordToUpdate, bRefreshAfterUpdate, true, false, null);
    }
    /**
     * Constructor.
     * @param recordToUpdate Record to update on close (if null, defaults to this file).
     */
    public UpdateOnCloseHandler(Record recordToUpdate, boolean bRefreshAfterUpdate, boolean bUpdateOnUpdate)
    {
        this();
        this.init(null, recordToUpdate, bRefreshAfterUpdate, true, bUpdateOnUpdate, null);
    }
    /**
     * Constructor.
     * @param recordToUpdate Record to update on close (if null, defaults to this file).
     */
    public UpdateOnCloseHandler(Record recordToUpdate, boolean bRefreshAfterUpdate, boolean bUpdateOnClose, boolean bUpdateOnUpdate)
    {
        this();
        this.init(null, recordToUpdate, bRefreshAfterUpdate, bUpdateOnClose, bUpdateOnUpdate, null);
    }
    /**
     * Constructor - set this field to "modified" if any changes to record.
     * @param recordToUpdate Record to update on close (if null, defaults to this file).
     */
    public UpdateOnCloseHandler(BaseField fieldToUpdate)
    {
        this();
        this.init(null, null, false, false, true, fieldToUpdate);
    }
    /**
     * Constructor.
     * @param record My owner (usually passed as null, and set on addListener in setOwner()).
     * @param recordToUpdate Record to update on close.
     */
    public void init(Record record, Record recordToUpdate, boolean bRefreshAfterUpdate, boolean bUpdateOnClose, boolean bUpdateOnUpdate, BaseField fieldToUpdate)
    {
        m_recordToUpdate = recordToUpdate;
        m_bRefreshAfterUpdate = bRefreshAfterUpdate;
        m_bUpdateOnClose = bUpdateOnClose;
        m_bUpdateOnUpdate = bUpdateOnUpdate;
        this.fieldToUpdate = fieldToUpdate;
        super.init(record);
    }
    /**
     * Set the field or file that owns this listener.
     * @param owner My owner.
     */
    public void setOwner(ListenerOwner owner)
    {
        super.setOwner(owner);
        if (m_recordToUpdate == null)
            m_recordToUpdate = this.getOwner();   // Defaults to this file
        if (owner != null) if (this.getOwner() != m_recordToUpdate)
            m_recordToUpdate.addListener(new FileRemoveBOnCloseHandler(this));
        if (m_recordToUpdate != null)
            m_recordToUpdate.setOpenMode((m_recordToUpdate.getOpenMode() | DBConstants.OPEN_LOCK_ON_CHANGE_STRATEGY) & ~(DBConstants.OPEN_READ_ONLY | DBConstants.OPEN_APPEND_ONLY));   // Lock the record if any changes
        if (owner != null)
            if (this.getOwner() == m_recordToUpdate)
                if (this.getOwner().getListener() != this)  // Prevents endless loop (listener is already the first in the list)
        {   // This is special weird logic - This must be the first in the listener list
            this.getOwner().removeListener(this, false);
            super.setOwner(owner);  // Set it back
            this.setNextListener(this.getOwner().getListener());
            this.getOwner().setListener(this);  // This MUST be the first listener on the list.
        }
    }
    /**
     * free this listener and update/add the target record.
     */
    public void free()
    {
        if (m_bUpdateOnClose)
            this.writeAndRefresh();
        super.free();
    }
    /**
     * Called when a change in the record status is about to happen/has happened.
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
        if (m_bUpdateOnUpdate)
            if ((iChangeType == DBConstants.AFTER_UPDATE_TYPE)
                || (iChangeType == DBConstants.AFTER_ADD_TYPE))
            return this.writeAndRefresh();
        if (iChangeType == DBConstants.BEFORE_FREE_TYPE)
            if (m_bUpdateOnClose)
                this.writeAndRefresh();
        return iErrorCode;
    }
    /**
     * Called when a new blank record is required for the table/query.
     * @param bDisplayOption If true, display any changes.
     */
    public void doNewRecord(boolean bDisplayOption)   // init this field override for other value
    {
        super.doNewRecord(bDisplayOption);
        if (m_bUpdateOnUpdate)
            this.writeAndRefresh(); // If there are any changes waiting, write them!
    }
    /**
     * free this listener and update/add the target record.
     */
    public int writeAndRefresh()
    {
        try   {
            if (m_recordToUpdate != null)
                if (m_recordToUpdate.isModified(true))
            {
                if (m_bRefreshAfterUpdate)
                    m_recordToUpdate.writeAndRefresh();
                else
                {
                    if (m_recordToUpdate.getEditMode() == Constants.EDIT_IN_PROGRESS)
                        m_recordToUpdate.set();
                    else if (m_recordToUpdate.getEditMode() == Constants.EDIT_ADD)
                        m_recordToUpdate.add();
                }
            }
            if (fieldToUpdate != null)
            {
                fieldToUpdate.setModified(true);
                //?result = fieldToUpdate.getRecord().handleRecordChange(m_fldTarget, DBConstants.FIELD_CHANGED_TYPE, bDisplayOption);    // Tell table that I'm getting changed (if not locked)
            }
        } catch(DBException ex)   {
            ex.printStackTrace();
        }
        return DBConstants.NORMAL_RETURN;   // For now
    }
    /**
     * 
     * @return
     */
    public Record getRecordToUpdate()
    {
        return m_recordToUpdate;
    }
    /**
     * Add this listener to a record (only if it doesn't already exist)
     * @param record
     * @param recordToUpdate
     * @param bRefreshAfterUpdate
     * @param bUpdateOnClose
     * @param bUpdateOnUpdate
     * @return
     */
    public static UpdateOnCloseHandler addUpdateOnCloseHandler(Record record, Record recordToUpdate, boolean bRefreshAfterUpdate, boolean bUpdateOnClose, boolean bUpdateOnUpdate)
    {
        UpdateOnCloseHandler listener = (UpdateOnCloseHandler)record.getListener(UpdateOnCloseHandler.class);
        while (listener != null)
        {
            if (listener.getRecordToUpdate() == recordToUpdate)
                break;  // Found, don't add again
            listener = (UpdateOnCloseHandler)listener.getListener(UpdateOnCloseHandler.class);
        }
        if (listener == null)
            record.addListener(listener = new UpdateOnCloseHandler(recordToUpdate, bRefreshAfterUpdate, bUpdateOnClose, bUpdateOnUpdate)); // Make sure this is updated with booking
        return listener;
    }
}
