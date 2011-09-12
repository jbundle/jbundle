/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.test.manual.test.db.shared;

//******************************************************************************
// Test the basic table functions (add, remove, move, etc.)
//******************************************************************************
import java.util.Hashtable;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.jbundle.app.test.vet.db.Vet;
import org.jbundle.app.test.vet.shared.db.Lizard;
import org.jbundle.app.test.vet.shared.db.Snake;
import org.jbundle.base.db.RecordOwner;
import org.jbundle.base.thread.BaseProcess;
import org.jbundle.base.util.BaseApplication;
import org.jbundle.base.util.Environment;
import org.jbundle.base.util.MainApplication;
import org.jbundle.model.Task;
import org.jbundle.model.util.Util;
import org.jbundle.test.manual.TestAll;
import org.jbundle.thin.base.thread.AutoTask;
import org.jbundle.thin.base.util.Application;


/**
 * BaseTest is the standard code to set for all the other tests.
 * @author  don
 * @version 
 */

public class ClientObjectTest extends ObjectTest
{

    /**
      *Creates new TestAll
      */
    public ClientObjectTest(String strTestName) {
        super(strTestName);
    }
    /**
     *
     */
    public static Test suite()
    {
        TestSuite suite= new TestSuite();     
        suite.addTest(new ClientObjectTest("testDatabase"));
//+        suite.addTest(new ClientObjectTest("testGrid"));
        return suite;
    }
    /**
     * Add the required args to the passed in args
     * @param args
     * @return
     */
    public String[] fixArgs(String[] args)
    {
    	String[] argsBase = {"dbSuffix=_test", "mainUserDBName=main", "programSharedDBName=program_test"};
    	String[] argsNew = new String[args.length + argsBase.length];
    	for (int i= 0; i < argsBase.length; i++)
    	{
    		argsNew[i] = argsBase[i];
    	}
    	for (int i= 0; i < args.length; i++)
    	{
    		argsNew[i + argsBase.length] = args[i];
    	}
    	return argsNew;
    }
    /**
     * Setup.
     */
    public void setUp()
    {
        if (vet == null)
        {
            String[] args = {"remote=Client", "local=Client", "table=Client"};
            args = TestAll.fixArgs(args);
            Map<String,Object> properties = new Hashtable<String,Object>();
            Util.parseArgs(properties, args);
            Environment env = new Environment(properties);
            Application app = new MainApplication(env, properties, null);
            Task task = new AutoTask(app, null, null);
            RecordOwner recordOwner = new BaseProcess(task, null, null);
            vet = new Vet(recordOwner);
            lizard = new Lizard(recordOwner);
            snake = new Snake(recordOwner);
        }
        this.addAnimalRecords(lizard, snake, vet, true);
    }
    /**
     * Tear down for the test.
     */
    public void tearDown()
    {
        ((BaseApplication)vet.getRecordOwner().getTask().getApplication()).getEnvironment().free();
    }
}
