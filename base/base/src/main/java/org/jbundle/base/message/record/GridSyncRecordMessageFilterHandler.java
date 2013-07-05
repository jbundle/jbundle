/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.message.record;

/**
 * @(#)SyncRecordListenerBehavior.java  0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.util.Hashtable;
import java.util.Map;

import org.jbundle.base.db.KeyField;
import org.jbundle.base.db.Record;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.ListenerOwner;
import org.jbundle.base.field.ReferenceField;
import org.jbundle.base.model.DBConstants;
import org.jbundle.model.DBException;
import org.jbundle.thin.base.db.FieldInfo;


/**
 * SyncRecordListenerBehavior - This listener keeps the associated record listener up-to-date on
 * what I'm listening for.
 * Here is how it works:
 * When I read a record, I tell the record listener to listen for changes to this record.
 * When I start a new query, I tell the record listener to stop listening.
 * <p>Note: DO NOT Clone this object.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class GridSyncRecordMessageFilterHandler extends BaseSyncRecordMessageFilterHandler
{

    /**
     * The type of handle to look for.
     */
    protected int m_iHandleType = DBConstants.BOOKMARK_HANDLE;
    /**
     * A bookmark can never be "0".
     */
    public final static String NO_BOOKMARK = "0";

    /**
    * Constructor.
    */
    public GridSyncRecordMessageFilterHandler()
    {
        super();
    }
    /**
    * Constructor.
    */
    public GridSyncRecordMessageFilterHandler(GridRecordMessageFilter recordMessageFilter, boolean bCloseOnFree)
    {
        this();
        this.init(null, recordMessageFilter, bCloseOnFree, DBConstants.BOOKMARK_HANDLE);
    }
    /**
    * OnSelectHandler - Constructor.
    */
    public GridSyncRecordMessageFilterHandler(GridRecordMessageFilter recordMessageFilter, boolean bCloseOnFree, int iHandleType)
    {
        this();
        this.init(null, recordMessageFilter, bCloseOnFree, iHandleType);
    }
    /**
    * Constructor.
    */
    public void init(Record record, GridRecordMessageFilter recordMessageFilter, boolean bCloseOnFree, int iHandleType)
    {
        super.init(record, recordMessageFilter, bCloseOnFree);
        m_iHandleType = iHandleType;
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
     * @param bDisplayOption Display the fields?
     */
    public void doValidRecord(boolean bDisplayOption) // init this field override for other value
    {
        Object bookmark = null;
        try   {
            bookmark = this.getOwner().getHandle(m_iHandleType);
        } catch (DBException ex)    {
            ex.printStackTrace();
        }
        this.addBookmarkFilter(bookmark);
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
            if (iChangeType == DBConstants.AFTER_ADD_TYPE)
            {
                Object bookmark = this.getOwner().getLastModified(m_iHandleType);
                if (bookmark != null)
                    this.addBookmarkFilter(bookmark);
            }
            if (iChangeType == DBConstants.AFTER_REQUERY_TYPE)
            {
                String strKeyName = null;
                Object objKeyData = null;
                if (this.getOwner().getDefaultOrder() > 0)
                {
                    strKeyName = this.getOwner().getKeyArea().getKeyName();
                    KeyField keyField = this.getOwner().getKeyArea().getKeyField(0);
                    BaseField fldKey = keyField.getField(DBConstants.FILE_KEY_AREA);
                    if (fldKey instanceof ReferenceField)
                    {
                        if (!keyField.getField(DBConstants.END_SELECT_KEY).isNull())
                            if (keyField.getField(DBConstants.END_SELECT_KEY).equals(keyField.getField(DBConstants.START_SELECT_KEY)))
                            {
                                fldKey = keyField.getField(DBConstants.START_SELECT_KEY);   // On a requery this should be the key
                                objKeyData = fldKey.getData();
                            }
                    }
                }
                this.clearBookmarkFilters(strKeyName, objKeyData);
            }
        }
        return iErrorCode;
    }
    /**
     * Update the listener to listen for changes to the following record.
     * @param bookmark The record to watch for changes to.
     */
    public void addBookmarkFilter(Object bookmark)
    {
        if (m_recordMessageFilter != null)
        {
            if (bookmark == null)   // Never
                bookmark = NO_BOOKMARK;
            Hashtable<String,Object> properties = new Hashtable<String,Object>();
            properties.put(GridRecordMessageFilter.ADD_BOOKMARK, bookmark);
            m_recordMessageFilter.updateFilterMap(properties);
        }
    }
    /**
     * Update the listener to listen for changes to no records.
     * @param strKeyName The (optional) key area the query is being of.
     * @param objKeyData The (optional) reference value of the key area.
     */
    public void clearBookmarkFilters(String strKeyName, Object objKeyData)
    {
        Map<String,Object> properties = new Hashtable<String,Object>();
        properties.put(GridRecordMessageFilter.ADD_BOOKMARK, GridRecordMessageFilter.CLEAR_BOOKMARKS);
        if (objKeyData != null)
        {
            properties.put(GridRecordMessageFilter.SECOND_KEY_HINT, strKeyName);
            properties.put(strKeyName, objKeyData);
        }
        m_recordMessageFilter.updateFilterMap(properties);
    }
}
