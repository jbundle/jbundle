/**
 * @(#)Snake.
 * Copyright © 2012 jbundle.org. All rights reserved.
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
    private static final long serialVersionUID = 1L;


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
        field = new FieldInfo(this, ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field.setHidden(true);
        field = new FieldInfo(this, LAST_CHANGED, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Date.class);
        field.setHidden(true);
        field = new FieldInfo(this, DELETED, 10, null, new Boolean(false));
        field.setDataClass(Boolean.class);
        field.setHidden(true);
        field = new FieldInfo(this, REPTILE_TYPE_ID, 10, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, EXTRA, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field = new FieldInfo(this, SPECIAL, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field = new FieldInfo(this, NAME, 30, null, null);
        field = new FieldInfo(this, VET_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, WEIGHT, 5, null, null);
        field.setDataClass(Short.class);
        field = new FieldInfo(this, CLEARANCE, 5, null, null);
        field.setDataClass(Short.class);
    }
    /**
    * Set up the key areas.
    */
    public void setupKeys()
    {
        KeyAreaInfo keyArea = null;
        keyArea = new KeyAreaInfo(this, Constants.UNIQUE, "ID");
        keyArea.addKeyField("ID", Constants.ASCENDING);
        keyArea = new KeyAreaInfo(this, Constants.NOT_UNIQUE, "VetID");
        keyArea.addKeyField("VetID", Constants.ASCENDING);
        keyArea = new KeyAreaInfo(this, Constants.SECONDARY_KEY, "Name");
        keyArea.addKeyField("Name", Constants.ASCENDING);
    }

}
