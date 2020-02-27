/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.test.manual.test.db.fullthin;

//******************************************************************************
// Test the basic table functions (add, remove, move, etc.)
//******************************************************************************
import org.apache.log4j.Category;
import org.jbundle.model.DBException;
import org.jbundle.test.manual.TestAll;
import org.jbundle.thin.app.test.test.db.TestTable;
import org.jbundle.thin.app.test.test.db.TestTableNoAuto;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.FieldList;
import org.jbundle.thin.base.db.FieldTable;
import org.jbundle.thin.base.screen.BaseApplet;


/**
 * BaseTest is the standard code to set for all the other tests.
 * @author  don
 * @version 
 */

public abstract class DatabaseTest extends BaseTest
{

    /**
      *Creates new TestAll
      */
    public DatabaseTest(String strTestName)
    {
        super(strTestName);
    }
    
    FieldList record = null;
    FieldTable table = null;
    BaseApplet applet = null;
    
    /**
     * Setup.
     */
    public void setUp()
    {
        cat = Category.getInstance(DatabaseTest.class.getName());

        BaseApplet.main(null);
        String[] args = {"remote=Client", "local=Client", "table=Client"};
        args = TestAll.fixArgs(args);
        applet = new BaseApplet(args);

        if (record == null)
            record = new TestTable(this);

        table = this.setupThinTable();
    }
    /**
     * Create the thin table - override this.
     */
    public abstract FieldTable setupThinTable();
    /**
     * Setup.
     */
    public void tearDown()
    {
        if (record != null)
        {
            record.free();
            record = null;
        }
    }
    /*
     * Do the test.
     */
    public void testDatabase()
    {
        FieldTable testTable = record.getTable();
        this.addTestTableRecords(testTable);

        boolean bSuccess = false;
        int iCount = 0;

    Category cat = Category.getInstance(this.getClass().getName());
//      cat.setPriority(Priority.DEBUG);
        try   {
            cat.debug("Try to add a duplicate record.");
            testTable.addNew();
            record.getField("ID").setString("4");
            record.getField("TestName").setString("F - Fam Trip Agent");
            record.getField("TestKey").setString("X");
            testTable.add(record);
            cat.debug("Error, duplicate not sensed");
            cat.debug(testTable.toString());
            assertTrue("Error, duplicate not sensed", false);
            System.exit(0);
        } catch (DBException e)   {
            cat.debug("Good, Duplicate sensed");
        }
        
        cat.debug("Count keys in both indexes.");
        try   {
            record.setKeyArea(Constants.PRIMARY_KEY);
            iCount = 0;
            testTable.close();
            while (testTable.hasNext())   {
                testTable.move(+1);
                iCount++;
            }
            cat.debug("Main index: Count: " + iCount);
            record.setKeyArea("TestCode");
            iCount = 0;
            testTable.close();
            while (testTable.hasNext())   {
                testTable.move(+1);
                iCount++;
            }
            cat.debug("Secondary index: Count: " + iCount);
            record.setKeyArea(Constants.PRIMARY_KEY);
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
                if (record.getField("ID").getString().equals("2"))
                {
                    testTable.edit();
                    record.getField("TestName").setString("B - Pretty Good Agent");
                    cat.debug("Update record #2.");
                    testTable.set(record);
                }
                else if (record.getField("ID").getString().equals("3"))
                {
                    cat.debug("Delete record #3.");
                    testTable.remove();
                }
    // Try to update a duplicate
                else if (record.getField("ID").getString().equals("4"))
                {
                    testTable.edit();
                    record.getField("ID").setString("1");
                    cat.debug("Try to update record #4. (Duplicate key).");
                    testTable.set(record);
                }
            }
            cat.debug("Error, duplicate not sensed");
            assertTrue("Error, duplicate not sensed", false);
            cat.debug(testTable.toString());
            System.exit(0);
        } catch (DBException e)   {
            cat.debug("Good, Duplicate sensed");
        }

