package org.jbundle.base.remote.db;

/**
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.Vector;

import org.jbundle.base.db.BaseDatabase;
import org.jbundle.base.db.BaseTable;
import org.jbundle.base.db.DatabaseException;
import org.jbundle.base.db.GridTable;
import org.jbundle.base.db.KeyArea;
import org.jbundle.base.db.QueryRecord;
import org.jbundle.base.db.Record;
import org.jbundle.base.db.RecordOwner;
import org.jbundle.base.db.event.FileListener;
import org.jbundle.base.db.filter.SubCurrentFilter;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.message.record.RecordMessageHeader;
import org.jbundle.base.remote.BaseSession;
import org.jbundle.base.util.DBConstants;
import org.jbundle.base.util.DBParams;
import org.jbundle.base.util.DatabaseOwner;
import org.jbundle.base.util.Debug;
import org.jbundle.base.util.MenuConstants;
import org.jbundle.base.util.Utility;
import org.jbundle.model.DBException;
import org.jbundle.model.Task;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.FieldInfo;
import org.jbundle.thin.base.db.FieldList;
import org.jbundle.thin.base.db.KeyAreaInfo;
import org.jbundle.thin.base.db.buff.BaseBuffer;
import org.jbundle.thin.base.db.buff.VectorBuffer;
import org.jbundle.thin.base.message.BaseMessage;
import org.jbundle.thin.base.message.event.ModelMessageHandler;
import org.jbundle.thin.base.remote.RemoteDatabase;
import org.jbundle.thin.base.remote.RemoteTable;
import org.jbundle.thin.base.util.Application;


/**
 * The server side of the SocketTable <--> RemoteTable socket.
 * Note: A TableSessionObject can have either a SessionObject (a remote session) or a
 * DatabaseSessionObject as its parent.
 * <p>WARNING: Do not override this class to create a session. TableSessionObject is only for
 * automatically created client sessions.
 * <p />Note: close() and addnew() are not implemented here. They are called automatically
 * on open() and add() respectively. Also, edit() is not required of a set(). It is automatic.
 */
