/**
 * @(#)MainControl.
 * Copyright © 2013 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.thin.main.db;

import java.util.*;
import org.jbundle.thin.base.util.*;

import org.jbundle.thin.base.db.*;

import org.jbundle.model.main.db.*;

public class MainControl extends FieldList
    implements MainControlModel
{
    private static final long serialVersionUID = 1L;


    public MainControl()
    {
        super();
    }
    public MainControl(Object recordOwner)
    {
        this();
        this.init(recordOwner);
    }
    public static final String MAIN_CONTROL_FILE = "MainControl";
    /**
     *  Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? MainControl.MAIN_CONTROL_FILE : super.getTableNames(bAddQuotes);
    }
    /**
     *  Get the Database Name.
     */
    public String getDatabaseName()
    {
        return "main";
    }
    /**
     *  Is this a local (vs remote) file?.
     */
    public int getDatabaseType()
    {
        return Constants.LOCAL | Constants.SHARED_DATA | Constants.LOCALIZABLE;
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
        field = new FieldInfo(this, PACKAGE_NAME, 30, null, null);
    }
    /**
    * Set up the key areas.
    */
    public void setupKeys()
    {
        KeyAreaInfo keyArea = null;
        keyArea = new KeyAreaInfo(this, Constants.UNIQUE, ID_KEY);
        keyArea.addKeyField(ID, Constants.ASCENDING);
    }

}
