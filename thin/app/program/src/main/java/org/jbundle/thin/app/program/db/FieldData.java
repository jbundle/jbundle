/**
 * @(#)FieldData.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.thin.app.program.db;

import java.util.*;
import org.jbundle.thin.base.util.*;

import org.jbundle.thin.base.db.*;

import org.jbundle.model.app.program.db.*;

public class FieldData extends FieldList
    implements FieldDataModel
{
    private static final long serialVersionUID = 1L;


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
        field = new FieldInfo(this, ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field.setHidden(true);
        field = new FieldInfo(this, LAST_CHANGED, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Date.class);
        field.setHidden(true);
        field = new FieldInfo(this, DELETED, 10, null, new Boolean(false));
        field.setDataClass(Boolean.class);
        field.setHidden(true);
        field = new FieldInfo(this, FIELD_NAME, 40, null, null);
        field = new FieldInfo(this, FIELD_CLASS, 40, null, null);
        field = new FieldInfo(this, BASE_FIELD_NAME, 40, null, null);
        field = new FieldInfo(this, DEPENDENT_FIELD_NAME, 40, null, null);
        field = new FieldInfo(this, MINIMUM_LENGTH, 4, null, null);
        field.setDataClass(Short.class);
        field = new FieldInfo(this, MAXIMUM_LENGTH, 5, null, null);
        field.setDataClass(Short.class);
        field = new FieldInfo(this, DEFAULT_VALUE, 50, null, null);
        field = new FieldInfo(this, INITIAL_VALUE, 50, null, null);
        field = new FieldInfo(this, FIELD_DESCRIPTION, 100, null, null);
        field = new FieldInfo(this, FIELD_DESC_VERTICAL, 14, null, null);
        field = new FieldInfo(this, FIELD_TYPE, 1, null, null);
        field = new FieldInfo(this, FIELD_DIMENSION, 3, null, null);
        field.setDataClass(Short.class);
        field = new FieldInfo(this, FIELD_FILE_NAME, 40, null, null);
        field = new FieldInfo(this, FIELD_SEQ_NO, 4, null, null);
        field.setDataClass(Short.class);
        field = new FieldInfo(this, FIELD_NOT_NULL, 1, null, null);
        field.setDataClass(Boolean.class);
        //field = new FieldInfo(this, DATA_CLASS, 20, null, null);
        field = new FieldInfo(this, HIDDEN, 10, null, null);
        field.setDataClass(Boolean.class);
        field = new FieldInfo(this, INCLUDE_SCOPE, 10, null, new Integer(0x001));
        field.setDataClass(Integer.class);
    }
    /**
    * Set up the key areas.
    */
    public void setupKeys()
    {
        KeyAreaInfo keyArea = null;
        keyArea = new KeyAreaInfo(this, Constants.UNIQUE, "ID");
        keyArea.addKeyField("ID", Constants.ASCENDING);
        keyArea = new KeyAreaInfo(this, Constants.NOT_UNIQUE, "FieldFileName");
        keyArea.addKeyField("FieldFileName", Constants.ASCENDING);
        keyArea.addKeyField("FieldSeqNo", Constants.ASCENDING);
        keyArea = new KeyAreaInfo(this, Constants.UNIQUE, "FieldName");
        keyArea.addKeyField("FieldFileName", Constants.ASCENDING);
        keyArea.addKeyField("FieldName", Constants.ASCENDING);
    }

}
