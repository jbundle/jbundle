package org.jbundle.base.db.grid;

/**
 * GridBuffer buffers a fixed group of objects.<p>
 *  This is similar to a Vector, but the initial index changes over time.
 *  Warning - Because this class does not know the object type, you are responsible for garbage collection.
 */
public class GridBuffer extends Object
{
    /**
     * Must be less than the ListSize.
     */
    protected static final int RECORD_BUFFER_SIZE = 64;   //3;
    /**
     * Bookmarks of last set of sequential records accessed.
     */
    protected Object m_aCurrentRecord[] = null;
    /**
     * A placeholder for an empty cell.
     */
    public static final Object gEmptyCell = new Object();
    /**
     * Current starting record.
     */
    protected int m_iCurrentRecordStart = 0;
    /**
     * Current ending record.
     */
    protected int m_iCurrentRecordEnd = 0;

    /**
     * This method was created by a SmartGuide.
     */
    public GridBuffer()
    {
        super();
        m_aCurrentRecord = new Object[RECORD_BUFFER_SIZE];  // Records
        m_iCurrentRecordStart = 0;      // The first
        m_iCurrentRecordEnd = 0;        // One after the last (Use array nomenclature)
        for (int i = m_iCurrentRecordStart; i < RECORD_BUFFER_SIZE; i++)
        {
            m_aCurrentRecord[i] = gEmptyCell;     // No record here
        }   
    }
    /**
     * Free all the objects in this buffer (same as the removeAll method).
     */
    public void free()
    {
        this.removeAll(); // Clear all the buffers

        m_aCurrentRecord = null;
    }
    /**
     * Add this record's unique info to current buffer.
     * @param iTargetPosition The position to add the bookmark at.
     * @param bookmark The bookmark to add.
     * @param gridList The gridlist to update with this bookmark.
     */
    public void addElement(int iTargetPosition, Object bookmark, GridList gridList)
    {
        int iArrayIndex = this.bufferToArrayIndex(iTargetPosition);
        if (!this.inBufferArray(iTargetPosition))
            iArrayIndex = this.newBufferStartsAt(iTargetPosition, gridList);    // Add code to adjust the buffer to a new location
        m_aCurrentRecord[iArrayIndex] = bookmark;
        if (!this.inBufferList(iTargetPosition))
            m_iCurrentRecordEnd = iTargetPosition + 1;  // Logical size is this large
    }
    /**
     * Convert the file position to the array location in the cache.
     * @param iTargetPosition The position translate to a physical location.
     * @return The physical location in the array.
     */
    public int bufferToArrayIndex(int iTargetPosition)
    {
        return iTargetPosition - m_iCurrentRecordStart;
    }
    /**
     * Get the record bookmark at this location.
     * @param iTargetPosition The position to retrieve the bookmark from.
     * @return The bookmark at this position (or null if not in the buffer).
     */
    public Object elementAt(int iTargetPosition)
    {
        Object bookmark = null;
        if (this.inBufferArray(iTargetPosition))
            bookmark = m_aCurrentRecord[this.bufferToArrayIndex(iTargetPosition)];
        if (bookmark == gEmptyCell)
            bookmark = null;
        return bookmark;
    }
    /**
     * This method does a sequential seqrch through the buffer looking for the bookmark.
     * @return int index in table; or -1 if not found.
     * @param bookmark java.lang.Object The bookmark to find.
     * @param iHandleType The handle type of the bookmark.
     */
    public int findElement(Object bookmark, int iHandleType)
    {
        int iTargetPosition;
        Object thisBookmark = null;
        if (bookmark == null)
            return -1;
        boolean bDataRecordFormat = false;
        for (iTargetPosition = m_iCurrentRecordStart; iTargetPosition < m_iCurrentRecordEnd; iTargetPosition++)
        {
            thisBookmark = this.elementAt(iTargetPosition);
            if (iTargetPosition == m_iCurrentRecordStart)
                if (thisBookmark instanceof DataRecord)
                    bDataRecordFormat = true;
            if (bookmark.equals(thisBookmark))
                return iTargetPosition;
            else if (bDataRecordFormat)
                if (bookmark.equals(((DataRecord)thisBookmark).getHandle(iHandleType)))
                    return iTargetPosition;
        }
//+ Still not found, do a binary search through the recordlist for a matching key
        return -1;  // Not found
    }
    /**
     * Free this object (override this method if using a known object type).
     * This method does not free the object, you need to override this method to check is the object can be freed (and free it).
     * @param bookmark java.lang.Object The object being removed from the buffer.
     */
    public void freeElement(Object bookmark)
    {
        return;
    }
    /**
     * Is this file position in the current physical buffer array?
     * @param iTargetPosition The logical position to check.
     * @return true if this position is in the current buffer array.
     */
    private boolean inBufferArray(int iTargetPosition)
    {
        return ((iTargetPosition >= m_iCurrentRecordStart) & (iTargetPosition < m_iCurrentRecordStart + RECORD_BUFFER_SIZE));
    }
    /**
     * Is this actual file position in the current cache?
     * @param iTargetPosition The logical position to check.
     * @return true if this position is in the current buffer list.
     */
    public boolean inBufferList(int iTargetPosition)
    {
        boolean bInBufferList = ((iTargetPosition >= m_iCurrentRecordStart) && (iTargetPosition < m_iCurrentRecordEnd));
        if (bInBufferList)
            if (m_aCurrentRecord[this.bufferToArrayIndex(iTargetPosition)] == gEmptyCell)
                bInBufferList = false;
        return bInBufferList;
    }
    /**
     * Move these to the access list before getting rid of them.
     * @param gridList The list to add these buffer items to.
     */
    public void moveBufferToAccessList(GridList gridList)
    {
        for (int index = m_iCurrentRecordStart; index < m_iCurrentRecordEnd; index++)
        {
            int iArrayIndex = this.bufferToArrayIndex(index);
            Object bookmark = m_aCurrentRecord[iArrayIndex];
            if (bookmark != gEmptyCell)
                gridList.addElement(index, bookmark);
        }
    }
    /**
     * Shift the current cache to the current records and setup a new buffer.
     * @param iTargetPosition The new target position.
     * @param gridList The gridList to update with buffer items before I free them.
     * @return The new physical position of this target position.
     */
    public int newBufferStartsAt(int iTargetPosition, GridList gridList)
    {
//**NOTE** This can be improved by moving any overlapping records in the old buffer to the new buffer
    // Discard the entire current buffer
        if (gridList != null)
            this.moveBufferToAccessList(gridList);
        for (int index = m_iCurrentRecordStart; index < m_iCurrentRecordEnd; index++)
        {
            int iArrayIndex = this.bufferToArrayIndex(index);
            Object bookmark = m_aCurrentRecord[iArrayIndex];
            m_aCurrentRecord[iArrayIndex] = gEmptyCell;
            if (!gridList.inRecordList(index))
                if ((bookmark != null) && (bookmark != gEmptyCell))
                    this.freeElement(bookmark);
        }
        m_iCurrentRecordStart = iTargetPosition;
        m_iCurrentRecordEnd = iTargetPosition;  // No records in buffer!
        return this.bufferToArrayIndex(iTargetPosition);
    }
    /**
     * Clear out the two buffers.
     */
    public void removeAll()
    {
        if (m_aCurrentRecord != null)
        {
            for (int index = m_iCurrentRecordStart; index < m_iCurrentRecordEnd; index++)
            {
                int iArrayIndex = this.bufferToArrayIndex(index);
                Object bookmark = m_aCurrentRecord[iArrayIndex];
                if ((bookmark != null) && (bookmark != gEmptyCell))
                    this.freeElement(bookmark);
                m_aCurrentRecord[iArrayIndex] = gEmptyCell;   // No record here
            }
        }
        m_iCurrentRecordStart = 0;      // The first
        m_iCurrentRecordEnd = 0;        // One after the last (Use array nomenclature)
    }
}
