/**
 * @(#)ProjectTaskModel.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.app.program.project.db;

import org.jbundle.model.main.db.*;

public interface ProjectTaskModel extends FolderModel
{

    //public static final String ID = ID;
    //public static final String LAST_CHANGED = LAST_CHANGED;
    //public static final String DELETED = DELETED;
    //public static final String NAME = NAME;
    public static final String PARENT_PROJECT_TASK_ID = PARENT_FOLDER_ID;
    //public static final String SEQUENCE = SEQUENCE;
    //public static final String COMMENT = COMMENT;
    //public static final String CODE = CODE;
    public static final String START_DATE_TIME = "StartDateTime";
    public static final String DURATION = "Duration";
    public static final String END_DATE_TIME = "EndDateTime";
    public static final String PROGRESS = "Progress";
    public static final String PROJECT_ID = "ProjectID";
    public static final String PROJECT_VERSION_ID = "ProjectVersionID";
    public static final String PROJECT_TYPE_ID = "ProjectTypeID";
    public static final String PROJECT_STATUS_ID = "ProjectStatusID";
    public static final String ASSIGNED_USER_ID = "AssignedUserID";
    public static final String PROJECT_PRIORITY_ID = "ProjectPriorityID";
    public static final String ENTERED_DATE = "EnteredDate";
    public static final String ENTERED_BY_USER_ID = "EnteredByUserID";
    public static final String CHANGED_DATE = "ChangedDate";
    public static final String CHANGED_BY_USER_ID = "ChangedByUserID";
    public static final String HAS_CHILDREN = "HasChildren";

    public static final String PARENT_PROJECT_TASK_ID_KEY = "ParentProjectTaskID";
    public static final String PROJECT_PREDECESSOR_DETAIL_SCREEN = "Predecessors";
    public static final String PROJECT_TASK_CALENDAR_SCREEN = "Project Calendar";
    public static final String PROJECT_TASK_SCREEN_CLASS = "org.jbundle.app.program.project.screen.ProjectTaskScreen";
    public static final String PROJECT_TASK_GRID_SCREEN_CLASS = "org.jbundle.app.program.project.screen.ProjectTaskGridScreen";

    public static final String PROJECT_TASK_FILE = "ProjectTask";
    public static final String THIN_CLASS = "org.jbundle.thin.app.program.project.db.ProjectTask";
    public static final String THICK_CLASS = "org.jbundle.app.program.project.db.ProjectTask";

}
