/**
 * @(#)DatabaseInfo.
 * Copyright © 2013 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.thin.main.db;

import java.util.*;
import org.jbundle.thin.base.util.*;

import org.jbundle.thin.base.db.*;

import org.jbundle.model.main.db.*;

public class DatabaseInfo extends FieldList
    implements DatabaseInfoModel
{
    private static final long serialVersionUID = 1L;


    public DatabaseInfo()
    {
        super();
    }
    public DatabaseInfo(Object recordOwner)
    {
        this();
        this.init(recordOwner);
    }
    public static final String DATABASE_INFO_FILE = "DatabaseInfo";
    /**
     *  Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? DatabaseInfo.DATABASE_INFO_FILE : super.getTableNames(bAddQuotes);
    }
    /**
     *  Get the Database Name.
     */
    public String getDatabaseName()
    {
        return "DatabaseInfo";
    }
    /**
     *  Is this a local (vs remote) file?.
     */
    public int getDatabaseType()
    {
        return Constants.REMOTE | Constants.SHARED_DATA;
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
        field = new FieldInfo(this, NAME, 30, null, null);
        field = new FieldInfo(this, DESCRIPTION, 30, null, null);
        field = new FieldInfo(this, VERSION, 20, null, null);
        field = new FieldInfo(this, START_ID, 10, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, END_ID, 10, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, BASE_DATABASE, 30, null, null);
        field = new FieldInfo(this, PROPERTIES, Constants.DEFAULT_FIELD_LENGTH, null, null);
    }
    /**
    * Set up the key areas.
    */
    public void setupKeys()
    {
        KeyAreaInfo keyArea = null;
        keyArea = new KeyAreaInfo(this, Constants.UNIQUE, ID_KEY);
        keyArea.addKeyField(ID, Constants.ASCENDING);
        keyArea = new KeyAreaInfo(this, Constants.NOT_UNIQUE, NAME_KEY);
        keyArea.addKeyField(NAME, Constants.ASCENDING);
    }
    /**
     * SetDatabaseName Method.
     */
    public void setDatabaseName(String databaseName)
    {
        // Not used in thin impl
    }

}
