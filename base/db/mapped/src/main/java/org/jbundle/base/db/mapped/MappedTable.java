package org.jbundle.base.db.mapped;

/**
 * @(#)SerialTable.java   1.16 95/12/14 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 *
 */
import org.jbundle.base.db.BaseDatabase;
import org.jbundle.base.db.Record;
import org.jbundle.base.db.Memory.MemoryTable;

/**
 * MappedTable - Reads through another table and filters the data depending on the record.
 *
 * @version 1.0.0
 * @author Don Corley
 */
public class MappedTable extends MemoryTable
{
    /**
     * Constructor (Don't call this one).
     */
    public MappedTable()
    {
        super();
    }
    /**
     * Constructor.
     * @param database The database to add this table to.
     * @param record The record to connect to this table.
     */
    public MappedTable(BaseDatabase database,Record record)
    {
        this();
        this.init(database, record);
    }
}
