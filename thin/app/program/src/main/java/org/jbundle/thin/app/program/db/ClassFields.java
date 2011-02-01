/**
 *  @(#)ClassFields.
 *  Copyright © 2010 tourapp.com. All rights reserved.
 */
package org.jbundle.thin.app.program.db;

import java.util.*;
import org.jbundle.thin.base.util.*;

import org.jbundle.thin.base.db.*;

public class ClassFields extends FieldList
{

    public ClassFields()
    {
        super();
    }
    public ClassFields(Object recordOwner)
    {
        this();
        this.init(recordOwner);
    }
    public static final String CLASS_FIELDS_FILE = "ClassFields";
    /**
     *  Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? ClassFields.CLASS_FIELDS_FILE : super.getTableNames(bAddQuotes);
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
        return Constants.REMOTE | Constants.SHARED_DATA | Constants.HIERARCHICAL;
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
        field = new FieldInfo(this, "ClassInfoClassName", 40, null, null);
        field = new FieldInfo(this, "ClassInfoID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, "ClassFieldClass", 60, null, null);
        field = new FieldInfo(this, "ClassFieldSequence", 10, null, new Integer(0));
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, "ClassFieldName", 40, null, null);
        field = new FieldInfo(this, "ClassFieldDesc", 30, null, null);
        field = new FieldInfo(this, "ClassFieldProtect", 30, null, null);
        field = new FieldInfo(this, "ClassFieldInitial", 32000, null, null);
        field.setDataClass(Object.class);
        field = new FieldInfo(this, "ClassFieldInitialValue", 32000, null, null);
        field.setDataClass(Object.class);
        field = new FieldInfo(this, "ThinInclude", 10, null, null);
        field.setDataClass(Boolean.class);
        field = new FieldInfo(this, "ThickInclude", 10, null, new Boolean(true));
        field.setDataClass(Boolean.class);
        field = new FieldInfo(this, "ClassFieldsType", Constants.DEFAULT_FIELD_LENGTH, null, null);
    }
    /**
    * Set up the key areas.
    */
    public void setupKeys()
    {
        KeyAreaInfo keyArea = null;
        keyArea = new KeyAreaInfo(this, Constants.UNIQUE, "PrimaryKey");
        keyArea.addKeyField("ID", Constants.ASCENDING);
        keyArea = new KeyAreaInfo(this, Constants.UNIQUE, "ClassInfoClassName");
        keyArea.addKeyField("ClassInfoClassName", Constants.ASCENDING);
        keyArea.addKeyField("ClassFieldSequence", Constants.ASCENDING);
        keyArea.addKeyField("ClassFieldName", Constants.ASCENDING);
    }

}
