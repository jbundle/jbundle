/**
 * @(#)ProjectTask.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.thin.app.program.project.db;

import java.util.*;
import org.jbundle.thin.base.util.*;

import org.jbundle.thin.base.db.*;

import org.jbundle.thin.main.db.*;
import org.jbundle.model.app.program.project.db.*;

public class ProjectTask extends Folder
    implements ProjectTaskModel
{
    private static final long serialVersionUID = 1L;


    public ProjectTask()
    {
        super();
    }
    public ProjectTask(Object recordOwner)
    {
        this();
        this.init(recordOwner);
    }
    public static final String PROJECT_TASK_FILE = "ProjectTask";
    /**
     *  Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? ProjectTask.PROJECT_TASK_FILE : super.getTableNames(bAddQuotes);
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
        field = new FieldInfo(this, NAME, 120, null, null);
        field = new FieldInfo(this, PARENT_PROJECT_TASK_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, SEQUENCE, 5, null, new Short((short)0));
        field.setDataClass(Short.class);
        field = new FieldInfo(this, COMMENT, 32000, null, null);
        field = new FieldInfo(this, CODE, 30, null, null);
        field = new FieldInfo(this, START_DATE_TIME, 25, null, null);
        field.setDataClass(Date.class);
        field = new FieldInfo(this, DURATION, 15, null, new Double(1));
        field.setDataClass(Double.class);
        field.setScale(-1);
        //field = new FieldInfo(this, END_DATE_TIME, 25, null, null);
        //field.setDataClass(Date.class);
        field = new FieldInfo(this, PROGRESS, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Float.class);
        field = new FieldInfo(this, PROJECT_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, PROJECT_VERSION_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, PROJECT_TYPE_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, PROJECT_STATUS_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, ASSIGNED_USER_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, PROJECT_PRIORITY_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, ENTERED_DATE, 25, null, null);
        field.setDataClass(Date.class);
        field = new FieldInfo(this, ENTERED_BY_USER_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, CHANGED_DATE, 25, null, null);
        field.setDataClass(Date.class);
        field = new FieldInfo(this, CHANGED_BY_USER_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, HAS_CHILDREN, 10, null, new Boolean(false));
        field.setDataClass(Boolean.class);
    }
    /**
    * Set up the key areas.
    */
    public void setupKeys()
    {
        KeyAreaInfo keyArea = null;
        keyArea = new KeyAreaInfo(this, Constants.UNIQUE, ID_KEY);
        keyArea.addKeyField(ID, Constants.ASCENDING);
        keyArea = new KeyAreaInfo(this, Constants.NOT_UNIQUE, PARENT_PROJECT_TASK_ID_KEY);
        keyArea.addKeyField(PARENT_PROJECT_TASK_ID, Constants.ASCENDING);
        keyArea.addKeyField(START_DATE_TIME, Constants.ASCENDING);
        keyArea.addKeyField(SEQUENCE, Constants.ASCENDING);
        keyArea.addKeyField(NAME, Constants.ASCENDING);
    }

}
