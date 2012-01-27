/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db;

/**
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import org.jbundle.base.model.DBConstants;
import org.jbundle.model.DBException;
import org.jbundle.model.db.Rec;
import org.jbundle.thin.base.db.FieldList;


/**
 * QueryTable - Special class for handling fake Querys.
 * A fake query is needed when a SQL engine is not available, or the underlying tables are
 * not in a SQL db. This table handles the joins, pass thrus, etc.
 */
public class QueryTable extends PassThruTable
{

    /**
     * QueryTable Constructor.
     */
    public QueryTable()
    {
        super();
    }
    /**
     * QueryTable Constructor.
     * @param database The database for this table.
     * @param record The queryRecord for this table.
     */
    public QueryTable(BaseDatabase database, Record record)
    {
        this();
        this.init(database, record);
    }
    /**
     * QueryTable Constructor.
     * @param database The database for this table.
     * @param record The queryRecord for this table.
     */
    public void init(BaseDatabase database, Record record)
    {
        super.init(database, record);
        if (((QueryRecord)record).getBaseRecord() != null)
            m_tableNext = ((QueryRecord)record).getBaseRecord().getTable();   // Pass most commands thru to the main record's table
    }
    /**
     * Move the position of the record.
     * @param iRelPosition The positions to move.
     * @return The record at this position.
     * @see moveTableQuery.
     * @exception DBException File exception.
     */
    public FieldList move(int iRelPosition) throws DBException
    {
        Record record = ((QueryRecord)m_record).moveTableQuery(iRelPosition);
        if (record != null)
            return (QueryRecord)m_record;
        else
            return null;
    }
    /**
     * Move the data source buffer to all the fields.
     * For a querytable, the override is skipped.
     * @return Always returns a NORMAL_RETURN.
     * @exception DBException File exception.
     */
    public int dataToFields(Rec record) throws DBException
    {
        return DBConstants.NORMAL_RETURN; // Can't do this
    }
    /**
     * Get the current table target.
     * This is usually used to keep track of the current table in Move(s).
     * (ie., If this is a table for the "Animal" record, m_CurrentRecord
     * could be an "Cat" table object).
     * Note: Be careful with this one... QueryTable should be the one to
     * accept move, etc.
     * Note: At one time, I coded this as (but it didn't work right):
     * <pre>
     * return ((QueryRecord)m_record).getBaseRecord().getTable();   // Current table
     * </pre>
     * @return This table (which is the current table).
     */
    public BaseTable getCurrentTable()
    {
        return this;
    }
    /**
     * Do the physical Open on this table (requery the table).
     * @exception DBException File exception.
     */
    public void open() throws DBException
    {
        if (this.isOpen())
            return;         // Ignore if already open
        this.getRecord().handleInitialKey();        // Set up the smaller key
        this.getRecord().handleEndKey();            // Set up the larger key
                // You can't process a QueryRecord using tables, use special table logic.
        ((QueryRecord)m_record).openTableQuery();
    }
}
