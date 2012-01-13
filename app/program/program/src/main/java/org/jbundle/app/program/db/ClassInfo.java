/**
 * @(#)ClassInfo.
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
import org.jbundle.app.program.screen.*;
import org.jbundle.base.services.*;
import java.io.*;
import org.jbundle.base.db.xmlutil.*;
import org.jbundle.model.app.program.db.*;

/**
 *  ClassInfo - Class information.
 */
public class ClassInfo extends VirtualRecord
     implements ClassInfoModel, ClassInfoService
{
    private static final long serialVersionUID = 1L;

    //public static final int kID = kID;
    public static final int kClassName = kVirtualRecordLastField + 1;
    public static final int kBaseClassName = kClassName + 1;
    public static final int kClassDesc = kBaseClassName + 1;
    public static final int kClassProjectID = kClassDesc + 1;
    public static final int kClassPackage = kClassProjectID + 1;
    public static final int kClassSourceFile = kClassPackage + 1;
    public static final int kClassType = kClassSourceFile + 1;
    public static final int kClassExplain = kClassType + 1;
    public static final int kClassHelp = kClassExplain + 1;
    public static final int kClassImplements = kClassHelp + 1;
    public static final int kSeeAlso = kClassImplements + 1;
    public static final int kTechnicalInfo = kSeeAlso + 1;
    public static final int kCopyDescFrom = kTechnicalInfo + 1;
    public static final int kClassInfoLastField = kCopyDescFrom;
    public static final int kClassInfoFields = kCopyDescFrom - DBConstants.MAIN_FIELD + 1;

    public static final int kIDKey = DBConstants.MAIN_KEY_FIELD;
    public static final int kClassNameKey = kIDKey + 1;
    public static final int kClassSourceFileKey = kClassNameKey + 1;
    public static final int kBaseClassNameKey = kClassSourceFileKey + 1;
    public static final int kClassProjectIDKey = kBaseClassNameKey + 1;
    public static final int kClassInfoLastKey = kClassProjectIDKey;
    public static final int kClassInfoKeys = kClassProjectIDKey - DBConstants.MAIN_KEY_FIELD + 1;
    public static final String RESOURCE_CLASS = "ListResourceBundle";
    /**
     * Default constructor.
     */
    public ClassInfo()
    {
        super();
    }
    /**
     * Constructor.
     */
    public ClassInfo(RecordOwner screen)
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

    public static final String kClassInfoFile = "ClassInfo";
    /**
     * Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? Record.formatTableNames(kClassInfoFile, bAddQuotes) : super.getTableNames(bAddQuotes);
    }
    /**
     * Get the name of a single record.
     */
    public String getRecordName()
    {
        return "Class";
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
        return DBConstants.REMOTE | DBConstants.SHARED_DATA | DBConstants.HIERARCHICAL | DBConstants.LOCALIZABLE;
    }
    /**
     * Make a default screen.
     */
    public BaseScreen makeScreen(ScreenLocation itsLocation, BasePanel parentScreen, int iDocMode, Map<String,Object> properties)
    {
        BaseScreen screen = null;
        if ((iDocMode & ScreenConstants.MAINT_MODE) != 0)
            screen = new ClassInfoScreen(this, itsLocation, parentScreen, null, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties);
        else if ((iDocMode & ScreenConstants.DISPLAY_MODE) != 0)
            screen = new ClassInfoGridScreen(this, itsLocation, parentScreen, null, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties);
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
        if (iFieldSeq == kClassName)
        {
            field = new StringField(this, "ClassName", 40, null, null);
            field.setNullable(false);
        }
        if (iFieldSeq == kBaseClassName)
            field = new StringField(this, "BaseClassName", 40, null, null);
        if (iFieldSeq == kClassDesc)
            field = new StringField(this, "ClassDesc", 255, null, null);
        if (iFieldSeq == kClassProjectID)
        {
            field = new ClassProjectField(this, "ClassProjectID", Constants.DEFAULT_FIELD_LENGTH, null, null);
            field.addListener(new InitOnceFieldHandler(null));
        }
        if (iFieldSeq == kClassPackage)
        {
            field = new StringField(this, "ClassPackage", 60, null, null);
            field.addListener(new InitOnceFieldHandler(null));
        }
        if (iFieldSeq == kClassSourceFile)
            field = new StringField(this, "ClassSourceFile", 40, null, null);
        if (iFieldSeq == kClassType)
        {
            field = new StringField(this, "ClassType", 20, null, null);
            field.addListener(new InitOnceFieldHandler(null));
        }
        if (iFieldSeq == kClassExplain)
            field = new XmlField(this, "ClassExplain", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kClassHelp)
            field = new XmlField(this, "ClassHelp", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kClassImplements)
            field = new StringField(this, "ClassImplements", 60, null, null);
        if (iFieldSeq == kSeeAlso)
            field = new MemoField(this, "SeeAlso", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kTechnicalInfo)
            field = new MemoField(this, "TechnicalInfo", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kCopyDescFrom)
            field = new StringField(this, "CopyDescFrom", 50, null, null);
        if (field == null)
        {
            field = super.setupField(iFieldSeq);
            if (field == null) if (iFieldSeq < kClassInfoLastField)
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
        if (iKeyArea == kClassNameKey)
        {
            keyArea = this.makeIndex(DBConstants.UNIQUE, "ClassName");
            keyArea.addKeyField(kClassName, DBConstants.ASCENDING);
        }
        if (iKeyArea == kClassSourceFileKey)
        {
            keyArea = this.makeIndex(DBConstants.UNIQUE, "ClassSourceFile");
            keyArea.addKeyField(kClassSourceFile, DBConstants.ASCENDING);
            keyArea.addKeyField(kClassName, DBConstants.ASCENDING);
        }
        if (iKeyArea == kBaseClassNameKey)
        {
            keyArea = this.makeIndex(DBConstants.NOT_UNIQUE, "BaseClassName");
            keyArea.addKeyField(kBaseClassName, DBConstants.ASCENDING);
        }
        if (iKeyArea == kClassProjectIDKey)
        {
            keyArea = this.makeIndex(DBConstants.NOT_UNIQUE, "ClassProjectID");
            keyArea.addKeyField(kClassProjectID, DBConstants.ASCENDING);
            keyArea.addKeyField(kClassName, DBConstants.ASCENDING);
        }
        if (keyArea == null) if (iKeyArea < kClassInfoLastKey)
        {
            keyArea = super.setupKey(iKeyArea);     
            if (keyArea == null) if (iKeyArea < kClassInfoLastKey)
                keyArea = new EmptyKey(this);
        }
        return keyArea;
    }
    /**
     * Get the link that will run this class.
     */
    public String getLink()
    {
        String strType = this.getField(ClassInfo.kClassType).getString();
        String strLink = this.getField(ClassInfo.kClassName).getString();
        if (this.getField(ClassInfo.kClassPackage).getLength() > 0)
            strLink = DBConstants.ROOT_PACKAGE + this.getField(ClassInfo.kClassPackage).toString() + "." + strLink;
        if (strType.equalsIgnoreCase("screen"))
            strLink = HtmlConstants.SERVLET_LINK + "?screen=" + strLink;
        else if (strType.equalsIgnoreCase("record"))
            strLink = HtmlConstants.SERVLET_LINK + "?record=" + strLink;
        return strLink;
    }
    /**
     * Read the ClassInfoService record
     * @param recordOwner The record owner to use to create the this record AND to optionally get the classinfo.
     * @param className if non-null read this class name, if null, use the recordowner properties to figure out the class.
     * @param getRecord If true, read the record.
    .
     */
    public ClassInfoService readClassInfo(RecordOwner recordOwner, String className)
    {
        String strParamRecord = null;
        String strParamScreenType = null;
        String strParamMenu = null;
        String strParamHelp = null;
        
        if (className == null)
        {
            strParamRecord = recordOwner.getProperty(DBParams.RECORD);      // Display record
            className = recordOwner.getProperty(DBParams.SCREEN);      // Display screen
            strParamScreenType = recordOwner.getProperty(DBParams.COMMAND);     // Display record
            strParamMenu = recordOwner.getProperty(DBParams.MENU);      // Display record
            strParamHelp = recordOwner.getProperty(DBParams.HELP);      // Display record
        }
        
        if (className == null)
            className = DBConstants.BLANK;
        if ((className.length() == 0) || (className.equals("Screen")) || (className.equals("GridScreen")))
        {
            if (strParamRecord != null) if (strParamRecord.length() > 0)
                className = strParamRecord;  // Use desc of record class if standard screen
        }
        
        try {
            this.setKeyArea(DBConstants.PRIMARY_KEY);
            if (className.lastIndexOf('.') != -1)
                className = className.substring(className.lastIndexOf('.') + 1);
            this.getField(ClassInfo.kClassName).setString(className);
            boolean bSuccess = false;
            this.setKeyArea(ClassInfo.kClassNameKey);
            if (className.length() > 0)
                bSuccess = this.seek("=");
            if (!bSuccess)
            {   // Not found, use standard screen maintenance screen
                if ((strParamMenu != null) && (strParamMenu.length() > 0))
                    this.getField(ClassInfo.kClassName).setString("MenuScreen");
                else if ((strParamRecord != null) && (strParamRecord.length() > 0))
                {
                    if ((strParamScreenType != null) && (strParamScreenType.length() > 0) && (strParamScreenType.equalsIgnoreCase(ThinMenuConstants.FORM)))
                        this.getField(ClassInfo.kClassName).setString("Screen");
                    else
                        this.getField(ClassInfo.kClassName).setString("GridScreen");
                }
                else
                    this.getField(ClassInfo.kClassName).setString(strParamHelp);
                if (!bSuccess)
                        bSuccess = this.seek("=");
            }
        } catch (DBException e) {
            e.printStackTrace();
            return null;
        }
        return this;
    }
    /**
     * Get the full class name.
     */
    public String getPackageName()
    {
        String packageName = this.getField(ClassInfo.kClassPackage).toString();
        ClassProject classProject = (ClassProject)((ReferenceField)this.getField(ClassInfo.kClassProjectID)).getReference();
        if (classProject != null)
            if ((classProject.getEditMode() == DBConstants.EDIT_IN_PROGRESS) || (classProject.getEditMode() == DBConstants.EDIT_CURRENT))
                packageName = classProject.getFullPackage(ClassProject.CodeType.THICK, packageName);
        return packageName;
    }
    /**
     * GetClassName Method.
     */
    public String getClassName()
    {
        return this.getField(ClassInfo.kClassName).toString();
    }
    /**
     * GetFullClassName Method.
     */
    public String getFullClassName()
    {
        String className = this.getField(ClassInfo.kClassName).toString();
        ClassProject classProject = (ClassProject)((ReferenceField)this.getField(ClassInfo.kClassProjectID)).getReference();
        String packageName = classProject.getFullPackage(ClassProject.CodeType.THICK, this.getField(ClassInfo.kClassPackage).toString());
        if (!packageName.endsWith("."))
            if (!className.endsWith("."))
                className = "." + className;
        return packageName + className;
    }
    /**
     * GetClassDesc Method.
     */
    public String getClassDesc()
    {
        return this.getField(ClassInfo.kClassDesc).toString();
    }
    /**
     * GetClassExplain Method.
     */
    public String getClassExplain()
    {
        return this.getField(ClassInfo.kClassExplain).toString();
    }
    /**
     * GetClassHelp Method.
     */
    public String getClassHelp()
    {
        return this.getField(ClassInfo.kClassHelp).toString();
    }
    /**
     * GetClassType Method.
     */
    public String getClassType()
    {
        return this.getField(ClassInfo.kClassType).toString();
    }
    /**
     * GetSeeAlso Method.
     */
    public String getSeeAlso()
    {
        return this.getField(ClassInfo.kSeeAlso).toString();
    }
    /**
     * GetTechnicalInfo Method.
     */
    public String getTechnicalInfo()
    {
        return this.getField(ClassInfo.kTechnicalInfo).toString();
    }
    /**
     * IsValidRecord Method.
     */
    public boolean isValidRecord()
    {
        return ((this.getEditMode() == DBConstants.EDIT_IN_PROGRESS) || (this.getEditMode() == DBConstants.EDIT_CURRENT));
    }
    /**
     * PrintHtmlTechInfo Method.
     */
    public void printHtmlTechInfo(PrintWriter out, String strTag, String strParams, String strData)
    {
        FieldData fieldInfo = new FieldData(Utility.getRecordOwner(this));
        
        String strClass = this.getClassName();
        fieldInfo.setKeyArea(FieldData.kFieldFileNameKey);
        fieldInfo.addListener(new StringSubFileFilter(strClass, FieldData.kFieldFileName, null, -1, null, -1));
        try   {
            out.println("<table border=1>");
            out.println("<tr>");
            out.println("<th>" + fieldInfo.getField(FieldData.kFieldName).getFieldDesc() + "</th>");
            out.println("<th>" + fieldInfo.getField(FieldData.kFieldClass).getFieldDesc() + "</th>");
            out.println("<th>" + fieldInfo.getField(FieldData.kBaseFieldName).getFieldDesc() + "</th>");
            out.println("<th>" + fieldInfo.getField(FieldData.kMaximumLength).getFieldDesc() + "</th>");
            out.println("<th>" + fieldInfo.getField(FieldData.kFieldDescription).getFieldDesc() + "</th>");
            out.println("</tr>");
            while (fieldInfo.hasNext())
            {
                fieldInfo.next();
                out.println("<tr>");
                out.println("<td>&nbsp;" + fieldInfo.getField(FieldData.kFieldName).toString() + "</td>");
                out.println("<td>&nbsp;" + fieldInfo.getField(FieldData.kFieldClass).toString() + "</td>");
                out.println("<td>&nbsp;" + fieldInfo.getField(FieldData.kBaseFieldName).toString() + "</td>");
                out.println("<td>&nbsp;" + fieldInfo.getField(FieldData.kMaximumLength).toString() + "</td>");
                out.println("<td>&nbsp;" + fieldInfo.getField(FieldData.kFieldDescription).toString() + "</td>");
                out.println("</tr>");
            }
            out.println("</table>");
        } catch (DBException ex)    {
            ex.printStackTrace();
        }
        
        fieldInfo.free();
        fieldInfo = null;
    }
    /**
     * PrintScreen Method.
     */
    public void printScreen(PrintWriter out, ResourceBundle reg)
    {
        String strClassXML = XmlUtilities.createXMLStringRecord(this);
        
        if (strClassXML == null)
            strClassXML = DBConstants.BLANK;
        
        String strOptXML = DBConstants.BLANK;
        FileHdr fileHdr = new FileHdr(null);
        fileHdr.setKeyArea(FileHdr.kFileNameKey);
        fileHdr.getField(FileHdr.kFileName).moveFieldToThis(this.getField(ClassInfo.kClassName));
        try   {
            if (fileHdr.seek("="))
            {
                strOptXML = XmlUtilities.createXMLStringRecord(fileHdr);
                FieldData fieldInfo = new FieldData(null);
                fieldInfo.setKeyArea(FieldData.kFieldFileNameKey);
                fieldInfo.addListener(new SubFileFilter(this.getField(ClassInfo.kClassName), FieldData.kFieldFileName, null, -1, null, -1));
                strOptXML += Utility.startTag(XMLTags.FIELD_LIST);
                while (fieldInfo.hasNext())
                {
                    fieldInfo.next();
                    strOptXML += XmlUtilities.createXMLStringRecord(fieldInfo);
                }
                strOptXML += Utility.endTag(XMLTags.FIELD_LIST);
                fieldInfo.free();
        
                KeyInfo keyInfo = new KeyInfo(null);
                keyInfo.setKeyArea(KeyInfo.kKeyFilenameKey);
                keyInfo.addListener(new SubFileFilter(this.getField(ClassInfo.kClassName), KeyInfo.kKeyFilename, null, -1, null, -1));
                strOptXML += Utility.startTag(XMLTags.KEY_LIST);
                while (keyInfo.hasNext())
                {
                    keyInfo.next();
                    strOptXML += XmlUtilities.createXMLStringRecord(keyInfo);
                }
                strOptXML += Utility.endTag(XMLTags.KEY_LIST);
                keyInfo.free();
            }
        } catch (DBException ex)    {
            ex.printStackTrace();
            strOptXML = DBConstants.BLANK;
        }
        fileHdr.free();
        
        String strContentArea =
                    strClassXML +
                    strOptXML;
        out.println(strContentArea);
    }
    /**
     * IsARecord Method.
     */
    public boolean isARecord()
    {
        Record recFileHdr = this.getRecordOwner().getRecord(FileHdr.kFileHdrFile);
        if (recFileHdr == null)
        {
            recFileHdr = new FileHdr(this.getRecordOwner());
            this.addListener(new FreeOnFreeHandler(recFileHdr));
        }
        if (!recFileHdr.getField(FileHdr.kFileName).equals(this.getField(ClassInfo.kClassName)))
        {
            try {
                recFileHdr.addNew();
                recFileHdr.getField(FileHdr.kFileName).moveFieldToThis(this.getField(ClassInfo.kClassName));
                int oldKeyArea = recFileHdr.getDefaultOrder();
                recFileHdr.setKeyArea(FileHdr.kFileNameKey);
                if (!recFileHdr.seek(null))
                    return false;
                recFileHdr.setKeyArea(oldKeyArea);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        FieldData recFieldData = (FieldData)this.getRecord(FieldData.kFieldDataFile);
        if (((recFileHdr.getEditMode() != DBConstants.EDIT_CURRENT) || (!this.getField(ClassInfo.kClassName).toString().equals(recFileHdr.getField(FileHdr.kFileName).getString())))
            && (!"Record".equalsIgnoreCase(this.getField(ClassInfo.kClassType).toString())))
                return false;     // If this isn't a physical file, don't build it.
        if (this.getField(ClassInfo.kBaseClassName).toString().contains("ScreenRecord"))
                return false;
        if ("Interface".equalsIgnoreCase(this.getField(ClassInfo.kClassType).toString()))   // An interface doesn't have an interface
            return false;     // If this isn't a physical file, don't build it.
        if (RESOURCE_CLASS.equals(this.getField(ClassInfo.kBaseClassName).toString()))
            return false;     // Resource only class
        return true;
    }

}
