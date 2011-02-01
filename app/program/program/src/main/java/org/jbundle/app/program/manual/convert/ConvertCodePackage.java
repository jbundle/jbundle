package org.jbundle.app.program.manual.convert;

/**
 * Copyright (c) 1996 jbundle.org. All Rights Reserved.
 *  .
 *      don@tourgeek.com
 */

import java.io.File;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import org.jbundle.base.db.Record;
import org.jbundle.base.thread.ProcessRunnerTask;
import org.jbundle.base.util.Environment;
import org.jbundle.base.util.MainApplication;
import org.jbundle.model.RecordOwnerParent;
import org.jbundle.model.Task;
import org.jbundle.thin.base.screen.BaseApplet;
import org.jbundle.thin.base.util.Util;

/**
import java.test.*;
import com.sun.java.swing.*;
import com.sun.java.swing.text.*;
*/
public class ConvertCodePackage extends ConvertCode
{
    /**
     * Constructor.
     */
    public ConvertCodePackage()
    {
        super();
    }
    /**
     * Initialization.
     * @param taskParent Optional task param used to get parent's properties, etc.
     * @param recordMain Optional main record.
     * @param properties Optional properties object (note you can add properties later).
     */
    public ConvertCodePackage(Task taskParent, Record recordMain, Map<String, Object> properties)
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
    
    public static int m_iPass = 1;
    
    public static void main (String[] args)
    {
        BaseApplet.main(args);      // This says I'm stand-alone
        Map<String,Object> properties = null;
        if (args != null)
        {
            properties = new Hashtable<String,Object>();
            Util.parseArgs(properties, args);
        }
        if (args != null)
        {
            if  (args.length >= 1)
                properties.put(SOURCE_DIR, args[0]);
            if  (args.length >= 2)
            	properties.put(DEST_DIR, args[1]);
            if  (args.length >= 3)
            	properties.put(EXTENSION, args[2]);
        }
        Environment env = new Environment(properties);
        MainApplication app = new MainApplication(env, properties, null);
        ProcessRunnerTask task = new ProcessRunnerTask(app, null, properties);

        ConvertCode convert = new ConvertCodePackage(task, null, properties);
        convert.run();
        String strSourceDir = convert.getProperty(SOURCE_DIR);
        m_iPass = 1;
        convert.run();
//        System.out.println("map: " + m_mapClasses);
        
        int iCount = m_vNames.size();
        m_strChange = new String[iCount][2];
        for (int i = 0; i < iCount; i++)
        {
            String strName = (String)m_vNames.get(i);
            String strParent = (String)m_vParents.get(i);
            if (strName.endsWith(".java"))
                strName = strName.substring(0, strName.length() - 5);
            if (strParent.startsWith(strSourceDir))
                strParent = strParent.substring(strSourceDir.length() + 1);
            m_strChange[i][0] = fixFilePath(strParent, ".") + '.' + strName;
            m_strChange[i][1] = fixFilePath(getSharedPackage(strParent), ".") + '.' + strName;
//if (strName.equals("POSTypez"))            
System.out.println("---------" + m_strChange[i][0] + ' ' + m_strChange[i][1]);
        }
        m_iPass = 2;
//System.exit(0);
        convert.run();
        
//        System.out.println("----------------: " + m_strChange);
    }
    protected static Map<String,String> m_mapClasses = new HashMap<String,String>();
    public static final String SHARED = "shared";
    public boolean m_bCurrentShared = false;
    /**
     * Move the files in this directory to the new directory.
     */
    public void moveThisFile(File file, String strName, String strParentSource)
    {
        if (m_iPass == 1)
        {
            if (!strName.startsWith("ObjectFactory"))
                if (strName.endsWith(".java"))
            {
                if (m_mapClasses.containsKey(strName))
                {
                    if (!SHARED.equals(m_mapClasses.get(strName)))
                    {   // First time, add current entry
                        this.addClassToShared(strName, m_mapClasses.get(strName));
                    }
                    m_mapClasses.put(strName, SHARED);
                    this.addClassToShared(strName, strParentSource);
                }
                else
                {
                    m_mapClasses.put(strName, strParentSource);
                }
            }
        }
        else
        {
            m_bCurrentShared = SHARED.equals(m_mapClasses.get(strName));
            if (m_bCurrentShared)
            {
                strParentSource = ConvertCodePackage.getSharedPackage(strParentSource);
            }
            super.moveThisFile(file, strName, strParentSource);
        }
    }
    public static final Vector<String> m_vNames = new Vector<String>();
    public static final Vector<String> m_vParents = new Vector<String>();
    public void addClassToShared(String strName, String strParent)
    {
        m_vNames.add(strName);
        m_vParents.add(strParent);
    }
    /**
     *
     */
    public static String getSharedPackage(String strParent)
    {
        int iSlash = strParent.lastIndexOf('/');
        if (iSlash != -1)
        {
            if (strParent.endsWith("impl"))
            {
                iSlash = strParent.lastIndexOf('/', iSlash - 1);
                strParent = strParent.substring(0, iSlash + 1) + "shared/impl";
            }
            else
                strParent = strParent.substring(0, iSlash + 1) + "shared";
        }
        return strParent;
    }
    public static String m_strPkg = "org.opentravel.b2001.shared";
    /**
     * Convert this line to the output format.
     */
    public String convertString(String string)
    {
        if (string.indexOf("package") == 0)
        {
            String strPkg = m_strPkg;
            if (string.indexOf(".impl") != -1)
                strPkg = strPkg + ".impl";
            if (m_bCurrentShared)
                string = "package " + strPkg + ";";
            else
                string += "\n" + "import " + strPkg + ".*;";
        }
        else
        {
            return super.convertString(string);
        }
        return string;
    }
}
