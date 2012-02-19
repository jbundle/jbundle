/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db.grid;

/**
 * GridList buffers the entire recordset.<p>
 *  Because it has a fixed size, the granularity increases as the maximum size is reached.
 */
public class GridList extends Object
{
    /**
     * After this size, the recordlist uses a buffering sceme to access records.
     */
    protected static final int MAX_RECORD_ARRAY_SIZE = 256;   //10;
    /**
     * Bookmarks of all records in table (granularity increases with size).
     */
    protected Object m_aRecords[] = null;
    /**
     * The granularity of the buffer.
     */
    protected int m_iRecordListStep = 1;
    /**
     * Last record +1 of the record list (records scanned).
     */
    protected int m_iRecordListEnd = 0;
    /**
     * Current virtual max size of the record list (records scanned).
     */
    protected int m_iRecordListMax = 0;

    /**
     * Constructor.
     */
    public GridList()
    {
        super();
        m_aRecords = null;
        m_iRecordListStep = 1;
        m_iRecordListEnd = 0;
        m_iRecordListMax = 0; // Current size of the record list (records scanned) (-1 - unknown)
    }
    /**
     * Add this record's unique info to this recordset (If it falls into this list).
     * @param iTargetPosition The position to add the element at.
     * @param bookmark The bookmark to add.
     */
    public void addElement(int iTargetPosition, Object bookmark)
    {
        if (iTargetPosition >= m_iRecordListMax)
            this.growAccessList(iTargetPosition);
        if (iTargetPosition >= m_iRecordListEnd)
            m_iRecordListEnd = iTargetPosition + 1;   // New logical end
        if (!this.validRecordIndex(iTargetPosition))
            return;     // This one doesn't belong in this list
    
        int iArrayIndex = this.listToArrayIndex(iTargetPosition);
        m_aRecords[iArrayIndex] = bookmark;
    }
    /**
     * Convert the physical location to the logicial file location.
     * @param iArrayIndex The physical array location to convert.
     * @return The Logical record position.
     */
    public int arrayToListIndex(int iArrayIndex)
    {
        return iArrayIndex * m_iRecordListStep;
    }
    /**
     * Get the record bookmark at this location.
     * @param iTargetPosition The logical position to retrieve.
     * @return The bookmark at this location.
     */
    public Object elementAt(int iTargetPosition)
    {
        int iArrayIndex;
        Object bookmark = null;
        if (!this.inRecordList(iTargetPosition))
            return null;    // Not here, you need to Move to < bookmark, and move next until you hit this one

        iArrayIndex = this.listToArrayIndex(iTargetPosition);
        bookmark = m_aRecords[iArrayIndex];
        return bookmark;
    }
    /**
     * Search through this list for this bookmark.
     * @return int index in table; or -1 if not found.
     * @param bookmark java.lang.Object The bookmark to search for.
     * @param iHandleType The type of bookmark passed.
     */
    public int findElement(Object bookmark, int iHandleType)
    {
        int iTargetPosition;
        Object thisBookmark = null;
        if (bookmark == null)
            return -1;
//+ Not found, look through the recordlist
        boolean bDataRecordFormat = false;
        for (iTargetPosition = 0; iTargetPosition < m_iRecordListEnd; iTargetPosition += m_iRecordListStep)
        {
            thisBookmark = this.elementAt(iTargetPosition);
            if (iTargetPosition == 0)
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
     * Free this list.
     */
    public void free()
    {
        this.removeAll(); // Clear all the buffers

        m_aRecords = null;
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
     * Increase the granularity of the access list.
     * @param iTargetPosition The virtual size of the list has to include this target position.
     */
    public void growAccessList(int iTargetPosition)
    {
        int iArrayIndex;
        if (m_aRecords == null)
        {
            m_aRecords = new Object[MAX_RECORD_ARRAY_SIZE];
            m_iRecordListStep = 1;
            m_iRecordListMax = MAX_RECORD_ARRAY_SIZE; // Last + 1
        }
        else
        {
        // First, free the odd items
            for (iArrayIndex = 1; iArrayIndex < MAX_RECORD_ARRAY_SIZE; iArrayIndex += 2)
            {   // Free the objects that are no longer used
                this.freeElement(m_aRecords[iArrayIndex]);
                m_aRecords[iArrayIndex] = null;
            }
        // Next, compact the even ones to the first half of the array
            int nSourceIndex, nDestIndex;
            for (nSourceIndex = 2, nDestIndex = 1; nSourceIndex < MAX_RECORD_ARRAY_SIZE; nSourceIndex += 2, nDestIndex++)
            {
                Object bookmark = m_aRecords[nSourceIndex];
                m_aRecords[nDestIndex] = bookmark;
                m_aRecords[nSourceIndex] = null;
            }
        // Now, reduce the size of the array and increase the step
            m_iRecordListMax = m_iRecordListMax * 2;
            m_iRecordListStep = m_iRecordListStep * 2;  // Grow the array by elimating the odd records
        }

        if (iTargetPosition >= m_iRecordListMax)
            this.growAccessList(iTargetPosition); // Still not big enough, do it again
    }
    /**
     * Is this record number in the current record list?
     * @param iTargetPosition The logical file position to search for.
     * @return true if this buffer is in this list.
     */
    public boolean inRecordList(int iTargetPosition)
    {
        return ((this.validRecordIndex(iTargetPosition)) & (this.validRecordList(iTargetPosition)));
    }
//*******************************************************************
// List Methods - These methods deal with the entire record list
//*******************************************************************

    /**
     * Convert this logical postion to the rounded physical array position.
     * @param iTargetPosition The position to convert.
     * @param The physical array location that is equal to or less than this position.
     */
    public int listToArrayIndex(int iTargetPosition)
    {
        return iTargetPosition / m_iRecordListStep;
    }
    /**
     * Clear out the two buffers.
     */
    public void removeAll()
    {
        if (m_aRecords != null)
        {
            for (int iArrayIndex = 0; iArrayIndex < MAX_RECORD_ARRAY_SIZE; iArrayIndex++)
            {   // Free the objects that are no longer used
                this.freeElement(m_aRecords[iArrayIndex]);
                m_aRecords[iArrayIndex] = null;
            }
            m_aRecords = null;
        }
        m_iRecordListStep = 1;
        m_iRecordListEnd = 0;
        m_iRecordListMax = 0; // Current size of the record list (records scanned) (-1 - unknown)
    }
    /**
     * Would this record go in the current list buffer?
     * @param iTargetPosition The logical position to check.
     * @return true if this buffer would go in the physical array.
     */
    public boolean validRecordIndex(int iTargetPosition)
    {
        return (iTargetPosition % m_iRecordListStep == 0);
    }
    /**
     * Is this position in the range of records scanned so far?
     * @param iTargetPosition The logical position to check.
     * @return true if this position is smaller than the current end of records size.
     */
    public boolean validRecordList(int iTargetPosition)
    {
        return (iTargetPosition < m_iRecordListEnd);
    }
}
