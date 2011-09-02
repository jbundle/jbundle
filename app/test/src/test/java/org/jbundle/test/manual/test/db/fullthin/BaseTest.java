package org.jbundle.test.manual.test.db.fullthin;

//******************************************************************************
// Test the basic table functions (add, remove, move, etc.)
//******************************************************************************
import java.util.Calendar;
import java.util.Date;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.Category;
import org.jbundle.model.DBException;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.Converter;
import org.jbundle.thin.base.db.FieldList;
import org.jbundle.thin.base.db.FieldTable;


/**
 * BaseTest is the standard code to set for all the other tests.
 * @author  don
 * @version 
 */
public class BaseTest extends TestCase
{
Date date = null;
Category cat = null;

    /**
      * Creates new TestAll.
      */
    public BaseTest(String strTestName)
    {
        super(strTestName);
    }
    public static Test suite()
    {
        TestSuite suite= new TestSuite();     
        // Hack - Get rid of mvn test error
        return suite;
    }
    /**
     * Add the test table records.
     */
    public void addTestTableRecords(FieldTable testTable)
    {
        cat = Category.getInstance(BaseTest.class.getName());
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
        } catch (Exception ex)  {
            ex.printStackTrace();
            cat.debug("Could not delete record: Error" + ex.getMessage() + "\n");
            cat.debug(record.toString());
            fail("Error deleting records. Error: " + ex.getMessage());
        }
        cat.debug("deleted records " + iCount + "\n");

        try   {
            cat.debug("Add records.\n");
            testTable.addNew();
            
date = new Date();
            record.getField("ID").setString("1");
            record.getField("TestName").setString("A - Excellent Agent");
            record.getField("TestMemo").setString("This is a very long line\nThis is the second line.");
            record.getField("TestYesNo").setState(true);
            record.getField("TestLong").setValue(15000000);
            record.getField("TestShort").setValue(14000);
            record.getField("TestDateTime").setData(date);
            record.getField("TestDate").setData(date, Constants.DISPLAY, Constants.SCREEN_MOVE);
            String strDate = record.getField("TestDate").toString();
            record.getField("TestDate").setString(strDate);     // Make sure this is date only
            record.getField("TestTime").setString("17:15");
            if (record.getField("TestTime").isNull())
                record.getField("TestTime").setString("5:15 PM");
            record.getField("TestFloat").setValue(1234.56);
            record.getField("TestDouble").setString("1234567.8912");
            if (record.getField("TestDouble").getValue() > 1234568)
                record.getField("TestDouble").setString("1.234.567,8912");
            record.getField("TestPercent").setValue(34.56);
            record.getField("TestReal").setValue(5432.432);
            record.getField("TestCurrency").setValue(1234567.89);
            record.getField("TestKey").setString("A");
cat.debug(record.toString());
            testTable.add(record);

            testTable.addNew();
            record.getField("ID").setString("2");
            record.getField("TestName").setString("B - Good Agent");
            record.getField("TestKey").setString("B");
            testTable.add(record);

            testTable.addNew();
            record.getField("ID").setString("3");
            record.getField("TestName").setString("C - Average Agent");
            record.getField("TestKey").setString("C");
            testTable.add(record);

            testTable.addNew();
            record.getField("ID").setString("4");
            record.getField("TestName").setString("F - Fam Trip Agent");
            record.getField("TestKey").setString("B");
            testTable.add(record);

            testTable.addNew();
            record.getField("ID").setString("5");
            record.getField("TestName").setString("T - Tour Operator");
            record.getField("TestKey").setString("B");
            testTable.add(record);

            testTable.addNew();
            record.getField("ID").setString("6");
            record.getField("TestName").setString("Q - Q Agency");
            record.getField("TestKey").setString("Q");
            testTable.add(record);
            cat.debug("6 records added.\n");
        } catch (Exception ex)  {
            ex.printStackTrace();
            cat.debug("Error adding record. Error: " + ex.getMessage() + "\n");
            cat.debug(record.toString());
            fail("Error adding record. Error: " + ex.getMessage());
        }

