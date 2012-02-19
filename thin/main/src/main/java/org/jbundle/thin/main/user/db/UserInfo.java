/**
 * @(#)UserInfo.
 * Copyright © 2012 jbundle.org. All rights reserved.
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
    private static final long serialVersionUID = 1L;


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
        field = new FieldInfo(this, ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field.setHidden(true);
        field = new FieldInfo(this, LAST_CHANGED, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Date.class);
        field.setHidden(true);
        field = new FieldInfo(this, DELETED, 10, null, new Boolean(false));
        field.setDataClass(Boolean.class);
        field.setHidden(true);
        field = new FieldInfo(this, PROPERTIES, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Object.class);
        field = new FieldInfo(this, USER_NAME, 60, null, null);
        field = new FieldInfo(this, FIRST_NAME, 20, null, null);
        field = new FieldInfo(this, LAST_NAME, 20, null, null);
        field = new FieldInfo(this, EXTENSION, 4, null, null);
        field = new FieldInfo(this, PASSWORD, 80, null, null);
        field = new FieldInfo(this, TERMINATION_DATE, 12, null, null);
        field.setDataClass(Date.class);
        field.setScale(Constants.DATE_ONLY);
        field = new FieldInfo(this, READ_ONLY_RECORD, 10, null, null);
        field.setDataClass(Boolean.class);
        field = new FieldInfo(this, USER_GROUP_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, USER_REFERENCE, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        //field = new FieldInfo(this, FRAMES, 10, null, null);
        //field.setDataClass(Boolean.class);
        //field = new FieldInfo(this, MENUBARS, 10, null, null);
        //field = new FieldInfo(this, NAV_MENUS, 10, null, null);
        //field = new FieldInfo(this, JAVA, 10, null, null);
        //field = new FieldInfo(this, BANNERS, 10, null, null);
        //field = new FieldInfo(this, LOGOS, 10, null, null);
        //field = new FieldInfo(this, TRAILERS, 10, null, null);
        //field = new FieldInfo(this, HELP_PAGE, 10, null, null);
        //field = new FieldInfo(this, LANGUAGE, 2, null, null);
        //field = new FieldInfo(this, HOME, 30, null, null);
        //field = new FieldInfo(this, MENU, 30, null, null);
        //field = new FieldInfo(this, MENU_DESC, 10, null, null);
        //field.setDataClass(Boolean.class);
        //field = new FieldInfo(this, CONTACT_TYPE_ID, 10, null, null);
        //field.setDataClass(Integer.class);
        //field = new FieldInfo(this, CONTACT_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        //field.setDataClass(Integer.class);
    }
    /**
    * Set up the key areas.
    */
    public void setupKeys()
    {
        KeyAreaInfo keyArea = null;
        keyArea = new KeyAreaInfo(this, Constants.UNIQUE, "ID");
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