    cat.debug("Now, try to read through all the records");
    try   {     
        record.setKeyArea("PrimaryKey");
        iCount = 0;
        testTable.open(); // Manually open it!
        testTable.close();
        while (testTable.hasNext())   {
//          cat.debug(testTable.toString());
            testTable.move(+1);
            iCount++;
        }
    } catch (DBException e)   {
        cat.debug("Error reading through file: Error" + e.getMessage() + "");
        assertTrue("Error reading through file", false);
    }
    cat.debug("Primary Count: " + iCount + "");
    try   {     
        record.setKeyArea("TestKey");
        iCount = 0;
        testTable.open(); // Manually open it!
        testTable.close();
        while (testTable.hasNext())   {
            testTable.move(+1);
            cat.debug("Record ID = " + record.getField("ID").toString());
            iCount++;
        }
    } catch (DBException e)   {
        cat.debug("Error reading through file: Error" + e.getMessage() + "");
        assertTrue("Error reading through file", false);
    }
    cat.debug("Secondary Count: " + iCount + "");

    cat.debug("Now, let's read through the file backwards, using the testTableKey.");
        try   {
            record.setKeyArea("TestKey");
            testTable.close();
            while (testTable.hasPrevious())   {
                testTable.move(Constants.PREVIOUS_RECORD);
                cat.debug("Record ID = " + record.getField("ID").toString());
            }
        } catch (DBException e)   {
            assertTrue("Error reading through file backwards", false);
            cat.debug("Error: " + e.getMessage() + "");
            cat.debug(testTable.toString());
            System.exit(0);
        }

        cat.debug("Now, let's seek a record in the PrimaryKey.");
        bSuccess = false;
        try   {
            record.setKeyArea("PrimaryKey");
//+         testTable.addNew();
            record.getField("ID").setString("2");
            bSuccess = testTable.seek("=");
        } catch (DBException ex)    {
            ex.printStackTrace();
            cat.debug("Error: " + ex.getMessage() + "");
            cat.debug(testTable.toString());
            assertTrue("Error seeking on the primary key", false);
            System.exit(0);
        }
        if (bSuccess)
        {
            cat.debug("Success");
            cat.debug(testTable.toString());
        }
        else
        {
            assertTrue("Error record not found", false);
            cat.debug("Error, not found");
            cat.debug(testTable.toString());
            System.exit(0);
        }

        cat.debug("Now, let's seek a record in the PrimaryKey that doesn't exist.");
        bSuccess = false;
        try   {
            record.setKeyArea("PrimaryKey");
            testTable.addNew();
            record.getField("ID").setString("3");
            bSuccess = testTable.seek("=");
        } catch (DBException e)   {
            assertTrue("Error on seek", false);
            cat.debug("Error: " + e.getMessage() + "");
            cat.debug(testTable.toString());
            System.exit(0);
        }
        if (bSuccess)
        {
            assertTrue("Error found non-existent key", false);
            cat.debug("Error, found key");
            cat.debug(testTable.toString());
            System.exit(0);
        }
        else
        {
            cat.debug("Good, not found");
        }

        cat.debug("Now, let's seek a record in the SecondaryKey. (Not unique)");
        try   {
            record.setKeyArea("TestKey");
            testTable.addNew();
            record.getField("TestKey").setString("B");
            bSuccess = testTable.seek("=");
        } catch (DBException e)   {
            assertTrue("Error seeking on Secondary key", false);
            cat.debug("Error: " + e.getMessage() + "");
            cat.debug(testTable.toString());
            System.exit(0);
        }
        if (bSuccess)
        {
            if ((record.getField("ID").getString().equals("2"))
                || (record.getField("ID").getString().equals("4"))
                || (record.getField("ID").getString().equals("5")))
            {
                cat.debug("Good, found first key");
                cat.debug(testTable.toString());
            }
            else
            {
                assertTrue("Error found wrong key", false);
                cat.debug("Error, found wrong key");
                cat.debug(testTable.toString());
                System.exit(0);
            }
        }
        else
        {
            assertTrue("Error key not found", false);
            cat.debug("Error, not found");
            cat.debug(testTable.toString());
            System.exit(0);
        }

