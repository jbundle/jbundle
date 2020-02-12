/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.test.manual.test.db.fullthin;

//******************************************************************************
// Test the basic table functions (add, remove, move, etc.)
//******************************************************************************
import org.jbundle.thin.app.test.test.db.TestTableNoAuto;
import org.jbundle.thin.base.db.FieldTable;
import org.jbundle.thin.base.db.mem.GridMemoryFieldTable;
import org.jbundle.thin.base.db.mem.base.PDatabase;
import org.jbundle.thin.base.db.mem.base.PTable;
import org.jbundle.thin.base.db.mem.base.PhysicalDatabaseParent;
import org.jbundle.thin.base.db.mem.memory.MDatabase;
import org.jbundle.thin.base.db.model.ThinPhysicalDatabase;

import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * BaseTest is the standard code to set for all the other tests.
 * @author  don
 * @version 
 */

public class MemoryDatabaseTest extends DatabaseTest
{

    /**
      *Creates new TestAll
      */
    public MemoryDatabaseTest(String strTestName) {
        super(strTestName);
    }
    
    public static Test suite()
    {
        TestSuite suite= new TestSuite();     
        suite.addTest(new MemoryDatabaseTest("testDatabase"));
        suite.addTest(new MemoryDatabaseTest("testDatabase"));  // Run it twice
        suite.addTest(new MemoryDatabaseTest("testGridAccess"));
        return suite;
    }
    /**
     * Setup.
     */
    public void setUp()
    {
        record = new TestTableNoAuto(this);
        super.setUp();
    }
    protected PhysicalDatabaseParent dbOwner = null;
    /**
     * Create the thin table.
     */
    public FieldTable setupThinTable()
    {
        if (dbOwner == null)
            dbOwner = new PhysicalDatabaseParent(null);
        PDatabase db = dbOwner.getPDatabase(record.getDatabaseName(), ThinPhysicalDatabase.MEMORY_TYPE, false);
        if (db == null)
            db = new MDatabase(dbOwner, record.getDatabaseName());
        PTable table = db.getPTable(record, true);
        return new GridMemoryFieldTable(record, table, null);
    }
}
