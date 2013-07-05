/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db.shared;

/**
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 *
 */
import java.util.Map;

import org.jbundle.base.db.BaseDatabase;
import org.jbundle.base.db.BaseTable;
import org.jbundle.base.db.Record;
import org.jbundle.model.db.DatabaseOwner;


/**
 * Access multiple tables as if they were overrides of the base record's table.
 * This is typically used to access multiple tables from the same overriding class,
 * such as a Dog and Cat class from an Animal class.
 */
public class FakeDatabase extends BaseDatabase
{
    /**
     * Constructor (Don't call this one).
     */
    public FakeDatabase()
    {
        super();
    }
    /**
     * Constructor.
     * @param databaseOwner My databaseOwner.
     * @param strDBName The database name.
     * @param iDatabaseType The database type (LOCAL/REMOTE).
     */
    public FakeDatabase(DatabaseOwner databaseOwner, String strDbName, int iDatabaseType)
    {
        this();
        this.init(databaseOwner, strDbName, iDatabaseType, null);
    }
    /**
     * Init this database and add it to the databaseOwner.
     * @param databaseOwner My databaseOwner.
     * @param iDatabaseType The database type (LOCAL/REMOTE).
     * @param strDBName The database name.
     */
    public void init(DatabaseOwner databaseOwner, String strDbName, int iDatabaseType, Map<String, Object> properties)
    {
        super.init(databaseOwner, strDbName, iDatabaseType, properties);
    }
    /**
     * Free this database object.
     */
    public void free()
    {
        super.free();
    }
    /**
     * You must override this to make a table for this database.
     * Always override this method.
     * @param record The record to make a table for.
     * @return BaseTable The new table.
     */
    public BaseTable doMakeTable(Record record)
    {
        return new FakeTable(this, record);
    }
    /**
     * Get the Database Name.
     * @param bPhysicalName Return the full physical name of the database
     * @return The db name.
     */
    public String getDatabaseName(boolean bPhysicalName)
    {
        return "Fake";
    }
}
