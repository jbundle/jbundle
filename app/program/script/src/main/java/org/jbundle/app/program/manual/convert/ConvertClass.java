/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.app.program.manual.convert;

//******************************************************************************
// Test the basic table functions (add, remove, move, etc.)
//******************************************************************************
import java.util.Hashtable;
import java.util.Map;

import org.jbundle.base.db.KeyArea;
import org.jbundle.base.db.Record;
import org.jbundle.base.db.filter.StringSubFileFilter;
import org.jbundle.base.db.filter.SubFileFilter;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.model.Utility;
import org.jbundle.base.thread.ProcessRunnerTask;
import org.jbundle.base.util.Environment;
import org.jbundle.base.util.MainApplication;
import org.jbundle.app.program.db.ClassFields;
import org.jbundle.app.program.db.ClassInfo;
import org.jbundle.app.program.db.ClassResource;
import org.jbundle.app.program.db.FieldData;
import org.jbundle.app.program.db.KeyInfo;
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
public class ConvertClass extends ConvertDB
{

    /**
     * Constructor.
     */
    public ConvertClass()
    {
        super();
    }
    /**
     * Initialization.
     * @param taskParent Optional task param used to get parent's properties, etc.
     * @param recordMain Optional main record.
     * @param properties Optional properties object (note you can add properties later).
     */
    public ConvertClass(Task taskParent, Record recordMain, Map<String, Object> properties)
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
    /**
     *
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
        Environment env = new Environment(properties);
        MainApplication app = new MainApplication(env, properties, null);
        ProcessRunnerTask task = new ProcessRunnerTask(app, null, properties);
        ConvertClass test = new ConvertClass(task, null, null);
        test.go();
        test.free();
        test = null;
        System.exit(0);
    }
    /**
     *
     */
    public void go()
    {
        Record recFieldData = new FieldData(this);
        Record recKeyInfo = new KeyInfo(this);
        Record recClassFields = new ClassFields(this);
        Record recClassResource = new ClassResource(this);
        Record recLogicFile = new LogicFile(this);
        Record recScreenIn = new ScreenIn(this);
        
        String strFromField = "PingResponseXmlbeansReplyOut2008A";
        String strToField = "PingResponseXmlbeansMessageOut2010A";
        try   {
            ClassInfo recClassInfo = new ClassInfo(this);
            while (recClassInfo.hasNext())
            {
                recClassInfo.next();
                if (recClassInfo.getField(ClassInfo.CLASS_NAME).toString().equals(strFromField))
//x             int iIndex = recClassInfo.getField(ClassInfo.BASE_CLASS_NAME).toString().indexOf(strFromField);
//x             if ((recClassInfo.getField(ClassInfo.CLASS_NAME).toString().equals("BaseListener"))
//x                 || (recClassInfo.getField(ClassInfo.CLASS_NAME).toString().equals("FileListener"))
//x                 || (recClassInfo.getField(ClassInfo.CLASS_NAME).toString().equals("FieldListener")))
//x                     iIndex = -1;
//x             if (iIndex != -1)
                {
//x                 strToField = recClassInfo.getField(ClassInfo.CLASS_NAME).toString().substring(0, iIndex) + "Handler";
                    System.out.println("Fixing field " + strToField);
                    this.fixDetail(recClassInfo, recFieldData, strFromField, strToField);
                    this.fixDetail(recClassInfo, recKeyInfo, strFromField, strToField);
                    this.fixDetail(recClassInfo, recClassFields, strFromField, strToField);
                    this.fixDetail(recClassInfo, recClassResource, strFromField, strToField);
                    this.fixDetail(recClassInfo, recLogicFile, strFromField, strToField);
                    this.fixDetail(recClassInfo, recScreenIn, strFromField, strToField);
                    recClassInfo.edit();
                    if (recClassInfo.getField(ClassInfo.CLASS_NAME).toString().equals(recClassInfo.getField(ClassInfo.CLASS_SOURCE_FILE).toString()))
                        recClassInfo.getField(ClassInfo.CLASS_SOURCE_FILE).setString(strToField);
                    recClassInfo.getField(ClassInfo.CLASS_NAME).setString(strToField);
//                  Utility.getLogger().info(recClassInfo.toString());
                    recClassInfo.set();
                }
            }
            // Now change any inheriting from this class
            recClassInfo.close();
            while (recClassInfo.hasNext())
            {
                recClassInfo.next();
                if (recClassInfo.getField(ClassInfo.BASE_CLASS_NAME).toString().equals(strFromField))
                {
                    recClassInfo.edit();
                    recClassInfo.getField(ClassInfo.BASE_CLASS_NAME).setString(strToField);
                    Utility.getLogger().info(recClassInfo.toString());
                    recClassInfo.set();
                }
            }
            recClassInfo.free();
            recClassInfo = null;
        } catch (DBException ex)    {
            ex.printStackTrace();
        }
    }
    /**
     *
     */
    public void fixDetail(Record recClassInfo, Record recDetail, String strFromField, String strToField)
    {
        int iKeyArea = 1;
        KeyArea keyArea = recDetail.getKeyArea(iKeyArea);
        BaseField field = keyArea.getField(0);
        int iSeq = 0;
        for (iSeq = 0; iSeq < recDetail.getFieldCount(); iSeq++)
        {
            if (recDetail.getField(iSeq) == field)
                break;
        }
        StringSubFileFilter filter = null;
        if (recDetail.getListener(SubFileFilter.class.getName()) == null)
        {
            recDetail.setKeyArea(iKeyArea);
            recDetail.addListener(filter = new StringSubFileFilter(recClassInfo.getField(ClassInfo.CLASS_NAME).toString(), iSeq, null, -1, null, -1));
        }
        try   {
            recDetail.close();
            while (recDetail.hasNext())
            {
                recDetail.next();
                recDetail.edit();
//              Utility.getLogger().info(recDetail.getField(LogicFile.METHOD_NAME).toString());
                recDetail.getField(iSeq).setString(strToField);
                if (recDetail.getTableNames(false).equals("LogicFile"))
                    if (recDetail.getField(LogicFile.METHOD_NAME).toString().equals(strFromField))
                {
                    recDetail.getField(LogicFile.METHOD_NAME).setString(strToField);
                }

                filter.setEnabledListener(false);
                recDetail.set();
                filter.setEnabledListener(true);
            }
        } catch (DBException ex)    {
            ex.printStackTrace();
        }
    }
}
