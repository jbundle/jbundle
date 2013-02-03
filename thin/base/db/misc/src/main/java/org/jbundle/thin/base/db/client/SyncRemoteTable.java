/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.db.client;

import java.util.Map;

import org.jbundle.model.DBException;
import org.jbundle.model.RemoteException;
import org.jbundle.thin.base.remote.RemoteDatabase;
import org.jbundle.thin.base.remote.RemoteTable;
import org.jbundle.thin.base.util.ThinUtil;


/**
 * This class simply passes the call on after it synchronizes.
 */
public class SyncRemoteTable extends Object
    implements RemoteTable
{
    /**
     * Table to pass calls on to.
     */
    protected RemoteTable m_tableRemote = null;
    /**
     * Object to synchronize on.
     */
    protected Object m_objSync = null;
    /**
     * Constructor.
     */
    public SyncRemoteTable()
    {
        super();
    }
    /**
     * Constructor.
     * @param The remotetable (actually, the ThinTable).
     */
    public SyncRemoteTable(RemoteTable tableRemote, Object objSync)
    {
        this();
        this.init(tableRemote, objSync);
    }
    /**
     * Constructor.
     * @param The remotetable (actually, the ThinTable).
     */
    public void init(RemoteTable tableRemote, Object objSync)
    {
        m_tableRemote = tableRemote;
        m_objSync = objSync;
    }
    /**
     * Free is never called for a CachedTable.
     */
    public void free() throws RemoteException
    {
        synchronized(m_objSync)
        {
            m_tableRemote.freeRemoteSession();
        }
        m_objSync = null;
        m_tableRemote = null;
    }
    /**
     *
     */
    public Object getSyncObject()
    {
        return m_objSync;
    }
    /**
     * Receive to this server and send the response.
     */
    public void freeRemoteSession() throws RemoteException
    {
        synchronized(m_objSync)
        {
            m_tableRemote.freeRemoteSession();
        }
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
        synchronized(m_objSync)
        {
            m_tableRemote.open(strKeyArea, iOpenMode, bDirection, strFields, objInitialKey, objEndKey, byBehaviorData);
        }
    }
    /**
     * Add add this data to the file.
     * @param data A vector object containing the raw data for the record.
     * @exception Exception File exception.
     */
    public Object add(Object data, int iOpenMode) throws DBException, RemoteException
    {
        synchronized(m_objSync)
        {
            return m_tableRemote.add(data, iOpenMode);
        }
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
        synchronized(m_objSync)
        {
            return m_tableRemote.edit(iOpenMode);
        }
    }
    /**
     * Update the current record.
     * @param The data to update.
     * @exception DBException File exception.
     */
    public void set(Object data, int iOpenMode) throws DBException, RemoteException
    {
        synchronized(m_objSync)
        {
            m_tableRemote.set(data, iOpenMode);
        }
    }
    /**
     * Delete the current record.
     * @param - This is a dummy param, because this call conflicts with a call in EJBHome.
     * @exception DBException File exception.
     */
    public void remove(Object data, int iOpenMode) throws DBException, RemoteException
    {
        synchronized(m_objSync)
        {
            m_tableRemote.remove(data, iOpenMode);
        }
    }
    /**
     * Move the current position and read the record (optionally read several records).
     * @param iRelPosition relative Position to read the next record.
     * @param iRecordCount Records to read.
     * @return If I read 1 record, this is the record's data.
     * @return If I read several records, this is a vector of the returned records.
     * @return If at EOF, or error, returns the error code as a Integer.
     * @exception DBException File exception.
     */
    public Object doMove(int iRelPosition, int iRecordCount) throws DBException, RemoteException
    {
        synchronized(m_objSync)
        {
            return m_tableRemote.doMove(iRelPosition, iRecordCount);
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
        synchronized(m_objSync)
        {
            return m_tableRemote.seek(strSeekSign, iOpenMode, strKeyArea, strFields, objKeyData);
        }
    }
    /**
     * Reposition to this record using this bookmark.<p>
     * JiniTables can't access the datasource on the server, so they must use the bookmark.
     */
    public Object doSetHandle(Object bookmark, int iOpenMode, String strFields, int iHandleType) throws DBException, RemoteException
    {
        synchronized(m_objSync)
        {
            return m_tableRemote.doSetHandle(bookmark, iOpenMode, strFields, iHandleType);
        }
    }
    /**
     * Get the DATA_SOURCE_HANDLE to the last modified or added record.
     */
    public Object getLastModified(int iHandleType) throws DBException, RemoteException
    {
        synchronized(m_objSync)
        {
            return m_tableRemote.getLastModified(iHandleType);
        }
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
        synchronized(m_objSync)
        {
            return m_tableRemote.get(iRowIndex, iRowCount);
        }
    }
    /**
     * Enable/Disable autosequence for this table.
     */
    public void setRemoteProperty(String strProperty, String strValue) throws RemoteException
    {
        synchronized(m_objSync)
        {
            m_tableRemote.setRemoteProperty(strProperty, strValue);
        }
    }
    /**
     * Get the remote table for this session.
     * @param strRecordName Table Name or Class Name of the record to find
     * Note: This method is used when the SessionObject is used as an application's remote peer.
     */
    public RemoteTable getRemoteTable(String strRecordName) throws RemoteException
    {
        synchronized(m_objSync)
        {
            return m_tableRemote.getRemoteTable(strRecordName);
        }
    }
    /**
     * Do a remote action.
     * @param strCommand Command to perform remotely.
     * @param properties Properties for this command (optional).
     * @return boolean success.
     */
    public Object doRemoteAction(String strCommand, Map<String, Object> properties) throws DBException, RemoteException
    {
        synchronized(m_objSync)
        {
            return m_tableRemote.doRemoteAction(strCommand, properties);
        }
    }
    /**
     * make a thin FieldList for this table.
     * Usually used for special queries that don't have a field list available.
     */
    public org.jbundle.thin.base.db.FieldList makeFieldList(String strFieldsToInclude) throws RemoteException
    {
        synchronized(m_objSync)
        {
            return m_tableRemote.makeFieldList(strFieldsToInclude);
        }
    }  
    /**
     * Build a new remote session and initialize it.
     * @param parentSessionObject The parent session for this new session (if null, parent = me).
     * @param strSessionClassName The class name of the remote session to build.
     */
    public org.jbundle.thin.base.remote.RemoteBaseSession makeRemoteSession(String strSessionClassName) throws RemoteException
    {
        synchronized(m_objSync)
        {
            return m_tableRemote.makeRemoteSession(strSessionClassName);
        }
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
     * If you want the remote table session, call this method with Remote.class.
     * @classType The base class I'm looking for (If null, return the next table on the chain) 
     * @return The remote table reference.
     */
    public RemoteTable getRemoteTableType(Class<?> classType) throws RemoteException
    {
        return ThinUtil.getRemoteTableType(m_tableRemote, classType);
    }
    /**
     * Get/Make this remote database session for this table session.
     * @param properties The client database properties (Typically for transaction support).
     */
    public RemoteDatabase getRemoteDatabase(Map<String, Object> properties) throws RemoteException
    {
        synchronized(m_objSync)
        {
            return m_tableRemote.getRemoteDatabase(properties);
        }        
    }
    /**
     * Get the remote table that I am passing commands to.
     */
    public RemoteTable getRemoteTable()
    {
        return m_tableRemote;
    }
    /**
     * Set the remote table.
     * @param remoteTable
     */
    public void setRemoteTable(RemoteTable remoteTable)
    {
        m_tableRemote = remoteTable;
    }
}
