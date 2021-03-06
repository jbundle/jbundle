/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.thread;

import java.util.Hashtable;
import java.util.Map;

import org.jbundle.base.db.Record;
import org.jbundle.base.model.DBParams;
import org.jbundle.base.util.Environment;
import org.jbundle.base.util.MainApplication;
import org.jbundle.model.App;
import org.jbundle.model.Task;
import org.jbundle.model.util.Util;
import org.jbundle.thin.base.util.Application;


/**
 *  WriteJava - Constructor
 */
public class StandaloneProcessRunnerProcess extends BaseProcess
{
    public static final String STANDALONE_PROCESS = "standaloneProcess";

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
        String process = this.getProperty(STANDALONE_PROCESS);
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
        Map<String,Object> properties = null;
        if (args != null)
        {
            properties = new Hashtable<String,Object>();
            Util.parseArgs(properties, args);
        }
        properties.put(DBParams.TABLE, DBParams.JDBC);
        properties.put(DBParams.LOCAL, DBParams.JDBC);
        properties.put(DBParams.REMOTE, DBParams.JDBC);
        App app = new Application(null, properties, null);
        ProcessRunnerTask task = new ProcessRunnerTask(app, null, null);
        StandaloneProcessRunnerProcess process = new StandaloneProcessRunnerProcess(task, null, properties);
        process.run();
        process.free();
        System.exit(0);
    }
}
