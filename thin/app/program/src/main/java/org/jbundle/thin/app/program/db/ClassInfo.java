/**
 * @(#)ClassInfo.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.thin.app.program.db;

import java.io.*;
import java.util.*;
import org.jbundle.model.*;
import java.util.*;
import org.jbundle.thin.base.util.*;

import org.jbundle.thin.base.db.*;

import org.jbundle.model.app.program.db.*;

public class ClassInfo extends FieldList
    implements ClassInfoModel
{
    private static final long serialVersionUID = 1L;


    public ClassInfo()
    {
        super();
    }
    public ClassInfo(Object recordOwner)
    {
        this();
        this.init(recordOwner);
    }
    public static final String CLASS_INFO_FILE = "ClassInfo";
    /**
     *  Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? ClassInfo.CLASS_INFO_FILE : super.getTableNames(bAddQuotes);
    }
    /**
     *  Get the Database Name.
     */
    public String getDatabaseName()
    {
        return "program";
    }
    /**
     *  Is this a local (vs remote) file?.
     */
    public int getDatabaseType()
    {
        return Constants.REMOTE | Constants.SHARED_DATA | Constants.HIERARCHICAL | Constants.LOCALIZABLE;
    }
    /**
    * Set up the screen input fields.
    */
    public void setupFields()
    {
        FieldInfo field = null;
        field = new FieldInfo(this, ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field.setHidden(true);
        field = new FieldInfo(this, LAST_CHANGED, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Date.class);
        field.setHidden(true);
        field = new FieldInfo(this, DELETED, 10, null, new Boolean(false));
        field.setDataClass(Boolean.class);
        field.setHidden(true);
        field = new FieldInfo(this, CLASS_NAME, 40, null, null);
        field = new FieldInfo(this, BASE_CLASS_NAME, 40, null, null);
        field = new FieldInfo(this, CLASS_DESC, 255, null, null);
        field = new FieldInfo(this, CLASS_PROJECT_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, CLASS_PACKAGE, 60, null, null);
        field = new FieldInfo(this, CLASS_SOURCE_FILE, 40, null, null);
        field = new FieldInfo(this, CLASS_TYPE, 20, null, null);
        field = new FieldInfo(this, CLASS_EXPLAIN, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Object.class);
        field = new FieldInfo(this, CLASS_HELP, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Object.class);
        field = new FieldInfo(this, CLASS_IMPLEMENTS, 60, null, null);
        field = new FieldInfo(this, SEE_ALSO, 32000, null, null);
        field.setDataClass(Object.class);
        field = new FieldInfo(this, TECHNICAL_INFO, 32000, null, null);
        field.setDataClass(Object.class);
        field = new FieldInfo(this, COPY_DESC_FROM, 50, null, null);
    }
    /**
    * Set up the key areas.
    */
    public void setupKeys()
    {
        KeyAreaInfo keyArea = null;
        keyArea = new KeyAreaInfo(this, Constants.UNIQUE, "ID");
        keyArea.addKeyField("ID", Constants.ASCENDING);
        keyArea = new KeyAreaInfo(this, Constants.NOT_UNIQUE, "ClassName");
        keyArea.addKeyField("ClassName", Constants.ASCENDING);
        keyArea = new KeyAreaInfo(this, Constants.NOT_UNIQUE, "ClassSourceFile");
        keyArea.addKeyField("ClassSourceFile", Constants.ASCENDING);
        keyArea.addKeyField("ClassName", Constants.ASCENDING);
        keyArea = new KeyAreaInfo(this, Constants.NOT_UNIQUE, "BaseClassName");
        keyArea.addKeyField("BaseClassName", Constants.ASCENDING);
        keyArea = new KeyAreaInfo(this, Constants.NOT_UNIQUE, "ClassProjectID");
        keyArea.addKeyField("ClassProjectID", Constants.ASCENDING);
        keyArea.addKeyField("ClassName", Constants.ASCENDING);
    }
    /**
     * Get the link that will run this class.
     */
    public String getLink()
    {
        return null; // Empty implementation
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
        return null; // Empty implementation
    }
    /**
     * GetClassName Method.
     */
    public String getClassName()
    {
        return null; // Empty implementation
    }
    /**
     * GetFullClassName Method.
     */
    public String getFullClassName()
    {
        return null; // Empty implementation
    }
    /**
     * GetClassDesc Method.
     */
    public String getClassDesc()
    {
        return null; // Empty implementation
    }
    /**
     * GetClassExplain Method.
     */
    public String getClassExplain()
    {
        return null; // Empty implementation
    }
    /**
     * GetClassHelp Method.
     */
    public String getClassHelp()
    {
        return null; // Empty implementation
    }
    /**
     * GetClassType Method.
     */
    public String getClassType()
    {
        return null; // Empty implementation
    }
    /**
     * GetSeeAlso Method.
     */
    public String getSeeAlso()
    {
        return null; // Empty implementation
    }
    /**
     * GetTechnicalInfo Method.
     */
    public String getTechnicalInfo()
    {
        return null; // Empty implementation
    }
    /**
     * IsValidRecord Method.
     */
    public boolean isValidRecord()
    {
        return false; // Empty implementation
    }
    /**
     * PrintHtmlTechInfo Method.
     */
    public void printHtmlTechInfo(PrintWriter out, String strTag, String strParams, String strData)
    {
        // Empty implementation
    }
    /**
     * PrintScreen Method.
     */
    public void printScreen(PrintWriter out, ResourceBundle reg)
    {
        // Empty implementation
    }

}
