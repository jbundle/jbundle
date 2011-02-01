/*
 * HotelRateRequestOut.java
 *
 * Created on September 26, 2003, 12:41 AM
 */

package org.jbundle.thin.base.message;

import org.jbundle.model.DBException;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.FieldList;

/**
 * This is the base message for sending and receiving requests.
 * Data in this object are stored in the native java object type.
 * Data can either be extracted as the Raw object, an External object, or
 * a string:
 * Raw Object: Native java object.
 * External Object: Externally recognizable data (such as Hotel Name rather than HotelID).
 * String: External Object converted to ASCII (The conversion is specified in the DataDesc).
 * Typically, you override the rawToExternal and externlToRaw to do your conversion (none by default).
 * Also: XML: Typically External to String conversion (with tags) except for items such as dates.
 * @author  don
 */
public class MessageRecordDesc extends BaseMessageRecordDesc
{
    private static final long serialVersionUID = 1L;

    public static final String MESSAGE_RECORD_TYPE = "messageRecordType";
    public static final String ADD_RECORD = "Add";
    public static final String CHANGE_RECORD = "Change";
    public static final String DELETE_RECORD = "Delete";

    protected FieldList m_recTargetFieldList = null;

    /**
     * Creates a new instance of HotelRateRequestOut
     */
    public MessageRecordDesc()
    {
        super();
    }
    /**
      * Initialize new BaseTrxMessage.
     * @param objRawMessage The (optional) raw data of the message.
     */
    public MessageRecordDesc(MessageDataParent messageDataParent, String strKey)
    {
        this();
        this.init(messageDataParent, strKey);
    }
    /**
      * Initialize new BaseTrxMessage.
     * @param objRawMessage The (optional) raw data of the message.
     */
    public void init(MessageDataParent messageDataParent, String strKey)
    {
        super.init(messageDataParent, strKey);
    }
    /**
     * Move the correct fields from ALL the detail records to the map.
     * Typically, you override this and loop through the records in the table.
     * If this method is used, is must be overidden to move the correct fields.
     * @param record The record to get the data from.
     */
    public int handlePutRawRecordData(FieldList record)
    {
        int iErrorCode = Constants.NORMAL_RETURN;
        int iRecordCount = this.getRecordCount(record);
        
        FieldList recTargetRecord = this.setDataIndex(RESET_INDEX, record);   // Reset index if multiple
        
        for (int index = 1; index <= iRecordCount; index++)
        {
            FieldList recNext = this.setDataIndex(index, recTargetRecord);
            if (recNext == null)
                break;
            
            iErrorCode = super.handlePutRawRecordData(recNext);

            if (iErrorCode != Constants.NORMAL_RETURN)
                break;
        }
        this.setDataIndex(END_OF_NODES, recTargetRecord);
        return iErrorCode;
    }
    /**
     * Get the record count (or MAX is unknown)
     * @param record
     * @return
     */
    public int getRecordCount(FieldList record)
    {
        int iRecordCount = 1;
        if ((this.getNodeType() == MessageRecordDesc.NON_UNIQUE_NODE)
            && (!this.isSingleDetail(record)))
                iRecordCount = Integer.MAX_VALUE;   // Until EOF
        return iRecordCount;
    }
    /**
     * Position to this node in the tree.
     * @param iNodeIndex The node to position to.
     * @param record The record I am moving data to. If this is null, don't position/setup the data.
     * @return An error code.
     */
    public FieldList setDataIndex(int iNodeIndex, FieldList record)
    {
        if (record != null)
        {
            if (RESET_INDEX == iNodeIndex)
            {
                m_recTargetFieldList = record;
                if ((this.getNodeType() == MessageRecordDesc.NON_UNIQUE_NODE)
                    && (!this.isSingleDetail(record)))
                        record = this.createSubDataRecord(record);
            }
            else if (END_OF_NODES == iNodeIndex)
            {
                if (!this.isCurrentDataRecord(record))
                {
                    record = this.updateRecord(record, false);
                    record.free();
                }
                m_recTargetFieldList = null;
            }
            else
            {
                if (!this.isCurrentDataRecord(record))
                {
                    record = this.updateRecord(record, false);
                    if (record != null)
                    {
                        try
                        {
                            if (record.getTable().hasNext())
                            {
                                record = (FieldList)record.getTable().next();
                                if (record != null)
                                    if (m_recTargetFieldList != null)
                                        if (record.getTableNames(false).equalsIgnoreCase(m_recTargetFieldList.getTableNames(false)))
                                            if (record.getTable().getHandle(0).equals(m_recTargetFieldList.getTable().getHandle(0)))
                                                if (m_recTargetFieldList.isModified())
                                            {   // I just read the same record as the target, make sure I have the current copy
                                                super.setDataIndex(iNodeIndex, record); // Set the node index before returning
                                                return m_recTargetFieldList;
                                            }
                            }
                            else
                                record = null;  // EOF
                        } catch (DBException ex) {
                            ex.printStackTrace();
                            record = null;
                        }
                    }
                }
            }
        }

        return super.setDataIndex(iNodeIndex, record);
    }
    /**
     * Am I using the current record as the data record?
     * @return true if I am
     */
    public boolean isCurrentDataRecord(FieldList record)
    {
        return (m_recTargetFieldList == record);
    }
    /**
     * Create the sub-detail record.
     * @param record
     * @return
     */
    public FieldList createSubDataRecord(FieldList record)
    {
        return record;  // Override this to actually create a new record.
    }
    /**
     * Update this record if it has been changed
     * @param record
     * @return The record (or null if error)
     */
    public FieldList updateRecord(FieldList record, boolean bRefresh)
    {
        try {
            Object bookmark = null;
            if (record.getEditMode() == Constants.EDIT_IN_PROGRESS)
            {
                if (bRefresh)
                    bookmark = record.getTable().getHandle(0);
                record.getTable().set(record);
            }
            else if ((record.getEditMode() == Constants.EDIT_ADD) && (record.isModified()))
            {
                record.getTable().add(record);
                if (bRefresh)
                    bookmark = record.getTable().getLastModified(0);
            }
            if (bRefresh)
                if (bookmark != null)
                {
                    record.getTable().setHandle(bookmark, 0);
                    record.getTable().edit();
                }
        } catch (DBException ex) {
            ex.printStackTrace();
            if (record != null)
                if (record.getTask() != null)
                    record.getTask().setLastError(ex.getMessage());
            return null;
        }
        return record;
    }
    /**
     * Move the map values to the correct record fields.
     * @param record The record to move the data to.
     * @return The error code.
     */
    public int handleGetRawRecordData(FieldList record)
    {
        int iErrorCode = Constants.NORMAL_RETURN;
        record = this.setNodeIndex(RESET_INDEX, record);   // Being careful
        int iNodeCount = 1;
        if (this.getNodeType() == MessageRecordDesc.NON_UNIQUE_NODE)
            iNodeCount = this.getMessage().getNodeCount(this.getFullKey(null));
        for (int index = 1; index <= iNodeCount; index++)
        {
            if (this.getNodeType() == MessageRecordDesc.NON_UNIQUE_NODE)
                record = this.setNodeIndex(index, record);
            
            iErrorCode = super.handleGetRawRecordData(record);
            
            if (iErrorCode != Constants.NORMAL_RETURN)
                break;
        }
        if (this.getNodeType() == MessageRecordDesc.NON_UNIQUE_NODE)
            this.setNodeIndex(END_OF_NODES, record);
        return iErrorCode;
    }
    /**
     * Position to this node in the tree.
     * @param iNodeIndex The node to position to.
     * @param record The record I am moving data to. If this is null, don't position/setup the data.
     * @return An error code.
     */
    public FieldList setNodeIndex(int iNodeIndex, FieldList record)
    {
        record = super.setNodeIndex(iNodeIndex, record);
        
        if (record == null)
            return null;
        
        if (RESET_INDEX == iNodeIndex)
        {
            m_recTargetFieldList = record;
            if (this.getNodeType() == MessageRecordDesc.NON_UNIQUE_NODE)
                record = this.createSubNodeRecord(record);
        }
        else if (END_OF_NODES == iNodeIndex)
        {
            if (!this.isCurrentNodeRecord(record))
            {
                record = this.updateRecord(record, true);
                this.freeSubNodeRecord(record);
            }
            m_recTargetFieldList = null;
        }
        else
        {
            if (!this.isCurrentNodeRecord(record))
            {
                record = this.updateRecord(record, false);
                record = this.readCurrentRecord(record);
            }
        }
        return record;
    }
    /**
     * Create the sub-detail record.
     * @param record
     * @return
     */
    public FieldList createSubNodeRecord(FieldList record)
    {
        return this.createSubDataRecord(record);    // By default these are the same
    }
    /**
     * Am I using the current record as the data record?
     * @return true if I am
     */
    public boolean isCurrentNodeRecord(FieldList record)
    {
        return this.isCurrentDataRecord(record);
    }
    /**
     * Create the sub-detail record.
     * @param record
     * @return
     */
    public boolean freeSubNodeRecord(FieldList record)
    {
        m_recTargetFieldList = null;
        if (record != null)
        {
            record.free();
            return true;
        }
        return false;
    }
    /**
     * Read the record described at the current data location.
     * @param record
     * @return null if error, otherwise return the record
     */
    public FieldList readCurrentRecord(FieldList record)
    {
        return record;  // Override this to do something
    }
}
