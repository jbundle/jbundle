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
    private static final long serialVersionUID = 1L;


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
        field = new FieldInfo(this, ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field.setHidden(true);
        field = new FieldInfo(this, LAST_CHANGED, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Date.class);
        field.setHidden(true);
        field = new FieldInfo(this, DELETED, 10, null, new Boolean(false));
        field.setDataClass(Boolean.class);
        field.setHidden(true);
        field = new FieldInfo(this, RESOURCE_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, CODE, 40, null, null);
        field = new FieldInfo(this, LANGUAGE, 2, null, null);
        field = new FieldInfo(this, LOCALE, 2, null, null);
        field = new FieldInfo(this, KEY_VALUE, 80, null, null);
        field = new FieldInfo(this, OBJECT_VALUE, 32000, null, null);
        field.setDataClass(Object.class);
    }
    /**
    * Set up the key areas.
    */
    public void setupKeys()
    {
        KeyAreaInfo keyArea = null;
        keyArea = new KeyAreaInfo(this, Constants.UNIQUE, "ID");
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
