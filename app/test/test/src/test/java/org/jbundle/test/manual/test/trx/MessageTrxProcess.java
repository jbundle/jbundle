/*
 *  @(#)MessageStormProcess.
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.test.manual.test.trx;

import java.util.Date;
import java.util.Map;

import org.jbundle.app.test.test.db.TestTable;
import org.jbundle.app.test.test.db.TestTableNoAuto;
import org.jbundle.base.db.Record;
import org.jbundle.base.db.RecordOwner;
import org.jbundle.base.db.SQLParams;
import org.jbundle.base.field.DateTimeField;
import org.jbundle.base.thread.BaseProcess;
import org.jbundle.base.util.DBConstants;
import org.jbundle.model.DBException;
import org.jbundle.model.RecordOwnerParent;


/**
 *  MessageStormProcess - .
 */
public class MessageTrxProcess extends BaseProcess
{
    /**
     * Default constructor.
     */
    public MessageTrxProcess()
    {
        super();
    }
    /**
     * Constructor.
     */
    public MessageTrxProcess(RecordOwnerParent taskParent, Record recordMain, Map<String,Object> properties)
    {
        this();
        this.init(taskParent, recordMain, properties);
    }
    /**
     * Initialize class fields.
     */
    public void init(RecordOwnerParent taskParent, Record recordMain, Map<String, Object> properties)
    {
        super.init(taskParent, recordMain, properties);
    }
    /**
     * Run Method.
     */
    public void run()
    {
        this.testTableRecords(this);
    }
    /**
     * StartProcess Method.
     */
    /**
     * Add the test table records.
     */
    public void testTableRecords(RecordOwner recordOwner)
    {
        recordOwner.setProperty(SQLParams.AUTO_COMMIT_PARAM, DBConstants.FALSE);
        
        TestTable testTable = new TestTableNoAuto(recordOwner);

        int iCount = 0;
        System.out.println("Begin Test\n");
        System.out.println("Open table.\n");
        try   {
            testTable.open();           // Open the table
        } catch (DBException e)   {
            String strError = e.getMessage();
            System.out.println(strError);
            System.out.println("Error on open");
        }

        try   {     
            testTable.setKeyArea(DBConstants.MAIN_KEY_AREA);
            iCount = 0;
            testTable.close();
            while (testTable.hasNext())
            {
                testTable.next();
                System.out.println(testTable.toString());
                iCount++;
            }
        } catch (DBException e)   {
            System.out.println("Error reading through file: Error" + e.getMessage() + "\n");
        }
            
        System.out.println("Delete all old records.\n");
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
            System.out.println("Could not delete record: Error" + e.getMessage() + "\n");
            System.out.println(testTable.toString());
            System.out.println("Error on delete");
        }
        System.out.println("deleted records " + iCount + "\n");

        try   {
            System.out.println("Add records.\n");
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
System.out.println(testTable.toString());
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

            System.out.println("4 records added.\n");
            
            testTable.getTable().getDatabase().commit();

        } catch (DBException e)   {
            System.out.println("Error adding record. Error: " + e.getMessage() + "\n");
            System.out.println(testTable.toString());
            System.out.println("Error on add");
        }
        
        try   {
            System.out.println("add two more record (with a rollback).\n");
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
            System.out.println("2 records added.\n");
            
            testTable.getTable().getDatabase().rollback();
        } catch (DBException ex)    {
            ex.printStackTrace();
            System.out.println("Error adding record. Error: " + ex.getMessage() + "\n");
            System.out.println(testTable.toString());
//          System.out.println("Error on add");
        }


        System.out.println("Count keys in both indexes.\n");
        try   {
            testTable.setKeyArea(DBConstants.MAIN_KEY_AREA);
            iCount = 0;
            testTable.close();
            while (testTable.hasNext())   {
                testTable.move(+1);
System.out.println(testTable.toString());
                iCount++;
            }
        } catch (DBException e)   {
            System.out.println("NO NO NO\n");
            System.out.println("Error on cound");
        }
        
        System.out.println("Count (should = 4 if db supports trx) = " + iCount);
        
    }

}
