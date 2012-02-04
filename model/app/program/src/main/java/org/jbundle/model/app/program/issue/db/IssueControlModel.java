/**
 * @(#)IssueControlModel.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.app.program.issue.db;

import org.jbundle.model.db.*;

public interface IssueControlModel extends Rec
{

    //public static final String ID = ID;
    //public static final String LAST_CHANGED = LAST_CHANGED;
    //public static final String DELETED = DELETED;
    public static final String PROJECT_ID = "ProjectID";
    public static final String PROJECT_VERSION_ID = "ProjectVersionID";
    public static final String ISSUE_TYPE_ID = "IssueTypeID";
    public static final String ISSUE_STATUS_ID = "IssueStatusID";
    public static final String ISSUE_PRIORITY_ID = "IssuePriorityID";

    public static final String ISSUE_CONTROL_FILE = "IssueControl";
    public static final String THIN_CLASS = "org.jbundle.thin.app.program.issue.db.IssueControl";
    public static final String THICK_CLASS = "org.jbundle.app.program.issue.db.IssueControl";

}
