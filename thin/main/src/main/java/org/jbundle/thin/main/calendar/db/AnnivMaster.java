/**
 * @(#)AnnivMaster.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.thin.main.calendar.db;

import java.util.*;
import org.jbundle.thin.base.util.*;

import org.jbundle.thin.base.db.*;

public class AnnivMaster extends org.jbundle.thin.base.db.FieldList
    implements org.jbundle.model.main.calendar.db.AnnivMasterModel
{

    public AnnivMaster()
    {
        super();
    }
    public AnnivMaster(Object recordOwner)
    {
        this();
        this.init(recordOwner);
    }
    public static final String ANNIV_MASTER_FILE = "AnnivMaster";
    /**
     *  Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? AnnivMaster.ANNIV_MASTER_FILE : super.getTableNames(bAddQuotes);
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
        field = new FieldInfo(this, "ID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field.setHidden(true);
        field = new FieldInfo(this, "LastChanged", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Date.class);
        field.setHidden(true);
        field = new FieldInfo(this, "Deleted", 10, null, new Boolean(false));
        field.setDataClass(Boolean.class);
        field.setHidden(true);
        field = new FieldInfo(this, "StartDateTime", 25, null, null);
        field.setDataClass(Date.class);
        field = new FieldInfo(this, "EndDateTime", 25, null, null);
        field.setDataClass(Date.class);
        field = new FieldInfo(this, "Description", 60, null, null);
        field = new FieldInfo(this, "RepeatIntervalID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, "RepeatCount", 5, null, null);
        field.setDataClass(Short.class);
        field = new FieldInfo(this, "CalendarCategoryID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, "Hidden", 10, null, new Boolean(false));
        field.setDataClass(Boolean.class);
        field = new FieldInfo(this, "Properties", Constants.DEFAULT_FIELD_LENGTH, null, null);
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
        keyArea = new KeyAreaInfo(this, Constants.NOT_UNIQUE, "StartDateTime");
        keyArea.addKeyField("StartDateTime", Constants.ASCENDING);
    }

}