        cat.debug("Now, let's seek a record in the SecondaryKey that doesn't exist");
        try   {
            testTable.addNew();
            record.getField("TestKey").setString("X");
            bSuccess = testTable.seek("=");
        } catch (DBException e)   {
            assertTrue("Error on read non-existent 2nd key", false);
            cat.debug("Error: " + e.getMessage() + "");
            cat.debug(testTable.toString());
            System.exit(0);
        }
        if (bSuccess)
        {
            assertTrue("Error found non-existent key", false);
            cat.debug("Error, found key");
            cat.debug(testTable.toString());
            System.exit(0);
        }
        else
        {
            cat.debug("Good, not found");
            cat.debug(testTable.toString());
        }

        cat.debug("Now, let's seek a the next >= record that does exist");
        try   {
            testTable.addNew();
            record.getField("TestKey").setString("B");
            bSuccess = testTable.seek(">=");
        } catch (DBException e)   {
            assertTrue("Error on seek >=", false);
            cat.debug("Error: " + e.getMessage() + "");
            cat.debug(testTable.toString());
            System.exit(0);
        }
        if (bSuccess)
        {
            if ((record.getField("ID").getString().equals("2"))
                || (record.getField("ID").getString().equals("4"))
                || (record.getField("ID").getString().equals("5")))
            {
                cat.debug("Success, found key");
                cat.debug(testTable.toString());
            }
            else
            {
                assertTrue("Error found wrong key", false);
                cat.debug("Error, found wrong key");
                cat.debug(testTable.toString());
                System.exit(0);
            }
        }
        else
        {
            assertTrue("Error found wrong key", false);
            cat.debug("Error, not found");
            cat.debug(testTable.toString());
            System.exit(0);
        }

        cat.debug("Now, let's seek a the next >= record that doesn't exist");
        try   {
            testTable.addNew();
            record.getField("TestKey").setString("C");
            bSuccess = testTable.seek(">=");
        } catch (DBException e)   {
            assertTrue("Error on seek >=", false);
            cat.debug("Error: " + e.getMessage() + "");
            cat.debug(testTable.toString());
            System.exit(0);
        }
        if (bSuccess)
        {
            if (record.getField("ID").getString().equals("6"))
            {
                cat.debug("Success, found key");
                cat.debug(testTable.toString());
            }
            else
            {
                assertTrue("Error found wrong key", false);
                cat.debug("Error, found wrong key");
                cat.debug(testTable.toString());
                System.exit(0);
            }
        }
        else
        {
            assertTrue("Error not found", false);
            cat.debug("Error, not found");
            cat.debug(testTable.toString());
            System.exit(0);
        }

        cat.debug("Now, let's seek a the next > record that does exist");
        try   {
            testTable.addNew();
            record.getField("TestKey").setString("B");
            bSuccess = testTable.seek(">");
        } catch (DBException e)   {
            assertTrue("Error on seek >=", false);
            cat.debug("Error: " + e.getMessage() + "");
            cat.debug(testTable.toString());
            System.exit(0);
        }
        if (bSuccess)
        {
            if (record.getField("ID").getString().equals("6"))
            {
                cat.debug("Success, found key");
                cat.debug(testTable.toString());
            }
            else
            {
                assertTrue("Error found wrong key", false);
                cat.debug("Error, found wrong key");
                cat.debug(testTable.toString());
                System.exit(0);
            }
        }
        else
        {
            assertTrue("Error not found", false);
            cat.debug("Error, not found");
            cat.debug(testTable.toString());
            System.exit(0);
        }

