package org.jbundle.base.message.opt;

import org.jbundle.base.db.GridTable;
import org.jbundle.base.db.Record;
import org.jbundle.base.db.RecordOwner;
import org.jbundle.base.db.grid.DataRecord;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.message.record.BaseRecordMessageListener;
import org.jbundle.base.message.record.RecordMessage;
import org.jbundle.base.message.record.RecordMessageHeader;
import org.jbundle.base.util.DBConstants;
import org.jbundle.base.util.DBParams;
import org.jbundle.model.DBException;
import org.jbundle.thin.base.db.Converter;
import org.jbundle.thin.base.message.BaseMessage;
import org.jbundle.thin.base.message.BaseMessageFilter;
import org.jbundle.thin.base.message.BaseMessageReceiver;
import org.jbundle.thin.base.util.Application;


/**
 * Listen and respond to record update messages.
 */
public class AutoRecordMessageListener extends BaseRecordMessageListener
{
    /**
     *
     */
    protected boolean m_bUpdateRecord = true;
    /**
     *
     */
    public static final String MULTIPLE_FIELDS = "multipleFields";
    /**
     *
     */
    public static final String COMPARE_FIELD_NAME = "compareFieldName";
    /**
     *
     */
    public static final String COMPARE_FIELD_VALUE = "compareFieldValue";

