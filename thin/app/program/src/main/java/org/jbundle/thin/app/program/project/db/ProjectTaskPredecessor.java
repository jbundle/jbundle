/**
 * @(#)ProjectTaskPredecessor.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.thin.app.program.project.db;

import java.util.*;
import org.jbundle.thin.base.util.*;

import org.jbundle.thin.base.db.*;

import org.jbundle.model.app.program.project.db.*;

public class ProjectTaskPredecessor extends FieldList
    implements ProjectTaskPredecessorModel
{
    private static final long serialVersionUID = 1L;


    public ProjectTaskPredecessor()
    {
        super();
    }
    public ProjectTaskPredecessor(Object recordOwner)
    {
        this();
        this.init(recordOwner);
    }
    public static final String PROJECT_TASK_PREDECESSOR_FILE = "ProjectTaskPredecessor";
    /**
     *  Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? ProjectTaskPredecessor.PROJECT_TASK_PREDECESSOR_FILE : super.getTableNames(bAddQuotes);
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
        field = new FieldInfo(this, PROJECT_TASK_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, PROJECT_TASK_PREDECESSOR_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, PREDECESSOR_TYPE, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field = new FieldInfo(this, PREDECESSOR_DELAY, 10, null, null);
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
        keyArea = new KeyAreaInfo(this, Constants.NOT_UNIQUE, PROJECT_TASK_ID_KEY);
        keyArea.addKeyField(PROJECT_TASK_ID, Constants.ASCENDING);
        keyArea.addKeyField(PROJECT_TASK_PREDECESSOR_ID, Constants.ASCENDING);
        keyArea = new KeyAreaInfo(this, Constants.NOT_UNIQUE, PROJECT_TASK_PREDECESSOR_ID_KEY);
        keyArea.addKeyField(PROJECT_TASK_PREDECESSOR_ID, Constants.ASCENDING);
        keyArea.addKeyField(PROJECT_TASK_ID, Constants.ASCENDING);
    }

}
