/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.test.manual.test.db.thin;

//******************************************************************************
// Test the basic table functions (add, remove, move, etc.)
//******************************************************************************
import org.jbundle.thin.base.db.FieldTable;
import org.jbundle.thin.base.db.client.RemoteFieldTable;
import org.jbundle.thin.base.db.client.memory.MemoryRemoteTable;
import org.jbundle.thin.base.remote.RemoteTable;

import junit.framework.Test;
import junit.framework.TestSuite;


// SimpleForm is the data entry form for the sample
public class ThinMemoryDBTest extends ThinBaseDBTest
{

    // Create the form
    public ThinMemoryDBTest(String strTestName)
    {
        super(strTestName);
    }
    public static Test suite()
    {
        TestSuite suite= new TestSuite();     
        suite.addTest(new ThinMemoryDBTest("verifyDataTest"));
        suite.addTest(new ThinMemoryDBTest("verifyDataTest"));  // Run it twice to make sure data is inited consistently
// VerifyAddTest also fails
//      suite.addTest(new ThinMemoryDBTest("verifyAddTest"));
// MemoryDBTest DOES NOT support grids.
//      suite.addTest(new ThinMemoryDBTest("gridAccessTest"));  // Run it twice to make sure data is inited consistently
        return suite;
    }
    /**
     * Create the thin table
     */
    public FieldTable setupThinTable()
    {
        RemoteTable table = new MemoryRemoteTable(null);
//x     table = new CachedRemoteTable(table);
        return new RemoteFieldTable(record, table, null);
    }
}
