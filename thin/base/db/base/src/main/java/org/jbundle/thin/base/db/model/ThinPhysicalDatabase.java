/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.db.model;

import org.jbundle.thin.base.db.FieldList;

/**
 * 
 * @author don
 *
 */
public interface ThinPhysicalDatabase {

    public static final char MEMORY_TYPE = 'M';
    public static final char NET_TYPE = 'N';
    public static final char PROXY_TYPE = 'Y';
    public static final char SERIAL_TYPE = 'S';
    public static final char XML_TYPE = 'X';
    public static final char MAPPED_TYPE = 'Z';
    public static final char PHYSICAL_TYPE = 'P';   // Never

    /**
     * Get the db name.
     * The db name is my key if a Pdatabase owner is specified.
     * @return The database name.
     */
    public String getDatabaseName();
    /**
     * Get the physical table that matches this BaseTable and create it if it doesn't exist.
     * Note: If the bCreateIfNotFound flag was set, create the new table or bump the use count.
     * @param record The table to create a raw data table from.
     * @return The raw data table (creates a new one if it doesn't already exist).
     */
    public ThinPhysicalTable getPTable(FieldList record, boolean bCreateIfNotFound, boolean  bEnableGridAccess);
}
