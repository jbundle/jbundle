/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.remote;

/**
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.util.Map;

import org.jbundle.model.DBException;


/**
 * RemoteTable - The interface for SocketTable <--> RemoteTable socket communication.
 */
public interface RemoteTable extends RemoteSession
{
    /**
     * Open - Receive to this server and send the response.
     * @throws RemoteException TODO
     * @exception Exception File exception.
     */
    public void open(String strKeyArea, int iOpenMode, boolean bDirection, String strFields, Object objInitialKey, Object objEndKey, byte[] byBehaviorData) throws DBException, RemoteException;
    /**
     * Add add this data to the file.
     * @param data A vector object containing the raw data for the record.
     * @param iOpenMode The current open mode.
     * @return A bookmark if there is no penalty (otherwise returns null).
     * @exception DBException File exception.
     * @throws RemoteException TODO
     */
    public Object add(Object data, int iOpenMode) throws DBException, RemoteException;
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
     * @throws RemoteException TODO
     */
    public int edit(int iOpenMode) throws DBException, RemoteException;
    /**
     * Update the current record.
     * @param iOpenMode The current open mode.
     * @param The data to update.
     * @throws RemoteException TODO
     * @exception Exception File exception.
     */
    public void set(Object data, int iOpenMode) throws DBException, RemoteException;
    /**
     * Delete the current record.
     * @param iOpenMode The current open mode.
     * @param - This is a dummy param, because this call conflicts with a call in EJBHome.
     * @throws RemoteException TODO
     * @exception Exception File exception.
     */
    public void remove(Object data, int iOpenMode) throws DBException, RemoteException;
    /**
     * Move the current position and read the record (optionally read several records).
     * @param iRelPosition relative Position to read the next record.
     * @param iRecordCount Records to read.
     * @return If I read 1 record, this is the record's data.
     * @return If I read several records, this is a vector of the returned records.
     * @return If at EOF, or error, returns the error code as a Integer.
     * @throws RemoteException TODO
     * @exception Exception File exception.
     */
    public Object doMove(int iRelPosition, int iRecordCount) throws DBException, RemoteException;
    /**
     * Retrieve this record from the key.
     * @param strSeekSign Which way to seek null/= matches data also &gt;, &lt;, &gt;=, and &lt;=.
     * @param strKeyArea The name of the key area to seek on.
     * @param objKeyData The data for the seek (The raw data if a single field, a BaseBuffer if multiple).
     * @returns The record (as a vector) if successful, The return code (as an Boolean) if not.
     * @exception DBException File exception.
     * @throws RemoteException TODO
     */
    public Object seek(String strSeekSign, int iOpenMode, String strKeyArea, String strFields, Object objKeyData) throws DBException, RemoteException;
    /**
     * Reposition to this record using this bookmark.
     * <p />JiniTables can't access the datasource on the server, so they must use the bookmark.
     * @param bookmark The handle of the record to retrieve.
     * @param iOpenMode The open mode
     * @param iHandleType The type of handle to use.
     * @return The record or the return code as an Boolean.
     * @throws RemoteException TODO
     */
    public Object doSetHandle(Object bookmark, int iOpenMode, String strFields, int iHandleType) throws DBException, RemoteException;
    /**
     * Get the DATA_SOURCE_HANDLE to the last modified or added record.
     * @param iHandleType The type of handle to use.
     * @return The handle of the last modified record.
     * @throws RemoteException TODO
     */
    public Object getLastModified(int iHandleType) throws DBException, RemoteException;
    /**
     * Receive this relative record in the table.
     * <p>Note: This is usually used only by thin clients, as thick clients have the code to
     * fake absolute access.
     * @param iRowIndex The row to retrieve.
     * @param iRowCount The number of rows to retrieve (Used only by CachedRemoteTable).
     * @return The record(s) or an error code as an Integer.
     * @throws RemoteException TODO
     * @exception Exception File exception.
     */
    public Object get(int iRowIndex, int iRowCount) throws DBException, RemoteException;
    /**
     * Set a table property.
     * @param strProperty The key to set.
     * @param strValue The value to set it to.
     * @throws RemoteException TODO
     */
    public void setRemoteProperty(String strProperty, String strValue) throws RemoteException;
    /**
     * Make a thin FieldList for this table.
     * Usually used for special queries that don't have a field list available.
     * @return The new serialized fieldlist.
     * @throws RemoteException TODO
     */
    public org.jbundle.thin.base.db.FieldList makeFieldList(String strFieldsToInclude) throws RemoteException;
    /**
     * Get the remote table reference.
     * If you want the remote table session, call this method with java.rmi.server.RemoteStub.class.
     * @classType The base class I'm looking for (If null, return the next table on the chain) 
     * @return The remote table reference.
     * @throws RemoteException TODO
     */
    public RemoteTable getRemoteTableType(Class<?> classType) throws RemoteException;
    /**
     * Get/Make this remote database session for this table session.
     * @param properties The client database properties (Typically for transaction support).
     * @throws RemoteException TODO
     */
    public RemoteDatabase getRemoteDatabase(Map<String, Object> properties) throws RemoteException;
}