    /**
     * Constructor.
     */
    public AutoRecordMessageListener()
    {
        super();
    }
    /**
     * Constructor.
     * @param bUpdateRecord If true, Record is an independent record which should be read and updated on changes.
     */
    public AutoRecordMessageListener(BaseMessageReceiver messageHandler, Record record, boolean bUpdateRecord, BaseMessageFilter messageFilter)
    {
        this();
        this.init(messageHandler, record, bUpdateRecord, messageFilter);
    }
    /**
     * Constructor.
     * @param bUpdateRecord If true, Record is an independent record which should be read and updated on changes.
     */
    public AutoRecordMessageListener(BaseMessageReceiver messageHandler, Record record, boolean bUpdateRecord)
    {
        this();
        this.init(messageHandler, record, bUpdateRecord, null);
    }
    /**
     * Constructor.
     */
    public void init(BaseMessageReceiver messageHandler, Record record, boolean bUpdateRecord, BaseMessageFilter messageFilter)
    {
        super.init(messageHandler, record, messageFilter);   // This will remove this listener on close.
        m_bUpdateRecord =  bUpdateRecord;
    }
    /**
     * Constructor.
     */
    public void free()
    {
        super.free();
    }
    /**
     * Receive the message.
     * Be very careful of multithreading issues,
     * as this will be running in an independent thread.
     */
    public int handleMessage(BaseMessage message)
    {
        if (m_record != null)
        {
            Object bookmark = message.get(DBConstants.STRING_OBJECT_ID_HANDLE);
            if (bookmark instanceof String)
                bookmark = Integer.parseInt((String)bookmark);  // Must be an Integer to compare correctly
            try   {
                if (m_bUpdateRecord)
                {   // Update an independent record
                    if (bookmark != null)
                    {   // Must have a bookmark
                        Record record = m_record.setHandle(bookmark, DBConstants.BOOKMARK_HANDLE);
                        if (record != null)
                        {   // No need to synchronize
                            if (record.edit() == DBConstants.NORMAL_RETURN)
                            {
                                this.updateFields(record, message, true);
                                record.set(); // This also propogates an updateRecord call.
                            }
                        }
                    }
                }
                else
                {   // Update the record if it is still on the screen
                    if (bookmark != null)
                    {
                        if (m_record.getTable() instanceof GridTable)
                            return this.processGridChange(message);
                        Object objectID = m_record.getHandle(DBConstants.BOOKMARK_HANDLE);
                        if (objectID != null)
                            if (bookmark.equals(objectID))
                        {
                            synchronized (m_record)
                            {   // Don't allow user to do anything while I make these changes
                                this.updateFields(m_record, message, true);
                            }
                        }
                    }
                    else
                    { // There isn't a current record on the screen - see if they wanted to update fields
                        String strFieldName = (String)message.get(COMPARE_FIELD_NAME);
                        String strFieldValue = (String)message.get(COMPARE_FIELD_VALUE);
                        if (strFieldName != null)
                        {
                            BaseField field = m_record.getField(strFieldName);
                            if (field != null) if (strFieldValue != null)
                                if (strFieldValue.equals(field.getString()))
                            {
                                synchronized (m_record)
                                {   // Don't allow user to do anything while I make these changes
                                    this.updateFields(m_record, message, true);
                                }
                            }
                        }
                    }
                }
            } catch (DBException ex)    {
                ex.printStackTrace();
            }
        }
        return DBConstants.NORMAL_RETURN;
    }
    /**
     * Receive the message.
     * Be very careful of multithreading issues,
     * as this will be running in an independent thread.
     */
    public int processGridChange(BaseMessage message)
    {
        Object bookmark = message.get(DBConstants.STRING_OBJECT_ID_HANDLE);
        if (bookmark instanceof String)
            bookmark = Integer.parseInt((String)bookmark);  // Must be an Integer to compare correctly
        GridTable gridTable = (GridTable)m_record.getTable();
        int iIndex = gridTable.findElement(bookmark, DBConstants.BOOKMARK_HANDLE);
        if (iIndex != -1)
        {
            DataRecord dataRecord = (DataRecord)gridTable.elementAt(iIndex);
            try   {
                Record record = (Record)m_record.clone();
                for (int i = 0; i < m_record.getFieldCount(); i++)
                {
                    record.getField(i).setSelected(m_record.getField(i).isSelected());
                }
                dataRecord.getBuffer().bufferToFields(record, DBConstants.DONT_DISPLAY, DBConstants.INIT_MOVE);
                boolean rgbModified[] = null;
                synchronized (m_record)
                {
                    try {
                        Object objectID = m_record.getHandle(DBConstants.BOOKMARK_HANDLE);
                        if (bookmark != null)
                            if (bookmark.equals(objectID))
                        {
                            {   // Don't allow user to do anything while I check these changes
                                rgbModified = m_record.getModified();
                            }
                        }
                    } catch (DBException ex) {
                        ex.printStackTrace();   // Never
                    }
                }
                for (int i = 0; i < m_record.getFieldCount(); i++)
                {
                    boolean bModified = false;
                    if (rgbModified != null)
                        bModified = rgbModified[i];
                    record.getField(i).setModified(bModified);
                }
                this.updateFields(record, message, true);
                dataRecord.getBuffer().fieldsToBuffer(record);
                record.free();
                RecordOwner recordOwner = m_record.getRecordOwner();
                if (recordOwner != null)
                {
                    Object source = recordOwner;
                    int iRecordMessageType = DBConstants.CACHE_UPDATE_TYPE;
                    RecordMessageHeader messageHeader = new RecordMessageHeader(m_record, bookmark, source, iRecordMessageType, null);
                    BaseMessage messageNew = new RecordMessage(messageHeader);
                    ((Application)recordOwner.getTask().getApplication()).getMessageManager().sendMessage(messageNew);
                }
            } catch (CloneNotSupportedException ex)   {
                ex.printStackTrace();
            }
        }
        return DBConstants.NORMAL_RETURN;
    }
    /**
     * Update the fields in this record using this property object.
     */
    public void updateFields(Record record, BaseMessage message, boolean bUpdateOnlyIfFieldNotModified)
    {
        String strField = (String)message.get(DBParams.FIELD);
        if (MULTIPLE_FIELDS.equalsIgnoreCase(strField))
        { // Process multiple fields
            for (int iFieldSeq = 0; iFieldSeq < record.getFieldCount(); iFieldSeq++)
            {
                BaseField field = record.getField(iFieldSeq);
                Object objFieldNameValue = message.get(field.getFieldName());
                if (objFieldNameValue != null)
                    if ((!bUpdateOnlyIfFieldNotModified) || (!field.isModified()))
                    {
                        if (objFieldNameValue instanceof String)
                            field.setString((String)objFieldNameValue);
                        else
                        {
                            try {
                                objFieldNameValue = Converter.convertObjectToDatatype(objFieldNameValue, field.getDataClass(), null);
                            } catch (Exception ex) {
                                objFieldNameValue = null;
                            }
                            field.setData(objFieldNameValue);
                        }
                    }
            }
        }
        else
        {
            String strValue = (String)message.get(DBParams.VALUE);
            BaseField field = record.getField(strField);
            if (field != null)
                if ((!bUpdateOnlyIfFieldNotModified) || (!field.isModified()))
                    field.setString(strValue);
        }
    }
}
