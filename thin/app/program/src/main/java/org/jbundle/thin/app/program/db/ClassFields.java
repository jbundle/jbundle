/**
 * @(#)ClassFields.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.thin.app.program.db;

import java.util.*;
import org.jbundle.thin.base.util.*;

import org.jbundle.thin.base.db.*;

import org.jbundle.model.app.program.db.*;

public class ClassFields extends FieldList
    implements ClassFieldsModel
{
    private static final long serialVersionUID = 1L;


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
        field = new FieldInfo(this, ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field.setHidden(true);
        field = new FieldInfo(this, LAST_CHANGED, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Date.class);
        field.setHidden(true);
        field = new FieldInfo(this, DELETED, 10, null, new Boolean(false));
        field.setDataClass(Boolean.class);
        field.setHidden(true);
        field = new FieldInfo(this, CLASS_INFO_CLASS_NAME, 40, null, null);
        field = new FieldInfo(this, CLASS_INFO_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, CLASS_FIELD_CLASS, 60, null, null);
        field = new FieldInfo(this, CLASS_FIELD_SEQUENCE, 10, null, new Integer(0));
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, CLASS_FIELD_NAME, 40, null, null);
        field = new FieldInfo(this, CLASS_FIELD_DESC, 30, null, null);
        field = new FieldInfo(this, CLASS_FIELD_PROTECT, 30, null, null);
        field = new FieldInfo(this, CLASS_FIELD_INITIAL, 32000, null, null);
        field.setDataClass(Object.class);
        field = new FieldInfo(this, CLASS_FIELD_INITIAL_VALUE, 32000, null, null);
        field.setDataClass(Object.class);
        field = new FieldInfo(this, CLASS_FIELDS_TYPE, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field = new FieldInfo(this, INCLUDE_SCOPE, 10, null, new Integer(0x001));
        field.setDataClass(Integer.class);
    }
    /**
    * Set up the key areas.
    */
    public void setupKeys()
    {
        KeyAreaInfo keyArea = null;
        keyArea = new KeyAreaInfo(this, Constants.UNIQUE, ID_KEY);
        keyArea.addKeyField(ID, Constants.ASCENDING);
        keyArea = new KeyAreaInfo(this, Constants.NOT_UNIQUE, CLASS_INFO_CLASS_NAME_KEY);
        keyArea.addKeyField(CLASS_INFO_CLASS_NAME, Constants.ASCENDING);
        keyArea.addKeyField(CLASS_FIELD_SEQUENCE, Constants.ASCENDING);
        keyArea.addKeyField(CLASS_FIELD_NAME, Constants.ASCENDING);
    }

}
