/**
 * @(#)ScreenIn.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.thin.app.program.db;

import java.util.*;
import org.jbundle.thin.base.util.*;

import org.jbundle.thin.base.db.*;

import org.jbundle.model.app.program.db.*;

public class ScreenIn extends FieldList
    implements ScreenInModel
{
    private static final long serialVersionUID = 1L;


    public ScreenIn()
    {
        super();
    }
    public ScreenIn(Object recordOwner)
    {
        this();
        this.init(recordOwner);
    }
    public static final String SCREEN_IN_FILE = "ScreenIn";
    /**
     *  Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? ScreenIn.SCREEN_IN_FILE : super.getTableNames(bAddQuotes);
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
        field = new FieldInfo(this, SCREEN_IN_PROG_NAME, 40, null, null);
        field = new FieldInfo(this, SCREEN_OUT_NUMBER, 2, null, null);
        field.setDataClass(Short.class);
        field = new FieldInfo(this, SCREEN_ITEM_NUMBER, 4, null, null);
        field.setDataClass(Short.class);
        field = new FieldInfo(this, SCREEN_FILE_NAME, 40, null, null);
        field = new FieldInfo(this, SCREEN_FIELD_NAME, 40, null, null);
        field = new FieldInfo(this, SCREEN_ROW, 8, null, null);
        field.setDataClass(Short.class);
        field = new FieldInfo(this, SCREEN_COL, 4, null, null);
        field.setDataClass(Short.class);
        field = new FieldInfo(this, SCREEN_GROUP, 4, null, null);
        field = new FieldInfo(this, SCREEN_PHYSICAL_NUM, 4, null, null);
        field.setDataClass(Short.class);
        field = new FieldInfo(this, SCREEN_LOCATION, 20, null, null);
        field = new FieldInfo(this, SCREEN_FIELD_DESC, 30, null, null);
        field = new FieldInfo(this, SCREEN_TEXT, 9999, null, null);
        field.setDataClass(Object.class);
        field = new FieldInfo(this, SCREEN_ANCHOR, 20, null, null);
        field = new FieldInfo(this, SCREEN_CONTROL_TYPE, 20, null, null);
    }
    /**
    * Set up the key areas.
    */
    public void setupKeys()
    {
        KeyAreaInfo keyArea = null;
        keyArea = new KeyAreaInfo(this, Constants.UNIQUE, ID_KEY);
        keyArea.addKeyField(ID, Constants.ASCENDING);
        keyArea = new KeyAreaInfo(this, Constants.NOT_UNIQUE, SCREEN_IN_PROG_NAME_KEY);
        keyArea.addKeyField(SCREEN_IN_PROG_NAME, Constants.ASCENDING);
        keyArea.addKeyField(SCREEN_ITEM_NUMBER, Constants.ASCENDING);
    }

}
