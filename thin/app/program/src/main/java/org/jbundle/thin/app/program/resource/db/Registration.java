/**
 * @(#)Registration.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.thin.app.program.resource.db;

import java.util.*;
import org.jbundle.thin.base.util.*;

import org.jbundle.thin.base.db.*;

import org.jbundle.model.app.program.resource.db.*;

public class Registration extends FieldList
    implements RegistrationModel
{

    //public static final String ID = ID;
    //public static final String LAST_CHANGED = LAST_CHANGED;
    //public static final String DELETED = DELETED;

    public Registration()
    {
        super();
    }
    public Registration(Object recordOwner)
    {
        this();
        this.init(recordOwner);
    }
    public static final String REGISTRATION_FILE = "Registration";
    /**
     *  Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? Registration.REGISTRATION_FILE : super.getTableNames(bAddQuotes);
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
        return Constants.LOCAL | Constants.USER_DATA;
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
        field = new FieldInfo(this, "ResourceID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, "Code", 40, null, null);
        field = new FieldInfo(this, "Language", 2, null, null);
        field = new FieldInfo(this, "Locale", 2, null, null);
        field = new FieldInfo(this, "KeyValue", 80, null, null);
        field = new FieldInfo(this, "ObjectValue", 32000, null, null);
        field.setDataClass(Object.class);
    }
    /**
    * Set up the key areas.
    */
    public void setupKeys()
    {
        KeyAreaInfo keyArea = null;
        keyArea = new KeyAreaInfo(this, Constants.UNIQUE, "PrimaryKey");
        keyArea.addKeyField("ID", Constants.ASCENDING);
        keyArea = new KeyAreaInfo(this, Constants.UNIQUE, "ResourceID");
        keyArea.addKeyField("ResourceID", Constants.ASCENDING);
        keyArea.addKeyField("Language", Constants.ASCENDING);
        keyArea.addKeyField("Locale", Constants.ASCENDING);
        keyArea.addKeyField("KeyValue", Constants.ASCENDING);
        keyArea = new KeyAreaInfo(this, Constants.UNIQUE, "Code");
        keyArea.addKeyField("ResourceID", Constants.ASCENDING);
        keyArea.addKeyField("Code", Constants.ASCENDING);
        keyArea.addKeyField("Language", Constants.ASCENDING);
        keyArea.addKeyField("Locale", Constants.ASCENDING);
        keyArea.addKeyField("KeyValue", Constants.ASCENDING);
    }

}
