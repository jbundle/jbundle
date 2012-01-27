/**
 * @(#)ProjectModel.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.app.program.project.db;

import org.jbundle.model.main.db.*;

public interface ProjectModel extends FolderModel
{
    public static final String PROJECT_SCREEN_CLASS = "org.jbundle.app.program.project.screen.ProjectScreen";
    public static final String PROJECT_GRID_SCREEN_CLASS = "org.jbundle.app.program.project.screen.ProjectGridScreen";

    public static final String PROJECT_FILE = "Project";
    public static final String THIN_CLASS = "org.jbundle.thin.app.program.project.db.Project";
    public static final String THICK_CLASS = "org.jbundle.app.program.project.db.Project";

}