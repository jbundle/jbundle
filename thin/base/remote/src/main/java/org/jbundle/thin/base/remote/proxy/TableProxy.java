/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.remote.proxy;

/**
 *  ApplicationServer - The interface to server objects.
 *  Copyright (c) 2005 jbundle.org. All rights reserved.
 */
import java.util.Map;

import org.jbundle.model.DBException;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.remote.RemoteDatabase;
import org.jbundle.thin.base.remote.RemoteTable;
import org.jbundle.thin.base.remote.RemoteException;
import org.jbundle.thin.base.remote.proxy.transport.BaseTransport;


/**
 *  ApplicationServer - The interface to server objects.
 */
public class TableProxy extends SessionProxy
    implements RemoteTable
{

    /**
     * Constructor.
     */
    public TableProxy()
    {
        super();
    }
    /**
     * Constructor.
     */
    public TableProxy(BaseProxy parentProxy, String strID)
    {
        this();
        this.init(parentProxy, strID);
    }
    /**
     * Constructor.
     */
    public void init(BaseProxy parentProxy, String strID)
    {
        super.init(parentProxy, strID);
    }
    /**
     * Create the proxy transport.
     * @param strCommand The command.
     * @return The transport.
     */
    public BaseTransport createProxyTransport(String strCommand)
    {
        BaseTransport transport = super.createProxyTransport(strCommand);
        return transport;
    }
//------------------------------Remote Implementation---------------------------------
    /**
     * Open - Receive to this server and send the response.
     * @exception Exception File exception.
     */
    public void open(String strKeyArea, int iOpenMode, boolean bDirection, String strFields, Object objInitialKey, Object objEndKey, byte[] byBehaviorData) throws DBException, RemoteException
    {
        BaseTransport  transport = this.createProxyTransport(OPEN);
        transport.addParam(KEY, strKeyArea);
        transport.addParam(MODE, iOpenMode);
        transport.addParam(DIRECTION, bDirection);
        transport.addParam(FIELDS, strFields);
        transport.addParam(INITIAL_KEY, objInitialKey);
        transport.addParam(END_KEY, objEndKey);
        transport.addParam(BEHAVIOR_DATA, byBehaviorData);
        Object strReturn = transport.sendMessageAndGetReply();
        Object objReturn = transport.convertReturnObject(strReturn);
        this.checkDBException(objReturn);
    }
    /**
     * Add add this data to the file.
     * @param data A vector object containing the raw data for the record.
     * @return A bookmark if there is no penalty (otherwise returns null).
     * @exception DBException File exception.
     */
    public Object add(Object data, int iOpenMode) throws DBException, RemoteException
    {
        BaseTransport transport = this.createProxyTransport(ADD);
        transport.addParam(DATA, data);
        transport.addParam(MODE, iOpenMode);
        Object strReturn = transport.sendMessageAndGetReply();
        Object objReturn = transport.convertReturnObject(strReturn);
        return this.checkDBException(objReturn);
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
        BaseTransport transport = this.createProxyTransport(EDIT);
        transport.addParam(MODE, iOpenMode);
        Object strReturn = transport.sendMessageAndGetReply();
        Object objReturn = transport.convertReturnObject(strReturn);
        this.checkDBException(objReturn);
        if (objReturn instanceof Integer)
            return ((Integer)objReturn).intValue();
        return Constants.NORMAL_RETURN;
    }
    /**
     * Update the current record.
     * @param The data to update.
     * @exception Exception File exception.
     */
    public void set(Object data, int iOpenMode) throws DBException, RemoteException
    {
        BaseTransport transport = this.createProxyTransport(SET);
        transport.addParam(DATA, data);
        transport.addParam(MODE, iOpenMode);
        Object strReturn = transport.sendMessageAndGetReply();
        Object objReturn = transport.convertReturnObject(strReturn);
        this.checkDBException(objReturn);
    }
    /**
     * Delete the current record.
     * @param - This is a dummy param, because this call conflicts with a call in EJBHome.
     * @exception Exception File exception.
     */
    public void remove(Object data, int iOpenMode) throws DBException, RemoteException
    {
        BaseTransport transport = this.createProxyTransport(REMOVE);
        transport.addParam(DATA, data);
        transport.addParam(MODE, iOpenMode);
        Object strReturn = transport.sendMessageAndGetReply();
        Object objReturn = transport.convertReturnObject(strReturn);
        this.checkDBException(objReturn);
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
        BaseTransport transport = this.createProxyTransport(DO_MOVE);
        transport.addParam(POSITION, iRelPosition);
        transport.addParam(COUNT, iRecordCount);
        Object strReturn = transport.sendMessageAndGetReply();
        Object objReturn = transport.convertReturnObject(strReturn);
        return this.checkDBException(objReturn);
    }
    /**
     * Retrieve this record from the key.
     * @param strSeekSign Which way to seek null/= matches data also &gt;, &lt;, &gt;=, and &lt;=.
     * @param strKeyArea The name of the key area to seek on.
     * @param objKeyData The data for the seek (The raw data if a single field, a BaseBuffer if multiple).
     * @returns The record (as a vector) if successful, The return code (as an Boolean) if not.
     * @exception DBException File exception.
     */
    public Object seek(String strSeekSign, int iOpenMode, String strKeyArea, String strFields, Object objKeyData) throws DBException, RemoteException
    {
        BaseTransport transport = this.createProxyTransport(SEEK);
        if (strSeekSign == null)
            strSeekSign = Constants.EQUALS;
        transport.addParam(SIGN, strSeekSign);
        transport.addParam(MODE, iOpenMode);
        transport.addParam(KEY, strKeyArea);
        transport.addParam(FIELDS, strFields);
        transport.addParam(KEY_DATA, objKeyData);
        Object strReturn = transport.sendMessageAndGetReply();
        Object objReturn = transport.convertReturnObject(strReturn);
        return this.checkDBException(objReturn);
    }
    /**
     * Reposition to this record using this bookmark.
     * <p />JiniTables can't access the datasource on the server, so they must use the bookmark.
     * @param bookmark The handle of the record to retrieve.
     * @param iHandleType The type of handle to use.
     * @return The record or the return code as an Boolean.
     */
    public Object doSetHandle(Object bookmark, int iOpenMode, String strFields, int iHandleType) throws DBException, RemoteException
    {
        BaseTransport transport = this.createProxyTransport(DO_SET_HANDLE);
        transport.addParam(BOOKMARK, bookmark);
        transport.addParam(MODE, iOpenMode);
        transport.addParam(FIELDS, strFields);
        transport.addParam(TYPE, iHandleType);
        transport.sendMessageAndGetReply();
        Object strReturn = transport.sendMessageAndGetReply();
        Object objReturn = transport.convertReturnObject(strReturn);
        return this.checkDBException(objReturn);
    }
    /**
     * Get the DATA_SOURCE_HANDLE to the last modified or added record.
     * @param iHandleType The type of handle to use.
     * @return The handle of the last modified record.
     */
    public Object getLastModified(int iHandleType) throws DBException, RemoteException
    {
        BaseTransport transport = this.createProxyTransport(GET_LAST_MODIFIED);
        transport.addParam(TYPE, iHandleType);
        Object strReturn = transport.sendMessageAndGetReply();
        Object objReturn = transport.convertReturnObject(strReturn);
        return this.checkDBException(objReturn);
    }
    /**
     * Receive this relative record in the table.
     * <p>Note: This is usually used only by thin clients, as thick clients have the code to
     * fake absolute access.
     * @param iRowIndex The row to retrieve.
     * @param iRowCount The number of rows to retrieve (Used only by CachedRemoteTable).
     * @return The record(s) or an error code as an Integer.
     * @exception Exception File exception.
     */
    public Object get(int iRowIndex, int iRowCount) throws DBException, RemoteException
    {
        BaseTransport transport = this.createProxyTransport(GET);
        transport.addParam(INDEX, iRowIndex);
        transport.addParam(COUNT, iRowCount);
        Object strReturn = transport.sendMessageAndGetReply();
        Object objReturn = transport.convertReturnObject(strReturn);
        return this.checkDBException(objReturn);
    }
    /**
     * Set a table property.
     * @param strProperty The key to set.
     * @param strValue The value to set it to.
     */
    public void setRemoteProperty(String strProperty, String strValue) throws RemoteException
    {
        BaseTransport transport = this.createProxyTransport(SET_REMOTE_PROPERTY);
        transport.addParam(KEY, strProperty);
        transport.addParam(VALUE, strValue);
        Object strReturn = transport.sendMessageAndGetReply();
        /*Object objReturn = */transport.convertReturnObject(strReturn);
//x        this.checkException(objReturn);
    }
    /**
     * Make a thin FieldList for this table.
     * Usually used for special queries that don't have a field list available.
     * @return The new serialized fieldlist.
     */
    public org.jbundle.thin.base.db.FieldList makeFieldList(String strFieldsToInclude) throws RemoteException
    {
        BaseTransport transport = this.createProxyTransport(MAKE_FIELD_LIST);
        transport.addParam(FIELDS, strFieldsToInclude);
        Object strReturn = transport.sendMessageAndGetReply();
        Object objReturn = transport.convertReturnObject(strReturn);
        return (org.jbundle.thin.base.db.FieldList)objReturn;
    }
    /**
     * Get the remote table reference.
     * If you want the remote table session, call this method with Remote.class.
     * @classType The base class I'm looking for (If null, return the next table on the chain) 
     * @return The remote table reference.
     */
    public RemoteTable getRemoteTableType(Class<?> classType) throws RemoteException
    {
        if (org.jbundle.model.Remote.class.getName().equals(classType.getName()))
            return this;    // For this purpose I am the remote object.
        return null;    // This method is never called remotely
    }
    /**
     * Get/Make this remote database session for this table session.
     * @param properties The client database properties (Typically for transaction support).
     */
    public RemoteDatabase getRemoteDatabase(Map<String, Object> properties) throws RemoteException
    {
        BaseTransport transport = this.createProxyTransport(GET_REMOTE_DATABASE);
        transport.addParam(PROPERTIES, properties);
        String strRemoteDatabaseID = (String)transport.sendMessageAndGetReply();
        // See if I have this one already
        DatabaseProxy dbProxy = ((TaskProxy)this.getParentProxy()).getDatabaseProxy(strRemoteDatabaseID);
        if (dbProxy == null)
            dbProxy = new DatabaseProxy((TaskProxy)this.getParentProxy(), strRemoteDatabaseID); // This will add it to my list
        return dbProxy;
    }
}
