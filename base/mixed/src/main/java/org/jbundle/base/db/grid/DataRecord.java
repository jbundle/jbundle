package org.jbundle.base.db.grid;

import org.jbundle.base.util.DBConstants;
import org.jbundle.thin.base.db.buff.BaseBuffer;

/**
 * Data record - representation of a physical data record object.
 *  <p>This object is used to cache data for reconstruction and/or re-retrieval of this record.
 */
public class DataRecord extends Object
{
    /**
     * Bookmark is always the primary key of the record.
     */
    protected transient Object m_bookmark = null;
    /**
     * Info needed to retrieve this persistent object (key from source system).
     */
    protected transient Object m_objectID = null;
    /**
     * Reference to this object (ie., RMI client object).
     * Reference is usually used by a caching utility to quickly reference the object.
     */
    protected transient Object m_dataSource = null;
    /**
     * All info to retrieve the persistent object.
     * (ie., RMI Server, object type, key).
     * or Table Name, key.
     */
    protected transient Object m_objectSource = null;
    /**
     * BaseBuffer - Cache of the data in this record.
     */
    protected transient BaseBuffer m_buffer = null;
    
    /**
     * This method was created by a SmartGuide.
     */
    public DataRecord()
    {
        super();
    }
    /**
     * This method was created by a SmartGuide.
     */
    public DataRecord(Object obj)
    {
        this();
        this.init(obj);
    }
    /**
     * This method was created by a SmartGuide.
     */
    public void init(Object obj)
    {
        m_bookmark = null;
        m_objectID = null;
        m_dataSource = null;
        m_objectSource = null;
        m_buffer = null;
    }
    /**
     * Does this datarecord describe the same datarecord?
     * @param object The datarecord to compare.
     * @return boolean True if these DataRecords describe the same record.
     */
    public boolean equals(Object object)
    {
        if (super.equals(object))
            return true;
        if (!(object instanceof DataRecord))
            return false;
        if (this.getHandle(DBConstants.DATA_SOURCE_HANDLE) != null)
            if (this.getHandle(DBConstants.DATA_SOURCE_HANDLE).equals(((DataRecord)object).getHandle(DBConstants.DATA_SOURCE_HANDLE)))
                return true;
        if (this.getHandle(DBConstants.BOOKMARK_HANDLE) != null)
            if (this.getHandle(DBConstants.BOOKMARK_HANDLE).equals(((DataRecord)object).getHandle(DBConstants.BOOKMARK_HANDLE)))
                return true;
        return false;
    }
    /**
     * Free the resources in this data representation.<p>
     *  NOTE: Be careful: This method may be called twice by DataRecordBuffer and then DataRecordGrid.
     */
    public void free()
    {
        m_bookmark = null;
        m_objectID = null;
        m_dataSource = null;
        m_objectSource = null;
        if (m_buffer != null)
        {
            m_buffer.free();
            m_buffer = null;
        } 
    }
    /**
     * This method was created by a SmartGuide.
     * @return tour.db.BaseBuffer The field data buffer.
     */
    public BaseBuffer getBuffer()
    {
        return m_buffer;
    }
    /**
     * This method was created by a SmartGuide.
     * @param iHandleType The type of handle you want to retrieve.
     * @return java.lang.Object The handle.
     */
    public Object getHandle(int iHandleType)
    {
        switch(iHandleType)
        {
            default:
            case DBConstants.BOOKMARK_HANDLE:
                return m_bookmark;
            case DBConstants.OBJECT_ID_HANDLE:
            case DBConstants.FULL_OBJECT_HANDLE:    // Closest match (exact match on multitables)
                return m_objectID;
            case DBConstants.DATA_SOURCE_HANDLE:
                return m_dataSource;
            case DBConstants.OBJECT_SOURCE_HANDLE:
                return m_objectSource;
        } 
    }
    /**
     * This method was created by a SmartGuide.
     * @param buffer tour.db.BaseBuffer The field data buffer to set.
     */
    public void setBuffer(BaseBuffer buffer)
    {
        m_buffer = buffer;
    }
    /**
     * This method was created by a SmartGuide.
     * @param bookmark java.lang.Object The bookmark to set.
     * @param iHandleType The type of handle you want to retrieve.
     */
    public void setHandle(Object bookmark, int iHandleType)
    {
        switch(iHandleType)
        {
            default:
            case DBConstants.BOOKMARK_HANDLE:
                m_bookmark = bookmark;
                break;
            case DBConstants.OBJECT_ID_HANDLE:
            case DBConstants.FULL_OBJECT_HANDLE:    // Closest match (exact match on multitables)
                m_objectID = bookmark;
                break;
            case DBConstants.DATA_SOURCE_HANDLE:
                m_dataSource = bookmark;
                break;
            case DBConstants.OBJECT_SOURCE_HANDLE:
                m_objectSource = bookmark;
                break;
        } 
    }
}
