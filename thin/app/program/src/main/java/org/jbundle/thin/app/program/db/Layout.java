/**
 * @(#)Layout.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.thin.app.program.db;

import java.util.*;
import org.jbundle.thin.base.util.*;

import org.jbundle.thin.base.db.*;

import org.jbundle.thin.main.db.*;
import org.jbundle.model.app.program.db.*;

public class Layout extends Folder
    implements LayoutModel
{
    private static final long serialVersionUID = 1L;


    public Layout()
    {
        super();
    }
    public Layout(Object recordOwner)
    {
        this();
        this.init(recordOwner);
    }
    public static final String LAYOUT_FILE = "Layout";
    /**
     *  Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? Layout.LAYOUT_FILE : super.getTableNames(bAddQuotes);
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
        return Constants.REMOTE | Constants.USER_DATA;
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
        field = new FieldInfo(this, NAME, 50, null, null);
        field = new FieldInfo(this, PARENT_FOLDER_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, SEQUENCE, 5, null, null);
        field.setDataClass(Short.class);
        field = new FieldInfo(this, COMMENT, 255, null, null);
        field.setDataClass(Object.class);
        field = new FieldInfo(this, CODE, 30, null, null);
        field = new FieldInfo(this, TYPE, 50, null, null);
        field = new FieldInfo(this, FIELD_VALUE, 255, null, null);
        field = new FieldInfo(this, RETURNS_VALUE, 50, null, null);
        field = new FieldInfo(this, MAX, 10, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, SYSTEM, 30, null, null);
        field = new FieldInfo(this, COMMENTS, 10, null, null);
        field.setDataClass(Boolean.class);
    }
    /**
    * Set up the key areas.
    */
    public void setupKeys()
    {
        KeyAreaInfo keyArea = null;
        keyArea = new KeyAreaInfo(this, Constants.UNIQUE, ID_KEY);
        keyArea.addKeyField(ID, Constants.ASCENDING);
        keyArea = new KeyAreaInfo(this, Constants.NOT_UNIQUE, PARENT_FOLDER_ID_KEY);
        keyArea.addKeyField(PARENT_FOLDER_ID, Constants.ASCENDING);
        keyArea.addKeyField(SEQUENCE, Constants.ASCENDING);
        keyArea.addKeyField(ID, Constants.ASCENDING);
    }

}
