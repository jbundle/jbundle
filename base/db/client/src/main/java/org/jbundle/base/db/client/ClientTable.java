/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db.client;

/**
 * Copyright © 2012 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;

import org.jbundle.base.db.BaseDatabase;
import org.jbundle.base.db.BaseTable;
import org.jbundle.base.db.DatabaseException;
import org.jbundle.base.db.KeyArea;
import org.jbundle.base.db.Record;
import org.jbundle.base.db.SQLParams;
import org.jbundle.base.db.event.FileListener;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.DBParams;
import org.jbundle.base.model.Utility;
import org.jbundle.model.DBException;
import org.jbundle.model.RemoteException;
import org.jbundle.model.db.Rec;
import org.jbundle.thin.base.db.FieldList;
import org.jbundle.thin.base.db.buff.BaseBuffer;
import org.jbundle.thin.base.db.buff.VectorBuffer;
import org.jbundle.thin.base.db.client.CachedRemoteTable;
import org.jbundle.thin.base.remote.RemoteTable;
import org.jbundle.thin.base.util.ThinUtil;


/**
 * The client side of the SocketTable <--> RemoteTable socket.
 * NOTE: Since BaseTable is suppose to be thread-safe, you have to be careful when
 * you call the RemoteTable since Ejb can only have one session - you must synchronize
 * to the server to be safe:<br />
 *              synchronized (record.getRecordOwner().getTask().getServer())<br />
 *              {   // In case this is called from another task
 */
public class ClientTable extends BaseTable
{
    /**
     * The TableSessionObject session.
     */
    protected RemoteTable m_tableRemote = null;
    /**
     * Last modified bookmark. This is the hint returned from remote add call and returned on getLastModified.
     */
    protected Object m_LastModifiedBookmark = null;
    /**
     * Direction of keys at last open
     */
    protected boolean m_bDirectionCurrent = DBConstants.ASCENDING;

