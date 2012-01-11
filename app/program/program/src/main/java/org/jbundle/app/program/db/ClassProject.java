/**
 * @(#)ClassProject.
 * Copyright © 2011 jbundle.org. All rights reserved.
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
import org.jbundle.base.screen.model.*;
import org.jbundle.base.screen.model.util.*;
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.main.db.*;
import org.jbundle.app.program.screen.*;
import org.jbundle.app.program.resource.screen.*;

/**
 *  ClassProject - .
 */
public class ClassProject extends Folder
{
    private static final long serialVersionUID = 1L;

    //public static final int kID = kID;
    //public static final int kParentFolderID = kParentFolderID;
    //public static final int kName = kName;
    public static final int kDescription = kFolderLastField + 1;
    public static final int kSystemClasses = kDescription + 1;
    public static final int kPackageName = kSystemClasses + 1;
    public static final int kProjectPath = kPackageName + 1;
    public static final int kInterfacePackage = kProjectPath + 1;
    public static final int kInterfaceProjectPath = kInterfacePackage + 1;
    public static final int kThinPackage = kInterfaceProjectPath + 1;
    public static final int kThinProjectPath = kThinPackage + 1;
    public static final int kResourcePackage = kThinProjectPath + 1;
    public static final int kResProjectPath = kResourcePackage + 1;
    public static final int kProperties = kResProjectPath + 1;
    public static final int kArtifactId = kProperties + 1;
    public static final int kGroupId = kArtifactId + 1;
    public static final int kClassProjectLastField = kGroupId;
    public static final int kClassProjectFields = kGroupId - DBConstants.MAIN_FIELD + 1;

    public static final int kIDKey = DBConstants.MAIN_KEY_FIELD;
    public static final int kParentFolderIDKey = kIDKey + 1;
    public static final int kNameKey = kParentFolderIDKey + 1;
    public static final int kClassProjectLastKey = kNameKey;
    public static final int kClassProjectKeys = kNameKey - DBConstants.MAIN_KEY_FIELD + 1;
    public static enum CodeType {BASE, THIN, RESOURCE_PROPERTIES, RESOURCE_CODE, INTERFACE};
    public static final String CLASS_DETAIL_SCREEN = "ClassDetail";
    public static final int CLASS_DETAIL_MODE = ScreenConstants.LAST_MODE * 4;
    public static final String RESOURCE_DETAIL_SCREEN = "ResourceDetail";
    public static final int RESOURCE_DETAIL_MODE = ScreenConstants.LAST_MODE * 8;
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

