/**
 * @(#)UserInfo.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.thin.main.user.db;

import java.util.*;
import org.jbundle.thin.base.util.*;

import org.jbundle.thin.base.db.*;

import org.jbundle.thin.main.db.*;
import org.jbundle.model.main.user.db.*;

public class UserInfo extends PropertiesRecord
    implements UserInfoModel
{

    //public static final String ID = ID;
    //public static final String LAST_CHANGED = LAST_CHANGED;
    //public static final String DELETED = DELETED;
    //public static final String PROPERTIES = PROPERTIES;

    public UserInfo()
    {
        super();
    }
    public UserInfo(Object recordOwner)
    {
        this();
        this.init(recordOwner);
    }
    public static final String USER_INFO_FILE = "UserInfo";
    /**
     *  Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? UserInfo.USER_INFO_FILE : super.getTableNames(bAddQuotes);
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
        field = new FieldInfo(this, "Properties", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Object.class);
        field = new FieldInfo(this, "UserName", 60, null, null);
        field = new FieldInfo(this, "FirstName", 20, null, null);
        field = new FieldInfo(this, "LastName", 20, null, null);
        field = new FieldInfo(this, "Extension", 4, null, null);
        field = new FieldInfo(this, "Password", 80, null, null);
        field = new FieldInfo(this, "TerminationDate", 12, null, null);
        field.setDataClass(Date.class);
        field.setScale(Constants.DATE_ONLY);
        field = new FieldInfo(this, "ReadOnlyRecord", 10, null, null);
        field.setDataClass(Boolean.class);
        field = new FieldInfo(this, "UserGroupID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, "UserReference", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        //field = new FieldInfo(this, "Frames", 10, null, null);
        //field.setDataClass(Boolean.class);
        //field = new FieldInfo(this, "Menubars", 10, null, null);
        //field = new FieldInfo(this, "NavMenus", 10, null, null);
        //field = new FieldInfo(this, "Java", 10, null, null);
        //field = new FieldInfo(this, "Banners", 10, null, null);
        //field = new FieldInfo(this, "Logos", 10, null, null);
        //field = new FieldInfo(this, "Trailers", 10, null, null);
        //field = new FieldInfo(this, "HelpPage", 10, null, null);
        //field = new FieldInfo(this, "Language", 2, null, null);
        //field = new FieldInfo(this, "Home", 30, null, null);
        //field = new FieldInfo(this, "Menu", 30, null, null);
        //field = new FieldInfo(this, "MenuDesc", 10, null, null);
        //field.setDataClass(Boolean.class);
        //field = new FieldInfo(this, "ContactTypeID", 10, null, null);
        //field.setDataClass(Integer.class);
        //field = new FieldInfo(this, "ContactID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        //field.setDataClass(Integer.class);
    }
    /**
    * Set up the key areas.
    */
    public void setupKeys()
    {
        KeyAreaInfo keyArea = null;
        keyArea = new KeyAreaInfo(this, Constants.UNIQUE, "PrimaryKey");
        keyArea.addKeyField("ID", Constants.ASCENDING);
        keyArea = new KeyAreaInfo(this, Constants.SECONDARY_KEY, "UserName");
        keyArea.addKeyField("UserName", Constants.ASCENDING);
    }
    /**
     * GetUserInfo Method.
     */
    public boolean getUserInfo(String strUser, boolean bForceRead)
    {
        return false; // TODO - Finish thin impl
    }
    /**
     * SetupNewUserHandler Method.
     */
    public void setupNewUserHandler()
    {
        // TODO Write thin impl
    }

}
