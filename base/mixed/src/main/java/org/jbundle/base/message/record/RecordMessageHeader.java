/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.message.record;

/**
 * RecordMessage.java
 *
 * Created on June 26, 2000, 3:19 AM
 */
 
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.jbundle.base.db.BaseTable;
import org.jbundle.base.db.Record;
import org.jbundle.base.model.DBConstants;
import org.jbundle.thin.base.message.BaseMessageHeader;
import org.jbundle.thin.base.message.MessageConstants;


/** 
 * A Record message is a message explaining the changes to this record.
 * NOTE: This object is sent over the wire, so make sure you only fill it with simple,
 * serializable objects!
 * @author  Administrator
 * @version 1.0.0
 */
public class RecordMessageHeader extends BaseMessageHeader
    implements Serializable, RecordMessageConstants
{
	private static final long serialVersionUID = 1L;

	/**
     * If set to true, update the record before processing this message.
     */
    public static final String UPDATE_ON_SELECT = "UPDATE_ON_SELECT";
    /**
     * For select type messages, this is the record to select.
     * Note: This CANNOT be sent as a regular message, it is only used in the OnSelectHandler direct message call.
     */
    public static final String RECORD_TO_UPDATE = "RECORD_TO_UPDATE";
    /**
     * The database type.
     */
    protected int m_iDatabaseType = 0;
    /**
     * The database source type;
     */
    protected String m_strSourceType = null;
    /**
     * The record message type (The type of record change).
     * DBConstants.SELECT_TYPE
     * DBConstants.AFTER_ADD_TYPE
     * DBConstants.AFTER_UPDATE_TYPE
     * DBConstants.AFTER_DELETE_TYPE
     * CACHE_UPDATE_TYPE - Cache was updated, update screen not gridcache.
     */
    protected int m_iRecordMessageType = -1;
    /**
     * Hints (for add new record).
     */
    protected Map<String,Object> m_mapHints = null;

    /**
     * Creates new RecordMessage.
     */
    public RecordMessageHeader()
    {
        super();
    }
    /**
     * Constructor.
     */
    public RecordMessageHeader(Record record, Object bookmark, Object source, int iRecordMessageType, Map<String, Object> mapHints)
    {
        this();
        this.init(record, bookmark, source, iRecordMessageType, mapHints);
    }
    /**
     * Constructor.
     */
    public void init(Record record, Object bookmark, Object source, int iRecordMessageType, Map<String, Object> mapHints)
    {
        if (source == null)
            source = record;
        Map<String,Object> properties = new HashMap<String,Object>();
        if (record != null)
        {
            properties.put(DB_NAME, record.getTable().getDatabase().getDatabaseName(true)); // Physical database must match
            properties.put(TABLE_NAME, record.getTableNames(false));
            if (bookmark == null)
                bookmark = NO_BOOKMARK;
            properties.put(BOOKMARK, bookmark);

            m_strSourceType = record.getTable().getSourceType();
            m_iDatabaseType = record.getTable().getDatabase().getDatabaseType();
        }
        m_iRecordMessageType = iRecordMessageType;
        m_mapHints = mapHints;
        super.init(MessageConstants.RECORD_QUEUE_NAME, MessageConstants.INTRANET_QUEUE, source, properties);
    }
    /**
     * Free this object.
     */
    public void free()
    {
        super.free();
    }
    /**
     * Get the database type.
     * @return The database type.
     */
    public int getDatabaseType()
    {
        return m_iDatabaseType;
    }
    /**
     * Get the name/value pairs in an ordered tree.
     * Note: Replace this with a DOM tree when it is available in the basic SDK.
     * @return A matrix with the name, type, etc.
     */
    public Object[][] createNameValueTree(Object mxString[][], Map<String, Object> properties)
    {
        mxString = super.createNameValueTree(mxString, properties);
        if (properties != null)
        {
            mxString = this.addNameValue(mxString, DB_NAME, properties.get(DB_NAME));
            mxString = this.addNameValue(mxString, TABLE_NAME, properties.get(TABLE_NAME));
            mxString = this.addNameValue(mxString, BOOKMARK, properties.get(BOOKMARK));
        }
        return mxString;
    }
    /**
     * Get the bookmark.
     */
    public Object getBookmark(int iHandleType)
    {
        Map<String,Object> properties = this.getProperties();
        Object bookmark = properties.get(BOOKMARK);
        if (iHandleType == DBConstants.BOOKMARK_HANDLE)
            return bookmark;
        if (iHandleType == DBConstants.FULL_OBJECT_HANDLE)
        {
            String strDatabaseName = (String)properties.get(DB_NAME);
            String strTableName = (String)properties.get(TABLE_NAME);
            // Must match the FULL_OBJECT_HANDLE format!
            String strSource = m_strSourceType + BaseTable.HANDLE_SEPARATOR + strDatabaseName + BaseTable.HANDLE_SEPARATOR + strTableName;
            strSource += BaseTable.HANDLE_SEPARATOR + bookmark.toString();
            return strSource;
        }
        return null;
    }
    /**
     * Get the record message type.
     */
    public int getRecordMessageType()
    {
        return m_iRecordMessageType;
    }
    /**
     * Should this record get this message?
     */
    public boolean isRecordMatch(Record record)
    {
        Map<String,Object> properties = this.getProperties();
        String strTableName = (String)properties.get(TABLE_NAME);
//        Object dsBookmark = this.getBookmark(DBConstants.BOOKMARK_HANDLE);
        if (record != null)
            if (record.getTableNames(false).equals(strTableName))
                return true;    // Match
        return false; // No match
    }
    /**
     * Does this key area hint match my hint?
     * @param strKeyArea The key area to match.
     * @param objKeyData The data to match.
     * @return true if match.
     */
    public boolean isMatchHint(String strKeyArea, Object objKeyData)
    {
        if (m_mapHints != null)
            if (objKeyData.equals(m_mapHints.get(strKeyArea)))
                return true;    // Match;
        return false;   // No match
    }
    /**
     * Return this table drive type for the getObjectSource call. (Override this method)
     * @return java.lang.String The source type.
     */
    public String getSourceType()
    {
        return m_strSourceType;
    }
}
