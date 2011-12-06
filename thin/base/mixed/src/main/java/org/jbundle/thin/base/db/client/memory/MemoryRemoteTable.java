/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.db.client.memory;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;

import org.jbundle.model.DBException;
import org.jbundle.model.db.Rec;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.FieldList;
import org.jbundle.thin.base.remote.RemoteDatabase;
import org.jbundle.thin.base.remote.RemoteException;
import org.jbundle.thin.base.remote.RemoteTable;


/**
 * This class creates a simple memory-based table that responds like a remote table.
 * NOTE: If you have more than on task sharing a table, use the SyncRemoteTable in
 * front of this table.
 * WARNING: Do not use MemoryRemoteTable in a updateable GridScreen, since MemoryRemoteTable
 * uses a SortedMap, the order and position of all the entries changed every time
 * there is a remove, set, or add. Instead, use the MemoryFieldTable!
 * Use this class for simple tasks such as creating a sorted list to populate a
 * display screen.
 */
public class MemoryRemoteTable extends Object
    implements RemoteTable
{
    /**
     * List of physical data.
     */
    protected SortedMap<Object,Object> m_mDataMap = null;
    /**
     * Current key for remove/set.
     */
    protected Object m_objCurrentKey = null;
    /**
     * Current record for get(x).
     */
    protected int m_iCurrentRecord = -1;
    /**
     * Sequential read iterator - currently on m_iCurrentRecord.
     */
    protected Iterator<Object> m_iterator = null;

    /**
     * Constructor.
     */
    public MemoryRemoteTable()
    {
        super();
    }
    /**
     * Constructor.
     * @param The remotetable (actually, the ThinTable).
     */
    public MemoryRemoteTable(FieldList record)
    {
        this();
        this.init(record);
    }
    /**
     * Constructor.
     * @param The remotetable (actually, the ThinTable).
     */
    public void init(Rec record)
    {
        m_objCurrentKey = null;
        m_mDataMap = new TreeMap<Object,Object>();
        m_iCurrentRecord = -1;
        m_iterator = null;
    }
    /**
     * Free is never called for a CachedTable.
     */
    public void free() throws RemoteException
    {
        this.freeRemoteSession(); // Same
    }
    /**
     * Receive to this server and send the response.
     */
    public void freeRemoteSession() throws RemoteException
    {
        m_objCurrentKey = null;
        m_mDataMap = null;
        m_iCurrentRecord = -1;
        m_iterator = null;
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
        m_objCurrentKey = null;
        m_iCurrentRecord = -1;
        m_iterator = null;
    }
    /**
     * Add add this data to the file.
     * @param data A vector object containing the raw data for the record.
     * @exception Exception File exception.
     */
    public Object add(Object data, int iOpenMode) throws DBException, RemoteException
    {
        Object key = this.getKey(data);
        if (key == null)
        {   // If autosequence, bump key
            try   {
                key = m_mDataMap.lastKey();
            } catch (NoSuchElementException ex)   {
                key = new Integer(0);
            }
            if (key instanceof Integer)   // Always
                key = new Integer(((Integer)key).intValue() + 1);
            ((Vector)data).set(0, key);
        }
        if (m_mDataMap.get(key) != null)
            throw new DBException("Duplicate record");
        m_mDataMap.put(key, data);
        m_objCurrentKey = null;
        m_iterator = null;
        
        return key;
    }
    /**
     * Lock the current record (not currently supported).
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
        return Constants.NORMAL_RETURN;
    }
    /**
     * Update the current record.
     * @param The data to update.
     * @exception Exception File exception.
     */
    public void set(Object data, int iOpenMode) throws DBException, RemoteException
    {
        if (m_objCurrentKey == null)
            throw new DBException(Constants.INVALID_RECORD);
        Object key = this.getKey(data);
        if (!key.equals(m_objCurrentKey))
            m_mDataMap.remove(key);
        m_mDataMap.put(key, data);      // Replace the data at this position
        m_objCurrentKey = null;
    }
    /**
     * Delete the current record.
     * @param - This is a dummy param, because this call conflicts with a call in EJBHome.
     * @exception Exception File exception.
     */
    public void remove(Object data, int iOpenMode) throws DBException, RemoteException
    {
        if (m_objCurrentKey == null)
            throw new DBException(Constants.INVALID_RECORD);
        m_mDataMap.remove(m_objCurrentKey);
        if (m_iCurrentRecord != -1)
            m_iCurrentRecord--;     // This will keep the sequential read correct.
        m_iterator = null;
    }
    /**
     * Move the current position and read the record (optionally read several records).
     * @param iRelPosition relative Position to read the next record.
     * @param iRecordCount Records to read.
     * @return If I read 1 record, this is the record's data.
     * @return If I read several records, this is a vector of the returned records.
     * @return If at EOF, or error, returns the error code as a Integer.
     * @exception Exception File exception.
     */
    public Object doMove(int iRelPosition, int iRecordCount) throws DBException, RemoteException
    {
        int iRowIndex = m_iCurrentRecord + iRelPosition;    // Calculate absolute position
        if ((m_iCurrentRecord == -1)
            && (iRelPosition == -1))
                iRowIndex = m_mDataMap.size() - 1;  // Special case - pos -1 after open -> pos last.
        return this.get(iRowIndex, iRecordCount);
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
        m_objCurrentKey = null;
        Object data = (Vector)m_mDataMap.get(objKeyData);
        if (data == null)
            data = Boolean.FALSE;
        else
            m_objCurrentKey = objKeyData;
        m_iCurrentRecord = -1;
        m_iterator = null;
        return data;
    }
    /**
     * Reposition to this record using this bookmark.
     * <p />In the thin implementation, you can only read using the primary key as the bookmark.
     * JiniTables can't access the datasource on the server, so they must use the bookmark.
     */
    public Object doSetHandle(Object bookmark, int iOpenMode, String strFields, int iHandleType) throws DBException, RemoteException
    {
        return this.seek(null, 0, null, strFields, bookmark);
    }
    /**
     * Get the Handle to the last modified or added record.
     * This uses some very inefficient (and incorrect) code... override if possible.
     * NOTE: There is a huge concurrency problem with this logic if another person adds
     * a record after you, you get the their (wrong) record, which is why you need to
     * provide a solid implementation when you override this method.
     * @param iHandleType The handle type.
     * @return The bookmark.
     * Not implemented.
     */
    public Object getLastModified(int iHandleType) throws DBException, RemoteException
    {
        return null;    // Not required, since add always returns the last bookmark (and RemoteFieldTable keeps it for the next call)
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
        if ((m_iterator == null)
            || (m_iCurrentRecord == -1)
            || (iRowIndex <= m_iCurrentRecord))
        {
            m_iterator = m_mDataMap.keySet().iterator();
            m_iCurrentRecord = -1;
        }
        while (m_iterator.hasNext())
        {
            m_iCurrentRecord++;
            m_objCurrentKey = m_iterator.next();
            if (m_iCurrentRecord == iRowIndex)
                return m_mDataMap.get(m_objCurrentKey);
        }
        m_iterator = null;
        m_iCurrentRecord = -1;
        m_objCurrentKey = null;
        return m_objCurrentKey;
    }
    /**
     * Get the key from this data record.
     */
    public Object getKey(Object data)
    {
        return ((Vector)data).get(0);
    }
    /**
     * Enable/Disable autosequence for this table.
     * Not implemented.
     */
    public void setRemoteProperty(String strProperty, String strValue) throws RemoteException
    {
        return;   // Not supported
    }
    /**
     * Get the remote table for this session.
     * Not implemented.
     */
    public RemoteTable getRemoteTable(String strRecordName) throws RemoteException
    {
        return null;    // Not supported
    }
    /**
     * Do a remote action.
     * Not implemented.
     * @param strCommand Command to perform remotely.
     * @param properties Properties for this command (optional).
     * @return boolean success.
     */
    public Object doRemoteAction(String strCommand, Map<String, Object> properties) throws DBException, RemoteException
    {
        return null;    // Not supported
    }
    /**
     * Make a thin FieldList for this table.
     * Not implemented.
     * Usually used for special queries that don't have a field list available.
     */
    public org.jbundle.thin.base.db.FieldList makeFieldList(String strFieldsToInclude) throws RemoteException
    {
        return null;    // Not supported
    }
    /**
     * Build a new remote session and initialize it.
     * @param parentSessionObject The parent session for this new session (if null, parent = me).
     * @param strSessionClassName The class name of the remote session to build.
     */
    public org.jbundle.thin.base.remote.RemoteBaseSession makeRemoteSession(String strSessionClassName) throws RemoteException
    {
        return null;
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
     * Get the remote table reference.
     * If you want the remote table session, call this method with java.rmi.server.RemoteStub.class.
     * @classType The base class I'm looking for (If null, return the next table on the chain) 
     * @return The remote table reference.
     */
    public RemoteTable getRemoteTableType(Class<?> classType) throws RemoteException
    {
        return null;
    }
    /**
     * Get/Make this remote database session for this table session.
     * @param properties The client database properties (Typically for transaction support).
     */
    public RemoteDatabase getRemoteDatabase(Map<String, Object> properties) throws RemoteException
    {
        return null;
    }
}
