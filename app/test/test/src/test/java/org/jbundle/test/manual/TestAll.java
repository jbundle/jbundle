/*
 * TestAll.java
 *
 * Created on March 19, 2001, 2:31 AM

 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.test.manual;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.Appender;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Category;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.SimpleLayout;

/**
 * Run all the tests.
 * TODO(don) --------- WARNING: I hard code the codebase to use localhost:8181 -----------
 * @author  don
 * @version 
 */
public class TestAll extends TestCase {

    /**
     *Creates new TestAll
     */
    public TestAll(String strTestName)
    {
        super(strTestName);
    }
    /**
     * Add the required args to the passed in args
     * @param args
     * @return
     */
    public static String[] fixArgs(String[] args)
    {
    	String[] argsBase = {/*"dbSuffix=_test", */"mainUserDBName=main", "programSharedDBName=program_test", "connectionType=proxy", /*"remotehost=linux-laptop", */"codebase=localhost:8181"};
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

    public static Test suite()
    {
       Category cat = Category.getRoot();
   // Now set its priority.
//     cat.setPriority(Priority.WARN);  // Don't print debug messages.
        Layout layout = new SimpleLayout();
//        Appender appender = new FileAppender(layout, System.out);
        Appender appender = new ConsoleAppender(layout, ConsoleAppender.SYSTEM_OUT);
        BasicConfigurator.configure(appender);

        TestSuite suite= new TestSuite();     
        suite.addTest(org.jbundle.test.manual.test.db.thick.JdbcDatabaseTest.suite());
        suite.addTest(org.jbundle.test.manual.test.db.thick.MemoryDatabaseTest.suite());
        suite.addTest(org.jbundle.test.manual.test.db.thick.JiniDatabaseTest.suite());

        suite.addTest(org.jbundle.test.manual.test.db.transactions.TransactionTest.suite());

        suite.addTest(org.jbundle.test.manual.test.db.thin.ThinMemoryDBTest.suite());
        suite.addTest(org.jbundle.test.manual.test.db.thin.ThinRemoteDBTest.suite());

        suite.addTest(org.jbundle.test.manual.test.db.fullthin.ClientDatabaseTest.suite());
        suite.addTest(org.jbundle.test.manual.test.db.fullthin.MemoryDatabaseTest.suite());

        suite.addTest(org.jbundle.test.manual.test.db.object.MemoryObjectTest.suite());
        suite.addTest(org.jbundle.test.manual.test.db.object.ClientObjectTest.suite());
        suite.addTest(org.jbundle.test.manual.test.db.object.JdbcObjectTest.suite());
        
        suite.addTest(org.jbundle.test.manual.test.db.thick.history.JdbcDatabaseTest.suite());

        return suite;
    }
    
    public void testMoneyEquals()
    {
        assertTrue(true);
    }
    
    public void testSimpleAdd()
    {
        assertTrue(true);
    }
}
