package org.jbundle.app.program.manual.convert;

/**
 * Copyright (c) 1996 jbundle.org. All Rights Reserved.
 *  .
 *      don@tourgeek.com
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Map;

import org.jbundle.base.db.Record;
import org.jbundle.base.thread.BaseProcess;
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
public class SetupJavac extends BaseProcess
{
// This program creates a bat file to compile all files.
        
    /**
     * Constructor.
     */
    public SetupJavac()
    {
        super();
    }
    /**
     * Initialization.
     * @param taskParent Optional task param used to get parent's properties, etc.
     * @param recordMain Optional main record.
     * @param properties Optional properties object (note you can add properties later).
     */
    public SetupJavac(Task taskParent, Record recordMain, Map<String, Object> properties)
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
    
    public static void main (String[] args)
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
        SetupJavac convert = new SetupJavac(task, null, null);
        convert.run();
    }
    
//  public String gJavacCommand = "javac -g -deprecation ";
    public String gJavacCommand = "/java/programs/jikes/jikes -g -deprecation ";
    /**
     * Move the files in this directory to the new directory.
     */
    public void run()
    {
        String strSource = "workspace/tourapp/src/com/tourapp/";
        String strMacro = "set classpath=%classpath%;/Java/jws/jdk1.2/jre/lib/rt.jar;/data/java/tour;/Java/programs/j2sdkee-beta/lib/j2ee.jar;.\n";
//x     strMacro += "javac -g";
//ie        strMacro += " ^\ncom/tourapp/**.java";
    
        File fileDir = new File(strSource);

        if (fileDir.isDirectory())
            strMacro = this.addDirectory(fileDir, strMacro);
        
        try   {
            FileOutputStream fileOut = new FileOutputStream("/Data/Java/Tour/bat/buildall.bat");
            PrintWriter outPW = new PrintWriter(fileOut);
            outPW.println(strMacro);
            outPW.close();
        } catch (FileNotFoundException ex)  {
            ex.printStackTrace();
        }
        
        System.exit(0);
    }
    /**
     * Move the files in this directory to the new directory.
     */
    public String addDirectory(File file, String strMacro)
    {
        File[] fileList = file.listFiles();
        boolean bFilesExist = false;
        for (int i = 0; i < fileList.length; i++)
        {   // Add all files
            if (!fileList[i].isDirectory())
            {
//x             this.addFile(fileList[i], strMacro);
                bFilesExist = true;
            }
        }
        if (bFilesExist)
        {
            String strPath = file.getPath();
            int iPos = strPath.indexOf("com");
            if (iPos != -1)
                strPath = strPath.substring(iPos);
            strMacro += "\n" + gJavacCommand + strPath + "/**.java";
        }
        for (int i = 0; i < fileList.length; i++)
        {   // Add all directories
            if (fileList[i].isDirectory())
                strMacro = this.addDirectory(fileList[i], strMacro);
        }
        return strMacro;
    }
    /**
     * Move the files in this directory to the new directory.
     */
    public String addFile(File file, String strFileList)
    {
        String strName = file.getName();
        int iJava = strName.indexOf(".java");
        if (iJava != -1) if (iJava == strName.length() - 5)
        {
            String strParent = file.getParent();
            String strPath = file.getPath();
            strFileList += strPath + "/" + strParent;
        }
        return strFileList;
    }
    /**
     * Replace the file separators with the correct characters.
     */
    public String fixFilePath(String strSource, char charSeparator)
    {
        String strDest = "";
        for (int i = 0; i < strSource.length(); i++)
        {
            if (strSource.charAt(i) == '/')
            strDest += charSeparator;
            else
            strDest += strSource.charAt(i);
        }
        return strDest;
    }
}
