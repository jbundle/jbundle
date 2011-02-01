package org.jbundle.test.manual.test.db.transactions;

//******************************************************************************
// Test the basic table functions (add, remove, move, etc.)
//******************************************************************************
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.Category;
import org.jbundle.app.test.test.db.TestTable;
import org.jbundle.app.test.test.db.TestTableNoAuto;
import org.jbundle.base.db.RecordOwner;
import org.jbundle.base.db.SQLParams;
import org.jbundle.base.field.DateTimeField;
import org.jbundle.base.thread.BaseProcess;
import org.jbundle.base.util.DBConstants;
import org.jbundle.base.util.Environment;
import org.jbundle.base.util.MainApplication;
import org.jbundle.model.DBException;
import org.jbundle.model.Task;
import org.jbundle.test.manual.TestAll;
import org.jbundle.thin.base.thread.AutoTask;
import org.jbundle.thin.base.util.Application;
import org.jbundle.thin.base.util.Util;


/**
 * BaseTest is the standard code to set for all the other tests.
 * @author  don
 * @version 
 */
public class TransactionTest extends TestCase
{

    /**
      *Creates new TestAll
      */
    public TransactionTest(String strTestName)
    {
        super(strTestName);
    }
    public static Test suite()
    {
        TestSuite suite= new TestSuite();     
        suite.addTest(new TransactionTest("testTableRecords"));
        return suite;
    }
    /**
     * Add the test table records.
     */
    public void testTableRecords()
    {
        Category cat = Category.getInstance(TransactionTest.class.getName());

        String[] args = {"remote=Jdbc", "local=Jdbc"};
        args = TestAll.fixArgs(args);
        Map<String,Object> properties = new Hashtable<String,Object>();
        Util.parseArgs(properties, args);
        Environment env = new Environment(properties);
        Application app = new MainApplication(env, properties, null);
        Task task = new AutoTask(app, null, null);
        RecordOwner recordOwner = new BaseProcess(task, null, null);
        
        recordOwner.setProperty(SQLParams.AUTO_COMMIT_PARAM, DBConstants.FALSE);
        
        TestTable testTable = new TestTableNoAuto(recordOwner);

        boolean bSuccess = false;
        int iCount = 0;
        cat.debug("Begin Test\n");
        cat.debug("Open table.\n");
        try   {
            testTable.open();           // Open the table
        } catch (DBException e)   {
            String strError = e.getMessage();
            cat.debug(strError);
            fail("Error on open");
        }

        try   {     
            testTable.setKeyArea(DBConstants.MAIN_KEY_AREA);
            iCount = 0;
            testTable.close();
            while (testTable.hasNext())
            {
                testTable.next();
                cat.debug(testTable.toString());
                iCount++;
            }
        } catch (DBException e)   {
            cat.debug("Error reading through file: Error" + e.getMessage() + "\n");
        }
            
        cat.debug("Delete all old records.\n");
        iCount = 0;
        try   {
            testTable.close();
            while (testTable.hasNext())
            {
                testTable.move(+1);
                testTable.remove();
                iCount++;
            }
            
            testTable.getTable().getDatabase().commit();

        } catch (DBException e)   {
            cat.debug("Could not delete record: Error" + e.getMessage() + "\n");
            cat.debug(testTable.toString());
            fail("Error on delete");
        }
        cat.debug("deleted records " + iCount + "\n");

        try   {
            cat.debug("Add records.\n");
            testTable.addNew();

            boolean bRefreshTest = true;
            if (bRefreshTest)
                testTable.setOpenMode(DBConstants.OPEN_REFRESH_AND_LOCK_ON_CHANGE_STRATEGY);  // Make sure keys are updated before sync

            testTable.getField(TestTable.kID).setString("1");
            testTable.getField(TestTable.kTestName).setString("A - Excellent Agent");
            testTable.getField(TestTable.kTestMemo).setString("This is a very long line\nThis is the second line.");
            testTable.getField(TestTable.kTestYesNo).setState(true);
            testTable.getField(TestTable.kTestLong).setValue(15000000);
            testTable.getField(TestTable.kTestShort).setValue(14000);
            testTable.getField(TestTable.kTestDateTime).setValue(DateTimeField.currentTime());
            ((DateTimeField)testTable.getField(TestTable.kTestDate)).setDate(new Date(), DBConstants.DISPLAY, DBConstants.SCREEN_MOVE);
            testTable.getField(TestTable.kTestTime).setString("5:15 PM");
            testTable.getField(TestTable.kTestFloat).setValue(1234.56);
            testTable.getField(TestTable.kTestDouble).setValue(1234567.8912);
            testTable.getField(TestTable.kTestPercent).setValue(34.56);
            testTable.getField(TestTable.kTestReal).setValue(5432.432);
            testTable.getField(TestTable.kTestCurrency).setValue(1234567.89);
            testTable.getField(TestTable.kTestKey).setString("A");
cat.debug(testTable.toString());
            testTable.add();

            testTable.addNew();
            testTable.getField(TestTable.kID).setString("2");
            testTable.getField(TestTable.kTestName).setString("B - Good Agent");
            testTable.getField(TestTable.kTestKey).setString("B");
            testTable.add();

            testTable.addNew();
            testTable.getField(TestTable.kID).setString("3");
            testTable.getField(TestTable.kTestName).setString("C - Average Agent");
            testTable.getField(TestTable.kTestKey).setString("C");
            testTable.add();

            testTable.addNew();
            testTable.getField(TestTable.kID).setString("4");
            testTable.getField(TestTable.kTestName).setString("F - Fam Trip Agent");
            testTable.getField(TestTable.kTestKey).setString("B");
            testTable.add();

            cat.debug("4 records added.\n");
            
            testTable.getTable().getDatabase().commit();

        } catch (DBException e)   {
            cat.debug("Error adding record. Error: " + e.getMessage() + "\n");
            cat.debug(testTable.toString());
            fail("Error on add");
        }
        
        try   {
            cat.debug("add two more record (with a rollback).\n");
            testTable.addNew();
            testTable.getField(TestTable.kID).setString("5");
            testTable.getField(TestTable.kTestName).setString("T - Tour Operator");
            testTable.getField(TestTable.kTestKey).setString("B");
            testTable.add();

            testTable.addNew();
            testTable.getField(TestTable.kID).setString("6");
            testTable.getField(TestTable.kTestName).setString("Q - Q Agency");
            testTable.getField(TestTable.kTestKey).setString("Q");
            testTable.add();
            cat.debug("2 records added.\n");
            
            testTable.getTable().getDatabase().rollback();
        } catch (DBException ex)    {
            ex.printStackTrace();
            cat.debug("Error adding record. Error: " + ex.getMessage() + "\n");
            cat.debug(testTable.toString());
//          fail("Error on add");
        }


        cat.debug("Count keys in both indexes.\n");
        try   {
            testTable.setKeyArea(DBConstants.MAIN_KEY_AREA);
            iCount = 0;
            testTable.close();
            while (testTable.hasNext())   {
                testTable.move(+1);
cat.debug(testTable.toString());
                iCount++;
            }
        } catch (DBException e)   {
            cat.debug("NO NO NO\n");
            fail("Error on cound");
        }
        
        cat.debug("Count (should = 4 if db supports trx) = " + iCount);
        
    }
}
