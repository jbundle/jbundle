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
import java.util.Hashtable;
import java.util.Map;

import org.jbundle.base.db.BaseDatabase;
import org.jbundle.base.db.BaseTable;
import org.jbundle.base.db.Record;
import org.jbundle.base.util.DBConstants;
import org.jbundle.base.util.DBParams;
import org.jbundle.thin.base.message.BaseMessage;
import org.jbundle.thin.base.message.BaseMessageFilter;
import org.jbundle.thin.base.message.BaseMessageHeader;
import org.jbundle.thin.base.message.MessageConstants;
import org.jbundle.thin.base.remote.RemoteSession;


/** 
 * A Record message is a message explaining the changes to this record.
 * NOTE: This object is sent over the wire, so make sure you only fill it with simple,
 * serializable objects!
 * @author  Administrator
 * @version 1.0.0
 */
public class BaseRecordMessageFilter extends BaseMessageFilter
    implements Serializable, RecordMessageConstants
{
	private static final long serialVersionUID = 1L;

	/**
     * The database type.
     */
    protected int m_iDatabaseType = 0;

    /**
     * Creates new RecordMessage.
     */
    public BaseRecordMessageFilter()
    {
        super();
    }
    /**
     * Constructor.
     */
    public BaseRecordMessageFilter(Record record, Object bookmark, Object source)
    {
        this();
        this.init(record, bookmark, source);
    }
    /**
     * Constructor.
     */
    public void init(Record record, Object bookmark, Object source)
    {
        if (source == null)
            source = record;
        Map<String,Object> properties = new HashMap<String,Object>();
        BaseDatabase database = null;
        if (record != null)
        {
            database = record.getTable().getDatabase();

            properties.put(DB_NAME, database.getDatabaseName(true));
            properties.put(TABLE_NAME, record.getTableNames(false));
            if (bookmark != null)
                properties.put(BOOKMARK, bookmark);

            m_iDatabaseType = record.getTable().getDatabase().getDatabaseType();
        }

        super.init(MessageConstants.RECORD_QUEUE_NAME, MessageConstants.INTRANET_QUEUE, source, properties);

        if (database != null)
        {
            if ((DBConstants.REMOTE != (record.getDatabaseType() & DBConstants.TABLE_TYPE_MASK))   // Track for remote files only
                && (DBConstants.REMOTE_MEMORY != (record.getDatabaseType() & DBConstants.TABLE_TYPE_MASK)))
                    this.setCreateRemoteFilter(false);
            if (DBConstants.FALSE.equals(database.getProperty(DBParams.CREATE_REMOTE_FILTER)))
                this.setCreateRemoteFilter(false);
            if (DBConstants.FALSE.equals(database.getProperty(DBParams.UPDATE_REMOTE_FILTER)))
                this.setUpdateRemoteFilter(false);
        }
    }
    /**
     * Free this object.
     */
    public void free()
    {
        Record record = null;
        if (this.getMessageSource() instanceof Record)
            record = (Record)this.getMessageSource();
        
        super.free();
        
        if (record != null)
        {
            BaseSyncRecordMessageFilterHandler fileListener = (BaseSyncRecordMessageFilterHandler)record.getListener(BaseSyncRecordMessageFilterHandler.class, false);
            if (fileListener != null)
                if (fileListener.getRecordMessageFilter() == this)
            {   // Always
                    // Note: Removing this fileListener will call free again for this object... shouldn't be a problem, since everything has been zeroed --- may want to fix this later
                record.removeListener(fileListener, true);
            }
        }
    }
    /**
     * Does this message header match this filter?
     * @param messageHeader The message header to check.
     * @return true if match, false if no match.
     */
    public boolean isFilterMatch(BaseMessageHeader messageHeader)
    {
        boolean bMatch = super.isFilterMatch(messageHeader);
        if (bMatch)
        {
            if (!(messageHeader instanceof RecordMessageHeader))
                return false;   // Never
            if (m_iDatabaseType != -1)
                if (m_iDatabaseType != ((RecordMessageHeader)messageHeader).getDatabaseType())
                    return false;   // No match
        }
        return bMatch;  // Match
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
        }
        return mxString;
    }
    /**
     * Try to figure out the remote session that this filter belongs to.
     * In this case, the remotesession should be the RecordOwner.
     * @return The remote session that belongs to this filter.
     */
    public Object getRemoteSession()
    {
        Object remoteSession = super.getRemoteSession();
        if (remoteSession == null)
            if (this.getMessageSource() instanceof Record)
        {
            Record record = (Record)this.getMessageSource();
            BaseTable table = record.getTable().getCurrentTable();
            if (DBParams.CLIENT.equals(table.getSourceType()))
                remoteSession = (RemoteSession)table.getRemoteTableType(java.rmi.server.RemoteStub.class);
            // Only pass the remoteSession if remoteSession is the client part of the remote session!
        }
        return remoteSession;
    }
    /**
     * Link this filter to this remote session.
     * This is ONLY used in the server (remote) version of a filter.
     * Override this to finish setting up the filter (such as behavior to adjust this filter).
     * In this case, the source cannot be passed to the remote session because it is the
     * record, so the record must be re-linked to this (remote) session.
     */
    public BaseMessageFilter linkRemoteSession(Object remoteSession)
    {
        if (remoteSession instanceof org.jbundle.base.remote.db.Session)
            if (m_source == null)
        {
            String strTableName = (String)this.getProperties().get(TABLE_NAME);
            Record record = ((org.jbundle.base.remote.db.Session)remoteSession).getRecord(strTableName);
            if (record != null)
            {
                record.addListener(new SyncRecordMessageFilterHandler(this, true));
                m_source = record;
            }
        }
        return super.linkRemoteSession(remoteSession);
    }
    /**
     * Do I send this message to the remote server?
     * @return true If I do (default).
     */
    public boolean isSendRemoteMessage(BaseMessage message)
    {
        if (message instanceof RecordMessage)   // Always
        {
            RecordMessageHeader recMessageHeader = (RecordMessageHeader)message.getMessageHeader();
            int iChangeType = recMessageHeader.getRecordMessageType();
            if (m_iDatabaseType == recMessageHeader.getDatabaseType())
                if (DBParams.CLIENT.equals(recMessageHeader.getSourceType()))   // Don't send only if Client DB is handling these messages.
            {
                if (!(this instanceof GridRecordMessageFilter))
                    if ((iChangeType == DBConstants.AFTER_UPDATE_TYPE)
                        || (iChangeType == DBConstants.AFTER_ADD_TYPE)
                        || (iChangeType == DBConstants.AFTER_DELETE_TYPE))
                            return false;    // The remote version of me handles this message remotely.
                if (this instanceof GridRecordMessageFilter)
                    if (iChangeType == DBConstants.AFTER_REQUERY_TYPE)
                        return false;    // The remote version of me handles this message remotely.
            }
        }
        return super.isSendRemoteMessage(message);
    }
    /**
     * Update this filter with this new information.
     * Override this to do something locally.
     * Remember to call super after updating this filter, as this method updates the remote copy of this filter.
     * @param properties New filter information (ie, bookmark=345).
     * @return The new filter change map (must contain enough information for the remote filter to sync).
     */
    public Map<String,Object> handleUpdateFilterMap(Map<String,Object> propFilter)
    {
        if (propFilter != null)
        {
            Object objDBType = propFilter.get(DB_TYPE);
            if (objDBType instanceof Integer)
                m_iDatabaseType = ((Integer)objDBType).intValue();
        }
        if (propFilter == null)
            propFilter = new Hashtable<String,Object>();
        propFilter.put(DB_TYPE, new Integer(m_iDatabaseType));   // Make sure remote has the same filter info
        return super.handleUpdateFilterMap(propFilter);   // Update any remote copy of this.
    }
    /**
     * Are these filters functionally the same?
     * @return true if they are.
     */
    public boolean isSameFilter(BaseMessageFilter filter)
    {
        if (filter.getClass().equals(this.getClass()))
        {
            if (this.get(DB_NAME) != null)
                if (this.get(DB_NAME).equals(filter.get(DB_NAME)))
                    if (this.get(TABLE_NAME) != null)
                        if (this.get(TABLE_NAME).equals(filter.get(TABLE_NAME)))
            {
                return true;
            }
            if (filter.isFilterMatch(this))
                ;
        }
        return false;
    }
}
