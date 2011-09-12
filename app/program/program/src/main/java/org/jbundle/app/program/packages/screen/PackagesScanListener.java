/**
 * @(#)PackagesScanListener.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.program.packages.screen;

import java.awt.*;
import java.util.*;

import org.jbundle.base.db.*;
import org.jbundle.thin.base.util.*;
import org.jbundle.thin.base.db.*;
import org.jbundle.base.db.event.*;
import org.jbundle.base.db.filter.*;
import org.jbundle.base.field.*;
import org.jbundle.base.field.convert.*;
import org.jbundle.base.field.event.*;
import org.jbundle.base.screen.model.*;
import org.jbundle.base.screen.model.util.*;
import org.jbundle.base.util.*;
import org.jbundle.model.*;
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
                m_recPackages.setKeyArea(Packages.kNameKey);
                m_recPackages.getField(Packages.kParentFolderID).setValue(intParent);
                m_recPackages.getField(Packages.kName).setString(directoryName);
                m_recPackages.getField(Packages.kClassProjectID).setData(null);
                boolean recordExists = false;
                if ((m_recPackages.seek(">=") == true)
                    && (m_recPackages.getField(Packages.kParentFolderID).getValue() == intParent)
                    && (m_recPackages.getField(Packages.kName).toString().equalsIgnoreCase(directoryName)))
                        recordExists = true;
                if (recordExists)
                { // Good, one exists already, use it (note: this reads the default [0] if it exists)
                    bookmark = m_recPackages.getHandle(DBConstants.BOOKMARK_HANDLE);
                    ClassProject.CodeType fieldCodeType = ((CodeTypeField)m_recPackages.getField(Packages.kCodeType)).getCodeType();
                    m_recPackages.edit(); // Always updates the "last updated" time
                    if (containsFiles)
                    {
                        if ((m_recPackages.getField(Packages.kClassProjectID).getValue() == 0)  // No project yet, I own it
                                || (ClassProject.CodeType.BASE == codeType)   // If there are files in a base dir, I own it
                                || (fieldCodeType == codeType)  // Another dir of the same type is default, now I own it
                                || (ClassProject.CodeType.BASE != codeType) && (ClassProject.CodeType.BASE != fieldCodeType)) // base doesn't own it, I own it
                        {
                            m_recPackages.getField(Packages.kClassProjectID).setValue(projectID); // If there are files, I own it.
                            ((CodeTypeField)m_recPackages.getField(Packages.kCodeType)).setCodeType(codeType);
                        }
                    }
                    else
                    {
                        if (m_recPackages.getField(Packages.kClassProjectID).getValue() == projectID)
                            if (fieldCodeType == codeType)
                        { // That's weird. I should not own it if there are no files in here.
                            m_recPackages.getField(Packages.kClassProjectID).setValue(0); // This says it is empty (will be replaced when I find a dir with files in it)
                        }
                    }
                    m_recPackages.handleRecordChange(m_recPackages.getField(Packages.kParentFolderID), DBConstants.FIELD_CHANGED_TYPE, true);     // init this field override for other value
                    m_recPackages.set();                        
                }
                else
                { // None exists, create a default entry
                    m_recPackages.addNew();
                    m_recPackages.getField(Packages.kParentFolderID).setValue(intParent);
                    m_recPackages.getField(Packages.kName).setString(directoryName);
                    if (containsFiles)
                        m_recPackages.getField(Packages.kClassProjectID).setValue(projectID); // If there are files, I own it.
                    else
                        m_recPackages.getField(Packages.kClassProjectID).setValue(0);
                    ((CodeTypeField)m_recPackages.getField(Packages.kCodeType)).setCodeType(codeType);
                    m_recPackages.add();
                    bookmark = m_recPackages.getLastModified(DBConstants.BOOKMARK_HANDLE);                      
                }
        
                // Now claim ownership of any empty directories above this
                while (m_recPackages.getField(Packages.kParentFolderID).getValue() != 0)
                {
                    int parentFolderID = (int)m_recPackages.getField(Packages.kParentFolderID).getValue();
                    m_recPackages.setKeyArea(Packages.kIDKey);
                    m_recPackages.addNew();
                    m_recPackages.getField(Packages.kID).setValue(parentFolderID);
                    if ((!m_recPackages.seek(null)) || (m_recPackages.getEditMode() != DBConstants.EDIT_CURRENT))
                        break;
                    if (m_recPackages.getField(Packages.kClassProjectID).getValue() == 0)
                    { // Any empty parents belong to the same project
                        m_recPackages.edit();
                        if (containsFiles)
                            m_recPackages.getField(Packages.kClassProjectID).setValue(projectID); // If there are files in child, I own it (for now)
                        else
                            m_recPackages.getField(Packages.kClassProjectID).setValue(0);
                        ((CodeTypeField)m_recPackages.getField(Packages.kCodeType)).setCodeType(codeType);
                        m_recPackages.set();
                    }
                }
        
                m_recPackages.setHandle(bookmark, DBConstants.BOOKMARK_HANDLE);
                iCounter = (int)m_recPackages.getField(Packages.kID).getValue();
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
