/**
 * @(#)UserPermission.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.thin.main.user.db;

import java.util.*;
import org.jbundle.thin.base.util.*;

import org.jbundle.thin.base.db.*;

import org.jbundle.model.main.user.db.*;

public class UserPermission extends FieldList
    implements UserPermissionModel
{
    private static final long serialVersionUID = 1L;


    public UserPermission()
    {
        super();
    }
    public UserPermission(Object recordOwner)
    {
        this();
        this.init(recordOwner);
    }
    public static final String USER_PERMISSION_FILE = "UserPermission";
    /**
     *  Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? UserPermission.USER_PERMISSION_FILE : super.getTableNames(bAddQuotes);
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
        field = new FieldInfo(this, USER_GROUP_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, USER_RESOURCE_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, LOGIN_LEVEL, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field = new FieldInfo(this, ACCESS_LEVEL, Constants.DEFAULT_FIELD_LENGTH, null, null);
    }
    /**
    * Set up the key areas.
    */
    public void setupKeys()
    {
        KeyAreaInfo keyArea = null;
        keyArea = new KeyAreaInfo(this, Constants.UNIQUE, "ID");
        keyArea.addKeyField("ID", Constants.ASCENDING);
        keyArea = new KeyAreaInfo(this, Constants.UNIQUE, "UserGroupID");
        keyArea.addKeyField("UserGroupID", Constants.ASCENDING);
        keyArea.addKeyField("UserResourceID", Constants.ASCENDING);
        keyArea.addKeyField("LoginLevel", Constants.ASCENDING);
        keyArea = new KeyAreaInfo(this, Constants.UNIQUE, "UserResourceID");
        keyArea.addKeyField("UserResourceID", Constants.ASCENDING);
        keyArea.addKeyField("UserGroupID", Constants.ASCENDING);
        keyArea.addKeyField("LoginLevel", Constants.ASCENDING);
    }

}
