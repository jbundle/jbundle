/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db.util.log;

/**
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import org.jbundle.base.db.BaseDatabase;
import org.jbundle.base.db.PassThruTable;
import org.jbundle.base.db.Record;
import org.jbundle.model.DBException;
import org.jbundle.model.db.Rec;
import org.jbundle.thin.base.db.FieldList;
import org.jbundle.thin.base.db.buff.BaseBuffer;
import org.jbundle.thin.base.db.buff.VectorBuffer;
import org.jbundle.thin.base.remote.proxy.ProxyConstants;


/**
 * LogTable - An Override of BaseTable that logs all changes.
 */
public class LogTable extends PassThruTable {
    

    /**
     * RecordList Constructor.
     */
    public LogTable()
    {
        super();
    }
    /**
     * RecordList Constructor.
     */
    public LogTable(BaseDatabase database, Record record)
    {
        this();
        this.init(database, record);
    }
    /**
     * 
     */
    public void free()
    {
        if (m_buffer != null)
            m_buffer.free();
        m_buffer = null;
        super.free();
    }
    /**
     * Close - Close the recordset.
     */
    public void close()
    {
        super.close();
    }
    /**
     * Delete - Delete this record (Always called from the record class).
     * @exception DBException File exception.
     */
    public void remove() throws DBException
    {
        super.remove();
        this.logTrx(this.getRecord(), ProxyConstants.REMOVE);
    }
    /**
     * Add this record (Always called from the record class).
     * @exception DBException File exception.
     */
    public void add(Rec fieldList) throws DBException
    {
        super.add(fieldList);
        this.logTrx((FieldList)fieldList, ProxyConstants.ADD);
    }
    /**
     * Update this record (Always called from the record class).
     * @exception DBException File exception.
     */
    public void set(Rec fieldList) throws DBException
    {
        super.set(fieldList);
        this.logTrx((FieldList)fieldList, ProxyConstants.SET);
    }
    /**
     * Log this transaction.
     * @param strTrxType The transaction type.
     */
    public void logTrx(FieldList record, String strTrxType)
    {
        BaseBuffer buffer = this.getBuffer();
        buffer.clearBuffer();
        buffer.addHeader(strTrxType);
        buffer.addHeader(record.getTableNames(false));
        buffer.addHeader(record.getCounterField().toString());
        if (ProxyConstants.REMOVE != strTrxType)
            buffer.fieldsToBuffer(record);
        Object objLogData = buffer.getPhysicalData();
        this.logTrx(objLogData);
    }
    /**
     * Log this transaction.
     * @param strLogData Data to log
     */
    public void logTrx(Object objLogData)
    {
        // Override this and log
    }
    /**
     * 
     * @return
     */
    public BaseBuffer getBuffer()
    {
        if (m_buffer == null)
            m_buffer = new VectorBuffer(null, BaseBuffer.PHYSICAL_FIELDS | BaseBuffer.MODIFIED_ONLY);
        return m_buffer;
    }
    protected BaseBuffer m_buffer = null;
}
