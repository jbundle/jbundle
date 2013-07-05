/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db.event;

/**
 * @(#)RecountOnValidHandler.java 0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import org.jbundle.base.db.Record;
import org.jbundle.base.field.ListenerOwner;
import org.jbundle.base.model.DBConstants;
import org.jbundle.model.DBException;


/**
 * On Valid record, Requery and read through the entire sub-file (usually to recount a field).
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class RecountOnValidHandler extends FileListener
{
    /**
     * The sub record.
     */
    protected Record m_recordSub = null;
    /**
     * Restore the current record after recount.
     */
    protected boolean m_bRestoreCurrentRecord = false;
    
    /**
     * This version moves the current Handle to the (Reference or FullReference) dest field.
     */
    public RecountOnValidHandler()
    {
        super();
    }
    /**
     * Constructor.
     * @param recordSub The sub-record.
     */
    public RecountOnValidHandler(Record recordSub)
    {
        this();
        this.init(null, recordSub, false);
    }
    /**
     * Constructor.
     * @param recordSub The sub-record.
     */
    public RecountOnValidHandler(Record recordSub, boolean bRestoreCurrentRecord)
    {
        this();
        this.init(null, recordSub, bRestoreCurrentRecord);
    }
    /**
     * Constructor.
     * @param record My owner (usually passed as null, and set on addListener in setOwner()).
     * @param recordSub The sub-record.
     */
    public void init(Record record, Record recordSub, boolean bRestoreCurrentRecord)
    {
        m_recordSub = recordSub;
        m_bRestoreCurrentRecord = bRestoreCurrentRecord;
        super.init(record);
    }
    /**
     * Set the field or file that owns this listener.
     * @param owner My owner.
     */
    public void setOwner(ListenerOwner owner)
    {
        super.setOwner(owner);
        if (this.getOwner() == null)
            return;
        if (m_recordSub != null)    // If field is not in this file, remember to remove it
            m_recordSub.addListener(new FileRemoveBOnCloseHandler(this));
    }
    /**
     * Called when a valid record is read from the table/query.
     * Re-read the sub-file on change.
     * @param bDisplayOption If true, display any changes.
     */
    public void doValidRecord(boolean bDisplayOption)
    {
        super.doValidRecord(bDisplayOption);
        this.recountRecords();
    }
    /**
     * Called when a valid record is read from the table/query.
     * Re-read the sub-file on change.
     * @param bDisplayOption If true, display any changes.
     */
    public void recountRecords()
    {
        try   {
            Object bookmark = null;
            if (m_bRestoreCurrentRecord)
                if (m_recordSub.getEditMode() == DBConstants.EDIT_CURRENT)
                    bookmark = m_recordSub.getHandle(DBConstants.BOOKMARK_HANDLE);
            m_recordSub.close();
            while (m_recordSub.hasNext())
            {   // Recount each sub-record
                m_recordSub.next();
            }
            if (bookmark != null)
                m_recordSub.setHandle(bookmark, DBConstants.BOOKMARK_HANDLE);
        } catch (DBException ex)    {
            ex.printStackTrace();
        }
    }
}
