/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.test.manual.test.db.thick;

//******************************************************************************
// Test the basic table functions (add, remove, move, etc.)
//******************************************************************************
import com.sun.corba.se.spi.ior.ObjectId;
import org.apache.log4j.Category;
import org.jbundle.app.test.test.db.TestTable;
import org.jbundle.app.test.test.db.TestTableNoAuto;
import org.jbundle.base.db.GridTable;
import org.jbundle.base.db.Record;
import org.jbundle.base.db.event.FileListener;
import org.jbundle.base.db.filter.CompareFileFilter;
import org.jbundle.base.db.filter.StringSubFileFilter;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.CounterField;
import org.jbundle.base.field.ReferenceField;
import org.jbundle.base.field.StringField;
import org.jbundle.base.model.DBConstants;
import org.jbundle.model.DBException;
import org.omg.CORBA_2_3.portable.OutputStream;


/**
 * BaseTest is the standard code to set for all the other tests.
 * @author  don
 * @version 
 */

public class DatabaseTest extends BaseTest
{

    /**
      * Creates new TestAll.
      */
    public DatabaseTest(String strTestName)
    {
        super(strTestName);
    }
    
    protected TestTable testTable = null;
    /**
     * Set up for the test.
     */
    public void setUp()
    {
    }
    /**
     * Tear down for the test.
     */
    public void tearDown()
    {
    }
    /*
     * Do the test.
     */
    public void testDatabase()
    {
        this.addTestTableRecords(testTable);

        testTable.setOpenMode(DBConstants.OPEN_NORMAL);  // Make sure refresh is turned off.

        boolean bSuccess = false;
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
        if (!testTable.isAutoSequence()) {
            try {
                cat.debug("Try to add a duplicate record.");
                testTable.addNew();
                testTable.getField(TestTable.kID).setString("4");
                testTable.getField(TestTable.TEST_NAME).setString("If you see this there was an error");
                testTable.getField(TestTable.TEST_KEY).setString("X");
                testTable.add();
                cat.debug("Error, duplicate not sensed");

                System.exit(0);
                cat.debug(testTable.toString());
                assertTrue("Error, duplicate not sensed", false);
                System.exit(0);
            } catch (DBException ex) {
                cat.debug("Good, Duplicate sensed");
            }
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
        String recordTwo = null;
        try   {
            Object bookmark = null;
            testTable.close();
            while (testTable.hasNext())   {
                testTable.move(+1);
                if (testTable.getField(TestTable.TEST_NAME).getString().equals("B - Good Agent"))
                {
                    recordTwo = testTable.getField(TestTable.kID).getString();
                    cat.debug("Update record #2.");
                    testTable.edit();
                    testTable.getField(TestTable.TEST_NAME).setString("B - Pretty Good Agent");
                    testTable.set();
                }
                else if (testTable.getField(TestTable.TEST_NAME).getString().equals("C - Average Agent"))
                {
                    cat.debug("Delete record #3.");
                    testTable.remove();
                }
                // Try to position to a record and update it (this should NOT mess up the query)
                // This is required to simulate a WRITE where the record has been updated and has to be refreshed
                else if (testTable.getField(TestTable.TEST_NAME).getString().equals("F - Fam Trip Agent"))
                {
                    bookmark = testTable.getHandle(DBConstants.BOOKMARK_HANDLE);
                    Record record = testTable.setHandle(bookmark, DBConstants.BOOKMARK_HANDLE);
                    record.edit();
                    record.getField(TestTable.TEST_NAME).setString("B - Pretty Good Agent #2");
                    cat.debug("Try to update record #4. (Set Handle during query).");
                    record.set();
                    testTable.addNew();
                    testTable.getField(0).setData(bookmark);
                    if (testTable.seek(null))
                    {
                        record.edit();
                        record.getField(TestTable.TEST_NAME).setString("B - Pretty Good Agent #3");
                        cat.debug("Try to update record #4. (Seek during query).");
                        record.set();
                    }
                }
                // Try to update a duplicate
                else if (testTable.getField(TestTable.kID).getString().equals("5"))
                {   // Note: Currently not working with autosequence
                    testTable.edit();
                    testTable.getField(TestTable.kID).setString("1");
                    cat.debug("Try to update record #5. (Duplicate key).");
                    testTable.set();
                }
            }
            cat.debug("Error, duplicate not sensed");
//            assertTrue("Error, duplicate not sensed", false);
            cat.debug(testTable.toString());
//            System.exit(0);
        } catch (DBException ex)    {
            ex.printStackTrace();
            cat.debug("Good, Duplicate sensed");
        }

    cat.debug("Now, try to read through all the records");
    try   {     
        testTable.setKeyArea("PrimaryKey");
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
        testTable.setKeyArea("TestKey");
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
    cat.debug("Secondary Count: " + iCount + "");

    cat.debug("Now, let's read through the file backwards, using the testTableKey.");
        try   {
            testTable.setKeyArea("TestKey");
            testTable.close();
            while (testTable.hasPrevious())   {
                testTable.move(DBConstants.PREVIOUS_RECORD);
                cat.debug(testTable.toString());
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
            testTable.setKeyArea("PrimaryKey");
//+         testTable.addNew();
            testTable.getField(TestTable.kID).setString(recordTwo);
            bSuccess = testTable.seek("=");
        } catch (DBException e)   {
                assertTrue("Error seeking on the primary key", false);
                cat.debug("Error: " + e.getMessage() + "");
            cat.debug(testTable.toString());
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
            testTable.setKeyArea("PrimaryKey");
            testTable.addNew();
            if (testTable.getField(TestTable.kID).getDataClass() == Integer.class)
                testTable.getField(TestTable.kID).setString("3");
            else
                testTable.getField(TestTable.kID).setString("000000000000000000000003");
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
            testTable.setKeyArea("TestKey");
            testTable.addNew();
            testTable.getField(TestTable.TEST_KEY).setString("B", DBConstants.DISPLAY, DBConstants.INIT_MOVE);
            testTable.getField(TestTable.TEST_CODE).setString("B", DBConstants.DISPLAY, DBConstants.INIT_MOVE);
            bSuccess = testTable.seek("=");
        } catch (DBException e)   {
            assertTrue("Error seeking on Secondary key", false);
            cat.debug("Error: " + e.getMessage() + "");
            cat.debug(testTable.toString());
            System.exit(0);
        }
        if (bSuccess)
        {
            if ((testTable.getField(TestTable.TEST_NAME).getString().startsWith("B"))
                || (testTable.getField(TestTable.TEST_NAME).getString().startsWith("F"))
                || (testTable.getField(TestTable.TEST_NAME).getString().startsWith("T")))
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
            testTable.getField(TestTable.TEST_KEY).setString("X");
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
            testTable.getField(TestTable.TEST_KEY).setString("B");
            bSuccess = testTable.seek(">=");
        } catch (DBException e)   {
            assertTrue("Error on seek >=", false);
            cat.debug("Error: " + e.getMessage() + "");
            cat.debug(testTable.toString());
            System.exit(0);
        }
        if (bSuccess)
        {
            if ((testTable.getField(TestTable.TEST_NAME).getString().startsWith("B"))
                    || (testTable.getField(TestTable.TEST_NAME).getString().startsWith("F"))
                    || (testTable.getField(TestTable.TEST_NAME).getString().startsWith("T")))
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
            testTable.getField(TestTable.TEST_KEY).setString("C");
            bSuccess = testTable.seek(">=");
        } catch (DBException e)   {
            assertTrue("Error on seek >=", false);
            cat.debug("Error: " + e.getMessage() + "");
            cat.debug(testTable.toString());
            System.exit(0);
        }
        if (bSuccess)
        {
            if (testTable.getField(TestTable.TEST_NAME).getString().startsWith("Q"))
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
            testTable.getField(TestTable.TEST_KEY).setString("B");
            testTable.getField(TestTable.TEST_CODE).setString("B");
            bSuccess = testTable.seek(">");
        } catch (DBException e)   {
            assertTrue("Error on seek >=", false);
            cat.debug("Error: " + e.getMessage() + "");
            cat.debug(testTable.toString());
            System.exit(0);
        }
        if (bSuccess)
        {
            if (testTable.getField(TestTable.TEST_NAME).getString().startsWith("Q"))
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
            testTable.getField(TestTable.TEST_KEY).setString("C");
            bSuccess = testTable.seek(">");
        } catch (DBException e)   {
            assertTrue("Error on seek next", false);
            cat.debug("Error: " + e.getMessage() + "");
            cat.debug(testTable.toString());
            System.exit(0);
        }
        if (bSuccess)
        {
            if (testTable.getField(TestTable.TEST_NAME).getString().startsWith("Q"))
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
            testTable.getField(TestTable.TEST_KEY).setString("B");
            testTable.getField(TestTable.TEST_CODE).setData(null);
            bSuccess = testTable.seek("<");
        } catch (DBException e)   {
            assertTrue("Error on seek <", false);
            cat.debug("Error: " + e.getMessage() + "");
            cat.debug(testTable.toString());
            System.exit(0);
        }
        if (bSuccess)
        {
            if (testTable.getField(TestTable.TEST_NAME).getString().startsWith("A"))
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
            testTable.getField(TestTable.TEST_KEY).setString("C");
            bSuccess = testTable.seek("<");
        } catch (DBException e)   {
            assertTrue("Error on seek <", false);
            cat.debug("Error: " + e.getMessage() + "");
            cat.debug(testTable.toString());
            System.exit(0);
        }
        if (bSuccess)
        {
            if (testTable.getField(TestTable.TEST_NAME).getString().startsWith("A"))
            {
                cat.debug("Success, found key");
                cat.debug(testTable.toString());
            }
            else if (testTable.getField(TestTable.kID).getString().equals("1"))
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
            testTable.getField(TestTable.TEST_KEY).setString("B");
            bSuccess = testTable.seek("<=");
        } catch (DBException e)   {
            assertTrue("Error on seek <=", false);
            cat.debug("Error: " + e.getMessage() + "");
            cat.debug(testTable.toString());
            System.exit(0);
        }
        if (bSuccess)
        {
            if (testTable.getField(TestTable.TEST_NAME).getString().startsWith("A"))
            {
                cat.debug("Success, found key");
                cat.debug(testTable.toString());
            }
            else if (testTable.getField(TestTable.kID).getString().equals("1"))
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
            testTable.getField(TestTable.TEST_KEY).setString("C");
            bSuccess = testTable.seek("<=");
        } catch (DBException e)   {
            assertTrue("Error on seek <=", false);
            cat.debug("Error: " + e.getMessage() + "");
            cat.debug(testTable.toString());
            System.exit(0);
        }
        if (bSuccess)
        {
            if (testTable.getField(TestTable.TEST_NAME).getString().startsWith("A"))
            {
                cat.debug("Success, found key");
                cat.debug(testTable.toString());
            }
            else if (testTable.getField(TestTable.kID).getString().equals("1"))
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
            testTable.setKeyArea("TestKey");
            testTable.close();
            while (testTable.hasNext())   {
                testTable.move(+1);
                if (testTable.getField(TestTable.TEST_NAME).getString().startsWith("B"))
                {
                    bookmark = testTable.getHandle(DBConstants.BOOKMARK_HANDLE);
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
            testTable.setHandle(bookmark, DBConstants.BOOKMARK_HANDLE);
            if (testTable.getField(TestTable.TEST_NAME).getString().startsWith("B"))
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

    BaseField fieldKey = new StringField(null, null, -1, null, null);
    fieldKey.setString("B");
    BaseField fieldID = new ReferenceField(null, null, -1, null, null);
    fieldID.setValue(2);

    cat.debug("Now, try to read through a range of records (Compare field!)");
    try   {     
        testTable.close();
        testTable.setKeyArea("PrimaryKey");
        iCount = 0;
//      FileListener listener = new CompareFileFilter(TestTable.TEST_KEY, "B", "=", null, false);
        FileListener listener = new CompareFileFilter(TestTable.TEST_KEY, fieldKey, "=", null, false);
        testTable.addListener(listener);
//      FileListener behavior2 = new CompareFileFilter(TestTable.kID, "2", ">", null, false);
        FileListener behavior2 = new CompareFileFilter(TestTable.ID, fieldID, ">", null, false);
        testTable.addListener(behavior2);
        testTable.open(); // Manually open it!
        testTable.close();
        while (testTable.hasNext())
        {
            testTable.move(+1);
            cat.debug(testTable.toString());
            iCount++;
        }
        testTable.removeListener(listener, true);
        testTable.removeListener(behavior2, true);
    } catch (DBException e)   {
        assertTrue("Error reading through file", false);
        cat.debug("Error reading through file: Error" + e.getMessage() + "");
    }

    cat.debug("Now, try to read through a range of records (Initial/End key)");
    try   {
        testTable.close();
        testTable.setKeyArea("TestKey");
        iCount = 0;
        FileListener listener = new StringSubFileFilter("B", TestTable.TEST_KEY, null, null, null, null);
        testTable.addListener(listener);
        testTable.close();
        while (testTable.hasNext())   {
            testTable.move(+1);
            cat.debug(testTable.toString());
            iCount++;
        }
        testTable.removeListener(listener, true);
    } catch (DBException e)   {
        assertTrue("Error reading through file", false);
        cat.debug("Error reading through file: Error" + e.getMessage() + "");
    }
    if (iCount == 3)
        cat.debug("Success, 3 records");
    else
    {
        assertTrue("Error wrong number of records", false);
        cat.debug("Error, found wrong number of records in recordset");
        System.exit(0);
    }


    cat.debug("Now, try to read through an empty range of records (Initial/End key)");
    try   {     
        testTable.close();
        testTable.setKeyArea("TestKey");
        iCount = 0;
        FileListener listener = new StringSubFileFilter("Z", TestTable.TEST_KEY, null, null, null, null);
        testTable.addListener(listener);
        testTable.close();
        while (testTable.hasNext())   {
            testTable.move(+1);
            cat.debug(testTable.toString());
            iCount++;
        }
        testTable.removeListener(listener, true);
    } catch (DBException e)   {
        assertTrue("Error reading through file", false);
        cat.debug("Error reading through file: Error" + e.getMessage() + "");
    }
    if (iCount == 0)
        cat.debug("Success, empty recordset");
    else
    {
        assertTrue("Error reading through file", false);
        cat.debug("Error, found records in empty recordset");
        System.exit(0);
    }

    cat.debug("Read through the first copy");
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
        assertTrue("Error reading through file", false);
        cat.debug("Error reading through file: Error" + e.getMessage() + "");
    }
    cat.debug("Primary Count: " + iCount + "");
    
    cat.debug("Now, start the concurrency testing");

    cat.debug("First, open a second copy of the file");
    TestTable testTable2 = new TestTableNoAuto(testTable.getRecordOwner());
    try   {
        testTable2.open();              // Open the table
    } catch (DBException e)   {
        String strError = e.getMessage();
        assertTrue("Error reading through file", false);
        cat.debug(strError);
        System.exit(0);
    }

    cat.debug("First, try some locking tests");
    cat.debug("Now lets try some deletes and reads");
    try   {     
        testTable.move(DBConstants.FIRST_RECORD);//+        testTable.moveFirst();
        testTable2.move(DBConstants.FIRST_RECORD);//+   testTable.moveFirst();
        testTable.move(+1);
        testTable2.move(+1);
        testTable.move(+1);
        testTable.remove();
        cat.debug("Try to move to a deleted record");
        testTable2.move(+1);
        cat.debug(testTable.toString());
    } catch (DBException ex)    {
        ex.printStackTrace();
        cat.debug("Error reading through file: Error" + ex.getMessage() + "");
        assertTrue("Error reading through file", false);
    }

    try   {     
        testTable.setKeyArea(DBConstants.MAIN_KEY_AREA);
        testTable2.setKeyArea(DBConstants.MAIN_KEY_AREA);
        testTable.getField(TestTable.kID).setString("2");
        bSuccess = testTable.seek("=");
        cat.debug("Seek file 1 - Primary: " + bSuccess + "");
        testTable2.getField(TestTable.kID).setString("2");
        bSuccess = testTable2.seek("=");
        cat.debug("Seek file 2 - Primary: " + bSuccess + "");
        cat.debug("Lock(edit) file 1");
        testTable.edit();
        cat.debug("Lock(edit) file 2");
        testTable2.edit();
        cat.debug("Update file 1");
        testTable.getField(TestTable.TEST_NAME).setString("B - Good Agent1");
        testTable.set();
        cat.debug("Update file 2");
        testTable2.getField(TestTable.TEST_NAME).setString("B - Good Agent2");
        testTable2.set();
    } catch (DBException e)   {
        cat.debug("Error reading through file (alternate success): Error" + e.getMessage() + "");
    }
    cat.debug("Locking tests done");

    cat.debug("Read through the second copy");
    try   {     
        testTable2.setKeyArea(DBConstants.MAIN_KEY_AREA);
        iCount = 0;
        testTable2.close();
        while (testTable2.hasNext())    {
            testTable2.move(+1);
            iCount++;
        }
    } catch (DBException e)   {
        assertTrue("Error reading through file", false);
        cat.debug("Error reading through file: Error" + e.getMessage() + "");
    }
    testTable2.free();
    testTable2 = null;
    cat.debug("Primary Count: " + iCount + "");

    cat.debug("Read through the first copy");
    try   {     
        testTable.setKeyArea(DBConstants.MAIN_KEY_AREA);
        iCount = 0;
        testTable.close();
        while (testTable.hasNext())   {
            testTable.move(+1);
            iCount++;
        }
    } catch (DBException e)   {
        assertTrue("Error reading through file", false);
        cat.debug("Error reading through file: Error" + e.getMessage() + "");
    }
    cat.debug("Primary Count: " + iCount + "");
    
    cat.debug("Read through the first copy using hasNext");
    try   {
        testTable.close();
        testTable.setKeyArea(DBConstants.MAIN_KEY_AREA);
        iCount = 0;
        while (testTable.hasNext())   {
            testTable.next();
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
     * Do the test.
     */
    public void testGridAccess()
    {
    //    PropertyConfigurator.configure("Simple");
     // get a category instance named "com.foo"
    Category cat = Category.getInstance(this.getClass().getName());

        this.addGridTestTableRecords(testTable);
// Now, start the tests....

        GridTable recordList = new GridTable(null, testTable);
        try   {
            boolean auto = false;
            if (testTable.getField(TestTable.kID) instanceof CounterField)
                auto = true;

            Record newRecord = null;
//          testTable.addListener(new CompareFileFilter(TestTable.TEST_KEY, "B", ">", null, false));

            recordList.close();
            cat.debug("First, lets read a record in the middle.\n");
            newRecord = (Record)recordList.get(4);
            cat.debug(testTable.toString());

            cat.debug("Next, lets read through the table using MoveTo.\n");
            int index = 0;
            for (index = 0; ; index++)
            {
                newRecord = (Record)recordList.get(index);
                if (newRecord == null)
                    break;
                cat.debug(newRecord.getField(0).toString());
            }
            cat.debug("Move to count: " + index);

            cat.debug("Now, let's access a record in the buffer.\n");
            newRecord = (Record)recordList.get(15);
            cat.debug(testTable.toString());

            cat.debug("Now, lets access a record in the record buffer.\n");
            newRecord = (Record)recordList.get(4);
            cat.debug(testTable.toString());
            cat.debug(", and update it.\n");
            newRecord.edit();
            newRecord.getField(TestTable.TEST_NAME).setString("New 4 Agent");
            newRecord.set();

            newRecord = (Record)recordList.get(4);
            cat.debug(", and update it again.\n");
            newRecord.edit();
            newRecord.getField(TestTable.TEST_NAME).setString("New 4 Agent Two");
            newRecord.set();

            cat.debug(testTable.toString());    // Make sure I get the right record

            for (index = 0; ; index++)
            {
                newRecord = (Record)recordList.get(index);
                if (newRecord == null)
                    break;
                cat.debug(newRecord.getField(0).toString());
            }
            cat.debug("Move to count: " + index);


            cat.debug("Now, lets access a record not in the record buffer.\n");
            newRecord = (Record)recordList.get(7);
            cat.debug(testTable.toString());

            cat.debug("Now, lets access a record not in any buffer.\n");
            newRecord = (Record)recordList.get(27);
            if (newRecord == null)
                cat.debug("Good, record doesn't exist");
            else
            {
                cat.debug("Error, returned crummy record");
                System.exit(0);
            }
            cat.debug("Now, lets access a record not in the record buffer.\n");
            newRecord = (Record)recordList.get(7);
            cat.debug(testTable.toString());
            cat.debug("Now, lets change some data and set.\n");
            newRecord.edit();
            newRecord.getField(TestTable.TEST_NAME).setString("New Agent");
            newRecord.set();
            cat.debug(testTable.toString());

            cat.debug("Now, lets access a record and delete.\n");
            newRecord = (Record)recordList.get(4);
            cat.debug(testTable.toString());
            newRecord.edit();
            newRecord.remove();
            cat.debug("Looks good!");
            
            cat.debug("Go through the new records");
            for (index = 0; ; index++)
            {
                newRecord = (Record)recordList.get(index);
                if (newRecord == null)
                    break;
                cat.debug(newRecord.getField(0).toString());
            }
            
            cat.debug("Adding a new record!");
            newRecord = recordList.getRecord();
            newRecord.addNew();
            if (!auto)
                newRecord.getField(TestTable.kID).setString("5");
            newRecord.getField(TestTable.TEST_NAME).setString("B - Good Agent");
            newRecord.getField(TestTable.TEST_KEY).setString("B");
            newRecord.add();
            
            cat.debug("Go through the new records after an addNew()");
            for (index = 0; ; index++)
            {
                newRecord = (Record)recordList.get(index);
                if (newRecord == null)
                    break;
                cat.debug(newRecord.getField(0).toString());
            }
            cat.debug("All done with that part!");
            
        } catch (DBException e)   {
            assertTrue("Error, reading through file: Error" + e.getMessage(), false);
            cat.debug("Error reading through file: Error" + e.getMessage() + "\n");
            cat.debug(testTable.toString());
            System.exit(0);
        }

        cat.debug("Test complete.\n");
    }
}
