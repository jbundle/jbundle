/**
 * @(#)ProjectTaskPredecessorModel.
 * Copyright © 2013 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.app.program.project.db;

import org.jbundle.model.db.*;

public interface ProjectTaskPredecessorModel extends Rec
{

    //public static final String ID = ID;
    //public static final String LAST_CHANGED = LAST_CHANGED;
    //public static final String DELETED = DELETED;
    public static final String PROJECT_TASK_ID = "ProjectTaskID";
    public static final String PROJECT_TASK_PREDECESSOR_ID = "ProjectTaskPredecessorID";
    public static final String PREDECESSOR_TYPE = "PredecessorType";
    public static final String PREDECESSOR_DELAY = "PredecessorDelay";

    public static final String PROJECT_TASK_ID_KEY = "ProjectTaskID";

    public static final String PROJECT_TASK_PREDECESSOR_ID_KEY = "ProjectTaskPredecessorID";
    public static final String PROJECT_TASK_PREDECESSOR_SCREEN_CLASS = "org.jbundle.app.program.project.screen.ProjectTaskPredecessorScreen";
    public static final String PROJECT_TASK_PREDECESSOR_GRID_SCREEN_CLA = "org.jbundle.app.program.project.screen.ProjectTaskPredecessorGridScreen";

    public static final String PROJECT_TASK_PREDECESSOR_FILE = "ProjectTaskPredecessor";
    public static final String THIN_CLASS = "org.jbundle.thin.app.program.project.db.ProjectTaskPredecessor";
    public static final String THICK_CLASS = "org.jbundle.app.program.project.db.ProjectTaskPredecessor";

}
