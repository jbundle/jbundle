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
import org.jbundle.base.db.VirtualRecord;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.thread.BaseProcess;
import org.jbundle.base.thread.ProcessRunnerTask;
import org.jbundle.base.util.Environment;
import org.jbundle.base.util.MainApplication;
import org.jbundle.app.program.db.ClassInfo;
import org.jbundle.app.program.db.ScreenIn;
import org.jbundle.model.DBException;
import org.jbundle.model.RecordOwnerParent;
import org.jbundle.model.Task;
import org.jbundle.model.util.Util;
import org.jbundle.thin.base.screen.BaseApplet;


/**
 * Template to change one record's field to another value.
 */
public class ConvertID extends BaseProcess
{

    /**
     * Constructor.
     */
    public ConvertID()
    {
        super();
    }
    /**
     * Initialization.
     * @param taskParent Optional task param used to get parent's properties, etc.
     * @param recordMain Optional main record.
     * @param properties Optional properties object (note you can add properties later).
     */
    public ConvertID(Task taskParent, Record recordMain, Map<String, Object> properties)
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
        ConvertID test = new ConvertID(task, null, null);
        test.go();
        test.free();
        test = null;
        System.exit(0);
    }
    public static int BUMP_VALUE = 16777216;
    public void go()
    {
        new ClassInfo(this);
        Record recClassInfo = new ScreenIn(this);
        this.bumpID(recClassInfo);
        recClassInfo.free();
    }
    /**
     * 
     * @param record
     */
    public void bumpID(Record record)
    {
        try   {
            record.setKeyArea(DBConstants.MAIN_KEY_AREA);
            while (record.hasNext())
            {
                record.next();
                int iID = (int)record.getField(VirtualRecord.kID).getValue();
                if (iID >= BUMP_VALUE)
                    break;
                if (this.isChangeToNewID(record))
                {
                    record.setAutoSequence(false);
                    record.edit();
                    iID = iID + BUMP_VALUE;
                    record.getField(VirtualRecord.kID).setValue(iID);
                    record.set();
                    record.setAutoSequence(true);
                }
            }
        } catch (DBException ex)    {
            ex.printStackTrace();
        }
    }
    /**
     * Change this to the new ID?
     * @param record
     * @return
     */
    public boolean isChangeToNewID(Record record)
    {
        Record recClassInfo = this.getRecord(ClassInfo.CLASS_INFO_FILE);
        recClassInfo.setKeyArea(ClassInfo.CLASS_NAME_KEY);
        recClassInfo.getField(ClassInfo.CLASS_NAME).moveFieldToThis(record.getField(ScreenIn.SCREEN_IN_PROG_NAME));
        try {
            if (!recClassInfo.seek(DBConstants.EQUALS))
                return false;
        } catch (DBException e) {
            e.printStackTrace();
        }
        return (recClassInfo.getField(ClassInfo.CLASS_PROJECT_ID).getValue() == 1);
    }
    /**
     * 
     * @param record
     */
    public void bumpField(Record record, BaseField field, Record recReference)
    {
        try   {
            record.setKeyArea(DBConstants.MAIN_KEY_AREA);
            recReference.setKeyArea(DBConstants.MAIN_KEY_AREA);
            while (record.hasNext())
            {
                record.next();
                int iValue = (int)field.getValue();
                record.edit();
                iValue = iValue + BUMP_VALUE;
                recReference.addNew();
                recReference.getField(VirtualRecord.kID).setValue(iValue);
                if (recReference.seek(DBConstants.EQUALS))
                {
                    field.setValue(iValue);
                    record.set();
                }
            }
        } catch (DBException ex)    {
            ex.printStackTrace();
        }
    }
}
