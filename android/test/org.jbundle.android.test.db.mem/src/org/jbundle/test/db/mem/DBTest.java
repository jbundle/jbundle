package org.jbundle.test.db.mem;

import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

import org.jbundle.model.App;
import org.jbundle.model.util.Util;
import org.jbundle.thin.app.test.test.db.TestTable;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.FieldList;
import org.jbundle.thin.base.db.FieldTable;
import org.jbundle.thin.base.db.client.RemoteFieldTable;
import org.jbundle.thin.base.db.mem.GridMemoryFieldTable;
import org.jbundle.thin.base.db.mem.base.PDatabase;
import org.jbundle.thin.base.db.mem.base.PTable;
import org.jbundle.thin.base.db.mem.base.PhysicalDatabaseParent;
import org.jbundle.thin.base.db.mem.memory.MDatabase;
import org.jbundle.thin.base.db.model.ThinPhysicalDatabase;
import org.jbundle.thin.base.remote.RemoteTable;
import org.jbundle.thin.base.remote.RemoteTask;
import org.jbundle.thin.base.util.Application;

public class DBTest extends Object
    implements Runnable
{
    
    public DBTest()
    {
        super();
    }
    public void run()
    {
        String text = "nothing";
        
        FieldTable fieldTable = null;
        if (false)
            fieldTable = this.setupThinTable();
        else
            fieldTable = this.setupRemoteThinTable();
        text = this.addTestTableRecords(fieldTable);
        
//        mPickDate.setText(text);
        memActivity.text = text;
    }
        
    /**
     * Create the thin table.
     */
    public FieldTable setupThinTable()
    {
        PhysicalDatabaseParent dbOwner = null;
        FieldList record = null;
//        BaseApplet.main(null);
        String[] args = {"remote=Client", "local=Client", "table=Client", "codebase=www.jbundle.org", "remotehost=www.jbundle.org", "connectionType=proxy"};
//        args = Test.fixArgs(args);
//        BaseApplet applet = new BaseApplet(args);
        Map<String,Object> properties = null;
        if (args != null)
        {
            properties = new Hashtable<String,Object>();
            Util.parseArgs(properties, args);
        }
        if (record == null)
            record = new TestTable(null);

        FieldTable fieldTable = null;       

        if (dbOwner == null)
            dbOwner = new PhysicalDatabaseParent(null);

        PDatabase db = dbOwner.getPDatabase(record.getDatabaseName(), ThinPhysicalDatabase.MEMORY_TYPE, false);
        if (db == null)
            db = new MDatabase(dbOwner, record.getDatabaseName());
        PTable ptable = db.getPTable(record, true);
        fieldTable = new GridMemoryFieldTable(record, ptable, null);
        
        
        return fieldTable;
    }
    /**
     * Create the thin table.
     */
    public FieldTable setupRemoteThinTable()
    {
        FieldList record = null;
//        BaseApplet.main(null);
//        String[] args = {"remote=Client", "local=Client", "table=Client", "codebase=www.jbundle.org", "remotehost=www.jbundle.org", "connectionType=proxy"};
        String[] args = {"remotehost=www.jbundle.org"};
//        args = Test.fixArgs(args);
//        BaseApplet applet = new BaseApplet(args);
        Map<String,Object> properties = null;
        if (args != null)
        {
            properties = new Hashtable<String,Object>();
            Util.parseArgs(properties, args);
        }
        App app = new Application(null, properties, null);

        if (record == null)
            record = new TestTable(null);

        FieldTable fieldTable = null;
        RemoteTask server = null;
        RemoteTable remoteTable = null;
        try   {
            server = (RemoteTask)app.getRemoteTask(null);
            Map<String, Object> dbProperties = app.getProperties();
            remoteTable = server.makeRemoteTable(record.getRemoteClassName(), null, null, dbProperties);
//?                    remoteTable = new CachedRemoteTable(remoteTable);
        } catch (Exception ex)  {
            ex.printStackTrace();
        }
        fieldTable = new RemoteFieldTable(record, remoteTable, server);
        
        return fieldTable;
    }
    /**
     * Create the thin table.
     */
    public FieldTable xsetupThinTable()
    {
        PhysicalDatabaseParent dbOwner = null;
        FieldList record = null;
        if (record == null)
            record = new TestTable(null);
        if (dbOwner == null)
            dbOwner = new PhysicalDatabaseParent(null);
        PDatabase db = dbOwner.getPDatabase(record.getDatabaseName(), ThinPhysicalDatabase.MEMORY_TYPE, false);
        if (db == null)
            db = new MDatabase(dbOwner, record.getDatabaseName());
        PTable ptable = db.getPTable(record, true);
        GridMemoryFieldTable table = new GridMemoryFieldTable(record, ptable, null);
        
        return table;
    }

    /**
     * Add the test table records.
     */
    public String addTestTableRecords(FieldTable testTable)
    {
        FieldList record = testTable.getRecord();
        int iCount = 0;
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
            return ("Error deleting records. Error: " + ex.getMessage());
        }

        try   {
            testTable.addNew();
            
Date date = new Date();
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
        } catch (Exception ex)  {
            ex.printStackTrace();
            return ("Error adding record. Error: " + ex.getMessage());
        }

        try   {
            record.setKeyArea(Constants.PRIMARY_KEY);
            iCount = 0;
            testTable.close();
            while (testTable.hasNext())
            {
                testTable.next();
                iCount++;
            }
        } catch (Exception ex)  {
            ex.printStackTrace();
            return ("Record count error");
        }
        return ("Count = " + iCount);

    }
}