        cat.debug("Now, let's seek a the next > record that doesn't exist");
        try   {
            testTable.addNew();
            record.getField("TestKey").setString("C");
            bSuccess = testTable.seek(">");
        } catch (DBException e)   {
            assertTrue("Error on seek next", false);
            cat.debug("Error: " + e.getMessage() + "");
            cat.debug(testTable.toString());
            System.exit(0);
        }
        if (bSuccess)
        {
            if (record.getField("ID").getString().equals("6"))
            {
                cat.debug("Success, found key");
                cat.debug(testTable.toString());
            }
            else
            {
                assertTrue("Error found wrong key", false);
                cat.debug("Error, found wrong key");
                cat.debug(testTable.toString());
                System.exit(0);
            }
        }
        else
        {
            assertTrue("Error not found", false);
            cat.debug("Error, not found");
            cat.debug(testTable.toString());
            System.exit(0);
        }

        cat.debug("Now, let's seek a the next < record that does exist");
        try   {
            testTable.addNew();
            record.getField("TestKey").setString("B");
            bSuccess = testTable.seek("<");
        } catch (DBException e)   {
            assertTrue("Error on seek <", false);
            cat.debug("Error: " + e.getMessage() + "");
            cat.debug(testTable.toString());
            System.exit(0);
        }
        if (bSuccess)
        {
            if (record.getField("ID").getString().equals("1"))
            {
                cat.debug("Success, found key");
                cat.debug(testTable.toString());
            }
            else
            {
                assertTrue("Error found wrong key", false);
                cat.debug("Error, found wrong key");
                cat.debug(testTable.toString());
                System.exit(0);
            }
        }
        else
        {
            assertTrue("Error not found", false);
            cat.debug("Error, not found");
            cat.debug(testTable.toString());
            System.exit(0);
        }

        cat.debug("Now, let's seek a the next < record that doesn't exist");
        try   {
            testTable.addNew();
            record.getField("TestKey").setString("C");
            bSuccess = testTable.seek("<");
        } catch (DBException e)   {
            assertTrue("Error on seek <", false);
            cat.debug("Error: " + e.getMessage() + "");
            cat.debug(testTable.toString());
            System.exit(0);
        }
        if (bSuccess)
        {
            if (record.getField("ID").getString().equals("5"))
            {
                cat.debug("Success, found key");
                cat.debug(testTable.toString());
            }
            else if (record.getField("ID").getString().equals("1"))
            {
                cat.debug("Alternate Success, found key");
                cat.debug(testTable.toString());
            }
            else
            {
                assertTrue("Error found wrong key", false);
                cat.debug("Error, found wrong key");
                cat.debug(testTable.toString());
                System.exit(0);
            }
        }
        else
        {
            assertTrue("Error not found", false);
            cat.debug("Error, not found");
            cat.debug(testTable.toString());
            System.exit(0);
        }

        cat.debug("Now, let's seek a the next <= record that does exist");
        try   {
            testTable.addNew();
            record.getField("TestKey").setString("B");
            bSuccess = testTable.seek("<=");
        } catch (DBException e)   {
            assertTrue("Error on seek <=", false);
            cat.debug("Error: " + e.getMessage() + "");
            cat.debug(testTable.toString());
            System.exit(0);
        }
        if (bSuccess)
        {
            if (record.getField("ID").getString().equals("2"))
            {
                cat.debug("Success, found key");
                cat.debug(testTable.toString());
            }
            else if (record.getField("ID").getString().equals("1"))
            {
                cat.debug("Alternate Success, found key");
                cat.debug(testTable.toString());
            }
            else
            {
                assertTrue("Error found wrong key", false);
                cat.debug("Error, found wrong key");
                cat.debug(testTable.toString());
                System.exit(0);
            }
        }
        else
        {
            assertTrue("Error not found", false);
            cat.debug("Error, not found");
            cat.debug(testTable.toString());
            System.exit(0);
        }

