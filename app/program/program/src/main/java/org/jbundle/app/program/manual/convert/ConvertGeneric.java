/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.app.program.manual.convert;

//******************************************************************************
// Test the basic table functions (add, remove, move, etc.)
//******************************************************************************
import java.util.Hashtable;
import java.util.Map;

import org.jbundle.app.program.db.LogicFile;
import org.jbundle.base.db.Record;
import org.jbundle.base.field.ReferenceField;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.thread.BaseProcess;
import org.jbundle.base.thread.ProcessRunnerTask;
import org.jbundle.base.util.Environment;
import org.jbundle.base.util.MainApplication;
import org.jbundle.main.db.Menus;
import org.jbundle.model.DBException;
import org.jbundle.model.RecordOwnerParent;
import org.jbundle.model.Task;
import org.jbundle.model.util.Util;
import org.jbundle.thin.base.screen.BaseApplet;


/**
 * Template to change one record's field to another value.
 */
public class ConvertGeneric extends ConvertBase
{

    /**
     * Constructor.
     */
    public ConvertGeneric()
    {
        super();
    } 
    /**
     * Initialization.
     * @param taskParent Optional task param used to get parent's properties, etc.
     * @param recordMain Optional main record.
     * @param properties Optional properties object (note you can add properties later).
     */
    public ConvertGeneric(Task taskParent, Record recordMain, Map<String, Object> properties)
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
        ConvertGeneric test = new ConvertGeneric(task, null, null);
        test.go();
        test.free();
        test = null;
        System.exit(0);
    }
    public void go()
    {
        try   {
            LogicFile record = new LogicFile(this);
//            Menus record = new Menus(this);
            while (record.hasNext())
            {
                record.next();
                {
                    String string = record.getField(LogicFile.kLogicSource).toString();
                    String strStart = string;
                    
                    //if (record.getCounterField().getValue() > 60000)
                    //    continue;

                    int start;
                    int end = 0;
                    while ((start = string.indexOf("k", end)) > 0)
                    {
                        end = start + 1;
                        char ch = string.charAt(start - 1);
                        if ((ch != '.') && (ch != ' '))
                            continue;
                        boolean isUpper = false;
                        for (; end < string.length(); end++)
                        {
                             ch = string.charAt(end);
                            if (!Character.isJavaIdentifierPart(ch))
                                break;
                            if (!Character.isUpperCase(ch))
                                isUpper = true;
                        }
                        if (!isUpper)
                            continue;
                        if (end >= string.length())
                            break;

                        String newName = convertNameToConstant(string.substring(start+1, end));
                        
                        string = string.substring(0, start) + newName + string.substring(end);

                        System.out.println(string);
                    }
                        
                        if (!strStart.equals(string))
                        {
                            System.out.println("--------------------------------------------------");
                            System.out.println(record.getField(LogicFile.kMethodClassName).toString());
                            record.edit();
                            System.out.println(string);record.getField(LogicFile.kLogicSource).setString(string);
                            record.set();
                        }
                }
            }
            record.free();
            record = null;
        } catch (DBException ex)    {
            ex.printStackTrace();
        }
    }
}
