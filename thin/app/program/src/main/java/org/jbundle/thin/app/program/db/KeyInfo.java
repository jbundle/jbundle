/**
 * @(#)KeyInfo.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.thin.app.program.db;

import java.util.*;
import org.jbundle.thin.base.util.*;

import org.jbundle.thin.base.db.*;

import org.jbundle.model.app.program.db.*;

public class KeyInfo extends FieldList
    implements KeyInfoModel
{

    public KeyInfo()
    {
        super();
    }
    public KeyInfo(Object recordOwner)
    {
        this();
        this.init(recordOwner);
    }
    public static final String KEY_INFO_FILE = "KeyInfo";
    /**
     *  Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? KeyInfo.KEY_INFO_FILE : super.getTableNames(bAddQuotes);
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
        field = new FieldInfo(this, "KeyFilename", 40, null, null);
        field = new FieldInfo(this, "KeyNumber", 2, null, null);
        field.setDataClass(Short.class);
        field = new FieldInfo(this, "KeyField1", 40, null, null);
        field = new FieldInfo(this, "KeyField2", 40, null, null);
        field = new FieldInfo(this, "KeyField3", 40, null, null);
        field = new FieldInfo(this, "KeyField4", 40, null, null);
        field = new FieldInfo(this, "KeyField5", 40, null, null);
        field = new FieldInfo(this, "KeyField6", 40, null, null);
        field = new FieldInfo(this, "KeyField7", 40, null, null);
        field = new FieldInfo(this, "KeyField8", 40, null, null);
        field = new FieldInfo(this, "KeyField9", 40, null, null);
        field = new FieldInfo(this, "KeyName", 40, null, null);
        field = new FieldInfo(this, "KeyType", 1, null, null);
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
        keyArea = new KeyAreaInfo(this, Constants.UNIQUE, "KeyFilename");
        keyArea.addKeyField("KeyFilename", Constants.ASCENDING);
        keyArea.addKeyField("KeyNumber", Constants.ASCENDING);
    }

}
