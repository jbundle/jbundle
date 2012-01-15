/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.app.program.manual.convert;

//******************************************************************************
// Test the basic table functions (add, remove, move, etc.)
//******************************************************************************
import java.util.Hashtable;
import java.util.Map;

import org.jbundle.app.program.db.ClassFields;
import org.jbundle.app.program.db.FileHdr;
import org.jbundle.app.program.db.LogicFile;
import org.jbundle.base.db.Record;
import org.jbundle.base.thread.BaseProcess;
import org.jbundle.base.thread.ProcessRunnerTask;
import org.jbundle.base.util.DBConstants;
import org.jbundle.base.util.Environment;
import org.jbundle.base.util.MainApplication;
import org.jbundle.model.DBException;
import org.jbundle.model.RecordOwnerParent;
import org.jbundle.model.Task;
import org.jbundle.model.util.Util;
import org.jbundle.thin.base.screen.BaseApplet;


/**
 * Template to change one record's field to another value.
 */
public class ConvertGeneric extends BaseProcess
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
            ClassFields cf = new ClassFields(this);
            FileHdr record = new FileHdr(this);
            while (record.hasNext())
            {
                record.next();
                String displayClass = record.getField(FileHdr.kDisplayClass).getString();
                String maintClass = record.getField(FileHdr.kMaintClass).getString();
                if ((maintClass.length() == 0) && (displayClass.length() == 0))
                    continue;
                
                if (record.getField(FileHdr.kID).getValue() > 50000)
                    continue;

                cf.addNew();
                cf.getField(ClassFields.kClassInfoClassName).setString(record.getField(FileHdr.kFileName).toString());
                cf.getField(ClassFields.kClassFieldClass).setString(maintClass);
                cf.getField(ClassFields.kClassFieldsType).setString("S");
                cf.getField(ClassFields.kClassFieldName).setString(convertNameToConstant(maintClass) + "_CLASS");
                cf.getField(ClassFields.kClassFieldSequence).setString("1000");
                cf.getField(ClassFields.kClassFieldInitialValue).setString("MAINT_MODE");
                cf.getField(ClassFields.kIncludeScope).setValue(LogicFile.INCLUDE_INTERFACE);
                cf.add();
                cf.addNew();
                cf.getField(ClassFields.kClassInfoClassName).setString(record.getField(FileHdr.kFileName).toString());
                cf.getField(ClassFields.kClassFieldClass).setString(displayClass);
                cf.getField(ClassFields.kClassFieldsType).setString("S");
                if (maintClass.equalsIgnoreCase(displayClass))
                    displayClass = displayClass + "2";
                cf.getField(ClassFields.kClassFieldName).setString(convertNameToConstant(displayClass) + "_CLASS");
                cf.getField(ClassFields.kClassFieldSequence).setString("1010");
                cf.getField(ClassFields.kClassFieldInitialValue).setString("DISPLAY_MODE");
                cf.getField(ClassFields.kIncludeScope).setValue(LogicFile.INCLUDE_INTERFACE);
                cf.add();
            }
            record.free();
            record = null;
        } catch (DBException ex)    {
            ex.printStackTrace();
        }
    }
    /**
     * A utility name to convert a java name to a constant.
     * (ie., thisClassName -> THIS_CLASS_NAME)
     */
    public String convertNameToConstant(String strName)
    {
        String strConstants = DBConstants.BLANK;
        int iLastUpper = -1;
        for (int i = 0; i < strName.length(); i++)
        {
            char chNext = strName.charAt(i);
            if (!Character.isLowerCase(chNext))
            {   // The next word is always uppercase.
                if (i != iLastUpper + 1)
                    strConstants += "_";    // Previous letter was not uppercase, this starts a new word
                else if ((iLastUpper != -1) && (i + 1 < strName.length()) && (Character.isLowerCase(strName.charAt(i + 1))))
                    strConstants += "_";    // Previous letter was upper and next is lower, start a new word
                iLastUpper = i;
            }
            strConstants += Character.toUpperCase(chNext);
        }
        return strConstants;
    }
}
