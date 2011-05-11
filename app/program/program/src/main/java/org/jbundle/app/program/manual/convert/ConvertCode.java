package org.jbundle.app.program.manual.convert;

/**
 * Copyright (c) 1996 jbundle.org. All Rights Reserved.
 *  .
 *      don@tourgeek.com
 */

import java.io.File;
import java.util.Hashtable;
import java.util.Map;

import org.jbundle.app.program.script.scan.BaseScanListener;
import org.jbundle.app.program.script.scan.ReplaceScanListener;
import org.jbundle.app.program.script.scan.ScanListener;
import org.jbundle.base.db.Record;
import org.jbundle.base.thread.ProcessRunnerTask;
import org.jbundle.base.util.DBConstants;
import org.jbundle.base.util.Environment;
import org.jbundle.base.util.MainApplication;
import org.jbundle.base.util.Utility;
import org.jbundle.model.RecordOwnerParent;
import org.jbundle.model.Task;
import org.jbundle.model.util.Util;
import org.jbundle.thin.base.screen.BaseApplet;
import org.jbundle.thin.base.util.ThinUtil;

/**
import java.test.*;
import com.sun.java.swing.*;
import com.sun.java.swing.text.*;
*/
public class ConvertCode extends ConvertBase
	implements ConvertConstants
{

	/**
    *
    */
    protected ScanListener m_listener = null;
    
    /**
     * Constructor.
     */
    public ConvertCode()
    {
        super();
    }
    /**
     * Initialization.
     * @param taskParent Optional task param used to get parent's properties, etc.
     * @param recordMain Optional main record.
     * @param properties Optional properties object (note you can add properties later).
     */
    public ConvertCode(Task taskParent, Record recordMain, Map<String, Object> properties)
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
// This program moves all the java files from one directory structure to another and converts strings in the move
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
        ConvertCode convert = new ConvertCode(task, null, properties);
        convert.run();
    }
    /**
     * Move the files in this directory to the new directory.
     */
    public void run()
    {
        String sourceDir = this.getProperty(SOURCE_DIR);
        sourceDir = ConvertBase.fixFilePath(sourceDir, File.separator);
        this.setProperty(SOURCE_DIR, sourceDir);
        
        String destDir = this.getProperty(DEST_DIR);
        destDir = ConvertBase.fixFilePath(destDir, File.separator);
        if (destDir != null)
        	if (destDir.equals(sourceDir))
        		destDir += "out";
        this.setProperty(DEST_DIR, destDir);
        
        String strSourcePackage = this.getProperty(SOURCE_PACKAGE);
        if (strSourcePackage == null)
        	strSourcePackage = ConvertBase.fixFilePath(sourceDir, ".");
        String strDestPackage = this.getProperty(DEST_PACKAGE);
        if (strDestPackage == null)
        	strDestPackage = ConvertBase.fixFilePath(destDir, ".");

        sourceDir = this.getFullPath(sourceDir);

        File fileDir = new File(sourceDir);
        
        if (fileDir.isDirectory())
            this.moveDirectory(fileDir, null);
    }
    /**
     * Move the files in this directory to the new directory.
     * @param objParentID Parent id
     */
    public void moveDirectory(File file, Object objParentID)
    {
    	if (this.getScanListener() != null)
    		objParentID = this.getScanListener().processThisDirectory(file, objParentID);
        File[] fileList = file.listFiles();
        for (int i = 0; i < fileList.length; i++)
        {
            if (fileList[i].isDirectory())
            {
            	if (this.getScanListener() != null)
                {
                    if (this.getScanListener().filterDirectory(fileList[i]))
                        this.moveDirectory(fileList[i], objParentID);
                }
            	else
            		this.moveDirectory(fileList[i], objParentID);
            }
            else if (fileList[i].isFile())
                this.moveThisFile(fileList[i]);
        }
    	if (this.getScanListener() != null)
    		this.getScanListener().postProcessThisDirectory(file, objParentID);
    }
    /**
     * Move the files in this directory to the new directory.
     */
    public void moveThisFile(File file)
    {
        String strDestName = file.getName();
        boolean bMoveFile = true;
        String extension = this.getProperty(EXTENSION);
        String filter = this.getProperty(FILTER);
    	if (extension != null)
    	{
    		bMoveFile = false;
            int iJava = strDestName.lastIndexOf(extension);
            if (iJava != -1) if (iJava == strDestName.length() - extension.length())
            	bMoveFile = true;
    	}
    	if (filter != null)
    	{
            bMoveFile = strDestName.matches(filter);
    	}
    	if ((bMoveFile) && (this.getScanListener() != null))
    		bMoveFile = this.getScanListener().filterFile(file);
        if (bMoveFile)
        {
            String strParentSource = file.getParent();
            this.moveThisFile(file, strDestName, strParentSource);
        }
    }
    /**
     * Move this file.
     */
    public void moveThisFile(File file, String destName, String parentSource)
    {
    	File fileDestDir = this.getDestPath(parentSource);
        this.getScanListener().moveThisFile(file, fileDestDir, destName);
    }
    /**
     * Get the destination pathname.
     * @param sourcePath The path to the source file
     * @return The path to place the destination file
     */
    public File getDestPath(String sourcePath)
    {
        String destPathname = DBConstants.BLANK;

        String dirPrefix = this.getProperty(DIR_PREFIX);
        String sourceDir = this.getProperty(SOURCE_DIR);
        String destDir = this.getProperty(DEST_DIR);
        int startPath = sourcePath.indexOf(sourceDir);
        
        // Now add the start of the source path
        if ((dirPrefix != null) && (sourceDir != null) && (sourceDir.length() > 0) && (sourcePath.indexOf(dirPrefix) == 0))
    		destPathname += dirPrefix;
        else
        {
        	if ((!destDir.startsWith("/")) && (!destDir.startsWith(File.separator)))
        	{
        		if (startPath == 0)
        		{
        			int iEndStartPath = sourceDir.lastIndexOf(File.separator);
        			if (iEndStartPath == -1)
        				iEndStartPath = sourceDir.lastIndexOf('/');
        			if (iEndStartPath > 0)
        				destPathname += sourceDir.substring(0, iEndStartPath + 1);
        		}
        	}
        }
        
        destPathname = Utility.addToPath(destPathname, destDir);
        
        if (startPath != -1)
        	destPathname = Utility.addToPath(destPathname, sourcePath.substring(startPath + sourceDir.length()));

        return new File(destPathname);
    }
    /**
     * Set the scan listener.
     * @param listener
     */
    public void setScanListener(ScanListener listener)
    {
    	m_listener = listener;
    }
    /**
     * Get the scan listener.
     * @returns listener
     */
    public ScanListener getScanListener()
    {
    	if (m_listener == null)
    	{
    		String strClassName = this.getProperty(LISTENER_CLASS);
    		if (strClassName == null)
    			strClassName = ReplaceScanListener.class.getName();
			m_listener = (ScanListener)ThinUtil.getClassService().makeObjectFromClassName(strClassName);
			if (m_listener != null)
				((BaseScanListener)m_listener).init(this, null);
			else
				System.exit(0);
    	}
    	return m_listener;
    }
    /**
     * Get the full path name.
     * @param pathToFix
     * @return
     */
    public String getFullPath(String pathToFix)
    {
        String dirPrefix = this.getProperty(DIR_PREFIX);
        if (dirPrefix == null)
        	dirPrefix = Utility.addToPath(System.getProperty("user.home"), "workspace/tourapp/src/com/tourapp");
        if (pathToFix == null)
            pathToFix = dirPrefix;
        else
        	if ((!pathToFix.startsWith("/")) && (!pathToFix.startsWith(".")) && (!pathToFix.startsWith(File.separator)))
        		pathToFix = Utility.addToPath(dirPrefix, pathToFix);
        return pathToFix;
    }
}
