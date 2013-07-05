/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db.event;

/**
 * @(#)DateChangedHandler.java  0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import org.jbundle.base.db.Record;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.ListenerOwner;
import org.jbundle.base.model.DBConstants;
import org.jbundle.model.DBException;
import org.jbundle.model.Task;
import org.jbundle.thin.base.db.FieldInfo;
import org.jbundle.thin.base.db.buff.BaseBuffer;
import org.jbundle.thin.base.db.buff.VectorBuffer;


/**
 * ReadOnlyRecordHandler - Set the "Date Changed" field whenever the record is changed.
 * NOTE: There is no Clone method, because this method will always be placed in the addListeners method of the file, which is called on Clone.
 * @version 1.0.0
 * @author    Don Corley
 */
public class ReadOnlyRecordHandler extends FileListener
{
    /**
     * The date changed field in this record.
     */
    protected BaseField m_field = null;
    /**
     * Create a new record when changing a locked record.
     */
    protected boolean m_bNewOnChange = false;
    /**
     * 
     */
    protected boolean m_bOrigLockFlag = false;

    /**
     * Constructor.
     */
    public ReadOnlyRecordHandler()
    {
        super();
    }
    /**
     * Constructor.
     * @param field The date changed field in this record.
     */
    public ReadOnlyRecordHandler(BaseField field)
    {
        this();
        this.init(null, field,  false);
    }
    /**
     * Constructor.
     * @param field The date changed field in this record.
     */
    public ReadOnlyRecordHandler(BaseField field, boolean bNewOnChange)
    {
        this();
        this.init(null, field,  bNewOnChange);
    }
    /**
     * Constructor.
     * @param record My owner (usually passed as null, and set on addListener in setOwner()).
     * @param iMainFilesField The sequence of the date changed field in this record.
     * @param field The date changed field in this record.
     * @param bNewOnChange If true, create a new record on change.
     */
    public void init(Record record, BaseField field, boolean bNewOnChange)
    {
        super.init(record);
        m_field = field;
        m_bNewOnChange = bNewOnChange;
    }
    /**
     * Set the field or file that owns this listener.
     * Note: There is no getOwner() method here... This is because
     * the specific listeners return the correct owner class on getOwner().
     * @param owner My owner.
     */
    public void setOwner(ListenerOwner owner)
    {
        super.setOwner(owner);
    }
    /**
     * Called when a valid record is read from the table/query.
     * @param bDisplayOption If true, display any changes.
     */
    public void doValidRecord(boolean bDisplayOption) // init this field override for other value
    {
        super.doValidRecord(bDisplayOption);
        if (m_field != null)
            m_bOrigLockFlag = m_field.getState();
    }
    /**
     * Called when a change is the record status is about to happen/has happened.
     * This method sets the field to the current time on an add or update.
     * @param field If this file change is due to a field, this is the field.
     * @param iChangeType The type of change that occurred.
     * @param bDisplayOption If true, display any changes.
     * @return an error code.
     */
    public int doRecordChange(FieldInfo field, int iChangeType, boolean bDisplayOption)
    { // Read a valid record
        boolean bAllowChange = !m_bOrigLockFlag;    // Orig locked = can't change, not locked = change okay
        if (m_field != null)
            if (!m_field.getState())
                bAllowChange = true;    // Unless just changed to unlocked = change still okay.
        if (!bAllowChange)
        {
            Task task = this.getOwner().getTask();
            switch (iChangeType)
            {
            case DBConstants.UPDATE_TYPE:
                if (m_bNewOnChange)
                {
                    try {
                        Record record = this.getOwner();
                        BaseBuffer buffer = new VectorBuffer(null, BaseBuffer.PHYSICAL_FIELDS | BaseBuffer.MODIFIED_ONLY);
                        buffer.fieldsToBuffer(record);
                        record.addNew();
                        buffer.bufferToFields(record, true, DBConstants.SCREEN_MOVE);
                        record.add();
                        Object bookmark = record.getLastModified(DBConstants.BOOKMARK_HANDLE);
                        record.setHandle(bookmark, DBConstants.BOOKMARK_HANDLE);
                        break;  // Normal return
                    } catch (DBException ex) {
                        ex.printStackTrace();
                    }
                }
            case DBConstants.DELETE_TYPE:
                if (task != null)
                    return task.setLastError("Can't change manually locked record");
                return DBConstants.ERROR_RETURN;
            default:
            }
        }
        return super.doRecordChange(field, iChangeType, bDisplayOption);        // Initialize the record
    }
}
