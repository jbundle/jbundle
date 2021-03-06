/**
 * @(#)UserLog.
 * Copyright © 2013 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.thin.main.user.db;

import java.util.*;
import org.jbundle.thin.base.util.*;

import org.jbundle.thin.base.db.*;

import org.jbundle.model.main.user.db.*;

public class UserLog extends FieldList
    implements UserLogModel
{
    private static final long serialVersionUID = 1L;


    public UserLog()
    {
        super();
    }
    public UserLog(Object recordOwner)
    {
        this();
        this.init(recordOwner);
    }
    public static final String USER_LOG_FILE = "UserLog";
    /**
     *  Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? UserLog.USER_LOG_FILE : super.getTableNames(bAddQuotes);
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
        return Constants.REMOTE | Constants.USER_DATA | Constants.SERVER_REWRITES | Constants.DONT_LOG_TRX;
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
        field = new FieldInfo(this, USER_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, LOG_TIME, 25, null, null);
        field.setDataClass(Date.class);
        field = new FieldInfo(this, MESSAGE, 127, null, null);
        field = new FieldInfo(this, USER_LOG_TYPE_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
    }
    /**
    * Set up the key areas.
    */
    public void setupKeys()
    {
        KeyAreaInfo keyArea = null;
        keyArea = new KeyAreaInfo(this, Constants.UNIQUE, ID_KEY);
        keyArea.addKeyField(ID, Constants.ASCENDING);
        keyArea = new KeyAreaInfo(this, Constants.NOT_UNIQUE, USER_ID_KEY);
        keyArea.addKeyField(USER_ID, Constants.ASCENDING);
        keyArea.addKeyField(LOG_TIME, Constants.ASCENDING);
    }
    /**
     * Log Method.
     */
    public void log(int iUserID, int iUserLogTypeID, String strMessage)
    {
        // TODO - add thin log code here
    }

}
