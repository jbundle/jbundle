package org.jbundle.app.program.manual.backup;

/**
 * OrderEntry.java:   Applet
 *  Copyright � 1997 jbundle.org. All rights reserved.
 *  
 *  @author Don Corley don@tourgeek.com
 *  @version 1.0.0.
 */
import java.util.Hashtable;
import java.util.Map;

import javax.swing.JApplet;

import org.jbundle.base.db.util.log.BackupConstants;
import org.jbundle.base.thread.BaseProcess;
import org.jbundle.base.util.BaseApplication;
import org.jbundle.model.Task;
import org.jbundle.model.util.Util;
import org.jbundle.thin.base.thread.AutoTask;


/**
 * Main Class for applet OrderEntry
 */
public class RestoreBackupApp extends BaseApplication
    implements BackupConstants
{

    /**
     *  Chat Screen Constructor.
     */
    public RestoreBackupApp()
    {
        super();
    }
    /**
     *  Chat Screen Constructor.
     * @param parent Typically, you pass the BaseApplet as the parent.
     * @param @record and the record or GridTableModel as the parent.
     */
    public RestoreBackupApp(String[] args)
    {
        this();
        Map<String,Object> properties = null;
        if (args != null)
        {
            properties = new Hashtable<String,Object>();
            Util.parseArgs(properties, args);
        }
        this.init(null, properties, null);
        Task task = new AutoTask(this, null, properties);
        BaseProcess process = new RestoreBackupProcess(task, null, properties);
        process.run();
        this.free();    // Done
    }
    /**
     *  Chat Screen Constructor.
     * @param parent Typically, you pass the BaseApplet as the parent.
     * @param @record and the record or GridTableModel as the parent.
     */
    public void init(Object env, Map<String,Object> properties, JApplet applet)
    {
        super.init(env, properties, applet);
    }
    /**
     *  The main() method acts as the applet's entry point when it is run
     *  as a standalone application. It is ignored if the applet is run from
     *  within an HTML page.
     */
    public static void main(String args[])
    {
        new RestoreBackupApp(args);
    }
}