        cat.debug("Now, let's seek a the next <= record that doesn't exist");
        try   {
            testTable.addNew();
            record.getField("TestKey").setString("C");
            bSuccess = testTable.seek("<=");
        } catch (DBException e)   {
            assertTrue("Error on seek <=", false);
            cat.debug("Error: " + e.getMessage() + "");
            cat.debug(testTable.toString());
            System.exit(0);
        }
        if (bSuccess)
        {
            if (record.getField("ID").getString().equals("5"))
            {
                cat.debug("Success, found key");
                cat.debug(testTable.toString());
            }
            else if (record.getField("ID").getString().equals("1"))
            {
                cat.debug("Alternate Success, found key");
                cat.debug(testTable.toString());
            }
            else
            {
                assertTrue("Error found wrong key", false);
                cat.debug("Error, found wrong key");
                cat.debug(testTable.toString());
                System.exit(0);
            }
        }
        else
        {
            assertTrue("Error not found", false);
            cat.debug("Error, not found");
            cat.debug(testTable.toString());
            System.exit(0);
        } 
      
        cat.debug("Now, Test the bookmark properties.");
        cat.debug("First, read through the file using the non unique key and get a bookmark.");
        Object bookmark = null;
        try   {
            record.setKeyArea("TestKey");
            testTable.close();
            while (testTable.hasNext())   {
                testTable.move(+1);
                if (record.getField("ID").getString().equals("2"))
                {
                    bookmark = testTable.getHandle(0);
                }
            }
        } catch (DBException e)   {
            assertTrue("Error in read loop", false);
            cat.debug("Error in read loop");
            cat.debug(testTable.toString());
            System.exit(0);
        }
        cat.debug("Now, try to position to the bookmark.");
        try   {
            testTable.setHandle(bookmark, 0);
            if (record.getField("ID").getString().equals("2"))
            {
                cat.debug("Success, found key");
                cat.debug(testTable.toString());
            }
            else
            {
                assertTrue("Error found wrong key", false);
                cat.debug("Error, found wrong key");
                cat.debug(testTable.toString());
                System.exit(0);
            }
        } catch (DBException e)   {
            assertTrue("Error in set bookmark", false);
            cat.debug("Error in setBookmark");
            cat.debug(testTable.toString());
            System.exit(0);
        }

        cat.debug("Now, start the concurrency testing");

    cat.debug("First, open a second copy of the file");
    FieldList recordSave = record;
    record = new TestTableNoAuto(null);
    this.setupThinTable();
    FieldList testTable2 = record;
    record = recordSave;
    
    FieldTable table2 = testTable2.getTable();

    try   {
        table2.open();              // Open the table
    } catch (DBException e)   {
        String strError = e.getMessage();
        assertTrue("Error reading through file", false);
        cat.debug(strError);
        System.exit(0);
    }

    cat.debug("First, try some locking tests");
    cat.debug("Now lets try some deletes and reads");
    try   {     
        table.move(Constants.FIRST_RECORD);//+      testTable.moveFirst();
        table2.move(Constants.FIRST_RECORD);//+     testTable.moveFirst();
        assertTrue(table.getRecord().getField(0).getData().equals(table2.getRecord().getField(0).getData()));
        table.move(+1);
        table2.move(+1);
        assertTrue(table.getRecord().getField(0).getData().equals(table2.getRecord().getField(0).getData()));
        table.move(+1);
        table.remove();
        cat.debug("Try to move to a deleted record");
        table2.move(+1);
//+     assertTrue(!table.getFieldList().getFieldInfo(0).getData().equals(table2.getFieldList().getFieldInfo(0).getData()));
        cat.debug(testTable.toString());
    } catch (DBException e)   {
        assertTrue("Error reading through file", false);
        cat.debug("Error reading through file: Error" + e.getMessage() + "");
    }

