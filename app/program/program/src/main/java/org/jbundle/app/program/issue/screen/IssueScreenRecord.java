/**
 * @(#)IssueScreenRecord.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.program.issue.screen;

import java.awt.*;
import java.util.*;

import org.jbundle.base.db.*;
import org.jbundle.thin.base.util.*;
import org.jbundle.thin.base.db.*;
import org.jbundle.base.db.event.*;
import org.jbundle.base.db.filter.*;
import org.jbundle.base.field.*;
import org.jbundle.base.field.convert.*;
import org.jbundle.base.field.event.*;
import org.jbundle.base.screen.model.*;
import org.jbundle.base.screen.model.util.*;
import org.jbundle.base.model.*;
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.model.db.*;
import org.jbundle.model.screen.*;
import org.jbundle.app.program.issue.db.*;
import org.jbundle.app.program.project.db.*;
import org.jbundle.main.user.db.*;

/**
 *  IssueScreenRecord - .
 */
public class IssueScreenRecord extends ScreenRecord
{
    private static final long serialVersionUID = 1L;

    public static final String KEY_ORDER = "KeyOrder";
    public static final String PROJECT_ID = "ProjectID";
    public static final String PROJECT_VERSION_ID = "ProjectVersionID";
    public static final String ISSUE_TYPE_ID = "IssueTypeID";
    public static final String ISSUE_STATUS_ID = "IssueStatusID";
    public static final String ASSIGNED_USER_ID = "AssignedUserID";
    public static final String ISSUE_PRIORITY_ID = "IssuePriorityID";
    /**
     * Default constructor.
     */
    public IssueScreenRecord()
    {
        super();
    }
    /**
     * Constructor.
     */
    public IssueScreenRecord(RecordOwner screen)
    {
        this();
        this.init(screen);
    }
    /**
     * Initialize class fields.
     */
    public void init(RecordOwner screen)
    {
        super.init(screen);
    }

    public static final String ISSUE_SCREEN_RECORD_FILE = null;   // Screen field
    /**
     * Add this field in the Record's field sequence.
     */
    public BaseField setupField(int iFieldSeq)
    {
        BaseField field = null;
        if (iFieldSeq == 0)
            field = new ShortField(this, KEY_ORDER, Constants.DEFAULT_FIELD_LENGTH, null, new Short((short)0));
        if (iFieldSeq == 1)
            field = new ProjectFilter(this, PROJECT_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 2)
            field = new ProjectVersionField(this, PROJECT_VERSION_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 3)
            field = new IssueTypeField(this, ISSUE_TYPE_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 4)
            field = new IssueStatusField(this, ISSUE_STATUS_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 5)
            field = new UserField(this, ASSIGNED_USER_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 6)
            field = new IssuePriorityField(this, ISSUE_PRIORITY_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (field == null)
            field = super.setupField(iFieldSeq);
        return field;
    }

}
