/**
 * @(#)ProjectModel.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.app.program.project.db;

import org.jbundle.model.main.db.*;

public interface ProjectModel extends FolderModel
{

    //public static final String ID = ID;
    //public static final String LAST_CHANGED = LAST_CHANGED;
    //public static final String DELETED = DELETED;
    //public static final String NAME = NAME;
    public static final String MAIN_PROJECT_ID = PARENT_FOLDER_ID;
    //public static final String SEQUENCE = SEQUENCE;
    //public static final String COMMENT = COMMENT;
    //public static final String CODE = CODE;
    public static final String USER_ID = "UserID";
    public static final String PROJECT_SCREEN_CLASS = "org.jbundle.app.program.project.screen.ProjectScreen";
    public static final String PROJECT_GRID_SCREEN_CLASS = "org.jbundle.app.program.project.screen.ProjectGridScreen";

    public static final String PROJECT_FILE = "Project";
    public static final String THIN_CLASS = "org.jbundle.thin.app.program.project.db.Project";
    public static final String THICK_CLASS = "org.jbundle.app.program.project.db.Project";

}
