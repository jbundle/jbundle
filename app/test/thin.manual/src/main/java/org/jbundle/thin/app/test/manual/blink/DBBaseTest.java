/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.app.test.manual.blink;

//******************************************************************************
// Test the basic table functions (add, remove, move, etc.)
//******************************************************************************
import org.jbundle.thin.base.db.FieldList;
import org.jbundle.thin.base.db.FieldTable;
import org.jbundle.thin.base.screen.BaseApplet;

// Creates the DAO DBEngine use the license
// import dao_dbengine;

// import dao350.*;   //import dao3032.*;
// import com.ms.com.Variant;

// SimpleForm is the data entry form for the sample
public class DBBaseTest extends BaseApplet
{
    private static final long serialVersionUID = 1L;

    // Create the form
    public DBBaseTest()
    {
        super();
    }
    /**
     *  OrderEntry Class Constructor.
     */
    public DBBaseTest(String args[])
    {
        this();
        this.init(args);
    }
    public void initTable(FieldTable testTable)
    {
        FieldList record = testTable.getRecord();
        int iCount = 0;
        System.out.print("Delete all old records.\n");
        iCount = 0;
        try   {
            testTable.close();
            while (testTable.hasNext())
            {
                testTable.next();
                testTable.remove();
                iCount++;
            }
        } catch (Exception e) {
            System.out.print("Could not delete record: Error" + e.getMessage() + "\n");
            System.out.print(record.toString());
            System.exit(0);
        }
        System.out.print("deleted records " + iCount + "\n");

        try   {
            System.out.print("Add records.\n");
            testTable.addNew();

            record.getField("ID").setString("1");
            record.getField("TestName").setString("A - Excellent Agent");
            record.getField("TestMemo").setString("This is a very long line\nThis is the second line.");
            record.getField("TestShort").setValue(2);
            testTable.add(record);

            testTable.addNew();
            record.getField("ID").setString("2");
            record.getField("TestName").setString("B - Good Agent");
            record.getField("TestShort").setValue(4);
            testTable.add(record);

            testTable.addNew();
            record.getField("ID").setString("3");
            record.getField("TestName").setString("C - Average Agent");
            record.getField("TestKey").setString("C");
            record.getField("TestShort").setValue(8);
            testTable.add(record);

            testTable.addNew();
            record.getField("ID").setString("4");
            record.getField("TestName").setString("F - Fam Trip Agent");
            record.getField("TestKey").setString("B");
            record.getField("TestShort").setValue(12);
            testTable.add(record);

            testTable.addNew();
            record.getField("ID").setString("5");
            record.getField("TestName").setString("T - Tour Operator");
            record.getField("TestKey").setString("B");
            record.getField("TestShort").setValue(14);
            testTable.add(record);

            testTable.addNew();
            record.getField("ID").setString("6");
            record.getField("TestName").setString("Q - Q Agency");
            record.getField("TestKey").setString("Q");
            record.getField("TestShort").setValue(16);
            testTable.add(record);
            System.out.print("6 records added.\n");
        } catch (Exception e) {
            System.out.print("Error adding record. Error: " + e.getMessage() + "\n");
            System.out.print(record.toString());
            System.exit(0);
        }

        System.out.print("Count keys in both indexes.\n");
        try   {
//          testTable.setKeyArea(Constants.MAIN_KEY_AREA);
            iCount = 0;
            testTable.close();
            while (testTable.hasNext())   {
                testTable.next();
System.out.print(record.toString());
                iCount++;
            }
        } catch (Exception e) {
            System.out.print("NO NO NO\n");
        }

    }
}
