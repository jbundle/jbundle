package org.jbundle.test.manual.test.db.thick;

//******************************************************************************
// Test the basic table functions (add, remove, move, etc.)
//******************************************************************************
import java.util.Calendar;
import java.util.Date;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.Category;
import org.jbundle.app.test.test.db.TestTable;
import org.jbundle.base.db.Record;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.DateTimeField;
import org.jbundle.base.util.DBConstants;
import org.jbundle.model.DBException;
import org.jbundle.test.manual.test.message.MessageTest;


/**
 * BaseTest is the standard code to set for all the other tests.
 * @author  don
 * @version 
 */
public class BaseTest extends TestCase {

    /**
      *Creates new TestAll
      */
    public BaseTest(String strTestName) {
        super(strTestName);
    }
    public static Test suite()
    {
        TestSuite suite= new TestSuite();     
        // Hack - Get rid of mvn test error
        return suite;
    }

    Date date = null;
    /**
     * Add the test table records.
     */
    public void addTestTableRecords(TestTable testTable)
    {
        Category cat = Category.getInstance(BaseTest.class.getName());
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
            date = new Date();
//////////////////////////
            BaseField.gCalendar.setTime(date);
            BaseField.gCalendar.set(Calendar.MILLISECOND, 0);
            date = BaseField.gCalendar.getTime();
//////////////////////////

            ((DateTimeField)testTable.getField(TestTable.kTestDateTime)).setDateTime(date, DBConstants.DISPLAY, DBConstants.SCREEN_MOVE);
            ((DateTimeField)testTable.getField(TestTable.kTestDate)).setDate(date, DBConstants.DISPLAY, DBConstants.SCREEN_MOVE);
            testTable.getField(TestTable.kTestTime).setString("5:15 PM");
            Calendar cal = ((DateTimeField)testTable.getField(TestTable.kTestTime)).getCalendar();
            if (cal.get(Calendar.HOUR_OF_DAY) != 17)
                testTable.getField(TestTable.kTestTime).setString("17:15");
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
            cat.debug("6 records added.\n");
        } catch (DBException e)   {
            cat.debug("Error adding record. Error: " + e.getMessage() + "\n");
            cat.debug(testTable.toString());
            fail("Error on add");
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
                this.verifyRecord(testTable, iCount);
            }
        } catch (DBException e)   {
            cat.debug("NO NO NO\n");
            fail("Error on cound");
        }
    }
    /**
     * Verify the record's value
     */
    public void verifyRecord(Record record, int iID)
    {
        assertTrue(record.getField("ID").getValue() == iID);
        if (iID == 1)
        {
            assertTrue("A - Excellent Agent".equals(record.getField("TestName").toString()));
            assertTrue("This is a very long line\nThis is the second line.".equals(record.getField("TestMemo").toString()));
            assertTrue(record.getField("TestYesNo").getState());
            assertTrue(record.getField("TestLong").getValue() == 15000000);
            assertTrue(record.getField("TestShort").getValue() == 14000);
            BaseField.gCalendar.setTime(date);
            BaseField.gCalendar.set(Calendar.MILLISECOND, 0);
            Date dateTime = BaseField.gCalendar.getTime();
            BaseField.gCalendar.set(Calendar.HOUR_OF_DAY, 0);
            BaseField.gCalendar.set(Calendar.MINUTE, 0);
            BaseField.gCalendar.set(Calendar.SECOND, 0);
            Date dateOnly = BaseField.gCalendar.getTime();
            BaseField.gCalendar.setTime(date);
            BaseField.gCalendar.set(Calendar.MILLISECOND, 0);
            BaseField.gCalendar.set(Calendar.YEAR, DBConstants.FIRST_YEAR);
            BaseField.gCalendar.set(Calendar.MONTH, Calendar.JANUARY);
            BaseField.gCalendar.set(Calendar.DATE, 1);
            BaseField.gCalendar.set(Calendar.HOUR_OF_DAY, 5 + 12);
            BaseField.gCalendar.set(Calendar.MINUTE, 15);
            BaseField.gCalendar.set(Calendar.SECOND, 0);
            Date timeOnly = BaseField.gCalendar.getTime();

            assertTrue((dateTime.equals(record.getField("TestDateTime").getData()))
                || (date.equals(record.getField("TestDateTime").getData())));
            assertTrue(dateOnly.equals(record.getField("TestDate").getData()));

            assertTrue(timeOnly.equals(record.getField("TestTime").getData()));
            assertTrue((int)(record.getField("TestFloat").getValue() * 100) == 1234.56 * 100);
            assertTrue(record.getField("TestDouble").getValue() == 1234567.89);   // 2 digits
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
    public void addGridTestTableRecords(TestTable testTable)
    {
        Category cat = Category.getInstance(BaseTest.class.getName());
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
            testTable.setKeyArea(DBConstants.MAIN_KEY_AREA);
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

            testTable.setOpenMode(DBConstants.OPEN_REFRESH_AND_LOCK_ON_CHANGE_STRATEGY);  // Make sure keys are updated before sync


            testTable.addNew();
            testTable.getField(TestTable.kID).setString("1");
            testTable.getField(TestTable.kTestName).setString("A - Excellent Agent");
            testTable.getField(TestTable.kTestKey).setString("A");
            String str = testTable.getField(TestTable.kID).getString();
            testTable.add();

            testTable.addNew();
            testTable.getField(TestTable.kID).setString("2");
            testTable.getField(TestTable.kTestName).setString("B - Good Agent");
            testTable.getField(TestTable.kTestKey).setString("B");
            str = testTable.getField(TestTable.kID).getString();
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

            testTable.addNew();
            testTable.getField(TestTable.kID).setString("5");
            testTable.getField(TestTable.kTestName).setString("T - Tour Operator");
            testTable.getField(TestTable.kTestKey).setString("B");
            testTable.add();

            testTable.addNew();
            testTable.getField(TestTable.kID).setString("6");
            testTable.getField(TestTable.kTestName).setString("6 - Q Agency");
            testTable.getField(TestTable.kTestKey).setString("Q");
            testTable.add();

            testTable.addNew();
            testTable.getField(TestTable.kID).setString("7");
            testTable.getField(TestTable.kTestName).setString("7 - Q Agency");
            testTable.getField(TestTable.kTestKey).setString("G");
            testTable.add();

            testTable.addNew();
            testTable.getField(TestTable.kID).setString("8");
            testTable.getField(TestTable.kTestName).setString("8 - H Agency");
            testTable.getField(TestTable.kTestKey).setString("H");
            testTable.add();

            testTable.addNew();
            testTable.getField(TestTable.kID).setString("9");
            testTable.getField(TestTable.kTestName).setString("9 - I Agency");
            testTable.getField(TestTable.kTestKey).setString("I");
            testTable.add();

            testTable.addNew();
            testTable.getField(TestTable.kID).setString("10");
            testTable.getField(TestTable.kTestName).setString("10 - J Agency");
            testTable.getField(TestTable.kTestKey).setString("J");
            testTable.add();

            testTable.addNew();
            testTable.getField(TestTable.kID).setString("11");
            testTable.getField(TestTable.kTestName).setString("K - 11 Agency");
            testTable.getField(TestTable.kTestKey).setString("K");
            testTable.add();

            testTable.addNew();
            testTable.getField(TestTable.kID).setString("12");
            testTable.getField(TestTable.kTestName).setString("L - 12 Agency");
            testTable.getField(TestTable.kTestKey).setString("L");
            testTable.add();

            testTable.addNew();
            testTable.getField(TestTable.kID).setString("13");
            testTable.getField(TestTable.kTestName).setString("M - 13 Agency");
            testTable.getField(TestTable.kTestKey).setString("M");
            testTable.add();

            testTable.addNew();
            testTable.getField(TestTable.kID).setString("14");
            testTable.getField(TestTable.kTestName).setString("N - 14 Agency");
            testTable.getField(TestTable.kTestKey).setString("N");
            testTable.add();

            testTable.addNew();
            testTable.getField(TestTable.kID).setString("15");
            testTable.getField(TestTable.kTestName).setString("O - 15 Agency");
            testTable.getField(TestTable.kTestKey).setString("O");
            testTable.add();

            testTable.addNew();
            testTable.getField(TestTable.kID).setString("16");
            testTable.getField(TestTable.kTestName).setString("P - 16 Agency");
            testTable.getField(TestTable.kTestKey).setString("P");
            testTable.add();
            cat.debug("16 records added.\n");
        } catch (DBException e)   {
            cat.debug("Error adding record. Error: " + e.getMessage() + "\n");
            cat.debug(testTable.toString());
            fail("Error on add");
        }

        cat.debug("Count keys in both indexes.\n");
        try   {
            testTable.setKeyArea(DBConstants.MAIN_KEY_AREA);
            iCount = 0;
            testTable.close();
            while (testTable.hasNext())   {
                testTable.move(+1);
                iCount++;
            }
            cat.debug("Main index: Count: " + iCount + "\n");
            testTable.setKeyArea(DBConstants.MAIN_KEY_AREA+1);
            iCount = 0;
            testTable.close();
            while (testTable.hasPrevious())   {
                testTable.move(-1);
                iCount++;
            }
            cat.debug("Secondary index: Count: " + iCount + "\n");
            testTable.setKeyArea(DBConstants.MAIN_KEY_AREA);
        } catch (DBException e)   {
            cat.debug("Error reading through file: Error" + e.getMessage() + "\n");
            cat.debug(testTable.toString());
            fail("Error reading through file");
        }
    }
}
