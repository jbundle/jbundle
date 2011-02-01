package org.jbundle.test.manual.test.db.thin;

//******************************************************************************
// Test the basic table functions (add, remove, move, etc.)
//******************************************************************************
import java.rmi.RemoteException;
import java.util.Map;

import org.jbundle.thin.app.test.test.db.TestTableNoAuto;
import org.jbundle.thin.base.db.FieldTable;
import org.jbundle.thin.base.db.client.CachedRemoteTable;
import org.jbundle.thin.base.db.client.RemoteFieldTable;
import org.jbundle.thin.base.db.client.VectorFieldTable;
import org.jbundle.thin.base.remote.RemoteTable;
import org.jbundle.thin.base.remote.RemoteTask;

import junit.framework.Test;
import junit.framework.TestSuite;


// SimpleForm is the data entry form for the sample
public class ThinRemoteDBTest extends ThinBaseDBTest
{

    // Create the form
    public ThinRemoteDBTest(String strTestName)
    {
        super(strTestName);
    }
    public static Test suite()
    {
        TestSuite suite= new TestSuite();     
        suite.addTest(new ThinRemoteDBTest("verifyDataTest"));
        suite.addTest(new ThinRemoteDBTest("verifyDataTest"));  // Run it twice to make sure data is inited consistently
        suite.addTest(new ThinRemoteDBTest("verifyAddTest"));
        suite.addTest(new ThinRemoteDBTest("gridAccessTest"));
//      suite.addTest(new ThinRemoteDBTest("cacheSeekTest"));
        return suite;
    }
    /**
     * Setup.
     */
    public void setUp()
    {
        record = new TestTableNoAuto(this);
        super.setUp();
    }
    /**
     * Create the thin table
     */
    public FieldTable setupThinTable()
    {
        RemoteTask server = null;
        RemoteTable remoteTable = null;
        VectorFieldTable testTable = null;
        try   {
            server = (RemoteTask)applet.getRemoteTask();
            Map<String, Object> dbProperties = applet.getApplication().getProperties();            
            remoteTable = server.makeRemoteTable(record.getRemoteClassName(), null, null, dbProperties);
            if (("gridAccessTest".equals(getName()))
                || ("cacheSeekTest".equals(getName())))
                    remoteTable = new CachedRemoteTable(remoteTable);
        } catch (RemoteException ex)    {
            ex.printStackTrace();
        } catch (Exception ex)  {
            ex.printStackTrace();
        }
        return new RemoteFieldTable(record, remoteTable, server);
    }
}