    try   {     
        record.setKeyArea(Constants.PRIMARY_KEY);
        testTable2.setKeyArea(Constants.PRIMARY_KEY);
        record.getField("ID").setString("2");
        bSuccess = table.seek("=");
        cat.debug("Seek file 1 - Primary: " + bSuccess + "");
        testTable2.getField("ID").setString("2");
        bSuccess = table2.seek("=");
        cat.debug("Seek file 2 - Primary: " + bSuccess + "");
        cat.debug("Lock(edit) file 1");
        table.edit();
        cat.debug("Lock(edit) file 2");
        table2.edit();
        cat.debug("Update file 1");
        record.getField("TestName").setString("B - Good Agent1");
        table.set(record);
        cat.debug("Update file 2");
        testTable2.getField("TestName").setString("B - Good Agent2");
        table2.set(testTable2);
    } catch (DBException e)   {
        cat.debug("Error reading through file (alternate success): Error" + e.getMessage() + "");
    }
    cat.debug("Locking tests done");

    cat.debug("Read through the second copy");
    try   {     
        testTable2.setKeyArea(Constants.PRIMARY_KEY);
        iCount = 0;
        table2.close();
        while (table2.hasNext())    {
            table2.move(+1);
            iCount++;
        }
    } catch (DBException e)   {
        assertTrue("Error reading through file", false);
        cat.debug("Error reading through file: Error" + e.getMessage() + "");
    }
    table2.free();
    table2 = null;
    cat.debug("Primary Count: " + iCount + "");

    cat.debug("Read through the first copy");
    try   {     
        record.setKeyArea(Constants.PRIMARY_KEY);
        iCount = 0;
        testTable.close();
        while (table.hasNext())   {
            table.move(+1);
            iCount++;
        }
    } catch (DBException e)   {
        assertTrue("Error reading through file", false);
        cat.debug("Error reading through file: Error" + e.getMessage() + "");
    }
    cat.debug("Primary Count: " + iCount + "");
    
    cat.debug("Read through the first copy using hasNext");
    try   {
        table.close();
        record.setKeyArea(Constants.PRIMARY_KEY);
        iCount = 0;
        while (table.hasNext())   {
            table.next();
            iCount++;
            cat.debug(testTable);
        }
    } catch (DBException e)   {
        assertTrue("Error reading through file", false);
        cat.debug("Error reading through file: Error" + e.getMessage() + "");
    }
    cat.debug("Has Next Primary Count: " + iCount + "");

