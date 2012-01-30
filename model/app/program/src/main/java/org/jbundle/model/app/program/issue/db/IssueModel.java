/**
 * @(#)IssueModel.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.app.program.issue.db;

import org.jbundle.model.db.*;

public interface IssueModel extends Rec
{

    //public static final String ID = ID;
    public static final String DESCRIPTION = "Description";
    public static final String PROJECT_ID = "ProjectID";
    public static final String PROJECT_VERSION_ID = "ProjectVersionID";
    public static final String ISSUE_TYPE_ID = "IssueTypeID";
    public static final String ISSUE_STATUS_ID = "IssueStatusID";
    public static final String ASSIGNED_USER_ID = "AssignedUserID";
    public static final String ISSUE_PRIORITY_ID = "IssuePriorityID";
    public static final String ISSUE_SEQUENCE = "IssueSequence";
    public static final String ENTERED_DATE = "EnteredDate";
    public static final String ENTERED_BY_USER_ID = "EnteredByUserID";
    public static final String CHANGED_DATE = "ChangedDate";
    public static final String CHANGED_BY_USER_ID = "ChangedByUserID";
    public static final String CLASS_INFO_ID = "ClassInfoID";

    public static final String DESCRIPTION_KEY = "Description";

    public static final String ISSUE_PRIORITY_ID_KEY = "IssuePriorityID";

    public static final String CLASS_INFO_ID_KEY = "ClassInfoID";
    public static final String ISSUE_SCREEN_CLASS = "org.jbundle.app.program.issue.screen.IssueScreen";
    public static final String ISSUE_GRID_SCREEN_CLASS = "org.jbundle.app.program.issue.screen.IssueGridScreen";

    public static final String ISSUE_FILE = "Issue";
    public static final String THIN_CLASS = "org.jbundle.thin.app.program.issue.db.Issue";
    public static final String THICK_CLASS = "org.jbundle.app.program.issue.db.Issue";

}
