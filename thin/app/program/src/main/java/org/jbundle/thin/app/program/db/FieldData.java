/**
 * @(#)FieldData.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.thin.app.program.db;

import java.util.*;
import org.jbundle.thin.base.util.*;

import org.jbundle.thin.base.db.*;

public class FieldData extends FieldList
{

    public FieldData()
    {
        super();
    }
    public FieldData(Object recordOwner)
    {
        this();
        this.init(recordOwner);
    }
    public static final String FIELD_DATA_FILE = "FieldData";
    /**
     *  Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? FieldData.FIELD_DATA_FILE : super.getTableNames(bAddQuotes);
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
        field = new FieldInfo(this, "FieldName", 40, null, null);
        field = new FieldInfo(this, "FieldClass", 40, null, null);
        field = new FieldInfo(this, "BaseFieldName", 40, null, null);
        field = new FieldInfo(this, "DependentFieldName", 40, null, null);
        field = new FieldInfo(this, "MinimumLength", 4, null, null);
        field.setDataClass(Short.class);
        field = new FieldInfo(this, "MaximumLength", 5, null, null);
        field.setDataClass(Short.class);
        field = new FieldInfo(this, "DefaultValue", 50, null, null);
        field = new FieldInfo(this, "InitialValue", 50, null, null);
        field = new FieldInfo(this, "FieldDescription", 100, null, null);
        field = new FieldInfo(this, "FieldDescVertical", 14, null, null);
        field = new FieldInfo(this, "FieldType", 1, null, null);
        field = new FieldInfo(this, "FieldDimension", 3, null, null);
        field.setDataClass(Short.class);
        field = new FieldInfo(this, "FieldFileName", 40, null, null);
        field = new FieldInfo(this, "FieldSeqNo", 4, null, null);
        field.setDataClass(Short.class);
        field = new FieldInfo(this, "FieldNotNull", 1, null, null);
        field.setDataClass(Boolean.class);
        //field = new FieldInfo(this, "DataClass", 20, null, null);
        field = new FieldInfo(this, "Hidden", 10, null, null);
        field.setDataClass(Boolean.class);
        field = new FieldInfo(this, "IncludeScope", 10, null, new Integer(0x001));
        field.setDataClass(Integer.class);
    }
    /**
    * Set up the key areas.
    */
    public void setupKeys()
    {
        KeyAreaInfo keyArea = null;
        keyArea = new KeyAreaInfo(this, Constants.UNIQUE, "PrimaryKey");
        keyArea.addKeyField("ID", Constants.ASCENDING);
        keyArea = new KeyAreaInfo(this, Constants.NOT_UNIQUE, "FieldFileName");
        keyArea.addKeyField("FieldFileName", Constants.ASCENDING);
        keyArea.addKeyField("FieldSeqNo", Constants.ASCENDING);
        keyArea = new KeyAreaInfo(this, Constants.UNIQUE, "FieldName");
        keyArea.addKeyField("FieldFileName", Constants.ASCENDING);
        keyArea.addKeyField("FieldName", Constants.ASCENDING);
    }

}
