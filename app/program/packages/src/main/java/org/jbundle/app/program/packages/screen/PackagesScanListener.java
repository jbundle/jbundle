/**
 * @(#)PackagesScanListener.
 * Copyright © 2013 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.program.packages.screen;

import java.util.*;

import org.jbundle.base.db.*;
import org.jbundle.thin.base.util.*;
import org.jbundle.thin.base.db.*;
import org.jbundle.base.db.event.*;
import org.jbundle.base.db.filter.*;
import org.jbundle.base.field.*;
import org.jbundle.base.field.convert.*;
import org.jbundle.base.field.event.*;
import org.jbundle.base.model.*;
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.model.db.*;
import org.jbundle.model.screen.*;
import org.jbundle.app.program.script.scan.*;
import java.io.*;
import org.jbundle.app.program.manual.convert.*;
import org.jbundle.app.program.db.*;
import org.jbundle.app.program.packages.db.*;

/**
 *  PackagesScanListener - Scan directories and update the packages file.
 */
public class PackagesScanListener extends BaseScanListener
{
    protected Packages m_recPackages = null;
    /**
     * Default constructor.
     */
    public PackagesScanListener()
    {
        super();
    }
    /**
     * Constructor.
     */
    public PackagesScanListener(ConvertCode parent, Packages recPackages)
    {
        this();
        this.init(parent, recPackages);
    }
    /**
     * Initialize class fields.
     */
    public void init(ConvertCode parent, Packages recPackages)
    {
        m_recPackages = null;
        m_recPackages = recPackages;
        super.init(parent, null);
    }
    /**
     * If this file should be processed, return true.
     */
    public boolean filterDirectory(File file)
    {
        if ("META-INF".equalsIgnoreCase(file.getName()))
            return false;
        return super.filterDirectory(file);   // All valid directories
    }
    /**
     * Do whatever processing that needs to be done on this directory.
     * @return caller specific information about this directory.
     */
    public Object processThisDirectory(File fileDir, Object objDirID)
    {
        int iCounter = 0;
        try {
            String directoryName = fileDir.getName();
        
            boolean containsFiles = this.directoryContainsFiles(fileDir);
            
            ClassProject.CodeType codeType = (ClassProject.CodeType)this.getProperties().get("codeType");
            String projectString = this.getProperty("projectID");
            int projectID = Integer.parseInt(Converter.stripNonNumber(projectString));
                    
            Integer intParent = (Integer)objDirID;
            if (intParent != null)
            {
                Object bookmark = null;
                m_recPackages.addNew();
                m_recPackages.setKeyArea(Packages.NAME_KEY);
                m_recPackages.getField(Packages.PARENT_FOLDER_ID).setValue(intParent);
                m_recPackages.getField(Packages.NAME).setString(directoryName);
                m_recPackages.getField(Packages.CLASS_PROJECT_ID).setData(null);
                boolean recordExists = false;
                if ((m_recPackages.seek(">=") == true)
                    && (m_recPackages.getField(Packages.PARENT_FOLDER_ID).getValue() == intParent)
                    && (m_recPackages.getField(Packages.NAME).toString().equalsIgnoreCase(directoryName)))
                        recordExists = true;
                if (recordExists)
                { // Good, one exists already, use it (note: this reads the default [0] if it exists)
                    bookmark = m_recPackages.getHandle(DBConstants.BOOKMARK_HANDLE);
                    ClassProject.CodeType fieldCodeType = ((CodeTypeField)m_recPackages.getField(Packages.CODE_TYPE)).getCodeType();
                    m_recPackages.edit(); // Always updates the "last updated" time
                    if (containsFiles)
                    {
                        if ((m_recPackages.getField(Packages.CLASS_PROJECT_ID).getValue() == 0)   // No project yet, I own it
                                || (ClassProject.CodeType.THICK == codeType)    // If there are files in a base dir, I own it
                                || (fieldCodeType == codeType)  // Another dir of the same type is default, now I own it
                                || (ClassProject.CodeType.THICK != codeType) && (ClassProject.CodeType.THICK != fieldCodeType))   // base doesn't own it, I own it
                        {
                            m_recPackages.getField(Packages.CLASS_PROJECT_ID).setValue(projectID);  // If there are files, I own it.
                            ((CodeTypeField)m_recPackages.getField(Packages.CODE_TYPE)).setCodeType(codeType);
                        }
                    }
                    else
                    {
                        if (m_recPackages.getField(Packages.CLASS_PROJECT_ID).getValue() == projectID)
                            if (fieldCodeType == codeType)
                        { // That's weird. I should not own it if there are no files in here.
                            m_recPackages.getField(Packages.CLASS_PROJECT_ID).setValue(0); // This says it is empty (will be replaced when I find a dir with files in it)
                        }
                    }
                    m_recPackages.handleRecordChange(m_recPackages.getField(Packages.PARENT_FOLDER_ID), DBConstants.FIELD_CHANGED_TYPE, true);     // init this field override for other value
                    m_recPackages.set();                        
                }
                else
                { // None exists, create a default entry
                    m_recPackages.addNew();
                    m_recPackages.getField(Packages.PARENT_FOLDER_ID).setValue(intParent);
                    m_recPackages.getField(Packages.NAME).setString(directoryName);
                    if (containsFiles)
                        m_recPackages.getField(Packages.CLASS_PROJECT_ID).setValue(projectID);  // If there are files, I own it.
                    else
                        m_recPackages.getField(Packages.CLASS_PROJECT_ID).setValue(0);
                    ((CodeTypeField)m_recPackages.getField(Packages.CODE_TYPE)).setCodeType(codeType);
                    m_recPackages.add();
                    bookmark = m_recPackages.getLastModified(DBConstants.BOOKMARK_HANDLE);                      
                }
        
                // Now claim ownership of any empty directories above this
                while (m_recPackages.getField(Packages.PARENT_FOLDER_ID).getValue() != 0)
                {
                    int parentFolderID = (int)m_recPackages.getField(Packages.PARENT_FOLDER_ID).getValue();
                    m_recPackages.setKeyArea(Packages.ID_KEY);
                    m_recPackages.addNew();
                    m_recPackages.getField(Packages.ID).setValue(parentFolderID);
                    if ((!m_recPackages.seek(null)) || (m_recPackages.getEditMode() != DBConstants.EDIT_CURRENT))
                        break;
                    if (m_recPackages.getField(Packages.CLASS_PROJECT_ID).getValue() == 0)
                    { // Any empty parents belong to the same project
                        m_recPackages.edit();
                        if (containsFiles)
                            m_recPackages.getField(Packages.CLASS_PROJECT_ID).setValue(projectID);  // If there are files in child, I own it (for now)
                        else
                            m_recPackages.getField(Packages.CLASS_PROJECT_ID).setValue(0);
                        ((CodeTypeField)m_recPackages.getField(Packages.CODE_TYPE)).setCodeType(codeType);
                        m_recPackages.set();
                    }
                }
        
                m_recPackages.setHandle(bookmark, DBConstants.BOOKMARK_HANDLE);
                iCounter = (int)m_recPackages.getField(Packages.ID).getValue();
            }
        } catch (DBException ex)    {
            ex.printStackTrace();
        }
        return new Integer(iCounter);
    }
    /**
     * Get the projectID if this directory has any files in it.
     * Otherwise, return 0 meaning I don't have to specify this package.
     */
    public boolean directoryContainsFiles(File fileDir)
    {
        boolean containsFiles = false;
        File[] files = fileDir.listFiles();
        if (files != null)
        {
            for (File file : files)
            {
                if (!file.isDirectory())
                    containsFiles = true;
            }
        }
        return containsFiles;
    }
    /**
     * If this file should be processed, return true.
     */
    public boolean filterFile(File file)
    {
        return false; // Not used
    }
    /**
     * Do whatever processing that needs to be done on this file.
     */
    public void moveThisFile(File fileSource, File fileDestDir, String strDestName)
    {
        // Do not process any files
    }

}
