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
import org.jbundle.base.db.filter.StringSubFileFilter;
import org.jbundle.base.thread.ProcessRunnerTask;
import org.jbundle.base.util.Environment;
import org.jbundle.base.util.MainApplication;
import org.jbundle.app.program.db.FieldData;
import org.jbundle.app.program.db.KeyInfo;
import org.jbundle.app.program.db.ScreenIn;
import org.jbundle.model.DBException;
import org.jbundle.model.RecordOwnerParent;
import org.jbundle.model.Task;
import org.jbundle.model.util.Util;
import org.jbundle.thin.base.screen.BaseApplet;


/**
 *
 */
public class ConvertField extends ConvertDB
{

    /**
     * Constructor.
     */
    public ConvertField()
    {
        super();
    }
    /**
     * Initialization.
     * @param taskParent Optional task param used to get parent's properties, etc.
     * @param recordMain Optional main record.
     * @param properties Optional properties object (note you can add properties later).
     */
    public ConvertField(Task taskParent, Record recordMain, Map<String, Object> properties)
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
        ConvertField test = new ConvertField(task, null, null);
        test.go();
        test.free();
        test = null;
        System.exit(0);
    }
    public void go()
    {
        super.go();     // Convert the DB source code.
        try   {
            if (m_strChange != null)
            {
                for (int i = 0; i < m_strChange.length; i++)
                {
                    String strFromFile = m_strChange[i][0].substring(0, m_strChange[i][0].indexOf(".k"));
                    String strFromField =  m_strChange[i][0].substring(m_strChange[i][0].indexOf(".k") + 2);
                    String strToField = null;
                    if (m_strChange[i][1].indexOf(".k") != -1)
                        strToField = m_strChange[i][1].substring(m_strChange[i][1].indexOf(".k") + 2);
                    else
                        strToField = m_strChange[i][1].substring(m_strChange[i][1].indexOf(".") + 1);
                    
                    this.convertFields(strFromFile, strFromField, strToField);
                    this.convertKeys(strFromFile, strFromField, strToField);
                }
                
                this.convertScreenFields();
            }

        } catch (DBException e)   {
            System.out.print("Error reading through file: Error" + e.getMessage() + "\n");
        }

    }
    public void convertFields(String strFromFile, String strFromField, String strToField) throws DBException
    {
        FieldData fieldInfo = new FieldData(null);
        fieldInfo.setKeyArea(FieldData.kFieldNameKey);
        fieldInfo.addListener(new StringSubFileFilter(strFromFile, FieldData.kFieldFileName, null, -1, null, -1));
        while (fieldInfo.hasNext())
        {
            fieldInfo.next();
            if (fieldInfo.getField(FieldData.kFieldName).toString().equals(strFromField))
            {
                System.out.println("Fixing field " + strToField);
                fieldInfo.edit();
                fieldInfo.getField(FieldData.kFieldName).setString(strToField);
                fieldInfo.set();
            }
        }
        fieldInfo.free();
        fieldInfo = null;
    }
    public void convertKeys(String strFromFile, String strFromField, String strToField) throws DBException
    {
        KeyInfo fieldInfo = new KeyInfo(null);
        fieldInfo.setKeyArea(KeyInfo.kKeyFilenameKey);
        fieldInfo.addListener(new StringSubFileFilter(strFromFile, KeyInfo.kKeyFilename, null, -1, null, -1));
        while (fieldInfo.hasNext())
        {
            fieldInfo.next();
            boolean bChanged = false;
            for (int iFieldSeq = KeyInfo.kKeyField1; iFieldSeq <= KeyInfo.kKeyField9; iFieldSeq++)
            {
                if (fieldInfo.getField(iFieldSeq).toString().equals(strFromField))
                {
                    if (!bChanged)
                    {
                        System.out.println("Fixing key " + strToField);
                        fieldInfo.edit();
                    }
                    fieldInfo.getField(iFieldSeq).setString(strToField);
                    bChanged = true;
                }
            }
            if (bChanged)
                fieldInfo.set();
        }
        fieldInfo.free();
        fieldInfo = null;
    }
    public void convertScreenFields() throws DBException
    {
        ScreenIn screenIn = new ScreenIn(null);
        while (screenIn.hasNext())
        {
            screenIn.next();
            for (int i = 0; i < m_strChange.length; i++)
            {
                String strFromFile = m_strChange[i][0].substring(0, m_strChange[i][0].indexOf(".k"));
                String strFromField =  m_strChange[i][0].substring(m_strChange[i][0].indexOf(".k") + 2);
                String strToField =  m_strChange[i][1].substring(m_strChange[i][1].indexOf(".k") + 2);
                
                if (screenIn.getField(ScreenIn.kScreenFileName).toString().equals(strFromFile))
                    if (screenIn.getField(ScreenIn.kScreenFieldName).toString().equals(strFromField))
                {
                    System.out.println("Fixing screen " + strToField);
                    screenIn.edit();
                    screenIn.getField(ScreenIn.kScreenFieldName).setString(strToField);
                    screenIn.set();
                }
            }
        }
        screenIn.free();
        screenIn = null;
    }
}