    /**
     * Constructor.
     */
    public ClientTable()
    {
        super();
    }
    /**
     * Constructor.
     * @param database The database to add this table to.
     * @param record The record to connect to this table.
     */
    public ClientTable(BaseDatabase database, Record record)
    {
        this();
        this.init(database, record);
    }
    /**
     * Init this table.
     * Add this table to the database and hook this table to the record.
     * @param database The database to add this table to.
     * @param record The record to connect to this table.
     */
    public void init(BaseDatabase database, Record record)
    {
        super.init(database, record);

        // Call super to set the table property (without calling remote).
        super.setProperty(DBParams.SUPRESSREMOTEDBMESSAGES, DBConstants.TRUE);
    }
    /**
     * Free this table object.
     * Don't call this directly, freeing the record will free the table correctly.
     * Client table just calls the remote freeTable() method.
     */
    public void free()
    {
        try   {
            if (m_tableRemote != null)
            {
                synchronized (this.getSyncObject())
                {   // In case this is called from another task
                    m_tableRemote.freeRemoteSession();
                }
            }
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
        m_tableRemote = null;
        super.free();
    }
    /**
     * Create a new empty record.
     * Discard the current fields, init all the fields and display them,
     * and set the status to EDIT_ADD.
     */
    public void addNew() throws DBException
    {
        super.addNew();     // Don't access server... clear fields locally.
    }
    /**
     * Do the physical Open on this table (requery the table).
     * @exception DBException File exception.
     */
    public void doOpen() throws DBException
    {
        try   {
    // FROM is automatic, since the remote BaseRecord is exactly the same as this one
    // ORDER BY
            KeyArea keyArea = this.getRecord().getKeyArea(-1);  // Current index
            String strKeyArea = null;
            boolean bDirection = DBConstants.ASCENDING;
            if (true)
            {
                strKeyArea = keyArea.getKeyName();
                bDirection = keyArea.getKeyField(DBConstants.MAIN_KEY_FIELD).getKeyOrder();
            }
            m_bDirectionCurrent = bDirection;
    // Open mode
            int iOpenMode = this.getRecord().getOpenMode();
    // SELECT (fields to select)
            String strFields = null;
            if (true) // All selected?
            {
                strFields = this.getRecord().getSQLFields(DBConstants.SQL_SELECT_TYPE, true);
                if (strFields.equals(" *"))
                    strFields = null; // Select all
            }
    // WHERE XYZ >=
            Object objInitialKey = null;
            if (true)
            {
                BaseBuffer buffer = new VectorBuffer(null);
                this.getRecord().handleInitialKey();
                if (keyArea.isModified(DBConstants.START_SELECT_KEY))
                {   // Anything set?
                    keyArea.setupKeyBuffer(buffer, DBConstants.START_SELECT_KEY, false);
                    objInitialKey = buffer.getPhysicalData();
                    buffer.addNextString(Integer.toString(keyArea.lastModified(DBConstants.START_SELECT_KEY, false))); // Largest modified field.
                }
            }
    // WHERE XYZ <=
            Object objEndKey = null;
            if (true)
            {
                BaseBuffer buffer = new VectorBuffer(null);
                this.getRecord().handleEndKey();
                if (keyArea.isModified(DBConstants.END_SELECT_KEY))
                {   // Anything set?
                    keyArea.setupKeyBuffer(buffer, DBConstants.END_SELECT_KEY, false);
                    objEndKey = buffer.getPhysicalData();
                    buffer.addNextString(Integer.toString(keyArea.lastModified(DBConstants.END_SELECT_KEY, false))); // Largest modified field.
                }
            }
    // WHERE XYZ
            byte[] byBehaviorData = null;
            boolean bDirty = false;
            if (this.getRecord().getListener() != null)
            {
                ByteArrayOutputStream baOut = new ByteArrayOutputStream();
                ObjectOutputStream daOut = new ObjectOutputStream(baOut);
                FileListener listener = (FileListener)this.getRecord().getListener();
                while (listener != null)
                {
                    if ((listener.getMasterSlaveFlag() & FileListener.RUN_IN_SLAVE) != 0) // Should exist in a SERVER environment
                        if ((listener.getMasterSlaveFlag() & FileListener.DONT_REPLICATE_TO_SLAVE) == 0) // Yes, replicate to the SERVER environment
                            if (listener.isEnabledListener()) // Yes, only replicate enabled listeners.
                    { // There should be a copy of this on the server
                        bDirty = true;
                        daOut.writeUTF(listener.getClass().getName());
                        listener.initRemoteStub(daOut);
                    }
                    listener = (FileListener)listener.getNextListener();
                }
                daOut.flush();
                if (bDirty)
                    byBehaviorData = baOut.toByteArray();
                daOut.close();
                baOut.close();
            }

            this.checkCacheMode(Boolean.TRUE);      // Make sure the cache is set up correctly for this type of query (typically needed)

            synchronized (this.getSyncObject())
            {   // In case this is called from another task
                m_tableRemote.open(strKeyArea, iOpenMode, bDirection, strFields, objInitialKey, objEndKey, byBehaviorData);
            }
        } catch (Exception ex) {
            throw DatabaseException.toDatabaseException(ex);
        }
    }
    /**
     * Make sure the cache is set up correctly for this type of query.
     * @param boolShouldBeCached Default cache mode (if null, no default assumed).
     */
    public void checkCacheMode(Boolean boolShouldBeCached)
    {
        try {
            boolean bCurrentlyCached = (this.getRemoteTableType(CachedRemoteTable.class) != null);
            if ((this.getRecord().getOpenMode() & DBConstants.OPEN_CACHE_RECORDS) == DBConstants.OPEN_CACHE_RECORDS)
                boolShouldBeCached = Boolean.TRUE;
//            if ((this.getRecord().getOpenMode() & DBConstants.OPEN_READ_ONLY) == DBConstants.OPEN_READ_ONLY);
//                    bShouldBeCached = true;
            if ((this.getRecord().getOpenMode() & DBConstants.OPEN_DONT_CACHE) == DBConstants.OPEN_DONT_CACHE)
                boolShouldBeCached = Boolean.FALSE;
            if (boolShouldBeCached == null)
                return;     // Not specified, Don't change.
            if ((!bCurrentlyCached) && (boolShouldBeCached.booleanValue()))
            {   // Add a cache
                Utility.getLogger().info("Cache ON: " + this.getRecord().getTableNames(false));
                this.setRemoteTable(new CachedRemoteTable(m_tableRemote));
            }
            else if ((bCurrentlyCached) && (!boolShouldBeCached.booleanValue()))
            {   // Remove the cache
                Utility.getLogger().info("Cache OFF: " + this.getRecord().getTableNames(false));
//                RemoteTable tableRemote = this.getRemoteTableType(org.jbundle.model.Remote.class);
                RemoteTable tableRemote = this.getRemoteTableType(CachedRemoteTable.class).getRemoteTableType(null);
                ((CachedRemoteTable)m_tableRemote).setRemoteTable(null);
                ((CachedRemoteTable)m_tableRemote).free();
                this.setRemoteTable(tableRemote);
            }
        } catch (RemoteException ex)    {
            // Never for this usage
        }
    }
    /**
     * Close this table.
     * This is not implemented for a ClientTable, since the TableSessionObject always does
     * a close before each open.
     */
    public void close()
    {
        // Remote Close is done automatically at open
        super.close();
    }
    /**
     * Create/Clear the current object (Always called from the record class).
     * This is not implemented for a ClientTable, since the TableSessionObject always does
     * an addNew before an add() call.
     * @exception DBException File exception.
     */
    public void doAddNew() throws DBException
    {
    }
    /**
     * Add this record (Always called from the record class).
     * Make the remote add call with the current data.
     * @param record The record to add.
     * @exception DBException File exception.
     */
    public void doAdd(Record record) throws DBException
    {
        m_LastModifiedBookmark = null;
        try   {
            BaseBuffer buffer = (BaseBuffer)m_dataSource;
            Object data = buffer.getPhysicalData();
            synchronized (this.getSyncObject())
            {   // In case this is called from another task
                m_LastModifiedBookmark = m_tableRemote.add(data, this.getRecord().getOpenMode());
            }
        } catch (Exception ex) {
            throw DatabaseException.toDatabaseException(ex);
        }
    }
    /**
     * Lock the current record.
     * Will not make the remote call if the remote db doesn't have lock capability.
     * The remote set method automatically does an edit before calling.
     * This method responds differently depending on what open mode the record is in:
     * OPEN_DONT_LOCK - A physical lock is not done. This is usually where deadlocks are possible
     * (such as screens) and where transactions are in use (and locks are not needed).
     * OPEN_LOCK_ON_EDIT - Holds a lock until an update or close. (Update crucial data, or hold records for processing)
     * Returns false is someone already has a lock on this record.
     * OPEN_WAIT_FOR_LOCK - Don't return from edit until you get a lock. (ie., Add to the total).
     * Returns false if someone has a hard lock or time runs out.
     * @return true if successful, false if lock failed.
     * @exception DBException FILE_NOT_OPEN
     * @exception DBException INVALID_RECORD - Record not current.
     */
    public int doEdit() throws DBException
    {
        try   {
            if ((!SQLParams.DB_EDIT_NOT_SUPPORTED.equals(((ClientDatabase)this.getDatabase()).getRemoteProperty(SQLParams.EDIT_DB_PROPERTY, false)))        //+ Remote db supports edit
                || ((this.getRecord().getOpenMode() & DBConstants.OPEN_LOCK_ON_EDIT_STRATEGY) != 0))
            {
                synchronized (this.getSyncObject())
                {   // In case this is called from another task
                    int iOpenMode = this.getRecord().getOpenMode() | DBConstants.OPEN_ERROR_ON_DIRTY_LOCK_TYPE;  // Make sure server doesn't refresh on dirty (I will do the refresh)
                    return m_tableRemote.edit(iOpenMode);    // Only call if edit is supported by remote db
                }
            }
            else
                return DBConstants.NORMAL_RETURN;
        } catch (Exception ex) {
            throw DatabaseException.toDatabaseException(ex);
        }
    }
    /**
     * Update this record (Always called from the record class).
     * @param record The record to add.
     * @exception DBException File exception.
     */
    public void doSet(Record record) throws DBException
    {
        try   {
            BaseBuffer buffer = (BaseBuffer)m_dataSource;
            Object data = buffer.getPhysicalData();
            synchronized (this.getSyncObject())
            {   // In case this is called from another task
                m_tableRemote.set(data, this.getRecord().getOpenMode());
            }
        } catch (Exception ex) {
            throw DatabaseException.toDatabaseException(ex);
        }
    }
    /**
     * Delete this record (Always called from the record class).
     * @exception DBException File exception.
     */
    public void doRemove() throws DBException
    {
        try   {
            synchronized (this.getSyncObject())
            {   // In case this is called from another task
                m_tableRemote.remove(null, this.getRecord().getOpenMode());
            }
        } catch (Exception ex) {
            throw DatabaseException.toDatabaseException(ex);
        }
    }
    /**
     * Move the position of the record.
     * @param iRelPosition - Relative position positive or negative or FIRST_RECORD/LAST_RECORD.
     * @return NORMAL_RETURN - The following are NOT mutually exclusive
     * @exception DBException File exception.
     */
    public int doMove(int iRelPosition) throws DBException
    {
        this.checkCacheMode(Boolean.TRUE);      // Make sure the cache is set up correctly for this type of query (typically needed)
        int iErrorCode = DBConstants.NORMAL_RETURN;
        try   {
            Object objData = null;
            synchronized (this.getSyncObject())
            {   // In case this is called from another task
                objData = m_tableRemote.doMove(iRelPosition, 1);
            }
            if (objData instanceof Vector)
            {
                Vector<Object> data = (Vector)objData;  //m_tableRemote.dataToFields();
                Record recordBase = this.getRecord();
                int iFieldTypes = BaseBuffer.PHYSICAL_FIELDS;
                if (!recordBase.isAllSelected())
                    iFieldTypes = BaseBuffer.DATA_FIELDS; // SELECTED_FIELDS; (selected and physical)
                BaseBuffer buffer = new VectorBuffer(data, iFieldTypes);
                if (DBParams.RECORD.equals(buffer.getHeader()))
                { // Warning: The target record was a multitable and This is the record name!
                    String strTableNames = buffer.getHeader().toString();
                    Utility.getLogger().warning("ClientTable.doMove() - Warning: Multitable needs to be specified: " + strTableNames);
                }
                else
                    buffer.resetPosition();
                m_dataSource = buffer;
                iErrorCode = DBConstants.NORMAL_RETURN;
            }
            else if (objData instanceof Number)
                iErrorCode = ((Number)objData).intValue();
            else
                iErrorCode = DBConstants.ERROR_RETURN;  // Never
            return iErrorCode;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw DatabaseException.toDatabaseException(ex);
        }
    }
    /**
     * Read the record that matches this record's current key.
     * <p>NOTE: You do not need to Open to do a seek or addNew.
     * @param strSeekSign - Seek sign:
     * @return true if successful, false if not found.
     * @exception FILE_NOT_OPEN.
     * @exception KEY_NOT_FOUND - The key was not found on read.
     */
    public boolean doSeek(String strSeekSign) throws DBException
    {
        KeyArea keyArea = this.getRecord().getKeyArea(-1);  // Current index
        String strKeyArea = keyArea.getKeyName();

        this.checkCacheMode(Boolean.FALSE);      // Make sure the cache is set up correctly for this type of query (typically not needed)

        Object objKeyData = null;
        if (keyArea.getKeyFields() == 1)    // Special case - single unique key;
            objKeyData = keyArea.getField(DBConstants.MAIN_KEY_FIELD).getData();
        else
        {
            BaseBuffer buffer = new VectorBuffer(null);
            keyArea.setupKeyBuffer(buffer, DBConstants.FILE_KEY_AREA);
            objKeyData = buffer.getPhysicalData();
        }
        try   {
            Object objData = null;
            String strFields = null;
            if (!this.getRecord().isAllSelected())
                strFields = this.getRecord().getSQLFields(DBConstants.SQL_SELECT_TYPE, true);
            boolean bDirection = keyArea.getKeyField(DBConstants.MAIN_KEY_FIELD).getKeyOrder();
            if (m_bDirectionCurrent != bDirection)
            {       // Change the key direction (rarely).
                this.close();
                this.open();
            }
            int iOpenMode = this.getRecord().getOpenMode();    // Open mode

            synchronized (this.getSyncObject())
            {   // In case this is called from another task
                objData = m_tableRemote.seek(strSeekSign, iOpenMode, strKeyArea, strFields, objKeyData);
            }
            if (objData instanceof Vector)
            {
                Record recordBase = this.getRecord();
                int iFieldTypes = BaseBuffer.PHYSICAL_FIELDS;
                if (!recordBase.isAllSelected())
                    iFieldTypes = BaseBuffer.DATA_FIELDS;   // SELECTED_FIELDS; (Selected and physical)
                Vector<Object> data = (Vector)objData;  //m_tableRemote.dataToFields();
                BaseBuffer buffer = new VectorBuffer(data, iFieldTypes);
                m_dataSource = buffer;
                return true;    // Success
            }
            else
                return false; // Not found
        } catch (Exception ex) {
            ex.printStackTrace();
            throw DatabaseException.toDatabaseException(ex);
        }
    }
    /**
     * Reposition to this record using this bookmark.
     * @param bookmark The handle to use to position the record.
     * @param iHandleType The type of handle (DATA_SOURCE/OBJECT_ID,OBJECT_SOURCE,BOOKMARK).
     * @return  - true - record found/false - record not found
     * @exception FILE_NOT_OPEN.
     * @exception DBException File exception.
     */
    public boolean doSetHandle(Object bookmark, int iHandleType) throws DBException
    {
        boolean bIsOpen = m_bIsOpen;
        this.checkCacheMode(Boolean.FALSE);      // Make sure the cache is set up correctly for this type of query (typically not needed)

        if (((iHandleType & DBConstants.OBJECT_SOURCE_HANDLE) != DBConstants.OBJECT_SOURCE_HANDLE)
            && ((iHandleType & DBConstants.OBJECT_ID_HANDLE) != DBConstants.OBJECT_ID_HANDLE))
                iHandleType = DBConstants.BOOKMARK_HANDLE;
        try   {
            Object objData = null;
            String strFields = null;
            if (!this.getRecord().isAllSelected())
                strFields = this.getRecord().getSQLFields(DBConstants.SQL_SELECT_TYPE, true);
            int iOpenMode = this.getRecord().getOpenMode();    // Open mode
            synchronized (this.getSyncObject())
            {   // In case this is called from another task
                objData = m_tableRemote.doSetHandle(bookmark, iOpenMode, strFields, iHandleType);
            }
            if (objData instanceof Vector)
            {
                Vector<Object> data = (Vector)objData;  //m_tableRemote.dataToFields();
                Record recordBase = this.getRecord();
                int iFieldTypes = BaseBuffer.PHYSICAL_FIELDS;
                if (!recordBase.isAllSelected())
                    iFieldTypes = BaseBuffer.DATA_FIELDS;   //SELECTED_FIELDS; (Selected and Physical)
                BaseBuffer buffer = new VectorBuffer(data, iFieldTypes);
                m_dataSource = buffer;
                return true;    // Success
            }
            else
                return false; // Not found
        } catch (Exception ex) {
            throw DatabaseException.toDatabaseException(ex);
        } finally   {
            m_bIsOpen = bIsOpen;    // Restore
        }
    }
    /**
     * Get a unique object that can be used to reposition to this record.
     * Note: This doesn't do a remote call, it just returns the current handle (bookmark type).
     * @param int iHandleType 
     *  BOOKMARK_HANDLE - long ID for access to native (DB) objects<br />
     *  OBJECT_ID_HANDLE - object ID of persistent object (remote or local)<br />
     *  DATA_SOURCE_HANDLE - pointer to the physical object (persistent or not)<br />
     *  OBJECT_SOURCE_HANDLE - source of this object (RMI Server or JDBC path)<br />
     *  FULL_OBJECT_HANDLE - full path to this object ObjectSource + ObjectID.
     * @return The data.
     * @exception FILE_NOT_OPEN.
     * @exception INVALID_RECORD - There is no current record.
     */
    public Object getHandle(int iHandleType) throws DBException   
    {
        if ((iHandleType & DBConstants.OBJECT_SOURCE_HANDLE) != DBConstants.OBJECT_SOURCE_HANDLE)
            iHandleType = DBConstants.BOOKMARK_HANDLE;      // This guarantees local and remote synchronization
        Object bookmark = super.getHandle(iHandleType);    // Use local handle
        return bookmark;
    }
    /**
     * Get the Handle to the last modified or added record.
     * This uses some very inefficient (and incorrect) code... override if possible.
     * NOTE: There is a huge concurrency problem with this logic if another person adds
     * a record after you, you get the their (wrong) record, which is why you need to
     * provide a solid implementation when you override this method.
     * @param iHandleType The handle type.
     * @return The bookmark.
     */
    public Object getLastModified(int iHandleType)
    {
        if ((iHandleType & DBConstants.OBJECT_SOURCE_HANDLE) != DBConstants.OBJECT_SOURCE_HANDLE)
            iHandleType = DBConstants.BOOKMARK_HANDLE;      // This guarantees local and remote synchronization
        if (iHandleType == DBConstants.BOOKMARK_HANDLE)
            if (m_LastModifiedBookmark != null)
                return m_LastModifiedBookmark;      // Ignore the iHandleType (Always bookmark type for JDBC)
        try   {
            synchronized (this.getSyncObject())
            {   // In case this is called from another task
                return m_tableRemote.getLastModified(iHandleType);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    /**
     * Move the data source buffer to all the fields.
     * In this implementation, move the local copy of the returned datasource to the fields.
     * @return an error code.
     * @exception DBException File exception.
     */
    public int dataToFields(Rec record) throws DBException
    {
        try   {
            return ((BaseBuffer)m_dataSource).bufferToFields((FieldList)record, DBConstants.DONT_DISPLAY, DBConstants.READ_MOVE);
        } catch (Exception ex) {
            throw DatabaseException.toDatabaseException(ex);
        }
    }
    /**
     * Move all the fields to the output buffer.
     * In this implementation, create a new VectorBuffer and move the fielddata to it.
     * @exception DBException File exception.
     */
    public void fieldsToData(Rec record) throws DBException
    {
        try   {
            int iFieldTypes = BaseBuffer.PHYSICAL_FIELDS;
            if (!((Record)record).isAllSelected())
                iFieldTypes = BaseBuffer.DATA_FIELDS;   //SELECTED_FIELDS; (Selected and physical)
            m_dataSource = new VectorBuffer(null, iFieldTypes);   //x new StringBuff();
            ((BaseBuffer)m_dataSource).fieldsToBuffer((FieldList)record);
        } catch (Exception ex) {
            throw DatabaseException.toDatabaseException(ex);
        }
    }
    /**
     * Get the remote table reference.
     * If you want the remote table session, call this method with Remote.class.
     * @classType The base class I'm looking for (If null, return the next table on the chain) 
     * @return The remote table reference.
     */
    public RemoteTable getRemoteTableType(Class<?> classType)
    {
        return ThinUtil.getRemoteTableType(m_tableRemote, classType);
    }
    /**
     * Set the remotetable reference.
     * Usually called from database, since database creates the table object.
     * @param tableRemote The reference to thre RemoteTable object.
     */
    public void setRemoteTable(RemoteTable tableRemote)
    {
        m_tableRemote = tableRemote;
    }
    /**
     * Set the remotetable reference.
     * Usually called from database, since database creates the table object.
     * @param tableRemote The reference to thre RemoteTable object.
     */
    public RemoteTable getRemoteTable()
    {
        return m_tableRemote;
    }
    /**
     * Is this a table (or SQL) type of "Table"?.
     * This flag determines when you hit the end of a query. If set, every record is compared
     * againt the last record in the query. If not, the only EOF determines the end (ie., a SQL query).
     * Override this and return false where the DB returns the complete query.
     * @return false Because a client table has the remote table handle the complicated calls.
     */
    public boolean isTable()
    {   // Use the physical 'table' calls
        return false;
    }
    /**
     * Set a property for this table.
     * Usually used to Enable/Disable autosequence for this table.
     * <p />Sets the remote properties and also sets the local properties.
     * @param strProperty The key to set.
     * @param strValue The value to set.
     */
    public void setProperty(String strProperty, String strValue)
    {
        try   {
            synchronized (this.getSyncObject())
            {   // In case this is called from another task
                m_tableRemote.setRemoteProperty(strProperty, strValue);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        super.setProperty(strProperty, strValue);
    }
    /**
     * Get the server serving this remote table.
     * This is needed to synchronize the remote calls.
     * For Jini/Rmi, just sync to the remote object, for Ejb or
     * a call through a proxy, sync to the server (record->->->server).
     * @return The server object.
     */
    public Object getSyncObject()
    {
        return ThinUtil.getRemoteTableType(m_tableRemote, org.jbundle.model.Remote.class);
    }
    /**
     * Return this table drive type for the getObjectSource call. (Override this method)
     * @return java.lang.String Return "JDBC".
     */
    public String getSourceType()
    {
        return DBParams.CLIENT;
    }
}
