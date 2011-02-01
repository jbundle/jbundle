package org.jbundle.base.db.event;

/**
 * @(#)RemoveFromQueryRecordOnCloseHandler.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import org.jbundle.base.db.QueryRecord;
import org.jbundle.base.db.Record;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.ListenerOwner;
import org.jbundle.base.util.DBConstants;
import org.jbundle.thin.base.db.Converter;
import org.jbundle.thin.base.db.FieldInfo;


/**
 * RemoveFromQueryRecordOnCloseHandler - Remove this record from the parent query
 * when this record is freed.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class RemoveFromQueryRecordOnCloseHandler extends FileListener
{
    /**
     * The parent query record.
     */
    protected QueryRecord m_queryRecord = null;

    /**
     * Constructor.
     */
    public RemoveFromQueryRecordOnCloseHandler()
    {
        super();
    }
    /**
     * Constructor.
     * @param queryRecord The parent query record.
     */
    public RemoveFromQueryRecordOnCloseHandler(QueryRecord queryRecord)
    {
        this();
        this.init(null, queryRecord);
    }
    /**
     * Constructor.
     * @param record My owner (usually passed as null, and set on addListener in setOwner()).
     * @param queryRecord The parent query record.
     */
    public void init(Record record, QueryRecord queryRecord)
    {
        super.init(record);
        m_queryRecord = queryRecord;
    }
    /**
     * Set the field or file that owns this listener.
     * If owner is set to null, remove this record from the query.
     * @param owner My owner.
     */
    public void setOwner(ListenerOwner owner)
    {
        if (owner == null)
            this.removeIt();
        super.setOwner(owner);
    }
    /**
     * Free this listener and remove this record from the query.
     */
    public void free()
    {
        this.removeIt();
        super.free();
    }
    /**
     * Remove this record from the query record.
     */
    public void removeIt()
    {
        if (m_queryRecord != null)
            if (this.getOwner() != null)
                m_queryRecord.removeRecord(this.getOwner());
        m_queryRecord = null;
    }
}
