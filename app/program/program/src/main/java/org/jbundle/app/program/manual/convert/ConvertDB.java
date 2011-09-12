/*
 * Copyright © 2011 jbundle.org. All rights reserved.
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
import org.jbundle.app.program.db.LogicFile;
import org.jbundle.app.program.db.ScreenIn;
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
public class ConvertDB extends ConvertBase
{

    /**
     * Constructor.
     */
    public ConvertDB()
    {
        super();
    }
    /**
     * Initialization.
     * @param taskParent Optional task param used to get parent's properties, etc.
     * @param recordMain Optional main record.
     * @param properties Optional properties object (note you can add properties later).
     */
    public ConvertDB(Task taskParent, Record recordMain, Map<String, Object> properties)
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
        ConvertDB test = new ConvertDB(task, null, null);
        test.go();
        test.free();
        test = null;
        System.exit(0);
    }
    public void go()
    {
        try   {     
            LogicFile recLogic = new LogicFile(this);
            recLogic.setKeyArea("PrimaryKey");
            recLogic.close();
            int iCount = 0, iChanged = 0;
            while (recLogic.hasNext())
            {
                recLogic.next();
                String strSource = recLogic.getField(LogicFile.kLogicSource).toString();
                String strNew = this.convertString(strSource);
                String strSource2 = recLogic.getField(LogicFile.kMethodReturns).toString();
                String strNew2 = this.convertString(strSource2);
                if ((strSource != strNew) || (strSource2 != strNew2))
                {
                    iChanged++;
                    System.out.println("Count: " + iCount + " Changed: " + iChanged);
                    recLogic.edit();
                    recLogic.getField(LogicFile.kLogicSource).setString(strNew);
                    recLogic.getField(LogicFile.kMethodReturns).setString(strNew2);
                    recLogic.set();
                }
                iCount++;
            }
            System.out.println("Count: " + iCount);


            ScreenIn recScreenIn = new ScreenIn(this);
            recScreenIn.setKeyArea("PrimaryKey");
            recScreenIn.close();
            iCount = 0;
            iChanged = 0;
            while (recScreenIn.hasNext())
            {
                recScreenIn.next();
                String strSource = recScreenIn.getField(ScreenIn.kScreenText).toString();
                String strNew = this.convertString(strSource);
                if (strSource != strNew)
                {
                    iChanged++;
                    System.out.println("Count: " + iCount + " Changed: " + iChanged);
                    recScreenIn.edit();
                    recScreenIn.getField(ScreenIn.kScreenText).setString(strNew);
                    recScreenIn.set();
                }
                iCount++;
            }
            System.out.println("Count: " + iCount);

        } catch (DBException e)   {
            System.out.print("Error reading through file: Error" + e.getMessage() + "\n");
        }

    }
}
