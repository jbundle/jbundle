package org.jbundle.base.db.net;

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
import org.jbundle.base.util.DBParams;


/**
 * SerialTable - Exactly the same as a Memory table, but the
 * raw data is seralized on close and unseralized on open.
 *
 * @version 1.0.0
 *
 * @author Don Corley
 */
public class NetTable extends MemoryTable
{
    /**
     * Constructor (Don't call this one).
     */
    public NetTable()
    {
        super();
    }
    /**
     * Constructor.
     * @param database The database to add this table to.
     * @param record The record to connect to this table.
     */
    public NetTable(BaseDatabase database,Record record)
    {
        this();
        this.init(database, record);
    }
    /**
     * Return this table drive type for the getObjectSource call. (Override this method)
     * @return java.lang.String Return "JDBC".
     */
    public String getSourceType()
    {
        return DBParams.NET;
    }
}
