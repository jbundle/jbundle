package org.jbundle.base.db.grid;

/**
 * This is a GridList that containts DataRecords.
 * 
 */
public class DataRecordList extends GridList
{

    /**
     * DataRecordList constructor comment.
     */
    public DataRecordList()
    {
        super();
    }
    /**
     * This method was created by a SmartGuide.
     * @return int index in table; or -1 if not found.
     * @param bookmark java.lang.Object
     */
    public int bookmarkToIndex(Object bookmark, int iHandleType)
    {
        int iTargetPosition;
        DataRecord thisBookmark = null;
        if (bookmark == null)
            return -1;
//+ Not found, look through the recordlist
        for (iTargetPosition = 0; iTargetPosition < m_iRecordListEnd; iTargetPosition += m_iRecordListStep)
        {
            thisBookmark = (DataRecord)this.elementAt(iTargetPosition);
            if (bookmark.equals(thisBookmark.getHandle(iHandleType)))
                return iTargetPosition;
        }
//+ Still not found, do a binary search through the recordlist for a matching key
        return -1;  // Not found
    }
    /**
     * Free this object (override this method if using a known object type).
     * @param bookmark java.lang.Object
     */
    public void freeElement (Object bookmark)
    {
        if (bookmark != null)
            ((DataRecord)bookmark).free();
    }
}
