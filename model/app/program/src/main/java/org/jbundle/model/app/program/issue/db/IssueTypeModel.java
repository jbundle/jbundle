/**
 * @(#)IssueTypeModel.
 * Copyright © 2013 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.app.program.issue.db;

import org.jbundle.model.db.*;

public interface IssueTypeModel extends Rec
{

    //public static final String ID = ID;
    //public static final String LAST_CHANGED = LAST_CHANGED;
    //public static final String DELETED = DELETED;
    public static final String DESCRIPTION = "Description";

    public static final String DESCRIPTION_KEY = "Description";

    public static final String ISSUE_TYPE_FILE = "IssueType";
    public static final String THIN_CLASS = "org.jbundle.thin.app.program.issue.db.IssueType";
    public static final String THICK_CLASS = "org.jbundle.app.program.issue.db.IssueType";

}
