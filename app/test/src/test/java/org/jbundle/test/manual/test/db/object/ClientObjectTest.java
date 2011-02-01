package org.jbundle.test.manual.test.db.object;

//******************************************************************************
// Test the basic table functions (add, remove, move, etc.)
//******************************************************************************
import java.util.Hashtable;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.jbundle.app.test.vet.db.Cat;
import org.jbundle.app.test.vet.db.Dog;
import org.jbundle.app.test.vet.db.Vet;
import org.jbundle.base.db.RecordOwner;
import org.jbundle.base.thread.BaseProcess;
import org.jbundle.base.util.BaseApplication;
import org.jbundle.base.util.Environment;
import org.jbundle.base.util.MainApplication;
import org.jbundle.model.Task;
import org.jbundle.test.manual.TestAll;
import org.jbundle.thin.base.thread.AutoTask;
import org.jbundle.thin.base.util.Application;
import org.jbundle.thin.base.util.Util;


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
        suite.addTest(new JdbcObjectTest("testGrid"));
        return suite;
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
            cat = new Cat(recordOwner);
            dog = new Dog(recordOwner);
        }
        this.addAnimalRecords(cat, dog, vet, true);
    }
    /**
     * Tear down for the test.
     */
    public void tearDown()
    {
        ((BaseApplication)vet.getRecordOwner().getTask().getApplication()).getEnvironment().free();
    }
}