    cat.debug("Test complete.");
    }
    /*
     * Do the test (same as grid access).
     */
    public void testGridCacheAccess()
    {
        this.testGridAccess();
    }
    /*
     * Do the test.
     */
    public void testGridAccess()
    {
    //    PropertyConfigurator.configure("Simple");
     // get a category instance named "com.foo"
    Category cat = Category.getInstance(this.getClass().getName());

        int iCount;
        this.addGridTestTableRecords(record.getTable());
// Now, start the tests....

        FieldTable recordList = record.getTable();
        try   {
            FieldList newRecord = null;
//          testTable.addListener(new CompareFileFilter(TestTable.TEST_KEY, "B", ">", null, false));

            recordList.close();
            cat.debug("First, lets read a record in the middle.\n");
            newRecord = (FieldList)recordList.get(4);
            cat.debug(record.toString());

            cat.debug("Next, lets read through the table using MoveTo.\n");
            int index = 0;
            for (index = 0; ; index++)
            {
                newRecord = (FieldList)recordList.get(index);
                if (newRecord == null)
                    break;
                cat.debug(newRecord.getField(0).toString());
            }
            cat.debug("Move to count: " + index);
            assertTrue("Move to count is incorrect count = " + index, (index == 16));

            cat.debug("Now, let's access a record in the buffer.\n");
            newRecord = (FieldList)recordList.get(15);
            cat.debug(record.toString());
            assertTrue("Move to 15 did not get record key 16 record = " + record.getField("ID").getData(), (((Integer)record.getField("ID").getData()).intValue() == 15 + 1));

            cat.debug("Now, lets access a record in the record buffer.\n");
            newRecord = (FieldList)recordList.get(4);
            assertTrue("Move to 4 did not get record key 5 record = " + record.getField("ID").getData(), (((Integer)record.getField("ID").getData()).intValue() == 4 + 1));
            cat.debug(record.toString());
            cat.debug(", and update it.\n");
            recordList.edit();
            newRecord.getField("TestName").setString("New 4 Agent");
            recordList.set(record);

            newRecord = (FieldList)recordList.get(4);
            cat.debug(", and update it again.\n");
            recordList.edit();
            assertTrue("Record was not properly updated record = " + record.getField("ID").getData(), ("New 4 Agent".equals(record.getField("TestName").getData())));
            newRecord.getField("TestName").setString("New 4 Agent Two");
            recordList.set(record);

            cat.debug(record.toString()); // Make sure I get the right record

            for (index = 0; ; index++)
            {
                newRecord = (FieldList)recordList.get(index);
                if (newRecord == null)
                    break;
                cat.debug(newRecord.getField(0).toString());
            }
            cat.debug("Move to count: " + index);


            cat.debug("Now, lets access a record not in the record buffer.\n");
            newRecord = (FieldList)recordList.get(7);
            cat.debug(record.toString());
            assertTrue("Move to 7 did not get record key 8 record = " + record.getField("ID").getData(), (((Integer)record.getField("ID").getData()).intValue() == 7 + 1));

            cat.debug("Now, lets access a record not in any buffer.\n");
            newRecord = (FieldList)recordList.get(27);
            if (newRecord == null)
                cat.debug("Good, record doesn't exist");
            else
            {
                cat.debug("Error, returned crummy record");
                fail("Error, returned crummy record");
            }
            cat.debug("Now, lets access a record not in the record buffer.\n");
            newRecord = (FieldList)recordList.get(7);
            cat.debug(record.toString());
            cat.debug("Now, lets change some data and set.\n");
            recordList.edit();
            newRecord.getField("TestName").setString("New Agent");
            recordList.set(record);
            cat.debug(record.toString());

            cat.debug("Now, lets access a record and delete.\n");
            newRecord = (FieldList)recordList.get(4);
            cat.debug(record.toString());
            recordList.edit();
            assertTrue("Move to 4 did not get record key 5 record = " + record.getField("ID").getData(), (((Integer)record.getField("ID").getData()).intValue() == 4 + 1));
            recordList.remove();
            cat.debug("Looks good!");
            
            cat.debug("Go through the new records");
            for (index = 0; ; index++)
            {
                newRecord = (FieldList)recordList.get(index);
                if (newRecord == null)
                    break;
                cat.debug(newRecord.getField(0).toString());
            }
            assertTrue("Move to count is incorrect count = " + index, (index == 16));
            
            newRecord = recordList.getRecord();
            recordList.addNew();
            newRecord.getField("ID").setString("5");
            newRecord.getField("TestName").setString("B - Good Agent");
            newRecord.getField("TestKey").setString("B");
            recordList.add(record);
            
            cat.debug("Go through the new records after an addNew()");
            for (index = 0; ; index++)
            {
                newRecord = (FieldList)recordList.get(index);
                if (newRecord == null)
                    break;
                cat.debug(newRecord.getField(0).toString());
            }
            assertTrue("Move to count is incorrect count = " + index, (index == 17));
            cat.debug("All done with that part!");
    
        } catch (DBException e)   {
            assertTrue("Error, reading through file: Error" + e.getMessage(), false);
            cat.debug("Error reading through file: Error" + e.getMessage() + "\n");
            cat.debug(record.toString());
            System.exit(0);
        }

        cat.debug("Test complete.\n");
    }
}
