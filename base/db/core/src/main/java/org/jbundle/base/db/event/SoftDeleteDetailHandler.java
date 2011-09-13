/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db.event;

/**
 * @(#)NoDeleteHandler.java 0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import org.jbundle.base.db.Record;
import org.jbundle.base.db.RecordOwner;
import org.jbundle.base.db.filter.SubFileFilter;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.util.Utility;
import org.jbundle.model.DBException;


/**
 * Flag this record as deleted instead of physically deleting it.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class SoftDeleteDetailHandler extends SoftDeleteHandler
{
    protected Record m_recDetail = null;
    
    /**
     * Constructor.
     */
    public SoftDeleteDetailHandler()
    {
        super();
    }
    /**
     * Constructor.
     * @param record The sub-record to check.
     */
    public SoftDeleteDetailHandler(BaseField fldDeleteFlag, Record recDetail)
    {
        this();
        this.init(null, fldDeleteFlag, recDetail);
    }
    /**
     * Constructor.
     * @param record My owner (usually passed as null, and set on addListener in setOwner()).
     */
    public void init(BaseField fldDeleteFlag, Record recDetail)
    {
        this.init(null, fldDeleteFlag, recDetail);
    }
    /**
     * Constructor.
     * @param record My owner (usually passed as null, and set on addListener in setOwner()).
     */
    public void init(Record record, BaseField fldDeleteFlag, Record recDetail)
    {
        m_fldDeleteFlag = fldDeleteFlag;
        m_recDetail = recDetail;
        super.init(record);
    }
    /**
     * Free this listener.
     */
    public void free()
    {
        m_fldDeleteFlag = null;
        m_recDetail = null;
        
        super.free();
    }
    /**
     * Get the detail record.
     * This gives you a chance of overriding this method and supplying the detail record, since
     * record deletes are done pretty infrequently and having to open this file every time
     * the header record is opened is a ton of overhead.
     */
    public Record getDetailRecord(RecordOwner screen)
    {
        return m_recDetail;
    }
    /**
     * Set the detail record.
     */
    public void setDetailRecord(Record record)
    {
        m_recDetail = record;
    }
    /**
     * Soft delete this record?
     * Override this to decide whether to soft delete or physically delete the record.
     * Soft delete if there are any detail record, otherwise hard delete this record.
     */
    public boolean isSoftDeleteThisRecord()
    {
        Record recDetailOld = m_recDetail;
        Record recDetail = this.getDetailRecord(Utility.getRecordOwner(this.getOwner()));
        if (m_recDetail != null)
            if (recDetailOld != m_recDetail)
            {
                recDetail.getRecordOwner().removeRecord(recDetail);
                this.getOwner().addListener(new FreeOnFreeHandler(recDetail));
            }
        if (recDetail != null)
        {
            if (recDetail.getListener(SubFileFilter.class) == null)
            {
                recDetail.addListener(new SubFileFilter(this.getOwner()));
            }
            try {
                recDetail.close();
                if (!recDetail.hasNext())
                    return false;   // No detail records = hard delete the record.
            } catch (DBException ex) {
                ex.printStackTrace();
            }
        }
        return super.isSoftDeleteThisRecord();
    }
}
