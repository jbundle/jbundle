/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db;

/**
 * Copyright © 2012 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import org.jbundle.base.db.event.FileListener;
import org.jbundle.base.db.filter.SubCurrentFilter;
import org.jbundle.base.db.grid.DataRecord;
import org.jbundle.base.db.grid.DataRecordBuffer;
import org.jbundle.base.db.grid.DataRecordList;
import org.jbundle.base.message.record.RecordMessageHeader;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.DBParams;
import org.jbundle.base.model.RecordOwner;
import org.jbundle.model.DBException;
import org.jbundle.model.db.Rec;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.FieldInfo;
import org.jbundle.thin.base.db.FieldList;
import org.jbundle.thin.base.db.buff.BaseBuffer;
import org.jbundle.thin.base.db.buff.VectorBuffer;
import org.jbundle.thin.base.message.BaseMessage;


/**
 * Needed for databases without scrollable cursors.
 * This is a utility class that extends any recordset with the use
 * of the get(int row) command.<p>
 * This is especially useful for grid and popup controls where you
 * must consistently position to a set row in the table. The move(int pos)
 * command in table cannot be relied on to position correctly due to
 * concurrent updates done by different users.<p>
 * Another use for this list is to keep track of object records and their order 
 * in the base class.<p>
 * This list is used to speed the process of finding a record from a row number or from a
 * few fields in the current key. During select, this table is filled with bookmarks,
 * and optionally fields from the records being read.<p>
 * <b>Technical Notes:</b><p>
 * This class is used for two main purposes:<p>
 * 1) To provide an easy way for a grid screen to access records by absolute position.<p>
 * 2) To provide simple access for popup menus to access records by absolute position
 *  or the bookmark (if popup is linked to the ReferenceField converter).<p>
 * <b>To accomplish this, this class uses the following data structures</b><p>
 * m_aRecords - An ordered list of ALL records. If the max size is reached, this list's
 * granularity increases to offer quick relative access to the entire recordset.<p>
 * m_aCurrentRecord - An ordered list of the most current sequential records accessed for quick
 * access to records displayed in a grid on the screen.<p>
 * <pre>
 * ***Changes required for this list***
 * 1) If search fields are not in the key, don't do a keyOnly read
 * 2) If the primary key field is not in this record, do a binary search through the record list for it!
 * 3) If the search fields are in the key, do a binary search rather than a sequential one
 * 4) Implement EditableTable in Swing set for screen table support!
 * ** TODO **
 * 1) Create another gridlist to add new (.add()) records to.
 * </pre>
 */
