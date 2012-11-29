/**
 * @(#)AnalysisLog.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.thin.app.program.db;

import org.jbundle.model.db.*;
import java.util.*;
import org.jbundle.thin.base.util.*;

import org.jbundle.thin.base.db.*;

import org.jbundle.model.app.program.db.*;

public class AnalysisLog extends FieldList
    implements AnalysisLogModel
{
    private static final long serialVersionUID = 1L;


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
        field = new FieldInfo(this, ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field.setHidden(true);
        field = new FieldInfo(this, LAST_CHANGED, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Date.class);
        field.setHidden(true);
        field = new FieldInfo(this, DELETED, 10, null, new Boolean(false));
        field.setDataClass(Boolean.class);
        field.setHidden(true);
        field = new FieldInfo(this, SYSTEM_ID, 10, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, OBJECT_ID, 10, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, CLASS_NAME, 255, null, null);
        field = new FieldInfo(this, DATABASE_NAME, 255, null, null);
        field = new FieldInfo(this, INIT_TIME, 25, null, null);
        field.setDataClass(Date.class);
        field = new FieldInfo(this, FREE_TIME, 25, null, null);
        field.setDataClass(Date.class);
        field = new FieldInfo(this, RECORD_OWNER, 255, null, null);
        field = new FieldInfo(this, STACK_TRACE, 32000, null, null);
    }
    /**
    * Set up the key areas.
    */
    public void setupKeys()
    {
        KeyAreaInfo keyArea = null;
        keyArea = new KeyAreaInfo(this, Constants.UNIQUE, ID_KEY);
        keyArea.addKeyField(ID, Constants.ASCENDING);
        keyArea = new KeyAreaInfo(this, Constants.NOT_UNIQUE, OBJECT_ID_KEY);
        keyArea.addKeyField(SYSTEM_ID, Constants.ASCENDING);
        keyArea.addKeyField(OBJECT_ID, Constants.ASCENDING);
    }
    /**
     * Log that this record has been added.
     * Call this from the end of record.init
     * @param record the record that is being added.
     */
    public void logAddRecord(Rec record, int iSystemID)
    {
        // Empty implementation
    }
    /**
     * Log that this record has been freed.
     * Call this from the end of record.free
     * @param record the record that is being added.
     */
    public void logRemoveRecord(Rec record, int iSystemID)
    {
        // Empty implementation
    }

}
