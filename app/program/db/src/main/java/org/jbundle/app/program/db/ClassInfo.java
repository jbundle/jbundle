/**
 * @(#)ClassInfo.
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
import java.io.*;
import org.jbundle.base.db.xmlutil.*;
import java.util.*;
import org.jbundle.model.app.program.db.*;

/**
 *  ClassInfo - Class information.
 */
public class ClassInfo extends VirtualRecord
     implements ClassInfoModel
{
    private static final long serialVersionUID = 1L;

    public static final String RESOURCE_CLASS = "ListResourceBundle";
    public static final String MAINT_SCREEN_CLASS = "org.jbundle.app.program.screen.ClassInfoScreen";
    public static final String GRID_SCREEN_CLASS = "org.jbundle.app.program.screen.ClassInfoGridScreen";
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
    /**
     * Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? Record.formatTableNames(CLASS_INFO_FILE, bAddQuotes) : super.getTableNames(bAddQuotes);
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
     * MakeScreen Method.
     */
    public ScreenParent makeScreen(ScreenLoc itsLocation, ComponentParent parentScreen, int iDocMode, Map<String,Object> properties)
    {
        ScreenParent screen = null;
        if ((iDocMode & ScreenConstants.MAINT_MODE) != 0)
            screen = Record.makeNewScreen(MAINT_SCREEN_CLASS, itsLocation, parentScreen, iDocMode | ScreenConstants.DETAIL_MODE | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties, this, true);
        else if ((iDocMode & ScreenConstants.DISPLAY_MODE) != 0)
            screen = Record.makeNewScreen(GRID_SCREEN_CLASS, itsLocation, parentScreen, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties, this, true);
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
            field = new StringField(this, CLASS_NAME, 40, null, null);
            field.setNullable(false);
        }
        if (iFieldSeq == 4)
            field = new StringField(this, BASE_CLASS_NAME, 40, null, null);
        if (iFieldSeq == 5)
            field = new StringField(this, CLASS_DESC, 255, null, null);
        if (iFieldSeq == 6)
        {
            field = new ClassProjectField(this, CLASS_PROJECT_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
            field.addListener(new InitOnceFieldHandler(null));
        }
        if (iFieldSeq == 7)
        {
            field = new StringField(this, CLASS_PACKAGE, 60, null, null);
            field.addListener(new InitOnceFieldHandler(null));
        }
        if (iFieldSeq == 8)
            field = new StringField(this, CLASS_SOURCE_FILE, 40, null, null);
        if (iFieldSeq == 9)
        {
            field = new ClassTypeField(this, CLASS_TYPE, 20, null, null);
            field.addListener(new InitOnceFieldHandler(null));
        }
        if (iFieldSeq == 10)
            field = new XmlField(this, CLASS_EXPLAIN, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 11)
            field = new XmlField(this, CLASS_HELP, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 12)
            field = new StringField(this, CLASS_IMPLEMENTS, 60, null, null);
        if (iFieldSeq == 13)
            field = new MemoField(this, SEE_ALSO, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 14)
            field = new MemoField(this, TECHNICAL_INFO, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 15)
            field = new StringField(this, COPY_DESC_FROM, 50, null, null);
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
            keyArea = this.makeIndex(DBConstants.UNIQUE, ID_KEY);
            keyArea.addKeyField(ID, DBConstants.ASCENDING);
        }
        if (iKeyArea == 1)
        {
            keyArea = this.makeIndex(DBConstants.NOT_UNIQUE, CLASS_NAME_KEY);
            keyArea.addKeyField(CLASS_NAME, DBConstants.ASCENDING);
        }
        if (iKeyArea == 2)
        {
            keyArea = this.makeIndex(DBConstants.NOT_UNIQUE, CLASS_SOURCE_FILE_KEY);
            keyArea.addKeyField(CLASS_SOURCE_FILE, DBConstants.ASCENDING);
            keyArea.addKeyField(CLASS_NAME, DBConstants.ASCENDING);
        }
        if (iKeyArea == 3)
        {
            keyArea = this.makeIndex(DBConstants.NOT_UNIQUE, BASE_CLASS_NAME_KEY);
            keyArea.addKeyField(BASE_CLASS_NAME, DBConstants.ASCENDING);
        }
        if (iKeyArea == 4)
        {
            keyArea = this.makeIndex(DBConstants.NOT_UNIQUE, CLASS_PROJECT_ID_KEY);
            keyArea.addKeyField(CLASS_PROJECT_ID, DBConstants.ASCENDING);
            keyArea.addKeyField(CLASS_NAME, DBConstants.ASCENDING);
        }
        if (keyArea == null)
            keyArea = super.setupKey(iKeyArea);     
        return keyArea;
    }
    /**
     * AddMasterListeners Method.
     */
    public void addMasterListeners()
    {
        super.addMasterListeners();
        
        this.getField(ClassInfo.CLASS_NAME).addListener(new MoveOnChangeHandler(this.getField(ClassInfo.CLASS_SOURCE_FILE), null, true, true));
    }
    /**
     * Get the link that will run this class.
     */
    public String getLink()
    {
        String strType = this.getField(ClassInfo.CLASS_TYPE).getString();
        String strLink = this.getField(ClassInfo.CLASS_NAME).getString();
        if (this.getField(ClassInfo.CLASS_PACKAGE).getLength() > 0)
            strLink = DBConstants.ROOT_PACKAGE + this.getField(ClassInfo.CLASS_PACKAGE).toString() + "." + strLink;
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
    public ClassInfoModel readClassInfo(PropertyOwner recordOwner, String className)
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
            this.getField(ClassInfo.CLASS_NAME).setString(className);
            boolean bSuccess = false;
            this.setKeyArea(ClassInfo.CLASS_NAME_KEY);
            if (className.length() > 0)
                bSuccess = this.seek("=");
            if (!bSuccess)
            {   // Not found, use standard screen maintenance screen
                if ((strParamMenu != null) && (strParamMenu.length() > 0))
                    this.getField(ClassInfo.CLASS_NAME).setString("MenuScreen");
                else if ((strParamRecord != null) && (strParamRecord.length() > 0))
                {
                    if ((strParamScreenType != null) && (strParamScreenType.length() > 0) && (strParamScreenType.equalsIgnoreCase(ThinMenuConstants.FORM)))
                        this.getField(ClassInfo.CLASS_NAME).setString("Screen");
                    else
                        this.getField(ClassInfo.CLASS_NAME).setString("GridScreen");
                }
                else
                    this.getField(ClassInfo.CLASS_NAME).setString(strParamHelp);
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
    public String getPackageName(ClassProject.CodeType codeType)
    {
        if (codeType == null)
            codeType = ClassProject.CodeType.THICK;
        String packageName = this.getField(ClassInfo.CLASS_PACKAGE).toString();
        ClassProject classProject = (ClassProject)((ReferenceField)this.getField(ClassInfo.CLASS_PROJECT_ID)).getReference();
        if (classProject != null)
            if ((classProject.getEditMode() == DBConstants.EDIT_IN_PROGRESS) || (classProject.getEditMode() == DBConstants.EDIT_CURRENT))
                packageName = classProject.getFullPackage(codeType, packageName);
        return packageName;
    }
    /**
     * GetClassName Method.
     */
    public String getClassName()
    {
        return this.getField(ClassInfo.CLASS_NAME).toString();
    }
    /**
     * GetFullClassName Method.
     */
    public String getFullClassName()
    {
        String className = this.getField(ClassInfo.CLASS_NAME).toString();
        ClassProject classProject = (ClassProject)((ReferenceField)this.getField(ClassInfo.CLASS_PROJECT_ID)).getReference();
        String packageName = classProject.getFullPackage(ClassProject.CodeType.THICK, this.getField(ClassInfo.CLASS_PACKAGE).toString());
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
        return this.getField(ClassInfo.CLASS_DESC).toString();
    }
    /**
     * GetClassExplain Method.
     */
    public String getClassExplain()
    {
        return this.getField(ClassInfo.CLASS_EXPLAIN).toString();
    }
    /**
     * GetClassHelp Method.
     */
    public String getClassHelp()
    {
        return this.getField(ClassInfo.CLASS_HELP).toString();
    }
    /**
     * GetClassType Method.
     */
    public String getClassType()
    {
        return this.getField(ClassInfo.CLASS_TYPE).toString();
    }
    /**
     * GetSeeAlso Method.
     */
    public String getSeeAlso()
    {
        return this.getField(ClassInfo.SEE_ALSO).toString();
    }
    /**
     * GetTechnicalInfo Method.
     */
    public String getTechnicalInfo()
    {
        return this.getField(ClassInfo.TECHNICAL_INFO).toString();
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
        FieldData fieldInfo = new FieldData(this.findRecordOwner());
        
        String strClass = this.getClassName();
        fieldInfo.setKeyArea(FieldData.FIELD_FILE_NAME_KEY);
        fieldInfo.addListener(new StringSubFileFilter(strClass, fieldInfo.getField(FieldData.FIELD_FILE_NAME), null, null, null, null));
        try   {
            out.println("<table border=1>");
            out.println("<tr>");
            out.println("<th>" + fieldInfo.getField(FieldData.FIELD_NAME).getFieldDesc() + "</th>");
            out.println("<th>" + fieldInfo.getField(FieldData.FIELD_CLASS).getFieldDesc() + "</th>");
            out.println("<th>" + fieldInfo.getField(FieldData.BASE_FIELD_NAME).getFieldDesc() + "</th>");
            out.println("<th>" + fieldInfo.getField(FieldData.MAXIMUM_LENGTH).getFieldDesc() + "</th>");
            out.println("<th>" + fieldInfo.getField(FieldData.FIELD_DESCRIPTION).getFieldDesc() + "</th>");
            out.println("</tr>");
            while (fieldInfo.hasNext())
            {
                fieldInfo.next();
                out.println("<tr>");
                out.println("<td>&#160;" + fieldInfo.getField(FieldData.FIELD_NAME).toString() + "</td>");
                out.println("<td>&#160;" + fieldInfo.getField(FieldData.FIELD_CLASS).toString() + "</td>");
                out.println("<td>&#160;" + fieldInfo.getField(FieldData.BASE_FIELD_NAME).toString() + "</td>");
                out.println("<td>&#160;" + fieldInfo.getField(FieldData.MAXIMUM_LENGTH).toString() + "</td>");
                out.println("<td>&#160;" + fieldInfo.getField(FieldData.FIELD_DESCRIPTION).toString() + "</td>");
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
        fileHdr.setKeyArea(FileHdr.FILE_NAME_KEY);
        fileHdr.getField(FileHdr.FILE_NAME).moveFieldToThis(this.getField(ClassInfo.CLASS_NAME));
        try   {
            if (fileHdr.seek("="))
            {
                strOptXML = XmlUtilities.createXMLStringRecord(fileHdr);
                FieldData fieldInfo = new FieldData(null);
                fieldInfo.setKeyArea(FieldData.FIELD_FILE_NAME_KEY);
                fieldInfo.addListener(new SubFileFilter(this.getField(ClassInfo.CLASS_NAME), FieldData.FIELD_FILE_NAME, null, null, null, null));
                strOptXML += Utility.startTag(XMLTags.FIELD_LIST);
                while (fieldInfo.hasNext())
                {
                    fieldInfo.next();
                    strOptXML += XmlUtilities.createXMLStringRecord(fieldInfo);
                }
                strOptXML += Utility.endTag(XMLTags.FIELD_LIST);
                fieldInfo.free();
        
                KeyInfo keyInfo = new KeyInfo(null);
                keyInfo.setKeyArea(KeyInfo.KEY_FILENAME_KEY);
                keyInfo.addListener(new SubFileFilter(this.getField(ClassInfo.CLASS_NAME), KeyInfo.KEY_FILENAME, null, null, null, null));
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
    public boolean isARecord(boolean isAFile)
    {
        Record recFileHdr = (Record)this.getRecordOwner().getRecord(FileHdr.FILE_HDR_FILE);
        if (recFileHdr == null)
        {
            recFileHdr = new FileHdr(this.getRecordOwner());
            this.addListener(new FreeOnFreeHandler(recFileHdr));
        }
        if (!recFileHdr.getField(FileHdr.FILE_NAME).equals(this.getField(ClassInfo.CLASS_NAME)))
        {
            try {
                recFileHdr.addNew();
                recFileHdr.getField(FileHdr.FILE_NAME).moveFieldToThis(this.getField(ClassInfo.CLASS_NAME));
                int oldKeyArea = recFileHdr.getDefaultOrder();
                recFileHdr.setKeyArea(FileHdr.FILE_NAME_KEY);
                recFileHdr.seek(null);
                recFileHdr.setKeyArea(oldKeyArea);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if ((recFileHdr.getEditMode() == DBConstants.EDIT_CURRENT) && (this.getField(ClassInfo.CLASS_NAME).toString().equals(recFileHdr.getField(FileHdr.FILE_NAME).getString())))
            return true;    // This is a file
        if (isAFile)
            return false;    // Just looking for files
        if (!"Record".equalsIgnoreCase(this.getField(ClassInfo.CLASS_TYPE).toString()))
            return false;     // If this isn't a physical file, don't build it.
        if (this.getField(ClassInfo.BASE_CLASS_NAME).toString().contains("ScreenRecord"))
            return false;
        if ("Interface".equalsIgnoreCase(this.getField(ClassInfo.CLASS_TYPE).toString()))   // An interface doesn't have an interface
            return false;     // If this isn't a physical file, don't build it.
        if (RESOURCE_CLASS.equals(this.getField(ClassInfo.BASE_CLASS_NAME).toString()))
            return false;     // Resource only class
        return true;    // This is a record
    }

}
