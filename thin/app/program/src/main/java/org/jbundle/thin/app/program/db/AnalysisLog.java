/**
 * @(#)AnalysisLog.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.thin.app.program.db;

import java.util.*;
import org.jbundle.thin.base.util.*;

import org.jbundle.thin.base.db.*;

public class AnalysisLog extends FieldList
{

    public AnalysisLog()
    {
        super();
    }
    public AnalysisLog(Object recordOwner)
    {
        this();
        this.init(recordOwner);
    }
    public static final String ANALYSIS_LOG_FILE = "AnalysisLog";
    /**
     *  Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? AnalysisLog.ANALYSIS_LOG_FILE : super.getTableNames(bAddQuotes);
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
        field = new FieldInfo(this, "SystemID", 10, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, "ObjectID", 10, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, "ClassName", 255, null, null);
        field = new FieldInfo(this, "DatabaseName", 255, null, null);
        field = new FieldInfo(this, "InitTime", 25, null, null);
        field.setDataClass(Date.class);
        field = new FieldInfo(this, "FreeTime", 25, null, null);
        field.setDataClass(Date.class);
        field = new FieldInfo(this, "RecordOwner", 255, null, null);
        field = new FieldInfo(this, "StackTrace", 32000, null, null);
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
        keyArea = new KeyAreaInfo(this, Constants.NOT_UNIQUE, "ObjectID");
        keyArea.addKeyField("SystemID", Constants.ASCENDING);
        keyArea.addKeyField("ObjectID", Constants.ASCENDING);
    }

}
