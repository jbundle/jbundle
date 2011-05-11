package org.jbundle.base.thread;

/**
 * @(#)DBServlet.java 0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import java.lang.reflect.Method;
import java.util.Map;

import org.jbundle.base.util.DBParams;
import org.jbundle.base.util.Utility;
import org.jbundle.model.App;
import org.jbundle.model.util.Util;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.thread.AutoTask;
import org.jbundle.util.osgi.finder.ClassServiceImpl;


/**
 * A Process is a self-contained piece of code that does a particular process.
 * To run a process, just:
 * <pre>
 * If you are running from a screen, you may want to:
 * Process proc = new MyProcess(this.getTask(), recordMain, propertiesOptional);
 * process.run();
 * process.free();
 * If you want to run this as a separate thread, then you can do this:
 * String strJob = null;
 * strJob = Utility.addURLParam(strJob, DBParams.TASK, ".base.thread.ProcessRunnerTask");  // Task class
 * strJob = Utility.addURLParam(strJob, DBParams.PROCESS, DBConstants.ROOT_PACKAGE + "program.MyProcess");  // Process class
 * strJob = Utility.addURLParam(strJob, "filename", "xyz");   // Some params
 * TaskScheduler.addTask(strJob);
 * </pre>
 * Note: Do not forget to free yourself!
 */
public class ProcessRunnerTask extends AutoTask
{
    /**
     * Is this task currently running?
     */
    protected boolean m_bTaskRunning = false;

    /**
     * Constructor.
     */
    public ProcessRunnerTask()
    {
        super();
    }
    /**
     * Constructor.
     */
    public ProcessRunnerTask(App application, String strParams, Map<String,Object> properties)
    {
        this();
        this.init(application, strParams, properties);
    }
    /**
     * Constructor.
     */
    public void init(App application, String strParams, Map<String, Object> properties)
    {
        super.init(application, strParams, properties);
    }
    /**
     * Free all the resources belonging to this task.
     */
    public void free()
    {
    	super.free();
    	m_bTaskRunning = false;
    }
    /**
     * This is a special method that stops the code when this screen is opened as a task.
     */
    public void stopTask()
    {
        Util.getLogger().warning("Error - Attempt to stop an AutoTask");
    }
    /**
     * Is this task currently involved in computations?
     * @return True if the task is currently active.
     */
    public boolean isRunning()
    {
    	return m_bTaskRunning;
    }
    /**
     * Run this task.
     * Note: Be sure to call super!
     * Override this method to do something.
     */
    public void run()
    {
    	m_bTaskRunning = true;
        super.run();
    	m_bTaskRunning = false;
    }
    /**
     * Get the name of this process and run it.
     */
    public void runTask()
    {
        String strProcess = this.getProperty(DBParams.PROCESS);
        BaseProcess job = (BaseProcess)ClassServiceImpl.getClassService().makeObjectFromClassName(strProcess);
        if (job != null)
        {
            this.runProcess(job, m_properties);
        }
        else
        {   // NOTE: It is not recommended to start a standalone process, since they can wreak havoc with this jvm
                // (since standalones assume complete control and often will exit(0))
            String strClass = this.getProperty("standalone"); // Applets are also run as tasks
            if ((strClass != null) && (strClass.length() > 0))
            {
                try
                {
                    if (strClass.indexOf('.') == 0)
                        strClass = Constants.ROOT_PACKAGE + strClass.substring(1);
                    Class<?> c = Class.forName(strClass);
                    // Don't instatiate the class, since I'm calling static main
                    if (c != null)
                    {
                        Class<?>[] parameterTypes = {String[].class};
                        Method method = c.getMethod("main", parameterTypes);
                        Object[] args = new Object[1];
                        Map<String,Object> properties = m_properties;   // Only passed in properties
                        String[] rgArgs = Utility.propertiesToArgs(properties);
                        args[0] = rgArgs;
                        method.invoke(null, args);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    /**
     * Run this process.
     * @param job The process to run.
     * @param properties The properties to pass to this process.
     */
    public void runProcess(BaseProcess job, Map<String,Object> properties)
    {
        job.init(this, null, properties);
        job.run();
        job.free();
    }
}

