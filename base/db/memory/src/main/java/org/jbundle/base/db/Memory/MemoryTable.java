package org.jbundle.base.db.Memory;

/**
 * @(#)MemoryTable.java   1.16 95/12/14 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 *
 */
import org.jbundle.base.db.BaseDatabase;
import org.jbundle.base.db.Record;
import org.jbundle.base.db.physical.PhysicalTable;
import org.jbundle.base.util.DBConstants;
import org.jbundle.base.util.DBParams;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.buff.BaseBuffer;
import org.jbundle.thin.base.db.buff.VectorBuffer;


/**
 * Implementation of a Memory Table.
 * A table which exists in RAM.
 *
 * @version 1.0.0
 *
 * @author Don Corley
 */
public class MemoryTable extends PhysicalTable
{
    /**
     * Constructor (Don't call this one).
     */
    public MemoryTable()
    {
        super();
    }
    /**
     * Constructor.
     * @param database The database to add this table to.
     * @param record The record to connect to this table.
     */
    public MemoryTable(BaseDatabase database,Record record)
    {
        this();
        this.init(database, record);
    }
    /**
     * Init this table.
     * Add this table to the database and hook this table to the record.
     * @param database The database to add this table to.
     * @param record The record to connect to this table.
     */
    public void init(BaseDatabase database, Record record)
    {
        super.init(database, record);

        if (database != null)
            if ((database.getDatabaseType() & Constants.REMOTE_MEMORY) == 0)
                this.setProperty(DBParams.SUPRESSREMOTEDBMESSAGES, DBConstants.TRUE);   // Since there is no remote copy
    }
    /**
     * Create a new output buffer.
     * @return BaseBuffer - The new (data) buffer.
     */
    public BaseBuffer getNewDataBuffer()
    {
        return new VectorBuffer(null);
    }
}
