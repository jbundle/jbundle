/**
 * @(#)ClassInfo.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.thin.app.program.db;

import java.util.*;
import org.jbundle.thin.base.util.*;

import org.jbundle.thin.base.db.*;

public class ClassInfo extends FieldList
{

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
        field = new FieldInfo(this, "ID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field.setHidden(true);
        field = new FieldInfo(this, "LastChanged", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Date.class);
        field.setHidden(true);
        field = new FieldInfo(this, "Deleted", 10, null, new Boolean(false));
        field.setDataClass(Boolean.class);
        field.setHidden(true);
        field = new FieldInfo(this, "ClassName", 40, null, null);
        field = new FieldInfo(this, "BaseClassName", 40, null, null);
        field = new FieldInfo(this, "ClassDesc", 255, null, null);
        field = new FieldInfo(this, "ClassProjectID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, "ClassPackage", 60, null, null);
        field = new FieldInfo(this, "ClassSourceFile", 40, null, null);
        field = new FieldInfo(this, "ClassType", 20, null, null);
        field = new FieldInfo(this, "ClassExplain", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Object.class);
        field = new FieldInfo(this, "ClassHelp", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Object.class);
        field = new FieldInfo(this, "ClassImplements", 60, null, null);
        field = new FieldInfo(this, "SeeAlso", 32000, null, null);
        field.setDataClass(Object.class);
        field = new FieldInfo(this, "TechnicalInfo", 32000, null, null);
        field.setDataClass(Object.class);
        field = new FieldInfo(this, "CopyDescFrom", 50, null, null);
    }
    /**
    * Set up the key areas.
    */
    public void setupKeys()
    {
        KeyAreaInfo keyArea = null;
        keyArea = new KeyAreaInfo(this, Constants.UNIQUE, "PrimaryKey");
        keyArea.addKeyField("ID", Constants.ASCENDING);
        keyArea = new KeyAreaInfo(this, Constants.UNIQUE, "ClassName");
        keyArea.addKeyField("ClassName", Constants.ASCENDING);
        keyArea = new KeyAreaInfo(this, Constants.UNIQUE, "ClassSourceFile");
        keyArea.addKeyField("ClassSourceFile", Constants.ASCENDING);
        keyArea.addKeyField("ClassName", Constants.ASCENDING);
        keyArea = new KeyAreaInfo(this, Constants.NOT_UNIQUE, "BaseClassName");
        keyArea.addKeyField("BaseClassName", Constants.ASCENDING);
        keyArea = new KeyAreaInfo(this, Constants.NOT_UNIQUE, "ClassProjectID");
        keyArea.addKeyField("ClassProjectID", Constants.ASCENDING);
        keyArea.addKeyField("ClassName", Constants.ASCENDING);
    }

}
