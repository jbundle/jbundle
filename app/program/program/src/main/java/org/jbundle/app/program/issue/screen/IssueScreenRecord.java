/**
 * @(#)IssueScreenRecord.
 * Copyright © 2011 jbundle.org. All rights reserved.
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
    public static final int kKeyOrder = kScreenRecordLastField + 1;
    public static final String PROJECT_ID = "ProjectID";
    public static final int kProjectID = kKeyOrder + 1;
    public static final String PROJECT_VERSION_ID = "ProjectVersionID";
    public static final int kProjectVersionID = kProjectID + 1;
    public static final String ISSUE_TYPE_ID = "IssueTypeID";
    public static final int kIssueTypeID = kProjectVersionID + 1;
    public static final String ISSUE_STATUS_ID = "IssueStatusID";
    public static final int kIssueStatusID = kIssueTypeID + 1;
    public static final String ASSIGNED_USER_ID = "AssignedUserID";
    public static final int kAssignedUserID = kIssueStatusID + 1;
    public static final String ISSUE_PRIORITY_ID = "IssuePriorityID";
    public static final int kIssuePriorityID = kAssignedUserID + 1;
    public static final int kIssueScreenRecordLastField = kIssuePriorityID;
    public static final int kIssueScreenRecordFields = kIssuePriorityID - DBConstants.MAIN_FIELD + 1;
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

    public static final String kIssueScreenRecordFile = null; // Screen field
    /**
     * Add this field in the Record's field sequence.
     */
    public BaseField setupField(int iFieldSeq)
    {
        BaseField field = null;
        if (iFieldSeq == kKeyOrder)
            field = new ShortField(this, "KeyOrder", Constants.DEFAULT_FIELD_LENGTH, null, new Short((short)0));
        if (iFieldSeq == kProjectID)
            field = new ProjectFilter(this, "ProjectID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kProjectVersionID)
            field = new ProjectVersionField(this, "ProjectVersionID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kIssueTypeID)
            field = new IssueTypeField(this, "IssueTypeID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kIssueStatusID)
            field = new IssueStatusField(this, "IssueStatusID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kAssignedUserID)
            field = new UserField(this, "AssignedUserID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kIssuePriorityID)
            field = new IssuePriorityField(this, "IssuePriorityID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (field == null)
        {
            field = super.setupField(iFieldSeq);
            if (field == null) if (iFieldSeq < kIssueScreenRecordLastField)
                field = new EmptyField(this);
        }
        return field;
    }

}
