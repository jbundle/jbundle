/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.test.manual.test.db.thin;

//******************************************************************************
// Test the basic table functions (add, remove, move, etc.)
//******************************************************************************
import java.util.Date;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.Category;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.FieldList;
import org.jbundle.thin.base.db.FieldTable;


// SimpleForm is the data entry form for the sample
public class DBBaseTest extends TestCase
{
Date date = null;
Category cat = null;

    // Create the form
    public DBBaseTest(String strTestName)
    {
        super(strTestName);
    }
    public static Test suite()
    {
        TestSuite suite= new TestSuite();     
        // Hack - Get rid of mvn test error
        return suite;
    }
    public void initTable(FieldTable testTable)
    {
        cat = Category.getInstance(DBBaseTest.class.getName());
        FieldList record = testTable.getRecord();
        boolean bSuccess = false;
        int iCount = 0;
        cat.debug("Delete all old records.\n");
        iCount = 0;
        try   {
            testTable.close();
            testTable.open();
            while (testTable.hasNext())
            {
                testTable.next();
                testTable.remove();
                iCount++;
            }
        } catch (Exception e) {
            cat.debug("Could not delete record: Error" + e.getMessage() + "\n");
            cat.debug(record.toString());
            fail("Error deleting records. Error: " + e.getMessage());
        }
        cat.debug("deleted records " + iCount + "\n");

        try   {
System.out.println("0");
            cat.debug("Add records.\n");
            testTable.addNew();
System.out.println("0.1");
            
date = new Date();
            record.getField("ID").setString("1");
            record.getField("TestName").setString("A - Excellent Agent");
            record.getField("TestMemo").setString("This is a very long line\nThis is the second line.");
            record.getField("TestYesNo").setState(true);
System.out.println("0.3");
            record.getField("TestLong").setValue(15000000);
            record.getField("TestShort").setValue(14000);
            record.getField("TestDateTime").setData(date);
System.out.println("0.6");
            record.getField("TestDate").setData(date, Constants.DISPLAY, Constants.SCREEN_MOVE);
            record.getField("TestTime").setString("5:15 PM");
            record.getField("TestFloat").setValue(1234.56);
System.out.println("0.7");
            record.getField("TestDouble").setValue(1234567.8912);
            record.getField("TestPercent").setValue(34.56);
            record.getField("TestReal").setValue(5432.432);
            record.getField("TestCurrency").setValue(1234567.89);
            record.getField("TestKey").setString("A");
cat.debug(record.toString());
System.out.println("0.9");
            testTable.add(record);
System.out.println("1");
            testTable.addNew();
            record.getField("ID").setString("2");
            record.getField("TestName").setString("B - Good Agent");
            record.getField("TestKey").setString("B");
            testTable.add(record);
System.out.println("2");
            testTable.addNew();
            record.getField("ID").setString("3");
            record.getField("TestName").setString("C - Average Agent");
            record.getField("TestKey").setString("C");
            testTable.add(record);
System.out.println("3");
            testTable.addNew();
            record.getField("ID").setString("4");
            record.getField("TestName").setString("F - Fam Trip Agent");
            record.getField("TestKey").setString("B");
            testTable.add(record);
System.out.println("4");
            testTable.addNew();
            record.getField("ID").setString("5");
            record.getField("TestName").setString("T - Tour Operator");
            record.getField("TestKey").setString("B");
            testTable.add(record);
System.out.println("5");
            testTable.addNew();
            record.getField("ID").setString("6");
            record.getField("TestName").setString("Q - Q Agency");
            record.getField("TestKey").setString("Q");
            testTable.add(record);
            cat.debug("6 records added.\n");
System.out.println("6");
        } catch (Exception e) {
            cat.debug("Error adding record. Error: " + e.getMessage() + "\n");
            cat.debug(record.toString());
            fail("Error adding record. Error: " + e.getMessage());
        }

        cat.debug("Count keys in both indexes.\n");
        try   {
//          testTable.setKeyArea(Constants.MAIN_KEY_AREA);
            iCount = 0;
            testTable.close();
            while (testTable.hasNext())   {
                testTable.next();
cat.debug(record.toString());
                iCount++;
            }
        } catch (Exception ex)  {
            ex.printStackTrace();
            fail("Record count error");
        }

    }
    /**
     * Verify the record's value
     */
    public void verifyRecord(FieldList record, int iID)
    {
cat.debug(record.getField("ID").getValue() + " == " + iID);
        assertTrue(record.getField("ID").getValue() == iID);
        if (iID == 1)
        {
            assertTrue("A - Excellent Agent".equals(record.getField("TestName").toString()));
            assertTrue("This is a very long line\nThis is the second line.".equals(record.getField("TestMemo").toString()));
            assertTrue(record.getField("TestYesNo").getState());
            assertTrue(record.getField("TestLong").getValue() == 15000000);
            assertTrue(record.getField("TestShort").getValue() == 14000);
cat.debug(record.getField("TestDateTime").getData());
cat.debug(record.getField("TestDate").getData());
cat.debug(record.getField("TestTime").getData());
cat.debug("" + record.getField("TestFloat").getValue());
cat.debug("" + record.getField("TestDouble").getValue());
cat.debug("date " + date);
//          assertTrue(date.equals(record.getFieldInfo("TestDateTime").getData()));
//          assertTrue(date.equals(record.getFieldInfo("TestDate").getData()));
//          record.getFieldInfo("TestTime").setString("5:15 PM");
//          assertTrue(record.getFieldInfo("TestFloat").getValue() == 1234.56);
//          assertTrue(record.getFieldInfo("TestDouble").getValue() == 1234567.8912);
//          assertTrue(record.getFieldInfo("TestPercent").getValue() == 34.56);
            assertTrue(record.getField("TestReal").getValue() == 5432.432);
            assertTrue(record.getField("TestCurrency").getValue() == 1234567.89);
            assertTrue("A".equals(record.getField("TestKey").toString()));
        }
    }
}
