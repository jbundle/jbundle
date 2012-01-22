/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.app.program.manual.util;

import java.util.Map;

import org.jbundle.base.db.Record;
import org.jbundle.base.db.filter.CompareFileFilter;
import org.jbundle.base.db.filter.StringSubFileFilter;
import org.jbundle.base.field.ReferenceField;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.screen.model.BasePanel;
import org.jbundle.base.screen.model.Screen;
import org.jbundle.base.screen.model.util.ScreenLocation;
import org.jbundle.app.program.db.ClassInfo;
import org.jbundle.app.program.db.ClassProject;
import org.jbundle.app.program.db.FileHdr;
import org.jbundle.model.app.program.db.ClassProjectModel.CodeType;
import org.jbundle.app.program.manual.util.data.NameList;
import org.jbundle.model.DBException;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.Converter;


/**
 *  WriteJava - Constructor.
 */
public class WriteClasses extends Screen
{
    protected String m_strFileName = null;
    protected String m_strPackage = null;
    protected String m_strProjectID = null;

    protected NameList m_ClassNameList = null;     // List of all classes written so far
    
    /**
     * Constructor.
     */
    public WriteClasses()
    {
        super();
    }
    /**
     * Constructor.
     */
    public WriteClasses(Record mainFile, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String,Object> properties)
    {
        this();
        this.init(mainFile, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
    }
    /**
     * Init.
     */
    public void init(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String, Object> properties)
    {
        m_strFileName = null;
        m_strPackage = null;
        m_strProjectID = null;
        
        m_ClassNameList = null;     // List of all classes written so far
    
    // Open the source files
        super.init(record, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);

//      this.writeFileDesc();   // Write the code
//          BasePanel panel = screen.getRootScreen();
//          panel.free();
    }
    /**
     *  Free.
     */
    public void free()
    {
        super.free();
    }
    /**
     * This is a special method that runs some code when this screen is opened as a task.
     */
    public void run()
    {
        m_strFileName = this.getProperty("fileName");
        if (m_strFileName == null)
            m_strFileName = Constants.BLANK;
        m_strPackage = this.getProperty("package");
        if (m_strPackage == null)
            m_strPackage = Constants.BLANK;
        m_strProjectID = this.getProperty("project");
        if (m_strProjectID == null)
            m_strProjectID = Constants.BLANK;
        this.writeFileDesc();   // Write the code
        BasePanel panel = this.getRootScreen();
        panel.free();
    }
    public Record openMainRecord()
    {
        return new FileHdr(this);
    }
    public void openOtherRecords()
    {
    	super.openOtherRecords();
        new ClassInfo(this);
    }
    /**
     *
     */
    public void setupSFields()
    {   // Override this to add screen behaviors
        Record query = this.getMainRecord();
        query.getField(FileHdr.kFileName).setupFieldView(this);   // Add this view to the list
    }
    /**
     *  Create the default file Class
     */
    public void writeFileDesc()
    {
    // Now write the record classes for any other records belonging in this source file
        ClassInfo classInfo = new ClassInfo(this);
        String strFileNameTarget = m_strFileName;
        String strBaseFileName = DBConstants.BLANK;
        classInfo.setKeyArea(ClassInfo.kClassSourceFileKey);
        if ((m_strFileName != null)
            && (m_strFileName.length() != 0))
        {
            StringSubFileFilter fileBehavior = new StringSubFileFilter(m_strFileName, ClassInfo.kClassSourceFile, null, -1, null, -1);
            fileBehavior.setEndKey(false);          // Manually check for EOF
            classInfo.addListener(fileBehavior);    // Only select records which match m_strFileName
        }
        if (m_strPackage.length() != 0)
            classInfo.addListener(new CompareFileFilter(ClassInfo.kClassPackage, m_strPackage, "=", null, false));
        if (m_strProjectID.length() != 0)
            classInfo.addListener(new CompareFileFilter(ClassInfo.kClassProjectID, m_strProjectID, "=", null, false));

        try   {
            classInfo.close();
            while (classInfo.hasNext())
            {
                classInfo.next();
                String strClassName = classInfo.getField(ClassInfo.kClassName).getString();
                String strRecordType = classInfo.getField(ClassInfo.kClassType).getString();
                if (strRecordType.length() == 0)
                    strRecordType = " ";
                String strPackage =  classInfo.getField(ClassInfo.kClassPackage).getString();
                String strFileName = classInfo.getField(ClassInfo.kClassSourceFile).getString();
                Record classProject = ((ReferenceField)classInfo.getField(ClassInfo.kClassProjectID)).getReference();
                if (classProject != null)
                	if ((classProject.getEditMode() == DBConstants.EDIT_IN_PROGRESS) || (classProject.getEditMode() == DBConstants.EDIT_CURRENT))
                		if (classProject.getField(ClassProject.kSystemClasses).getState() == true)	// Special system classes, don't generate!
                        strFileName = DBConstants.BLANK; // Skip these
                if (strFileName.length() == 0)
                    continue;
                if (!strBaseFileName.equals(strFileName))
                {
                    if (strBaseFileName.length() != 0)
                    {   // Not the first file
                        if ((strFileNameTarget != null) && (strFileNameTarget.length() > 0))
                            if (strFileName.length() < strFileNameTarget.length())
                                break;  // EOF
                        if ((strFileNameTarget != null) && (strFileNameTarget.length() > 0))
                            if (strFileName.substring(0, strFileNameTarget.length()).compareTo(strFileNameTarget) > 0)
                                break;  // EOF
                        
                        m_ClassNameList.free();
                        m_ClassNameList = null;
                    }

                    NameList aClassNameList = new NameList();
                    m_ClassNameList = aClassNameList;

                    strBaseFileName = strFileName;    // Current File
                }
                this.writeThisClass(strClassName, strRecordType);
            } // End of write record method(s) loop
            classInfo.close();
        } catch (DBException ex)   {
            ex.printStackTrace();
        }
    }
    /**
     *  Write this class using the correct method for the type.
     */
    public void writeThisClass(String strClassName, String strRecordType)
    {
        Record recFileHdr = this.getRecord(FileHdr.kFileHdrFile);
        try   {
            recFileHdr.addNew();
            recFileHdr.getField(FileHdr.kFileName).setString(strClassName);
            recFileHdr.setKeyArea(FileHdr.kFileNameKey);
            if (recFileHdr.seek(DBConstants.EQUALS))    // Is there a file with this name?
                strRecordType = "Record"; // Just to be sure
        } catch (DBException ex)   {
            ex.printStackTrace();
        }

        if (strRecordType.length() == 0)
            strRecordType = " ";
        if (m_ClassNameList.addName(strClassName) == false)
            return;

        WriteClass writeClass = null;
        if (strRecordType.equalsIgnoreCase("Record"))
            writeClass = new WriteRecordClass(this.getTask(), null, null);
        else if (strRecordType.equalsIgnoreCase("Resource"))
            writeClass = new WriteResourceClass(this.getTask(), null, null);
        else
            writeClass = new WriteClass(this.getTask(), null, null);
        if (writeClass != null)
        {
            writeClass.writeClass(strClassName, CodeType.THICK);
            writeClass.free();
        }

    } // End of write record method(s) loop
}
