/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.test.suite.db.mongodb;

//******************************************************************************
// Test the basic table functions (add, remove, move, etc.)
//******************************************************************************
import java.util.Hashtable;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.jbundle.app.test.test.db.TestTable;
import org.jbundle.base.model.RecordOwner;
import org.jbundle.base.thread.BaseProcess;
import org.jbundle.base.util.BaseApplication;
import org.jbundle.base.util.Environment;
import org.jbundle.base.util.MainApplication;
import org.jbundle.model.Task;
import org.jbundle.model.util.Util;
import org.jbundle.test.manual.TestAll;
import org.jbundle.test.manual.test.db.thick.DatabaseTest;
import org.jbundle.thin.base.thread.AutoTask;
import org.jbundle.thin.base.util.Application;


/**
 * BaseTest is the standard code to set for all the other tests.
 * @author  don
 * @version
 */

public class MongodbDatabaseTest extends DatabaseTest
{

    /**
      *Creates new TestAll
      */
    public MongodbDatabaseTest(String strTestName) {
        super(strTestName);
    }

    public static Test suite()
    {
        TestSuite suite= new TestSuite();
        suite.addTest(new MongodbDatabaseTest("testDatabase"));
        suite.addTest(new MongodbDatabaseTest("testDatabase"));
        suite.addTest(new MongodbDatabaseTest("testGridAccess"));
        return suite;
    }

    /**
     * Set up for the test.
     */
    public void setUp()
    {
        if (testTable == null)
        {
            String[] args = {"remote=Mongodb", "local=Mongodb", "table=Mongodb"};
            args = TestAll.fixArgs(args);
            Map<String,Object> properties = new Hashtable<String,Object>();
            Util.parseArgs(properties, args);
            Environment env = new Environment(properties);
            Application app = new MainApplication(env, properties, null);
            Task task = new AutoTask(app, null, null);
            RecordOwner recordOwner = new BaseProcess(task, null, null);
            testTable = new TestTable(recordOwner);
        }
        this.addTestTableRecords(testTable);
    }
    /**
     * Tear down for the test.
     */
    public void tearDown()
    {
        ((BaseApplication)testTable.getRecordOwner().getTask().getApplication()).getEnvironment().free();
    }
}
