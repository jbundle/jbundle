/**
 * @(#)ScreenIn.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.thin.app.program.db;

import java.util.*;
import org.jbundle.thin.base.util.*;

import org.jbundle.thin.base.db.*;

public class ScreenIn extends org.jbundle.thin.base.db.FieldList
    implements org.jbundle.model.app.program.db.ScreenInModel
{

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
        field = new FieldInfo(this, "ID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field.setHidden(true);
        field = new FieldInfo(this, "LastChanged", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Date.class);
        field.setHidden(true);
        field = new FieldInfo(this, "Deleted", 10, null, new Boolean(false));
        field.setDataClass(Boolean.class);
        field.setHidden(true);
        field = new FieldInfo(this, "ScreenInProgName", 40, null, null);
        field = new FieldInfo(this, "ScreenOutNumber", 2, null, null);
        field.setDataClass(Short.class);
        field = new FieldInfo(this, "ScreenItemNumber", 4, null, null);
        field.setDataClass(Short.class);
        field = new FieldInfo(this, "ScreenFileName", 40, null, null);
        field = new FieldInfo(this, "ScreenFieldName", 40, null, null);
        field = new FieldInfo(this, "ScreenRow", 8, null, null);
        field.setDataClass(Short.class);
        field = new FieldInfo(this, "ScreenCol", 4, null, null);
        field.setDataClass(Short.class);
        field = new FieldInfo(this, "ScreenGroup", 4, null, null);
        field = new FieldInfo(this, "ScreenPhysicalNum", 4, null, null);
        field.setDataClass(Short.class);
        field = new FieldInfo(this, "ScreenLocation", 20, null, null);
        field = new FieldInfo(this, "ScreenFieldDesc", 30, null, null);
        field = new FieldInfo(this, "ScreenText", 9999, null, null);
        field.setDataClass(Object.class);
        field = new FieldInfo(this, "ScreenAnchor", 20, null, null);
        field = new FieldInfo(this, "ScreenControlType", 20, null, null);
    }
    /**
    * Set up the key areas.
    */
    public void setupKeys()
    {
        KeyAreaInfo keyArea = null;
        keyArea = new KeyAreaInfo(this, Constants.UNIQUE, "PrimaryKey");
        keyArea.addKeyField("ID", Constants.ASCENDING);
        keyArea = new KeyAreaInfo(this, Constants.NOT_UNIQUE, "ScreenInProgName");
        keyArea.addKeyField("ScreenInProgName", Constants.ASCENDING);
        keyArea.addKeyField("ScreenItemNumber", Constants.ASCENDING);
    }

}
