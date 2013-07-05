/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db.serial;

/**
 * @(#)SerialTable.java   1.16 95/12/14 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 *
 */
import org.jbundle.base.db.BaseDatabase;
import org.jbundle.base.db.Record;
import org.jbundle.base.db.memory.MemoryTable;

/**
 * SerialTable - Exactly the same as a Memory table, but the
 * raw data is seralized on close and unseralized on open.
 *
 * @version 1.0.0
 *
 * @author Don Corley
 */
public class SerialTable extends MemoryTable
{
    /**
     * Constructor (Don't call this one).
     */
    public SerialTable()
    {
        super();
    }
    /**
     * Constructor.
     * @param database The database to add this table to.
     * @param record The record to connect to this table.
     */
    public SerialTable(BaseDatabase database, Record record)
    {
        this();
        this.init(database, record);
    }
}
