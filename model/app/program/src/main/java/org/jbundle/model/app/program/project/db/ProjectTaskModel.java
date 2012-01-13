/**
 * @(#)ProjectTaskModel.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.app.program.project.db;

import org.jbundle.model.main.db.*;

public interface ProjectTaskModel extends FolderModel
{
    public static final String PROJECT_PREDECESSOR_DETAIL_SCREEN = "Predecessors";
    public static final String PROJECT_TASK_CALENDAR_SCREEN = "Project Calendar";

    public static final String PROJECT_TASK_FILE = "ProjectTask";
    public static final String THIN_CLASS = "org.jbundle.thin.app.program.project.db.ProjectTask";
    public static final String THICK_CLASS = "org.jbundle.app.program.project.db.ProjectTask";

}
