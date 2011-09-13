/*
 *  @(#)GetProductCostHandler.
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db.grid;

import java.util.HashSet;

import org.jbundle.base.db.Record;
import org.jbundle.base.db.event.FileListener;
import org.jbundle.base.util.DBConstants;
import org.jbundle.model.DBException;
import org.jbundle.thin.base.db.FieldInfo;


/**
 *  Keep track of whether a record has been serviced or not in a GridScreen.
 * ie., The first time a record is read, updated, or added call doFirstValidRecord.
 */
public class RecordCacheHandler extends FileListener
{
    /**
     * The field value cache (keyed by objectID).
     */
    protected HashSet<Object> m_hsCache = null;

    /**
     * Default constructor.
     */
    public RecordCacheHandler()
    {
        super();
    }
    /**
     * GetProductCostHandler Method.
     */
    public RecordCacheHandler(Record record)
    {
        this();
        this.init(record);
    }
    /**
     * Initialize class fields.
     */
    public void init(Record record)
    {
        super.init(record);
        m_hsCache = new HashSet<Object>();
    }
    /**
     * Called when a change is the record status is about to happen/has happened.
     * @param field If this file change is due to a field, this is the field.
     * @param iChangeType The type of change that occurred.
     * @param bDisplayOption If true, display any changes.
     * @return an error code.
     */
    public int doRecordChange(FieldInfo field, int iChangeType, boolean bDisplayOption)
    {   // Return an error to stop the change
        if (iChangeType == DBConstants.AFTER_REQUERY_TYPE)
        {
            this.clearCache();
        }
        if ((iChangeType == DBConstants.AFTER_UPDATE_TYPE)
            || (iChangeType == DBConstants.AFTER_ADD_TYPE))
        {
            this.doFirstValidRecord(bDisplayOption);
            this.putCacheField();
        }
        if (iChangeType == DBConstants.AFTER_DELETE_TYPE)
        {
            this.removeCacheField();
        }
        if (iChangeType == DBConstants.FIELD_CHANGED_TYPE)
        {
        }
        return super.doRecordChange(field, iChangeType, bDisplayOption);
    }
    /**
     * DoValidRecord Method.
     */
    public void doValidRecord(boolean bDisplayOption)
    {
        super.doValidRecord(bDisplayOption);
        if (!this.isCached())
        {
            this.doFirstValidRecord(bDisplayOption);
            this.putCacheField();
        }
    }
    /**
     * Setup the target field's value.
     * Override this method.
     */
    public void doFirstValidRecord(boolean bDisplayOption)
    {
        // Override this.
    }
    /**
     * Cache the target field using the current record.
     */
    public boolean isCached()
    {
        try {
            Record record = this.getOwner();
            Object objKey = record.getHandle(DBConstants.OBJECT_ID_HANDLE);
            return (m_hsCache.contains(objKey)); // True if cache value exists
        } catch (DBException ex)    {
            ex.printStackTrace();
        }
        return false;   // Never
    }
    /**
     * Cache the target field using the current record.
     */
    public void putCacheField()
    {
        try {
            Record record = this.getOwner();
            Object objKey = record.getHandle(DBConstants.OBJECT_ID_HANDLE);
            m_hsCache.add(objKey);
        } catch (DBException ex)    {
            ex.printStackTrace();
        }
    }
    /**
     * Cache the target field using the current record.
     */
    public void removeCacheField()
    {
        try {
            Record record = this.getOwner();
            Object objKey = record.getHandle(DBConstants.OBJECT_ID_HANDLE);
            m_hsCache.remove(objKey);
        } catch (DBException ex)    {
            ex.printStackTrace();
        }
    }
    /**
     * Cache the target field using the current record.
     */
    public void clearCache()
    {
        if (m_hsCache != null)
            m_hsCache.clear();
        m_hsCache = new HashSet<Object>();
    }
}
