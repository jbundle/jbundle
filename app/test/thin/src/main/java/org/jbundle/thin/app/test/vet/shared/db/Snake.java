/**
 * @(#)Snake.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.thin.app.test.vet.shared.db;

import java.util.*;
import org.jbundle.thin.base.util.*;

import org.jbundle.thin.base.db.*;

import org.jbundle.thin.app.test.vet.shared.db.*;
import org.jbundle.model.app.test.vet.shared.db.*;

public class Snake extends Reptile
    implements SnakeModel
{

    //public static final String ID = ID;
    //public static final String LAST_CHANGED = LAST_CHANGED;
    //public static final String DELETED = DELETED;
    //public static final String REPTILE_TYPE_ID = REPTILE_TYPE_ID;
    //public static final String EXTRA = EXTRA;
    //public static final String SPECIAL = SPECIAL;
    //public static final String NAME = NAME;
    //public static final String VET_ID = VET_ID;
    //public static final String WEIGHT = WEIGHT;
    //public static final String CLEARANCE = CLEARANCE;

    public Snake()
    {
        super();
    }
    public Snake(Object recordOwner)
    {
        this();
        this.init(recordOwner);
    }
    public static final String SNAKE_FILE = "Reptile";
    /**
     *  Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? Snake.SNAKE_FILE : super.getTableNames(bAddQuotes);
    }
    /**
     *  Get the Database Name.
     */
    public String getDatabaseName()
    {
        return "test";
    }
    /**
     *  Is this a local (vs remote) file?.
     */
    public int getDatabaseType()
    {
        return Constants.REMOTE | Constants.SHARED_TABLE | Constants.USER_DATA;
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
        field = new FieldInfo(this, "ReptileTypeID", 10, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, "Extra", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field = new FieldInfo(this, "Special", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field = new FieldInfo(this, "Name", 30, null, null);
        field = new FieldInfo(this, "VetID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, "Weight", 5, null, null);
        field.setDataClass(Short.class);
        field = new FieldInfo(this, "Clearance", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Short.class);
    }
    /**
    * Set up the key areas.
    */
    public void setupKeys()
    {
        KeyAreaInfo keyArea = null;
        keyArea = new KeyAreaInfo(this, Constants.UNIQUE, "PrimaryKey");
        keyArea.addKeyField("ID", Constants.ASCENDING);
        keyArea = new KeyAreaInfo(this, Constants.NOT_UNIQUE, "VetID");
        keyArea.addKeyField("VetID", Constants.ASCENDING);
        keyArea = new KeyAreaInfo(this, Constants.SECONDARY_KEY, "Name");
        keyArea.addKeyField("Name", Constants.ASCENDING);
    }

}
