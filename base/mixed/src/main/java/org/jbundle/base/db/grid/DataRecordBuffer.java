/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db.grid;

/**
 * This is a GridBuffer that containts DataRecords.
 * 
 */
public class DataRecordBuffer extends GridBuffer
{

    /**
     * DataRecordBuffer constructor comment.
     */
    public DataRecordBuffer()
    {
        super();
    }
    /**
     * This method was created by a SmartGuide.
     * @return int index in table; or -1 if not found.
     * @param bookmark java.lang.Object The bookmark to find in this table.
     * @param iHandleType int The type of handle to look for (typically OBJECT_ID).
     */
    public int bookmarkToIndex(Object bookmark, int iHandleType)
    {
        DataRecord thisBookmark = null;
        if (bookmark == null)
            return -1;
        for (int iTargetPosition = m_iCurrentRecordStart; iTargetPosition < m_iCurrentRecordEnd; iTargetPosition++)
        {
            thisBookmark = (DataRecord)this.elementAt(iTargetPosition);
            if (thisBookmark != null)
                if (bookmark.equals(thisBookmark.getHandle(iHandleType)))
                    return iTargetPosition;
        }
//+ Still not found, do a binary search through the recordlist for a matching key
        return -1;  // Not found
    }
    /**
     * Free this object (override this method if using a known object type).
     * @param bookmark java.lang.Object The object to free if it is a DataRecord.
     */
    public void freeElement(Object bookmark)
    {
        if (bookmark != null)
            ((DataRecord)bookmark).free();
    }
}
