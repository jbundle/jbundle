/**
 * @(#)IssueModel.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.app.program.issue.db;

import org.jbundle.model.db.*;

public interface IssueModel extends Rec
{
    public static final String ISSUE_SCREEN_CLASS = "org.jbundle.app.program.issue.screen.IssueScreen";
    public static final String ISSUE_GRID_SCREEN_CLASS = "org.jbundle.app.program.issue.screen.IssueGridScreen";

    public static final String ISSUE_FILE = "Issue";
    public static final String THIN_CLASS = "org.jbundle.thin.app.program.issue.db.Issue";
    public static final String THICK_CLASS = "org.jbundle.app.program.issue.db.Issue";

}
