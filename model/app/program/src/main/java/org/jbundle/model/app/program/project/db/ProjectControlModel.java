/**
 * @(#)ProjectControlModel.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.app.program.project.db;

import org.jbundle.model.db.*;

public interface ProjectControlModel extends Rec
{

    //public static final String ID = ID;
    public static final String START_ICON = "StartIcon";
    public static final String END_ICON = "EndIcon";
    public static final String START_PARENT_ICON = "StartParentIcon";
    public static final String END_PARENT_ICON = "EndParentIcon";
    public static final String TASK_COLOR = "TaskColor";
    public static final String TASK_SELECT_COLOR = "TaskSelectColor";
    public static final String PARENT_TASK_COLOR = "ParentTaskColor";
    public static final String PARENT_TASK_SELECT_COLOR = "ParentTaskSelectColor";

    public static final String PROJECT_CONTROL_FILE = "ProjectControl";
    public static final String THIN_CLASS = "org.jbundle.thin.app.program.project.db.ProjectControl";
    public static final String THICK_CLASS = "org.jbundle.app.program.project.db.ProjectControl";

}
