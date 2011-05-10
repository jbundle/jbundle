package org.jbundle.app.test.manual.initialload;

import java.util.Hashtable;
import java.util.Map;

import org.jbundle.app.test.test.db.TestTable;
import org.jbundle.base.screen.model.BaseScreen;
import org.jbundle.base.thread.BaseProcess;
import org.jbundle.base.thread.ProcessRunnerTask;
import org.jbundle.base.util.Environment;
import org.jbundle.base.util.MainApplication;
import org.jbundle.model.DBException;
import org.jbundle.model.util.Util;
import org.jbundle.thin.base.screen.BaseApplet;

public class InitialLoadTest extends BaseScreen {

    public InitialLoadTest() {
        
        new TestRead().start();     

        new TestRead().start();     
        
    }
    static MainApplication app = null;
    /**
     * @param args
     */
    public static void main(String[] args) {
        BaseApplet.main(args);      // This says I'm stand-alone
        Map<String,Object> properties = null;
        if (args != null)
        {
            properties = new Hashtable<String,Object>();
            Util.parseArgs(properties, args);
        }
        Environment env = new Environment(properties);
        app = new MainApplication(env, properties, null);
        
        new InitialLoadTest();

    }

    public static class TestRead extends Thread
    {
        public TestRead()
        {
            super();
        }
        public void run()
        {
            ProcessRunnerTask task = new ProcessRunnerTask(app, null, null);
            
            BaseProcess process = new BaseProcess(task, null, null);

            TestTable testTable = new TestTable(process);
            
            try {
                testTable.addNew();
                
                testTable.getField(TestTable.kID).setValue(7);
                testTable.setKeyArea(TestTable.kIDKey);
                boolean success = testTable.seek(null);
                
                System.out.println(success);
            } catch (DBException e) {
                e.printStackTrace();
            }
        }        
    }
}
