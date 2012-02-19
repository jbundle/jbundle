/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.db.client;

import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import org.jbundle.model.DBException;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.FieldTable;
import org.jbundle.thin.base.db.util.ArrayCache;
import org.jbundle.thin.base.remote.RemoteDatabase;
import org.jbundle.thin.base.remote.RemoteException;
import org.jbundle.thin.base.remote.RemoteTable;
import org.jbundle.thin.base.util.ThinUtil;


/**
 * Cached list of Records.
 * <p />This table caches the last x recently accessed records and returns them on demand.
 * <p />This class also synchronizes the physical access with the currently cached record, ie., if
 * you just read a cached record, and now you want to update it, this class will do the physical
 * read of the cached record to process a correct set/update.
 * <p />This fakes the client into thinking I'm a RemoteTable and passes the calls on.
 * <p />Note: This caches records two ways, if the first call is a seek, records are cached by primary key,
 * if records are first accessed by get(x) or move(x), then records are cached by row position.
 * NOTE: This model assumes you start with an empty list and then you add the items after the initial display.
 * WARNING: The mapCache that is used to cache seek(s) has no upper limit, so only use it for a small number of records.
 * <p>This class caches the following types of access:
 * <br>- get(x) - All get calls retrieve several records starting with the one requested.
 * <br>- seek(xxx) - All seek calls cache the seeked record by it's key for possible repeat seek.
 * <br>- move(x) - Moves are not cached UNLESS the record is read-only (because of the difficulty of repositions for updates/deletes).
 * NOTE: You should NOT use this with thick client (although it will work) because thick clients don't know
 * how to invalidate a cache entry if they get a message that the entry has changed.
 */