        cat.debug("Count keys.\n");
        try   {
            record.setKeyArea(Constants.PRIMARY_KEY);
            iCount = 0;
            testTable.close();
            while (testTable.hasNext())
            {
                testTable.next();
cat.debug(record.toString());
                iCount++;
                this.verifyRecord(record, iCount);
            }
        } catch (Exception ex)  {
            ex.printStackTrace();
            fail("Record count error");
        }
        assertTrue("Count is not = 6; count = " + iCount, (iCount == 6));

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
            Converter.gCalendar.setTime(date);
            Converter.gCalendar.set(Calendar.MILLISECOND, 0);
            Date dateTime = Converter.gCalendar.getTime();
            Converter.gCalendar.set(Calendar.HOUR_OF_DAY, 0);
            Converter.gCalendar.set(Calendar.MINUTE, 0);
            Converter.gCalendar.set(Calendar.SECOND, 0);
            Date dateOnly = Converter.gCalendar.getTime();
            Converter.gCalendar.setTime(date);
            Converter.gCalendar.set(Calendar.MILLISECOND, 0);
            Converter.gCalendar.set(Calendar.YEAR, Constants.FIRST_YEAR);
            Converter.gCalendar.set(Calendar.MONTH, Calendar.JANUARY);
            Converter.gCalendar.set(Calendar.DATE, 1);
            Converter.gCalendar.set(Calendar.HOUR_OF_DAY, 5 + 12);
            Converter.gCalendar.set(Calendar.MINUTE, 15);
            Converter.gCalendar.set(Calendar.SECOND, 0);
            Date timeOnly = Converter.gCalendar.getTime();

