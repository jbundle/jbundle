/**
 * @(#)KeyInfo.
 * Copyright © 2013 jbundle.org. All rights reserved.
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
    private static final long serialVersionUID = 1L;


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
        field = new FieldInfo(this, ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field.setHidden(true);
        field = new FieldInfo(this, LAST_CHANGED, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Date.class);
        field.setHidden(true);
        field = new FieldInfo(this, DELETED, 10, null, new Boolean(false));
        field.setDataClass(Boolean.class);
        field.setHidden(true);
        field = new FieldInfo(this, KEY_FILENAME, 40, null, null);
        field = new FieldInfo(this, KEY_NUMBER, 2, null, null);
        field.setDataClass(Short.class);
        field = new FieldInfo(this, KEY_FIELD_1, 40, null, null);
        field = new FieldInfo(this, KEY_FIELD_2, 40, null, null);
        field = new FieldInfo(this, KEY_FIELD_3, 40, null, null);
        field = new FieldInfo(this, KEY_FIELD_4, 40, null, null);
        field = new FieldInfo(this, KEY_FIELD_5, 40, null, null);
        field = new FieldInfo(this, KEY_FIELD_6, 40, null, null);
        field = new FieldInfo(this, KEY_FIELD_7, 40, null, null);
        field = new FieldInfo(this, KEY_FIELD_8, 40, null, null);
        field = new FieldInfo(this, KEY_FIELD_9, 40, null, null);
        field = new FieldInfo(this, KEY_NAME, 40, null, null);
        field = new FieldInfo(this, KEY_TYPE, 1, null, null);
        field = new FieldInfo(this, INCLUDE_SCOPE, 10, null, new Integer(0x004));
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
        keyArea = new KeyAreaInfo(this, Constants.UNIQUE, KEY_FILENAME_KEY);
        keyArea.addKeyField(KEY_FILENAME, Constants.ASCENDING);
        keyArea.addKeyField(KEY_NUMBER, Constants.ASCENDING);
    }

}
