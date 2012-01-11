/**
 * @(#)ProjectTask.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.thin.app.program.project.db;

import java.util.*;
import org.jbundle.thin.base.util.*;

import org.jbundle.thin.base.db.*;

public class ProjectTask extends org.jbundle.thin.main.db.Folder
    implements org.jbundle.model.app.program.project.db.ProjectTaskModel
{

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
        field = new FieldInfo(this, "ID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field.setHidden(true);
        field = new FieldInfo(this, "LastChanged", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Date.class);
        field.setHidden(true);
        field = new FieldInfo(this, "Deleted", 10, null, new Boolean(false));
        field.setDataClass(Boolean.class);
        field.setHidden(true);
        field = new FieldInfo(this, "Name", 120, null, null);
        field = new FieldInfo(this, "ParentProjectTaskID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, "Sequence", 5, null, new Short((short)0));
        field.setDataClass(Short.class);
        field = new FieldInfo(this, "Comment", 32000, null, null);
        field.setDataClass(Object.class);
        field = new FieldInfo(this, "Code", 30, null, null);
        field = new FieldInfo(this, "StartDateTime", 25, null, null);
        field.setDataClass(Date.class);
        field = new FieldInfo(this, "Duration", 15, null, new Double(1));
        field.setDataClass(Double.class);
        field.setScale(-1);
        //field = new FieldInfo(this, "EndDateTime", 25, null, null);
        //field.setDataClass(Date.class);
        field = new FieldInfo(this, "Progress", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Float.class);
        field = new FieldInfo(this, "ProjectID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, "ProjectVersionID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, "ProjectTypeID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, "ProjectStatusID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, "AssignedUserID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, "ProjectPriorityID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, "EnteredDate", 25, null, null);
        field.setDataClass(Date.class);
        field = new FieldInfo(this, "EnteredByUserID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, "ChangedDate", 25, null, null);
        field.setDataClass(Date.class);
        field = new FieldInfo(this, "ChangedByUserID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, "HasChildren", 10, null, new Boolean(false));
        field.setDataClass(Boolean.class);
    }
    /**
    * Set up the key areas.
    */
    public void setupKeys()
    {
        KeyAreaInfo keyArea = null;
        keyArea = new KeyAreaInfo(this, Constants.UNIQUE, "PrimaryKey");
        keyArea.addKeyField("ID", Constants.ASCENDING);
        keyArea = new KeyAreaInfo(this, Constants.NOT_UNIQUE, "ParentProjectTaskID");
        keyArea.addKeyField("ParentProjectTaskID", Constants.ASCENDING);
        keyArea.addKeyField("StartDateTime", Constants.ASCENDING);
        keyArea.addKeyField("Sequence", Constants.ASCENDING);
        keyArea.addKeyField("Name", Constants.ASCENDING);
    }

}