public class GridTable extends PassThruTable
{
    /**
     * Unknown file position.
     */
    protected static final int UNKNOWN_POSITION = -1;
    /**
     * 'addNew' file position.
     */
    protected static final int ADD_NEW_POSITION = -2;
    /**
     * Last physical file position.
     */
    protected int m_iPhysicalFilePosition = UNKNOWN_POSITION;
    /**
     * Actual end of file +1 (-1 means don't know).
     */
    protected int m_iEndOfFileIndex = UNKNOWN_POSITION;
    /**
     * Last logical file position for update.
     */
    protected int m_iLogicalFilePosition = UNKNOWN_POSITION;
    /**
     * The grid buffer (cache of most recently accessed sequential records).
     */
    protected DataRecordBuffer m_gridBuffer = null;
    /**
     * All new records are added to the end (and cached in this sequential buffer).
     */
    protected DataRecordBuffer m_gridNew = null;
    /**
     * The grid list (snapshot of entire query).
     */
    protected DataRecordList m_gridList = null;
    /**
     * A place to set a temporary file behavior (to be removed later).
     */
    protected FileListener m_behCompare = null;     // Special listener to read from a sub-set of the grid's query
    /**
     * This flag is used to flag that a non-cached record had to be accessed, which I assume
     * messes up the current sequential query, requiring a new SELECT ... WHERE ID > x call.
     */
    protected boolean m_bIsSequentialQueryValid = false;
    /**
     * Cache ALL the record data? (Note: Currently this is always true... may want to fix this later).
     */
    protected boolean m_bCacheRecordData = true;
    /**
     * GridTable Constructor.
     */
    public GridTable()
    {
        super();
    }
    /**
     * GridTable Constructor.
     * @param database Should be null, as the last table on the chain contains the database.
     * @param record The record's current table will be changed to grid table and moved down my list.
     */
    public GridTable(BaseDatabase database, Record record)
    {
        this();
        this.init(database, record);
    }
    /**
     * Constructor.
     * @param database Should be null, as the last table on the chain contains the database.
     * @param record The record's current table will be changed to grid table and moved down my list.
     */
    public void init(BaseDatabase database, Record record)
    {
        super.init(database, record);

        m_gridList = new DataRecordList();      
        m_iEndOfFileIndex = UNKNOWN_POSITION; // Actual end of file (-1 means don't know)

        m_gridBuffer = new DataRecordBuffer();
        m_gridNew = new DataRecordBuffer();
        m_iPhysicalFilePosition = UNKNOWN_POSITION;
        m_iLogicalFilePosition = UNKNOWN_POSITION;

        if (((record.getOpenMode() & DBConstants.OPEN_READ_ONLY) != DBConstants.OPEN_READ_ONLY)
            && (record.getCounterField() != null))
                record.setOpenMode(record.getOpenMode() | DBConstants.OPEN_REFRESH_AND_LOCK_ON_CHANGE_STRATEGY);  // Must have for GridTable to re-read.
    }
    /**
     * Free.
     */
    public void free()
    {
        this.removeAll(); // Clear all the buffers

        super.free();
        
        if (m_gridList != null)
            m_gridList.free();
        m_gridList = null;
        if (m_gridBuffer != null)
            m_gridBuffer.free();
        m_gridBuffer = null;
        if (m_gridNew != null)
            m_gridNew.free();
        m_gridNew = null;
    }
    /**
     * Open this table (requery the table).
     * @exception DBException File exception.
     */
    public void open() throws DBException
    {
        super.open();
    }
    /**
     * Close this table.
     */
    public void close()
    {
        this.removeAll(); // Clear all the buffers
        super.close();
    }
    /**
     * Free the buffers in this grid table.
     */
    public void removeAll()
    {
        if (m_gridList != null)
            m_gridList.removeAll();
        if (m_gridBuffer != null)
            m_gridBuffer.removeAll();
        if (m_gridNew != null)
            m_gridNew.removeAll();
        m_iEndOfFileIndex = UNKNOWN_POSITION;
        m_iPhysicalFilePosition = UNKNOWN_POSITION;
        m_iLogicalFilePosition = UNKNOWN_POSITION;
    }
    /**
     * Cache records on read.
     * @param bCache Turn on or off.
     */
    public void setCache(boolean bCache)
    {
        m_bCacheRecordData = bCache;
    }
    /**
     * Add this record's unique info to the end of the recordset.
     * The current record is at this logical position, cache it.
     * @param iTargetPosition The position to add this record at.
     */
    public void addRecordReference(int iTargetPosition)
    {
        DataRecord bookmark = this.getNextTable().getDataRecord(m_bCacheRecordData, BaseBuffer.SELECTED_FIELDS);
        m_gridBuffer.addElement(iTargetPosition, bookmark, m_gridList);
    }
    /**
     * Search through the buffers for this bookmark.
     * @return int index in table; or -1 if not found.
     * @param bookmark java.lang.Object The bookmark to search for.
     * @param iHandleType The bookmark type.
     */
    public int bookmarkToIndex(Object bookmark, int iHandleType)
    {
        if (bookmark == null)
            return -1;
        int iTargetPosition = m_gridBuffer.bookmarkToIndex(bookmark, iHandleType);
        if (iTargetPosition == -1)
            iTargetPosition = m_gridList.bookmarkToIndex(bookmark, iHandleType);
        return iTargetPosition;   // Target position
    }
    /**
     * Search through the record buffer for this bookmark.
     * If you find it, refresh the data and return the index. (The data possibly changed)
     * @return int index in table; or -1 if not found.
     * @param bookmark java.lang.Object The new bookmark.
     * @param iHandleType The bookmark type.
     */
    public int refreshBookmark(Object bookmark, int iHandleType, boolean bReReadMessage)
    {
        if (bookmark == null)
            return -1;
        int iTargetPosition = m_gridBuffer.bookmarkToIndex(bookmark, iHandleType);
        if (iTargetPosition != -1)
        { // Refresh the data in the buffer.
            Object objElement = m_gridBuffer.elementAt(iTargetPosition);
            if (objElement instanceof DataRecord)
            {   // The only thing that I know for sure is the bookmark is correct.
                DataRecord dataRecord = (DataRecord)objElement;
                dataRecord.setBuffer(null);
                dataRecord.setHandle(null, DBConstants.BOOKMARK_HANDLE);
                dataRecord.setHandle(null, DBConstants.OBJECT_ID_HANDLE);
                dataRecord.setHandle(null, DBConstants.DATA_SOURCE_HANDLE);
                dataRecord.setHandle(null, DBConstants.OBJECT_SOURCE_HANDLE);
                dataRecord.setHandle(bookmark, iHandleType);
                if (bReReadMessage)
                {   // Re-read and refresh the cache.
                    try {
                        this.get(iTargetPosition);
                    } catch (DBException ex) {
                        ex.printStackTrace();
                    }
                }
                else
                {
                    BaseTable table = this;
                    while (true)
                    {       // Search down the table links for a cache table and then clear the entry.
                        if (table == null)
                            break;
                        if (DBParams.CLIENT.equals(table.getSourceType()))
                            break;
                        if ((table instanceof PassThruTable)
                            && (((PassThruTable)table).getNextTable() != table))
                                table = ((PassThruTable)table).getNextTable();
                        else if (table == table.getCurrentTable())
                            break;
                        else
                            table = table.getCurrentTable();
                    }
                    // Note: This works great unless there is a cached copy of the record, so invalidate remote cache copy.
                    if (DBParams.CLIENT.equals(table.getSourceType()))
                    {
                        org.jbundle.thin.base.db.client.CachedRemoteTable cacheTable = (org.jbundle.thin.base.db.client.CachedRemoteTable)table.getRemoteTableType(org.jbundle.thin.base.db.client.CachedRemoteTable.class);
                        if (cacheTable != null)
                            cacheTable.setCache(bookmark, null);    // Clear cache, so next read will do a physical read
                    }
                }
            }
        }
        return iTargetPosition;   // Return index
    }
    /**
     * Get the record bookmark at this location.
     * @param iTargetPosition The position to retrieve this bookmark from.
     * @return The bookmark as this position.
     */
    public Object elementAt(int iTargetPosition)
    {
        Record record = null;
        Object bookmark = null;
        if (m_iEndOfFileIndex != UNKNOWN_POSITION) if (iTargetPosition >= m_iEndOfFileIndex)
            return null;    // Past/At EOF
        if (!m_gridBuffer.inBufferList(iTargetPosition))
        {
            bookmark = m_gridList.elementAt(iTargetPosition);
            if (bookmark != null)
                return bookmark;    // In grid list, return it!
            // Find the next lower item in the list
            int iLowestNonNullIndex = this.lowestNonNullIndex(iTargetPosition);
            if (iLowestNonNullIndex != -1)
            {
                bookmark = m_gridList.elementAt(iLowestNonNullIndex);
                if (bookmark == null)
                    bookmark = m_gridBuffer.elementAt(iLowestNonNullIndex);
            }
            
            boolean bFindTargetUsingCurrentQuery = false;
            if (bookmark == null)
                bFindTargetUsingCurrentQuery = true;
            else
            {
                if (iTargetPosition == m_iPhysicalFilePosition + 1)
                    if (m_bIsSequentialQueryValid)
                        bFindTargetUsingCurrentQuery = true;
            }

            if (!bFindTargetUsingCurrentQuery)
            { // This record is not in the grid list, because the granularity is too great
                // Read the record before it, and move forward.
                // Move to > bookmark, and move next until you hit this target.
                this.getNextTable().setDataRecord((DataRecord)bookmark);
                record = this.getRecord();
                if (m_behCompare != null)
                    record.removeListener(m_behCompare, true);
                m_behCompare = new SubCurrentFilter(true, false);
                m_behCompare.setMasterSlaveFlag(RecordOwner.MASTER | RecordOwner.SLAVE);    // Should run in either space
                record.addListener(m_behCompare);
                this.getNextTable().close();
                this.getRecord().setOpenMode(this.getRecord().getOpenMode() | DBConstants.OPEN_SUPPRESS_MESSAGES);   // Don't send an "AFTER_REQUERY_TYPE" message
                try   {
                    m_iPhysicalFilePosition = iLowestNonNullIndex - 1;
                    m_bIsSequentialQueryValid = true;
                    record = (Record)this.move(+1);
                    m_iPhysicalFilePosition = iLowestNonNullIndex;
                } catch (DBException e)   {
                    record = null;
                } finally {
                    this.getRecord().setOpenMode(this.getRecord().getOpenMode() & ~DBConstants.OPEN_SUPPRESS_MESSAGES);
                }
                if (record == null) if (m_behCompare != null)
                {
                    this.getRecord().removeListener(m_behCompare, true);
                    m_behCompare = null;
                }
            }

            try   {
                int iCurrentPosition = m_iPhysicalFilePosition;
                if (iCurrentPosition == -1)
                {
                    for (iCurrentPosition = 0; ; iCurrentPosition++)
                    {       // Find the first non-deleted record
                        if ((!m_gridBuffer.inBufferList(iCurrentPosition))
                            || (m_gridBuffer.elementAt(iCurrentPosition) != null))
                                break;
                    }
                    record = (Record)this.move(DBConstants.FIRST_RECORD);
                    if (record != null)
                    {
                        if (iCurrentPosition != 0)
                        {
                            bookmark = m_gridList.elementAt(0);
                            if (bookmark == null)
                                bookmark = m_gridBuffer.elementAt(0);
                            m_gridBuffer.addElement(0, null, m_gridList);   // Set this as deleted again
                            m_gridBuffer.addElement(iCurrentPosition, bookmark, m_gridList);   // Set this in the right logical position
                            m_iPhysicalFilePosition = iCurrentPosition;
                        }
                    }
                    else
                    {
                        iTargetPosition = 0;    // Make sure current position = 0!
                        m_iEndOfFileIndex = 0;  // EOF + 1
                    }
                }
                int iMoveRelative = iTargetPosition - iCurrentPosition;
                if (iMoveRelative < 0)
                    record = (Record)this.move(iMoveRelative);
                else
                {
                    while (iMoveRelative > 0)
                    {
                        if ((m_gridBuffer.inBufferList(m_iPhysicalFilePosition + 1))
                            && (m_gridBuffer.elementAt(m_iPhysicalFilePosition + 1) == null))
                        {
                            m_iPhysicalFilePosition++;  // This record shows deleted, don't fill with a record.
                        }
                        else
                        {
                            record = (Record)this.move(+1);
                            if (record == null)
                                break;
                        }
                        iMoveRelative--;
                    }
                }
                m_bIsSequentialQueryValid = true; // The query is in force, you may use move(+1) next time (instead of starting a new query)
            } catch (DBException ex)    {
                ex.printStackTrace();
                record = null;
            }
            m_iPhysicalFilePosition = iTargetPosition;
            if (m_behCompare != null)
            {
                this.getRecord().removeListener(m_behCompare, true);
                m_behCompare = null;
            }
            if (record == null)
                return null;
        }
        if (m_gridBuffer.inBufferList(iTargetPosition))
            bookmark = m_gridBuffer.elementAt(iTargetPosition);
        else
            bookmark = m_gridList.elementAt(iTargetPosition);
        return bookmark;
    }
    /**
     * Search backwards from this position for the first non-null value.
     * @param iTargetPosition The position to retrieve this bookmark from.
     * @return index of non-null value or -1 if all lower values are null.
     */
    public int lowestNonNullIndex(int iTargetPosition)
    {
        Object bookmark = null;
        int iBookmarkIndex = iTargetPosition;
        while (bookmark == null)
        {
            int iArrayIndex = m_gridList.listToArrayIndex(iBookmarkIndex);
            iBookmarkIndex =  m_gridList.arrayToListIndex(iArrayIndex);   // Lowest valid bookmark
            bookmark = m_gridList.elementAt(iBookmarkIndex);
            if (bookmark == null)
            {
                if (iBookmarkIndex == 0)
                    return -1;  // None found
                iBookmarkIndex--;
                if (m_gridBuffer != null)
                    bookmark = m_gridBuffer.elementAt(iBookmarkIndex);  // Check to see if it's in this list
            }
        }
        return iBookmarkIndex;  // Valid bookmark, return it.
    }
    /**
     * Find this bookmark in one of the lists.
     * @return int index in table; or -1 if not found.
     * @param bookmark java.lang.Object The bookmark to look for.
     * @param iHandleType The type of bookmark to look for.
     */
    public int findElement(Object bookmark, int iHandleType)
    {
        if (bookmark == null)
            return -1;
        int iTargetPosition = m_gridBuffer.findElement(bookmark, iHandleType);
        if (iTargetPosition == -1)
            iTargetPosition = m_gridList.findElement(bookmark, iHandleType);
        return iTargetPosition;   // Not found
    }
    /**
     * Move the position of the record.
     * Note: move is called from other methods in this class to get a relative record.
     * @param iRelPosition - Relative position positive or negative or FIRST_RECORD/LAST_RECORD.
     * @return The fieldlist as the new position or null if eof/bof.
     * @exception FILE_NOT_OPEN.
     * @exception INVALID_RECORD - Record position is not current or move past EOF or BOF.
     */
    public FieldList move(int iRelPosition) throws DBException
    {
        m_iLogicalFilePosition = UNKNOWN_POSITION;
        FieldList record = this.getNextTable().move(iRelPosition);
        int iCurrentPosition = m_iPhysicalFilePosition;
        if (iRelPosition == DBConstants.FIRST_RECORD)
            iCurrentPosition = 0;
        else if (iRelPosition == DBConstants.LAST_RECORD)
        {
            if (m_iEndOfFileIndex == UNKNOWN_POSITION)
            {   // May want to do m_iEndOfFileIndex = this.getRecordCount() when/if supported
                iCurrentPosition = UNKNOWN_POSITION;    // -1 if not reached yet in a Next
            }
            else
                iCurrentPosition = m_iEndOfFileIndex - 1; // EOF
        }
        else
            iCurrentPosition += iRelPosition;
        m_iPhysicalFilePosition = iCurrentPosition;
        if ((record == null) && ((iRelPosition == DBConstants.FIRST_RECORD) || (iRelPosition == DBConstants.NEXT_RECORD)))  // EOF
            m_iEndOfFileIndex = iCurrentPosition; // EOF + 1
        else
        {
            if (iCurrentPosition != -1)
                this.addRecordReference(iCurrentPosition);
            m_iLogicalFilePosition = iCurrentPosition;      // Last accessed record #
        }
        return record;
    }
    /**
     * Move the position of the record to this record location.
     * Be careful, if a record at a row is deleted, this method will return a new
     * (empty) record, so you need to check the record status before updating it.
     * @param iRelPosition - Absolute position of the record to retrieve.
     * @return The record at this location (or null if not found).
     * @exception DBException File exception.
     */
    public Object get(int iPosition) throws DBException
    {
        m_iLogicalFilePosition = UNKNOWN_POSITION;
        DataRecord dataRecord = (DataRecord)this.elementAt(iPosition);
        if (dataRecord == null)
        {
            this.getNextTable().addNew();
            m_iPhysicalFilePosition = UNKNOWN_POSITION;
            if (m_iEndOfFileIndex != UNKNOWN_POSITION) if (iPosition >= m_iEndOfFileIndex)
                return null;    // Past/At EOF
            this.getCurrentTable().getRecord().setEditMode(DBConstants.EDIT_NONE);  // Tell caller this is deleted.
            return this.getCurrentTable().getRecord();  // Deleted record - return the new (empty) record
        }
        Object oldBookmark = this.getNextTable().getHandle(DBConstants.OBJECT_ID_HANDLE);
        boolean bUseCurrentRecord = false;
        if (dataRecord.getHandle(DBConstants.OBJECT_ID_HANDLE) != null)
            if (dataRecord.getHandle(DBConstants.OBJECT_ID_HANDLE).equals(oldBookmark))
                if (this.getCurrentTable().getRecord().getEditMode() == Constants.EDIT_CURRENT)
                    if (this.getCurrentTable().getRecord().isModified() == false)
                        bUseCurrentRecord = true;
        boolean bFound = true;
        if (!bUseCurrentRecord)
        {
            bFound = this.getNextTable().setDataRecord(dataRecord);
            if (dataRecord.getBuffer() == null)
            {
                m_bIsSequentialQueryValid = false;
                this.addRecordReference(iPosition);   // Re-cache data
            }
        }
        m_iLogicalFilePosition = iPosition;         // Valid record is at this row
        if (!bFound)
        {
            this.getNextTable().addNew();
            m_iPhysicalFilePosition = UNKNOWN_POSITION;
            if (m_iEndOfFileIndex != UNKNOWN_POSITION) if (iPosition >= m_iEndOfFileIndex)
                return null;    // Past/At EOF
            this.getCurrentTable().getRecord().setEditMode(DBConstants.EDIT_NONE);  // Tell caller this is deleted.
        }
        return this.getCurrentTable().getRecord();
    }
    /**
     * Update this record (Always called from the record class).
     * This called the set it override, but re-adjusts the bookmarks after the change.
     * @param fieldList The record to add.
     * @exception DBException File exception.
     */
    public void set(Rec fieldList) throws DBException
    {
        super.set(fieldList);  // See doRecordChange for the special grid behaviors
        m_iPhysicalFilePosition = UNKNOWN_POSITION;     // After a set, the physical position is unknown
    }
    /**
     * Reposition to this record Using this bookmark.
     * @param Object bookmark Bookmark.
     * @param int iHandleType Type of handle (see getHandle).
     * @exception FILE_NOT_OPEN.
     * @return record if found/null - record not found.
     */
    public FieldList setHandle(Object bookmark, int iHandleType) throws DBException
    {
        m_objectID = null;      // No current record
        m_dataSource = null;
        FieldList fieldList = this.getNextTable().setHandle(bookmark, iHandleType);
        if (fieldList != null)
            m_iRecordStatus = DBConstants.RECORD_NORMAL;
        else
            m_iRecordStatus = DBConstants.RECORD_INVALID | DBConstants.RECORD_AT_BOF | DBConstants.RECORD_AT_EOF;
        return fieldList;
    }
    /**
     * Get a unique object that can be used to reposition to this record.
     */
    public Object getHandle(int iHandleType) throws DBException   
    {
        return this.getNextTable().getHandle(iHandleType);
    }
    /**
     * Read the record that matches this record's current key.
     * For a GridTable, this method calls the inherited seek.
     * @return true if successful, false if not found.
     * @exception FILE_NOT_OPEN.
     * @exception KEY_NOT_FOUND - The key was not found on read.
     */
    public boolean seek(String strSeekSign) throws DBException
    {
        // NOTE: This extra logic deals with a very specific and obscure case:
        // If this GRID table is NOT currently being used for indexed access (ie., get(x))
        // You can do a seek and I will cache the record. On a subsequent seek, I will return the cached
        // record. This is typically used for thin client accessing cached virtual fields in a table session.
        boolean bAutonomousSeek = false;
        if (m_iEndOfFileIndex == UNKNOWN_POSITION)
            if ((m_iPhysicalFilePosition == UNKNOWN_POSITION) || (m_iPhysicalFilePosition == ADD_NEW_POSITION))
                if ((m_iLogicalFilePosition == UNKNOWN_POSITION) || (m_iLogicalFilePosition == ADD_NEW_POSITION))
                    bAutonomousSeek = true;
        if (bAutonomousSeek)
        {
            Object bookmark = this.getRecord().getHandle(DBConstants.BOOKMARK_HANDLE);
            int iPosition = this.findElement(bookmark, DBConstants.BOOKMARK_HANDLE);
            if (iPosition == 0)
            {
                DataRecord dataRecord = (DataRecord)this.elementAt(iPosition);
                return this.getNextTable().setDataRecord(dataRecord);    // Success always.
            }
        }
    //  Don't set m_iPhysicalFilePosition = UNKNOWN_POSITION, because tables use seek to do queries.
        boolean bSuccess = super.seek(strSeekSign);
        if (bAutonomousSeek)
        {
            if (bSuccess)
                this.addRecordReference(0);
        }
        return bSuccess;
    }
    /**
     * Delete this record (Always called from the record class).
     * For a gridtable remove does inherrited then clears the cache.
     * @exception DBException File exception.
     */
    public void remove() throws DBException
    {
        super.remove();
        if ((m_iLogicalFilePosition != UNKNOWN_POSITION) && (m_iLogicalFilePosition != ADD_NEW_POSITION))
            m_gridBuffer.addElement(m_iLogicalFilePosition, null, m_gridList);
    }
    /**
     * Create/Clear the current object (Always called from the record class).
     * For a gridtable, addnew makes sure this will refresh on change (so I can get a good bookmark on add).
     * @exception DBException File exception.
     */
    public void addNew() throws DBException
    {
        super.addNew();
        m_iLogicalFilePosition = ADD_NEW_POSITION;
        m_iPhysicalFilePosition = ADD_NEW_POSITION;
    }
    /**
     * Add this new record (Always called from the record class).
     * For a gridtable, I grab the bookmark and add it to the end of the buffer.
     * @param fieldList The record to add.
     * @exception DBException File exception.
     * @see addNewBookmark.
     */
    public void add(Rec fieldList) throws DBException
    {
        super.add(fieldList);  // See doRecordChange for the special grid behaviors
    }
    /**
     * Here is a bookmark for a brand new record, add it to the end of the list.
     * @param bookmark The bookmark (usually a DataRecord) of the record to add.
     * @param iHandleType The type of bookmark to add.
     * @return the index of the new entry.
     */
    public int addNewBookmark(Object bookmark, int iHandleType)
    {
        if (bookmark == null)
            return -1;
        if (iHandleType != DBConstants.DATA_SOURCE_HANDLE)
        {   // The only thing that I know for sure is the bookmark is correct.
            DataRecord dataRecord = new DataRecord(null);
            dataRecord.setHandle(bookmark, iHandleType);
            bookmark = dataRecord;
        }

        int iIndexAdded = m_iEndOfFileIndex;
        if (m_iEndOfFileIndex == -1)
        { // End of file has not been reached - add this record to the "Add" buffer.
                // Add code here
        }
        else
        {
            // Update the record cached at this location
            m_gridBuffer.addElement(m_iEndOfFileIndex, bookmark, m_gridList);
            m_iEndOfFileIndex++;
        }
        return iIndexAdded;
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
     * This is a little tricky from the GridTable. You arn't sure what record was physically accessed last,
     * so you have to make the record at this position in the grid current, before locking.
     */
    public int edit() throws DBException
    {
        if (m_iLogicalFilePosition == UNKNOWN_POSITION)
            throw new DatabaseException(DBConstants.INVALID_RECORD);    // Data source exception
        else if (m_iLogicalFilePosition == ADD_NEW_POSITION)
        {   // addNew - No extra processing (Usually locking a refeshed record)
        }
        else if (m_iLogicalFilePosition != m_iPhysicalFilePosition)
        { // Move to this physical location
            try   {
                DataRecord dataRecord = (DataRecord)this.elementAt(m_iLogicalFilePosition);
                m_iPhysicalFilePosition = m_iLogicalFilePosition;
                this.getNextTable().addNew();   // Make sure setHandle doesn't just use the current record!
                dataRecord.setBuffer(null);   // Make sure you don't just restore the cached copy
                boolean bFound = this.getNextTable().setDataRecord(dataRecord);
                if (bFound)
                    this.addRecordReference(m_iLogicalFilePosition);    // Update data record to current copy
                if (!bFound)
                    throw new DatabaseException(DBConstants.INVALID_RECORD);    // Data source exception
            } catch (DBException ex)    {
                ex.printStackTrace(); // Ignore exception?
            }
        }
        return super.edit();
    }
    /**
     * Get the main (base) record.
     * @return The record this grid table is manipulating.
     */
    public Record getRecord()
    {
        if (m_record instanceof Record)
            return (Record)m_record;    // Don't call super (see next method)
        else    // TODO This is not very good code. Fix the mismatch between Thick and Thin
            return this.getCurrentTable().getRecord();  //  This is usually called from thin code, so make sure you return the CURRENT record.
    }
    /**
     * doRecordChange Method.
     * If this is an update or add type, grab the bookmark.
     * @param field The field that is changing.
     * @param iChangeType The type of change.
     * @param bDisplayOption not used here.
     */
    protected DataRecord m_tempDataRecord = null;
    protected boolean m_bIsRefreshedRecord = false;
    public int doRecordChange(FieldInfo field, int iChangeType, boolean bDisplayOption)
    {
        int iErrorCode = super.doRecordChange(field, iChangeType, bDisplayOption);
        if (iErrorCode != DBConstants.NORMAL_RETURN)
            return iErrorCode;
     
        Record record = this.getNextTable().getRecord();
        if (iChangeType == DBConstants.ADD_TYPE)
            m_bIsRefreshedRecord = record.isRefreshedRecord();

        if (((iChangeType == DBConstants.UPDATE_TYPE) && (m_iLogicalFilePosition != UNKNOWN_POSITION) && (m_iLogicalFilePosition != ADD_NEW_POSITION))
            || ((iChangeType == DBConstants.ADD_TYPE) && (m_iLogicalFilePosition == ADD_NEW_POSITION)))
        {
            int iOldEditMode = record.getEditMode();
            record.setEditMode(DBConstants.EDIT_CURRENT);   // Fake it! Can't get the datarecord without this status
            m_tempDataRecord = this.getNextTable().getDataRecord(m_bCacheRecordData, BaseBuffer.SELECTED_FIELDS);
            record.setEditMode(iOldEditMode);
            if (m_tempDataRecord != null)
                m_tempDataRecord.setHandle(null, DBConstants.DATA_SOURCE_HANDLE);   // After the update the source will be unknown
        }
        
        if (iChangeType == DBConstants.AFTER_UPDATE_TYPE)
        {
            if ((m_iLogicalFilePosition != UNKNOWN_POSITION) && (m_iLogicalFilePosition != ADD_NEW_POSITION))
                m_gridBuffer.addElement(m_iLogicalFilePosition, m_tempDataRecord, m_gridList);
            
            m_tempDataRecord = null;    // Don't free it.. it may be in the grid model
        }

        if (iChangeType == DBConstants.AFTER_ADD_TYPE)
        {
            if (m_iLogicalFilePosition == ADD_NEW_POSITION)
            {
                if (m_tempDataRecord == null)
                {   // Get the last modified, then get the datarecord that went with it.
                    Object handle = this.getNextTable().getLastModified(DBConstants.DATA_SOURCE_HANDLE);
                    try {
                        this.getNextTable().setHandle(handle, DBConstants.DATA_SOURCE_HANDLE);
                    } catch (DBException e) {
                        e.printStackTrace();
                    }
                    m_tempDataRecord = this.getNextTable().getDataRecord(m_bCacheRecordData, BaseBuffer.SELECTED_FIELDS);
                }
                else if (m_tempDataRecord.getHandle(DBConstants.BOOKMARK_HANDLE) == null) // If you don't have the ID, you can't re-update this record!
                {       // Need the bookmark of the new record.
                    // This code is a little tricky... I do this to keep from pulling the entire record back from the server on a new record
                    Object handle = this.getNextTable().getLastModified(DBConstants.BOOKMARK_HANDLE);
                    m_tempDataRecord.setHandle(handle, DBConstants.BOOKMARK_HANDLE);
                    // Next, update the record to include the bookmark I got from the server.
                    this.getRecord().getKeyArea(DBConstants.MAIN_KEY_AREA).reverseBookmark(handle, DBConstants.FILE_KEY_AREA);    // Make sure the physical record has the bookmark in its fields
                    // Now, re-cache the record fields and re-set them in the datarecord.
                    BaseBuffer buffer = new VectorBuffer(null, BaseBuffer.SELECTED_FIELDS);
                    buffer.fieldsToBuffer(this.getRecord(), BaseBuffer.SELECTED_FIELDS);
                    m_tempDataRecord.setBuffer(buffer);
                }
            }

            if ((m_bIsRefreshedRecord) && ((record.getOpenMode() & DBConstants.OPEN_REFRESH_AND_LOCK_ON_CHANGE_STRATEGY) == DBConstants.OPEN_REFRESH_AND_LOCK_ON_CHANGE_STRATEGY))
                if (m_iEndOfFileIndex != UNKNOWN_POSITION)
                    m_iEndOfFileIndex--;    // The record has already been added, re-add the updated copy
            this.addNewBookmark(m_tempDataRecord, DBConstants.DATA_SOURCE_HANDLE);
            
            m_tempDataRecord = null;   // Don't free it.. it may be in the grid model
        }

        return iErrorCode;
    }
    /**
     * Use this record update notification to update this gridtable.
     * @param message A RecordMessage detailing an update to this gridtable.
     * @param bAddIfNotFound Add the record to the end if not found
     * @return The row that was updated, or -1 if none.
     */
    public int updateGridToMessage(BaseMessage message, boolean bReReadMessage, boolean bAddIfNotFound)
    {
        Record record = this.getRecord(); // Record changed
        int iHandleType = DBConstants.BOOKMARK_HANDLE;  // OBJECT_ID_HANDLE;
        if (this.getNextTable() instanceof org.jbundle.base.db.shared.MultiTable)
            iHandleType = DBConstants.FULL_OBJECT_HANDLE;
        Object bookmark = ((RecordMessageHeader)message.getMessageHeader()).getBookmark(iHandleType);
        int iRecordMessageType = ((RecordMessageHeader)message.getMessageHeader()).getRecordMessageType();
        // See if this record is currently displayed or buffered, if so, refresh and display.
        GridTable table = (GridTable)record.getTable();
        int iIndex = -1;
        if (iRecordMessageType == -1)
        {
            iIndex = table.bookmarkToIndex(bookmark, iHandleType);  // Find this bookmark in the table
        }
        else if (iRecordMessageType == DBConstants.CACHE_UPDATE_TYPE)
        {   // No need to update anything as this is just a notification that the cache has been updaed already.
            iIndex = table.bookmarkToIndex(bookmark, iHandleType);  // Find this bookmark in the table
        }
        else if (iRecordMessageType == DBConstants.SELECT_TYPE)
        {   // A secondary record was selected, update the secondary field to the new value.
            // Don't need to update the table for a select
        }
        else if (iRecordMessageType == DBConstants.AFTER_ADD_TYPE)
        {
            iIndex = table.refreshBookmark(bookmark, iHandleType, false);  // Double-check to see if it has already been added
            if (iIndex == -1)
            {
                if (bAddIfNotFound)
                {   // Note: It might be nice to check this filter in the message on the server, then I don't change to read and check it.
                    try {
                        record = record.setHandle(bookmark, iHandleType);
                        // Fake the handleCriteria to think this is a slave. NOTE: This is not cool in a multitasked environment.
                        int iDBMasterSlave = record.getTable().getCurrentTable().getDatabase().getMasterSlave();
                        record.getTable().getCurrentTable().getDatabase().setMasterSlave(RecordOwner.MASTER | RecordOwner.SLAVE);
                        boolean bMatch = record.handleRemoteCriteria(null, false, null);
                        record.getTable().getCurrentTable().getDatabase().setMasterSlave(iDBMasterSlave);
                        if (bMatch)
                        {
                            iHandleType = DBConstants.DATA_SOURCE_HANDLE;
                            bookmark = this.getDataRecord(m_bCacheRecordData, BaseBuffer.SELECTED_FIELDS);
                        }
                        else
                            bookmark = null;
                    } catch (DBException e) {
                        e.printStackTrace();
                    }
                    if (bookmark != null)
                        iIndex = table.addNewBookmark(bookmark, iHandleType);   // Add this new record to the table
                }
            }
        }
        else
        {
            iIndex = table.refreshBookmark(bookmark, iHandleType, bReReadMessage);  // Refresh the data for this bookmark
        }
        return iIndex;
    }
    /**
     * Get the row count.
     * @return The record count or -1 if unknown.
     */
    public int size()
    {
        return m_iEndOfFileIndex;
    }
}