    public static final String kClassProjectFile = "ClassProject";
    /**
     * Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? Record.formatTableNames(kClassProjectFile, bAddQuotes) : super.getTableNames(bAddQuotes);
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
    public BaseScreen makeScreen(ScreenLocation itsLocation, BasePanel parentScreen, int iDocMode, Map<String,Object> properties)
    {
        BaseScreen screen = null;
        if ((iDocMode & ClassProject.CLASS_DETAIL_MODE) == ClassProject.CLASS_DETAIL_MODE)
            screen = new ClassInfoGridScreen(this, null, itsLocation, parentScreen, null, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties);
        else if ((iDocMode & ClassProject.RESOURCE_DETAIL_MODE) == ClassProject.RESOURCE_DETAIL_MODE)
            screen = new ResourceGridScreen(this, null, itsLocation, parentScreen, null, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties);
        else if ((iDocMode & ScreenConstants.DETAIL_MODE) == ScreenConstants.DETAIL_MODE)
            screen = new ClassProjectGridScreen(this, null, itsLocation, parentScreen, null, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties);
        else if ((iDocMode & ScreenConstants.MAINT_MODE) == ScreenConstants.MAINT_MODE)
            screen = new ClassProjectScreen(this, itsLocation, parentScreen, null, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties);
        else if ((iDocMode & ScreenConstants.DISPLAY_MODE) != 0)
            screen = new ClassProjectGridScreen(this, itsLocation, parentScreen, null, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties);
        else if ((iDocMode & ScreenConstants.MENU_MODE) != 0)
            screen = new ClassProjectScreen(this, itsLocation, parentScreen, null, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties);
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
        //if (iFieldSeq == kID)
        //{
        //  field = new CounterField(this, "ID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        //  field.setHidden(true);
        //}
        if (iFieldSeq == kParentFolderID)
            field = new ClassProjectField(this, "ParentFolderID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kName)
        {
            field = new StringField(this, "Name", 40, null, null);
            field.setNullable(false);
        }
        if (iFieldSeq == kDescription)
            field = new StringField(this, "Description", 40, null, null);
        if (iFieldSeq == kSystemClasses)
            field = new BooleanField(this, "SystemClasses", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kPackageName)
            field = new StringField(this, "PackageName", 30, null, null);
        if (iFieldSeq == kProjectPath)
            field = new StringField(this, "ProjectPath", 128, null, null);
        if (iFieldSeq == kInterfacePackage)
            field = new StringField(this, "InterfacePackage", 40, null, null);
        if (iFieldSeq == kInterfaceProjectPath)
            field = new StringField(this, "InterfaceProjectPath", 128, null, null);
        if (iFieldSeq == kThinPackage)
            field = new StringField(this, "ThinPackage", 40, null, null);
        if (iFieldSeq == kThinProjectPath)
            field = new StringField(this, "ThinProjectPath", 128, null, null);
        if (iFieldSeq == kResourcePackage)
            field = new StringField(this, "ResourcePackage", 40, null, null);
        if (iFieldSeq == kResProjectPath)
            field = new StringField(this, "ResProjectPath", 128, null, null);
        if (iFieldSeq == kProperties)
            field = new PropertiesField(this, "Properties", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kArtifactId)
            field = new StringField(this, "ArtifactId", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kGroupId)
            field = new StringField(this, "GroupId", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (field == null)
        {
            field = super.setupField(iFieldSeq);
            if (field == null) if (iFieldSeq < kClassProjectLastField)
                field = new EmptyField(this);
        }
        return field;
    }
    /**
     * Add this key area description to the Record.
     */
    public KeyArea setupKey(int iKeyArea)
    {
        KeyArea keyArea = null;
        if (iKeyArea == kIDKey)
        {
            keyArea = this.makeIndex(DBConstants.UNIQUE, "PrimaryKey");
            keyArea.addKeyField(kID, DBConstants.ASCENDING);
        }
        if (iKeyArea == kParentFolderIDKey)
        {
            keyArea = this.makeIndex(DBConstants.NOT_UNIQUE, "ParentFolderID");
            keyArea.addKeyField(kParentFolderID, DBConstants.ASCENDING);
        }
        if (iKeyArea == kNameKey)
        {
            keyArea = this.makeIndex(DBConstants.NOT_UNIQUE, "Name");
            keyArea.addKeyField(kName, DBConstants.ASCENDING);
        }
        if (keyArea == null) if (iKeyArea < kClassProjectLastKey)
        {
            keyArea = super.setupKey(iKeyArea);     
            if (keyArea == null) if (iKeyArea < kClassProjectLastKey)
                keyArea = new EmptyKey(this);
        }
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
        Record recProgramControl = this.getRecordOwner().getRecord(ProgramControl.kProgramControlFile);
        if (recProgramControl == null)
            recProgramControl = new ProgramControl(Utility.getRecordOwner(this));
        
        String packagePath = DBConstants.BLANK;
        if (strPackage != null)
        {
            strPackage = this.getFullPackage(codeType, strPackage);
            packagePath = strPackage.replace('.', '/') + "/";
        }
        
        String strFileRoot = DBConstants.BLANK;
        if (fullPath)
        {
            strFileRoot = recProgramControl.getField(ProgramControl.kBaseDirectory).toString();
            if (!strFileRoot.endsWith("/"))
                strFileRoot += "/";
        }
        String strSourcePath = null;
        if (sourcePath)
            strSourcePath = recProgramControl.getField(ProgramControl.kSourceDirectory).toString();
        else
            strSourcePath = recProgramControl.getField(ProgramControl.kClassDirectory).toString();
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
            programControl = this.getRecordOwner().getRecord(ProgramControl.kProgramControlFile);
        Record programControlTemp = null;
        if (programControl == null)
            programControl = programControlTemp = new ProgramControl(Utility.getRecordOwner(this));
        
        String startPackage = DBConstants.ROOT_PACKAGE.substring(0, DBConstants.ROOT_PACKAGE.length() - 1);
        if (codeType == CodeType.BASE)
            if (!programControl.getField(ProgramControl.kPackageName).isNull())
        {
            if (programControl.getField(ProgramControl.kThinPackage).toString().startsWith("."))
                startPackage = startPackage + programControl.getField(ProgramControl.kPackageName).toString();
            else
                startPackage = programControl.getField(ProgramControl.kPackageName).toString();
        }
        if (codeType == CodeType.THIN)
        {
            if (!programControl.getField(ProgramControl.kThinPackage).isNull())
            {
                if (programControl.getField(ProgramControl.kThinPackage).toString().startsWith("."))
                    startPackage = startPackage + programControl.getField(ProgramControl.kThinPackage).toString();
                else
                    startPackage = programControl.getField(ProgramControl.kThinPackage).toString();
            }
            else
                startPackage = startPackage + ".thin";
        }
        if ((codeType == CodeType.RESOURCE_CODE) || (codeType == CodeType.RESOURCE_PROPERTIES))
        {
            if (!programControl.getField(ProgramControl.kResourcePackage).isNull())
            {
                if (programControl.getField(ProgramControl.kResourcePackage).toString().startsWith("."))
                    startPackage = startPackage + programControl.getField(ProgramControl.kResourcePackage).toString();
                else
                    startPackage = programControl.getField(ProgramControl.kResourcePackage).toString();
            }
            else
                startPackage = startPackage + ".res";
        }
        if (codeType == CodeType.INTERFACE)
        {
            if (!programControl.getField(ProgramControl.kInterfacePackage).isNull())
            {
                if (programControl.getField(ProgramControl.kInterfacePackage).toString().startsWith("."))
                    startPackage = startPackage + programControl.getField(ProgramControl.kInterfacePackage).toString();
                else
                    startPackage = programControl.getField(ProgramControl.kInterfacePackage).toString();
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
            strSrcPath = this.getField(ClassProject.kName).toString();
        String pathChar = bPackagePath ? "." : "/";
        switch (codeType)
        {
        case BASE:
            if (!bPackagePath) if (!this.getField(ClassProject.kProjectPath).isNull())
                strSrcPath = this.getField(ClassProject.kProjectPath).toString();
            if (bPackagePath) if (!this.getField(ClassProject.kPackageName).isNull())
                strSrcPath = this.getField(ClassProject.kPackageName).toString();
            break;
        case THIN:
            if (!bPackagePath) if (!this.getField(ClassProject.kThinProjectPath).isNull())
                strSrcPath = this.getField(ClassProject.kThinProjectPath).toString();
            if (bPackagePath) if (!this.getField(ClassProject.kThinPackage).isNull())
                strSrcPath = this.getField(ClassProject.kThinPackage).toString();
            break;
        case RESOURCE_PROPERTIES:
        case RESOURCE_CODE:
            if (!bPackagePath) if (!this.getField(ClassProject.kResProjectPath).isNull())
                strSrcPath = this.getField(ClassProject.kResProjectPath).toString();
            if (bPackagePath) if (!this.getField(ClassProject.kResourcePackage).isNull())
                strSrcPath = this.getField(ClassProject.kResourcePackage).toString();
            break;
        case INTERFACE:
            if (!bPackagePath) if (!this.getField(ClassProject.kInterfaceProjectPath).isNull())
                strSrcPath = this.getField(ClassProject.kInterfaceProjectPath).toString();
            if (bPackagePath) if (!this.getField(ClassProject.kInterfacePackage).isNull())
                strSrcPath = this.getField(ClassProject.kInterfacePackage).toString();
            break;
        }
        
        if (strSrcPath == null)
            strSrcPath = DBConstants.BLANK;
        
        if (this.getField(ClassProject.kParentFolderID).getValue() != 0)
        { // Continue up the chain
            ClassProject classProject = (ClassProject)((ReferenceField)this.getField(ClassProject.kParentFolderID)).getReference();
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
        return strSrcPath;
    }

}
