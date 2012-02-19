/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db.xml;

/**
 * @(#)XmlTable.java    1.16 95/12/14 Don Corley
 *
 * Copyright © 2012 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 *
 */
import org.jbundle.base.db.BaseDatabase;
import org.jbundle.base.db.Record;
import org.jbundle.base.db.memory.MemoryTable;

/**
 * XmlTable - Implement the Xml table.
 *  The Xml table is EXACTLY the same as the memory table, except when it
 *  closes, data is written to the XML file. When it opens, data is extracted from
 *  the XML file.
 *
 * @version 1.0.0
 * @author Don Corley
 */
public class XmlTable extends MemoryTable
{
    /**
     * Constructor (Don't call this one).
     */
    public XmlTable()
    {
        super();
    }
    /**
     * Constructor.
     * @param database The database to add this table to.
     * @param record The record to connect to this table.
     */
    public XmlTable(BaseDatabase database, Record record)
    {
        this();
        this.init(database, record);
    }
}
