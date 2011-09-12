/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.test.manual.test.db.thick;

//******************************************************************************
// Test the basic table functions (add, remove, move, etc.)
//******************************************************************************
import java.util.Hashtable;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.jbundle.app.test.test.db.TestTableNoAuto;
import org.jbundle.base.db.RecordOwner;
import org.jbundle.base.thread.BaseProcess;
import org.jbundle.base.util.BaseApplication;
import org.jbundle.base.util.Environment;
import org.jbundle.base.util.MainApplication;
import org.jbundle.model.Task;
import org.jbundle.model.util.Util;
import org.jbundle.test.manual.TestAll;
import org.jbundle.thin.base.thread.AutoTask;
import org.jbundle.thin.base.util.Application;


/**
 * BaseTest is the standard code to set for all the other tests.
 * @author  don
 * @version 
 */

public class JiniDatabaseTest extends DatabaseTest
{

    /**
      *Creates new TestAll
      */
    public JiniDatabaseTest(String strTestName) {
        super(strTestName);
    }
    
    public static Test suite()
    {
        TestSuite suite= new TestSuite();     
        suite.addTest(new JiniDatabaseTest("testDatabase"));
        suite.addTest(new JiniDatabaseTest("testDatabase"));    // Run it twice
        suite.addTest(new JiniDatabaseTest("testGridAccess"));
        return suite;
    }

    /**
     * Set up for the test.
     */
    public void setUp()
    {
        if (testTable == null)
        {
            String[] args = {"remote=Client", "local=Client", "table=Client", "connectionType=proxy"};
            args = TestAll.fixArgs(args);
            Map<String,Object> properties = new Hashtable<String,Object>();
            Util.parseArgs(properties, args);
            Environment env = new Environment(properties);
            Application app = new MainApplication(env, properties, null);
            env.setDefaultApplication(app);
            Task task = new AutoTask(app, null, null);
//          SApplet.main(null);
//          Task task = new SApplet();
//          task.initTask(app, null);

            RecordOwner recordOwner = new BaseProcess(task, null, null);
            testTable = new TestTableNoAuto(recordOwner);
        }
    }
    /**
     * Tear down for the test.
     */
    public void tearDown()
    {
        ((BaseApplication)testTable.getRecordOwner().getTask().getApplication()).getEnvironment().free();
        try   {
        Thread.currentThread().sleep(1 * 1000);     // Wait for remote to get ready
        } catch (InterruptedException ex) {
        }
    }
}