public class CachedRemoteTable extends Object
    implements RemoteTable
{
    /**
     * If user is using get(x) or move(x), cache by array position.
     */
    protected ArrayCache m_mapCache = null;
    /**
     * If user is using seek, cache by primary key.
     */
    protected Hashtable<Object,Object> m_htCache = null;
    /**
     * The htCache was populated by gets [true] or moves [false].
     */
    protected boolean m_bhtGet = true;
    /**
     * The remote table.
     */
    protected RemoteTable m_tableRemote = null;
    /**
     * A placeholder for a deleted record.
     */
    public static final Object NONE = new Integer(-1);
    /**
     * Update the cache on a set (if not, re-read after write).
     */
    protected boolean m_bCacheOnWrite = true;
    /**
     * Currently read physical record.
     */
    protected Object m_objCurrentPhysicalRecord = NONE;
    /**
     * Currently locked physical record.
     */
    protected Object m_objCurrentLockedRecord = NONE;
    /**
     * Currently read cached record (App thinks this is the current physical record).
     */
    protected Object m_objCurrentCacheRecord = NONE;
    /**
     * Current move position.
     */
    protected int m_iCurrentLogicalPosition = -1;
    /**
     * End of file row when reached.
     */
    protected int m_iPhysicalLastRecordPlusOne = -1;
    /**
     * The open mode of the last open.
     */
    protected int m_iOpenMode = Constants.OPEN_NORMAL;
    /**
     * Number of records read in a multiple read.
     */
    public static final int MULTIPLE_READ_COUNT = 12;

    /**
     * Constructor.
     */
    public CachedRemoteTable()
    {
        super();
    }
    /**
     * Constructor.
     * @param The remotetable (actually, the ThinTable).
     */
    public CachedRemoteTable(RemoteTable tableRemote)
    {
        this();
        this.init(tableRemote);
    }
    /**
     * Constructor.
     * @param The remotetable (actually, the ThinTable).
     */
    public void init(RemoteTable tableRemote)
    {
        m_tableRemote = tableRemote;
        m_objCurrentPhysicalRecord = NONE;
        m_objCurrentLockedRecord = NONE;
        m_objCurrentCacheRecord = NONE;
        m_iCurrentLogicalPosition = -1;
        m_iPhysicalLastRecordPlusOne = -1;
    }
    /**
     * Free is never called for a CachedTable.
     */
    public void free() throws RemoteException
    {
        this.freeRemoteSession();
    }
    /**
     * Free the remote table.
     */
    public void freeRemoteSession() throws RemoteException
    {
        if (m_tableRemote != null)
            m_tableRemote.freeRemoteSession();
        m_tableRemote = null;
        m_mapCache = null;
        m_htCache = null;
    }
    /**
     * Get the remote table reference.
     * If you want the remote table session, call this method with Remote.class.
     * @classType The base class I'm looking for (If null, return the next table on the chain) 
     * @return The remote table reference.
     */
    public RemoteTable getRemoteTableType(Class<?> classType) throws RemoteException
    {
        return ThinUtil.getRemoteTableType(m_tableRemote, classType);
    }
    /**
     * Set my remote session.
     */
    public void setRemoteTable(RemoteTable tableRemote)
    {
        m_tableRemote = tableRemote;
    }
    /**
     * Open - Open this table for a query.
     * @param strKeyArea The default key area (null to leave unchanged).
     * @param strFields The fields to select (comma separated).
     * @param objInitialKey The start key (as a raw data object or a BaseBuffer).
     * @param objEndKey The end key (as a raw data object or a BaseBuffer).
     * @param byBehaviorData A steam describing the behaviors to add and the initialization data.
     * @exception DBException File exception.
     */
    public void open(String strKeyArea, int iOpenMode, boolean bDirection, String strFields, Object objInitialKey, Object objEndKey, byte[] byBehaviorData) throws DBException, RemoteException
    {
        m_tableRemote.open(strKeyArea, iOpenMode, bDirection, strFields, objInitialKey, objEndKey, byBehaviorData);
        m_mapCache = null;
        m_htCache = null;
        m_iOpenMode = iOpenMode;

        m_objCurrentPhysicalRecord = NONE;
        m_objCurrentLockedRecord = NONE;
        m_objCurrentCacheRecord = NONE;
        m_iCurrentLogicalPosition = -1;
        m_iPhysicalLastRecordPlusOne = -1;
    }
    /**
     * Add - add this data to the file.
     * @param data A vector object containing the raw data for the record.
     * @exception Exception File exception.
     */
    public Object add(Object data, int iOpenMode) throws DBException, RemoteException
    {
        m_objCurrentPhysicalRecord = NONE;
        m_objCurrentLockedRecord = NONE;
        m_objCurrentCacheRecord = NONE;
        Object bookmark = m_tableRemote.add(data, iOpenMode);
        if (m_iPhysicalLastRecordPlusOne != -1)
            if (m_bCacheOnWrite)
        {
            if (m_mapCache != null)
            {
                m_mapCache.set(m_iPhysicalLastRecordPlusOne, data);
                m_iPhysicalLastRecordPlusOne++;
            }
        }
        return bookmark;
    }
    /**
     * Lock the current record.
     * This method responds differently depending on what open mode the record is in:
     * OPEN_DONT_LOCK - A physical lock is not done. This is usually where deadlocks are possible
     * (such as screens) and where transactions are in use (and locks are not needed).
     * OPEN_LOCK_ON_EDIT - Holds a lock until an update or close. (Update crucial data, or hold records for processing)
     * Returns false is someone alreay has a lock on this record.
     * OPEN_WAIT_FOR_LOCK - Don't return from edit until you get a lock. (ie., Add to the total).
     * Returns false if someone has a hard lock or time runs out.
     * @return true if successful, false is lock failed.
     * @exception DBException FILE_NOT_OPEN
     * @exception DBException INVALID_RECORD - Record not current.
     */
    public int edit(int iOpenMode) throws DBException, RemoteException
    {
        this.checkCurrentCacheIsPhysical(null);
        m_objCurrentLockedRecord = m_objCurrentPhysicalRecord;
        return m_tableRemote.edit(iOpenMode);
    }
    /**
     * Update the current record.
     * @param The data to update.
     * @exception Exception File exception.
     */
    public void set(Object data, int iOpenMode) throws DBException, RemoteException
    {
        int iErrorCode = this.checkCurrentCacheIsPhysical(data);
        if (iErrorCode != Constants.NORMAL_RETURN)
            throw new DBException(iErrorCode);  // Data probably changed from last time I read it.
        m_tableRemote.set(data, iOpenMode);
        if (m_objCurrentCacheRecord != null)
        {
            if (m_mapCache != null)
            {
                if (m_bCacheOnWrite)
                    m_mapCache.set(((Integer)m_objCurrentCacheRecord).intValue(), data);
                else
                    m_mapCache.set(((Integer)m_objCurrentCacheRecord).intValue(), null);
            }
            else if (m_htCache != null)
            {
                m_htCache.remove(m_objCurrentCacheRecord);  // Do not put the new record back, as you don't know the new key.
            }
        }
        m_objCurrentPhysicalRecord = NONE;
        m_objCurrentLockedRecord = NONE;
        m_objCurrentCacheRecord = NONE;
    }
    /**
     * Delete the current record.
     * @param - This is a dummy param, because this call conflicts with a call in EJBHome.
     * @exception Exception File exception.
     */
    public void remove(Object data, int iOpenMode) throws DBException, RemoteException
    {
        this.checkCurrentCacheIsPhysical(null);
        m_tableRemote.remove(data, iOpenMode);
        if (m_objCurrentCacheRecord != null)
        {
            if (m_mapCache != null)
                m_mapCache.set(((Integer)m_objCurrentCacheRecord).intValue(), NONE);
            else if (m_htCache != null)
                m_htCache.remove(m_objCurrentCacheRecord);
        }
        m_objCurrentPhysicalRecord = NONE;
        m_objCurrentLockedRecord = NONE;
        m_objCurrentCacheRecord = NONE;
    }
    /**
     * Move the current position and read the record (optionally read several records).
     * Note: The cache only kicks in for move(+1).
     * @param iRelPosition relative Position to read the next record.
     * @param iRecordCount Records to read.
     * @return If I read 1 record, this is the record's data.
     * @return If I read several records, this is a vector of the returned records.
     * @return If at EOF, or error, returns the error code as a Integer.
     * @exception Exception File exception.
     */
    public Object doMove(int iRelPosition, int iRecordCount) throws DBException, RemoteException
    {
        boolean bShouldBeCached = (((m_iOpenMode & Constants.OPEN_CACHE_RECORDS) == Constants.OPEN_CACHE_RECORDS)
                || ((m_iOpenMode & Constants.OPEN_READ_ONLY) == Constants.OPEN_READ_ONLY));
        if (!bShouldBeCached)
        {   // If it is not read-only or explicit request, don't cache (it is way too hard to reposition the records for an update/remove).
            m_objCurrentPhysicalRecord = NONE;
            m_objCurrentLockedRecord = NONE;
            m_objCurrentCacheRecord = NONE;
            return m_tableRemote.doMove(iRelPosition, iRecordCount);
        }
        else
        {   // If it is read-only, use the hash table cache to do a multiple read.
            int iCurrentLogicalPosition = m_iCurrentLogicalPosition + iRelPosition;
            if (iRelPosition == Constants.FIRST_RECORD)
                iCurrentLogicalPosition = 0;
            if (iRelPosition == Constants.LAST_RECORD)
            {
                if (m_iPhysicalLastRecordPlusOne == -1)
                    iCurrentLogicalPosition = -1;
                else
                    iCurrentLogicalPosition = m_iPhysicalLastRecordPlusOne - 1;
            }
            return this.cacheGetMove(iRelPosition, iRecordCount, iCurrentLogicalPosition, false);
        }
    }
    /**
     * Move or get this record and cache multiple records if possible.
     * @param iRowOrRelative For get the row to retrieve, for move the relative row to retrieve.
     * @param iRowCount The number of rows to retrieve (Used only by EjbCachedTable).
     * @param iAbsoluteRow The absolute row of the first row to retrieve (or -1 if unknown).
     * @param bGet If true get, if false move.
     * @return The record or an error code as an Integer.
     * @exception Exception File exception.
     * @exception RemoteException RMI exception.
     */
    public Object cacheGetMove(int iRowOrRelative, int iRowCount, int iAbsoluteRow, boolean bGet) throws DBException, RemoteException
    {
        m_bhtGet = bGet;
        m_objCurrentPhysicalRecord = NONE;
        m_objCurrentCacheRecord = NONE;
        m_iCurrentLogicalPosition = iAbsoluteRow;
        if ((m_iPhysicalLastRecordPlusOne != -1) && (m_iCurrentLogicalPosition >= m_iPhysicalLastRecordPlusOne))
            return FieldTable.EOF_RECORD;
        try   {
            Object objData = null;
            if ((m_mapCache == null) && (m_htCache == null))
                m_mapCache = new ArrayCache();
            if (m_mapCache != null) if (iAbsoluteRow != -1)
                objData = m_mapCache.get(iAbsoluteRow);   // Try to find this in the cache
            if (objData == NONE)
            {
                objData = FieldTable.DELETED_RECORD;        // Deleted record
            }
            else if (objData != null)
            {
                m_objCurrentCacheRecord = new Integer(iAbsoluteRow);
            }
            else if (objData == null)
            {
                m_objCurrentLockedRecord = NONE;    // You lose your lock on physical move.
                if (m_mapCache != null)
                    if (iAbsoluteRow == m_mapCache.getEndIndex() + 1)
                        iRowCount = MULTIPLE_READ_COUNT;    // If you are adding to the end of the cache, try reading a bunch at once.
                if (bGet)
                    objData = m_tableRemote.get(iRowOrRelative, iRowCount);
                else
                    objData = m_tableRemote.doMove(iRowOrRelative, iRowCount);
                if (objData instanceof Vector)
                    if (((Vector)objData).size() > 1)
                        if (!(((Vector)objData).get(0) instanceof Vector))
                            iRowCount = 1;  // Multiple read not supported (This vector is a record).
                if (objData instanceof Vector)
                {
                    if (m_mapCache != null)
                    {
                        m_objCurrentCacheRecord = new Integer(m_iCurrentLogicalPosition);
                        if (iRowCount == 1)
                        {
                            m_mapCache.set(m_iCurrentLogicalPosition, objData);
                            m_objCurrentPhysicalRecord = m_objCurrentCacheRecord;
                        }
                        else
                        {
                            Vector<Object> objectVector = (Vector)objData;
                            for (int i = objectVector.size() - 1; i >= 0; i--)
                            {   // I go in reverse, so the objData object will be the object at the iAbsoluteRow.
                                objData = objectVector.get(i);
                                if (objData instanceof Vector)
                                {
                                    if (iAbsoluteRow != -1)
                                    {
                                        m_mapCache.set(iAbsoluteRow + i, objData);
                                        if (i == objectVector.size() - 1)
                                            m_objCurrentPhysicalRecord = new Integer(iAbsoluteRow + i);
                                    }
                                }
                                else
                              if ((m_iPhysicalLastRecordPlusOne == -1) || (m_iPhysicalLastRecordPlusOne <= m_iCurrentLogicalPosition + i))
                                    m_iPhysicalLastRecordPlusOne = m_iCurrentLogicalPosition + i;   // Recordstatus = EOF
                              else
                                  objData = FieldTable.DELETED_RECORD;        // Deleted record
                            }
                        }
                    }
                }
                else
                {
                    if ((m_iPhysicalLastRecordPlusOne == -1) || (m_iPhysicalLastRecordPlusOne <= m_iCurrentLogicalPosition))
                        m_iPhysicalLastRecordPlusOne = m_iCurrentLogicalPosition;
                    else
                    {
                        if ((!FieldTable.DELETED_RECORD.equals(objData)) && (!FieldTable.EOF_RECORD.equals(objData)))
                            objData = FieldTable.DELETED_RECORD;        // Deleted record (if not one that I recognize)
                    }
                }
            }
            return objData;
        } catch (RemoteException ex) {
            throw ex;
        }
    }
    /**
     * Retrieve the record that matches this key.
     * @param strSeekSign Which way to seek null/= matches data also &gt;, &lt;, &gt;=, and &lt;=.
     * @param strKeyArea The name of the key area to seek on.
     * @param objKeyData The data for the seek (The raw data if a single field, a BaseBuffer if multiple).
     * @returns The record (as a vector) if successful, The return code (as an Boolean) if not.
     * @exception DBException File exception.
     */
    public Object seek(String strSeekSign, int iOpenMode, String strKeyArea, String strFields, Object objKeyData) throws DBException, RemoteException
    {
        m_objCurrentLockedRecord = NONE;
        m_objCurrentCacheRecord = NONE;
        try   {
            Object objData = null;
            if ((m_mapCache == null) && (m_htCache == null))
                m_htCache = new Hashtable<Object,Object>();    // Okay, start caching keys and records
            if (m_htCache != null)
            {
                if ((strKeyArea == null)
                    || (strKeyArea.length() == 0)
                    || (strKeyArea.equalsIgnoreCase(Constants.PRIMARY_KEY)))
                        if (objKeyData instanceof Integer)
                            objData = m_htCache.get(objKeyData);    // Try to find this in the cache
            }
            m_mapCache = null;  // If seek is used, the grid cache is no good anymore.
            if (objData != null)
                m_objCurrentCacheRecord = objKeyData;   // Current data is Cached
            if (objData == null)
            {
                m_objCurrentPhysicalRecord = NONE;
                objData = m_tableRemote.seek(strSeekSign, iOpenMode, strKeyArea, strFields, objKeyData);
                if (objData instanceof Vector)
                    if (m_htCache != null)
                {
                    m_objCurrentPhysicalRecord = objKeyData;    // Current data is current
                    m_objCurrentCacheRecord = objKeyData;
                    m_htCache.put(objKeyData, objData);
                }
            }
            return objData;
        } catch (RemoteException ex) {
            throw ex;
        }
    }
    /**
     * Reposition to this record using this bookmark.<p/>
     * JiniTables can't access the datasource on the server, so they must use the bookmark.
     */
    public Object doSetHandle(Object bookmark, int iOpenMode, String strFields, int iHandleType) throws DBException, RemoteException
    {
        m_objCurrentCacheRecord = NONE;
        m_objCurrentLockedRecord = NONE;
        try {   // This caching logic is exactly the same as seek caching.
            Object objData = null;
            if ((m_mapCache == null) && (m_htCache == null))
                m_htCache = new Hashtable<Object,Object>();    // Okay, start caching keys and records
            if (m_htCache != null)
            {
                if (bookmark instanceof Integer)
                    objData = m_htCache.get(bookmark);    // Try to find this in the cache
            }
            m_mapCache = null;  // If seek is used, the grid cache is no good anymore.
            if (objData != null)
                m_objCurrentCacheRecord = bookmark;   // Current data is Cached
            if (objData == null)
            {
                m_objCurrentPhysicalRecord = NONE;
                objData = m_tableRemote.doSetHandle(bookmark, iOpenMode, strFields, iHandleType);
                if (objData instanceof Vector)
                    if (m_htCache != null)
                {
                    m_objCurrentPhysicalRecord = bookmark;    // Current data is current
                    m_objCurrentCacheRecord = bookmark;
                    m_htCache.put(bookmark, objData);
                }
            }
            return objData;
        } catch (RemoteException ex) {
            throw ex;
        }
    }
    /**
     * Get the DATA_SOURCE_HANDLE to the last modified or added record.
     */
    public Object getLastModified(int iHandleType) throws DBException, RemoteException
    {
        m_objCurrentPhysicalRecord = NONE;
        m_objCurrentCacheRecord = NONE;
        return m_tableRemote.getLastModified(iHandleType);
    }
    /**
     * Receive this relative record in the table.
     * <p>Note: This is usually used only by thin clients, as thick clients have the code to
     * fake absolute access.
     * @param iRowIndex The row to retrieve.
     * @param iRowCount The number of rows to retrieve (Used only by EjbCachedTable).
     * @return The record(s) or an error code as an Integer.
     * @exception Exception File exception.
     */
    public Object get(int iRowIndex, int iRowCount) throws DBException, RemoteException
    {
        return this.cacheGetMove(iRowIndex, iRowCount, iRowIndex, true);
    }
    /**
     * Make sure this record is the expected current physical record (physically read the cached record).
     * @param data Data that this row should contain.
     * @param iTargetRow Row that needs to be made current.
     * @return If the physical record has changed, return a 
     * @exception Exception File exception.
     * @exception RemoteException RMI exception.
     */
    public int checkCurrentCacheIsPhysical(Object data) throws DBException, RemoteException
    {
        int iErrorCode = Constants.NORMAL_RETURN;
        Object objTargetRow = m_objCurrentCacheRecord;
        if (objTargetRow != NONE)
        {
            if (m_mapCache != null)
            {
                int iTargetRow = ((Integer)objTargetRow).intValue();
                int iCurrentPhysicalRecord = ((Integer)m_objCurrentPhysicalRecord).intValue();
                if (iTargetRow != iCurrentPhysicalRecord)
                {   // Current cached record has not been read... read it.
                    try   {
                        if (m_bhtGet)
                            data = m_tableRemote.get(iTargetRow, 1);
                        else
                        {
//x                            Object dataRecord = m_mapCache.get(iTargetRow);
//x                            if (dataRecord instanceof Vector)
//x                            {
//x                                Object bookmark = ((Vector)dataRecord).get(0);
//x                                data = m_tableRemote.doSetHandle(bookmark, " *", 0);
//x                            }
                            throw new DBException("Can't update: Only read-only grid tables can be cached");    // Never (A moveNext is never updated)
                        }
                        if (data instanceof Vector)
                            m_objCurrentPhysicalRecord = objTargetRow;  // Always
                    } catch (RemoteException ex) {
                        throw ex;
                    }
                }
                if (data != null)
                    if (data instanceof Vector)
                        m_mapCache.set(iTargetRow, data); // Make sure the cache matches the actual
                m_objCurrentCacheRecord = objTargetRow;
            }
            if (m_htCache != null)
            {
                if (!objTargetRow.equals(m_objCurrentPhysicalRecord))
                {   // Current cached record has not been read... read it.
                    try   {
                        data = m_tableRemote.seek(Constants.EQUALS, Constants.OPEN_NORMAL | Constants.OPEN_DONT_UPDATE_LAST_READ, Constants.PRIMARY_KEY, null, objTargetRow);
                        if (data instanceof Vector)
                        {
                            m_objCurrentPhysicalRecord = objTargetRow;  // Always
// Don't check the data, since the client doesn't handle mergeToCurrent (This should never happen, since I pass the OPEN_DONT_UPDATE_LAST_READ)
//                            Object oldData = m_htCache.get(objTargetRow);  // Get the old copy
//                            if ((oldData != null) && (!oldData.equals(data)))
//                                iErrorCode = Constants.INVALID_RECORD;  // Must have changed from the last time I read it.
                        }
                        else
                            iErrorCode = Constants.INVALID_RECORD;  // Must have been deleted
                    } catch (RemoteException ex) {
                        throw ex;
                    }
                }
                if (data != null)
                    if (data instanceof Vector)
                        m_htCache.put(objTargetRow, data);  // Make sure the cache matches the actual
                m_objCurrentCacheRecord = objTargetRow;
            }
        }
        return iErrorCode;
    }
    /**
     * Clear this entry in the cache, so the next access will get the remote data.
     * @param iTargetRow Row that needs to be updated.
     * @param data Data that this row should contain (null to clear the cache entry).
     */
    public boolean setCache(Object objTargetRow, Object data)
    {
        if (objTargetRow != NONE)
        {
            if (m_mapCache != null)
            {
                int iTargetRow = ((Integer)objTargetRow).intValue();
                int iCurrentPhysicalRecord = ((Integer)m_objCurrentPhysicalRecord).intValue();
                if (iTargetRow == iCurrentPhysicalRecord)
                {   // Reset the current record
                    m_objCurrentPhysicalRecord = NONE;
                    m_objCurrentCacheRecord = NONE;
                    m_objCurrentLockedRecord = NONE;
                }
                if (m_iPhysicalLastRecordPlusOne == iTargetRow)
                    m_iPhysicalLastRecordPlusOne++;
                m_mapCache.set(iTargetRow, data); // Make sure the cache matches the actual
                return true;
            }
            if (m_htCache != null)
            {
                if (objTargetRow.equals(m_objCurrentPhysicalRecord))
                {   // Reset the current record.
                    m_objCurrentPhysicalRecord = NONE;
                    m_objCurrentCacheRecord = NONE;
                    m_objCurrentLockedRecord = NONE;
                }
                if (data != null)
                    m_htCache.put(objTargetRow, data);  // Make sure the cache matches the actual
                else
                    m_htCache.remove(objTargetRow);
                return true;
            }
        }
        return false; // nothing updated
    }
    /**
     * Enable/Disable autosequence for this table.
     */
    public void setRemoteProperty(String strProperty, String strValue) throws RemoteException
    {
        m_tableRemote.setRemoteProperty(strProperty, strValue);
    }
    /**
     * Get the remote table for this session.
     * @param strRecordName Table Name or Class Name of the record to find
     * Note: This method is used when the SessionObject is used as an application's remote peer.
     */
    public RemoteTable getRemoteTable(String strRecordName) throws RemoteException
    {
        return m_tableRemote.getRemoteTable(strRecordName);
    }
    /**
     * Do a remote action.
     * @param strCommand Command to perform remotely.
     * @param properties Properties for this command (optional).
     * @return boolean success.
     */
    public Object doRemoteAction(String strCommand, Map<String, Object> properties) throws DBException, RemoteException
    {
        return m_tableRemote.doRemoteAction(strCommand, properties);
    }
    /**
     * make a thin FieldList for this table.
     * Usually used for special queries that don't have a field list available.
     */
    public org.jbundle.thin.base.db.FieldList makeFieldList(String strFieldsToInclude) throws RemoteException
    {
        return m_tableRemote.makeFieldList(strFieldsToInclude);
    }
    /**
     * Search through the buffers for this bookmark.
     * @return int index in table; or -1 if not found.
     * @param bookmark java.lang.Object
     */
    public int bookmarkToIndex(Object bookmark)
    {
        if (bookmark != null)
            if (m_mapCache != null)
        {
            for (int iRowIndex = 0; iRowIndex < m_mapCache.size(); iRowIndex++)
            {
                Object data = m_mapCache.get(iRowIndex);
                if (data instanceof Vector)
                {
                    Object objID = ((Vector)data).get(0);
                    if (bookmark.equals(objID))
                        return iRowIndex;
                }
            }
        }
        return -1;  // Not found
    }
    /**
     * Build a new remote session and initialize it.
     * @param parentSessionObject The parent session for this new session (if null, parent = me).
     * @param strSessionClassName The class name of the remote session to build.
     */
    public org.jbundle.thin.base.remote.RemoteBaseSession makeRemoteSession(String strSessionClassName) throws RemoteException
    {
        return m_tableRemote.makeRemoteSession(strSessionClassName);
    }
    /**
     * Link the filter to this remote session.
     * This is a special method that is needed because the remote link is passed a remote reference to the session
     * even though it is in the same JVM. What you need to do in your implementation is lookup the message filter
     * and call messageFilter.linkRemoteSession(this); See RemoteSession Object for the Only implementation.
     * NOTE: This is never called from Client -> Server, so an implementation is not needed here.
     * @param messageFilter A serialized copy of the messageFilter to link this session to.
     */
    public org.jbundle.thin.base.message.BaseMessageFilter setupRemoteSessionFilter(org.jbundle.thin.base.message.BaseMessageFilter messageFilter) throws RemoteException
    {
        return null;
    }
    /**
     * Get/Make this remote database session for this table session.
     * @param properties The client database properties (Typically for transaction support).
     */
    public RemoteDatabase getRemoteDatabase(Map<String, Object> properties) throws RemoteException
    {
        return m_tableRemote.getRemoteDatabase(properties);
    }
}
