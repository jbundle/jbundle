/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.test.manual.test.message;

//******************************************************************************
// Test the basic table functions (add, remove, move, etc.)
//******************************************************************************
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jbundle.app.test.vet.db.Vet;
import org.jbundle.base.thread.ProcessRunnerTask;
import org.jbundle.base.util.Environment;
import org.jbundle.base.util.MainApplication;
import org.jbundle.model.util.Util;
import org.jbundle.test.manual.test.db.thick.JdbcDatabaseTest;
import org.jbundle.thin.base.message.BaseMessage;
import org.jbundle.thin.base.message.TreeMessage;
import org.jbundle.thin.base.screen.BaseApplet;


// SimpleForm is the data entry form for the sample
public class MessageTest extends TestCase
{

    // Create the form
    public MessageTest(String strTestName)
    {
        super(strTestName);
    }
    /**
     * Setup.
     */
    public void setUp()
    {
    }
    /**
     * Setup.
     */
    public void tearDown()
    {
    }
    
    public static Test suite()
    {
        TestSuite suite= new TestSuite();     
        suite.addTest(new MessageTest("runTest"));
        return suite;
    }

    public static final void main(String[] args)
    {
        BaseApplet.main(args);      // This says I'm stand-alone
        Map<String,Object> properties = null;
        if (args != null)
        {
            properties = new Hashtable<String,Object>();
            Util.parseArgs(properties, args);
        }
        Environment env = new Environment(properties);
        MainApplication app = new MainApplication(env, properties, null);
        ProcessRunnerTask task = new ProcessRunnerTask(app, null, properties);
        try {
            new MessageTest("Test").runTest();
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        System.exit(0);
    }
    /**
     * 
     */
    public void runTest() throws Throwable
    {
        System.out.println("Start Test");
        
        BaseMessage message = null;
        
        message = new TreeMessage(null, null);
        this.loadMessage(message);
        this.printMessage(message);

        String strParam = null;
        Class classRawObject = null;
        boolean bRequired = true;
        Object objRawDefault = null;
/*
        message = new TreeMessage(null, null);
        new MessageFieldDesc(message, "key1", classRawObject, bRequired, objRawDefault);
        new MessageFieldDesc(message, "key2", classRawObject, bRequired, objRawDefault);
        new MessageFieldDesc(message, "integer1", Integer.class, bRequired, objRawDefault);
        new MessageFieldDesc(message, "date1", Date.class, bRequired, objRawDefault);
        this.loadMessage(message);
        this.printMessage(message);
        Object date1 = message.getMessageFieldDesc("date1").get();
        System.out.println("date1" + " = " + date1 + "  class= " + date1.getClass().getName());
        String strXML = message.getXML(true);
        
        message = new TreeMessage(null, null);
        new MessageFieldDesc(message, "key1", classRawObject, bRequired, objRawDefault);
        new MessageFieldDesc(message, "key2", classRawObject, bRequired, objRawDefault);
        new MessageFieldDesc(message, "integer1", Integer.class, bRequired, objRawDefault);
        new MessageFieldDesc(message, "date1", Date.class, bRequired, objRawDefault);
        message.setXML(strXML);
        this.printMessage(message);        
        
        TestTable record = new TestTable(this);
        record.addNew();
        record.getField(TestTable.TEST_NAME).setString("Test Name");
        record.getField(TestTable.TEST_DOUBLE).setValue(1234.56);
        ((DateTimeField)record.getField(TestTable.TEST_DATE)).setDate(new Date(), true, Constants.INIT_MOVE);
        message = new TreeMessage(null, null);
        new MessageFieldDesc(message, record.getField(TestTable.TEST_NAME).getFieldName(), classRawObject, bRequired, objRawDefault);
        new MessageFieldDesc(message, record.getField(TestTable.TEST_DOUBLE).getFieldName(), classRawObject, bRequired, objRawDefault);
        new MessageFieldDesc(message, record.getField(TestTable.TEST_DATE).getFieldName(), Date.class, bRequired, objRawDefault);
        message.getMessageDataDesc(null).putRawRecordData(record);
        this.printMessage(message);        
        record = new TestTable(null);
        ((MessageRecordDesc)message.getMessageDataDesc(null)).getRawRecordData(record);
        System.out.println("-------------------------------------");
        System.out.println(record.toString());
        System.out.println("-------------------------------------");
        ((MessageRecordDesc)message.getMessageDataDesc(null)).initForMessage(record);
        System.out.println(record.toString());
        
        Cat cat = new Cat(null);
        cat.next();
        message = new TreeMessage(null, null);
        CatMessageRecordDesc messageDesc = new CatMessageRecordDesc(message, "cat");
        messageDesc.putRawRecordData(cat);
        this.printMessage(message);
        
        cat.addNew();
        messageDesc.getRawRecordData(cat);
        System.out.println("-------------------------------------");
        System.out.println(cat);
        System.out.println("-------------------------------------");
        cat.add();
  */      
        Vet vet = new Vet(null);
        vet.next();
        message = new TreeMessage(null, null);
        VetsMessageRecordDesc vetMessageDesc = new VetsMessageRecordDesc(message, "vet");
        vetMessageDesc.handlePutRawRecordData(vet);
        this.printMessage(message);

        vet.addNew();
        vetMessageDesc.getRawRecordData(vet);
        System.out.println("-------------------------------------");
        System.out.println(vet);
        System.out.println("-------------------------------------");
        vet.add();

        System.out.println("End Test");
    }
    /**
     * 
     * @param message
     */
    public void loadMessage(BaseMessage message)
    {
        String key = "key1";
        Object value = "value1";
        message.put(key, value);
        key = "key2";
        value = "value2";
        message.put(key, value);
        key = "integer1";
        value = new Integer(1);
        message.put(key, value);
        key = "date1";
        value = new Date();
        message.put(key, value);
    }
    /**
     * 
     * @param message
     */
    public void printMessage(BaseMessage message)
    {
        System.out.println(message.getXML(true));
        this.printKey(message, "key1");
        this.printKey(message, "key2");
        this.printKey(message, "integer1");
        this.printKey(message, "date1");
    }

    public void printKey(BaseMessage message, String strKey)
    {
        if (message.get(strKey) != null)
            System.out.println(strKey + " = " + message.get(strKey) + "  class= " + message.get(strKey).getClass().getName() + " String= " + message.getString(strKey));
    }
}
