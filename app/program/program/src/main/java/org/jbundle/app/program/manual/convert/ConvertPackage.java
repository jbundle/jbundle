/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.app.program.manual.convert;

//******************************************************************************
// Test the basic table functions (add, remove, move, etc.)
//******************************************************************************
import java.util.Hashtable;
import java.util.Map;

import org.jbundle.base.db.Record;
import org.jbundle.base.thread.ProcessRunnerTask;
import org.jbundle.base.util.Environment;
import org.jbundle.base.util.MainApplication;
import org.jbundle.app.program.db.ClassInfo;
import org.jbundle.model.DBException;
import org.jbundle.model.RecordOwnerParent;
import org.jbundle.model.Task;
import org.jbundle.model.util.Util;
import org.jbundle.thin.base.screen.BaseApplet;


// Creates the DAO DBEngine use the license
// import dao_dbengine;

// import dao350.*;   //import dao3032.*;
// import com.ms.com.Variant;

// SimpleForm is the data entry form for the sample
public class ConvertPackage extends ConvertDB
{

    /**
     * Constructor.
     */
    public ConvertPackage()
    {
        super();
    }
    /**
     * Initialization.
     * @param taskParent Optional task param used to get parent's properties, etc.
     * @param recordMain Optional main record.
     * @param properties Optional properties object (note you can add properties later).
     */
    public ConvertPackage(Task taskParent, Record recordMain, Map<String, Object> properties)
    {
        this();
        this.init(taskParent, recordMain, properties);
    }
    /**
     * Initialization.
     * @param taskParent Optional task param used to get parent's properties, etc.
     * @param recordMain Optional main record.
     * @param properties Optional properties object (note you can add properties later).
     */
    public void init(RecordOwnerParent taskParent, Record recordMain, Map<String, Object> properties)
    {
        super.init(taskParent, recordMain, properties);
    }
    /**
     * Free.
     */
    public void free()
    {
        super.free();
    }
    
    public static void main(String[] args)
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
        ConvertPackage test = new ConvertPackage(task, null, null);
        test.go();
        test.free();
        test = null;
        System.exit(0);
    }
    public void go()
    {
//        String strClass = "Data";
        String strFromField = "tourutil.script.import";
        String strToField = "tourutil.script.importdata";
        try   {
            ClassInfo recClassInfo = new ClassInfo(this);
            while (recClassInfo.hasNext())
            {
                recClassInfo.next();
//                if (recClassInfo.getField(ClassInfo.CLASS_NAME).toString().endsWith(strClass))
                        if (recClassInfo.getField(ClassInfo.CLASS_PACKAGE).toString().equals(strFromField))
                {
                    System.out.println("Fixing field " + strToField);
                    recClassInfo.edit();
                    recClassInfo.getField(ClassInfo.CLASS_PACKAGE).setString(strToField);
                    recClassInfo.set();
                }
            }
            recClassInfo.free();
            recClassInfo = null;
        } catch (DBException ex)    {
            ex.printStackTrace();
        }
    }
}
