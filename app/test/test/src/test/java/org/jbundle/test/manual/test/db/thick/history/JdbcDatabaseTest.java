/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.test.manual.test.db.thick.history;

//******************************************************************************
// Test the basic table functions (add, remove, move, etc.)
//******************************************************************************
import java.util.Hashtable;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.log4j.Category;
import org.jbundle.app.test.test.db.TestTable;
import org.jbundle.app.test.test.db.TestTableHistory;
import org.jbundle.base.db.event.HistoryHandler;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.RecordOwner;
import org.jbundle.base.thread.BaseProcess;
import org.jbundle.base.util.BaseApplication;
import org.jbundle.base.util.Environment;
import org.jbundle.base.util.MainApplication;
import org.jbundle.model.DBException;
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

public class JdbcDatabaseTest extends DatabaseTest
{

    /**
      *Creates new TestAll
      */
    public JdbcDatabaseTest(String strTestName) {
        super(strTestName);
    }
    
    public static Test suite()
    {
        TestSuite suite= new TestSuite();     
        suite.addTest(new JdbcDatabaseTest("testHistory"));
        return suite;
    }

    /**
     * Set up for the test.
     */
    public void setUp()
    {
        if (testTable == null)
        {
            String[] args = {"remote=Jdbc", "local=Jdbc"};
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
    /*
     * Do the test.
     */
    public void testHistory()
    {
        this.addTestTableRecords(testTable);

        int iCount = 0;
//      TestTable testTable = new TestTable(null);
/**
        ResourceTable gridTable = new ResourceTable(null, testTable);
        TestTable testTableTwo = new TestTableNoAuto((RecordOwner)null)
        {
            public String getDatabaseName()
            {
                return super.getDatabaseName() + "_es";
            }
        };
        gridTable.addTable(testTableTwo.getTable());

*/

// Try to add a duplicate

    Category cat = Category.getInstance(this.getClass().getName());
//      cat.setPriority(Priority.DEBUG);
    
    testTable.addListener(new HistoryHandler(new TestTableHistory(testTable.getRecordOwner())));
    
        try   {
            cat.debug("Try to add another record.");
            testTable.addNew();
            testTable.getField(TestTable.kID).setString("4");
            testTable.getField(TestTable.TEST_NAME).setString("F - Fam Trip Agent");
            testTable.getField(TestTable.TEST_KEY).setString("X");
            testTable.add();
            cat.debug("good, duplicate not sensed");
        } catch (DBException e)   {
            cat.debug("Good, Duplicate sensed");
        }
        
        cat.debug("Count keys in both indexes.");
        try   {
            testTable.setKeyArea(DBConstants.MAIN_KEY_AREA);
            iCount = 0;
            testTable.close();
            while (testTable.hasNext())   {
                testTable.move(+1);
                iCount++;
            }
            cat.debug("Main index: Count: " + iCount);
            testTable.setKeyArea(DBConstants.MAIN_KEY_AREA+1);
            iCount = 0;
            testTable.close();
            while (testTable.hasNext())   {
                testTable.move(+1);
                iCount++;
            }
            cat.debug("Secondary index: Count: " + iCount);
            testTable.setKeyArea(DBConstants.MAIN_KEY_AREA);
        } catch (DBException e)   {
            assertTrue("Error reading through file", false);
            cat.debug("Error reading through file: Error" + e.getMessage());
            cat.debug(testTable.toString());
            System.exit(0);
        }
        cat.debug("Now, do some operations while reading through the file.");
        try   {
            testTable.close();
            while (testTable.hasNext())   {
                testTable.move(+1);
                if (testTable.getField(TestTable.kID).getString().equals("2"))
                {
                    testTable.edit();
                    testTable.getField(TestTable.TEST_NAME).setString("B - Pretty Good Agent");
                    cat.debug("Update record #2.");
                    testTable.set();
                }
                else if (testTable.getField(TestTable.kID).getString().equals("3"))
                {
                    cat.debug("Delete record #3.");
                    testTable.remove();
                }
    // Try to update a duplicate
                else if (testTable.getField(TestTable.kID).getString().equals("4"))
                {
                    testTable.edit();
                    testTable.getField(TestTable.kID).setString("1");
                    cat.debug("Try to update record #4. (Duplicate key).");
                    testTable.set();
                }
            }
            cat.debug("good, duplicate not sensed");
        } catch (DBException e)   {
            cat.debug("Good, Duplicate sensed");
        }

    }

}
