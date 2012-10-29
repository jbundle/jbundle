/**
 * @(#)ClassProject.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.program.db;

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
import org.jbundle.base.model.*;
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.model.db.*;
import org.jbundle.model.screen.*;
import org.jbundle.main.db.*;
import org.jbundle.app.program.resource.db.*;
import org.jbundle.model.app.program.db.*;

/**
 *  ClassProject - .
 */
public class ClassProject extends Folder
     implements ClassProjectModel
{
    private static final long serialVersionUID = 1L;

    public static final int CLASS_DETAIL_MODE = ScreenConstants.DETAIL_MODE | ScreenConstants.LAST_MODE * 4;
    public static final int RESOURCE_DETAIL_MODE = ScreenConstants.DETAIL_MODE | ScreenConstants.LAST_MODE * 8;
    /**
     * Default constructor.
     */
    public ClassProject()
    {
        super();
    }
    /**
     * Constructor.
     */
    public ClassProject(RecordOwner screen)
    {
        this();
        this.init(screen);
    }
    /**
     * Initialize class fields.
     */
    public void init(RecordOwner screen)
    {
        super.init(screen);
    }
    /**
     * Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? Record.formatTableNames(CLASS_PROJECT_FILE, bAddQuotes) : super.getTableNames(bAddQuotes);
    }
    /**
     * Get the Database Name.
     */
    public String getDatabaseName()
    {
        return "program";
    }
    /**
     * Is this a local (vs remote) file?.
     */
    public int getDatabaseType()
    {
        return DBConstants.REMOTE | DBConstants.SHARED_DATA | DBConstants.HIERARCHICAL;
    }
    /**
     * MakeScreen Method.
     */
    public ScreenParent makeScreen(ScreenLoc itsLocation, ComponentParent parentScreen, int iDocMode, Map<String,Object> properties)
    {
        ScreenParent screen = null;
        if ((iDocMode & ClassProject.CLASS_DETAIL_MODE) == ClassProject.CLASS_DETAIL_MODE)
            screen = Record.makeNewScreen(ClassInfo.CLASS_INFO_SCREEN_CLASS, itsLocation, parentScreen, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties, this, true);
        else if ((iDocMode & ClassProject.RESOURCE_DETAIL_MODE) == ClassProject.RESOURCE_DETAIL_MODE)
            screen = Record.makeNewScreen(Resource.RESOURCE_GRID_SCREEN_CLASS, itsLocation, parentScreen, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties, this, true);
        else if ((iDocMode & ScreenConstants.DETAIL_MODE) == ScreenConstants.DETAIL_MODE)
            screen = Record.makeNewScreen(CLASS_PROJECT_GRID_SCREEN_CLASS, itsLocation, parentScreen, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties, this, true);
        else if ((iDocMode & ScreenConstants.MAINT_MODE) == ScreenConstants.MAINT_MODE)
            screen = Record.makeNewScreen(CLASS_PROJECT_SCREEN_CLASS, itsLocation, parentScreen, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties, this, true);
        else if ((iDocMode & ScreenConstants.DISPLAY_MODE) != 0)
            screen = Record.makeNewScreen(CLASS_PROJECT_GRID_SCREEN_CLASS, itsLocation, parentScreen, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties, this, true);
        else if ((iDocMode & ScreenConstants.MENU_MODE) != 0)
            screen = Record.makeNewScreen(CLASS_PROJECT_SCREEN_CLASS, itsLocation, parentScreen, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties, this, true);
        else
            screen = super.makeScreen(itsLocation, parentScreen, iDocMode, properties);
        return screen;
    }
    /**
     * Add this field in the Record's field sequence.
     */
    public BaseField setupField(int iFieldSeq)
    {
        BaseField field = null;
        //if (iFieldSeq == 0)
        //{
        //  field = new CounterField(this, ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        //  field.setHidden(true);
        //}
        //if (iFieldSeq == 1)
        //{
        //  field = new RecordChangedField(this, LAST_CHANGED, Constants.DEFAULT_FIELD_LENGTH, null, null);
        //  field.setHidden(true);
        //}
        //if (iFieldSeq == 2)
        //{
        //  field = new BooleanField(this, DELETED, Constants.DEFAULT_FIELD_LENGTH, null, new Boolean(false));
        //  field.setHidden(true);
        //}
        if (iFieldSeq == 3)
        {
            field = new StringField(this, NAME, 40, null, null);
            field.setNullable(false);
        }
        if (iFieldSeq == 4)
            field = new ClassProjectField(this, PARENT_FOLDER_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        //if (iFieldSeq == 5)
        //  field = new ShortField(this, SEQUENCE, Constants.DEFAULT_FIELD_LENGTH, null, null);
        //if (iFieldSeq == 6)
        //  field = new MemoField(this, COMMENT, Constants.DEFAULT_FIELD_LENGTH, null, null);
        //if (iFieldSeq == 7)
        //  field = new StringField(this, CODE, 30, null, null);
        if (iFieldSeq == 8)
            field = new StringField(this, DESCRIPTION, 40, null, null);
        if (iFieldSeq == 9)
            field = new BooleanField(this, SYSTEM_CLASSES, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 10)
            field = new StringField(this, PACKAGE_NAME, 30, null, null);
        if (iFieldSeq == 11)
            field = new StringField(this, PROJECT_PATH, 128, null, null);
        if (iFieldSeq == 12)
            field = new StringField(this, INTERFACE_PACKAGE, 40, null, null);
        if (iFieldSeq == 13)
            field = new StringField(this, INTERFACE_PROJECT_PATH, 128, null, null);
        if (iFieldSeq == 14)
            field = new StringField(this, THIN_PACKAGE, 40, null, null);
        if (iFieldSeq == 15)
            field = new StringField(this, THIN_PROJECT_PATH, 128, null, null);
        if (iFieldSeq == 16)
            field = new StringField(this, RESOURCE_PACKAGE, 40, null, null);
        if (iFieldSeq == 17)
            field = new StringField(this, RES_PROJECT_PATH, 128, null, null);
        if (iFieldSeq == 18)
            field = new PropertiesField(this, PROPERTIES, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 19)
            field = new StringField(this, ARTIFACT_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 20)
            field = new StringField(this, GROUP_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (field == null)
            field = super.setupField(iFieldSeq);
        return field;
    }
    /**
     * Add this key area description to the Record.
     */
    public KeyArea setupKey(int iKeyArea)
    {
        KeyArea keyArea = null;
        if (iKeyArea == 0)
        {
            keyArea = this.makeIndex(DBConstants.UNIQUE, "ID");
            keyArea.addKeyField(ID, DBConstants.ASCENDING);
        }
        if (iKeyArea == 1)
        {
            keyArea = this.makeIndex(DBConstants.NOT_UNIQUE, "ParentFolderID");
            keyArea.addKeyField(PARENT_FOLDER_ID, DBConstants.ASCENDING);
        }
        if (iKeyArea == 2)
        {
            keyArea = this.makeIndex(DBConstants.NOT_UNIQUE, "Name");
            keyArea.addKeyField(NAME, DBConstants.ASCENDING);
        }
        if (keyArea == null)
            keyArea = super.setupKey(iKeyArea);     
        return keyArea;
    }
    /**
     * Convert the command to the screen document type.
     * @param strCommand The command text.
     * @param The standard document type (MAINT/DISPLAY/SELECT/MENU/etc).
     */
    public int commandToDocType(String strCommand)
    {
        if (CLASS_DETAIL_SCREEN.equalsIgnoreCase(strCommand))
            return CLASS_DETAIL_MODE;
        if (RESOURCE_DETAIL_SCREEN.equalsIgnoreCase(strCommand))
            return RESOURCE_DETAIL_MODE;
        return super.commandToDocType(strCommand);
    }
    /**
     * Get the path to this file name.
     * @param strFileName The filename to find (If blank, just get the path to the root package; If null, path to project source).
     * @param strPackage The relative package to this file name (If null, no package, if blank, default package path)
     * @param codeType The code type 
     * @param fullPath If true, full path; if false relative to top-level project
     * @return The path to this package.
     */
    public String getFileName(String strFileName, String strPackage, CodeType codeType, boolean fullPath, boolean sourcePath)
    {
        ProgramControl recProgramControl = (ProgramControl)this.getRecordOwner().getRecord(ProgramControl.PROGRAM_CONTROL_FILE);
        if (recProgramControl == null)
            recProgramControl = new ProgramControl(this.findRecordOwner());
        
        String packagePath = DBConstants.BLANK;
        if (strPackage != null)
        {
            strPackage = this.getFullPackage(codeType, strPackage);
            packagePath = strPackage.replace('.', '/') + "/";
        }
        
        String strFileRoot = DBConstants.BLANK;
        if (fullPath)
        {
            strFileRoot = recProgramControl.getBasePath();
            if (!strFileRoot.endsWith("/"))
                strFileRoot += "/";
        }
        String strSourcePath = null;
        if (sourcePath)
            strSourcePath = recProgramControl.getField(ProgramControl.SOURCE_DIRECTORY).toString();
        else
            strSourcePath = recProgramControl.getField(ProgramControl.CLASS_DIRECTORY).toString();
        if ((this.getEditMode() == DBConstants.EDIT_CURRENT) || (this.getEditMode() == DBConstants.EDIT_IN_PROGRESS))
        {
            String strSrcPath = this.getPath(codeType, false);
            if (strSrcPath.length() > 0)
            {
                if (!strSrcPath.endsWith("/"))
                    strSrcPath += "/";
                if (!strSrcPath.endsWith(strSourcePath))
                    strSourcePath = strSrcPath + strSourcePath;
                else
                    strSourcePath = strSrcPath;
            }
        }
        
        if (strFileName == null)
            strFileName = DBConstants.BLANK;
        if (strFileName.length() > 0)
            if (strFileName.indexOf(".") == -1)
        {
            if (codeType == CodeType.RESOURCE_PROPERTIES)
                strFileName = strFileName + ".properties";
            else
                strFileName = strFileName + ".java";
        }
        strFileName = strFileRoot + strSourcePath + packagePath + strFileName;
        return strFileName;
    }
    /**
     * GetFullPackage Method.
     */
    public String getFullPackage(CodeType codeType, String packageName)
    {
        if (packageName == null)
            packageName = DBConstants.BLANK;
        if (packageName.length() > 0)
            if (!packageName.startsWith("."))
                return packageName;
        
        Record programControl = null;
        if (this.getRecordOwner() != null)
            programControl = (Record)this.getRecordOwner().getRecord(ProgramControl.PROGRAM_CONTROL_FILE);
        Record programControlTemp = null;
        if (programControl == null)
            programControl = programControlTemp = new ProgramControl(this.findRecordOwner());
        
        String startPackage = DBConstants.ROOT_PACKAGE.substring(0, DBConstants.ROOT_PACKAGE.length() - 1);
        if (codeType == CodeType.THICK)
            if (!programControl.getField(ProgramControl.PACKAGE_NAME).isNull())
        {
            if (programControl.getField(ProgramControl.THIN_PACKAGE).toString().startsWith("."))
                startPackage = startPackage + programControl.getField(ProgramControl.PACKAGE_NAME).toString();
            else
                startPackage = programControl.getField(ProgramControl.PACKAGE_NAME).toString();
        }
        if (codeType == CodeType.THIN)
        {
            if (!programControl.getField(ProgramControl.THIN_PACKAGE).isNull())
            {
                if (programControl.getField(ProgramControl.THIN_PACKAGE).toString().startsWith("."))
                    startPackage = startPackage + programControl.getField(ProgramControl.THIN_PACKAGE).toString();
                else
                    startPackage = programControl.getField(ProgramControl.THIN_PACKAGE).toString();
            }
            else
                startPackage = startPackage + ".thin";
        }
        if ((codeType == CodeType.RESOURCE_CODE) || (codeType == CodeType.RESOURCE_PROPERTIES))
        {
            if (!programControl.getField(ProgramControl.RESOURCE_PACKAGE).isNull())
            {
                if (programControl.getField(ProgramControl.RESOURCE_PACKAGE).toString().startsWith("."))
                    startPackage = startPackage + programControl.getField(ProgramControl.RESOURCE_PACKAGE).toString();
                else
                    startPackage = programControl.getField(ProgramControl.RESOURCE_PACKAGE).toString();
            }
            else
                startPackage = startPackage + ".res";
        }
        if (codeType == CodeType.INTERFACE)
        {
            if (!programControl.getField(ProgramControl.INTERFACE_PACKAGE).isNull())
            {
                if (programControl.getField(ProgramControl.INTERFACE_PACKAGE).toString().startsWith("."))
                    startPackage = startPackage + programControl.getField(ProgramControl.INTERFACE_PACKAGE).toString();
                else
                    startPackage = programControl.getField(ProgramControl.INTERFACE_PACKAGE).toString();
            }
            else
                startPackage = startPackage + ".model";
        }
        
        String fullPackage = this.getPath(codeType, true);
        if (fullPackage == null)
            fullPackage = DBConstants.BLANK;
        if ((fullPackage.length() == 0) || (fullPackage.startsWith(".")))
            fullPackage = startPackage + fullPackage;
        if (fullPackage.endsWith("."))
            fullPackage = fullPackage.substring(0, fullPackage.length() - 1);
        if (packageName.startsWith("."))
            packageName = packageName.substring(1);
        
        if (programControlTemp != null)
            programControlTemp.free();
        
        if (fullPackage.length() == 0)
            return packageName;
        else if (packageName.length() == 0)
            return fullPackage;
        return fullPackage + '.' + packageName;
    }
    /**
     * GetPath Method.
     */
    public String getPath(CodeType codeType, boolean bPackagePath)
    {
        String strSrcPath = DBConstants.BLANK;
        if (!bPackagePath)
            strSrcPath = this.getField(ClassProject.NAME).toString();
        String pathChar = bPackagePath ? "." : "/";
        switch (codeType)
        {
        case THICK:
            if (!bPackagePath) if (!this.getField(ClassProject.PROJECT_PATH).isNull())
                strSrcPath = this.getField(ClassProject.PROJECT_PATH).toString();
            if (bPackagePath) if (!this.getField(ClassProject.PACKAGE_NAME).isNull())
                strSrcPath = this.getField(ClassProject.PACKAGE_NAME).toString();
            break;
        case THIN:
            if (!bPackagePath) if (!this.getField(ClassProject.THIN_PROJECT_PATH).isNull())
                strSrcPath = this.getField(ClassProject.THIN_PROJECT_PATH).toString();
            if (bPackagePath) if (!this.getField(ClassProject.THIN_PACKAGE).isNull())
                strSrcPath = this.getField(ClassProject.THIN_PACKAGE).toString();
            break;
        case RESOURCE_PROPERTIES:
        case RESOURCE_CODE:
            if (!bPackagePath) if (!this.getField(ClassProject.RES_PROJECT_PATH).isNull())
                strSrcPath = this.getField(ClassProject.RES_PROJECT_PATH).toString();
            if (bPackagePath) if (!this.getField(ClassProject.RESOURCE_PACKAGE).isNull())
                strSrcPath = this.getField(ClassProject.RESOURCE_PACKAGE).toString();
            break;
        case INTERFACE:
            if (!bPackagePath) if (!this.getField(ClassProject.INTERFACE_PROJECT_PATH).isNull())
                strSrcPath = this.getField(ClassProject.INTERFACE_PROJECT_PATH).toString();
            if (bPackagePath) if (!this.getField(ClassProject.INTERFACE_PACKAGE).isNull())
                strSrcPath = this.getField(ClassProject.INTERFACE_PACKAGE).toString();
            break;
        }
        
        if (strSrcPath == null)
            strSrcPath = DBConstants.BLANK;
        
        if (this.getField(ClassProject.PARENT_FOLDER_ID).getValue() != 0)
        { // Continue up the chain
            ClassProject classProject = (ClassProject)((ReferenceField)this.getField(ClassProject.PARENT_FOLDER_ID)).getReference();
            if (classProject != null)
                if ((classProject.getEditMode() == DBConstants.EDIT_CURRENT) || (classProject.getEditMode() == DBConstants.EDIT_IN_PROGRESS))
            {
                String basePath = classProject.getPath(codeType, bPackagePath);
                if (basePath != null) if (basePath.length() > 0)
                {
                    if (".".equals(pathChar))
                        if ((strSrcPath.length() > 0) && (!strSrcPath.startsWith(".")))
                            basePath = DBConstants.BLANK; // The src path is not relative, start at the root
                    if ((basePath.endsWith(pathChar)) || (basePath.length() == 0))
                        pathChar = DBConstants.BLANK;
                    if (pathChar.length() > 0)
                    {
                        if (strSrcPath.startsWith(pathChar))
                            strSrcPath = strSrcPath.substring(1);
                        if (basePath.endsWith(pathChar))
                            basePath = basePath.substring(0, basePath.length() - 1);
                    }
                    if ((strSrcPath.length() == 0) || (basePath.length() == 0))
                        pathChar = DBConstants.BLANK;
                    strSrcPath = basePath + pathChar + strSrcPath;
                }
            }
        }
        
        if (!bPackagePath)
        {
            strSrcPath = Utility.replaceResources(strSrcPath, null, null, this.getRecordOwner());
            try {
                strSrcPath = Utility.replaceResources(strSrcPath, null, Utility.propertiesToMap(System.getProperties()), null);
            } catch (SecurityException e) {
                // Ignore
            }
            strSrcPath = Utility.normalizePath(strSrcPath);
        }
        
        return strSrcPath;
    }

}
