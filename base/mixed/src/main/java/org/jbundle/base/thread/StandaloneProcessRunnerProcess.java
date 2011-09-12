/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.thread;

import java.util.Hashtable;
import java.util.Map;

import org.jbundle.base.db.Record;
import org.jbundle.base.util.DBParams;
import org.jbundle.base.util.Environment;
import org.jbundle.base.util.MainApplication;
import org.jbundle.model.Task;
import org.jbundle.model.util.Util;
import org.jbundle.thin.base.screen.BaseApplet;


/**
 *  WriteJava - Constructor
 */
public class StandaloneProcessRunnerProcess extends BaseProcess
{
    /**
     * Constructor.
     */
    public StandaloneProcessRunnerProcess()
    {
        super();
    }
    /**
     * Constructor.
     */
    public StandaloneProcessRunnerProcess(Task taskParent, Record recordMain, Map<String,Object> properties)
    {
        this();
        this.init(taskParent, recordMain, properties);
    }
    /**
     * This is a special method that runs some code when this screen is opened as a task.
     */
    public void run()
    {
    	Map<String,Object> properties = this.getProperties();
        String process = this.getProperty("standaloneProcess");
        properties.put(DBParams.PROCESS, process);
        Environment env = new Environment(properties);
        MainApplication app = new MainApplication(env, properties, null);
        ProcessRunnerTask task = new ProcessRunnerTask(app, null, properties);
        task.run();	// Since I already have a processor thread (Note: This method will free when done)
        env.freeIfDone();
    }
    /**
     * 
     * @param args
     */
    public static void main(String[] args)
    {
        BaseApplet.main(args);      // This says I'm stand-alone
        Map<String,Object> properties = null;
        if (args != null)
        {
            properties = new Hashtable<String,Object>();
            Util.parseArgs(properties, args);
        }
        properties.put(DBParams.TABLE, DBParams.JDBC);
        properties.put(DBParams.LOCAL, DBParams.JDBC);
        properties.put(DBParams.REMOTE, DBParams.JDBC);
        StandaloneProcessRunnerProcess process = new StandaloneProcessRunnerProcess(null, null, properties);
        process.run();
        process.free();
        System.exit(0);
    }
}
