/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.test.manual.test.db.fullthin;

//******************************************************************************
// Test the basic table functions (add, remove, move, etc.)
//******************************************************************************
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.jbundle.thin.app.test.test.db.TestTableNoAuto;
import org.jbundle.thin.base.db.FieldTable;
import org.jbundle.thin.base.db.client.CachedRemoteTable;
import org.jbundle.thin.base.db.client.RemoteFieldTable;
import org.jbundle.thin.base.db.client.VectorFieldTable;
import org.jbundle.thin.base.remote.RemoteException;
import org.jbundle.thin.base.remote.RemoteTable;
import org.jbundle.thin.base.remote.RemoteTask;


/**
 * BaseTest is the standard code to set for all the other tests.
 * @author  don
 * @version 
 */

public class ClientDatabaseTest extends DatabaseTest
{

    /**
      *Creates new TestAll
      */
    public ClientDatabaseTest(String strTestName) {
        super(strTestName);
    }
    
    public static Test suite()
    {
        TestSuite suite= new TestSuite();     
        suite.addTest(new ClientDatabaseTest("testDatabase"));
        suite.addTest(new ClientDatabaseTest("testDatabase"));  // Run it twice
        suite.addTest(new ClientDatabaseTest("testGridAccess"));
        suite.addTest(new ClientDatabaseTest("testGridCacheAccess"));
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
            if (("testGridCacheAccess".equals(getName()))
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
