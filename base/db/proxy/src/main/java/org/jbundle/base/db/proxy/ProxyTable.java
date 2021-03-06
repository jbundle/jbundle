/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db.proxy;

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
import org.jbundle.base.model.DBParams;

/**
 * ProxyTable - Exactly the same as a Net table, but the
 * raw data is sent through the proxy instead of direct http.
 *
 * @version 1.0.0
 *
 * @author Don Corley
 */
public class ProxyTable extends MemoryTable
{
    /**
     * Constructor (Don't call this one).
     */
    public ProxyTable()
    {
        super();
    }
    /**
     * Constructor.
     * @param database The database to add this table to.
     * @param record The record to connect to this table.
     */
    public ProxyTable(BaseDatabase database,Record record)
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
        return DBParams.PROXY;
    }
}
