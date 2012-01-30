/**
 * @(#)IssueHistoryModel.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.app.program.issue.db;

import org.jbundle.model.db.*;

public interface IssueHistoryModel extends Rec
{

    //public static final String ID = ID;
    public static final String COMMENT = "Comment";
    public static final String ISSUE_ID = "IssueID";

    public static final String ISSUE_ID_KEY = "IssueID";
    public static final String ISSUE_HISTORY_SCREEN_CLASS = "org.jbundle.app.program.issue.screen.IssueHistoryScreen";
    public static final String ISSUE_HISTORY_GRID_SCREEN_CLASS = "org.jbundle.app.program.issue.screen.IssueHistoryGridScreen";

    public static final String ISSUE_HISTORY_FILE = "IssueHistory";
    public static final String THIN_CLASS = "org.jbundle.thin.app.program.issue.db.IssueHistory";
    public static final String THICK_CLASS = "org.jbundle.app.program.issue.db.IssueHistory";

}
