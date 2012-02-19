/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.test.manual.test.db.thin;

//******************************************************************************
// Test the basic table functions (add, remove, move, etc.)
//******************************************************************************
import org.apache.log4j.Category;
import org.jbundle.test.manual.TestAll;
import org.jbundle.thin.app.test.test.db.TestTable;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.FieldList;
import org.jbundle.thin.base.db.FieldTable;
import org.jbundle.thin.base.screen.BaseApplet;


//org.apache.log4j.Category.getRoot().debug("Row: " + objData);
public abstract class ThinBaseDBTest extends DBBaseTest
{

    // Create the form
    public ThinBaseDBTest(String strTestName)
    {
        super(strTestName);
    }

    Category cat = null;
    FieldList record = null;
    FieldTable testTable = null;
    BaseApplet applet = null;
    /**
     * Setup.
     */
    public void setUp()
    {
        cat = Category.getInstance(ThinBaseDBTest.class.getName());

        BaseApplet.main(null);
        String[] args = {"remote=Client", "local=Client", "table=Client", "connectionType=proxy"};
        args = TestAll.fixArgs(args);
        applet = new BaseApplet(args);

        if (record == null)
            record = new TestTable(this);

        testTable = this.setupThinTable();
// Add all the test records   
        this.initTable(testTable);
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
    /**
     *  See if the data was correctly added.
     */
    public void verifyDataTest()
    {
        int iCount = 0;
        cat.debug("Count records.\n");
        try   {
//          testTable.setKeyArea(Constants.MAIN_KEY_AREA);
            iCount = 0;
            testTable.close();
            while (testTable.hasNext())   {
                testTable.next();
cat.debug(record.toString());
                iCount++;
                this.verifyRecord(record, iCount);
                assertTrue(((Integer)record.getField(0).getData()).intValue() == iCount);
            }
        } catch (Exception e) {
            fail("Record count error");
        }
        assertTrue(iCount == 6);

        cat.debug("Count records backwards.\n");
        iCount = 6;
        try   {
//          testTable.setKeyArea(Constants.MAIN_KEY_AREA);
            testTable.close();
            while (testTable.hasPrevious())   {
                testTable.previous();
                this.verifyRecord(record, iCount);
cat.debug(record.toString());
                assertTrue(((Integer)record.getField(0).getData()).intValue() == iCount);
                iCount--;
            }
        } catch (Exception e) {
            fail("Record count error");
        }
//      assertTrue(iCount == 0);

        cat.debug("Seek records.\n");
        iCount = 1;
        try   {
//          testTable.setKeyArea(Constants.MAIN_KEY_AREA);
            while (iCount < 10)   {
                record.getField(0).setData(new Integer(iCount));
                boolean bSuccess = testTable.seek("=");
                if (bSuccess)
                {
                assertTrue(iCount <= 6);
                this.verifyRecord(record, iCount);
cat.debug(record.toString());
                assertTrue(((Integer)record.getField(0).getData()).intValue() == iCount);
                }
                else
                {
cat.debug("" + iCount);
cat.debug(record.toString());
                    assertTrue(iCount > 6);
                }
                iCount++;
            }
        } catch (Exception e) {
            fail("Record count error");
        }
    }
    /**
     *  See if the data was correctly added.
     */
    public void verifyAddTest()
    {
        boolean bSuccess = false;
        int iCount = 0;
        try   {
            testTable.open();           // Open the table
        } catch (Exception e) {
            String strError = e.getMessage();
            cat.debug(strError);
            fail("Exception on open");
        }

// Try to add a duplicate
        try   {
            cat.debug("Try to add a duplicate record.\n");
            testTable.addNew();
            record.getField("ID").setString("4");
            record.getField("TestName").setString("F - Fam Trip Agent");
            record.getField("TestKey").setString("X");
            testTable.add(record);
            cat.debug("Error, duplicate not sensed\n");
            cat.debug(record.toString());
            assertTrue("Error, duplicate not sensed", false);
        } catch (Exception e) {
            cat.debug("Good, Duplicate sensed\n");
        }

        try   {
            cat.debug("Try to remove a record. First seek, then remove\n");
            testTable.addNew();
            record.getField("ID").setString("4");
            bSuccess = testTable.seek(null);
            assertTrue("Error fail on seek", bSuccess);
            
            cat.debug("Try to remove it.\n");
            testTable.remove();
            
            
            cat.debug("Try to add it back.\n");
            testTable.addNew();
            record.getField("ID").setString("4");
            record.getField("TestName").setString("F - Fam Trip Agent");
            record.getField("TestKey").setString("X");
            testTable.add(record);
            cat.debug("Good, added correctly\n");

        } catch (Exception e) {
            cat.debug("Error\n" + e.getMessage());
            fail("Error on del/add msg: " + e.getMessage());
        }

        cat.debug("Now, do some operations while reading through the file.\n");
        try   {
            testTable.close();
            while (testTable.hasNext())   {
                testTable.next();
                if (record.getField("ID").toString().equals("2"))
                {
                    testTable.edit();
                    record.getField("TestName").setString("B - Pretty Good Agent2");
                    cat.debug("Update record #2.\n");
                    testTable.set(record);
                }
                else if (record.getField("ID").getString().equals("3"))
                {
                    cat.debug("Delete record #3.\n");
                    testTable.remove();
                }
    // Try to update a duplicate
                else if (record.getField("ID").getString().equals("4"))
                {
                    testTable.edit();
                    record.getField("ID").setString("1");
                    cat.debug("Try to update record #4. (Duplicate key).\n");
                    testTable.set(record);
                }
            }
            cat.debug("Error, duplicate not sensed\n");
            cat.debug(record.toString());
            assertTrue("Error, duplicate not sensed\n", false);
        } catch (Exception e) {
            cat.debug("Good, Duplicate sensed\n");
        }

    cat.debug("Now, try to read through all the records\n");
    try   {     
//      testTable.setKeyArea("PrimaryKey");
        iCount = 0;
        testTable.open(); // Manually open it!
        testTable.close();
        while (testTable.hasNext())   {
//          cat.debug(record.toString());
            testTable.next();
            iCount++;
        }
    } catch (Exception e) {
        cat.debug("Error reading through file: Error" + e.getMessage() + "\n");
    }
    cat.debug("Primary Count: " + iCount + "\n");
    assertTrue("Didn't remove a record", iCount == 5);


        cat.debug("Now, let's seek a record in the PrimaryKey.\n");
        bSuccess = false;
        try   {
//          testTable.setKeyArea("PrimaryKey");
//+         testTable.addNew();
            record.getField("ID").setString("2");
            bSuccess = testTable.seek("=");
        } catch (Exception e) {
            cat.debug("Error: " + e.getMessage() + "\n");
            cat.debug(record.toString());
            fail("Error on seek");
        }
        if (bSuccess)
        {
            cat.debug("Success\n");
            cat.debug(record.toString());
            assertTrue("Record not updated correctly", record.getField("TestName").getString().equals("B - Pretty Good Agent2"));

        }
        else
        {
            cat.debug("Error, not found\n");
            cat.debug(record.toString());
            fail("Error on seek");
        }

        cat.debug("Now, let's seek a record in the PrimaryKey that doesn't exist.\n");
        bSuccess = false;
        try   {
//          testTable.setKeyArea("PrimaryKey");
            testTable.addNew();
            record.getField("ID").setString("3");
            bSuccess = testTable.seek("=");
        } catch (Exception e) {
            cat.debug("Error: " + e.getMessage() + "\n");
            cat.debug(record.toString());
            fail("Error on seek");
        }
        if (bSuccess)
        {
            cat.debug("Error, found key\n");
            cat.debug(record.toString());
            fail("Error on seek");
        }
        else
        {
            cat.debug("Good, not found\n");
        }

        cat.debug("Count records backwards and verify the records.\n");
        iCount = 6;
        try   {
//          testTable.setKeyArea(Constants.MAIN_KEY_AREA);
            testTable.close();
            while (testTable.hasPrevious())   {
                testTable.previous();
                if (iCount == 3)
                    iCount = 2;   // Record three was deleted
                this.verifyRecord(record, iCount);
cat.debug(record.toString());
                assertTrue(((Integer)record.getField(0).getData()).intValue() == iCount);
                iCount--;
            }
        } catch (Exception e) {
            fail("Record count error");
        }
//      assertTrue(iCount == 0);
    }
    /**
     *  Test the grid access. This test is especially helpful for testing the caching class.
     */
    public void gridAccessTest()
    {
        boolean bSuccess = false;
        int iCount = 0;

        try   {
            testTable.open();           // Open the table
        } catch (Exception e) {
            String strError = e.getMessage();
            cat.debug(strError);
            fail();
        }

// Add all the test records   
        cat.debug("Count keys in both indexes.\n");
        try   {
//          testTable.setKeyArea(Constants.MAIN_KEY_AREA);
            iCount = 0;
            testTable.close();
            while (testTable.hasNext())   {
                testTable.next();
                iCount++;
            }
            cat.debug("Main index: Count: " + iCount + "\n");
        } catch (Exception e) {
            cat.debug("Error: " + e.getMessage() + "\n");
            cat.debug(record.toString());
            fail();
        }

        cat.debug("Now, let's Test the GET cache.\n");
        cat.debug("Now, let's seek a record in the PrimaryKey.\n");
        testTable.close();
        bSuccess = false;
        try   {
//          testTable.setKeyArea("PrimaryKey");
//+         testTable.addNew();
            bSuccess = (testTable.get(2) != null);
        } catch (Exception e) {
            cat.debug("Error: " + e.getMessage() + "\n");
            cat.debug(record.toString());
            fail();
        }
        if (bSuccess)
        {
            cat.debug("Success\n");
            cat.debug(record.toString());
        }
        else
        {
            cat.debug("Error, not found\n");
            cat.debug(record.toString());
            fail();
        }

        cat.debug("Now, let's seek another record in the PrimaryKey.\n");
        bSuccess = false;
        try   {
            bSuccess = (testTable.get(3) != null);
        } catch (Exception e) {
            cat.debug("Error: " + e.getMessage() + "\n");
            cat.debug(record.toString());
            fail();
        }
        if (bSuccess)
        {
            cat.debug("Good, found key\n");
        }
        else
        {
            cat.debug("Error, not found\n");
            cat.debug(record.toString());
            fail();
        }
    
        cat.debug("Now, let's seek the original record in the PrimaryKey.\n");
        bSuccess = false;
        try   {
            bSuccess = (testTable.get(2) != null);
        } catch (Exception e) {
            cat.debug("Error: " + e.getMessage() + "\n");
            cat.debug(record.toString());
            fail();
        }

        cat.debug("Change a field and update it.\n");
        try   {
            testTable.edit();
            record.getField("TestName").setString("New Name");
            testTable.set(record);
        } catch (Exception ex)  {
            ex.printStackTrace();
            cat.debug("Error: " + ex.getMessage() + "\n");
            cat.debug(record.toString());
            fail();
        }

        cat.debug("Delete it.\n");
        record.getField("TestName").setString("New Name");
        try   {
            bSuccess = (testTable.get(2) != null);
            testTable.edit();
            testTable.remove();
        } catch (Exception e) {
            cat.debug("Error: " + e.getMessage() + "\n");
            cat.debug(record.toString());
            fail();
        }

        cat.debug("Try to access it again\n");
        bSuccess = false;
        try   {
            testTable.get(2);
            bSuccess = (testTable.getRecord().getEditMode() == Constants.EDIT_IN_PROGRESS);
        } catch (Exception ex)  {
            ex.printStackTrace();
            cat.debug("Error: " + ex.getMessage() + "\n");
            cat.debug(record.toString());
            fail();
        }
        if (bSuccess)
        {
            cat.debug("Error, found key\n");
            cat.debug(record.toString());
            fail();
        }
        else
        {
            cat.debug("Good, not found\n");
        }

        cat.debug("Test complete.\n");

    }
    /**
     *  The main() method acts as the applet's entry point when it is run
     *  as a standalone application. It is ignored if the applet is run from
     *  within an HTML page.
     */
    public void cacheSeekTest()
    {
        boolean bSuccess = false;
        int iCount = 0;


        cat.debug("Count keys in both indexes.\n");
        try   {
//          testTable.setKeyArea(Constants.MAIN_KEY_AREA);
            iCount = 0;
            testTable.close();
            while (testTable.hasNext())   {
                testTable.next();
                iCount++;
            }
            cat.debug("Main index: Count: " + iCount + "\n");
        } catch (Exception e) {
            cat.debug("Error: " + e.getMessage() + "\n");
            cat.debug(record.toString());
            fail();
        }

        cat.debug("Now, let's Test the SEEK cache.\n");
        cat.debug("Now, let's seek a record in the PrimaryKey.\n");
        testTable.close();
        bSuccess = false;
        try   {
//          testTable.setKeyArea("PrimaryKey");
//+         testTable.addNew();
            record.getField("ID").setString("2");
            bSuccess = testTable.seek("=");
        } catch (Exception e) {
            cat.debug("Error: " + e.getMessage() + "\n");
            cat.debug(record.toString());
            fail();
        }
        if (bSuccess)
        {
            cat.debug("Success\n");
            cat.debug(record.toString());
        }
        else
        {
            cat.debug("Error, not found\n");
            cat.debug(record.toString());
            fail();
        }

        cat.debug("Now, let's seek another record in the PrimaryKey.\n");
        bSuccess = false;
        try   {
//          testTable.setKeyArea("PrimaryKey");
            testTable.addNew();
            record.getField("ID").setString("3");
            bSuccess = testTable.seek("=");
        } catch (Exception e) {
            cat.debug("Error: " + e.getMessage() + "\n");
            cat.debug(record.toString());
            fail();
        }
        if (bSuccess)
        {
            cat.debug("Good, found key\n");
        }
        else
        {
            cat.debug("Error, not found\n");
            cat.debug(record.toString());
            fail();
        }
    
        cat.debug("Now, let's seek the original record in the PrimaryKey.\n");
        bSuccess = false;
        try   {
//          testTable.setKeyArea("PrimaryKey");
//+         testTable.addNew();
            record.getField("ID").setString("2");
            bSuccess = testTable.seek("=");
        } catch (Exception e) {
            cat.debug("Error: " + e.getMessage() + "\n");
            cat.debug(record.toString());
            fail();
        }

        cat.debug("Change a field and update it.\n");
        record.getField("TestName").setString("New Name");
        try   {
            testTable.edit();
            testTable.set(record);
        } catch (Exception e) {
            cat.debug("Error: " + e.getMessage() + "\n");
            cat.debug(record.toString());
            fail();
        }

        cat.debug("Delete it.\n");
        record.getField("TestName").setString("New Name");
        try   {
            record.getField("ID").setString("2");
            bSuccess = testTable.seek("=");
            testTable.edit();
            testTable.remove();
        } catch (Exception e) {
            cat.debug("Error: " + e.getMessage() + "\n");
            cat.debug(record.toString());
            fail();
        }

        cat.debug("Try to access it again\n");
        bSuccess = false;
        try   {
            testTable.addNew();
            record.getField("ID").setString("2");
            bSuccess = testTable.seek("=");
        } catch (Exception e) {
            cat.debug("Error: " + e.getMessage() + "\n");
            cat.debug(record.toString());
            fail();
        }
        if (bSuccess)
        {
            cat.debug("Error, found key\n");
            cat.debug(record.toString());
            fail();
        }
        else
        {
            cat.debug("Good, not found\n");
        }

        cat.debug("Test complete.\n");

    }
    /**
     *
     */
    public void testSecondary()
    {
        boolean bSuccess = false;
        int iCount = 0;
        cat.debug("Now, let's seek a record in the SecondaryKey. (Not unique)\n");
        try   {
//          testTable.setKeyArea("TestKey");
            testTable.addNew();
            record.getField("TestKey").setString("B");
            bSuccess = testTable.seek("=");
        } catch (Exception e) {
            cat.debug("Error: " + e.getMessage() + "\n");
            cat.debug(record.toString());
            fail("Error on seek secondary");
        }
        if (bSuccess)
        {
            if ((record.getField("ID").getString().equals("2"))
                || (record.getField("ID").getString().equals("4"))
                || (record.getField("ID").getString().equals("5")))
            {
                cat.debug("Good, found first key\n");
                cat.debug(record.toString());
            }
            else
            {
                cat.debug("Error, found wrong key\n");
                cat.debug(record.toString());
                fail("Error wrong key found");
            }
        }
        else
        {
            cat.debug("Error, not found\n");
            cat.debug(record.toString());
            fail("Error not found");
        }

        cat.debug("Now, let's seek a record in the SecondaryKey that doesn't exist\n");
        try   {
            testTable.addNew();
            record.getField("TestKey").setString("X");
            bSuccess = testTable.seek("=");
        } catch (Exception e) {
            cat.debug("Error: " + e.getMessage() + "\n");
            cat.debug(record.toString());
            fail("Error on seek");
        }
        if (bSuccess)
        {
            cat.debug("Error, found key\n");
            cat.debug(record.toString());
            fail("Error on seek");
        }
        else
        {
            cat.debug("Good, not found\n");
            cat.debug(record.toString());
        }

        cat.debug("Now, let's seek a the next >= record that does exist\n");
        try   {
            testTable.addNew();
            record.getField("TestKey").setString("B");
            bSuccess = testTable.seek(">=");
        } catch (Exception e) {
            cat.debug("Error: " + e.getMessage() + "\n");
            cat.debug(record.toString());
            fail("Error on seek");
        }
        if (bSuccess)
        {
            if ((record.getField("ID").getString().equals("2"))
                || (record.getField("ID").getString().equals("4"))
                || (record.getField("ID").getString().equals("5")))
            {
                cat.debug("Success, found key\n");
                cat.debug(record.toString());
            }
            else
            {
                cat.debug("Error, found wrong key\n");
                cat.debug(record.toString());
                fail("Error on seek");
            }
        }
        else
        {
            cat.debug("Error, not found\n");
            cat.debug(record.toString());
            fail("Error on seek");
        }

        cat.debug("Now, let's seek a the next >= record that doesn't exist\n");
        try   {
            testTable.addNew();
            record.getField("TestKey").setString("C");
            bSuccess = testTable.seek(">=");
        } catch (Exception e) {
            cat.debug("Error: " + e.getMessage() + "\n");
            cat.debug(record.toString());
            fail("Error on seek");
        }
        if (bSuccess)
        {
            if (record.getField("ID").getString().equals("6"))
            {
                cat.debug("Success, found key\n");
                cat.debug(record.toString());
            }
            else
            {
                cat.debug("Error, found wrong key\n");
                cat.debug(record.toString());
                fail("Error found wrong key");
            }
        }
        else
        {
            cat.debug("Error, not found\n");
            cat.debug(record.toString());
            fail("Error, not found");
        }

        cat.debug("Now, let's seek a the next > record that does exist\n");
        try   {
            testTable.addNew();
            record.getField("TestKey").setString("B");
            bSuccess = testTable.seek(">");
        } catch (Exception e) {
            cat.debug("Error: " + e.getMessage() + "\n");
            cat.debug(record.toString());
            fail("Error on seek");
        }
        if (bSuccess)
        {
            if (record.getField("ID").getString().equals("6"))
            {
                cat.debug("Success, found key\n");
                cat.debug(record.toString());
            }
            else
            {
                cat.debug("Error, found wrong key\n");
                cat.debug(record.toString());
                fail("Error, found wrong key");
            }
        }
        else
        {
            cat.debug("Error, not found\n");
            cat.debug(record.toString());
            fail("Error, not found");
        }

        cat.debug("Now, let's seek a the next > record that doesn't exist\n");
        try   {
            testTable.addNew();
            record.getField("TestKey").setString("C");
            bSuccess = testTable.seek(">");
        } catch (Exception e) {
            cat.debug("Error: " + e.getMessage() + "\n");
            cat.debug(record.toString());
            fail("Error on seek");
        }
        if (bSuccess)
        {
            if (record.getField("ID").getString().equals("6"))
            {
                cat.debug("Success, found key\n");
                cat.debug(record.toString());
            }
            else
            {
                cat.debug("Error, found wrong key\n");
                cat.debug(record.toString());
                fail("Error on seek");
            }
        }
        else
        {
            cat.debug("Error, not found\n");
            cat.debug(record.toString());
            fail("Error on seek");
        }

        cat.debug("Now, let's seek a the next < record that does exist\n");
        try   {
            testTable.addNew();
            record.getField("TestKey").setString("B");
            bSuccess = testTable.seek("<");
        } catch (Exception e) {
            cat.debug("Error: " + e.getMessage() + "\n");
            cat.debug(record.toString());
            fail("Error on seek");
        }
        if (bSuccess)
        {
            if (record.getField("ID").getString().equals("1"))
            {
                cat.debug("Success, found key\n");
                cat.debug(record.toString());
            }
            else
            {
                cat.debug("Error, found wrong key\n");
                cat.debug(record.toString());
                fail("Error on seek");
            }
        }
        else
        {
            cat.debug("Error, not found\n");
            cat.debug(record.toString());
            fail("Error on seek");
        }

        cat.debug("Now, let's seek a the next < record that doesn't exist\n");
        try   {
            testTable.addNew();
            record.getField("TestKey").setString("C");
            bSuccess = testTable.seek("<");
        } catch (Exception e) {
            cat.debug("Error: " + e.getMessage() + "\n");
            cat.debug(record.toString());
            fail("Error on seek");
        }
        if (bSuccess)
        {
            if (record.getField("ID").getString().equals("5"))
            {
                cat.debug("Success, found key\n");
                cat.debug(record.toString());
            }
            else if (record.getField("ID").getString().equals("1"))
            {
                cat.debug("Alternate Success, found key\n");
                cat.debug(record.toString());
            }
            else
            {
                cat.debug("Error, found wrong key\n");
                cat.debug(record.toString());
                fail("Error on seek");
            }
        }
        else
        {
            cat.debug("Error, not found\n");
            cat.debug(record.toString());
            fail("Error on seek");
        }

        cat.debug("Now, let's seek a the next <= record that does exist\n");
        try   {
            testTable.addNew();
            record.getField("TestKey").setString("B");
            bSuccess = testTable.seek("<=");
        } catch (Exception e) {
            cat.debug("Error: " + e.getMessage() + "\n");
            cat.debug(record.toString());
            fail("Error on seek");
        }
        if (bSuccess)
        {
            if (record.getField("ID").getString().equals("2"))
            {
                cat.debug("Success, found key\n");
                cat.debug(record.toString());
            }
            else if (record.getField("ID").getString().equals("1"))
            {
                cat.debug("Alternate Success, found key\n");
                cat.debug(record.toString());
            }
            else
            {
                cat.debug("Error, found wrong key\n");
                cat.debug(record.toString());
                fail("Error on seek");
            }
        }
        else
        {
            cat.debug("Error, not found\n");
            cat.debug(record.toString());
            fail("Error on seek");
        }

        cat.debug("Now, let's seek a the next <= record that doesn't exist\n");
        try   {
            testTable.addNew();
            record.getField("TestKey").setString("C");
            bSuccess = testTable.seek("<=");
        } catch (Exception e) {
            cat.debug("Error: " + e.getMessage() + "\n");
            cat.debug(record.toString());
            fail("Error on seek");
        }
        if (bSuccess)
        {
            if (record.getField("ID").getString().equals("5"))
            {
                cat.debug("Success, found key\n");
                cat.debug(record.toString());
            }
            else if (record.getField("ID").getString().equals("1"))
            {
                cat.debug("Alternate Success, found key\n");
                cat.debug(record.toString());
            }
            else
            {
                cat.debug("Error, found wrong key\n");
                cat.debug(record.toString());
                fail("Error on seek");
            }
        }
        else
        {
            cat.debug("Error, not found\n");
            cat.debug(record.toString());
            fail("Error on seek");
        } 
    }
    /**
     *
     */
    public void testBookmarks()
    {
        boolean bSuccess = false;
        int iCount = 0;

        cat.debug("Now, Test the bookmark properties.\n");
        cat.debug("First, read through the file using the non unique key and get a bookmark.\n");
        Object bookmark = null;

    cat.debug("Read through the first copy\n");
    try   {     
//      testTable.setKeyArea(Constants.MAIN_KEY_AREA);
        iCount = 0;
        testTable.close();
        while (testTable.hasNext())   {
            testTable.next();
            iCount++;
        }
    } catch (Exception e) {
        cat.debug("Error reading through file: Error" + e.getMessage() + "\n");
        fail("Error reading through file");
    }
    cat.debug("Primary Count: " + iCount + "\n");
    
    cat.debug("Read through the first copy using hasNext\n");
    try   {
        testTable.close();
//      testTable.setKeyArea(Constants.MAIN_KEY_AREA);
        iCount = 0;
        while (testTable.hasNext())   {
            testTable.next();
            iCount++;
            cat.debug(testTable);
        }
    } catch (Exception e) {
        cat.debug("Error reading through file: Error" + e.getMessage() + "\n");
    }
    cat.debug("Has Next Primary Count: " + iCount + "\n");

    cat.debug("Test complete.\n");

    }
}
