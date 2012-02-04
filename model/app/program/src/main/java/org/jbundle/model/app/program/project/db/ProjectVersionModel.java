/**
 * @(#)ProjectVersionModel.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.app.program.project.db;

import org.jbundle.model.db.*;

public interface ProjectVersionModel extends Rec
{

    //public static final String ID = ID;
    //public static final String LAST_CHANGED = LAST_CHANGED;
    //public static final String DELETED = DELETED;
    public static final String DESCRIPTION = "Description";
    public static final String PROJECT_ID = "ProjectID";
    public static final String USER_ID = "UserID";
    public static final String CLOSED = "Closed";
    public static final String DUE_DATE = "DueDate";

    public static final String DESCRIPTION_KEY = "Description";

    public static final String PROJECT_ID_KEY = "ProjectID";

    public static final String PROJECT_VERSION_FILE = "ProjectVersion";
    public static final String THIN_CLASS = "org.jbundle.thin.app.program.project.db.ProjectVersion";
    public static final String THICK_CLASS = "org.jbundle.app.program.project.db.ProjectVersion";

}