            assertTrue((dateTime.equals(record.getField("TestDateTime").getData()))
                || (date.equals(record.getField("TestDateTime").getData())));
            assertTrue(dateOnly.equals(record.getField("TestDate").getData()));
            assertTrue(timeOnly.equals(record.getField("TestTime").getData()));
            assertTrue((int)(record.getField("TestFloat").getValue() * 100) == 1234.56 * 100);
            assertTrue((long)(record.getField("TestDouble").getValue() * 100) == (long)(1234567.89 * 100));   // 2 digits
            assertTrue((int)(record.getField("TestPercent").getValue() * 100) == 34.56 * 100);
            assertTrue(record.getField("TestReal").getValue() == 5432.432);
            assertTrue(record.getField("TestCurrency").getValue() == 1234567.89);
            assertTrue("A".equals(record.getField("TestKey").toString()));
        }
        if (iID == 2)
        {
            assertTrue("B - Good Agent".equals(record.getField("TestName").toString()));
            assertTrue("B".equals(record.getField("TestKey").toString()));
        }
    }
    /** 
     * Add the test table records.
     */
    public void addGridTestTableRecords(FieldTable testTable)
    {
        Category cat = Category.getInstance(BaseTest.class.getName());
        FieldList record = testTable.getRecord();

        int iCount = 0;
        cat.debug("Begin Test\n");
        cat.debug("Open table.\n");
        try   {
            testTable.open();           // Open the table
        } catch (DBException e)   {
            String strError = e.getMessage();
            cat.debug(strError);
            fail("Error open");
        }

        
        try   {     
            record.setKeyArea(Constants.PRIMARY_KEY);
            iCount = 0;
            testTable.close();
            while (testTable.hasNext())   {
                testTable.move(+1);
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
            while (testTable.hasNext())   {
                testTable.move(+1);
                    testTable.remove();
                iCount++;
            }
        } catch (DBException e)   {
            cat.debug("Could not delete record: Error" + e.getMessage() + "\n");
            cat.debug(testTable.toString());
            fail("Error on delete");
        }
        cat.debug("deleted records " + iCount + "\n");

        try   {
            cat.debug("Add records.\n");

//?         testTable.setOpenMode(Constants.OPEN_REFRESH_AND_LOCK_ON_CHANGE);    // Make sure keys are updated before sync

            testTable.addNew();
            record.getField("ID").setString("1");
            record.getField("TestName").setString("A - Excellent Agent");
            record.getField("TestKey").setString("A");
            String str = record.getField("ID").getString();
            testTable.add(record);

            testTable.addNew();
            record.getField("ID").setString("2");
            record.getField("TestName").setString("B - Good Agent");
            record.getField("TestKey").setString("B");
            str = record.getField("ID").getString();
            testTable.add(record);

            testTable.addNew();
            record.getField("ID").setString("3");
            record.getField("TestName").setString("C - Average Agent");
            record.getField("TestKey").setString("C");
            testTable.add(record);

            testTable.addNew();
            record.getField("ID").setString("4");
            record.getField("TestName").setString("F - Fam Trip Agent");
            record.getField("TestKey").setString("B");
            testTable.add(record);

            testTable.addNew();
            record.getField("ID").setString("5");
            record.getField("TestName").setString("T - Tour Operator");
            record.getField("TestKey").setString("B");
            testTable.add(record);

            testTable.addNew();
            record.getField("ID").setString("6");
            record.getField("TestName").setString("6 - Q Agency");
            record.getField("TestKey").setString("Q");
            testTable.add(record);

            testTable.addNew();
            record.getField("ID").setString("7");
            record.getField("TestName").setString("7 - Q Agency");
            record.getField("TestKey").setString("G");
            testTable.add(record);

            testTable.addNew();
            record.getField("ID").setString("8");
            record.getField("TestName").setString("8 - H Agency");
            record.getField("TestKey").setString("H");
            testTable.add(record);

            testTable.addNew();
            record.getField("ID").setString("9");
            record.getField("TestName").setString("9 - I Agency");
            record.getField("TestKey").setString("I");
            testTable.add(record);

            testTable.addNew();
            record.getField("ID").setString("10");
            record.getField("TestName").setString("10 - J Agency");
            record.getField("TestKey").setString("J");
            testTable.add(record);

            testTable.addNew();
            record.getField("ID").setString("11");
            record.getField("TestName").setString("K - 11 Agency");
            record.getField("TestKey").setString("K");
            testTable.add(record);

            testTable.addNew();
            record.getField("ID").setString("12");
            record.getField("TestName").setString("L - 12 Agency");
            record.getField("TestKey").setString("L");
            testTable.add(record);

            testTable.addNew();
            record.getField("ID").setString("13");
            record.getField("TestName").setString("M - 13 Agency");
            record.getField("TestKey").setString("M");
            testTable.add(record);

            testTable.addNew();
            record.getField("ID").setString("14");
            record.getField("TestName").setString("N - 14 Agency");
            record.getField("TestKey").setString("N");
            testTable.add(record);

            testTable.addNew();
            record.getField("ID").setString("15");
            record.getField("TestName").setString("O - 15 Agency");
            record.getField("TestKey").setString("O");
            testTable.add(record);

            testTable.addNew();
            record.getField("ID").setString("16");
            record.getField("TestName").setString("P - 16 Agency");
            record.getField("TestKey").setString("P");
            testTable.add(record);
            cat.debug("16 records added.\n");
        } catch (DBException e)   {
            cat.debug("Error adding record. Error: " + e.getMessage() + "\n");
            cat.debug(testTable.toString());
            fail("Error on add");
        }

        cat.debug("Count keys in both indexes.\n");
        try   {
            record.setKeyArea(Constants.PRIMARY_KEY);
            iCount = 0;
            testTable.close();
            while (testTable.hasNext())   {
                testTable.move(+1);
                iCount++;
            }
            cat.debug("Main index: Count: " + iCount + "\n");
            record.setKeyArea("TestCode");
            iCount = 0;
            testTable.close();
            while (testTable.hasPrevious())   {
                testTable.move(-1);
                iCount++;
            }
            cat.debug("Secondary index: Count: " + iCount + "\n");
            record.setKeyArea(Constants.PRIMARY_KEY);
            testTable.close();
        } catch (DBException e)   {
            cat.debug("Error reading through file: Error" + e.getMessage() + "\n");
            cat.debug(testTable.toString());
            fail("Error reading through file");
        }
    }
}