public class TableSession extends Session
    implements RemoteTable
{
    private static final long serialVersionUID = 1L;

    public static final String SELECTED = "SELECTED";
    public static final String CACHE_GRID_TABLE_PARAM = "cacheGridTable";

    /**
     * Constructor.
     */
    public TableSession() throws RemoteException
    {
        super();
    }
    /**
     * Constructor.
     * @param parentSessionObject The parent session for this table session (a databasesessionobject).
     * @param record The record this session calls.
     * @param objectID If this table session refers to a particular record object, this is the handle.
     */
    public TableSession(BaseSession parentSessionObject, Record record, Object objectID) throws RemoteException
    {
        this();
        this.init(parentSessionObject, record, objectID);
    }
    /**
     * Constructor.
     * @param parentSessionObject The parent session for this table session (a databasesessionobject).
     * @param record The record this session calls.
     * @param objectID If this table session refers to a particular record object, this is the handle.
     */
    public void init(BaseSession parentSessionObject, Record record, Object objectID)
    {
        super.init(parentSessionObject, record, objectID);
    }
    /**
     * Free the session.
     */
    public void free()
    {
        super.free();
    }
    /**
     * Free the table (this is the call that is called from the client).
     * Frees the main record and calls free().
     */
    public void freeRemoteSession() throws RemoteException
    {
        try   {
            Utility.getLogger().info("EJB Free" + ((this.getMainRecord() != null) ? this.getMainRecord().getTable() : ""));
            if (this.getMainRecord() != null)
                this.getMainRecord().free();
            this.free();
        } catch (Exception ex)  {
            Debug.print(ex, TableSession.class.getName());
        }
    }
    /**
     * Open - Open this table for a query.
     * @param strKeyArea The default key area (null to leave unchanged).
     * @param strFields The fields to select (comma separated).
     * @param objInitialKey The start key (as a raw data object or a BaseBuffer).
     * @param objEndKey The end key (as a raw data object or a BaseBuffer).
     * @param byBehaviorData A steam describing the behaviors to add and the initialization data.
     * @exception Exception File exception.
     * @exception RemoteException RMI exception.
     */
    public void open(String strKeyArea, int iOpenMode, boolean bDirection, String strFields, Object objInitialKey, Object objEndKey, byte[] byBehaviorData) throws DBException, RemoteException
    {
        try   {
            synchronized (this.getTask())
            {   // In case two tasks are calling here
                this.getMainRecord().close();

        // FROM is automatic, since the remote BaseRecord is exactly the same as this one
        // ORDER BY
                KeyArea keyArea =  this.getMainRecord().getKeyArea();
                if (strKeyArea != null)
                {
                    this.getMainRecord().setKeyArea(strKeyArea);
                    keyArea = this.getMainRecord().getKeyArea(-1);
                    // This next set of code deals with a special case where the caller wants a non-key area order
                    if (this.getMainRecord().getDefaultOrder() == Constants.MAIN_KEY_AREA)
                        if (!keyArea.getKeyName().equals(strKeyArea))
                            if (!Constants.PRIMARY_KEY.equals(strKeyArea))
                    {
                        BaseField field = this.getMainRecord().getField(strKeyArea);
                        if (field != null)
                        {
                            KeyArea tempKeyStart = this.getMainRecord().makeIndex(DBConstants.NOT_UNIQUE, null);  // Add temp key
                            tempKeyStart.addKeyField(field, bDirection);
                            this.getMainRecord().setKeyArea(this.getMainRecord().getKeyAreaCount() - 1);
                        }
                    }
                    keyArea.setKeyOrder(bDirection);
                }
        // Open mode
                this.getMainRecord().setOpenMode(iOpenMode);
        // SELECT (fields to select)
                if (strFields != null)
                    this.getMainRecord().setSelected(strFields);    // Select these fields
                else
                {
                    Record recordBase = this.getMainRecord().getTable().getCurrentTable().getRecord();
                    int iFieldTypes = this.getFieldTypes(recordBase);
                    if (iFieldTypes == BaseBuffer.PHYSICAL_FIELDS)
                        this.getMainRecord().setSelected(true);   // Select these fields (otherwise leave the selection alone)
                }
        // WHERE XYZ >=
                FileListener listener = null;
                listener = (FileListener)this.getMainRecord().getListener();
                while (listener != null)
                {   // Clear inited flag from all Linked behaviors
                    if ((listener.getMasterSlaveFlag() & FileListener.LINKED_TO_SLAVE) != 0)
                        listener.setMasterSlaveFlag(listener.getMasterSlaveFlag() & ~FileListener.INITED_IN_SLAVE);    // Clear inited flag
                    listener = (FileListener)listener.getNextListener();
                }
                if (objInitialKey != null)
                {
                    VectorBuffer recBuff = new VectorBuffer((Vector)objInitialKey);
                    int iLastModified = -1;
                    keyArea.reverseKeyBuffer(recBuff, DBConstants.FILE_KEY_AREA);
                    String strLastModified = recBuff.getNextString();
                    try {
                        if (strLastModified != null)
                            iLastModified = Integer.parseInt(strLastModified);
                    } catch (NumberFormatException ex)  {
                        iLastModified = -1;
                    }
                    this.getMainRecord().addListener(listener = new SubCurrentFilter(iLastModified, true, false));   // Use current
                    listener.setMasterSlaveFlag(listener.getMasterSlaveFlag() | FileListener.INITED_IN_SLAVE | FileListener.LINKED_TO_SLAVE | FileListener.RUN_IN_SLAVE);
                }
        // WHERE XYZ >=
                if (objEndKey != null)
                {
                    VectorBuffer recBuff = new VectorBuffer((Vector)objEndKey);
                    keyArea.reverseKeyBuffer(recBuff, DBConstants.FILE_KEY_AREA);
                    int iLastModified = -1;
                    String strLastModified = recBuff.getNextString();
                    try {
                        if (strLastModified != null)
                            iLastModified = Integer.parseInt(strLastModified);
                    } catch (NumberFormatException ex)  {
                        iLastModified = -1;
                    }
                    this.getMainRecord().addListener(listener = new SubCurrentFilter(iLastModified, false, true));   // Use current
                    listener.setMasterSlaveFlag(listener.getMasterSlaveFlag() | FileListener.INITED_IN_SLAVE | FileListener.LINKED_TO_SLAVE | FileListener.RUN_IN_SLAVE);
                }
        // WHERE XYZ
        // The following code replicates the Behaviors for the server class.
        // If the listener doesn't exist, it is created and the current params are set.
        // If the listener does exist, the current params are set.
        // Note: There is a special section of code to see that if two behaviors with the same
        // name exist, they are set separately.
                if (byBehaviorData != null)
                {
                    ByteArrayInputStream baIn = new ByteArrayInputStream(byBehaviorData);
                    ObjectInputStream daIn = new ObjectInputStream(baIn);
                    String strBehaviorName = null;
                    try   {
                            strBehaviorName = daIn.readUTF();
                        } catch (IOException ex)    {
                            strBehaviorName = null;
                        }
                    while (strBehaviorName != null)
                    {
                        listener = (FileListener)this.getMainRecord().getListener(strBehaviorName);
                        while (listener != null)
                        {   // Already set up this listener, find the next one or null
                            if ((listener.getMasterSlaveFlag() & FileListener.LINKED_TO_SLAVE) != 0)
                                if ((listener.getMasterSlaveFlag() & FileListener.INITED_IN_SLAVE) == 0)
                                    break;      // Use this listener (Linked, but not inited)
                            listener = (FileListener)listener.getListener(strBehaviorName);
                        }
                        if (listener == null)
                        {
                        	listener = (FileListener)Utility.makeObjectFromClassName(strBehaviorName);
                        }
                        else
                        {
                            this.getMainRecord().removeListener(listener, false);
                        }
                        listener.initRemoteSkel(daIn);
                        this.getMainRecord().addListener(listener);
                        listener.setMasterSlaveFlag(listener.getMasterSlaveFlag() | FileListener.INITED_IN_SLAVE | FileListener.LINKED_TO_SLAVE);
                        try   {
                            strBehaviorName = daIn.readUTF();
                        } catch (IOException ex)    {
                            strBehaviorName = null;
                        }
                    }
                    daIn.close();
                    baIn.close();
                }
                listener = (FileListener)this.getMainRecord().getListener();
                while (listener != null)
                {   // Remove old created behaviors (linked, but not inited)
                    FileListener behaviorToRemove = listener;
                    listener = (FileListener)listener.getNextListener();
                    if ((behaviorToRemove.getMasterSlaveFlag() & FileListener.LINKED_TO_SLAVE) != 0)
                        if ((behaviorToRemove.getMasterSlaveFlag() & FileListener.INITED_IN_SLAVE) == 0)
                            this.getMainRecord().removeListener(behaviorToRemove, true);
                }
        // End
                Utility.getLogger().info("EJB Open key: " + strKeyArea);
                this.getMainRecord().open();
            }
        } catch (DBException ex)    {
            throw ex;
        } catch (Exception ex)  {
            ex.printStackTrace();
            throw new DBException(ex.getMessage());
        }
    }
    /**
     * Add add this data to the file.
     * @param data A vector object containing the raw data for the record.
     * @exception DBException File exception.
     * @exception RemoteException RMI exception.
     */
    public Object add(Object data, int iOpenMode) throws DBException, RemoteException
    {
        Object bookmarkLast = null;
        Record record = this.getMainRecord();
        int iOldOpenMode = record.getOpenMode();
        try   {
            Utility.getLogger().info("EJB Add");
            synchronized (this.getTask())
            {
                record.setOpenMode((iOldOpenMode & ~DBConstants.LOCK_TYPE_MASK) | (iOpenMode & DBConstants.LOCK_TYPE_MASK));  // Use client's lock data
                record.addNew();
                Record recordBase = record.getTable().getCurrentTable().getRecord();
                int iFieldTypes = this.getFieldTypes(recordBase);

                int iErrorCode = this.moveBufferToFields(data, iFieldTypes, recordBase);  // Screen move... need to validate!
                if (iErrorCode != DBConstants.NORMAL_RETURN)
                {
                    throw new DatabaseException(iErrorCode);
                }
                if (DBConstants.TRUE.equals(record.getTable().getProperty(DBParams.SUPRESSREMOTEDBMESSAGES)))
                    record.setSupressRemoteMessages(true);
                record.add();
                bookmarkLast = record.getLastModified(DBConstants.BOOKMARK_HANDLE);
            }
        } catch (DBException ex)    {
            throw ex;
        } catch (Exception ex)  {
            ex.printStackTrace();
            throw new DBException(ex.getMessage());
        } finally   {
            record.setSupressRemoteMessages(false);
            this.getMainRecord().setOpenMode(iOldOpenMode);
        }
        return bookmarkLast;
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
     * @exception RemoteException RMI exception.
     */
    public int edit(int iOpenMode) throws DBException, RemoteException
    {
        Record record = this.getMainRecord();
        int iOldOpenMode = record.getOpenMode();
        try   {
            Utility.getLogger().info("EJB Edit");
            synchronized (this.getTask())
            {
                record.setOpenMode((iOldOpenMode & ~DBConstants.LOCK_TYPE_MASK) | (iOpenMode & DBConstants.LOCK_TYPE_MASK));  // Use client's lock data
                return record.edit();
            }
        } catch (DBException ex)    {
            throw ex;
        } catch (Exception ex)  {
            ex.printStackTrace();
            throw new DBException(ex.getMessage());
        } finally {
            record.setOpenMode(iOldOpenMode);
        }
    }
    /**
     * Update the current record.
     * This method has some wierd code to emulate the way behaviors are called on a write.
     * @param The data to update.
     * @exception DBException File exception.
     * @exception RemoteException RMI exception.
     */
    public void set(Object data, int iOpenMode) throws DBException, RemoteException
    {
        Record record = this.getMainRecord();
        int iOldOpenMode = record.getOpenMode();
        try   {
            Utility.getLogger().info("EJB Set");
            synchronized (this.getTask())
            {
                record.setOpenMode((iOldOpenMode & ~DBConstants.LOCK_TYPE_MASK) | (iOpenMode & DBConstants.LOCK_TYPE_MASK));  // Use client's lock data
                if (record.getEditMode() == Constants.EDIT_CURRENT)
                    record.edit();
                Record recordBase = record.getTable().getCurrentTable().getRecord();
                int iFieldTypes = this.getFieldTypes(recordBase);
                int iErrorCode = this.moveBufferToFields(data, iFieldTypes, recordBase);
                if (iErrorCode != DBConstants.NORMAL_RETURN)
                    ;   //?
                if (DBConstants.TRUE.equals(record.getTable().getProperty(DBParams.SUPRESSREMOTEDBMESSAGES)))
                    record.setSupressRemoteMessages(true);
                record.getTable().set(recordBase);
            }
        } catch (DBException ex)    {
            throw ex;
        } catch (Exception ex)  {
            ex.printStackTrace();
            throw new DBException(ex.getMessage());
        } finally   {
            record.setSupressRemoteMessages(false);
            this.getMainRecord().setOpenMode(iOldOpenMode);
        }
    }
    /**
     * Delete the current record.
     * @param data This is a dummy param, because this call conflicts with a call in EJBHome.
     * @exception DBException File exception.
     * @exception RemoteException RMI exception.
     */
    public void remove(Object data, int iOpenMode) throws DBException, RemoteException
    {
        Record record = this.getMainRecord();
        int iOldOpenMode = record.getOpenMode();
        try   {
            Utility.getLogger().info("EJB Remove");
            synchronized (this.getTask())
            {
                record.setOpenMode((iOldOpenMode & ~DBConstants.LOCK_TYPE_MASK) | (iOpenMode & DBConstants.LOCK_TYPE_MASK));  // Use client's lock data
                if (record.getEditMode() == Constants.EDIT_CURRENT)
                    record.edit();
                if (DBConstants.TRUE.equals(record.getTable().getProperty(DBParams.SUPRESSREMOTEDBMESSAGES)))
                    record.setSupressRemoteMessages(true);
                record.remove();
            }
        } catch (DBException ex)    {
            throw ex;
        } catch (Exception ex)  {
            ex.printStackTrace();
            throw new DBException(ex.getMessage());
        } finally   {
            record.setSupressRemoteMessages(false);
            this.getMainRecord().setOpenMode(iOldOpenMode);
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
     * @exception RemoteException RMI exception.
     */
    public Object doMove(int iRelPosition, int iRecordCount) throws DBException, RemoteException
    {
        try   {
            Utility.getLogger().info("EJB Move");
            if (iRecordCount == 1)
            {
                return this.doMoveOne(iRelPosition);
            }
            else
            {
                Vector<Object> objVector = new Vector<Object>();
                for (; iRecordCount > 0;iRecordCount--)
                {
                    Object objData = this.doMoveOne(iRelPosition);
                    objVector.add(objData);
                    if (!(objData instanceof Vector))
                        break;
                    iRelPosition = +1;
                }
                return objVector; // Vector of multiple rows
            }
        } catch (DBException ex)    {
            throw ex;
        } catch (Exception ex)  {
            ex.printStackTrace();
            throw new DBException(ex.getMessage());
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
     * @exception RemoteException RMI exception.
     */
    public Object doMoveOne(int iRelPosition) throws DBException, RemoteException
    {
        try   {
            synchronized (this.getTask())
            {
                FieldList record = this.getMainRecord().move(iRelPosition);
                int iRecordStatus = DBConstants.RECORD_NORMAL;
                if (record == null)
                {
                    if (iRelPosition >= 0)
                        iRecordStatus = DBConstants.RECORD_AT_EOF;
                    else
                        iRecordStatus = DBConstants.RECORD_AT_BOF;
                }
                if (iRecordStatus == DBConstants.NORMAL_RETURN)
                {
                    Record recordBase = this.getMainRecord();
                    int iFieldTypes = this.getFieldTypes(recordBase);
                    BaseBuffer buffer = new VectorBuffer(null, iFieldTypes);
                    if (!(recordBase instanceof QueryRecord))
                    {
                        Record recordTarget = recordBase.getTable().getCurrentTable().getRecord();
                        if (recordTarget != recordBase)
                            if (!recordTarget.getTableNames(false).equalsIgnoreCase(recordBase.getTableNames(false)))
                            {
                                buffer.addHeader(DBParams.RECORD);  // Since header count is not passed this specifies multitable
                                buffer.addHeader(recordTarget.getTableNames(false));
                            }
                        recordBase = recordTarget;
                    }
                    buffer.fieldsToBuffer(recordBase, iFieldTypes);
                    return buffer.getPhysicalData();
                }
                else
                {
                    return new Integer(iRecordStatus);
                }
            }
        } catch (DBException ex)    {
            throw ex;
        } catch (Exception ex)  {
            ex.printStackTrace();
            throw new DBException(ex.getMessage());
        }
    }
    /**
     * Retrieve this record from the key.
     * @param strSeekSign Which way to seek null/= matches data also &gt;, &lt;, &gt;=, and &lt;=.
     * @param iOpenMode The temporary open mode to use for this seek.
     * @param strKeyArea The name of the key area to seek on.
     * @param objKeyData The data for the seek (The raw data if a single field, a BaseBuffer if multiple).
     * @returns The record (as a vector) if successful, The return code (as an Boolean) if not.
     * @exception DBException File exception.
     * @exception RemoteException RMI exception.
     */
    public Object seek(String strSeekSign, int iOpenMode, String strKeyArea, String strFields, Object objKeyData) throws DBException, RemoteException
    {
        int iOldOpenMode = this.getMainRecord().getOpenMode();
        try   {
            Utility.getLogger().info("EJB Seek sign: " + strSeekSign + " key: " + strKeyArea + " data: " + objKeyData);
    // SELECT (fields to select)
            synchronized (this.getTask())
            {
                Record recordBase = this.getMainRecord().getTable().getCurrentTable().getRecord();
                this.getMainRecord().setOpenMode(iOpenMode);
                int iFieldTypes = this.getFieldTypes(recordBase);
                if (strFields != null)
                    this.getMainRecord().setSelected(strFields);    // Select these fields
                else
                {
                    if (iFieldTypes == BaseBuffer.PHYSICAL_FIELDS)
                        this.getMainRecord().setSelected(true);   // Select these fields (otherwise leave the selection alone)
                }
                iFieldTypes = this.getFieldTypes(recordBase);

                boolean bResponse = false;
                if ((iOpenMode & DBConstants.OPEN_REFRESH_TO_CURRENT) != 0)
                {
                    int iErrorCode = this.getMainRecord().refreshToCurrent(DBConstants.AFTER_UPDATE_TYPE, false);
                    if (iErrorCode == DBConstants.NORMAL_RETURN)
                        bResponse = true;
                }
                else if (DBConstants.SEEK_CURRENT_RECORD.equals(strSeekSign))
                { // Just return the current record
                    bResponse = true;
                }
                else
                { // Attempt to read this record
                    this.getMainRecord().setKeyArea(strKeyArea);
                    KeyArea keyArea = this.getMainRecord().getKeyArea(-1);  // Current index

                    if (!(objKeyData instanceof Vector))
                    {   // If they just passed the key value, make a vector and pass it!
                        Vector<Object> vector = new Vector<Object>();
                        vector.add(objKeyData);
                        objKeyData = vector;
                    }
                    BaseBuffer buffer = new VectorBuffer((Vector)objKeyData);
                    keyArea.reverseKeyBuffer(buffer, DBConstants.FILE_KEY_AREA);
                    bResponse = this.getMainRecord().seek(strSeekSign);
                }
                if (bResponse)
                {
                    recordBase = this.getMainRecord().getTable().getCurrentTable().getRecord();
                    BaseBuffer buffer2 = new VectorBuffer(null, iFieldTypes);
                    buffer2.fieldsToBuffer(recordBase, iFieldTypes);
                    return buffer2.getPhysicalData();
                }
                else
                    return new Boolean(false);
            }
        } catch (DBException ex)    {
            throw ex;
        } catch (Exception ex)  {
            ex.printStackTrace();
            throw new DBException(ex.getMessage());
        } finally {
            this.getMainRecord().setOpenMode((iOldOpenMode & ~DBConstants.LOCK_TYPE_MASK) | (iOpenMode & DBConstants.LOCK_TYPE_MASK));	// Keep the client lock settings
        }
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
        int iOldOpenMode = this.getMainRecord().getOpenMode();
        try   {
            Utility.getLogger().info("EJB doSetHandle, key= " + bookmark);
            Record record = this.getMainRecord();

    // SELECT (fields to select)
            synchronized (this.getTask())
            {
                Record recordBase = this.getMainRecord().getTable().getCurrentTable().getRecord();
                this.getMainRecord().setOpenMode(iOpenMode);
                int iFieldTypes = this.getFieldTypes(recordBase);
                if (strFields != null)
                    this.getMainRecord().setSelected(strFields);    // Select these fields
                else
                {
                    if (iFieldTypes == BaseBuffer.PHYSICAL_FIELDS)
                        this.getMainRecord().setSelected(true);   // Select these fields (otherwise leave the selection alone)
                }
                iFieldTypes = this.getFieldTypes(recordBase);

                // pend(don) Make sure the table does not serve up the current record (temporary fix until messaging is okay).
                record.setEditMode(Constants.EDIT_NONE);
                // Obviously, the previous code needs to be in a record messageListener method.

                record = record.setHandle(bookmark, iHandleType);
                if (record != null)
                {
                    recordBase = this.getMainRecord().getTable().getCurrentTable().getRecord();
                    BaseBuffer buffer = new VectorBuffer(null, iFieldTypes);
                    buffer.fieldsToBuffer(recordBase, iFieldTypes);
                    return buffer.getPhysicalData();
                }
                else
                    return new Boolean(false);
            }
        } catch (DBException ex)    {
            throw ex;
        } catch (Exception ex)  {
            ex.printStackTrace();
            throw new DBException(ex.getMessage());
        } finally {
            this.getMainRecord().setOpenMode((iOldOpenMode & ~DBConstants.LOCK_TYPE_MASK) | (iOpenMode & DBConstants.LOCK_TYPE_MASK));    // Keep the client lock settings
        }
    }
    /**
     * Get the DATA_SOURCE_HANDLE to the last modified or added record.
     * @param iHandleType The type of handle to use.
     * @return The handle of the last modified record.
     */
    public Object getLastModified(int iHandleType) throws DBException, RemoteException
    {
        try   {
            Utility.getLogger().info("EJB getLastModified");
            synchronized (this.getTask())
            {
                Object bookmark = this.getMainRecord().getLastModified(iHandleType);
                return bookmark;
            }
        } catch (Exception ex)  {
            ex.printStackTrace();
            throw new DBException(ex.getMessage());
        }
    }
    /**
     * Receive this relative record in the table.
     * <p />Note: This is usually used only by thin clients, as thick clients have the code to
     * fake absolute access.
     * @param iRowIndex The row to retrieve.
     * @return The record or an error code as an Integer.
     * @exception DBException File exception.
     * @exception RemoteException RMI exception.
     */
    public Object get(int iRowIndex, int iRecordCount) throws DBException, RemoteException
    {
        try   {
            Utility.getLogger().info("EJB get: row= " + iRowIndex + " count= " + iRecordCount);

            synchronized (this.getTask())
            {
                Record gridRecord = this.getMainRecord();

                gridRecord.setEditMode(DBConstants.EDIT_NONE);  // This will keep me from using the current record (Since cache is handled by the client)
                if (iRecordCount == 1)
                    return this.getAtRow(iRowIndex);
                else
                {
                    Vector<Object> objVector = new Vector<Object>();
                    for (int iRow = iRowIndex; iRecordCount > 0; iRow++, iRecordCount--)
                    {
                        Object objData = this.getAtRow(iRow);
                        objVector.add(objData);
                        if (!(objData instanceof Vector))
                            break;
                    }
                    return objVector; // Vector of multiple rows
                }
            }
        } catch (DBException ex)    {
            throw ex;
        }
    }
    /**
     * Get the gridtable for this record (or create one if it doesn't exit).
     * @param gridRecord The record to get/create a gridtable for.
     * @return The gridtable.
     */
    public GridTable getGridTable(Record gridRecord)
    {
        GridTable gridTable = null;
        if (!(gridRecord.getTable() instanceof GridTable))
        {
            gridTable = new GridTable(null, gridRecord);
            boolean bCacheGrid = false;
            if (DBConstants.TRUE.equalsIgnoreCase(this.getProperty(CACHE_GRID_TABLE_PARAM)))
                bCacheGrid = true;
            gridTable.setCache(bCacheGrid);  // Typically, the client is a gridscreen which caches the records (so I don't have to!)
        }
        gridTable = (GridTable)gridRecord.getTable();
        return gridTable;
    }
    /** A record with this datasource handle changed, notify any behaviors that are checking.
     * NOTE: Be very careful as this code is running in an independent thread
     * (synchronize to the task before calling record calls).
     * NOTE: For now, you are only notified of the main record changes.
     */
    public int handleMessage(BaseMessage message)
    {
        Record record = this.getMainRecord(); // Record changed
        if ((record.getTable() instanceof GridTable)     // Always except HTML
            && (message.getMessageHeader() != null)
                && (message.getMessageHeader() instanceof RecordMessageHeader))
        {
            int iIndex = -1;
            synchronized (this.getTask())
            {
                iIndex = ((GridTable)record.getTable()).updateGridToMessage(message, true, true);
            }
            if (iIndex != -1)
            { // This screen has changed, add this clue so a remote thin table will know where the change was.
                message.put(ModelMessageHandler.START_INDEX_PARAM, Integer.toString(iIndex));
            }
        }
        return super.handleMessage(message);
    }
    /**
     * Retreive this relative record in the table.
     * This method is used exclusively by the get method to read a single row of a grid table.
     * @param iRowIndex The row to retrieve.
     * @return The record or an error code as an Integer.
     * @exception DBException File exception.
     * @exception RemoteException RMI exception.
     */
    private Object getAtRow(int iRowIndex) throws DBException
    {
        try   {
            Utility.getLogger().info("get row " + iRowIndex);

            GridTable gridTable = this.getGridTable(this.getMainRecord());

            gridTable.getCurrentTable().getRecord().setEditMode(Constants.EDIT_NONE);
            Record record = (Record)gridTable.get(iRowIndex);
            int iRecordStatus = DBConstants.RECORD_NORMAL;
            if (record == null)
            {
                if (iRowIndex >= 0)
                    iRecordStatus = DBConstants.RECORD_AT_EOF;
                else
                    iRecordStatus = DBConstants.RECORD_AT_BOF;
            }
            else
            {
                if (record.getEditMode() == DBConstants.EDIT_NONE)
                    iRecordStatus = DBConstants.RECORD_NEW; // Deleted record.
                if (record.getEditMode() == DBConstants.EDIT_ADD)
                    iRecordStatus = DBConstants.RECORD_NEW;
            }
            if (iRecordStatus == DBConstants.NORMAL_RETURN)
            {
                Record recordBase = this.getMainRecord().getTable().getCurrentTable().getRecord();
                int iFieldTypes = this.getFieldTypes(recordBase);
                BaseBuffer buffer = new VectorBuffer(null, iFieldTypes);
                buffer.fieldsToBuffer(recordBase, iFieldTypes);
                return buffer.getPhysicalData();
            }
            else
                return new Integer(iRecordStatus);
        } catch (DBException ex)    {
            throw ex;
        } catch (Exception ex)  {
            ex.printStackTrace();
            throw new DBException(ex.getMessage());
        }
    }
    /**
     * Set a table property.
     * @param strProperty The key to set.
     * @param strValue The value to set it to.
     */
    public void setProperty(String strProperty, String strValue)
    {
        Record record = this.getMainRecord();
        BaseTable table = record.getTable();
        table.setProperty(strProperty, strValue);
    }
    /**
     * Make a thin FieldList for this table.
     * @param strFieldsToInclude The fields to include in this field list (Pass the string [PHYSICAL] or SELECTED).
     * @return The thin FieldList for this record.
     * Usually used for special queries that don't have a field list available.
     */
    public org.jbundle.thin.base.db.FieldList makeFieldList(String strFieldsToInclude) throws RemoteException
    {
        synchronized (this.getTask())
        {
            FieldList fieldList = new FieldList(null);
            Record record = this.getMainRecord();
            boolean bAllSelected = record.isAllSelected();
            if (SELECTED.equalsIgnoreCase(strFieldsToInclude))
                bAllSelected = false;
            if (bAllSelected)
                m_iFieldTypes = BaseBuffer.PHYSICAL_FIELDS;
            else
                m_iFieldTypes = BaseBuffer.SELECTED_FIELDS; // Pass all selected (thin will need them).
            if (SELECTED.equalsIgnoreCase(strFieldsToInclude))
                m_iFieldTypes = BaseBuffer.SELECTED_FIELDS; // Pass all selected (including virtual)
            for (int iFieldSeq = 0; iFieldSeq < record.getFieldCount(); iFieldSeq++)
            {
                BaseField field = record.getField(iFieldSeq);
                if ((m_iFieldTypes == BaseBuffer.PHYSICAL_FIELDS) ||  (m_iFieldTypes == BaseBuffer.DATA_FIELDS))
                    if (field.isVirtual())
                        continue;   // Do not use virtual fields
                if ((m_iFieldTypes == BaseBuffer.DATA_FIELDS) || (m_iFieldTypes == BaseBuffer.SELECTED_FIELDS))
                    if (!field.isSelected())
                        continue;   // If not all fields are selected, only build the selected fields.
                FieldInfo fieldInfo = new FieldInfo(fieldList, field.getFieldName(), field.getMaxLength(), field.getFieldDesc(), field.getDefault());
                fieldInfo.setDataClass(field.getDataClass());
            }
    //      for (int iKeyFieldSeq = 0; iKeyFieldSeq < record.getKeyAreaCount(); iKeyFieldSeq++)
            int iKeyFieldSeq = 0;
            {
                KeyArea keyArea = record.getKeyArea(iKeyFieldSeq);
                KeyAreaInfo keyAreaInfo = new KeyAreaInfo(fieldList, keyArea.getUniqueKeyCode(), keyArea.getKeyName());
                int iKeyField = 0;
                    keyAreaInfo.addKeyField(keyArea.getField(iKeyField).getFieldName(), keyArea.getKeyOrder(iKeyField));
            }
            return fieldList;
        }
    }
    /**
     * The last field types selected (ie., PHYSICAL_FIELD, SELECTED_FIELDS, etc.).
     */
    protected int m_iFieldTypes = -1;
    /**
     * Return thie field types to select/return used on the last makeFieldList call.
     * @param record The record to check the field types selected.
     * @return The fieldtypes selected.
     */
    public int getFieldTypes(Record record)
    {
        int iFieldTypes = m_iFieldTypes;
        if (iFieldTypes == -1)
        {
            iFieldTypes = BaseBuffer.PHYSICAL_FIELDS;
            if (!record.isAllSelected())
                iFieldTypes = BaseBuffer.DATA_FIELDS; // (Selected excluding virtual)
        }
        return iFieldTypes;
    }
    /**
     * Return thie field types to select/return used on the last makeFieldList call.
     * @param record The record to check the field types selected.
     * @return The fieldtypes selected.
     */
    public void setFieldTypes(Record record, int iFieldTypes)
    {
        m_iFieldTypes = iFieldTypes;
    }
    /**
     * Move the buffer to the fields. Use this instead of directly calling buffer.bufferToFields to
     * simulate user input.
     * NOTE: This is some wierd code - You must call the field behaviors only if they change and only after all of them are loaded (ouch). 
     * @param record The record to check the field types selected.
     * @return The fieldtypes selected.
     */
    public int moveBufferToFields(Object data, int iFieldTypes, Record recordBase)
        throws DBException
    {
        BaseBuffer buffer = new VectorBuffer((Vector)data, iFieldTypes);
        this.cleanBuffer(buffer, recordBase);
        
        Object objCounter = null;
        if (recordBase.getCounterField() != null)
            objCounter = recordBase.getCounterField().getData();
        int iOpenMode = recordBase.getOpenMode();
        if ((iOpenMode & DBConstants.OPEN_REFRESH_AND_LOCK_ON_CHANGE_STRATEGY) == DBConstants.OPEN_REFRESH_AND_LOCK_ON_CHANGE_STRATEGY)
            recordBase.setOpenMode(iOpenMode & ~DBConstants.OPEN_REFRESH_AND_LOCK_ON_CHANGE_STRATEGY);     // Don't refresh until I'm done.
        int iFieldCount = recordBase.getFieldCount();
        boolean[] rgbModified = new boolean[iFieldCount];
        Object[] rgobjEnabled = new Object[iFieldCount];
        for (int iFieldSeq = 0; iFieldSeq < iFieldCount; iFieldSeq++)
        {
            rgbModified[iFieldSeq] = recordBase.getField(iFieldSeq).isModified();
            recordBase.getField(iFieldSeq).setModified(false);
            rgobjEnabled[iFieldSeq] = recordBase.getField(iFieldSeq).setEnableListeners(false);
        }
        int iErrorCode = buffer.bufferToFields(recordBase, iFieldTypes, DBConstants.DONT_DISPLAY, DBConstants.SCREEN_MOVE);   // Screen move... need to validate!
        if (iErrorCode != DBConstants.NORMAL_RETURN)
            throw new DatabaseException(iErrorCode);
        recordBase.setOpenMode(iOpenMode);     // Restore to original value.
        if (objCounter != null)
            if (recordBase.getCounterField().getData() == null)
                recordBase.getCounterField().setData(objCounter);   // Can't set the counter to null!
        for (int iFieldSeq = 0; iFieldSeq < recordBase.getFieldCount(); iFieldSeq++)
        {
            recordBase.getField(iFieldSeq).setEnableListeners((boolean[])rgobjEnabled[iFieldSeq]);
        }
        for (int iFieldSeq = 0; iFieldSeq < recordBase.getFieldCount(); iFieldSeq++)
        {
            BaseField field = recordBase.getField(iFieldSeq);
            if (field.isModified())
            {
                iErrorCode = field.handleFieldChanged(DBConstants.DONT_DISPLAY, DBConstants.SCREEN_MOVE);
                if (iErrorCode != DBConstants.NORMAL_RETURN)
                {
                    throw new DatabaseException(iErrorCode);
                }
            }
            if (rgbModified[iFieldSeq])
                if (!field.isModified()) // If this started modified, set it back.
                    field.setModified(true);
        }
        return iErrorCode;
    }
    /**
     * Clean the buffer of fields that will change a field that has been modified by the client.
     * @param buffer The Vector buffer to clean.
     * @param record The target Record.
     */
    public void cleanBuffer(BaseBuffer buffer, Record record)
    {
        Vector<Object> vector = (Vector)buffer.getPhysicalData();
        int iCurrentIndex = 0;
        buffer.resetPosition(); // Start at the first field
        int iFieldCount = record.getFieldCount();   // Number of fields to read in
        for (int iFieldSeq = Constants.MAIN_FIELD; iFieldSeq <= iFieldCount + Constants.MAIN_FIELD - 1; iFieldSeq++)
        {
            BaseField field = record.getField(iFieldSeq);
            if (!buffer.skipField(field))
            {
                if (field.isModified())
                    vector.set(iCurrentIndex, BaseBuffer.DATA_SKIP);    // If target modified don't move data
        //?        else if (record.getEditMode() == DBConstants.EDIT_ADD)
        //            if (field.getDefault() == vector.get(iCurrentIndex))
        //                vector.set(iCurrentIndex, BaseBuffer.DATA_SKIP); // On Add if data is the default, don't set.
                iCurrentIndex++;
            }
        }
    }
    /**
     * Get the remote table reference.
     * If you want the remote table session, call this method with java.rmi.server.RemoteStub.class.
     * @classType The base class I'm looking for (If null, return the next table on the chain) 
     * @return The remote table reference.
     */
    public RemoteTable getRemoteTableType(Class<?> classType) throws RemoteException
    {
        Utility.getLogger().warning("Error: getRemoteTableType should never be called remotely");
        return null;
    }
    /**
     * Get/Make this remote database session for this table session.
     * @param properties The client database properties (Typically for transaction support).
     */
    public RemoteDatabase getRemoteDatabase(Map<String, Object> properties) throws RemoteException
    {
        Record record = this.getMainRecord();
        BaseDatabase database = record.getTable().getDatabase();
        Application app = (Application)this.getTask().getApplication();
        DatabaseSession remoteDatabase = null;
        for (Task task : app.getTaskList().keySet())
        {
            if (task instanceof BaseSession)
            {
                remoteDatabase = ((BaseSession)task).getDatabaseSession(database);
                if (remoteDatabase != null)
                    break;
            }
        }
        if (database != null)
            if (properties != null)
                database.getProperties().putAll(properties);    // Add these properties to the current db properties.
        if (remoteDatabase == null)
            remoteDatabase = createDatabaseSession(database, properties);
        return remoteDatabase;
    }
    /**
     * Create a session for this database (and place it in the right spot in the session hierarchy)
     * @param database
     * @param properties
     * @return
     * @throws RemoteException
     */
    public DatabaseSession createDatabaseSession(BaseDatabase database, Map<String, Object> properties) throws RemoteException
    {
        DatabaseSession remoteDatabase = null;
        Map<String,Object> propOld = this.getProperties();
        this.setProperties(properties);
        DatabaseOwner databaseOwner = database.getDatabaseOwner();
        if (databaseOwner == null)
            databaseOwner = this.getDatabaseOwner();    // Never
        this.setProperties(propOld);
        if (databaseOwner instanceof Application)
            remoteDatabase = new DatabaseSession((Application)databaseOwner);   // Typical
        else
            remoteDatabase = new DatabaseSession((BaseSession)databaseOwner, null); // If AUTO_COMMIT if off, I need my own database
        database.setMasterSlave(RecordOwner.SLAVE);      // Don't create client behaviors
        remoteDatabase.setDatabase(database);
        return remoteDatabase;
    }
    /**
     * Process the command.
     * Step 1 - Process the command if possible and return true if processed.
     * Step 2 - If I can't process, pass to all children (with me as the source).
     * Step 3 - If children didn't process, pass to parent (with me as the source).
     * Note: Never pass to a parent or child that matches the source (to avoid an endless loop).
     * @param strCommand The command to process.
     * @param sourceSField The source screen field (to avoid echos).
     * @param bUseSameWindow If this command creates a new screen, create in a new window?
     * @return true if success.
     */
    public Object doRemoteCommand(String strCommand, Map<String, Object> properties) throws RemoteException, DBException
    {
    	int iRecordCount = m_vRecordList.getRecordCount();
    	int iOldMasterSlave = this.setMasterSlave(RecordOwner.SLAVE | RecordOwner.MASTER);
    	Record record = this.getMainRecord();
    	if (properties != null)
    		if (Boolean.TRUE.equals(properties.get(MenuConstants.CLONE)))
    			if (record.getClass().getName().equals(properties.get(DBParams.RECORD)))
		{
			try {
				record = (Record)record.clone();
				this.addRecord(record);
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
			record.readSameRecord(this.getMainRecord(), false, false);
		}
    	Object objReturn = record.doRemoteCommand(strCommand, properties);
    	this.setMasterSlave(iOldMasterSlave);
    	if (record != this.getMainRecord())
    	{
    		record.free();
    		while (m_vRecordList.getRecordCount() > iRecordCount)
    		{	// Close all except the main record
    			m_vRecordList.getRecordAt(m_vRecordList.getRecordCount() - 1).free();
    		}
    	}
    	if (!Boolean.FALSE.equals(objReturn))
    		return objReturn;
        return super.doRemoteCommand(strCommand, properties);
    }
    /**
     * Is this recordowner the master or slave.
     * The slave is typically the TableSessionObject that is created to manage a ClientTable.
     * @return The MASTER/SLAVE flag.
     */
    public int getMasterSlave()
    {
    	int iMasterSlave = super.getMasterSlave();
    	if (iMasterSlave == RecordOwner.MASTER)
    		if (this.getClass() == org.jbundle.base.remote.db.TableSession.class)
    			return RecordOwner.SLAVE;       // Auto-created session is the Slave session (Unless it was overridden)
        return iMasterSlave;
    }

}
